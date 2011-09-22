package org.mmmr.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.mmmr.services.swing.StatusListener;

// TODO when this issue is resolved (http://sourceforge.net/apps/mediawiki/sevenzipjbind/index.php?title=Maven) download 7zipbinding from maven repo
/**
 * @author Jurgen
 */
public class DynamicLoading {
    private static final String[] MAVEN_REPO = {
            "http://repo1.maven.org/maven2/",
            "http://mmmr.googlecode.com/svn/maven2",
            "http://uk.maven.org/maven2",
            "http://mirrors.ibiblio.org/pub/mirrors/maven2/" };

    public static void init(StatusListener status, Config cfg) throws Exception {
        String message = "";
        try {
            IOMethods.newDir("data/libs");
            BufferedReader in = new BufferedReader(new InputStreamReader(DynamicLoading.class.getClassLoader().getResourceAsStream("libs.txt")));
            String relative;
            while ((relative = in.readLine()) != null) {
                String[] parts = relative.split("::");
                long len = Long.parseLong(parts[0]);
                relative = parts[1];
                File jar = new File(cfg.getLibs(), relative.substring(relative.lastIndexOf('/') + 1));
                message = jar.getName();
                if (!(jar.exists() && (jar.length() == len))) {
                    status.setStatus("Program libraries: downloading " + jar.getName(), null);
                    boolean success = false;
                    while (!success) {
                        for (String repo : DynamicLoading.MAVEN_REPO) {
                            URL url = new URL(repo + "/" + relative);
                            try {
                                DownloadingService.downloadURL(url, jar);
                                if (jar.length() != len) {
                                    jar.delete();
                                    throw new IOException(jar.getName() + ": length><" + len);
                                }
                                success = true;
                                break;
                            } catch (Exception ex) {
                                success = false;
                                ExceptionAndLogHandler.log(ex);
                            }
                        }
                    }
                    if (!success) {
                        jar.delete();
                        throw new IOException(jar.getName() + ": length><" + len);
                    }
                }
                if (cfg.getParameterValue("dev") == null) {
                    status.setStatus("Program libraries: loading " + jar.getName(), null);
                    IOMethods.loadjarAtRuntime(jar);
                }
            }
            in.close();

            status.setStatus("Program libraries: ready", true);
        } catch (Exception ex) {
            status.setStatus("Program libraries: loading failed: " + message, false);
            throw ex;
        }
    }

    public static void main(String[] args) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(new File("src/main/resources/libs.txt")));
            String M2_REPO = System.getenv("M2_REPO");
            if (M2_REPO == null) {
                throw new RuntimeException("M2_REPO system variable not set");
            }
            String repo = new File(M2_REPO + "/repository").getAbsolutePath().replace('\\', '/');
            for (String cp : System.getProperty("java.class.path").split(";")) {
                String path = new File(cp).getAbsolutePath().replace('\\', '/');
                if (path.startsWith(repo)) {
                    String relative = path.substring(repo.length() + 1);
                    URL url = new URL(DynamicLoading.MAVEN_REPO + relative);
                    System.out.println(url.openConnection().getContentLength() + "::" + url);
                    out.write(url.openConnection().getContentLength() + "::" + relative + "\n");
                }
            }
            out.close();
        } catch (Exception ex) {
            ExceptionAndLogHandler.log(ex);
        }
    }

}
