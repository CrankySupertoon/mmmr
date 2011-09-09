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

	public ISequentialOutStream getStream(int i, ExtractAskMode extractAskMode) throws SevenZipException {
	    this.index = i;
	    skipExtraction = (Boolean) inArchive.getProperty(index, PropID.IS_FOLDER);
	    if (skipExtraction || extractAskMode != ExtractAskMode.EXTRACT) {
		return null;
	    }
	    return new ISequentialOutStream() {
		public int write(byte[] data) throws SevenZipException {
		    try {
			Object path = inArchive.getProperty(index, PropID.PATH);
			if (!path.equals(current)) {
			    if (out != null) {
				out.flush();
				out.close();
			    }
			    total = 0;
			    crc32 = new CRC32();
			    current = String.valueOf(path);
			    File target = new File(outdir, String.valueOf(path));
			    target.getParentFile().mkdirs();
			    out = new BufferedOutputStream(new FileOutputStream(target));
			}
			try {
			    out.write(data, 0, data.length);
			    crc32.update(data, 0, data.length);
			} catch (IOException ex) {
			    throw new RuntimeException(ex);
			}
			total += data.length;
			return data.length;
		    } catch (IOException ex) {
			throw new RuntimeException(ex);
		    }
		}
	    };
	}

	public void prepareOperation(ExtractAskMode extractAskMode) throws SevenZipException {
	    //
	}

	public void setCompleted(long completeValue) throws SevenZipException {
	    //
	}

	public void setOperationResult(ExtractOperationResult extractOperationResult) throws SevenZipException {
	    if (skipExtraction) {
		return;
	    }
	    try {
		out.flush();
		out.close();
	    } catch (Exception ex) {
		//
	    }
	    if (extractOperationResult != ExtractOperationResult.OK) {
		System.err.println("Extraction error");
	    } else {
		System.out.println(Long.toHexString(crc32.getValue()).toUpperCase() + " | " + total + " | " + inArchive.getProperty(index, PropID.PATH));
	    }
	}

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
	if (exception != null)
	    throw exception;
	if (runtimeException != null)
	    throw runtimeException;
    }
}
