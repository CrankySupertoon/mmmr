package org.mmmr.services.swing;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileInputStream;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.mmmr.Dependency;
import org.mmmr.Mod;
import org.mmmr.services.Config;
import org.mmmr.services.XmlService;
import org.mmmr.services.swing.common.ETable;
import org.mmmr.services.swing.common.ETableHeaders;
import org.mmmr.services.swing.common.ETableRecordBean;

/**
 * @author Jurgen
 */
public class ModWizard extends JFrame {
    private static final long serialVersionUID = -6261674801873385201L;

    public static void main(String[] args) {
        try {
            Config cfg = new Config();
            new ModWizard(cfg, new XmlService(cfg).load(new FileInputStream(new File(cfg.getMods(), "Doggy Talents v1.5.9.zip.xml")), Mod.class))
                    .setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    public ModWizard(Config cfg, Mod mod) {
        if (mod == null) {
            mod = new Mod();
        }

        JPanel mainpanel = new JPanel(new MigLayout("wrap 4", "[][grow][][grow]", ""));
        this.getContentPane().add(mainpanel, BorderLayout.CENTER);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField name = new JTextField(mod.getName());
        String DDot = ":";
        mainpanel.add(new JLabel("Name" + DDot), "");
        mainpanel.add(name, "growx");

        JTextField version = new JTextField(mod.getVersion());
        mainpanel.add(new JLabel("Version" + DDot), "");
        mainpanel.add(version, "growx");

        JTextField url = new JTextField(mod.getUrl());
        mainpanel.add(new JLabel("Link" + DDot), "");
        mainpanel.add(url, "span 3, growx");

        JTextField mcversion = new JTextField(mod.getMcVersionDependency());
        mainpanel.add(new JLabel("MC version" + DDot), "");
        mainpanel.add(mcversion, "growx");

        JTextField mode = new JTextField(String.valueOf(mod.getMode()));
        mainpanel.add(new JLabel("Mode" + DDot), "");
        mainpanel.add(mode, "growx");

        JTextField archive = new JTextField(String.valueOf(mod.getArchive()));
        mainpanel.add(new JLabel("Archive" + DDot), "");
        mainpanel.add(archive, "span 3, growx");

        {
            ETable dependencies = new ETable();
            ETableHeaders headers = new ETableHeaders();
            headers.add("name");
            headers.add("version");
            headers.add("url");
            dependencies.setHeaders(headers);
            for (Dependency element : mod.getDependencies()) {
                System.out.println(element);
                ETableRecordBean<Dependency> record = new ETableRecordBean<Dependency>(headers.getColumnNames(), element);
                dependencies.addRecord(record);
            }
            mainpanel.add(new JScrollPane(dependencies), "span 4 6, growx, growy");
        }

        {
            ETable resources = new ETable();
            ETableHeaders headers = new ETableHeaders();
            headers.add("sourcePath");
            headers.add("targetPath");
            headers.add("include");
            headers.add("exclude");
            resources.setHeaders(headers);
            for (Dependency element : mod.getDependencies()) {
                System.out.println(element);
                ETableRecordBean<Dependency> record = new ETableRecordBean<Dependency>(headers.getColumnNames(), element);
                resources.addRecord(record);
            }
            mainpanel.add(new JScrollPane(resources), "span 4 6, growx, growy");
        }
    }
}
