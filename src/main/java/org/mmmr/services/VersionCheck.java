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

    public static final String shortJarName = "mmmr"; //$NON-NLS-1$

    public static final String packageName = "org.mmmr"; //$NON-NLS-1$

    public static final String mavenBase = "http://mmmr.googlecode.com/svn/maven2/" + VersionCheck.packageName.replace('.', '/') + "/" //$NON-NLS-1$ //$NON-NLS-2$
            + VersionCheck.shortJarName;

    public static final String mavenPom = "pom.xml"; //$NON-NLS-1$

    public static final String internalMavenPom = "META-INF/maven/" + VersionCheck.packageName.replace('.', '/') + "/" + VersionCheck.shortJarName //$NON-NLS-1$ //$NON-NLS-2$
            + "/" + VersionCheck.mavenPom; //$NON-NLS-1$

    static {
        String v = "?"; //$NON-NLS-1$
        try {
            InputStream resource = new File(VersionCheck.mavenPom).exists() ? new FileInputStream(VersionCheck.mavenPom) : VersionCheck.class
                    .getClassLoader().getResourceAsStream(VersionCheck.internalMavenPom);
            v = org.w3c.dom.Text.class.cast(XmlService.xpath(resource, "/project/version/text()").get(0)).getTextContent(); //$NON-NLS-1$
        } catch (Exception ex) {
            ExceptionAndLogHandler.log(ex);
        }
        version = v;
    }

    public static void check(Config cfg) {
        try {
            if (cfg.getParameterValue("console") == null) { //$NON-NLS-1$
                return;
            }
            String mavenProjectBase = VersionCheck.mavenBase + "/maven-metadata.xml"; //$NON-NLS-1$
            byte[] data = DownloadingService.downloadURL(new URL(mavenProjectBase));
            List<String> versions = new ArrayList<String>();
            for (Node node : XmlService.xpath(new ByteArrayInputStream(data), "/metadata/versioning/versions/version")) { //$NON-NLS-1$
                versions.add(node.getTextContent());
            }
            Collections.sort(versions);
            String latestversion = versions.get(versions.size() - 1);
            if ((VersionCheck.version.compareTo(latestversion) < 0)) {
                String text = String.format(Messages.getString("VersionCheck.newer_version"), latestversion, VersionCheck.version);//$NON-NLS-1$
                if (IOMethods.showConfirmation(cfg, cfg.getShortTitle(), text)) {
                    String fname = VersionCheck.shortJarName + "-" + latestversion + ".jar"; //$NON-NLS-1$ //$NON-NLS-2$
                    String dl = VersionCheck.mavenBase + "/" + latestversion + "/" + fname; //$NON-NLS-1$ //$NON-NLS-2$
                    File newjar = new File(fname);
                    DownloadingService.downloadURL(new URL(dl), newjar);
                    BatCheck.force(IOMethods.getCurrentJar(), newjar);
                    System.exit(0);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
