package org.mmmr.services;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
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

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.mmmr.services.swing.common.FancySwing;

/**
 * @author Jurgen
 */
public class IOMethods {
    public static class MemInfo {
        public final long memfreemb;

        public final long memtotmb;

        public final double memusage;

        private transient String toString;

        public MemInfo(long memtotmb, long memfreemb, double memusage) {
            super();
            this.memtotmb = memtotmb;
            this.memfreemb = memfreemb;
            this.memusage = memusage;
        }

        @Override
        public String toString() {
            if (this.toString == null) {
                this.toString = new ToStringBuilder(this).appendSuper(super.toString()).append("memfreemb", this.memfreemb)
                        .append("memtotmb", this.memtotmb).append("memusage", this.memusage).toString();
            }
            return this.toString;
        }
    }

    private static long _copy(File source, File target) throws IOException {
        if (target != null) {
            target.getParentFile().mkdirs();
        }
        OutputStream out = target == null ? null : new FileOutputStream(target);
        CheckedInputStream in = new CheckedInputStream(new FileInputStream(source), new CRC32());
        byte[] buffer = new byte[1024 * 8];
        int read;
        while ((read = in.read(buffer)) != -1) {
            if (out != null) {
                out.write(buffer, 0, read);
            }
        }
        if (out != null) {
            out.close();
        }
        in.close();
        return in.getChecksum().getValue();
    }

    public static long copyFile(File source, File target) throws IOException {
        return IOMethods._copy(source, target);
    }

    public static String crc2string(long crc) {
        return Long.toHexString(crc).toUpperCase();
    }

    public static long crc32File(File source) throws IOException {
        return IOMethods._copy(source, null);
    }

