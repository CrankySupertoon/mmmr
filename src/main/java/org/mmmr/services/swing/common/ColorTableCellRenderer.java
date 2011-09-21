package org.mmmr.services.swing.common;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableCellRenderer;

public class ColorTableCellRenderer extends DefaultTableCellRenderer.UIResource {
    /** serialVersionUID */
    private static final long serialVersionUID = -7605301072046365348L;

    protected Icon emptyIcon;

    public ColorTableCellRenderer() {
        BufferedImage bi = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        this.emptyIcon = new ImageIcon(bi);
    }

    private Icon createIcon(Color color) {
        BufferedImage bi = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bi.createGraphics();
        g2d.setColor(color);
        g2d.fillRoundRect(0, 0, 16, 16, 4, 4);
        return new ImageIcon(bi);
    }

    /**
     * 
     * @see javax.swing.table.DefaultTableCellRenderer#setValue(java.lang.Object)
     */
    @Override
    protected void setValue(Object value) {
        if (value == null) {
            this.setIcon(this.emptyIcon);
            super.setValue("");
            return;
        }
        Color color = Color.class.cast(value);
        String red = Integer.toHexString(color.getRed());
        if (red.length() == 1) {
            red = "0" + red;
        }
        String green = Integer.toHexString(color.getGreen());
        if (green.length() == 1) {
            green = "0" + green;
        }
        String blue = Integer.toHexString(color.getBlue());
        if (blue.length() == 1) {
            blue = "0" + blue;
        }
        String alpha = Integer.toHexString(color.getAlpha());
        if (alpha.length() == 1) {
            alpha = "0" + alpha;
        }
        if (alpha.equals("ff")) {
            super.setValue(("#" + red + green + blue).toUpperCase());
            this.setIcon(this.createIcon(color));
        } else {
            super.setValue(("#" + red + green + blue + "(" + alpha + ")").toUpperCase());
            this.setIcon(this.createIcon(color));
        }
    }
}
