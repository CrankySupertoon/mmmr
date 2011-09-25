package org.mmmr.services;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JLabel;

/**
 * @author Jurgen
 */
public class NiceFont {
    public static void main(String[] args) {
        try {
            // 128x128
            int dd = 8;
            int wh = 128 * dd;
            BufferedImage bi = new BufferedImage(wh, wh, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = bi.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setFont(Font.createFont(Font.TRUETYPE_FONT, new File("data/cfg/dejavu-fonts-ttf-2.33/ttf/DejaVuSansMono.ttf")).deriveFont(8f * dd));
            g2d.setColor(Color.white);
            int d = wh / 16;
            int sqrt = (int) Math.sqrt(256);
            for (int i = 1; i <= 256; i++) {
                String s = new String(new byte[] { (byte) i }, "cp850");
                g2d.drawChars(s.toCharArray(), 0, 1, (i % sqrt) * d, -15 + ((1 + (i / sqrt)) * d));
            }
            ImageIO.write(bi, "png", new File(".minecraft/bin/minecraft.jar/font/default.png"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

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
