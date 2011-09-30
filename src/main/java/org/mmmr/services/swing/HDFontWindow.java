package org.mmmr.services.swing;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.mmmr.services.Config;
import org.mmmr.services.ExceptionAndLogHandler;
import org.mmmr.services.UtilityMethods;
import org.mmmr.services.Messages;
import org.mmmr.services.NiceFont;
import org.mmmr.services.swing.common.UIUtils;
import org.mmmr.services.swing.common.UIUtils.MoveMouseListener;
import org.mmmr.services.swing.common.RoundedPanel;

public class HDFontWindow extends JFrame {
    private static final long serialVersionUID = -8166133499677459166L;

    private static Object getFieldValue(Object object, Class<?> clazz, String fieldname) throws Exception {
        Field field = clazz.getDeclaredField(fieldname);
        field.setAccessible(true);

        return field.get(object);
    }

    private static Object getFieldValue(Object object, String fieldname) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldname);
        field.setAccessible(true);

        return field.get(object);
    }

    public static void main(String[] args) {
        try {
            new HDFontWindow(NiceFont.prepareFont(new Config())).setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private final Config cfg;

    private JLabel preview;

    private int scale = 8; // 8x128 = 1024

    private static final int whb = 128;

    public HDFontWindow(Config cfg) {
        this.cfg = cfg;
        this.setIconImage(cfg.getIcon().getImage());
        this.setTitle(cfg.getTitle());
        this.setUndecorated(true);
        final RoundedPanel mainpanel = new RoundedPanel(new BorderLayout());
        mainpanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        mainpanel.getDelegate().setShady(false);
        new MoveMouseListener(mainpanel);
        this.getContentPane().add(mainpanel, BorderLayout.CENTER);
        JLabel label = new JLabel(this.getTitle());
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(cfg.getFontTitle());
        mainpanel.add(label, BorderLayout.NORTH);

        Vector<String> options = new Vector<String>();
        final Map<String, Font> map = new HashMap<String, Font>();

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gs = ge.getDefaultScreenDevice();
        final GraphicsConfiguration gc = gs.getDefaultConfiguration();
        // the first two rows (-2) are not drawn because they do not contain drawable characters
        BufferedImage bi = gc.createCompatibleImage(this.scale * HDFontWindow.whb, (this.scale - 2) * HDFontWindow.whb, Transparency.OPAQUE);
        Graphics2D g2d = bi.createGraphics();

        for (Font font : ge.getAllFonts()) {
            try {
                font = font.deriveFont(Font.PLAIN, 12f); // create default font
                @SuppressWarnings("unused")
                String family = font.getFamily();
                font.canDisplay(' '); // initializes Font2DHandle
                g2d.getFontMetrics(font); // make sure we can get FontMetrics info
                @SuppressWarnings({ "restriction", "unused" })
                String path = (String) HDFontWindow.getFieldValue(
                        HDFontWindow.getFieldValue(HDFontWindow.getFieldValue(font, "font2DHandle"), "font2D"), sun.font.PhysicalFont.class, //$NON-NLS-1$ //$NON-NLS-2$
                        "platName"); //$NON-NLS-1$
                String full = font.getFontName();
                ExceptionAndLogHandler.log(full);
                options.add(full);
                map.put(full, font);
            } catch (Exception ex) {
                //
            }
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        final DefaultComboBoxModel model = new DefaultComboBoxModel(options);
        @SuppressWarnings({ "rawtypes", "unchecked" })
        final JComboBox combo = new JComboBox(model);

        JPanel actions = new JPanel(new GridLayout(1, -1));
        actions.add(combo);
        mainpanel.add(actions, BorderLayout.SOUTH);

        this.preview = new JLabel(new ImageIcon(bi));
        HDFontWindow.this.preview.setToolTipText(Messages.getString("HDFont.tooltip"));
        mainpanel.add(this.preview, BorderLayout.CENTER);

        combo.setSelectedItem("DejaVu Sans Mono"); //$NON-NLS-1$
        this.preview(mainpanel, map, gc, "DejaVu Sans Mono");

        combo.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getWheelRotation() < 0) {
                    int newIndex = combo.getSelectedIndex() - 1;
                    if (newIndex >= 0) {
                        combo.setSelectedIndex(newIndex);
                    }
                } else {
                    int newIndex = combo.getSelectedIndex() + 1;
                    if (newIndex < model.getSize()) {
                        combo.setSelectedIndex(newIndex);
                    }
                }
            }
        });

        combo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            HDFontWindow.this.preview(mainpanel, map, gc, e.getItem());
                        }
                    });
                }
            }
        });

        JButton choose = new JButton(Messages.getString("HDFont.choose_font")); //$NON-NLS-1$
        choose.setFont(cfg.getFontLarge());
        choose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Font font = map.get(combo.getSelectedItem());
                    NiceFont.hqFontFile(gc, false, HDFontWindow.this.cfg, HDFontWindow.this.scale, font);
                    HDFontWindow.this.dispose();
                } catch (Exception ex) {
                    ExceptionAndLogHandler.log(ex);
                }
            }
        });
        actions.add(choose);

        JButton restore = new JButton(Messages.getString("HDFont.restore")); //$NON-NLS-1$
        restore.setFont(cfg.getFontLarge());
        restore.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String path = "font/default.png";//$NON-NLS-1$
                    UtilityMethods.copyFile(new File(HDFontWindow.this.cfg.getMcJarBackup(), path), new File(HDFontWindow.this.cfg.getMcJar(), path));
                    HDFontWindow.this.dispose();
                } catch (Exception ex) {
                    ExceptionAndLogHandler.log(ex);
                }
            }
        });
        actions.add(restore);

        JButton quit = new JButton(Messages.getString("HDFont.do_not_make_changes")); //$NON-NLS-1$
        quit.setFont(cfg.getFontLarge());
        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HDFontWindow.this.dispose();
            }
        });
        actions.add(quit);

        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        this.setUndecorated(true);
        UIUtils.translucent(this);
        this.pack();
        UIUtils.rounded(this);
        this.setResizable(false);
    }

    protected void preview(final RoundedPanel mainpanel, final Map<String, Font> map, final GraphicsConfiguration gc, final Object key) {
        Font font = map.get(key);
        // full bitmap like minecraft uses it
        BufferedImage prv = NiceFont.hqFontFile(gc, true, HDFontWindow.this.cfg, HDFontWindow.this.scale, font);
        // the first two rows (-2) are not drawn because they do not contain drawable characters
        // FIXME: what to do on monitors less than 1024 pixels heigh
        // the bitmap does not fit and pushes buttons and combobox of the screen
        BufferedImage _bi = gc.createCompatibleImage(HDFontWindow.this.scale * HDFontWindow.whb, (HDFontWindow.this.scale - 2) * HDFontWindow.whb,
                Transparency.OPAQUE);
        Graphics2D _g2d = _bi.createGraphics();
        _g2d.drawImage(prv, null, 0, -2 * HDFontWindow.whb);
        _g2d.dispose();
        mainpanel.remove(HDFontWindow.this.preview);
        HDFontWindow.this.preview = new JLabel(new ImageIcon(_bi));
        HDFontWindow.this.preview.setToolTipText(Messages.getString("HDFont.tooltip"));
        mainpanel.add(HDFontWindow.this.preview, BorderLayout.CENTER);
        mainpanel.revalidate();
    }
}
