package org.mmmr.services;

import static org.mmmr.services.IOMethods.crc32File;
import static org.mmmr.services.IOMethods.selectFile;
import static org.mmmr.services.IOMethods.deleteDirectory;
import static org.mmmr.services.DynamicLoading.downloadURL;
import static org.mmmr.services.IOMethods.extract;
import static org.mmmr.services.IOMethods.fileEquals;
import static org.mmmr.services.IOMethods.list;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBException;

import org.mmmr.MC;
import org.mmmr.MCFile;
import org.mmmr.Mod;
import org.mmmr.ModCompilation;
import org.mmmr.Resource;

public class MMMR implements StartMe {
    private Config cfg;

    private DBService db;

    private XmlService x;

    private JFrame frame;

    private Container contentPane;

    private MC mc;

    @Override
    public void start(String[] args) throws Exception {
        frame = new JFrame();
        contentPane = frame.getContentPane();
        cfg = new Config(args, new File("DUMMY").getAbsoluteFile().getParentFile());
        contentPane.setLayout(new GridLayout(-1, 1));
        frame.setSize(200, 250);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);

        db = DBService.getInstance(cfg.getDbdir());
        x = new XmlService(cfg.getData());
        testXML(x, cfg.getMods());
        mc = db.getOrCreate(new MC("1.7.3"));

