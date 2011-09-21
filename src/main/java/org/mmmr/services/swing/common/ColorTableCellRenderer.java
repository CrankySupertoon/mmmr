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

    private Icon createEmptyIcon() {
        BufferedImage bi = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
        return new ImageIcon(bi);
    }

    private Icon createIcon(Color color) {
        BufferedImage bi = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bi.createGraphics();
        g2d.setColor(color);
        g2d.fillRect(0, 0, 16, 16);
        return new ImageIcon(bi);
    }

    /**
     * 
     * @see javax.swing.table.DefaultTableCellRenderer#setValue(java.lang.Object)
     */
    @Override
    protected void setValue(Object value) {
        if (value == null) {
            this.setIcon(this.createEmptyIcon());
            super.setValue("");
        }
        Color color = Color.class.cast(value);
        String red = Integer.toHexString(color.getRed());
        String green = Integer.toHexString(color.getGreen());
        String blue = Integer.toHexString(color.getBlue());
        String alpha = Integer.toHexString(color.getBlue());
        super.setValue(("#" + red + green + blue + alpha).toUpperCase());
        this.setIcon(this.createIcon(color));
    }
}
