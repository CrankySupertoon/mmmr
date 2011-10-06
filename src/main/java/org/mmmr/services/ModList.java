package org.mmmr.services;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mmmr.Mod;

/**
 * @author Jurgen
 */
public class ModList {
    /**
     * write list of mod configurations available in subversion so users can download them when they are updated/added
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            File mods = new File("data/mods/"); //$NON-NLS-1$
            File[] modxmls = mods.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (!name.endsWith(".xml")) { //$NON-NLS-1$
                        return false;
                    }
                    if (name.toLowerCase().contains("yogbox")) { //$NON-NLS-1$
                        return false;
                    }
                    return true;
                }
            });
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data/mods/modlist.txt"))); //$NON-NLS-1$
            XmlService xmlService = new XmlService(new Config());
            for (File mod : modxmls) {
                System.out.println(mod.getName());
                try {
                    FileInputStream in = new FileInputStream(mod);
                    xmlService.load(in, Mod.class);
                    in.close();
                    out.write(mod.lastModified() + "::" + mod.getName() + "\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
                } catch (Exception ex) {
                    ex.printStackTrace(System.out);
                }
            }
            out.flush();
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * update mod configurations
     */
    public static void update(Config cfg) throws MalformedURLException, IOException, URISyntaxException {
        Map<String, Long> existing = new HashMap<String, Long>();
        File[] modxmls = cfg.getMods().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (!name.endsWith(".xml")) { //$NON-NLS-1$
                    return false;
                }
                if (name.toLowerCase().contains("yogbox")) { //$NON-NLS-1$
                    return false;
                }
                return true;
            }
        });
        if (modxmls != null) {
            for (File mod : modxmls) {
                existing.put(mod.getName(), mod.lastModified());
            }
        }
        URL base = new URL(cfg.getMmmrSvnOnGoogleCode());
        for (String record : new String(DownloadingService.downloadURL(new URL(cfg.getMmmrSvnOnGoogleCode() + "/data/mods/modlist.txt"))) //$NON-NLS-1$
                .split("\r\n")) { //$NON-NLS-1$
            if (StringUtils.isBlank(record)) {
                continue;
            }
            String[] d = record.split("::"); //$NON-NLS-1$
            String xmlname = d[1];
            Long lastmod = Long.parseLong(d[0]);
            Long lastmodlocal = existing.get(xmlname);
            if ((lastmodlocal == null) || (lastmodlocal < lastmod)) {
                // does not exists or newer on server => download
                URI uri = new URI(base.getProtocol(), base.getHost(), base.getPath() + "/data/mods/" + xmlname, null); //$NON-NLS-1$
                String url = uri.toURL().toString();
                File target = new File(cfg.getMods(), xmlname);
                try {
                    DownloadingService.downloadURL(new URL(url), target);
                } catch (IOException ex) {
                    target.delete();
                    throw ex;
                }
            }
        }
    }
}
