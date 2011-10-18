package org.mmmr.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.mmmr.services.interfaces.ArchiveEntry;
import org.mmmr.services.interfaces.ArchiveOutputStreamBuilder;

/**
 * @author Jurgen
 */
public class MockArchiveOutputStreamBuilder implements ArchiveOutputStreamBuilder {
    @Override
    public OutputStream createOutputStream(ArchiveEntry entry) throws IOException {
        return new ByteArrayOutputStream();
    }
}