        stateChanged();
    }

    private void stateChanged() throws IOException {
        System.out.println("state changed");
        contentPane.setVisible(false);
        contentPane.removeAll();

        if (list(cfg.getMcBaseFolder()).size() == 0) {
            initalStart();
        } else if (cfg.getMcJar().exists() && cfg.getMcJar().isFile() && list(cfg.getBackupOriginalJar()).size() == 0) {
            minecraftBackup();
        } else if (!cfg.getMcJarBackup().exists() && !"true".equals(cfg.getProperty("jogbox.ignore", "?"))) {
            askToInstallJogBox();
        } else {
            afterAll();
        }

        contentPane.setVisible(true);
    }

    private void initalStart() {
        JButton b = new JButton("Minecraft not found:\nDownload Minecraft");
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initalStartAction();
            }
        });
        contentPane.add(b, null);
    }

    private void waitButton() {
        System.out.println("continue button");

        contentPane.setVisible(false);
        contentPane.removeAll();
        JButton b = new JButton("Continue");
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    stateChanged();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        contentPane.add(b, null);
        contentPane.setVisible(true);
    }

    private void initalStartAction() {
        try {
            downloadURL(new URL("https://s3.amazonaws.com/MinecraftDownload/launcher/Minecraft.exe"), new File(cfg.getThisFolder(), "Minecraft.exe"));
            downloadURL(new URL("https://s3.amazonaws.com/MinecraftDownload/launcher/minecraft.jar"), new File(cfg.getThisFolder(), "minecraft.jar"));
            JOptionPane.showMessageDialog(null, "Minecraft will now start up.\nLog in and let it download all files.");
            startMC();
            waitButton();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void minecraftBackup() {
        final JButton b = new JButton("Backup Minecraft");
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                minecraftBackupAction();
            }
        });
        contentPane.add(b, null);
    }

    private void minecraftBackupAction() {
        try {
            extract(cfg.getMcJar(), cfg.getBackupOriginalJar());

            if (!new File(cfg.getBackupOriginalJar(), "META-INF/MOJANG_C.SF").exists()) {
                deleteDirectory(cfg.getBackupOriginalJar());
                throw new RuntimeException("not a clean install"); // TODO show message
            }

            stateChanged();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void askToInstallJogBox() {
        {
            JButton b = new JButton("Install JogBox");
            b.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    askToInstallJogBoxAction1();
                }
            });
            contentPane.add(b, null);
        }
        {
            JButton b = new JButton("Do not install JogBox");
            b.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    askToInstallJogBoxAction2();
                }
            });
            contentPane.add(b, null);
        }
    }

    private void askToInstallJogBoxAction1() {
        try {
            File jbinstaller = selectFile(cfg.getThisFolder(), new javax.swing.filechooser.FileFilter() {
                @Override
                public boolean accept(File f) {
                    if (f.isDirectory())
                        return true;
                    String name = f.getName().toLowerCase();
                    if (!name.endsWith(".jar"))
                        return false;
                    return name.contains("yogbox") && name.contains("install") && !name.contains("uninstall");
                }

                @Override
                public String getDescription() {
                    return "YogBox Installer";
                }
            });

            if (jbinstaller == null) {
                stateChanged();
                return;
            }

            List<String> command = new ArrayList<String>();
            command.add("javaw.exe");
            command.add("-jar");
            command.add(jbinstaller.getAbsolutePath());
            System.out.println(command);
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.environment().put("AppData", cfg.getThisFolder().getAbsolutePath());
            pb.environment().put("APPDATA", cfg.getThisFolder().getAbsolutePath());
            @SuppressWarnings("unused")
            Process javap = pb.start();

            // if (cfg.getMcJarBackup().exists()) {
            // // jogbox installed
            // cfg.setProperty("jogbox.ignore", "false");
            //
            // File[] listFiles = cfg.getMinecraftJogboxBackup().listFiles();
            // if (listFiles == null || listFiles.length == 0) {
            // extract(cfg.getMcJar(), cfg.getMinecraftJogboxBackup());
            // removeOriginalFiles(cfg.getMinecraftJogboxBackup().getAbsolutePath().length() + 1, cfg.getMinecraftJogboxBackup(),
            // cfg.getBackupOriginalJar());
            // }
            //
            // removeOriginalFiles(cfg.getMinecraftJogboxBackup().getAbsolutePath().length() + 1, cfg.getMinecraftJogboxBackup(),
            // cfg.getBackupOriginalJar());
            //
            // ModCompilation jb = db.getOrCreate(new ModCompilation("YogBox", "1.1"));
            // if (jb.getId() == null) {
            // jb = initJogBox(cfg.getData());
            // db.save(jb);
            // }
            // File file = new File(cfg.getData(), "minecraft_jogbox_mods.xml");
            // if (!file.exists()) {
            // x.save(new FileOutputStream(file), jb);
            // }
            //
            // } else {
            // // jogbox not installed
            // cfg.setProperty("jogbox.ignore", "true");
            // }

            waitButton();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void askToInstallJogBoxAction2() {
        try {
            cfg.setProperty("jogbox.ignore", "true");

            stateChanged();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void afterAll() throws IOException {
        if (cfg.getMcJar().isFile()) {
            moveJarToDir();
        }
        if (mc.getId() == null) {
            addContents(mc, "bin/minecraft.jar/", cfg.getBackupOriginalJar().getAbsolutePath().length() + 1, cfg.getBackupOriginalJar());
            addContents(mc, cfg.getMcResources().getName() + "/", cfg.getMcResources().getAbsolutePath().length() + 1, cfg.getMcResources());
            db.save(mc);
        }

        contentPane.add(new JLabel("ready to use MMMR"), null);
    }

    private void moveJarToDir() throws IOException {
        File minecraftZip = new File(cfg.getMcJar().getAbsolutePath() + ".zip");
        cfg.getMcJar().renameTo(minecraftZip);
        cfg.getMcJar().mkdirs();
        extract(minecraftZip, cfg.getMcJar());
    }

    private void startMC() throws IOException {
        int xmx = (int) (1024 * 1.5);
        int xms = xmx;
        String commandline = "javaw.exe -Xms"
                + xms
                + "M -Xmx"
                + xmx
                + "m -client -XX:+UseConcMarkSweepGC -XX:+DisableExplicitGC -XX:+UseAdaptiveGCBoundary -XX:MaxGCPauseMillis=500 -XX:-UseGCOverheadLimit -XX:SurvivorRatio=12 -Xnoclassgc -XX:UseSSE=3 -Xincgc -jar "
                + "minecraft.jar";

        FileOutputStream out = new FileOutputStream(new File(cfg.getThisFolder(), "minecraft.bat"));
        out.write(("SET APPDATA=" + cfg.getThisFolder().getAbsolutePath() + "\r\n" + commandline + "\r\npause").getBytes());
        out.close();

        List<String> command = new ArrayList<String>(Arrays.asList(commandline.split(" ")));
        command.set(0, "C:/Program Files/Java/jre7/bin/" + command.get(0));
        System.out.println(command);
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.environment().put("AppData", cfg.getThisFolder().getAbsolutePath());
        pb.environment().put("APPDATA", cfg.getThisFolder().getAbsolutePath());
        @SuppressWarnings("unused")
        Process javap = pb.start();
    }

    private void testXML(XmlService x, File dir) throws FileNotFoundException, JAXBException {
        Mod mod = new Mod();
        mod.setArchive("[FileCopter]OptiFine_1.7.3_HD_MT_G2.zip");
        mod.setName("OptiFine_HD_MT_G2");
        mod.setVersion("1.7.3");
        mod.setUrl("http://www.minecraftforum.net/topic/249637-173-optifine-hd-g-fps-boost/");
        mod.addDepencency(mc);
        mod.addResource(new Resource("./", "bin/minecraft.jar"));
        x.save(new FileOutputStream(new File(dir, "[FileCopter]OptiFine_1.7.3_HD_MT_G2.zip.xml")), mod);
    }

    private void addContents(MC mc, String prefix, int pos, File fd) throws IOException {
        if (fd.isFile()) {
            mc.addFile(new MCFile(prefix + fd.getAbsolutePath().substring(pos), new Date(fd.lastModified()), crc32File(fd)));
        }
        for (File child : list(fd)) {
            addContents(mc, prefix, pos, child);
        }
    }

    private void removeOriginalFiles(int s, File source, File duplicateBase) throws IOException {
        if (source.isFile()) {
            String relativePath = source.getAbsolutePath().substring(s);
            File duplicate = new File(duplicateBase, relativePath);
            if (fileEquals(source, duplicate)) {
                System.out.println("delete backup duplicate " + relativePath);
                duplicate.delete();
            }

        }
        for (File child : list(source)) {
            removeOriginalFiles(s, child, duplicateBase);
        }
    }

    private ModCompilation initJogBox(File data) throws FileNotFoundException, JAXBException {
        ModCompilation yogbox = new ModCompilation("YogBox", "1.1");
        yogbox.setMc(mc);
        yogbox.addMod(new Mod("Single Player RPG Mod", "1.4a", "http://www.minecraftforum.net/topic/479017-173-single-player-rpg-mod-v14"));
        yogbox.addMod(new Mod("Millenaire", "1.3.1",
                "http://www.minecraftforum.net/topic/227822-173-millenaire-npc-village-132-large-pesant-house-indian-guard-towers-norman-fort/"));
        yogbox.addMod(new Mod("Somnia", "1.3",
                "http://www.minecraftforum.net/topic/162771-173-somnia-v13-sspmltfc-compatibility-fixes-for-dynamic-lights-and-seasons-mod/"));
        yogbox.addMod(new Mod("Rei's Minimap", "1.8", "http://www.minecraftforum.net/topic/482147-173-aug25-reis-minimap-v18/"));
        yogbox.addMod(new Mod("Elemental Creepers", "1.2.3", "http://www.minecraftforum.net/topic/498342-173-elemental-creepers-v13-smp-support/"));
        yogbox.addMod(new Mod("Inventory Tweaks", "1.22", "http://www.minecraftforum.net/topic/323444-173spsmp-inventory-tweaks-130-august-16/"));
        yogbox.addMod(new Mod("Convenient Inventory Mod", "1.9", "http://www.minecraftforum.net/topic/504091-v173-convenient-inventory-19/"));
        yogbox.addMod(new Mod("Doors open Doors tweak", "?",
                "http://forums.somethingawful.com/showthread.php?threadid=3383735&amp;userid=0&perpage=40&pagenumber=552#post393496394"));
        yogbox.addMod(new Mod("Proper Fence Behavior", "1.7.3", "http://www.minecraftforum.net/topic/404530-173proper-fence-behaviour-for-mobs/"));
        yogbox.addMod(new Mod("The Seasons Mod", "1.34", "http://www.minecraftforum.net/topic/499733-173134the-seasons-mod/"));
        yogbox.addMod(new Mod("Pfaeff's Mod", "0.8.3?",
                "http://www.minecraftforum.net/topic/200272-v173-pfaeffs-mod-ssp-v083-allocator-jump-pad-and-more/"));
        yogbox.addMod(new Mod("Mystic Mods: Mystic Ruins", "0.3",
                "http://www.minecraftforum.net/topic/303729-173-mystic-mods-dungeons-ruins-ores-vines-stones/"));
        yogbox.addMod(new Mod("Mystic Mods: Mystic Ores", "0.4",
                "http://www.minecraftforum.net/topic/303729-173-mystic-mods-dungeons-ruins-ores-vines-stones/"));
        yogbox.addMod(new Mod("Mystic Mods: Mystic Stones", "0.4?",
                "http://www.minecraftforum.net/topic/303729-173-mystic-mods-dungeons-ruins-ores-vines-stones/"));
        yogbox.addMod(new Mod("Baby Animals", "1.7.5", "http://www.minecraftforum.net/topic/190053-173-baby-animals-v175-mobml/"));
        yogbox.addMod(new Mod("Scokeev9's mods: BiomeWater", "3.0?",
                "http://www.minecraftforum.net/topic/228940-173scokeev9-premiumwood-v144-12-more-thanks-for-250000-dls/#water"));
        yogbox.addMod(new Mod("Scokeev9's mods: ScotTools API and Fix)", "10.1?",
                "http://www.minecraftforum.net/topic/228940-173scokeev9-premiumwood-v144-12-more-thanks-for-250000-dls/#tools"));
        yogbox.addMod(new Mod("Scokeev9's mods: Randomite)", "4.0",
                "http://www.minecraftforum.net/topic/228940-173scokeev9-premiumwood-v144-12-more-thanks-for-250000-dls/#randomite"));
        yogbox.addMod(new Mod("More Health", "7.5",
                "http://www.minecraftforum.net/topic/115172-v173-noheros-mods-more-health-v75-extras-chance-sword-v15/#health"));
        yogbox.addMod(new Mod("ModOptionsAPI", "0.7?", "http://www.minecraftforum.net/topic/191591-v173-moapi-modoptionsapi-textkey-options-07/"));
        yogbox.addMod(new Mod("GuiAPI", "0.10.4",
                "http://www.minecraftforum.net/topic/91544-173jul-8-lahwrans-mods-zanmini-worldeditgui-guiapi-no-more-adfly/#SettingsAPI"));
        yogbox.addMod(new Mod("Mo' Creatures", "2.12.2",
                "http://www.minecraftforum.net/topic/81771-v173-mo-creatures-v2122-with-kitties-mice-and-rats/"));
        yogbox.addMod(new Mod("DaftPVF's mods: Slime Armor", "?", "http://www.minecraftforum.net/topic/124117-v173-daftpvfs-mods/#slime_boots"));
        yogbox.addMod(new Mod("DaftPVF's mods: Score", "?", "http://www.minecraftforum.net/topic/124117-v173-daftpvfs-mods/#score"));
        yogbox.addMod(new Mod("DaftPVF's mods: Floating Ruins", "?", "http://www.minecraftforum.net/topic/124117-v173-daftpvfs-mods/#floating_ruins"));
        yogbox.addMod(new Mod("DaftPVF's mods: Starting Inventory", "?",
                "http://www.minecraftforum.net/topic/124117-v173-daftpvfs-mods/#starting_inventory"));
        yogbox.addMod(new Mod("DaftPVF's mods: Treecapitator", "?", "http://www.minecraftforum.net/topic/124117-v173-daftpvfs-mods/#treecapitator"));
        yogbox.addMod(new Mod("DaftPVF's mods: Crystal Wing", "?", "http://www.minecraftforum.net/topic/124117-v173-daftpvfs-mods/#crystal_wing"));
        yogbox.addMod(new Mod("DaftPVF's mods: Web Generation", "?", "http://www.minecraftforum.net/topic/124117-v173-daftpvfs-mods/#web_generation"));
        yogbox.addMod(new Mod("Risugami's Mods: ModLoader Beta", "1.7.3",
                "http://www.minecraftforum.net/topic/75440-v173-risugamis-mods-recipe-book-updated/"));
        yogbox.addMod(new Mod("Risugami's Mods: AudioMod Beta", "1.7_01",
                "http://www.minecraftforum.net/topic/75440-v173-risugamis-mods-recipe-book-updated/"));
        yogbox.addMod(new Mod("Risugami's Mods: Death Chest Beta", "1.7_01",
                "http://www.minecraftforum.net/topic/75440-v173-risugamis-mods-recipe-book-updated/"));
        yogbox.addMod(new Mod("Risugami's Mods: Shelf Beta", "1.7.3",
                "http://www.minecraftforum.net/topic/75440-v173-risugamis-mods-recipe-book-updated/"));
        yogbox.addMod(new Mod("Risugami's Mods: More Stackables Beta", "1.7_01",
                "http://www.minecraftforum.net/topic/75440-v173-risugamis-mods-recipe-book-updated/"));
        yogbox.addMod(new Mod("Risugami's Mods: Elemental Arrows Beta", "1.7.3",
                "http://www.minecraftforum.net/topic/75440-v173-risugamis-mods-recipe-book-updated/"));
        yogbox.addMod(new Mod("Risugami's Mods: Recipe Book Beta", "1.7.3",
                "http://www.minecraftforum.net/topic/75440-v173-risugamis-mods-recipe-book-updated/"));
        yogbox.addMod(new Mod("SteamCraft: Airship", "2.5.1?", "http://www.steam-craft.com/airship"));
        yogbox.addMod(new Mod("SteamCraft: Pirate", "1.7.3", "http://www.steam-craft.com/pirate"));
        yogbox.addMod(new Mod("Hogofwar's Bone Mods: Magical Bone Sticks", "?",
                "http://www.minecraftforum.net/topic/190980-173-hogofwars-bone-mods/#bone"));
        yogbox.addMod(new Mod("Hogofwar's Bone Mods: Bone Torch", "?", "http://www.minecraftforum.net/topic/190980-173-hogofwars-bone-mods/#torch"));
        yogbox.addMod(new Mod("Peaceful Pack", "3", "http://www.minecraftforum.net/topic/368468-173-peaceful-pack-v3/"));
        yogbox.addMod(new Mod("Biome Caves", "3?", "http://www.minecraftforum.net/topic/311162-173-biome-caves-ice-and-sand-dungeons-v3/"));
        yogbox.addMod(new Mod("Buried Treasures", "3.3?", "http://www.minecraftforum.net/topic/151762-v15-01-buried-treasures-33-thanks-ncrawler/"));
        yogbox.addMod(new Mod("Israphel", yogbox.getVersion(),
                "http://yogiverse.com/showthread.php?11841-YogBox-A-carefully-chosen-compilation-of-good-mods"));
        yogbox.addMod(new Mod("Ninja Ghost", yogbox.getVersion(),
                "http://yogiverse.com/showthread.php?11841-YogBox-A-carefully-chosen-compilation-of-good-mods"));
        yogbox.addMod(new Mod("Tree, Nether, Pyramid and Floating Dungeons", "4",
                "http://www.minecraftforum.net/topic/388302-173-tree-nether-pyramid-and-floating-dungeons-v4/"));

        return yogbox;
    }
}
//
// File minecraftOriginalBackup = newDir(backup, "original");
// if (minecraftOriginalBackup.listFiles() == null)
// extract(mcJarBackup, new File(minecraftOriginalBackup,
// "minecraft.jar"));
//
// File minecraftJogboxBackup = newDir(backup, "jogbox");
// if (minecraftJogboxBackup.listFiles() == null)
// extract(mcJar, new File(minecraftJogboxBackup, "minecraft.jar"));
//
// if (!new File(minecraftJogboxBackup,
// "minecraft_jogbox_jar_compare.log").exists()) {
// File p1 = new File(minecraftOriginalBackup, "minecraft.jar");
// File p2 = new File(minecraftJogboxBackup, "minecraft.jar");
// BufferedWriter p3 = new BufferedWriter(new FileWriter(new
// File(minecraftJogboxBackup,
// "minecraft_jogbox_jar_compare.log")));
// compare(p1, p2, p1, p2, p3);
// p3.flush();
// p3.close();
// }
// if (mcJar.isFile()) {
// File minecraftZip = new File(mcJar.getAbsolutePath() + ".zip");
// mcJar.renameTo(minecraftZip);
// mcJar.mkdirs();
// extract(minecraftZip, mcJar);
// }
//
// InstallationService is = new InstallationService();
// File modxml = new File(mods,
// "[FileCopter]OptiFine_1.7.3_HD_MT_G2.xml");
// Mod modToInstall = x.load(new FileInputStream(modxml),
// Mod.class);
// is.installMod(db, modToInstall, mods, tmp, mcBaseFolder);
// is.uninstallMod(db, modToInstall, mods, tmp, mcBaseFolder);