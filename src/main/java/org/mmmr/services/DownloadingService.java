package org.mmmr.services;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.mmmr.services.impl.DownloadingServiceSimple;
import org.mmmr.services.interfaces.DownloadingServiceI;

/**
 * @author Jurgen
 */
public class DownloadingService {
    private static DownloadingServiceI downloadingService = new DownloadingServiceSimple();

    public static byte[] downloadURL(URL url) throws IOException {
        return DownloadingService.getDownloadingService().downloadURL(url);
    }

    public static void downloadURL(URL url, File target) throws IOException {
        DownloadingService.getDownloadingService().downloadURL(url, target);
    }

    private static DownloadingServiceI getDownloadingService() {
        if (DownloadingService.downloadingService instanceof DownloadingServiceSimple) {
            try {
                DownloadingService.downloadingService = (DownloadingServiceI) Class.forName("org.mmmr.services.impl.DownloadingServiceHttpClient")
                        .newInstance();
            } catch (Throwable ex) {
                System.out.println(ex);
            }
        }
        return DownloadingService.downloadingService;
    }
}
