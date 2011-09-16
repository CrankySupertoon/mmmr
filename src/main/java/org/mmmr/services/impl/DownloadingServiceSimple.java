package org.mmmr.services.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.mmmr.services.Config;
import org.mmmr.services.ExceptionAndLogHandler;
import org.mmmr.services.interfaces.DownloadingServiceI;

public class DownloadingServiceSimple implements DownloadingServiceI {
    @Override
    public void downloadURL(URL url, File target) throws IOException {
        ExceptionAndLogHandler.log(url);
        URLConnection conn = url.openConnection();
        conn.setAllowUserInteraction(false);
        conn.setConnectTimeout(30 * 1000);
        conn.setDefaultUseCaches(true);
        conn.setReadTimeout(30 * 1000);
        conn.setUseCaches(true);
        int total = conn.getContentLength();
        int dl = 0;
        InputStream uin = conn.getInputStream();
        OutputStream fout = new FileOutputStream(target);
        byte[] buffer = new byte[1024 * 8];
        int read;
        while ((read = uin.read(buffer)) > 0) {
            fout.write(buffer, 0, read);
            dl += read;
            System.out.println(Config.NUMBER_FORMAT.format(dl) + "/" + Config.NUMBER_FORMAT.format(total));
        }
        fout.close();
        uin.close();
    }
}
