package org.mmmr.services;

import static org.mmmr.services.IOMethods.downloadURL;
import static org.mmmr.services.IOMethods.unzip;

import java.awt.Font;
import java.io.File;
import java.net.URL;

import javax.swing.JLabel;

/**
 * @author Jurgen
 */
public class MMMRStart {
    public static void main(String[] args) {
	try {
	    FancySwing.lookAndFeel();
	    Config cfg = new Config(args, new File("DUMMY").getAbsoluteFile().getParentFile());
	    prepareFont(cfg);
	    StatusWindow statusWindow = new StatusWindow(cfg);
	    statusWindow.setVisible(true);
	    DynamicLoading.init(statusWindow.getLibstatus(), cfg);
	    StartMe starter = StartMe.class.cast(Class.forName("org.mmmr.services.MMMR").newInstance());
	    starter.setCfg(cfg);
	    starter.setStatusWindow(statusWindow);
	    starter.start(args);
	} catch (Exception e) {
	    e.printStackTrace();
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
		URL dejavu = new URL("http://www.mirrorservice.org/sites/download.sourceforge.net/pub/sourceforge/d/project/de/dejavu/dejavu/2.33/dejavu-fonts-ttf-2.33.zip");
		File file = new File(cfg.getTmp(), "dejavu-fonts-ttf-2.33.zip");
		downloadURL(dejavu, file);
		unzip(file, cfg.getCfg());
	    }
	    font = Font.createFont(Font.TRUETYPE_FONT, fontfont);
	} catch (Exception e) {
	    e.printStackTrace();
	    font = new JLabel().getFont();
	}
	cfg.setFont(font);
	Font font18 = font.deriveFont(18f);
	cfg.setFont18(font18);
    }
}
