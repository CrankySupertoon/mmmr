package org.mmmr.services;

import java.io.BufferedWriter;
import java.io.File;
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
            File mods = new File("data/mods");
            File[] modxmls = mods.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (!name.endsWith(".xml")) {
                        return false;
                    }
                    if (name.toLowerCase().contains("yogbox")) {
                        return false;
                    }
                    return true;
                }
            });
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("src/main/modlist.txt")));
            for (File mod : modxmls) {
                out.write(mod.lastModified() + "::" + mod.getName() + "\r\n");
            }
            out.flush();
            out.close();

            ModList.update(new Config(args, new File("DUMMY").getAbsoluteFile().getParentFile()));
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
                if (!name.endsWith(".xml")) {
                    return false;
                }
                if (name.toLowerCase().contains("yogbox")) {
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
        for (String record : new String(DownloadingService.downloadURL(new URL(cfg.getMmmrSvnOnGoogleCode() + "/src/main/modlist.txt")))
                .split("\r\n")) {
            String[] d = record.split("::");
            String xmlname = d[1];
            Long lastmod = Long.parseLong(d[0]);
            Long lastmodlocal = existing.get(xmlname);
            if ((lastmodlocal == null) || (lastmodlocal < lastmod)) {
                // does not exists or newer on server => download
                URI uri = new URI(base.getProtocol(), base.getHost(), base.getPath() + "/data/mods/" + xmlname, null);
                String url = uri.toURL().toString();
                DownloadingService.downloadURL(new URL(url), new File(cfg.getMods(), xmlname));
            }
        }
    }
}