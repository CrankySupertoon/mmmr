package org.mmmr.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Jurgen
 */
public class BatCheck {
    public static void check(Config cfg) throws IOException {
        // when running in development mode => do nothing
        if (UtilityMethods.isDevelopmentMode()) {
            return;
        }
        // see if bat files exist and create if not
        File console = new File("start MMMR console.bat"); //$NON-NLS-1$
        File noconsole = new File("start MMMR no-console.bat"); //$NON-NLS-1$
        String batstring = !(console.exists() || noconsole.exists()) ? new String(UtilityMethods.read(MMMRStart.class.getClassLoader()
                .getResourceAsStream("bat/start MMMR.bat"))) : null; //$NON-NLS-1$
        if (!noconsole.exists()) {
            // w is put behind java so if starts no console
            byte[] bytes = batstring.replaceAll("\\Q{CONSOLE}\\E", "w").replaceAll("\\Q{JAR}\\E", UtilityMethods.getCurrentJar().getName()).getBytes(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            FileOutputStream bnco = new FileOutputStream(noconsole);
            bnco.write(bytes);
            bnco.close();
        }
        if (!console.exists()) {
            byte[] bytes = batstring.replaceAll("\\Q{CONSOLE}\\E", "").replaceAll("\\Q{JAR}\\E", UtilityMethods.getCurrentJar().getName()).getBytes(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            FileOutputStream bnco = new FileOutputStream(console);
            bnco.write(bytes);
            bnco.close();
        }
        // when not running from bat file: give error (will exit afterwards)
        if (cfg.getParameterValue("console") == null) { //$NON-NLS-1$
            UtilityMethods.showInformation(cfg, Messages.getString("BatCheck.runningMMMR"), //$NON-NLS-1$
                    Messages.getString("BatCheck.start_bat")); //$NON-NLS-1$
            System.exit(-1);
        }
    }

    public static void force(File oldjar, File newjar) throws IOException {
        File console = new File("start MMMR console.bat"); //$NON-NLS-1$
        File noconsole = new File("start MMMR no-console.bat"); //$NON-NLS-1$
        String batstring = "@if exist " + oldjar.getName() + " del " + oldjar.getName() + "\r\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                + new String(UtilityMethods.read(MMMRStart.class.getClassLoader().getResourceAsStream("bat/start MMMR.bat"))); //$NON-NLS-1$
        {
            // w is put behind java so if starts no console
            byte[] bytes = batstring.replaceAll("\\Q{CONSOLE}\\E", "w").replaceAll("\\Q{JAR}\\E", newjar.getName()).getBytes(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            FileOutputStream bnco = new FileOutputStream(noconsole);
            bnco.write(bytes);
            bnco.close();
        }
        {
            byte[] bytes = batstring.replaceAll("\\Q{CONSOLE}\\E", "").replaceAll("\\Q{JAR}\\E", newjar.getName()).getBytes(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            FileOutputStream bnco = new FileOutputStream(console);
            bnco.write(bytes);
            bnco.close();
        }
    }
}
