package org.mmmr.services;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JLabel;

/**
 * @author Jurgen
 */
public class NiceFont {
    @SuppressWarnings("null")
    public static BufferedImage hqFontFile(GraphicsConfiguration gc, boolean debug, Config cfg, int scale, Font f) {
        try {
            int charactercount = 256; // 256 = number of characters: 16 rows x 16 columns
            int sqrt = (int) Math.sqrt(charactercount); // 16
            int wh = 128 * scale; // scale defaults to 8: 8*128 = 1024 pixels
            int d = wh / sqrt; // pixels per row, pixels per column; cell is square

            BufferedImage bi = gc.createCompatibleImage(wh, wh, Transparency.BITMASK);
            Graphics2D g2d = bi.createGraphics();
            // g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            // g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            // g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
            // g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            // g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            // g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
            int size = 8 * scale; // start with 8 point font x scale
            f = f.deriveFont((float) size);
            FontMetrics fm = g2d.getFontMetrics(f);
            Integer direction = null;
            while (fm.getHeight() != d) {
                if (fm.getHeight() > d) {
                    if (direction == null) {
                        direction = -1;
                    } else if (direction == +1) {
                        break;
                    }
                    size--;
                } else {
                    if (direction == null) {
                        direction = +1;
                    } else if (direction == -1) {
                        break;
                    }
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
            int maxw = 0; // maxw will be the maximum width of characters for this font: used to center font horizontally
            for (int w : fm.getWidths()) {
                maxw = Math.max(maxw, w);
            }
            int dx = (d - maxw) / 2; // center font horizontally, shift dx pixels
            int dy = fm.getAscent();// center font vertically, shift dy pixels
            for (int i = 1; i <= charactercount; i++) {
                String s = new String(new byte[] { (byte) i }, "cp850"); //codepage 850 (VGA default?) //$NON-NLS-1$
                g2d.drawChars(s.toCharArray(), 0, 1, ((i % sqrt) * d) + dx, ((i / sqrt) * d) + dy);
            }
            g2d.dispose();
            if (debug) {
                return bi;
            }
            ImageIO.write(bi, "png", new File(cfg.getMcJar(), "font/default.png"));//$NON-NLS-1$//$NON-NLS-2$
            return bi;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * @see http://www.mirrorservice.org/sites/download.sourceforge.net/pub/sourceforge/d/project/de/dejavu/dejavu/2.33/dejavu-fonts-2.33.tar.bz2
     */
    public static Config prepareFont(Config cfg) {
        Font font = null;
        Font fontNarrow = null;
        Font fontTitle = null;
        Font fontMono = null;
        Font fontMonoBold = null;
        String path = "dejavu-fonts-ttf-2.33/ttf/"; //$NON-NLS-1$
        try {
            File fontfile1 = new File(cfg.getLibs(), path + "DejaVuSans.ttf"); //$NON-NLS-1$
            File fontfile2 = new File(cfg.getLibs(), path + "DejaVuSansCondensed.ttf"); //$NON-NLS-1$
            File fontfile3 = new File(cfg.getLibs(), path + "DejaVuSansCondensed-BoldOblique.ttf"); //$NON-NLS-1$
            File fontfile4 = new File(cfg.getLibs(), path + "DejaVuSansMono.ttf"); //$NON-NLS-1$
            File fontfile5 = new File(cfg.getLibs(), path + "DejaVuSansMono-Bold.ttf"); //$NON-NLS-1$
            if (!fontfile1.exists()) {
                URL dejavu = new URL(
                        "http://www.mirrorservice.org/sites/download.sourceforge.net/pub/sourceforge/d/project/de/dejavu/dejavu/2.33/dejavu-fonts-ttf-2.33.zip"); //$NON-NLS-1$
                File file = new File(cfg.getLibs(), "dejavu-fonts-ttf-2.33.zip"); //$NON-NLS-1$
                DownloadingService.downloadURL(dejavu, file);
                ArchiveService.extract(file, cfg.getLibs());
            }
            font = Font.createFont(Font.TRUETYPE_FONT, fontfile1);
            fontNarrow = Font.createFont(Font.TRUETYPE_FONT, fontfile2);
            fontTitle = Font.createFont(Font.TRUETYPE_FONT, fontfile3);
            fontMono = Font.createFont(Font.TRUETYPE_FONT, fontfile4);
            fontMonoBold = Font.createFont(Font.TRUETYPE_FONT, fontfile5);

            GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
            for (File ff : new File(cfg.getLibs(), path).listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith("ttf");//$NON-NLS-1$
                }
            })) {
                Font newfont = Font.createFont(Font.TRUETYPE_FONT, ff);
                localGraphicsEnvironment.registerFont(newfont);
            }
        } catch (Exception ex) {
            ExceptionAndLogHandler.log(ex);
            font = new JLabel().getFont();
            fontNarrow = font;
            fontTitle = font;
            fontMono = font;
            fontMonoBold = font;
        }
        cfg.setFontLarge(font.deriveFont(18f));
        cfg.setFontSmall(fontNarrow.deriveFont(10f));
        cfg.setFontTable(font.deriveFont(10f));
        cfg.setFontTitle(fontTitle.deriveFont(22f));
        cfg.setFontMono(fontMono.deriveFont(12f));
        cfg.setFontMonoBold(fontMonoBold.deriveFont(12f));

        return cfg;
    }
}
