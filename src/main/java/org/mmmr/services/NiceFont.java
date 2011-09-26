package org.mmmr.services;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
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
    public static void hqFontFile(boolean debug, Config cfg, int scale) {
        try {
            int wh = 128 * scale;
            int sqrt = (int) Math.sqrt(256);
            int d = wh / sqrt;
            BufferedImage bi = new BufferedImage(wh, wh, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = bi.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
            g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Font f = cfg.getFontMono();
            int size = 8 * scale;
            f = f.deriveFont((float) size);
            FontMetrics fm = g2d.getFontMetrics(f);
            while (fm.getHeight() != d) {
                if (fm.getHeight() > d) {
                    size--;
                } else {
                    size++;
                }
                f = f.deriveFont((float) size);
                fm = g2d.getFontMetrics(f);
            }
            ExceptionAndLogHandler.log("size=" + size);//$NON-NLS-1$
            g2d.setFont(f);
            fm = g2d.getFontMetrics();
            g2d.setColor(Color.white);
            if (debug) {
                g2d.setColor(Color.black);
                for (int i = 1; i < sqrt; i++) {
                    g2d.drawLine(i * d, 0, i * d, wh);
                }
                for (int i = 1; i < sqrt; i++) {
                    g2d.drawLine(0, i * d, wh, i * d);
                }
            }
            int maxw = 0;
            for (int w : fm.getWidths()) {
                maxw = Math.max(maxw, w);
            }
            int dy = (d - maxw) / 2;
            for (int i = 1; i <= 256; i++) {
                String s = new String(new byte[] { (byte) i }, "cp850");//$NON-NLS-1$
                g2d.drawChars(s.toCharArray(), 0, 1, (i % sqrt) * d + dy, (i / sqrt) * d + fm.getAscent());
            }
            ImageIO.write(bi, "png", new File(cfg.getMcJar(), "font/default.png"));//$NON-NLS-1$//$NON-NLS-2$
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void hqFontFile(Config cfg) {
        NiceFont.hqFontFile(false, cfg, 8);
    }

    /**
     * @see http://www.mirrorservice.org/sites/download.sourceforge.net/pub/sourceforge/d/project/de/dejavu/dejavu/2.33/dejavu-fonts-2.33.tar.bz2
     */
    public static void prepareFont(Config cfg) {
        Font font = null;
        Font fontNarrow = null;
        Font fontTitle = null;
        Font fontMono = null;
        try {
            File fontfile1 = new File(cfg.getCfg(), "dejavu-fonts-ttf-2.33/ttf/DejaVuSans.ttf"); //$NON-NLS-1$
            File fontfile2 = new File(cfg.getCfg(), "dejavu-fonts-ttf-2.33/ttf/DejaVuSansCondensed.ttf"); //$NON-NLS-1$
            File fontfile3 = new File(cfg.getCfg(), "dejavu-fonts-ttf-2.33/ttf/DejaVuSansCondensed-BoldOblique.ttf"); //$NON-NLS-1$
            File fontfile4 = new File(cfg.getCfg(), "dejavu-fonts-ttf-2.33/ttf/DejaVuSansMono.ttf"); //$NON-NLS-1$
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
            fontMono = Font.createFont(Font.TRUETYPE_FONT, fontfile4);
        } catch (Exception ex) {
            ExceptionAndLogHandler.log(ex);
            font = new JLabel().getFont();
            fontNarrow = font;
            fontTitle = font;
            fontMono = font;
        }
        cfg.setFontLarge(font.deriveFont(18f));
        cfg.setFontSmall(fontNarrow.deriveFont(10f));
        cfg.setFontTable(font.deriveFont(10f));
        cfg.setFontTitle(fontTitle.deriveFont(22f));
        cfg.setFontMono(fontMono.deriveFont(12f));
    }
}
