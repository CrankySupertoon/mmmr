package org.mmmr.services.interfaces;

import java.io.File;
import java.io.IOException;

public interface ArchiveServiceI {

    public abstract void extract(File archive, File out) throws IOException;

}
