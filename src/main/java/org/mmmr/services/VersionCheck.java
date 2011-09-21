package org.mmmr.services;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Node;

/**
 * @author Jurgen
 */
public class VersionCheck {
    public static final String version;

    public static final String shortJarName = "mmmr";

    public static final String packageName = "org.mmmr";

    public static final String mavenBase = "http://mmmr.googlecode.com/svn/maven2/" + VersionCheck.packageName.replace('.', '/') + "/"
            + VersionCheck.shortJarName;

    public static final String mavenPom = "pom.xml";

    public static final String internalMavenPom = "META-INF/maven/" + VersionCheck.packageName.replace('.', '/') + "/" + VersionCheck.shortJarName
            + "/" + VersionCheck.mavenPom;

    static {
        String v = "?";
        try {
            InputStream resource = new File(VersionCheck.mavenPom).exists() ? new FileInputStream(VersionCheck.mavenPom) : VersionCheck.class
                    .getClassLoader().getResourceAsStream(VersionCheck.internalMavenPom);
            v = org.w3c.dom.Text.class.cast(XmlService.xpath(resource, "/project/version/text()").get(0)).getTextContent();
        } catch (Exception ex) {
            ExceptionAndLogHandler.log(ex);
        }
        version = v;
    }

    public static void check(Config cfg) {
        try {
            if (cfg.getParameterValue("console") == null) {
                return;
            }
            String mavenProjectBase = VersionCheck.mavenBase + "/maven-metadata.xml";
            byte[] data = DownloadingService.downloadURL(new URL(mavenProjectBase));
            List<String> versions = new ArrayList<String>();
            for (Node node : XmlService.xpath(new ByteArrayInputStream(data), "/metadata/versioning/versions/version")) {
                versions.add(node.getTextContent());
            }
            Collections.sort(versions);
            String latestversion = versions.get(versions.size() - 1);
            if ((VersionCheck.version.compareTo(latestversion) < 0)) {
                if (IOMethods.showConfirmation(cfg, cfg.getShortTitle(),
                        "A newer version is available: download now?\nThe program will exit afterwards.")) {
                    String fname = VersionCheck.shortJarName + "-" + latestversion + ".jar";
                    String dl = VersionCheck.mavenBase + "/" + latestversion + "/" + fname;
                    File newjar = new File(fname);
                    DownloadingService.downloadURL(new URL(dl), newjar);
                    IOMethods.getCurrentJar().deleteOnExit();
                    System.exit(0);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
