package org.mmmr.services;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.zip.CRC32;

import net.sf.sevenzipjbinding.ExtractAskMode;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IArchiveExtractCallback;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.ISevenZipInArchive;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;

/**
 * add data/libs/sevenzipjbinding.jar and data/libs/sevenzipjbinding-AllWindows.jar to the classpath after running {@link SevenZipDownloader}
 * 
 * @author Jurgen
 */
public class ArchiveService {
    /**
     * @see http://sevenzipjbind.sourceforge.net/basic_snippets.html#extraction-single-file
     */
    private static class Callback implements IArchiveExtractCallback {
        private CRC32 crc32;

        private String current;

        private ISevenZipInArchive inArchive;

        private int index;

        private OutputStream out;

        private File outdir;

        private boolean skipExtraction;

        private long total;

        public Callback(File outdir, ISevenZipInArchive inArchive) {
            this.outdir = outdir;
            this.inArchive = inArchive;
        }

        @Override
        public ISequentialOutStream getStream(int i, ExtractAskMode extractAskMode) throws SevenZipException {
            this.index = i;
            this.skipExtraction = (Boolean) this.inArchive.getProperty(this.index, PropID.IS_FOLDER);
            if (this.skipExtraction || (extractAskMode != ExtractAskMode.EXTRACT)) {
                return null;
            }
            return new ISequentialOutStream() {
                @Override
                public int write(byte[] data) throws SevenZipException {
                    try {
                        Object path = Callback.this.inArchive.getProperty(Callback.this.index, PropID.PATH);
                        if (!path.equals(Callback.this.current)) {
                            if (Callback.this.out != null) {
                                Callback.this.out.flush();
                                Callback.this.out.close();
                            }
                            Callback.this.total = 0;
                            Callback.this.crc32 = new CRC32();
                            Callback.this.current = String.valueOf(path);
                            File target = new File(Callback.this.outdir, String.valueOf(path));
                            target.getParentFile().mkdirs();
                            Callback.this.out = new BufferedOutputStream(new FileOutputStream(target));
                        }
                        try {
                            Callback.this.out.write(data, 0, data.length);
                            Callback.this.crc32.update(data, 0, data.length);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        Callback.this.total += data.length;
                        return data.length;
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            };
        }

        @Override
        public void prepareOperation(ExtractAskMode extractAskMode) throws SevenZipException {
            //
        }

        @Override
        public void setCompleted(long completeValue) throws SevenZipException {
            //
        }

        @Override
        public void setOperationResult(ExtractOperationResult extractOperationResult) throws SevenZipException {
            if (this.skipExtraction) {
                return;
            }
            try {
                this.out.flush();
                this.out.close();
            } catch (Exception ex) {
                //
            }
            if (extractOperationResult != ExtractOperationResult.OK) {
                ExceptionAndLogHandler.log(new RuntimeException("Extraction error"));
            } else {
                // (Long.toHexString(this.crc32.getValue()).toUpperCase() + " | " + this.total + " | " + this.inArchive.getProperty(this.index,
                // PropID.PATH));
            }
        }

        @Override
        public void setTotal(long total) throws SevenZipException {
            //
        }
    }

    public static void extract(File archive, File out) throws IOException {
        IOException exception = null;
        RuntimeException runtimeException = null;
        RandomAccessFile randomAccessFile = null;
        ISevenZipInArchive inArchive = null;
        try {
            randomAccessFile = new RandomAccessFile(archive, "r");
            inArchive = SevenZip.openInArchive(null, new RandomAccessFileInStream(randomAccessFile));
            int[] in = new int[inArchive.getNumberOfItems()];
            for (int i = 0; i < in.length; i++) {
                in[i] = i;
            }
            inArchive.extract(in, false, new Callback(out, inArchive));
        } catch (Exception e) {
            System.err.println("Error occurs: " + e);
            runtimeException = new RuntimeException(e);
        } finally {
            if (inArchive != null) {
                try {
                    inArchive.close();
                } catch (SevenZipException e) {
                    System.err.println("Error closing archive: " + e);
                    exception = new IOException(e);
                }
            }
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    System.err.println("Error closing file: " + e);
                    exception = e;
                }
            }
        }
        if (exception != null) {
            throw exception;
        }
        if (runtimeException != null) {
            throw runtimeException;
        }
    }
}
