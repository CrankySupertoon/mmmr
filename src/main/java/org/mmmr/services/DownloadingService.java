package org.mmmr.services;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Map;

import org.mmmr.services.impl.DownloadingServiceSimple;
import org.mmmr.services.interfaces.DownloadingServiceI;

/**
 * @author Jurgen
 */
public class DownloadingService {
    private static DownloadingServiceI downloadingService = new DownloadingServiceSimple();

    public static final ByteArrayOutputStream readOnly = new ByteArrayOutputStream() {
        @Override
        public void write(byte[] b) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public synchronized void write(byte[] b, int off, int len) {
            throw new UnsupportedOperationException();
        }

        @Override
        public synchronized void write(int b) {
            throw new UnsupportedOperationException();
        }

        @Override
        public synchronized void writeTo(OutputStream out) throws IOException {
            throw new UnsupportedOperationException();
        }
    };

    public static byte[] downloadURL(URL url) throws IOException {
        return DownloadingService.getDownloadingService().downloadURL(url);
    }

    public static Map<String, Object> downloadURL(URL url, File target) throws IOException {
        return DownloadingService.getDownloadingService().downloadURL(url, target);
    }

    public static Map<String, Object> downloadURL(URL url, OutputStream target) throws IOException {
        return DownloadingService.getDownloadingService().downloadURL(url, target);
    }

    private static DownloadingServiceI getDownloadingService() {
        if (DownloadingService.downloadingService instanceof DownloadingServiceSimple) {
            try {
                DownloadingService.downloadingService = (DownloadingServiceI) Class.forName("org.mmmr.services.impl.DownloadingServiceHttpClient") //$NON-NLS-1$
                        .newInstance();
            } catch (Throwable ex) {
                System.out.println(ex);
            }
        }
        return DownloadingService.downloadingService;
    }
}
