package org.mmmr.services.interfaces;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Jurgen
 */
public interface ArchiveOutputStreamBuilder {
    public OutputStream createOutputStream(ArchiveEntry entry) throws IOException;
}
