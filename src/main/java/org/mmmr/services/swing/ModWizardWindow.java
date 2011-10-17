package org.mmmr.services.swing;

import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.TransferHandler;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;

import org.mmmr.Dependency;
import org.mmmr.Mod;
import org.mmmr.Resource;
import org.mmmr.services.Config;
import org.mmmr.services.XmlService;
import org.mmmr.services.swing.common.ETable;
import org.mmmr.services.swing.common.ETableConfig;
import org.mmmr.services.swing.common.ETableHeaders;
import org.mmmr.services.swing.common.ETableRecordBean;
import org.mmmr.services.swing.common.RoundedPanel;
import org.mmmr.services.swing.common.UIUtils;
import org.mmmr.services.swing.common.UIUtils.MoveMouseListener;

/**
 * @author Jurgen
 */
public class ModWizardWindow extends JFrame {
    private static final long serialVersionUID = -6261674801873385201L;

    public static void main(String[] args) {
        try {
            UIUtils.lookAndFeel();
            Config cfg = new Config();
            ModWizardWindow modWizardWindow = new ModWizardWindow(cfg, new XmlService(cfg).load(new FileInputStream(new File(cfg.getMods(),
                    "Doggy Talents v1.5.9.zip.xml")), Mod.class));
            modWizardWindow.setLocationRelativeTo(null);
            modWizardWindow.setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    private Config cfg;

    public ModWizardWindow(final Config cfg, Mod mod) {
        if (mod == null) {
            mod = new Mod();
        }

        this.cfg = cfg;

        RoundedPanel mainpanel = new RoundedPanel(new MigLayout("wrap 5", "[][grow][][grow][20]", ""));
        mainpanel.getDelegate().setShady(false);
        new MoveMouseListener(mainpanel);
        mainpanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        this.getContentPane().add(mainpanel);

        this.setIconImage(cfg.getIcon().getImage());
        this.setTitle(cfg.getTitle());

        JTextField name = new JTextField(mod.getName());
        String DDot = ":";
        mainpanel.add(new JLabel("Name" + DDot), "");
        mainpanel.add(name, "growx");

        JTextField version = new JTextField(mod.getVersion());
        mainpanel.add(new JLabel("Version" + DDot), "");
        mainpanel.add(version, "growx");

        mainpanel.add(new JLabel(""), "");

        final JTextField url = new JTextField(mod.getUrl());
        mainpanel.add(new JLabel("Link" + DDot), "");
        mainpanel.add(url, "span 3, growx");

        JButton ddLink = new JButton("x");
        mainpanel.add(ddLink, ""); // drag-drop link text

        JTextField mcversion = new JTextField(mod.getMcVersionDependency());
        mainpanel.add(new JLabel("MC version" + DDot), "");
        mainpanel.add(mcversion, "growx");

        JTextField mode = new JTextField(String.valueOf(mod.getMode()));
        mainpanel.add(new JLabel("Mode" + DDot), "");
        mainpanel.add(mode, "growx");

        mainpanel.add(new JLabel(""), "");

        final JTextField archive = new JTextField(String.valueOf(mod.getArchive()));
        mainpanel.add(new JLabel("Archive" + DDot), "");
        mainpanel.add(archive, "span 3, growx");

        JButton ddArchive = new JButton("x");
        mainpanel.add(ddArchive, ""); // drag-drop archive file

        ETableConfig ecfg = new ETableConfig(false);
        ecfg.setResizable(true);
        ecfg.setEditable(true);

        JButton ddXml = new JButton("x");
        final ETable dependencies = new ETable(ecfg);
        {
            ETableHeaders headers = new ETableHeaders();
            headers.add("name", String.class, true);
            headers.add("version", String.class, true);
            headers.add("url", String.class, true);
            dependencies.setHeaders(headers);
            for (Dependency element : mod.getDependencies()) {
                ETableRecordBean<Dependency> record = new ETableRecordBean<Dependency>(headers.getColumnNames(), element);
                dependencies.getEventSafe().addRecord(record);
            }
            dependencies.packColumn(0, 4);
            dependencies.packColumn(1, 4);

            mainpanel.add(new JScrollPane(dependencies), "span 4 6, growx, growy");
            mainpanel.add(ddXml, "span 1 6, growx, growy"); // drag-drop xml file dependency
        }

        {
            ETable resources = new ETable(ecfg);
            ETableHeaders headers = new ETableHeaders();
            headers.add("sourcePath", String.class, true);
            headers.add("targetPath", String.class, true);
            headers.add("include", String.class, true);
            headers.add("exclude", String.class, true);
            resources.setHeaders(headers);
            for (Resource element : mod.getResources()) {
                ETableRecordBean<Resource> record = new ETableRecordBean<Resource>(headers.getColumnNames(), element);
                resources.getEventSafe().addRecord(record);
            }

            mainpanel.add(new JScrollPane(resources), "span 4 6, growx, growy");
            mainpanel.add(new JLabel(""), "span 1 6");
        }

        ddArchive.setTransferHandler(new TransferHandler() {
            private static final long serialVersionUID = 9015337880684741674L;

            @Override
            public boolean canImport(TransferSupport support) {
                return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
            }

            @Override
            public boolean importData(TransferSupport support) {
                try {
                    @SuppressWarnings("unchecked")
                    List<File> files = List.class.cast(support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor));
                    archive.setText(files.get(0).getName());

                    return true;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return false;
            }
        });

        ddXml.setTransferHandler(new TransferHandler() {
            private static final long serialVersionUID = 9015337880684741674L;

            @Override
            public boolean canImport(TransferSupport support) {
                return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
            }

            @Override
            public boolean importData(TransferSupport support) {
                try {
                    @SuppressWarnings("unchecked")
                    List<File> files = List.class.cast(support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor));

                    for (File file : files) {
                        try {
                            Mod dependency = cfg.getXml().load(new FileInputStream(file), Mod.class);
                            Dependency element = new Dependency(dependency);
                            ETableRecordBean<Dependency> record = new ETableRecordBean<Dependency>(dependencies.getEventSafe().getHeadernames(),
                                    element);
                            dependencies.getEventSafe().addRecord(record);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    return true;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return false;
            }
        });

        ddLink.setTransferHandler(new TransferHandler() {
            private static final long serialVersionUID = 9015337880684741674L;

            @Override
            public boolean canImport(TransferSupport support) {
                try {
                    return support.isDataFlavorSupported(new DataFlavor("application/x-java-url; class=java.net.URL"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return false;
            }

            @Override
            public boolean importData(TransferSupport support) {
                try {
                    URL u = URL.class.cast(support.getTransferable().getTransferData(new DataFlavor("application/x-java-url; class=java.net.URL")));
                    url.setText(u.toExternalForm());

                    return true;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return false;
            }
        });

        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setUndecorated(true);
        UIUtils.translucent(this);
        this.setSize(1280, 600);
        UIUtils.rounded(this);
        this.setResizable(false);
    }
}
