package org.mmmr.services;

import java.awt.Font;
import java.io.File;
import java.net.URL;

import javax.swing.JLabel;

/**
 * @author Jurgen
 */
public class NiceFont {
    /**
     * @see http://www.mirrorservice.org/sites/download.sourceforge.net/pub/sourceforge/d/project/de/dejavu/dejavu/2.33/dejavu-fonts-2.33.tar.bz2
     */
    public static void prepareFont(Config cfg) {
        Font font = null;
        Font fontNarrow = null;
        try {
            File fontfile = new File(cfg.getCfg(), "dejavu-fonts-ttf-2.33/ttf/DejaVuSans.ttf");
            File fontfile2 = new File(cfg.getCfg(), "dejavu-fonts-ttf-2.33/ttf/DejaVuSansCondensed.ttf");
            if (!fontfile.exists()) {
                URL dejavu = new URL(
                        "http://www.mirrorservice.org/sites/download.sourceforge.net/pub/sourceforge/d/project/de/dejavu/dejavu/2.33/dejavu-fonts-ttf-2.33.zip");
                File file = new File(cfg.getTmp(), "dejavu-fonts-ttf-2.33.zip");
                DownloadingService.downloadURL(dejavu, file);
                ArchiveService.extract(file, cfg.getCfg());
            }
            font = Font.createFont(Font.TRUETYPE_FONT, fontfile);
            fontNarrow = Font.createFont(Font.TRUETYPE_FONT, fontfile2);
        } catch (Exception ex) {
            ExceptionAndLogHandler.log(ex);
            font = new JLabel().getFont();
            fontNarrow = font;
        }
        cfg.setFont(font.deriveFont(18f));
        Font font18 = font.deriveFont(18f);
        cfg.setFont18(font18);
        fontNarrow = fontNarrow.deriveFont(18f);
        cfg.setFontNarrow(fontNarrow);
    }
}
