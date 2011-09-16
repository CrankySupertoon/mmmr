package org.mmmr.services.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.mmmr.services.interfaces.DownloadingServiceI;

public class DownloadingServiceHttpClient implements DownloadingServiceI {

    public DownloadingServiceHttpClient() {
        throw new RuntimeException(new IllegalAccessException());
    }

    @Override
    public void downloadURL(URL url, File target) throws IOException {
        throw new RuntimeException(new IllegalAccessException());
    }
}
