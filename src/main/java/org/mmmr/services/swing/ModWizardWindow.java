package org.mmmr.services.swing;

import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;

import org.mmmr.Dependency;
import org.mmmr.Mod;
import org.mmmr.Resource;
import org.mmmr.services.ArchiveService;
import org.mmmr.services.Config;
import org.mmmr.services.ExceptionAndLogHandler;
import org.mmmr.services.XmlService;
import org.mmmr.services.interfaces.ArchiveEntry;
import org.mmmr.services.interfaces.Path;
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
    public static class EnhancedPath extends Path {
        private final boolean dir;

        public EnhancedPath(ArchiveEntry ae) {
            super(ae.path.getPath());
            this.dir = ae.dir;
        }
    }

    private static final long serialVersionUID = -6261674801873385201L;

    public static List<Resource> findMapping(File archive) {
        List<Resource> resources = new ArrayList<Resource>();
        try {
            boolean jarfolder = false;
            Map<EnhancedPath, String> mapping = new HashMap<EnhancedPath, String>();
            for (ArchiveEntry ae : ArchiveService.list(archive)) {
                if (ae.path.getPath().toLowerCase().contains("read")) {
                    //
                } else if (ae.dir && ae.path.getPath().toLowerCase().contains("jar")) {
                    jarfolder = true;
                    mapping.put(new EnhancedPath(ae), "bin/" + Config.MINECRAFT_JAR);
                } else if (ae.dir && ae.path.getPath().toLowerCase().contains("resource")) {
                    mapping.put(new EnhancedPath(ae), "resources");
                } else if (ae.dir && ae.path.getPath().toLowerCase().contains("mods")) {
                    mapping.put(new EnhancedPath(ae), "mods");
                } else {
                    //
                }
            }
            Path[] firstRun = mapping.keySet().toArray(new Path[0]);
            for (ArchiveEntry ae : ArchiveService.list(archive)) {
                if (ae.path.getPath().toLowerCase().contains("read")) {
                    //
                } else if (ae.dir && ae.path.getPath().toLowerCase().contains("jar")) {
                    //
                } else if (ae.dir && ae.path.getPath().toLowerCase().contains("resource")) {
                    //
                } else if (ae.dir && ae.path.getPath().toLowerCase().contains("mods")) {
                    //
                } else {
                    boolean exists = false;
                    for (Path existing : firstRun) {
                        // System.out.println("? " + ae.path + " // " + existing + " // " + ae.path.startsWith(existing));
                        if (ae.path.startsWith(existing)) {
                            exists = true;
                            continue;
                        }

                        if (existing.startsWith(ae.path)) {
                            exists = true;
                            continue;
                        }
                    }
                    if (!exists) {
                        if (jarfolder) {
                            mapping.put(new EnhancedPath(ae), ae.path.getPath());
                        } else {
                            mapping.put(new EnhancedPath(ae), "bin/" + Config.MINECRAFT_JAR + "/" + ae.path.getPath());
                        }
                    }
                }
            }
            for (Map.Entry<EnhancedPath, String> e : mapping.entrySet()) {
                if (e.getKey().dir) {
                    resources.add(new Resource(("".equals(e.getKey().getPath()) ? "." : e.getKey().getPath()) + "/", ("".equals(e.getValue()) ? "."
                            : e.getValue()) + "/"));
                } else {
                    resources.add(new Resource(e.getKey().getPath(), e.getValue()));
                }
            }
        } catch (Exception ex) {
            ExceptionAndLogHandler.log(ex);
        }
        return resources;
    }

    public static void main(String[] args) {
        try {
            UIUtils.lookAndFeel();
            Config cfg = new Config();
            ModWizardWindow modWizardWindow = new ModWizardWindow(cfg, new XmlService(cfg).load(new FileInputStream(new File(cfg.getMods(),
                    "Doggy Talents v1.5.9.zip.xml")), Mod.class));
            modWizardWindow.setLocationRelativeTo(null);
            modWizardWindow.setVisible(true);
        } catch (Exception ex) {
            ExceptionAndLogHandler.log(ex);
        }
    }

    @SuppressWarnings("unused")
    private Config cfg;

    private DataFlavor linkDataFlavor = null;

    public ModWizardWindow(final Config cfg, Mod mod) {
        if (mod == null) {
            mod = new Mod();
        }

        this.cfg = cfg;

        RoundedPanel mainpanel = new RoundedPanel(new MigLayout("wrap 5", "[][grow][][grow][26]", ""));
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
                dependencies.getSimpleThreadSafeInterface().addRecord(record);
            }
            dependencies.getSimpleThreadSafeInterface().packColumn(0);
            dependencies.getSimpleThreadSafeInterface().packColumn(1);

            mainpanel.add(new JScrollPane(dependencies), "span 4 6, growx, growy");
            mainpanel.add(ddXml, "span 1 6, growx, growy"); // drag-drop xml file dependency
        }

        {
            final ETable resources = new ETable(ecfg);
            ETableHeaders headers = new ETableHeaders();
            headers.add("sourcePath", String.class, true);
            headers.add("targetPath", String.class, true);
            headers.add("include", String.class, true);
            headers.add("exclude", String.class, true);
            resources.setHeaders(headers);
            for (Resource element : mod.getResources()) {
                ETableRecordBean<Resource> record = new ETableRecordBean<Resource>(headers.getColumnNames(), element);
                resources.getSimpleThreadSafeInterface().addRecord(record);
            }

            mainpanel.add(new JScrollPane(resources), "span 4 6, growx, growy");
            JButton comp = new JButton("x");
            comp.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        File a = new File(cfg.getMods(), archive.getText());
                        List<Resource> findMapping = ModWizardWindow.findMapping(a);
                        resources.getSimpleThreadSafeInterface().removeAllRecords();
                        for (Resource r : findMapping) {
                            ETableRecordBean<Resource> record = new ETableRecordBean<Resource>(resources.getSimpleThreadSafeInterface().getHeadernames(), r);
                            resources.getSimpleThreadSafeInterface().addRecord(record);
                        }
                    } catch (Exception ex) {
                        ExceptionAndLogHandler.log(ex);
                    }
                }
            });
            mainpanel.add(comp, "span 1 6, growx, growy");
        }

        ddArchive.setTransferHandler(new TransferHandler() {
            private static final long serialVersionUID = 9015337880684741674L;

            @Override
            public boolean canImport(TransferSupport support) {
                List<File> files = ModWizardWindow.this.accepts(support, "7z", "rar", "zip");

                if (files == null) {
                    return false;
                }

                support.setDropAction(TransferHandler.LINK);

                if (files.size() > 1) {
                    return false;
                }

                return true;
            }

            @Override
            public boolean importData(TransferSupport support) {
                try {
                    archive.setText(ModWizardWindow.this.asFileList(support).get(0).getName());

                    return true;
                } catch (Exception ex) {
                    ExceptionAndLogHandler.log(ex);
                }
                return false;
            }
        });

        ddXml.setTransferHandler(new TransferHandler() {
            private static final long serialVersionUID = 9015337880684741674L;

            @Override
            public boolean canImport(TransferSupport support) {
                List<File> files = ModWizardWindow.this.accepts(support, "xml");

                if (files == null) {
                    return false;
                }

                support.setDropAction(TransferHandler.LINK);

                return true;
            }

            @Override
            public boolean importData(TransferSupport support) {
                try {
                    for (File file : ModWizardWindow.this.asFileList(support)) {
                        try {
                            Mod dependency = cfg.getXml().load(new FileInputStream(file), Mod.class);
                            Dependency element = new Dependency(dependency);
                            ETableRecordBean<Dependency> record = new ETableRecordBean<Dependency>(dependencies.getSimpleThreadSafeInterface().getHeadernames(),
                                    element);
                            dependencies.getSimpleThreadSafeInterface().addRecord(record);
                        } catch (Exception ex) {
                            ExceptionAndLogHandler.log(ex);
                        }
                    }

                    return true;
                } catch (Exception ex) {
                    ExceptionAndLogHandler.log(ex);
                }
                return false;
            }
        });

        ddLink.setTransferHandler(new TransferHandler() {
            private static final long serialVersionUID = 9015337880684741674L;

            @Override
            public boolean canImport(TransferSupport support) {
                try {
                    support.setDropAction(TransferHandler.LINK);
                    return support.isDataFlavorSupported(ModWizardWindow.this.getLinkflavor());
                } catch (Exception ex) {
                    ExceptionAndLogHandler.log(ex);
                }
                return false;
            }

            @Override
            public boolean importData(TransferSupport support) {
                try {
                    URL u = URL.class.cast(support.getTransferable().getTransferData(ModWizardWindow.this.getLinkflavor()));
                    url.setText(u.toExternalForm());

                    return true;
                } catch (Exception ex) {
                    ExceptionAndLogHandler.log(ex);
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

    protected List<File> accepts(TransferSupport support, String... exts) {
        if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            return null;
        }
        List<File> files = this.asFileList(support);
        if ((files == null) || (files.size() == 0)) {
            return null;
        }
        for (File file : files) {
            boolean success = false;
            for (String ext : exts) {
                if (file.getName().endsWith("." + ext)) {
                    success = true;
                }
            }
            if (!success) {
                return null;
            }
        }
        return files;
    }

    @SuppressWarnings("unchecked")
    protected List<File> asFileList(TransferSupport support) {
        try {
            return List.class.cast(support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor));
        } catch (Exception ex) {
            ExceptionAndLogHandler.log(ex);
            return null;
        }
    }

    protected DataFlavor getLinkflavor() throws ClassNotFoundException {
        if (this.linkDataFlavor == null) {
            this.linkDataFlavor = new DataFlavor("application/x-java-url; class=java.net.URL");
        }
        return this.linkDataFlavor;
    }
}
