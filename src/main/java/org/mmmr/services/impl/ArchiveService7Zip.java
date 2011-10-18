package org.mmmr.services.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.sevenzipjbinding.ExtractAskMode;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IArchiveExtractCallback;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.ISevenZipInArchive;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;

import org.mmmr.services.DefaultArchiveEntryMatcher;
import org.mmmr.services.DefaultArchiveOutputStreamBuilder;
import org.mmmr.services.interfaces.ArchiveEntry;
import org.mmmr.services.interfaces.ArchiveEntryMatcher;
import org.mmmr.services.interfaces.ArchiveOutputStreamBuilder;

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
public class ArchiveService7Zip extends ArchiveServiceSimple {
    private abstract class Callback implements net.sf.sevenzipjbinding.ICryptoGetTextPassword, IArchiveExtractCallback {
        //
    }

    private class InvocationException extends RuntimeException {
        private static final long serialVersionUID = -8129174131545796688L;

        public InvocationException(IOException cause) {
            super(cause);
        }
    }

    private ArchiveEntry createEntry(ISevenZipInArchive inArchive, int i) throws SevenZipException {
        return new ArchiveEntry(//
                (String) inArchive.getProperty(i, PropID.PATH),//
                (Long) inArchive.getProperty(i, PropID.SIZE),//
                (Date) inArchive.getProperty(i, PropID.CREATION_TIME),//
                (Date) inArchive.getProperty(i, PropID.LAST_WRITE_TIME),//
                (Long) inArchive.getProperty(i, PropID.PACKED_SIZE),//
                (String) inArchive.getProperty(i, PropID.GROUP),//
                (String) inArchive.getProperty(i, PropID.USER),//
                (Boolean) inArchive.getProperty(i, PropID.IS_FOLDER)//
        );
    }

    /**
     * 
     * @see org.mmmr.services.impl.ArchiveServiceSimple#extract(java.io.File, org.mmmr.services.interfaces.ArchiveOutputStreamBuilder,
     *      org.mmmr.services.interfaces.ArchiveEntryMatcher)
     */
    @Override
    public Collection<ArchiveEntry> extract(final File archive, final ArchiveOutputStreamBuilder target, final ArchiveEntryMatcher matcher)
            throws IOException {
        return this.extract(archive, target, matcher, null);
    }

    public Collection<ArchiveEntry> extract(final File archive, final ArchiveOutputStreamBuilder target, final ArchiveEntryMatcher matcher,
            final String pwd) throws IOException {
        final Wrapper<RandomAccessFile> randomAccessFile = new Wrapper<RandomAccessFile>();
        final Wrapper<ISevenZipInArchive> inArchive = new Wrapper<ISevenZipInArchive>();
        try {
            randomAccessFile.v = new RandomAccessFile(archive, "r");
            inArchive.v = SevenZip.openInArchive(null, new RandomAccessFileInStream(randomAccessFile.v));
            List<Integer> indexesToExtract = new ArrayList<Integer>();
            final Map<Integer, ArchiveEntry> map = new HashMap<Integer, ArchiveEntry>();
            for (int i = 0; i < inArchive.v.getNumberOfItems(); i++) {
                if ((Boolean) inArchive.v.getProperty(i, PropID.IS_FOLDER)) {
                    continue;
                }
                ArchiveEntry ae = this.createEntry(inArchive.v, i);
                if (matcher.matches(ae)) {
                    indexesToExtract.add(i);
                    map.put(i, ae);
                }
            }
            int[] in = new int[indexesToExtract.size()];
            for (int i = 0; i < in.length; i++) {
                in[i] = indexesToExtract.get(i);
            }
            final Wrapper<OutputStream> outputStream = new Wrapper<OutputStream>();
            inArchive.v.extract(in, false, new Callback() {
                @Override
                public String cryptoGetTextPassword() throws SevenZipException {
                    return pwd;
                }

                @Override
                public ISequentialOutStream getStream(int idx, ExtractAskMode extractAskMode) throws SevenZipException {
                    boolean skipExtraction = (Boolean) inArchive.v.getProperty(idx, PropID.IS_FOLDER);
                    if (skipExtraction || (extractAskMode != ExtractAskMode.EXTRACT)) {
                        return null;
                    }
                    ArchiveEntry entry = map.get(idx);
                    try {
                        outputStream.v = new BufferedOutputStream(target.createOutputStream(entry));
                    } catch (IOException ex) {
                        throw new InvocationException(ex);
                    }
                    return new ISequentialOutStream() {
                        @Override
                        public int write(byte[] data) throws SevenZipException {
                            try {
                                outputStream.v.write(data);
                            } catch (IOException ex) {
                                throw new InvocationException(ex);
                            }
                            return data.length;
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
                    if (extractOperationResult != ExtractOperationResult.OK) {
                        throw new RuntimeException(String.valueOf(extractOperationResult));
                    }
                    if (outputStream.v == null) {
                        return;
                    }
                    try {
                        outputStream.v.flush();
                        outputStream.v.close();
                    } catch (IOException ex) {
                        throw new InvocationException(ex);
                    }
                    outputStream.v = null;
                }

                @Override
                public void setTotal(long total) throws SevenZipException {
                    //
                }
            });

            return map.values();
        } catch (InvocationException ex) {
            throw (IOException) ex.getCause();
        } catch (SevenZipException ex) {
            throw new IOException(ex);
        } finally {
            if (inArchive.v != null) {
                try {
                    inArchive.v.close();
                } catch (Exception e) {
                    System.err.println("Error closing archive: " + e);
                }
            }
            if (randomAccessFile.v != null) {
                try {
                    randomAccessFile.v.close();
                } catch (Exception e) {
                    System.err.println("Error closing file: " + e);
                }
            }
        }
    }

    /**
     * 
     * @see org.mmmr.services.interfaces.ArchiveServiceI#extract(java.io.File, java.io.File)
     */
    @Override
    public Collection<ArchiveEntry> extract(File archive, File target) throws IOException {
        return this.extract(archive, new DefaultArchiveOutputStreamBuilder(target), new DefaultArchiveEntryMatcher());
    }

    /**
     * 
     * @see org.mmmr.services.interfaces.ArchiveServiceI#list(java.io.File)
     */
    @Override
    public List<ArchiveEntry> list(File archive) throws IOException {
        List<ArchiveEntry> entries = new ArrayList<ArchiveEntry>();
        RandomAccessFile randomAccessFile = null;
        ISevenZipInArchive inArchive = null;
        try {
            randomAccessFile = new RandomAccessFile(archive, "r");
            inArchive = SevenZip.openInArchive(null, new RandomAccessFileInStream(randomAccessFile));
            int itemCount = inArchive.getNumberOfItems();
            for (int i = 0; i < itemCount; i++) {
                entries.add(this.createEntry(inArchive, i));
            }
        } catch (InvocationException ex) {
            throw (IOException) ex.getCause();
        } catch (SevenZipException ex) {
            throw new IOException(ex);
        } finally {
            if (inArchive != null) {
                try {
                    inArchive.close();
                } catch (Exception e) {
                    System.err.println("Error closing archive: " + e);
                }
            }
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (Exception e) {
                    System.err.println("Error closing file: " + e);
                }
            }
        }
        return entries;
    }
}
