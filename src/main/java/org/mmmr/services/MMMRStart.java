package org.mmmr.services;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import javax.swing.JLabel;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.varia.LevelRangeFilter;

/**
 * @author Jurgen
 */
public class MMMRStart {
    private static void adjustLogging(Config cfg) throws IOException {
	String levelstr = cfg.getProperty("logging.level", "?");
	if (!"?".equals(levelstr)) {
	    Level level;
	    if ("TRACE".equals(levelstr)) {
		level = Level.TRACE;
	    } else if ("DEBUG".equals(levelstr)) {
		level = Level.DEBUG;
	    } else if ("WARN".equals(levelstr)) {
		level = Level.WARN;
	    } else if ("ERROR".equals(levelstr)) {
		level = Level.ERROR;
	    } else if ("FATAL".equals(levelstr)) {
		level = Level.FATAL;
	    } else {
		level = Level.OFF;
	    }
	    org.apache.log4j.Logger.getRootLogger().setLevel(level);
	    Enumeration<?> allAppenders = org.apache.log4j.Logger.getRootLogger().getAllAppenders();
	    while (allAppenders.hasMoreElements()) {
		Appender appender = Appender.class.cast(allAppenders.nextElement());
		LevelRangeFilter filter = LevelRangeFilter.class.cast(appender.getFilter());
		filter.setLevelMin(level);
	    }
	}
    }

    public static void main(String[] args) {
	try {
	    FancySwing.lookAndFeel();
	    Config cfg = new Config(args, new File("DUMMY").getAbsoluteFile().getParentFile());
	    MMMRStart.adjustLogging(cfg);
	    MMMRStart.prepareFont(cfg);
	    StatusWindow statusWindow = new StatusWindow(cfg);
	    statusWindow.setVisible(true);
	    DynamicLoading.init(statusWindow.getLibstatus(), cfg);
	    MMMRI starter = MMMRI.class.cast(Class.forName("org.mmmr.services.MMMR").newInstance());
	    starter.setCfg(cfg);
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
		URL dejavu = new URL("http://www.mirrorservice.org/sites/download.sourceforge.net/pub/sourceforge/d/project/de/dejavu/dejavu/2.33/dejavu-fonts-ttf-2.33.zip");
		File file = new File(cfg.getTmp(), "dejavu-fonts-ttf-2.33.zip");
		IOMethods.downloadURL(dejavu, file);
		IOMethods.unzip(file, cfg.getCfg());
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
