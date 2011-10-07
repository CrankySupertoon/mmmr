package org.mmmr.services.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;

import net.sf.sevenzipjbinding.ExtractAskMode;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IInStream;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.ISevenZipInArchive;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;

import org.mmmr.services.ExceptionAndLogHandler;
import org.mmmr.services.interfaces.ArchiveEntry;
import org.mmmr.services.interfaces.ArchiveEntryMatcher;

/**
 * extracts lots of compressions formats including but not limited to zip, rar, 7z (7zip)<br/>
 * download and sourcecode available from ... (see links)
 * 
 * @author Jurgen
 * 
 * @see http://sevenzipjbind.sourceforge.net/
 * @see http://sourceforge.net/apps/mediawiki/sevenzipjbind/index.php?title=Main_Page
 * @see http://sevenzipjbind.sourceforge.net/basic_snippets.html
 */
public class ArchiveService7Zip extends ArchiveServiceSimple implements net.sf.sevenzipjbinding.IArchiveOpenCallback,
        net.sf.sevenzipjbinding.ICryptoGetTextPassword, net.sf.sevenzipjbinding.IArchiveOpenVolumeCallback,
        net.sf.sevenzipjbinding.IArchiveExtractCallback {
    // /**
    // * @see http://sevenzipjbind.sourceforge.net/basic_snippets.html#extraction-single-file
    // */
    // private static class Callback implements IArchiveExtractCallback {
    // private CRC32 crc32;
    //
    // private String current;
    //
    // private ISevenZipInArchive inArchive;
    //
    // private int index;
    //
    // private OutputStream out;
    //
    // private File outdir;
    //
    // private boolean skipExtraction;
    //
    // @SuppressWarnings("unused")
    // private long total;
    //
    // public Callback(File outdir, ISevenZipInArchive inArchive) {
    // this.outdir = outdir;
    // this.inArchive = inArchive;
    // }
    //
    // /**
    // *
    // * @see net.sf.sevenzipjbinding.IArchiveExtractCallback#getStream(int, net.sf.sevenzipjbinding.ExtractAskMode)
    // */
    // @Override
    // public ISequentialOutStream getStream(int i, ExtractAskMode extractAskMode) throws SevenZipException {
    // this.index = i;
    // this.skipExtraction = (Boolean) this.inArchive.getProperty(this.index, PropID.IS_FOLDER);
    // if (this.skipExtraction || (extractAskMode != ExtractAskMode.EXTRACT)) {
    // return null;
    // }
    // return new ISequentialOutStream() {
    // @Override
    // public int write(byte[] data) throws SevenZipException {
    // try {
    // Object path = Callback.this.inArchive.getProperty(Callback.this.index, PropID.PATH);
    // if (!path.equals(Callback.this.current)) {
    // if (Callback.this.out != null) {
    // Callback.this.out.flush();
    // Callback.this.out.close();
    // }
    // Callback.this.total = 0;
    // Callback.this.crc32 = new CRC32();
    // Callback.this.current = String.valueOf(path);
    // File target = new File(Callback.this.outdir, String.valueOf(path));
    // target.getParentFile().mkdirs();
    // Callback.this.out = new BufferedOutputStream(new FileOutputStream(target));
    // }
    // try {
    // Callback.this.out.write(data, 0, data.length);
    // Callback.this.crc32.update(data, 0, data.length);
    // } catch (IOException ex) {
    // throw new RuntimeException(ex);
    // }
    // Callback.this.total += data.length;
    // return data.length;
    // } catch (IOException ex) {
    // throw new RuntimeException(ex);
    // }
    // }
    // };
    // }
    //
    // /**
    // *
    // * @see net.sf.sevenzipjbinding.IArchiveExtractCallback#prepareOperation(net.sf.sevenzipjbinding.ExtractAskMode)
    // */
    // @Override
    // public void prepareOperation(ExtractAskMode extractAskMode) throws SevenZipException {
    // //
    // }
    //
    // /**
    // *
    // * @see net.sf.sevenzipjbinding.IProgress#setCompleted(long)
    // */
    // @Override
    // public void setCompleted(long completeValue) throws SevenZipException {
    // //
    // }
    //
    // /**
    // *
    // * @see net.sf.sevenzipjbinding.IArchiveExtractCallback#setOperationResult(net.sf.sevenzipjbinding.ExtractOperationResult)
    // */
    // @Override
    // public void setOperationResult(ExtractOperationResult extractOperationResult) throws SevenZipException {
    // if (this.skipExtraction) {
    // return;
    // }
    // try {
    // this.out.flush();
    // this.out.close();
    // } catch (Exception ex) {
    // ex.printStackTrace();
    // }
    // if (extractOperationResult != ExtractOperationResult.OK) {
    //                ExceptionAndLogHandler.log(new RuntimeException("Extraction error")); //$NON-NLS-1$
    // }
    // }
    //
    // /**
    // *
    // * @see net.sf.sevenzipjbinding.IProgress#setTotal(long)
    // */
    // @Override
    // public void setTotal(long total) throws SevenZipException {
    // //
    // }
    // }

    private static String algorithm = "DESede";

    private static Key key;

    private static Cipher cipher;

    static {
        try {
            ArchiveService7Zip.key = KeyGenerator.getInstance(ArchiveService7Zip.algorithm).generateKey();
            ArchiveService7Zip.cipher = Cipher.getInstance(ArchiveService7Zip.algorithm);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String decrypt(byte[] encryptionBytes) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        ArchiveService7Zip.cipher.init(Cipher.DECRYPT_MODE, ArchiveService7Zip.key);
        byte[] recoveredBytes = ArchiveService7Zip.cipher.doFinal(encryptionBytes);
        String recovered = new String(recoveredBytes);
        return recovered;
    }

    private static byte[] encrypt(String input) throws GeneralSecurityException {
        ArchiveService7Zip.cipher.init(Cipher.ENCRYPT_MODE, ArchiveService7Zip.key);
        byte[] inputBytes = input.getBytes();
        return ArchiveService7Zip.cipher.doFinal(inputBytes);
    }

    private byte[] pwd = null;

    /**
     * 
     * @see net.sf.sevenzipjbinding.ICryptoGetTextPassword#cryptoGetTextPassword()
     */
    @Override
    public String cryptoGetTextPassword() throws SevenZipException {
        if (this.pwd != null) {
            try {
                return ArchiveService7Zip.decrypt(this.pwd);
            } catch (NullPointerException ex) {
                //
            } catch (GeneralSecurityException ex) {
                ex.printStackTrace();
            }
        }
        System.out.print("password: ");
        Scanner scanner = new Scanner(System.in);
        String pass = scanner.next();
        try {
            this.pwd = ArchiveService7Zip.encrypt(pass);
        } catch (NullPointerException ex) {
            //
        } catch (GeneralSecurityException ex) {
            ex.printStackTrace();
        }
        return pass;
    }

    /**
     * 
     * @see org.mmmr.services.impl.ArchiveServiceSimple#extract(java.io.File, java.io.File)
     */
    @Override
    public void extract(File archive, File out) throws IOException {
        this.extract(archive, out, null);
        //
        // IOException exception = null;
        // RuntimeException runtimeException = null;
        // RandomAccessFile randomAccessFile = null;
        // ISevenZipInArchive inArchive = null;
        // try {
        //            randomAccessFile = new RandomAccessFile(archive, "r"); //$NON-NLS-1$
        // inArchive = SevenZip.openInArchive(null, new RandomAccessFileInStream(randomAccessFile));
        // int[] in = new int[inArchive.getNumberOfItems()];
        // for (int i = 0; i < in.length; i++) {
        // in[i] = i;
        // }
        // inArchive.extract(in, false, new Callback(out, inArchive));
        // } catch (Exception e) {
        //            System.err.println("Error occurs: " + e); //$NON-NLS-1$
        // runtimeException = new RuntimeException(e);
        // } finally {
        // if (inArchive != null) {
        // try {
        // inArchive.close();
        // } catch (SevenZipException e) {
        //                    System.err.println("Error closing archive: " + e); //$NON-NLS-1$
        // exception = new IOException(e);
        // }
        // }
        // if (randomAccessFile != null) {
        // try {
        // randomAccessFile.close();
        // } catch (IOException e) {
        //                    System.err.println("Error closing file: " + e); //$NON-NLS-1$
        // exception = e;
        // }
        // }
        // }
        // if (exception != null) {
        // throw exception;
        // }
        // if (runtimeException != null) {
        // throw runtimeException;
        // }
    }

    /**
     * 
     * @see org.mmmr.services.impl.ArchiveServiceSimple#extract(java.io.File, java.io.File, org.mmmr.services.interfaces.ArchiveEntryMatcher)
     */
    @Override
    public void extract(File archive, File out, ArchiveEntryMatcher matcher) throws IOException {
        RandomAccessFile randomAccessFile = null;
        ISevenZipInArchive inArchive = null;
        try {
            randomAccessFile = new RandomAccessFile(archive, "r");
            inArchive = SevenZip.openInArchive(null, new RandomAccessFileInStream(randomAccessFile), this);
            ISimpleInArchive simpleInArchive = inArchive.getSimpleInterface();

            for (ISimpleInArchiveItem item : simpleInArchive.getArchiveItems()) {
                if (item.isFolder()) {
                    continue;
                }

                if (!matcher.matches(new ArchiveEntry(item.getPath()))) {
                    ExceptionAndLogHandler.log("skipping " + item.getPath());
                    continue;
                }

                ExceptionAndLogHandler.log("extracting " + item.getPath());

                File target = new File(out, item.getPath());
                target.getParentFile().mkdirs();
                FileOutputStream fileout = null;

                try {
                    fileout = new FileOutputStream(target);
                    final FileOutputStream fout = fileout;
                    ExtractOperationResult result = item.extractSlow(new ISequentialOutStream() {
                        @Override
                        public int write(byte[] data) throws SevenZipException {
                            try {
                                fout.write(data);
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                            return data.length;
                        }
                    });
                    if (result != ExtractOperationResult.OK) {
                        throw new IOException("extract failed with " + result + " on " + item.getPath());
                    }
                } finally {
                    if (fileout != null) {
                        try {
                            fileout.close();
                        } catch (Exception ex) {
                            ExceptionAndLogHandler.log(ex);
                        }
                    }
                }
            }
        } catch (SevenZipException ex) {
            throw new IOException(ex);
        } finally {
            this.pwd = null;
            if (inArchive != null) {
                try {
                    inArchive.close();
                } catch (SevenZipException ex) {
                    ExceptionAndLogHandler.log(ex);
                }
            }
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException ex) {
                    ExceptionAndLogHandler.log(ex);
                }
            }
        }
    }

    /**
     * 
     * @see net.sf.sevenzipjbinding.IArchiveOpenVolumeCallback#getProperty(net.sf.sevenzipjbinding.PropID)
     */
    @Override
    public Object getProperty(PropID paramPropID) throws SevenZipException {
        return null;
    }

    /**
     * 
     * @see net.sf.sevenzipjbinding.IArchiveExtractCallback#getStream(int, net.sf.sevenzipjbinding.ExtractAskMode)
     */
    @Override
    public ISequentialOutStream getStream(int paramInt, ExtractAskMode paramExtractAskMode) throws SevenZipException {
        return null;
    }

    /**
     * 
     * @see net.sf.sevenzipjbinding.IArchiveOpenVolumeCallback#getStream(java.lang.String)
     */
    @Override
    public IInStream getStream(String paramString) throws SevenZipException {
        return null;
    }

    /**
     * 
     * @see org.mmmr.services.impl.ArchiveServiceSimple#list(File)
     */
    @Override
    public List<ArchiveEntry> list(File archive) throws IOException {
        List<ArchiveEntry> entries = new ArrayList<ArchiveEntry>();
        RandomAccessFile randomAccessFile = null;
        ISevenZipInArchive inArchive = null;
        try {
            randomAccessFile = new RandomAccessFile(archive, "r");
            inArchive = SevenZip.openInArchive(null, new RandomAccessFileInStream(randomAccessFile), this);
            ISimpleInArchive simpleInArchive = inArchive.getSimpleInterface();
            for (ISimpleInArchiveItem item : simpleInArchive.getArchiveItems()) {
                entries.add(new ArchiveEntry(item.getPath()));
                // System.out.println(String.format("| %9s | %9s | %9s | %9s |", item.getSize(), item.getPackedSize(), item.getPath(),
                // String.valueOf(item.getCRC())));
            }
        } catch (SevenZipException ex) {
            throw new IOException(ex);
        } finally {
            if (inArchive != null) {
                try {
                    inArchive.close();
                } catch (SevenZipException e) {
                    System.err.println("Error closing archive: " + e);
                }
            }
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    System.err.println("Error closing file: " + e);
                }
            }
        }

        return entries;
    }

    /**
     * 
     * @see net.sf.sevenzipjbinding.IArchiveExtractCallback#prepareOperation(net.sf.sevenzipjbinding.ExtractAskMode)
     */
    @Override
    public void prepareOperation(ExtractAskMode paramExtractAskMode) throws SevenZipException {
        //
    }

    /**
     * 
     * @see net.sf.sevenzipjbinding.IProgress#setCompleted(long)
     */
    @Override
    public void setCompleted(long paramLong) throws SevenZipException {
        //
    }

    /**
     * 
     * @see net.sf.sevenzipjbinding.IArchiveOpenCallback#setCompleted(java.lang.Long, java.lang.Long)
     */
    @Override
    public void setCompleted(Long paramLong1, Long paramLong2) throws SevenZipException {
        //
    }

    /**
     * 
     * @see net.sf.sevenzipjbinding.IArchiveExtractCallback#setOperationResult(net.sf.sevenzipjbinding.ExtractOperationResult)
     */
    @Override
    public void setOperationResult(ExtractOperationResult paramExtractOperationResult) throws SevenZipException {
        //
    }

    public void setPassword(String pwd) throws GeneralSecurityException {
        this.pwd = ArchiveService7Zip.encrypt(pwd);
    }

    /**
     * 
     * @see net.sf.sevenzipjbinding.IProgress#setTotal(long)
     */
    @Override
    public void setTotal(long paramLong) throws SevenZipException {
        //
    }

    /**
     * 
     * @see net.sf.sevenzipjbinding.IArchiveOpenCallback#setTotal(java.lang.Long, java.lang.Long)
     */
    @Override
    public void setTotal(Long paramLong1, Long paramLong2) throws SevenZipException {
        //
    }

}
