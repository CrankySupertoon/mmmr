package org.mmmr.services.interfaces;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.mmmr.services.ArchiveEntry;

/**
 * @author Jurgen
 */
public interface ArchiveServiceI {
    public abstract void compress(File basedir, List<String> files, File archive) throws IOException;

    public abstract void extract(File archive, File out) throws IOException;

    public abstract void extract(File archive, File out, List<String> paths) throws IOException;

    public abstract List<ArchiveEntry> list(File archive) throws IOException;
}
