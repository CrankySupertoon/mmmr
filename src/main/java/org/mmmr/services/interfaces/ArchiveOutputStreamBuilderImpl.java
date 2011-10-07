package org.mmmr.services.interfaces;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Jurgen
 */
public class ArchiveOutputStreamBuilderImpl implements ArchiveOutputStreamBuilder {
    private File target;

    /**
     * 
     * create a ArchiveOutputStreamBuilderImpl
     * 
     * @param target
     */
    public ArchiveOutputStreamBuilderImpl(File target) {
        this.target = target;
    }

    /**
     * 
     * @see org.mmmr.services.interfaces.ArchiveOutputStreamBuilder#createOutputStream(java.lang.String)
     */
    @Override
    public OutputStream createOutputStream(ArchiveEntry entry) throws IOException {
        File file = new File(this.target, entry.path);
        file.getParentFile().mkdirs();
        return new FileOutputStream(file);
    }

}
