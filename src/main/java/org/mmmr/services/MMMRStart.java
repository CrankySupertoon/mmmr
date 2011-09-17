package org.mmmr.services;

import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.swing.JLabel;
import javax.swing.ToolTipManager;

import org.mmmr.services.swing.FancySwing;
import org.mmmr.services.swing.StatusWindow;

/**
 * @author Jurgen
 */
public class MMMRStart {

    private static void checkBat(Config cfg) throws IOException {
        File bnc = new File("start minecraft no-console.bat");
        if (!bnc.exists()) {
            InputStream in = MMMRStart.class.getResourceAsStream("bat/start minecraft no-console.bat");
            byte[] buffer = new byte[in.available()];
            in.read(buffer);
            in.close();
            FileOutputStream bnco = new FileOutputStream(bnc);
            bnco.write(buffer);
            bnco.close();
        }
        File nc = new File("start minecraft console.bat");
        if (!nc.exists()) {
            InputStream in = MMMRStart.class.getResourceAsStream("bat/start minecraft console.bat");
            byte[] buffer = new byte[in.available()];
            in.read(buffer);
            in.close();
            FileOutputStream bnco = new FileOutputStream(nc);
            bnco.write(buffer);
            bnco.close();
        }
        if ((cfg.getParameterValue("console") == null) && (cfg.getParameterValue("dev") == null)) {
            IOMethods.showInformation(cfg, "Running Minecraft Mod Manager Reloaded",
                    "Start with Batch File 'start minecraft console' or 'start minecraft no-console'");
            throw new IllegalArgumentException();
        }
    }

    public static void main(String[] args) {
        try {
            FancySwing.lookAndFeel();
            Config cfg = new Config(args, new File("DUMMY").getAbsoluteFile().getParentFile());
            MMMRStart.checkBat(cfg);
            ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
            toolTipManager.setInitialDelay(100);
            toolTipManager.setReshowDelay(100);
            toolTipManager.setDismissDelay(7500);
            MMMRStart.prepareFont(cfg);
            StatusWindow statusWindow = new StatusWindow(cfg);
            statusWindow.setVisible(true);
            DynamicLoading.init(statusWindow.getLibstatus(), cfg);
            MMMRI starter = MMMRI.class.cast(Class.forName("org.mmmr.services.MMMR").newInstance());
            starter.setCfg(cfg);
            ExceptionAndLogHandler.adjustLogging(cfg);
            starter.setStatusWindow(statusWindow);
            starter.start(args);
        } catch (Exception ex) {
            ExceptionAndLogHandler.log(ex);
        }
    }

    /**
     * @see http://www.mirrorservice.org/sites/download.sourceforge.net/pub/sourceforge/d/project/de/dejavu/dejavu/2.33/dejavu-fonts-2.33.tar.bz2
     */
    private static void prepareFont(Config cfg) {
        Font font = null;
        try {
            File fontfont = new File(cfg.getCfg(), "dejavu-fonts-ttf-2.33/ttf/DejaVuSans.ttf");
            if (!fontfont.exists()) {
                URL dejavu = new URL(
                        "http://www.mirrorservice.org/sites/download.sourceforge.net/pub/sourceforge/d/project/de/dejavu/dejavu/2.33/dejavu-fonts-ttf-2.33.zip");
                File file = new File(cfg.getTmp(), "dejavu-fonts-ttf-2.33.zip");
                DownloadingService.downloadURL(dejavu, file);
                ArchiveService.extract(file, cfg.getCfg());
            }
            font = Font.createFont(Font.TRUETYPE_FONT, fontfont);
        } catch (Exception ex) {
            ExceptionAndLogHandler.log(ex);
            font = new JLabel().getFont();
        }
        cfg.setFont(font.deriveFont(182));
        Font font18 = font.deriveFont(18f);
        cfg.setFont18(font18);
    }
}
