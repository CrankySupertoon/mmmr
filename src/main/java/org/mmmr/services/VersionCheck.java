package org.mmmr.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeFilter;

@SuppressWarnings("restriction")
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

    private static ByteArrayOutputStream get(String urlList, HttpClient httpclient) throws IOException, ClientProtocolException {
        HttpGet httpget = new HttpGet(urlList);
        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();
        InputStream in = entity.getContent();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int read;
        byte[] buffer = new byte[1024 * 8 * 4];
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        in.close();
        out.close();
        return out;
    }

    public static void main(String[] args) {
        try {
            String urlList = "http://code.google.com/feeds/p/mmmr/downloads/basic";
            HttpClient httpclient = new DefaultHttpClient();
            ByteArrayOutputStream out = VersionCheck.get(urlList, httpclient);

            com.sun.org.apache.xerces.internal.parsers.DOMParser parser = new com.sun.org.apache.xerces.internal.parsers.DOMParser();
            parser.parse(new org.xml.sax.InputSource(new ByteArrayInputStream(out.toByteArray())));
            com.sun.org.apache.xerces.internal.dom.DocumentImpl document = (com.sun.org.apache.xerces.internal.dom.DocumentImpl) parser.getDocument();
            org.w3c.dom.traversal.NodeIterator iterator = document.createNodeIterator(document.getDocumentElement(), NodeFilter.SHOW_ELEMENT,
                    new AllElements(), true);
            Pattern pattern = Pattern.compile("<a href=\"(http://mmmr.googlecode.com/files/[^\"]*)\">");
            Node n;
            List<String> downloads = new ArrayList<String>();
            while ((n = iterator.nextNode()) != null) {
                String value = n.getChildNodes().item(0).getTextContent();
                Matcher matcher = pattern.matcher(value);
                matcher.find();
                downloads.add(matcher.group(1));
            }
            String latest = downloads.get(0);
            int slashIndex = latest.lastIndexOf('/');
            ByteArrayOutputStream jar = VersionCheck.get(latest, httpclient);
            String name = latest.substring(slashIndex + 1);
            FileOutputStream test = new FileOutputStream(name);
            test.write(jar.toByteArray());
            test.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
