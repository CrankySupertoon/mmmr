package org.mmmr.test;

import java.io.File;
import java.net.URL;

import org.mmmr.services.ArchiveService;
import org.mmmr.services.DownloadingService;

/**
 * download 7zip buinding liberaries, standalone,for use in development
 * 
 * @author Jurgen
 */
public class SevenZipDownloader {
    public static void main(String[] args) {
        try {
            new File("data/tmp").mkdirs();
            new File("data/libs").mkdirs();
            File zip = new File("./data/tmp/sevenzipjbinding-4.65-1.04-rc-extr-only-AllWindows.zip");
            URL url = new URL(
                    "http://www.mirrorservice.org/sites/download.sourceforge.net/pub/sourceforge/s/project/se/sevenzipjbind/7-Zip-JBinding/4.65-1.04rc-extr-only/sevenzipjbinding-4.65-1.04-rc-extr-only-AllWindows.zip");
            DownloadingService.downloadURL(url, zip);
            ArchiveService.extract(zip, new File("data/tmp/"));

            File jarFrom = new File("data/tmp/sevenzipjbinding-4.65-1.04-rc-extr-only-AllWindows/lib/sevenzipjbinding.jar");
            File jarTo = new File("data/libs/sevenzipjbinding.jar");
            jarFrom.renameTo(jarTo);

            jarFrom = new File("data/tmp/sevenzipjbinding-4.65-1.04-rc-extr-only-AllWindows/lib/sevenzipjbinding-AllWindows.jar");
            jarTo = new File("data/libs/sevenzipjbinding-AllWindows.jar");
            jarFrom.renameTo(jarTo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}