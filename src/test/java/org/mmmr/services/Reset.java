package org.mmmr.services;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Scanner;

import org.mmmr.services.impl.ArchiveService7Zip;

public class Reset {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("reset client? ");
            String userInput = scanner.next();
            boolean resetClient = userInput.startsWith("1") || userInput.startsWith("y");
            System.out.print("reset server? ");
            userInput = scanner.next();
            boolean resetServer = userInput.startsWith("1") || userInput.startsWith("y");
            Config cfg = new Config();
            ArchiveService7Zip z7 = new ArchiveService7Zip();
            if (resetClient) {
                UtilityMethods.deleteDirectory(cfg.getBackup());
                UtilityMethods.deleteDirectory(cfg.getCfg());
                UtilityMethods.deleteDirectory(cfg.getDbdir());
                UtilityMethods.deleteDirectory(cfg.getMcBaseFolder());
                UtilityMethods.deleteDirectory(cfg.getLogs());
                File clientBackup = new File(cfg.getClientFolder(), ".minecraft.rar");
                if (clientBackup.exists()) {
                    z7.extract(clientBackup, cfg.getClientFolder());
                }
            }
            if (resetServer) {
                for (File f : cfg.getServerFolder().listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return !"minecraft_server.jar.backup".equals(name);
                    }
                })) {
                    if (f.isDirectory()) {
                        UtilityMethods.deleteDirectory(f);
                    } else {
                        f.delete();
                    }
                }
                File serverBackup = new File(cfg.getServerFolder(), "minecraft_server.jar.backup");
                if (serverBackup.exists()) {
                    UtilityMethods.copyFile(serverBackup, new File(cfg.getServerFolder(), "minecraft_server.jar"));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
