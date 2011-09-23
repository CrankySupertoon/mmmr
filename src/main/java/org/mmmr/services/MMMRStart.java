package org.mmmr.services;

import java.io.File;

import javax.swing.ToolTipManager;

import org.mmmr.services.swing.StatusWindow;
import org.mmmr.services.swing.common.FancySwing;

/**
 * @author Jurgen
 */
public class MMMRStart {

    public static void main(String[] args) {
        try {
            ExceptionAndLogHandler.log(IOMethods.getCurrentJar().getAbsolutePath());
            FancySwing.lookAndFeel();
            Config cfg = new Config(args, new File("DUMMY").getAbsoluteFile().getParentFile());
            BatCheck.check(cfg);
            VersionCheck.check(cfg);
            System.getProperties().list(System.out);
            ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
            toolTipManager.setInitialDelay(100);
            toolTipManager.setReshowDelay(100);
            toolTipManager.setDismissDelay(7500);
            NiceFont.prepareFont(cfg);
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

}
