package org.mmmr.services;

import java.io.File;
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
                UtilityMethods.delete(cfg.getBackup());
                UtilityMethods.delete(cfg.getCfg());
                UtilityMethods.delete(cfg.getDbdir());
                UtilityMethods.delete(cfg.getMcBaseFolder());
                UtilityMethods.delete(cfg.getLogs());
                File clientBackup = new File(cfg.getClientFolder(), ".minecraft.rar");
                if (clientBackup.exists()) {
                    z7.extract(clientBackup, cfg.getClientFolder());
                }
            }
            if (resetServer) {
                for (File f : cfg.getServerFolder().listFiles()) {
                    UtilityMethods.delete(f);
                }
                if (cfg.getBackupServerJar().exists()) {
                    UtilityMethods.copyFile(cfg.getBackupServerJar(), new File(cfg.getServerFolder(), Config.MINECRAFT_SERVER_JAR));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
