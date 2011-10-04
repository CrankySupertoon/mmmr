package org.mmmr.test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.util.Arrays;
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

/**
 * @see http://sevenzipjbind.sourceforge.net/basic_snippets.html
 */
public class _7zipTst implements net.sf.sevenzipjbinding.IArchiveOpenCallback, net.sf.sevenzipjbinding.ICryptoGetTextPassword,
        net.sf.sevenzipjbinding.IArchiveOpenVolumeCallback, net.sf.sevenzipjbinding.IArchiveExtractCallback {
    private static String algorithm = "DESede";

    private static Key key;

    private static Cipher cipher;

    static {
        try {
            _7zipTst.key = KeyGenerator.getInstance(_7zipTst.algorithm).generateKey();
            _7zipTst.cipher = Cipher.getInstance(_7zipTst.algorithm);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String decrypt(byte[] encryptionBytes) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        _7zipTst.cipher.init(Cipher.DECRYPT_MODE, _7zipTst.key);
        byte[] recoveredBytes = _7zipTst.cipher.doFinal(encryptionBytes);
        String recovered = new String(recoveredBytes);
        return recovered;
    }

    private static byte[] encrypt(String input) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        _7zipTst.cipher.init(Cipher.ENCRYPT_MODE, _7zipTst.key);
        byte[] inputBytes = input.getBytes();
        return _7zipTst.cipher.doFinal(inputBytes);
    }

    public static void main(String[] args) {
        try {
            _7zipTst tst = new _7zipTst();
            String[] formats = { "zip", "rar", "7z" };
            String[] suffix = { "", "2" };
            for (String format : formats) {
                for (String element : suffix) {
                    String f = "file" + element + "." + format;
                    System.out.println(f);
                    tst.extract(new File("C:/tmp/arch/" + f));
                    System.out.println();
                }
            }
        } catch (SevenZipException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private byte[] pwd = null;

    /**
     * 
     * @see net.sf.sevenzipjbinding.ICryptoGetTextPassword#cryptoGetTextPassword()
     */
    @Override
    public String cryptoGetTextPassword() throws SevenZipException {
        if (1 == 1) {
            return "test";
        }
        if (this.pwd != null) {
            try {
                return _7zipTst.decrypt(this.pwd);
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
            this.pwd = _7zipTst.encrypt(pass);
        } catch (NullPointerException ex) {
            //
        } catch (GeneralSecurityException ex) {
            ex.printStackTrace();
        }
        return pass;
    }

    public void extract(File archive) throws IOException, SevenZipException {
        RandomAccessFile randomAccessFile = null;
        ISevenZipInArchive inArchive = null;
        try {
            randomAccessFile = new RandomAccessFile(archive, "r");
            inArchive = SevenZip.openInArchive(null, new RandomAccessFileInStream(randomAccessFile), this);
            ISimpleInArchive simpleInArchive = inArchive.getSimpleInterface();

            for (ISimpleInArchiveItem item : simpleInArchive.getArchiveItems()) {
                final int[] hash = new int[] { 0 };
                if (!item.isFolder()) {
                    ExtractOperationResult result = item.extractSlow(new ISequentialOutStream() {
                        @Override
                        public int write(byte[] data) throws SevenZipException {
                            hash[0] ^= Arrays.hashCode(data); // Consume data
                            return data.length; // Return amount of consumed data
                        }
                    });
                    if (result == ExtractOperationResult.OK) {
                        System.out.println(String.format("%9X | %s", //
                                hash[0], item.getPath()));
                    } else {
                        System.err.println("Error extracting item: " + result);
                    }
                }
            }
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

    public void list(File archive) throws IOException, SevenZipException {
        this.pwd = null;
        RandomAccessFile randomAccessFile = null;
        ISevenZipInArchive inArchive = null;
        try {
            randomAccessFile = new RandomAccessFile(archive, "r");
            inArchive = SevenZip.openInArchive(null, new RandomAccessFileInStream(randomAccessFile), new _7zipTst());
            ISimpleInArchive simpleInArchive = inArchive.getSimpleInterface();

            System.out.println("| Size      | Compr.Sz. | Filename  | CRC       |");
            System.out.println("+-----------+-----------+-----------+-----------+");

            for (ISimpleInArchiveItem item : simpleInArchive.getArchiveItems()) {
                System.out.println(String.format("| %9s | %9s | %9s | %9s |", item.getSize(), item.getPackedSize(), item.getPath(),
                        String.valueOf(item.getCRC())));
            }
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
