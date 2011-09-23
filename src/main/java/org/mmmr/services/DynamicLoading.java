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
            "http://repo1.maven.org/maven2/", //$NON-NLS-1$
            "http://mmmr.googlecode.com/svn/maven2", //$NON-NLS-1$
            "http://uk.maven.org/maven2", //$NON-NLS-1$
            "http://mirrors.ibiblio.org/pub/mirrors/maven2/" }; //$NON-NLS-1$

    public static void init(StatusListener status, Config cfg) throws Exception {
        String message = ""; //$NON-NLS-1$
        try {
            IOMethods.newDir("data/libs"); //$NON-NLS-1$
            BufferedReader in = new BufferedReader(new InputStreamReader(DynamicLoading.class.getClassLoader().getResourceAsStream("libs.txt"))); //$NON-NLS-1$
            String relative;
            while ((relative = in.readLine()) != null) {
                String[] parts = relative.split("::"); //$NON-NLS-1$
                long len = Long.parseLong(parts[0]);
                relative = parts[1];
                File jar = new File(cfg.getLibs(), relative.substring(relative.lastIndexOf('/') + 1));
                message = jar.getName();
                if (!(jar.exists() && (jar.length() == len))) {
                    status.setStatus(Messages.getString("DynamicLoading.libs_downloading") + jar.getName(), null); //$NON-NLS-1$
                    boolean success = false;
                    while (!success) {
                        for (String repo : DynamicLoading.MAVEN_REPO) {
                            URL url = new URL(repo + "/" + relative); //$NON-NLS-1$
                            try {
                                DownloadingService.downloadURL(url, jar);
                                if (jar.length() != len) {
                                    jar.delete();
                                    throw new IOException(jar.getName() + ": length><" + len); //$NON-NLS-1$
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
                        throw new IOException(jar.getName() + ": length><" + len); //$NON-NLS-1$
                    }
                }
                if (cfg.getParameterValue("dev") == null) { //$NON-NLS-1$
                    status.setStatus(Messages.getString("DynamicLoading.libs_loading") + jar.getName(), null); //$NON-NLS-1$
                    IOMethods.loadjarAtRuntime(jar);
                }
            }
            in.close();

            status.setStatus(Messages.getString("DynamicLoading.libs_ready"), true); //$NON-NLS-1$
        } catch (Exception ex) {
            status.setStatus(Messages.getString("DynamicLoading.libs_loading_failed") + message, false); //$NON-NLS-1$
            throw ex;
        }
    }

    public static void main(String[] args) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(new File("src/main/resources/libs.txt"))); //$NON-NLS-1$
            String M2_REPO = System.getenv("M2_REPO"); //$NON-NLS-1$
            if (M2_REPO == null) {
                throw new RuntimeException("M2_REPO system variable not set"); //$NON-NLS-1$
            }
            String repo = new File(M2_REPO + "/repository").getAbsolutePath().replace('\\', '/'); //$NON-NLS-1$
            for (String cp : System.getProperty("java.class.path").split(";")) { //$NON-NLS-1$ //$NON-NLS-2$
                String path = new File(cp).getAbsolutePath().replace('\\', '/');
                if (path.endsWith("target/classes")) { //$NON-NLS-1$
                    continue;
                }
                out.write(new File(cp).length() + "::" + path.substring(repo.length() + 1) + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            out.close();
        } catch (Exception ex) {
            ExceptionAndLogHandler.log(ex);
        }
    }

}
