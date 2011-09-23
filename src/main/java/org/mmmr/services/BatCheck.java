package org.mmmr.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Jurgen
 */
public class BatCheck {
    public static void check(Config cfg) throws IOException {
        // when running in eclipse with startup parameter "dev" or "-dev" => do nothing
        if (cfg.getParameterValue("dev") != null) {
            return;
        }
        // see if bat files exist and create if not
        File console = new File("start MMMR console.bat");
        File noconsole = new File("start MMMR no-console.bat");
        String batstring = !(console.exists() || noconsole.exists()) ? new String(IOMethods.read(MMMRStart.class.getClassLoader()
                .getResourceAsStream("bat/start MMMR.bat"))) : null;
        if (!noconsole.exists()) {
            // w is put behind java so if starts no console
            byte[] bytes = batstring.replaceAll("\\Q{CONSOLE}\\E", "w").replaceAll("\\Q{JAR}\\E", IOMethods.getCurrentJar().getName()).getBytes();
            FileOutputStream bnco = new FileOutputStream(noconsole);
            bnco.write(bytes);
            bnco.close();
        }
        if (!console.exists()) {
            byte[] bytes = batstring.replaceAll("\\Q{CONSOLE}\\E", "").replaceAll("\\Q{JAR}\\E", IOMethods.getCurrentJar().getName()).getBytes();
            FileOutputStream bnco = new FileOutputStream(console);
            bnco.write(bytes);
            bnco.close();
        }
        // when not running from bat file: give error (will exit afterwards)
        if (cfg.getParameterValue("console") == null) {
            IOMethods.showInformation(cfg, "Running Minecraft Mod Manager Reloaded",
                    "Start with Batch File 'start minecraft console' or 'start minecraft no-console'");
            System.exit(-1);
        }
    }

    public static void force(File oldjar, File newjar) throws IOException {
        File console = new File("start MMMR console.bat");
        File noconsole = new File("start MMMR no-console.bat");
        String batstring = "@if exist " + oldjar.getName() + " del " + oldjar.getName() + "\r\n"
                + new String(IOMethods.read(MMMRStart.class.getClassLoader().getResourceAsStream("bat/start MMMR.bat")));
        {
            // w is put behind java so if starts no console
            byte[] bytes = batstring.replaceAll("\\Q{CONSOLE}\\E", "w").replaceAll("\\Q{JAR}\\E", newjar.getName()).getBytes();
            FileOutputStream bnco = new FileOutputStream(noconsole);
            bnco.write(bytes);
            bnco.close();
        }
        {
            byte[] bytes = batstring.replaceAll("\\Q{CONSOLE}\\E", "").replaceAll("\\Q{JAR}\\E", newjar.getName()).getBytes();
            FileOutputStream bnco = new FileOutputStream(console);
            bnco.write(bytes);
            bnco.close();
        }
    }
}
