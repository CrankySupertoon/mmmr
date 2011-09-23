package org.mmmr.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.mmmr.Mod;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * uses JAXB
 * 
 * @author Jurgen
 */
public class XmlService {
    private static final XPathFactory xfactory = XPathFactory.newInstance();

    private static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    static {
        // was true but xpath didn't work on pom.xml
        XmlService.factory.setNamespaceAware(false);
    }

    public static List<Node> xpath(InputStream xml, String xpathString) throws Exception {
        Document doc = XmlService.factory.newDocumentBuilder().parse(xml);
        doc.normalizeDocument();
        NodeList nodes = NodeList.class.cast(XmlService.xfactory.newXPath().compile(xpathString).evaluate(doc, XPathConstants.NODESET));
        List<Node> nodeList = new ArrayList<Node>();
        for (int i = 0; i < nodes.getLength(); i++) {
            nodeList.add(nodes.item(i));
        }
        return nodeList;
    }

    private Marshaller marshaller;

    private Unmarshaller unmarshaller;

    public XmlService(Config cfg) throws JAXBException, SAXException, IOException {
        this.init(cfg);
    }

    private void init(Config cfg) throws JAXBException, SAXException, IOException {
        String contextPath = Mod.class.getPackage().getName();
        final File xsdfile = new File(cfg.getData(), contextPath + ".xsd"); //$NON-NLS-1$
        JAXBContext context = JAXBContext.newInstance(contextPath);
        context.generateSchema(new SchemaOutputResolver() {
            @Override
            public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
                StreamResult result = new StreamResult(xsdfile);
                result.setSystemId(xsdfile.toURI().toURL().toString());
                return result;
            }
        });
        SchemaFactory sf = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = sf.newSchema(xsdfile);
        this.marshaller = context.createMarshaller();
        this.marshaller.setSchema(schema);
        this.marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        this.unmarshaller = context.createUnmarshaller();
        this.unmarshaller.setSchema(schema);
    }

    public <T> T load(InputStream in, Class<T> type) throws JAXBException {
        return type.cast(this.unmarshaller.unmarshal(in));
    }

    public <T> T save(OutputStream out, T object) throws JAXBException {
        this.marshaller.marshal(object, out);
        this.marshaller.marshal(object, System.out);
        return object;
    }
}
