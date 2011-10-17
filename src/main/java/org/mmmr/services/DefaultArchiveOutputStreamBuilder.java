package org.mmmr.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.mmmr.services.interfaces.ArchiveEntry;
import org.mmmr.services.interfaces.ArchiveOutputStreamBuilder;

/**
 * @author Jurgen
 */
public class DefaultArchiveOutputStreamBuilder implements ArchiveOutputStreamBuilder {
    private File target;

    /**
     * 
     * create a ArchiveOutputStreamBuilderImpl
     * 
     * @param target
     */
    public DefaultArchiveOutputStreamBuilder(File target) {
        this.target = target;
    }

    /**
     * 
     * @see org.mmmr.services.interfaces.ArchiveOutputStreamBuilder#createOutputStream(java.lang.String)
     */
    @Override
    public OutputStream createOutputStream(ArchiveEntry entry) throws IOException {
        File file = new File(this.target, entry.path.getPath());
        file.getParentFile().mkdirs();
        return new FileOutputStream(file);
    }

}