    public static boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    IOMethods.deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        return (path.delete());
    }

    public static boolean fileEquals(File f1, File f2) throws IOException {
        return (f1.isDirectory() && f2.isDirectory())
                || (f1.exists() && f2.exists() && (f1.length() == f2.length()) && (IOMethods.crc32File(f1) == IOMethods.crc32File(f2)));
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
            String version = text.substring(pos + 13, text.indexOf("\"", pos + 15));

            info.add(new String[] { option, version, String.valueOf(_64bit) });
        }
        return info;
    }

    public static Collection<String> getAllJavaRuntimes() {
        Collection<String> all = new HashSet<String>();
        for (String opt : IOMethods.getRegValue("HKEY_LOCAL_MACHINE\\SOFTWARE\\JavaSoft\\Java Development Kit", "JavaHome", "REG_SZ")) {
            all.add(opt + "\\jre");
        }
        for (String opt : IOMethods.getRegValue("HKEY_LOCAL_MACHINE\\SOFTWARE\\Wow6432Node\\JavaSoft\\Java Development Kit", "JavaHome", "REG_SZ")) {
            all.add(opt + "\\jre");
        }
        all.addAll(IOMethods.getRegValue("HKEY_LOCAL_MACHINE\\SOFTWARE\\JavaSoft\\Java Runtime Environment", "JavaHome", "REG_SZ"));
        all.addAll(IOMethods.getRegValue("HKEY_LOCAL_MACHINE\\SOFTWARE\\Wow6432Node\\JavaSoft\\Java Runtime Environment", "JavaHome", "REG_SZ"));
        return all;
    }

    @SuppressWarnings("restriction")
    public static MemInfo getMemInfo() {
        com.sun.management.OperatingSystemMXBean o = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        long memtotmb = o.getTotalPhysicalMemorySize() / 1024 / 1024;
        long memfreemb = o.getFreePhysicalMemorySize() / 1024 / 1024;
        double memusage = (double) (memtotmb - memfreemb) / memtotmb;
        return new MemInfo(memtotmb, memfreemb, memusage);
    }

    public static List<String> getRegValue(String path, String key, String type) {
        try {
            String command = "reg query \"" + path + "\" /s /v " + key;
            Process process = Runtime.getRuntime().exec(command);
            final InputStream is = process.getInputStream();
            final StringWriter sw = new StringWriter();
            Thread reader = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int c;
                        while ((c = is.read()) != -1) {
                            sw.write(c);
                        }
                    } catch (IOException ex) {
                        ExceptionAndLogHandler.log(ex);
                    }
                }
            });
            reader.setDaemon(true);
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
        } catch (Exception ex) {
            ExceptionAndLogHandler.log(ex);
            return null;
        }
    }

    public static byte[] getResource(String path) throws IOException {
        return IOMethods.read(IOMethods.class.getClassLoader().getResourceAsStream(path));
    }

    public static boolean is64Bit() {
        return "64".equals(System.getProperties().getProperty("sun.arch.data.model"));
    }

    @SuppressWarnings("unchecked")
    public static List<File> list(File dir) {
        File[] tmp = dir.listFiles();
        if ((tmp == null) || (tmp.length == 0)) {
            return Collections.EMPTY_LIST;
        }
        return Arrays.asList(tmp);
    }

    public static List<File> listRecursive(File dir) {
        List<File> all = new ArrayList<File>();
        IOMethods.listRecursive(dir, all);
        return all;
    }

    private static void listRecursive(File dir, List<File> all) {
        File[] tmp = dir.listFiles();
        if ((tmp == null) || (tmp.length == 0)) {
            return;
        }
        for (File child : tmp) {
            all.add(child);
            if (child.isDirectory()) {
                IOMethods.listRecursive(child, all);
            }
        }
    }

    public static void loadjarAtRuntime(File jar) throws SecurityException, NoSuchMethodException, IllegalArgumentException, MalformedURLException,
            IllegalAccessException, InvocationTargetException {
        System.out.println("loading " + jar.getName());
        URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class<?> sysclass = URLClassLoader.class;
        Method method = sysclass.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        method.invoke(sysloader, new Object[] { jar.toURI().toURL() });
    }

    public static File newDir(File parent, String relative) {
        File newfile = new File(parent, relative);
        newfile.mkdirs();
        return newfile;
    }

    public static File newDir(String relative) {
        File newfile = new File(relative);
        newfile.mkdirs();
        return newfile;
    }

    public static Map<String, String> parseParams(String[] args) {
        Map<String, String> parameterValues = new HashMap<String, String>();
        if (args != null) {
            for (String arg : args) {
                if (arg.indexOf('=') == -1) {
                    if (arg.startsWith("-")) {
                        parameterValues.put(arg.substring(1), "true");
                    } else {
                        parameterValues.put(arg, "true");
                    }
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

    public static List<String> process(boolean capture, boolean log, String... command) throws IOException {
        List<String> lines = new ArrayList<String>();
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process p = pb.start();

        if (capture) {
            InputStream is = p.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;

            while ((line = br.readLine()) != null) {
                if (capture) {
                    lines.add(line);
                }

                if (log) {
                    ExceptionAndLogHandler.log(line);
                }
            }

            is.close();
        }

        return lines;
    }

    public static byte[] read(InputStream in) throws IOException {
        byte[] buffer = new byte[1024 * 8 * 4];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        return out.toByteArray();
    }

    public static File selectFile(File start, javax.swing.filechooser.FileFilter ff) {
        JFileChooser fc = new JFileChooser(start);
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fc.setAcceptAllFileFilterUsed(false);
        if (ff != null) {
            fc.addChoosableFileFilter(ff);
        }
        int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            return file;
        }
        return null;
    }

    public static boolean showConfirmation(Config cfg, String title, String message) {
        JOptionPane jop = new JOptionPane(message, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
        JDialog dialog = jop.createDialog(FancySwing.getCurrentFrame(), title);
        if (cfg != null) {
            dialog.setIconImage(cfg.getIcon().getImage());
        }
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
        dialog.dispose();
        return jop.getValue().equals(JOptionPane.YES_OPTION);
    }

    public static void showInformation(Config cfg, String title, String message) {
        JOptionPane jop = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION);
        JDialog dialog = jop.createDialog(FancySwing.getCurrentFrame(), title);
        if (cfg != null) {
            dialog.setIconImage(cfg.getIcon().getImage());
        }
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
        dialog.dispose();
    }

    @SuppressWarnings("unchecked")
    public static <T> T showOptions(Config cfg, String title, String message, T[] options, T selected) {
        JOptionPane pane = new JOptionPane(message, JOptionPane.QUESTION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, null, null);
        pane.setWantsInput(true);
        pane.setSelectionValues(options);
        pane.setInitialSelectionValue(selected);
        JDialog dialog = pane.createDialog(FancySwing.getCurrentFrame(), title);
        if (cfg != null) {
            dialog.setIconImage(cfg.getIcon().getImage());
        }
        pane.selectInitialValue();
        dialog.setVisible(true);
        dialog.dispose();
        return (T) pane.getInputValue();
    }

    public static void showWarning(Config cfg, String title, String message) {
        JOptionPane jop = new JOptionPane(message, JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION);
        JDialog dialog = jop.createDialog(FancySwing.getCurrentFrame(), title);
        if (cfg != null) {
            dialog.setIconImage(cfg.getIcon().getImage());
        }
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
        dialog.dispose();
    }
}