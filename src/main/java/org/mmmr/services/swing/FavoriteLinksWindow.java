package org.mmmr.services.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import org.apache.commons.lang.StringUtils;
import org.mmmr.services.Config;
import org.mmmr.services.DownloadingService;
import org.mmmr.services.ExceptionAndLogHandler;
import org.mmmr.services.Messages;
import org.mmmr.services.swing.common.ETable;
import org.mmmr.services.swing.common.ETableHeaders;
import org.mmmr.services.swing.common.ETableRecordArray;
import org.mmmr.services.swing.common.RoundedPanel;
import org.mmmr.services.swing.common.UIUtils;
import org.mmmr.services.swing.common.UIUtils.MoveMouseListener;

/**
 * @author Jurgen
 */
public class FavoriteLinksWindow extends JWindow {
    /** serialVersionUID */
    private static final long serialVersionUID = -2596040987353689775L;

    public static void main(String[] args) {
        try {
            File linkdir = new File("data/links/"); //$NON-NLS-1$
            File[] links = linkdir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (!name.toLowerCase().endsWith(".url")) { //$NON-NLS-1$
                        return false;
                    }
                    return true;
                }
            });
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data/links/links.txt"))); //$NON-NLS-1$
            for (File link : links) {
                out.write(link.lastModified() + "::" + link.getName() + "\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            out.flush();
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Config cfg;

    public FavoriteLinksWindow(Config cfg) throws IOException, URISyntaxException {
        super((Window) UIUtils.getCurrentFrame());
        this.cfg = cfg;
        try {
            this.updateLinks();
        } catch (Exception ex) {
            ExceptionAndLogHandler.log(ex);
        }
        final ETable table = new ETable() {
            private static final long serialVersionUID = -8250534232070637135L;

            @Override
            protected JTableHeader createDefaultTableHeader() {
                return new JTableHeader(this.getColumnModel()) {
                    private static final long serialVersionUID = 4738636168685789178L;

                    @Override
                    public void processMouseMotionEvent(MouseEvent me) {
                        this.setCursor(new Cursor(Cursor.MOVE_CURSOR));
                        super.processMouseMotionEvent(me);
                    }
                };
            }

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int rowIndex, int vColIndex) {
                Component c = this.super_prepareRenderer(renderer, rowIndex, vColIndex);
                String url = String.valueOf(this.getRecordAtVisualRow(rowIndex).get(1));
                JLabel.class.cast(c).setToolTipText(url);
                this.setCursor(new Cursor(Cursor.HAND_CURSOR));
                return c;
            }
        };
        ETableHeaders headers = new ETableHeaders();
        headers.add(Messages.getString("FavoriteLinksWindow.links")); //$NON-NLS-1$
        table.getEventSafe().setHeaders(headers);
        File f = new File(cfg.getData(), "links"); //$NON-NLS-1$
        for (File ff : f.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith("url"); //$NON-NLS-1$
            }
        })) {
            FileInputStream in = new FileInputStream(ff);
            Properties p = new Properties();
            p.load(in);
            in.close();
            table.getEventSafe().addRecord(new ETableRecordArray(ff.getName().replaceAll("\\.url", "").replaceAll("\\.URL", ""), p.get("URL"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        }

        this.setAlwaysOnTop(true);

        final RoundedPanel mainpanel = new RoundedPanel(new BorderLayout());
        mainpanel.getDelegate().setShady(false);
        mainpanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        this.getContentPane().add(mainpanel, BorderLayout.CENTER);
        mainpanel.setLayout(new BorderLayout());
        mainpanel.add(table.getTableHeader(), BorderLayout.NORTH);
        new MoveMouseListener(table.getTableHeader());
        mainpanel.add(table, BorderLayout.CENTER);
        JButton close = new JButton("close");
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainpanel.removeAll();
                FavoriteLinksWindow.this.setVisible(false);
                FavoriteLinksWindow.this.dispose();
            }
        });
        mainpanel.add(close, BorderLayout.SOUTH);
        this.pack();
        this.setSize(400, this.getHeight());
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    if ((e.getClickCount() == 1) && (e.getButton() == MouseEvent.BUTTON1)) {
                        int row = table.rowAtPoint(e.getPoint());
                        if (row == -1) {
                            return;
                        }
                        int col = table.columnAtPoint(e.getPoint());
                        if (col == -1) {
                            return;
                        }
                        if (col == 0) {
                            String url = String.valueOf(table.getRecordAtVisualRow(row).get(1));
                            if (Desktop.isDesktopSupported()) {
                                Desktop.getDesktop().browse(URI.create(url));
                            } else {
                                // TODO
                            }
                        }
                    }
                } catch (Exception ex) {
                    ExceptionAndLogHandler.log(ex);
                }
            }
        });

        UIUtils.translucent(this);
        UIUtils.rounded(this);
    }

    /**
     * update links
     */
    public void updateLinks() throws MalformedURLException, IOException, URISyntaxException {
        Map<String, Long> existing = new HashMap<String, Long>();

        File[] urls = new File(this.cfg.getData(), "links").listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (!name.toLowerCase().endsWith(".url")) { //$NON-NLS-1$
                    return false;
                }
                return true;
            }
        });
        if (urls != null) {
            for (File url : urls) {
                existing.put(url.getName(), url.lastModified());
            }
        }
        URL base = new URL(this.cfg.getMmmrSvnOnGoogleCode());
        for (String record : new String(DownloadingService.downloadURL(new URL(this.cfg.getMmmrSvnOnGoogleCode() + "/data/links/links.txt"))) //$NON-NLS-1$
                .split("\r\n")) { //$NON-NLS-1$
            if (StringUtils.isBlank(record)) {
                continue;
            }
            String[] d = record.split("::"); //$NON-NLS-1$
            String urlname = d[1];
            Long lastmod = Long.parseLong(d[0]);
            Long lastmodlocal = existing.get(urlname);
            if ((lastmodlocal == null) || (lastmodlocal < lastmod)) {
                // does not exists or newer on server => download
                URI uri = new URI(base.getProtocol(), base.getHost(), base.getPath() + "/data/links/" + urlname, null); //$NON-NLS-1$
                String url = uri.toURL().toString();
                File target = new File(this.cfg.getData(), "links/" + urlname);
                try {
                    DownloadingService.downloadURL(new URL(url), target);
                } catch (IOException ex) {
                    target.delete();
                    throw ex;
                }
            }
        }
    }
}
