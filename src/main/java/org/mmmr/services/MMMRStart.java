package org.mmmr.services;

import java.awt.Font;
import java.io.File;
import java.net.URL;

import javax.swing.JLabel;
import javax.swing.ToolTipManager;

import org.mmmr.services.swing.FancySwing;
import org.mmmr.services.swing.StatusWindow;

/**
 * @author Jurgen
 */
public class MMMRStart {

    public static void main(String[] args) {
        try {
            FancySwing.lookAndFeel();
            ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
            toolTipManager.setInitialDelay(100);
            toolTipManager.setReshowDelay(100);
            toolTipManager.setDismissDelay(5000);
            Config cfg = new Config(args, new File("DUMMY").getAbsoluteFile().getParentFile());
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
