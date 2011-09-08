package org.mmmr.services;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JFileChooser;

import net.sf.sevenzipjbinding.ExtractAskMode;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IArchiveExtractCallback;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.ISevenZipInArchive;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;

public class IOMethods {

    public static File selectFile(File start, javax.swing.filechooser.FileFilter ff) {
        JFileChooser fc = new JFileChooser(start);
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fc.setAcceptAllFileFilterUsed(false);
        if (ff != null)
            fc.addChoosableFileFilter(ff);
        int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            return file;
        }
        return null;
    }

    public static boolean fileEquals(File f1, File f2) throws IOException {
        return f1.exists() && f2.exists() && f1.length() == f2.length() && crc32File(f1) == crc32File(f2);
    }

    @SuppressWarnings("unchecked")
    public static List<File> list(File dir) {
        File[] tmp = dir.listFiles();
        if (tmp == null || tmp.length == 0)
            return Collections.EMPTY_LIST;
        return Arrays.asList(tmp);
    }

    public static File newDir(String relative) {
        File newfile = new File(relative);
        newfile.mkdirs();
        return newfile;
    }

    public static File newDir(File parent, String relative) {
        File newfile = new File(parent, relative);
        newfile.mkdirs();
        return newfile;
    }

    public static boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

    public static void unzip(File zip, BufferedWriter log, File outdir) throws IOException {
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zip));
        ZipEntry ze;
        byte[] buffer = new byte[1024 * 8];
        int read;
        while ((ze = zis.getNextEntry()) != null) {
            if (ze.isDirectory())
                continue;
            CRC32 crc = null;
            OutputStream fout = null;
            if (log != null) {
                crc = new CRC32();
            }
            if (outdir != null) {
                File file = new File(outdir, ze.getName());
                file.getParentFile().mkdirs();
                fout = new FileOutputStream(file);
            }
            while ((read = zis.read(buffer)) != -1) {
                if (outdir != null)
                    fout.write(buffer, 0, read);
                if (log != null)
                    crc.update(buffer, 0, read);
            }
            if (outdir != null)
                fout.close();
            if (log != null)
                log.write(ze.getName() + " :: " + crc.getValue() + "\n");
        }
    }

    // public static void extractAnyArchive(File zip7exe, File archive, File outdir) throws IOException {
    // ProcessBuilder pb = new ProcessBuilder(zip7exe.getAbsolutePath(), "x", archive.getAbsolutePath());
    // pb.directory(outdir);
    // pb.start();
    // }

    public static void extract(File file, File out) throws IOException {
        IOException exception = null;
        RuntimeException runtimeException = null;
        RandomAccessFile randomAccessFile = null;
        ISevenZipInArchive inArchive = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "r");
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

    public static Map<String, String> parseParams(String[] args) {
        Map<String, String> parameterValues = new HashMap<String, String>();
        if (args != null) {
            for (String arg : args) {
                if (arg.indexOf('=') == -1) {
                    parameterValues.put(arg, "true");
                } else {
                    String[] kv = arg.split("=");
                    if (kv[0].startsWith("-")) {
                        parameterValues.put(kv[0].substring(1), kv[1]);
                    } else {
                        parameterValues.put(kv[0], kv[1]);
                    }
                }
            }
        }
        return parameterValues;
    }

    public static long copyFile(File source, File target) throws IOException {
        return _(source, target);
    }

    public static long crc32File(File source) throws IOException {
        return _(source, null);
    }

    private static long _(File source, File target) throws IOException {
        OutputStream out = target == null ? null : new FileOutputStream(target);
        CheckedInputStream in = new CheckedInputStream(new FileInputStream(source), new CRC32());
        byte[] buffer = new byte[1024 * 8];
        int read;
        while ((read = in.read(buffer)) != -1)
            if (out != null)
                out.write(buffer, 0, read);
        if (out != null)
            out.close();
        in.close();
        return in.getChecksum().getValue();
    }

    public static String crc2string(long crc) {
        return Long.toHexString(crc).toUpperCase();
    }

    public static boolean is64Bit() {
        return "64".equals(System.getProperties().getProperty("sun.arch.data.model"));
    }

    public static void main(String[] args) {
        try {
            Collection<String> all = getAllJavaRuntimes();
            getAllJavaInfo(all);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * list of java runtime options containing per record: absolute path to jre, java version, java 64 bit (boolean as text)
     */
    public static List<String[]> getAllJavaInfo(Collection<String> all) throws IOException {
        List<String[]> info = new ArrayList<String[]>();
        for (String option : all) {
            ProcessBuilder pb = new ProcessBuilder(option + "/bin/java.exe", "-version");
            pb.redirectErrorStream(true);
            Process p = pb.start();
            InputStream in = p.getInputStream();
            StringBuilder sb = new StringBuilder();
            int c;
            while ((c = in.read()) != -1) {
                sb.append((char) c);
            }
            String text = sb.toString().toLowerCase();
            boolean _64bit = text.contains("64-bit");
            int pos = text.indexOf("java version \"") + 1;
            String version = text.substring(pos+ 13, text.indexOf("\"" , pos + 15));

            info.add(new String[]{option,version,String.valueOf( _64bit)});
        }
        return info;
    }

    public static Collection<String> getAllJavaRuntimes() {
        Collection<String> all = new HashSet<String>();
        for (String opt : getRegValue("HKEY_LOCAL_MACHINE\\SOFTWARE\\JavaSoft\\Java Development Kit", "JavaHome", "REG_SZ"))
            all.add(opt + "\\jre");
        for (String opt : getRegValue("HKEY_LOCAL_MACHINE\\SOFTWARE\\Wow6432Node\\JavaSoft\\Java Development Kit", "JavaHome", "REG_SZ"))
            all.add(opt + "\\jre");
        all.addAll(getRegValue("HKEY_LOCAL_MACHINE\\SOFTWARE\\JavaSoft\\Java Runtime Environment", "JavaHome", "REG_SZ"));
        all.addAll(getRegValue("HKEY_LOCAL_MACHINE\\SOFTWARE\\Wow6432Node\\JavaSoft\\Java Runtime Environment", "JavaHome", "REG_SZ"));
        return all;
    }

    public static List<String> getRegValue(String path, String key, String type) {
        try {
            String command = "reg query \"" + path + "\" /s /v " + key;
            Process process = Runtime.getRuntime().exec(command);
            final InputStream is = process.getInputStream();
            final StringWriter sw = new StringWriter();
            Thread reader = new Thread(new Runnable() {
                public void run() {
                    try {
                        int c;
                        while ((c = is.read()) != -1)
                            sw.write(c);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            reader.start();
            process.waitFor();
            reader.join();

            String result = sw.toString();
            StringTokenizer st = new StringTokenizer(result, "\r\n");
            List<String> results = new ArrayList<String>();
            while (st.hasMoreTokens()) {
                String token = st.nextToken().trim();
                if (token.startsWith(key)) {
                    token = token.substring(token.indexOf(type) + type.length()).trim();
                    results.add(token);
                }
            }
            return results;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @see http://sevenzipjbind.sourceforge.net/basic_snippets.html#extraction-single-file
     */
    private static class Callback implements IArchiveExtractCallback {
        private CRC32 crc32;

        private int index;

        private boolean skipExtraction;

        private ISevenZipInArchive inArchive;

        private File outdir;

        private String current;

        private OutputStream out;

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
                System.out.println(Long.toHexString(crc32.getValue()).toUpperCase() + " | " + total + " | "
                        + inArchive.getProperty(index, PropID.PATH));
            }
        }

        public void setCompleted(long completeValue) throws SevenZipException {
            //
        }

        public void setTotal(long total) throws SevenZipException {
            //
        }
    }
}