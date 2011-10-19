package org.mmmr.services.swing;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.miginfocom.swing.MigLayout;

import org.mmmr.Dependency;
import org.mmmr.Mod;
import org.mmmr.Mode;
import org.mmmr.Resource;
import org.mmmr.services.ArchiveService;
import org.mmmr.services.Config;
import org.mmmr.services.ExceptionAndLogHandler;
import org.mmmr.services.Messages;
import org.mmmr.services.UtilityMethods;
import org.mmmr.services.XmlService;
import org.mmmr.services.interfaces.ArchiveEntry;
import org.mmmr.services.interfaces.Path;
import org.mmmr.services.swing.common.ETable;
import org.mmmr.services.swing.common.ETableConfig;
import org.mmmr.services.swing.common.ETableHeaders;
import org.mmmr.services.swing.common.ETableRecord;
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

    public static List<Resource> findMapping(File archive) throws IOException {
        List<Resource> resources = new ArrayList<Resource>();
        boolean jarfolder = false;
        Map<EnhancedPath, String> mapping = new HashMap<EnhancedPath, String>();
        for (ArchiveEntry ae : ArchiveService.list(archive)) {
            if (ae.path.getPath().toLowerCase().contains("read")) {//$NON-NLS-1$ 
                //
            } else if (ae.dir && ae.path.getPath().toLowerCase().contains("jar")) {//$NON-NLS-1$ 
                jarfolder = true;
                mapping.put(new EnhancedPath(ae), "bin/" + Config.MINECRAFT_JAR);//$NON-NLS-1$ 
            } else if (ae.dir && ae.path.getPath().toLowerCase().contains("resource")) {//$NON-NLS-1$ 
                mapping.put(new EnhancedPath(ae), "resources");//$NON-NLS-1$ 
            } else if (ae.dir && ae.path.getPath().toLowerCase().contains("mods")) {//$NON-NLS-1$ 
                mapping.put(new EnhancedPath(ae), "mods");//$NON-NLS-1$ 
            } else {
                //
            }
        }
        Path[] firstRun = mapping.keySet().toArray(new Path[0]);
        for (ArchiveEntry ae : ArchiveService.list(archive)) {
            if (ae.path.getPath().toLowerCase().contains("read")) {
                //
            } else if (ae.dir && ae.path.getPath().toLowerCase().contains("jar")) {//$NON-NLS-1$ 
                //
            } else if (ae.dir && ae.path.getPath().toLowerCase().contains("resource")) {//$NON-NLS-1$ 
                //
            } else if (ae.dir && ae.path.getPath().toLowerCase().contains("mods")) {//$NON-NLS-1$ 
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
                resources.add(new Resource(("".equals(e.getKey().getPath()) ? "." : e.getKey().getPath()) + "/", ("".equals(e.getValue()) ? "." : e
                        .getValue()) + "/"));
            } else {
                resources.add(new Resource(e.getKey().getPath(), e.getValue()));
            }
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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public ModWizardWindow(final Config cfg, Mod mod) {
        if (mod == null) {
            mod = new Mod();
            this.setTitle(cfg.getTitle());
        } else {
            this.setTitle(cfg.getTitle() + " - " + mod.getArchive());//$NON-NLS-1$ 
        }

        this.cfg = cfg;

        JPanel formpanel = new JPanel(new MigLayout("wrap 5", "[][grow][][grow][26]", ""));//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
        JPanel actions = new JPanel(new GridLayout(1, -1));

        {
            RoundedPanel mainpanel = new RoundedPanel(new BorderLayout());
            mainpanel.getDelegate().setShady(false);
            new MoveMouseListener(mainpanel);
            mainpanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
            this.getContentPane().add(mainpanel);

            mainpanel.add(formpanel, BorderLayout.CENTER);
            mainpanel.add(actions, BorderLayout.SOUTH);
        }

        this.setIconImage(cfg.getIcon().getImage());

        final JTextField name = new JTextField(mod.getName());
        String DDot = ":";//$NON-NLS-1$ 
        formpanel.add(new JLabel("Name" + DDot), "");//$NON-NLS-2$ 
        formpanel.add(name, "growx");//$NON-NLS-1$ 

        final JTextField version = new JTextField(mod.getVersion());
        formpanel.add(new JLabel("Version" + DDot), "");
        formpanel.add(version, "growx");//$NON-NLS-1$ 

        formpanel.add(new JLabel(""), "");//$NON-NLS-2$ 

        final JTextField url = new JTextField(mod.getUrl());
        formpanel.add(new JLabel("Link" + DDot), "");//$NON-NLS-2$ 
        formpanel.add(url, "span 3, growx");//$NON-NLS-1$ 

        JButton ddLink = new JButton(UtilityMethods.getIcon("images/dropurl.png"));
        formpanel.add(ddLink, ""); // drag-drop link text

        final JTextField mcversion = new JTextField(mod.getMcVersionDependency());
        formpanel.add(new JLabel("MC version" + DDot), "");//$NON-NLS-2$ 
        formpanel.add(mcversion, "growx");//$NON-NLS-1$ 

        Mode[] values = { null, Mode.SSP, Mode.SMP };
        final JComboBox mode = new JComboBox(values);
        if (mod.getMode() != null) {
            mode.setSelectedItem(mod.getMode());
        }
        formpanel.add(new JLabel("Mode" + DDot), "");//$NON-NLS-2$ 
        formpanel.add(mode, "growx");//$NON-NLS-1$ 

        formpanel.add(new JLabel(""), "");//$NON-NLS-2$ 

        final JTextField archive = new JTextField(String.valueOf(mod.getArchive()));
        archive.getDocument().addDocumentListener(new DocumentListener() {
            private void change() {
                ModWizardWindow.this.setTitle(cfg.getTitle() + " - " + archive.getText());//$NON-NLS-1$ 
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                this.change();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                this.change();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                this.change();
            }
        });
        formpanel.add(new JLabel("Archive" + DDot), "");//$NON-NLS-2$ 
        formpanel.add(archive, "span 3, growx");//$NON-NLS-1$ 

        JButton ddArchive = new JButton(UtilityMethods.getIcon("images/dropcopy.png"));
        ddArchive.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    File f = UtilityMethods.selectFile(cfg.getMods(), new javax.swing.filechooser.FileFilter() {
                        @Override
                        public boolean accept(File file) {
                            if (file.isDirectory()) {
                                return true;
                            }
                            String filename = file.getName().toLowerCase();
                            if (!filename.endsWith(".jar") && !filename.endsWith(".7z") && !filename.endsWith(".rar") && !filename.endsWith(".zip") && !filename.endsWith(".gz")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                                return false;
                            }
                            return !filename.contains("yogbox"); //$NON-NLS-1$ 
                        }

                        @Override
                        public String getDescription() {
                            return "Archives";
                        }
                    });
                    if (f == null) {
                        return;
                    }
                    archive.setText(f.getName());
                    ModWizardWindow.this.setTitle(cfg.getTitle() + " - " + archive.getText());
                } catch (Exception ex) {
                    ExceptionAndLogHandler.log(ex);
                }
            }
        });
        formpanel.add(ddArchive, ""); //$NON-NLS-1$ // drag-drop archive file

        ETableConfig ecfg = new ETableConfig(false);
        ecfg.setResizable(true);
        ecfg.setEditable(true);

        JButton ddXml = new JButton(UtilityMethods.getIcon("images/dropcopy.png"));
        final ETable dependencies = new ETable(ecfg);
        {
            ETableHeaders headers = new ETableHeaders();
            headers.add("name", String.class, true);//$NON-NLS-1$ 
            headers.add("version", String.class, true);//$NON-NLS-1$ 
            headers.add("url", String.class, true);//$NON-NLS-1$ 
            dependencies.setHeaders(headers);
            for (Dependency element : mod.getDependencies()) {
                ETableRecordBean<Dependency> record = new ETableRecordBean<Dependency>(headers.getColumnNames(), element);
                dependencies.getSimpleThreadSafeInterface().addRecord(record);
            }
            dependencies.getSimpleThreadSafeInterface().packColumn(0);
            dependencies.getSimpleThreadSafeInterface().packColumn(1);

            formpanel.add(new JScrollPane(dependencies), "span 4 6, growx, growy");//$NON-NLS-1$ 
            formpanel.add(ddXml, "span 1 6, growx, growy");//$NON-NLS-1$  // drag-drop xml file dependency
        }

        final ETable resources = new ETable(ecfg);
        {
            ETableHeaders headers = new ETableHeaders();
            headers.add("sourcePath", String.class, true);//$NON-NLS-1$ 
            headers.add("targetPath", String.class, true);//$NON-NLS-1$ 
            headers.add("include", String.class, true);//$NON-NLS-1$ 
            headers.add("exclude", String.class, true);//$NON-NLS-1$ 
            resources.setHeaders(headers);
            for (Resource element : mod.getResources()) {
                ETableRecordBean<Resource> record = new ETableRecordBean<Resource>(headers.getColumnNames(), element);
                resources.getSimpleThreadSafeInterface().addRecord(record);
            }

            formpanel.add(new JScrollPane(resources), "span 4 6, growx, growy");//$NON-NLS-1$ 
            JButton comp = new JButton(UtilityMethods.getIcon("images/dropcopy.png"));
            comp.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    File a = new File(cfg.getMods(), archive.getText());
                    try {
                        List<Resource> findMapping = ModWizardWindow.findMapping(a);
                        resources.getSimpleThreadSafeInterface().removeAllRecords();
                        for (Resource r : findMapping) {
                            ETableRecordBean<Resource> record = new ETableRecordBean<Resource>(resources.getSimpleThreadSafeInterface()
                                    .getHeadernames(), r);
                            resources.getSimpleThreadSafeInterface().addRecord(record);
                        }
                    } catch (FileNotFoundException ex) {
                        ExceptionAndLogHandler.handle(cfg, Messages.getString("Exception.title"),//$NON-NLS-1$ 
                                String.format(Messages.getString("Exception.FileNotFoundException"), a.getAbsolutePath()), ex);//$NON-NLS-1$ 
                    } catch (Exception ex) {
                        ExceptionAndLogHandler.log(ex);
                    }
                }
            });
            formpanel.add(comp, "span 1 6, growx, growy");//$NON-NLS-1$ 
        }

        ddArchive.setTransferHandler(new TransferHandler() {
            private static final long serialVersionUID = 9015337880684741674L;

            @Override
            public boolean canImport(TransferSupport support) {
                List<File> files = ModWizardWindow.this.accepts(support, "7z", "rar", "zip", "jar", "gz");//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ 

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
                    String an = ModWizardWindow.this.asFileList(support).get(0).getName();
                    archive.setText(an);
                    ModWizardWindow.this.setTitle(cfg.getTitle() + " - " + an);//$NON-NLS-1$ 

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
                List<File> files = ModWizardWindow.this.accepts(support, "xml");//$NON-NLS-1$ 

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
                            ETableRecordBean<Dependency> record = new ETableRecordBean<Dependency>(dependencies.getSimpleThreadSafeInterface()
                                    .getHeadernames(), element);
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

        final Mod modNew = mod;
        {
            JButton save = new JButton(Messages.getString("ModWizardWindow.save")); //$NON-NLS-1$
            save.setFont(cfg.getFontLarge());
            save.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    modNew.setName(name.getText());
                    modNew.setUrl(url.getText());
                    modNew.setMode(Mode.class.cast(mode.getSelectedItem()));
                    modNew.setVersion(version.getText());
                    modNew.setArchive(archive.getText());
                    modNew.setMcVersionDependency(mcversion.getText());

                    modNew.removeAllDependencies();
                    for (ETableRecord<Dependency> record : dependencies.getRecords()) {
                        modNew.addDepencency(record.getBean());
                    }

                    modNew.removeAllResources();
                    for (ETableRecord<Resource> record : resources.getRecords()) {
                        modNew.addResource(record.getBean());
                    }
                }
            });
            actions.add(save);
        }
        {
            JButton quit = new JButton(Messages.getString("ModWizardWindow.close")); //$NON-NLS-1$
            quit.setFont(cfg.getFontLarge());
            quit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ModWizardWindow.this.dispose();
                }
            });
            actions.add(quit);
        }

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
            this.linkDataFlavor = new DataFlavor("application/x-java-url; class=java.net.URL");//$NON-NLS-1$ 
        }
        return this.linkDataFlavor;
    }
}
