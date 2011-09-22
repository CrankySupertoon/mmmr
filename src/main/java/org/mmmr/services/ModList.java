package org.mmmr.services;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.OutputStreamWriter;

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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * update mod configurations
     */
    public static void update() {

    }
}
