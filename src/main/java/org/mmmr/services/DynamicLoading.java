package org.mmmr.services;

import static org.mmmr.services.IOMethods.downloadURL;
import static org.mmmr.services.IOMethods.loadjarAtRuntime;
import static org.mmmr.services.IOMethods.newDir;
import static org.mmmr.services.IOMethods.unzip;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.mmmr.services.StatusWindow.StatusPanel;

/**
 * @author Jurgen
 */
public class DynamicLoading {
    private static final String MAVEN_REPO = "http://repo1.maven.org/maven2/";

    private static final String MAVEN_REPO_MIRROR = "http://uk.maven.org/maven2";

    public static void init(StatusPanel status, Config cfg) throws Exception {
	String message = "";
	try {
	    newDir("data/libs");

	    String relative = null;
	    {
		File lib1 = new File(cfg.getLibs(), "sevenzipjbinding.jar");
		File lib2 = new File(cfg.getLibs(), "sevenzipjbinding-AllWindows.jar");

		if (!lib1.exists() || !lib2.exists()) {
		    File zip = new File(cfg.getTmp(), "sevenzipjbinding-4.65-1.04-rc-extr-only-AllWindows.zip");
		    message = zip.getName();
		    status.setStatus("Program libraries: downloading " + zip.getName(), null);
		    URL url = new URL(
			    "http://www.mirrorservice.org/sites/download.sourceforge.net/pub/sourceforge/s/project/se/sevenzipjbind/7-Zip-JBinding/4.65-1.04rc-extr-only/sevenzipjbinding-4.65-1.04-rc-extr-only-AllWindows.zip");
		    downloadURL(url, zip);
		    unzip(zip, cfg.getTmp());
		    {
			File jarFrom = new File(cfg.getTmp(), "sevenzipjbinding-4.65-1.04-rc-extr-only-AllWindows/lib/sevenzipjbinding.jar");
			message = lib1.getName();
			jarFrom.renameTo(lib1);
		    }
		    {
			File jarFrom = new File(cfg.getTmp(), "sevenzipjbinding-4.65-1.04-rc-extr-only-AllWindows/lib/sevenzipjbinding-AllWindows.jar");
			message = lib2.getName();
			jarFrom.renameTo(lib2);
		    }
		}
		status.setStatus("Program libraries: loading " + lib1.getName(), null);
		loadjarAtRuntime(lib1);
		status.setStatus("Program libraries: loading " + lib2.getName(), null);
		loadjarAtRuntime(lib2);
	    }
	    {
		BufferedReader in = new BufferedReader(new InputStreamReader(DynamicLoading.class.getClassLoader().getResourceAsStream("libs.txt")));
		while ((relative = in.readLine()) != null) {
		    String[] parts = relative.split("::");
		    long len = Long.parseLong(parts[0]);
		    relative = parts[1];
		    File jar = new File(cfg.getLibs(), relative.substring(relative.lastIndexOf('/') + 1));
		    message = jar.getName();
		    if (!(jar.exists() && jar.length() == len)) {
			status.setStatus("Program libraries: downloading " + jar.getName(), null);
			URL url = new URL(MAVEN_REPO + relative);
			try {
			    downloadURL(url, jar);
			    if (jar.length() != len) {
				jar.delete();
				throw new IOException("length><" + len);
			    }
			} catch (Exception e) {
			    url = new URL(MAVEN_REPO_MIRROR + relative);
			    downloadURL(url, jar);
			    if (jar.length() != len) {
				jar.delete();
				throw new IOException("length><" + len);
			    }
			}
		    }
		    status.setStatus("Program libraries: loading " + jar.getName(), null);
		    loadjarAtRuntime(jar);
		}
		in.close();
	    }
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
		// FIXME via home directory + .m
	    }
	    String repo = new File(M2_REPO + "/repository").getAbsolutePath().replace('\\', '/');
	    for (String cp : System.getProperty("java.class.path").split(";")) {
		String path = new File(cp).getAbsolutePath().replace('\\', '/');
		if (path.startsWith(repo)) {
		    String relative = path.substring(repo.length() + 1);
		    URL url = new URL(MAVEN_REPO + relative);
		    System.out.println(url.openConnection().getContentLength() + "::" + url);
		    out.write(url.openConnection().getContentLength() + "::" + relative + "\n");
		}
	    }
	    out.close();
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

}
