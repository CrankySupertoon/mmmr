package org.mmmr.services;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeFilter;

public class VersionCheck {
    public static class AllElements implements NodeFilter {
        @Override
        public short acceptNode(Node n) {
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                if ("content".equals(n.getNodeName())) {
                    return NodeFilter.FILTER_ACCEPT;
                }
            }
            return NodeFilter.FILTER_SKIP;
        }
    }

    public static String version;

    static {
        Properties mavenprop = new Properties();

        File pom = new File("pom.xml");

        try {
            if (pom.exists()) {
                // development
                String pomxml = new String(IOMethods.read(new FileInputStream(pom)));
                VersionCheck.version = pomxml.substring(pomxml.indexOf("<version>") + "<version>".length(), pomxml.indexOf("</version>"));
            } else {
                // normal execution
                mavenprop.load(VersionCheck.class.getClassLoader().getResourceAsStream("META-INF/maven/org.mmmr/mmmr/pom.properties"));
                VersionCheck.version = mavenprop.getProperty("version");
            }
        } catch (Exception ex) {
            ExceptionAndLogHandler.log(ex);
        }
    }

    public static void check(Config cfg) {
        try {
            String mavemProjectBase = "http://mmmr.googlecode.com/svn/maven2/org/mmmr/mmmr/maven-metadata.xml";
            byte[] data = DownloadingService.downloadURL(new URL(mavemProjectBase));

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(data));
            XPathFactory xfactory = XPathFactory.newInstance();
            XPath xpath = xfactory.newXPath();
            XPathExpression expr = xpath.compile("/metadata/versioning/versions/version");
            Object result = expr.evaluate(doc, XPathConstants.NODESET);
            NodeList nodes = (NodeList) result;
            List<String> versions = new ArrayList<String>();
            for (int i = 0; i < nodes.getLength(); i++) {
                versions.add(nodes.item(i).getTextContent());
            }
            Collections.sort(versions);
            String latestversion = versions.get(versions.size() - 1);

            if (VersionCheck.version.compareTo(latestversion) < 0) {
                if (IOMethods.showConfirmation(cfg, cfg.getShortTitle(), "A newer version is available, download now?")) {
                    String fname = "mmmr-" + latestversion + ".jar";
                    String dl = "http://mmmr.googlecode.com/svn/maven2/org/mmmr/mmmr/" + latestversion + "/" + fname;
                    File newjar = new File(fname);
                    DownloadingService.downloadURL(new URL(dl), newjar);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
