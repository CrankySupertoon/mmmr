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
        Font fontTitle = null;
        try {
            File fontfile1 = new File(cfg.getCfg(), "dejavu-fonts-ttf-2.33/ttf/DejaVuSans.ttf"); //$NON-NLS-1$
            File fontfile2 = new File(cfg.getCfg(), "dejavu-fonts-ttf-2.33/ttf/DejaVuSansCondensed.ttf"); //$NON-NLS-1$
            File fontfile3 = new File(cfg.getCfg(), "dejavu-fonts-ttf-2.33/ttf/DejaVuSansCondensed-BoldOblique.ttf"); //$NON-NLS-1$
            if (!fontfile1.exists()) {
                URL dejavu = new URL(
                        "http://www.mirrorservice.org/sites/download.sourceforge.net/pub/sourceforge/d/project/de/dejavu/dejavu/2.33/dejavu-fonts-ttf-2.33.zip"); //$NON-NLS-1$
                File file = new File(cfg.getTmp(), "dejavu-fonts-ttf-2.33.zip"); //$NON-NLS-1$
                DownloadingService.downloadURL(dejavu, file);
                ArchiveService.extract(file, cfg.getCfg());
            }
            font = Font.createFont(Font.TRUETYPE_FONT, fontfile1);
            fontNarrow = Font.createFont(Font.TRUETYPE_FONT, fontfile2);
            fontTitle = Font.createFont(Font.TRUETYPE_FONT, fontfile3);
        } catch (Exception ex) {
            ExceptionAndLogHandler.log(ex);
            font = new JLabel().getFont();
            fontNarrow = font;
            fontTitle = font;
        }
        cfg.setFontLarge(font.deriveFont(18f));
        cfg.setFontSmall(fontNarrow.deriveFont(10f));
        cfg.setFontTable(font.deriveFont(10f));
        cfg.setFontTitle(fontTitle.deriveFont(22f));
    }
}
