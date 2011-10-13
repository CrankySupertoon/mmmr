package org.mmmr.services.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.mmmr.services.interfaces.ArchiveEntry;
import org.mmmr.services.interfaces.ArchiveEntryMatcher;
import org.mmmr.services.interfaces.ArchiveOutputStreamBuilder;
import org.mmmr.services.interfaces.ArchiveServiceI;

/**
 * (un)zip via java (using native java liv for zip compression)
 * 
 * @author Jurgen
 */
public class ArchiveServiceSimple implements ArchiveServiceI {
    private String charset = "UTF-8";

    /**
     * 
     * @see org.mmmr.services.interfaces.ArchiveServiceI#compress(java.io.File, java.util.List, java.io.File)
     */
    @Override
    public void compress(File basedir, List<String> files, File archive) throws IOException {
        ZipOutputStream out = null;
        FileInputStream in = null;
        byte[] buffer = new byte[1024 * 8 * 4];
        try {
            out = new ZipOutputStream(new FileOutputStream(archive));
            out.setLevel(9);

            for (String file : files) {
                ZipEntry ze = new ZipEntry(file);
                in = new FileInputStream(new File(basedir, file));
                out.putNextEntry(ze);
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                in.close();
                in = null;
            }
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception ex) {
                    //
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (Exception ex) {
                    //
                }
            }
        }
    }

    /**
     * 
     * @see org.mmmr.services.interfaces.ArchiveServiceI#extract(java.io.File, org.mmmr.services.interfaces.ArchiveOutputStreamBuilder,
     *      org.mmmr.services.interfaces.ArchiveEntryMatcher)
     */
    @Override
    public void extract(File archive, ArchiveOutputStreamBuilder out, ArchiveEntryMatcher matcher) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("extract");
    }

    /**
     * 
     * @see org.mmmr.services.interfaces.ArchiveServiceI#extract(java.io.File, java.io.File)
     */
    @Override
    public void extract(File archive, File basedir) throws IOException {
        ZipInputStream in = null;
        OutputStream out = null;
        try {
            in = new ZipInputStream(new FileInputStream(archive), Charset.forName(this.charset));
            ZipEntry ze;
            byte[] buffer = new byte[1024 * 8 * 4];
            int read;
            while ((ze = in.getNextEntry()) != null) {
                if (ze.isDirectory()) {
                    continue;
                }

                File file = new File(basedir, ze.getName());
                file.getParentFile().mkdirs();
                out = new FileOutputStream(file);
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                out.close();
                out = null;
            }
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception ex) {
                    //
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (Exception ex) {
                    //
                }
            }
        }
    }

    public String getCharset() {
        return this.charset;
    }

    /**
     * 
     * @see org.mmmr.services.interfaces.ArchiveServiceI#list(File)
     */
    @Override
    public List<ArchiveEntry> list(File archive) throws IOException {
        List<ArchiveEntry> entries = new ArrayList<ArchiveEntry>();
        ZipInputStream in = null;
        try {
            in = new ZipInputStream(new FileInputStream(archive), Charset.forName(this.charset));
            ZipEntry ze;
            while ((ze = in.getNextEntry()) != null) {
                if (ze.isDirectory()) {
                    continue;
                }
                entries.add(new ArchiveEntry(ze.getName(), ze.getSize(), new Date(ze.getTime()), new Date(ze.getTime()), ze.getCompressedSize(),
                        null, null));
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception ex) {
                    //
                }
            }
        }
        return entries;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }
}
