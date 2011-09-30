package org.mmmr.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author Jurgen
 */
public class BackupService {
    public static void backup(Config cfg) throws IOException {
        List<String> paths = new ArrayList<String>();
        File saves = new File(cfg.getMcBaseFolder(), "saves");
        for (File child : UtilityMethods.listRecursive(saves)) {
            if (child.isDirectory()) {
                continue;
            }
            paths.add(UtilityMethods.relativePath(cfg.getMcBaseFolder(), child));
        }
        File stats = new File(cfg.getMcBaseFolder(), "stats");
        for (File child : UtilityMethods.listRecursive(stats)) {
            if (child.isDirectory()) {
                continue;
            }
            paths.add(UtilityMethods.relativePath(cfg.getMcBaseFolder(), child));
        }
        paths.add("options.txt");
        String dateTimeString = javax.xml.bind.DatatypeConverter.printDateTime(Calendar.getInstance());
        File archive = new File(cfg.getBackup(), "bacukp " + dateTimeString.replace(':', '_') + ".zip");
        ArchiveService.compress(cfg.getMcBaseFolder(), paths, archive);
    }

    public static void restore(Config cfg, File backup) throws IOException {
        ArchiveService.extract(backup, cfg.getMcBaseFolder());
    }
}
