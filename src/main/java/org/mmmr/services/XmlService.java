package org.mmmr.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.mmmr.Mod;
import org.xml.sax.SAXException;

/**
 * uses JAXB
 * 
 * @author Jurgen
 */
public class XmlService {
    private Marshaller marshaller;

    private Unmarshaller unmarshaller;

    public XmlService(Config cfg) throws JAXBException, SAXException, IOException {
        this.init(cfg);
    }

    void init(Config cfg) throws JAXBException, SAXException, IOException {
        String contextPath = Mod.class.getPackage().getName();
        final File xsdfile = new File(cfg.getData(), contextPath + ".xsd");
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
