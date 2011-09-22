package org.mmmr.services.interfaces;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Map;

/**
 * @author Jurgen
 */
public interface DownloadingServiceI {
    public abstract byte[] downloadURL(URL url) throws IOException;

    public abstract Map<String, Object> downloadURL(URL url, File target) throws IOException;

    public abstract Map<String, Object> downloadURL(URL url, OutputStream target) throws IOException;
}
