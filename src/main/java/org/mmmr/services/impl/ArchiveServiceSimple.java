package org.mmmr.services.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.mmmr.services.interfaces.ArchiveServiceI;

/**
 * unzip via java (using native java liv for zip compression)
 * 
 * @author Jurgen
 */
public class ArchiveServiceSimple implements ArchiveServiceI {
    @Override
    public void extract(File archive, File out) throws IOException {
        ZipInputStream zis = new ZipInputStream(new FileInputStream(archive));
        ZipEntry ze;
        byte[] buffer = new byte[1024 * 8];
        int read;
        while ((ze = zis.getNextEntry()) != null) {
            if (ze.isDirectory()) {
                continue;
            }
            OutputStream fout = null;
            File file = new File(out, ze.getName());
            file.getParentFile().mkdirs();
            fout = new FileOutputStream(file);
            while ((read = zis.read(buffer)) != -1) {
                fout.write(buffer, 0, read);
            }
            fout.close();
        }
    }
}
