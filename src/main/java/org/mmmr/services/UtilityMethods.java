package org.mmmr.services;

import java.awt.Component;
import java.awt.Frame;
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
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLEncoder;
import java.text.Normalizer;
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

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.WindowConstants;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.swingeasy.RoundedOptionPane;
import org.swingeasy.UIUtils;
import org.swingeasy.UIUtils.MoveMouseListener;

/**
 * do not put methods in here that use non standard Java classes
 * 
 * @author Jurgen
 */
public class UtilityMethods {
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
                this.toString = new ToStringBuilder(this).appendSuper(super.toString()).append("memfreemb", this.memfreemb) //$NON-NLS-1$
                        .append("memtotmb", this.memtotmb).append("memusage", this.memusage).toString(); //$NON-NLS-1$ //$NON-NLS-2$
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

    public static String absolutePath(File file) throws IOException {
        return file.getCanonicalFile().getAbsolutePath().replace('\\', '/');
    }

    public static long copy(InputStream in, OutputStream out) throws IOException, NullPointerException {
        long total = -1;
        try {
            byte[] buffer = new byte[1024 * 8];
            int read;

            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
                total += read;
            }
        } finally {
            try {
                in.close();
            } catch (Exception ex) {
                //
            }

            try {
                out.close();
            } catch (Exception ex) {
                //
            }
        }
        return total;
    }

    public static long copyFile(File source, File target) throws IOException {
        return UtilityMethods._copy(source, target);
    }

    public static String crc2string(long crc) {
        return Long.toHexString(crc).toUpperCase();
    }

    public static long crc32File(File source) throws IOException {
        return UtilityMethods._copy(source, null);
    }

    private static JDialog createDialog(JOptionPane pane, String title) {
        try {
            final JDialog dialog = new JDialog((Frame) null, title, true);
            dialog.setUndecorated(true);
            int style = UtilityMethods.styleFromMessageType(pane.getMessageType());
            Method method = JOptionPane.class.getDeclaredMethod("initDialog", JDialog.class, Integer.TYPE, Component.class); //$NON-NLS-1$
            method.setAccessible(true);
            method.invoke(pane, dialog, style, null);
            return dialog;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * delete a file or directory recursive, does not throw exceptions when files does not exists or null is given but returns false
     */
    public static boolean delete(File path) {
        ExceptionAndLogHandler.log("deleting " + path);
        if ((path != null) && path.exists()) {
            File[] files = path.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        UtilityMethods.delete(file);
                    } else {
                        file.delete();
                    }
                }
            }
            return path.delete();
        }
        return false;
    }

    protected static void dialogPostCreate(JDialog dialog) {
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        UIUtils.rounded(dialog);
        UIUtils.translucent(dialog);
        dialog.setLocationRelativeTo(UIUtils.getCurrentFrame());
        dialog.setVisible(true);
        dialog.dispose();
    }

    /**
     * encode url parameter value part (' ' will be replaced by '+')
     */
    public static String encodeURLParameter(String parameter) {
        if (parameter == null) {
            return null;
        }
        try {
            return URLEncoder.encode(parameter, "UTF-8");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * encode url path part (' ' will be replaced by '%20')
     */
    public static String encodeURLPath(String path) {
        if (path == null) {
            return null;
        }
        boolean addSlash = !path.startsWith("/");
        if (addSlash) {
            path = "/" + path;
        }
        try {
            URI base = new URI("http", null, "www.google.com", 80, null, null, null);
            URI uri = new URI("http" // protocol
                    , null // user
                    , "www.google.com" // host
                    , 80 // port, verplicht
                    , path // path MOET met '/' beginnen
                    , null // key1=value1&key2=value2... ZONDER te beginnen met '?' */
                    , null // fragment, wat achter de # staat aka html anchor
            );
            String baserequest = base.toASCIIString();
            String request = uri.toASCIIString();
            String encodedPath = request.substring(baserequest.length());
            if (addSlash) {
                encodedPath = encodedPath.substring(1);
            }
            return encodedPath;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static boolean fileEquals(File f1, File f2) throws IOException {
        return (f1.isDirectory() && f2.isDirectory())
                || (f1.exists() && f2.exists() && (f1.length() == f2.length()) && (UtilityMethods.crc32File(f1) == UtilityMethods.crc32File(f2)));
    }

    /**
     * list of java runtime options containing per record: absolute path to jre, java version, java 64 bit (boolean as text)
     */
    public static List<String[]> getAllJavaInfo(Collection<String> all) throws IOException {
        List<String[]> info = new ArrayList<String[]>();
        for (String option : all) {
            ProcessBuilder pb = new ProcessBuilder(option + "/bin/java.exe", "-version"); //$NON-NLS-1$ //$NON-NLS-2$
            pb.redirectErrorStream(true);
            Process p = pb.start();
            InputStream in = p.getInputStream();
            StringBuilder sb = new StringBuilder();
            int c;
            while ((c = in.read()) != -1) {
                sb.append((char) c);
            }
            String text = sb.toString().toLowerCase();
            boolean _64bit = text.contains("64-bit"); //$NON-NLS-1$
            int pos = text.indexOf("java version \"") + 1; //$NON-NLS-1$
            String version = text.substring(pos + 13, text.indexOf("\"", pos + 15)); //$NON-NLS-1$

            info.add(new String[] { option, version, String.valueOf(_64bit) });
        }
        return info;
    }

    public static Collection<String> getAllJavaRuntimes() {
        Collection<String> all = new HashSet<String>();
        for (String opt : UtilityMethods.getRegValue("HKEY_LOCAL_MACHINE\\SOFTWARE\\JavaSoft\\Java Development Kit", "JavaHome", "REG_SZ")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            all.add(opt + "\\jre"); //$NON-NLS-1$
        }
        for (String opt : UtilityMethods.getRegValue(
                "HKEY_LOCAL_MACHINE\\SOFTWARE\\Wow6432Node\\JavaSoft\\Java Development Kit", "JavaHome", "REG_SZ")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            all.add(opt + "\\jre"); //$NON-NLS-1$
        }
        all.addAll(UtilityMethods.getRegValue("HKEY_LOCAL_MACHINE\\SOFTWARE\\JavaSoft\\Java Runtime Environment", "JavaHome", "REG_SZ")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        all.addAll(UtilityMethods.getRegValue("HKEY_LOCAL_MACHINE\\SOFTWARE\\Wow6432Node\\JavaSoft\\Java Runtime Environment", "JavaHome", "REG_SZ")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        return all;
    }

    public static File getCurrentDir() {
        return new File(System.getProperty("user.dir"));//$NON-NLS-1$ 
    }

    public static File getCurrentJar() {
        return new File(UtilityMethods.class.getProtectionDomain().getCodeSource().getLocation().getPath());
    }

    public static String getDrive(File file) throws IOException {
        return file.getCanonicalPath().substring(0, 2);
    }

    public static Icon getIcon(String path) {
        return new ImageIcon(UtilityMethods.class.getClassLoader().getResource(path));
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
            String command = "reg query \"" + path + "\" /s /v " + key; //$NON-NLS-1$ //$NON-NLS-2$
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
            StringTokenizer st = new StringTokenizer(result, "\r\n"); //$NON-NLS-1$
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
        return UtilityMethods.read(UtilityMethods.class.getClassLoader().getResourceAsStream(path));
    }

    public static boolean is64Bit() {
        return "64".equals(System.getProperties().getProperty("sun.arch.data.model")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public static boolean isDevelopmentMode() {
        return !UtilityMethods.getCurrentJar().getName().endsWith(".jar");
    }

    public static boolean isStandAloneMode() {
        return !UtilityMethods.isDevelopmentMode();
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
        UtilityMethods.listRecursive(dir, all);
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
                UtilityMethods.listRecursive(child, all);
            }
        }
    }

    public static void loadjarAtRuntime(File jar) throws SecurityException, NoSuchMethodException, IllegalArgumentException, MalformedURLException,
            IllegalAccessException, InvocationTargetException {
        System.out.println("loading " + jar.getName()); //$NON-NLS-1$
        URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class<?> sysclass = URLClassLoader.class;
        Method method = sysclass.getDeclaredMethod("addURL", URL.class); //$NON-NLS-1$
        method.setAccessible(true);
        method.invoke(sysloader, new Object[] { jar.toURI().toURL() });
    }

    public static void main(String[] args) {
        for (File f : new File("C:/java/workspaces/TRUNK").listFiles()) {
            try {
                for (File ff : f.listFiles()) {
                    File fff = new File(ff, "target-eclipse/.svn");
                    if (fff.exists()) {
                        UtilityMethods.delete(fff);
                        System.out.println(fff);
                    }
                }
            } catch (NullPointerException ex) {
                // TODO: handle exception
            }
        }
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
                    if (arg.startsWith("-")) { //$NON-NLS-1$
                        parameterValues.put(arg.substring(1), "true"); //$NON-NLS-1$
                    } else {
                        parameterValues.put(arg, "true"); //$NON-NLS-1$
                    }
                } else {
                    String[] kv = arg.split("="); //$NON-NLS-1$
                    if (kv[0].startsWith("-")) { //$NON-NLS-1$
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

    public static String relativePath(File base, File file) throws IOException {
        return file.getCanonicalFile().getAbsolutePath().substring(base.getCanonicalFile().getAbsolutePath().length() + 1).replace('\\', '/');
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
        RoundedOptionPane jop = new RoundedOptionPane(message, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
        jop.getDelegate().setShady(false);
        new MoveMouseListener(jop);
        JDialog dialog = UtilityMethods.createDialog(jop, title);
        if (cfg != null) {
            dialog.setIconImage(cfg.getIcon().getImage());
        }
        UtilityMethods.dialogPostCreate(dialog);
        return jop.getValue().equals(JOptionPane.YES_OPTION);
    }

    public static boolean showConfirmationOkCancel(Config cfg, String title, String message) {
        RoundedOptionPane jop = new RoundedOptionPane(message, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        jop.getDelegate().setShady(false);
        new MoveMouseListener(jop);
        JDialog dialog = UtilityMethods.createDialog(jop, title);
        if (cfg != null) {
            dialog.setIconImage(cfg.getIcon().getImage());
        }
        UtilityMethods.dialogPostCreate(dialog);
        return jop.getValue().equals(JOptionPane.OK_OPTION);
    }

    public static void showInformation(Config cfg, String title, String message) {
        RoundedOptionPane jop = new RoundedOptionPane(message, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION);
        jop.getDelegate().setShady(false);
        new MoveMouseListener(jop);
        JDialog dialog = UtilityMethods.createDialog(jop, title);
        if (cfg != null) {
            dialog.setIconImage(cfg.getIcon().getImage());
        }
        UtilityMethods.dialogPostCreate(dialog);
    }

    @SuppressWarnings("unchecked")
    public static <T> T showOptions(Config cfg, String title, String message, T[] options, T selected) {
        RoundedOptionPane jop = new RoundedOptionPane(message, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, null, null);
        jop.getDelegate().setShady(false);
        new MoveMouseListener(jop);
        jop.setWantsInput(true);
        jop.setSelectionValues(options);
        jop.setInitialSelectionValue(selected);
        JDialog dialog = UtilityMethods.createDialog(jop, title);
        if (cfg != null) {
            dialog.setIconImage(cfg.getIcon().getImage());
        }
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        UIUtils.rounded(dialog);
        UIUtils.translucent(dialog);
        if (cfg != null) {
            dialog.setIconImage(cfg.getIcon().getImage());
        }
        jop.selectInitialValue();
        dialog.setLocationRelativeTo(UIUtils.getCurrentFrame());
        dialog.setVisible(true);
        dialog.dispose();
        if ("uninitializedValue".equals(jop.getInputValue())) {
            return null;
        }
        return (T) jop.getInputValue();
    }

    public static void showWarning(Config cfg, String title, String message) {
        RoundedOptionPane jop = new RoundedOptionPane(message, JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION);
        jop.getDelegate().setShady(false);
        new MoveMouseListener(jop);
        JDialog dialog = UtilityMethods.createDialog(jop, title);
        if (cfg != null) {
            dialog.setIconImage(cfg.getIcon().getImage());
        }
        UtilityMethods.dialogPostCreate(dialog);
    }

    public static String sortable(String s) {
        if (StringUtils.isBlank(s)) {
            return null;
        }

        StringBuilder sb = new StringBuilder();

        for (char c : s.toCharArray()) {
            for (char nc : Normalizer.normalize(String.valueOf(c), Normalizer.Form.NFKD).toCharArray()) {
                nc = Character.toUpperCase(nc);
                if (('A' <= nc) && (nc <= 'Z')) {
                    sb.append(nc);
                }
            }
        }

        if (sb.length() == 0) {
            sb.append(" ");
        }

        return sb.toString();
    }

    private static int styleFromMessageType(int messageType) {
        switch (messageType) {
            case JOptionPane.ERROR_MESSAGE:
                return JRootPane.ERROR_DIALOG;
            case JOptionPane.QUESTION_MESSAGE:
                return JRootPane.QUESTION_DIALOG;
            case JOptionPane.WARNING_MESSAGE:
                return JRootPane.WARNING_DIALOG;
            case JOptionPane.INFORMATION_MESSAGE:
                return JRootPane.INFORMATION_DIALOG;
            case JOptionPane.PLAIN_MESSAGE:
            default:
                return JRootPane.PLAIN_DIALOG;
        }
    }
}