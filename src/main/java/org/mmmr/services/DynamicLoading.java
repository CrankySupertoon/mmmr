package org.mmmr.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;

public class DynamicLoading {
    private static final String MAVEN_REPO = "http://repo1.maven.org/maven2/";

    public static void init(File libs) throws IOException, SecurityException, IllegalArgumentException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(DynamicLoading.class.getClassLoader().getResourceAsStream("libs.txt")));
            String relative;
            while ((relative = in.readLine()) != null) {
                File jar = new File(libs, relative.substring(relative.lastIndexOf('/') + 1));
                if (!(jar.exists() && jar.length() > (4 * 1024))) {
                    URL url = new URL(MAVEN_REPO + relative);
                    downloadURL(url, jar);
                }
                loadjarAtRuntime(jar);
            }
            in.close();
        }
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(DynamicLoading.class.getClassLoader().getResourceAsStream(
                    "7zip/7zip.binding")));
            String relative;
            while ((relative = in.readLine()) != null) {
                File jar = new File(libs, relative.substring(relative.lastIndexOf('/') + 1));
                if (!(jar.exists() && jar.length() > (4 * 1024))) {
                    URL url = new URL(DynamicLoading.class.getClassLoader().getResource("7zip/") + relative);
                    downloadURL(url, jar);
                }
                loadjarAtRuntime(jar);
            }
            in.close();
        }
    }

    public static void main(String[] args) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(new File("src/main/resources/libs.txt")));
            String M2_REPO = System.getenv("M2_REPO");
            if (M2_REPO == null) {
                // FIXME via home directory + .m
            }
            String repo = new File(M2_REPO + "/repository").getAbsolutePath().replace('\\', '/');
            for (String cp : System.getProperty("java.class.path").split(";")) {
                String path = new File(cp).getAbsolutePath().replace('\\', '/');
                if (path.startsWith(repo)) {
                    String relative = path.substring(repo.length() + 1);
                    URL url = new URL(MAVEN_REPO + relative);
                    System.out.println(url.openConnection().getContentLength() + "::" + url);
                    out.write(relative + "\n");
                }
            }
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void loadjarAtRuntime(File jar) throws SecurityException, NoSuchMethodException, IllegalArgumentException, MalformedURLException,
            IllegalAccessException, InvocationTargetException {
        URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class<?> sysclass = URLClassLoader.class;
        Method method = sysclass.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        method.invoke(sysloader, new Object[] { jar.toURI().toURL() });
    }

    public static void downloadURL(URL url, File target) throws IOException {
        System.out.println(url);
        URLConnection conn = url.openConnection();
        conn.setAllowUserInteraction(false);
        conn.setConnectTimeout(10000);
        conn.setDefaultUseCaches(true);
        conn.setReadTimeout(10000);
        conn.setUseCaches(true);
        InputStream uin = conn.getInputStream();
        OutputStream fout = new FileOutputStream(target);
        byte[] buffer = new byte[1024 * 8];
        int read;
        while ((read = uin.read(buffer)) > 0) {
            fout.write(buffer, 0, read);
        }
        fout.close();
        uin.close();
    }
}
