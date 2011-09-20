package org.mmmr.services.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.mmmr.services.Config;
import org.mmmr.services.interfaces.DownloadingServiceI;

/**
 * @author Jurgen
 */
public class DownloadingServiceHttpClient implements DownloadingServiceI {
    /**
     * 
     * @see org.mmmr.services.interfaces.DownloadingServiceI#downloadURL(java.net.URL)
     */
    @Override
    public byte[] downloadURL(URL url) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget;
        try {
            httpget = new HttpGet(url.toURI());
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException(ex);
        }
        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024 * 8 * 4];
            long total = entity.getContentLength();
            long dl = 0;
            InputStream uin = entity.getContent();
            int read;
            while ((read = uin.read(buffer)) > 0) {
                out.write(buffer, 0, read);
                dl += read;
                System.out.println("httpclient: " + Config.NUMBER_FORMAT.format(dl) + "/" + Config.NUMBER_FORMAT.format(total));
            }
            out.close();
            uin.close();

            return out.toByteArray();
        }
        throw new IOException("" + url);
    }

    /**
     * 
     * @see org.mmmr.services.interfaces.DownloadingServiceI#downloadURL(java.net.URL, java.io.File)
     */
    @Override
    public void downloadURL(URL url, File target) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget;
        try {
            httpget = new HttpGet(url.toURI());
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException(ex);
        }
        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            OutputStream fout = new FileOutputStream(target);
            byte[] buffer = new byte[1024 * 8 * 4];
            long total = entity.getContentLength();
            long dl = 0;
            InputStream uin = entity.getContent();
            int read;
            while ((read = uin.read(buffer)) > 0) {
                fout.write(buffer, 0, read);
                dl += read;
                System.out.println("httpclient: " + Config.NUMBER_FORMAT.format(dl) + "/" + Config.NUMBER_FORMAT.format(total));
            }
            fout.close();
            uin.close();
        }
    }
}
