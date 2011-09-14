package org.mmmr.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;
import javax.xml.bind.JAXBException;

import org.mmmr.MC;
import org.mmmr.MCFile;
import org.mmmr.Mod;
import org.mmmr.ModPack;
import org.mmmr.Resource;

/**
 * @author Jurgen
 */
public class MMMR implements MMMRI {
    public static void writeMCBat(Config cfg) throws IOException {
	String commandline = cfg.getMcCommandline();
	{
	    FileOutputStream out = new FileOutputStream(new File(cfg.getThisFolder(), "start minecraft console.bat"));
	    out.write(("SET APPDATA=" + cfg.getThisFolder().getAbsolutePath() + "\r\n" + commandline + "\r\npause").getBytes());
	    out.close();
	}
	{
	    FileOutputStream out = new FileOutputStream(new File(cfg.getThisFolder(), "start minecraft no-console.bat"));
	    out.write(("SET APPDATA=" + cfg.getThisFolder().getAbsolutePath() + "\r\n" + commandline.replaceAll("java.exe", "javaw.exe")).getBytes());
	    out.close();
	}
    }

    private Config cfg;

    private MC mc;

    private StatusWindow statusWindow;

    private void addContents(MC mc, String prefix, int pos, File fd) throws IOException {
	if (fd.isFile()) {
	    mc.addFile(new MCFile(prefix + fd.getAbsolutePath().substring(pos), new Date(fd.lastModified()), IOMethods.crc32File(fd)));
	}
	for (File child : IOMethods.list(fd)) {
	    this.addContents(mc, prefix, pos, child);
	}
    }

    /**
     * returns true if needs reinstalling
     */
    private boolean checkMCInstall() {
	if (IOMethods.list(this.cfg.getMcBaseFolder()).size() == 0) {
	    return true;
	}
	if (!new File(this.cfg.getThisFolder(), "Minecraft.exe").exists()) {
	    return true;
	}
	if (!new File(this.cfg.getThisFolder(), "minecraft.jar").exists()) {
	    return true;
	}
	if (!new File(this.cfg.getMcBaseFolder(), "bin/minecraft.jar").exists()) {
	    return true;
	}
	if (!new File(this.cfg.getMcBaseFolder(), "bin/jinput.jar").exists()) {
	    return true;
	}
	if (!new File(this.cfg.getMcBaseFolder(), "bin/lwjgl.jar").exists()) {
	    return true;
	}
	if (!new File(this.cfg.getMcBaseFolder(), "bin/lwjgl_util.jar").exists()) {
	    return true;
	}

	return false;
    }

    private ModPack initJogBox(File data) throws FileNotFoundException, JAXBException {
	ModPack yogbox = new ModPack("YogBox", "1.1");
	yogbox.setDescription("YogBox_1.7.3_v1.1.zip");
	yogbox.setMcVersionDependency("1.7.3");

	yogbox.addMod(new Mod("Single Player RPG Mod", "1.4a", "http://www.minecraftforum.net/topic/479017-173-single-player-rpg-mod-v14", "bin/minecraft.jar/rpgmod/"));
	yogbox.addMod(new Mod("Millenaire", "1.3.1",
		"http://www.minecraftforum.net/topic/227822-173-millenaire-npc-village-132-large-pesant-house-indian-guard-towers-norman-fort/", "millenaire/"));
	yogbox.addMod(new Mod("Somnia", "1.3", "http://www.minecraftforum.net/topic/162771-173-somnia-v13-sspmltfc-compatibility-fixes-for-dynamic-lights-and-seasons-mod/",
		"bin/minecraft.jar/mod_Somnia.class"));
	yogbox.addMod(new Mod("Rei's Minimap", "1.8", "http://www.minecraftforum.net/topic/482147-173-aug25-reis-minimap-v18/", "mods/rei_minimap/"));
	yogbox.addMod(new Mod("Baby Animals", "1.7.5", "http://www.minecraftforum.net/topic/190053-173-baby-animals-v175-mobml/", "mods/BabyAnimals_v1_75.zip"));
	yogbox.addMod(new Mod("Mo' Creatures", "2.12.2", "http://www.minecraftforum.net/topic/81771-v173-mo-creatures-v2122-with-kitties-mice-and-rats/", "mods/mocreatures/"));
	yogbox.addMod(new Mod("More Health", "7.5", "http://www.minecraftforum.net/topic/115172-v173-noheros-mods-more-health-v75-extras-chance-sword-v15/#health",
		"mods/moreHealth/"));
	yogbox.addMod(new Mod("DaftPVF's mods: Web Generation", "?", "http://www.minecraftforum.net/topic/124117-v173-daftpvfs-mods/#web_generation", "mods/Web Generation.zip"));
	yogbox.addMod(new Mod("DaftPVF's mods: Treecapitator", "?", "http://www.minecraftforum.net/topic/124117-v173-daftpvfs-mods/#treecapitator", "mods/Treecapitator.zip"));
	yogbox.addMod(new Mod("DaftPVF's mods: Starting Inventory", "?", "http://www.minecraftforum.net/topic/124117-v173-daftpvfs-mods/#starting_inventory",
		"mods/Starting Inventory.zip"));
	yogbox.addMod(new Mod("Risugami's Mods: Shelf Beta", "1.7.3", "http://www.minecraftforum.net/topic/75440-v173-risugamis-mods-recipe-book-updated/", "mods/Shelf.zip"));
	yogbox.addMod(new Mod("Risugami's Mods: Recipe Book Beta", "1.7.3", "http://www.minecraftforum.net/topic/75440-v173-risugamis-mods-recipe-book-updated/",
		"mods/Recipe Book.zip"));
	yogbox.addMod(new Mod("Mystic Mods: Mystic Ruins", "0.3", "http://www.minecraftforum.net/topic/303729-173-mystic-mods-dungeons-ruins-ores-vines-stones/",
		"mods/Mystic_Ruins_v03.zip"));
	yogbox.addMod(new Mod("DaftPVF's mods: Floating Ruins", "?", "http://www.minecraftforum.net/topic/124117-v173-daftpvfs-mods/#floating_ruins", "mods/Floating Ruins.zip"));
	yogbox.addMod(new Mod("Elemental Creepers", "1.2.3", "http://www.minecraftforum.net/topic/498342-173-elemental-creepers-v13-smp-support/",
		"mods/ElementalCreepers_v1.2.3.zip"));
	yogbox.addMod(new Mod("DaftPVF's mods: Crystal Wing", "?", "http://www.minecraftforum.net/topic/124117-v173-daftpvfs-mods/#crystal_wing", "mods/Crystal Wing.zip"));
	yogbox.addMod(new Mod("Hogofwar's Bone Mods: Bone Torch", "?", "http://www.minecraftforum.net/topic/190980-173-hogofwars-bone-mods/#torch", "mods/BoneTorch.zip"));
	yogbox.addMod(new Mod("Hogofwar's Bone Mods: Magical Bone Sticks", "?", "http://www.minecraftforum.net/topic/190980-173-hogofwars-bone-mods/#bone", "mods/BoneSticks.zip"));
	yogbox.addMod(new Mod("Proper Fence Behavior", "1.7.3", "http://www.minecraftforum.net/topic/404530-173proper-fence-behaviour-for-mobs/",
		"bin/minecraft.jar/mod_FenceFix.class"));
	yogbox.addMod(new Mod("The Seasons Mod", "1.34", "http://www.minecraftforum.net/topic/499733-173134the-seasons-mod/", "bin/minecraft.jar/nandonalt/Seasons/"));
	yogbox.addMod(new Mod("Mystic Mods: Mystic Ores", "0.4", "http://www.minecraftforum.net/topic/303729-173-mystic-mods-dungeons-ruins-ores-vines-stones/",
		"bin/minecraft.jar/mysticores/"));
	yogbox.addMod(new Mod("Scokeev9's mods: Randomite)", "4.0",
		"http://www.minecraftforum.net/topic/228940-173scokeev9-premiumwood-v144-12-more-thanks-for-250000-dls/#randomite", "bin/minecraft.jar/scokeev/randomite.png"));
	yogbox.addMod(new Mod("DaftPVF's mods: Slime Armor", "?", "http://www.minecraftforum.net/topic/124117-v173-daftpvfs-mods/#slime_boots",
		"bin/minecraft.jar/daftpvf/slimeBoots.png"));
	yogbox.addMod(new Mod("DaftPVF's mods: Score", "?", "http://www.minecraftforum.net/topic/124117-v173-daftpvfs-mods/#score", "bin/minecraft.jar/mod_score.class"));
	yogbox.addMod(new Mod("Risugami's Mods: Elemental Arrows Beta", "1.7.3", "http://www.minecraftforum.net/topic/75440-v173-risugamis-mods-recipe-book-updated/",
		"bin/minecraft.jar/mod_Arrows.class"));
	yogbox.addMod(new Mod("Risugami's Mods: More Stackables Beta", "1.7_01", "http://www.minecraftforum.net/topic/75440-v173-risugamis-mods-recipe-book-updated/",
		"bin/minecraft.jar/mod_Stackables.class"));
	yogbox.addMod(new Mod("Risugami's Mods: ModLoader Beta", "1.7.3", "http://www.minecraftforum.net/topic/75440-v173-risugamis-mods-recipe-book-updated/",
		"bin/minecraft.jar/ModLoader.class"));
	yogbox.addMod(new Mod("SteamCraft: Airship", "2.5.1?", "http://www.steam-craft.com/airship", "mods/airshipV2.51.1.7.3.zip"));
	yogbox.addMod(new Mod("Peaceful Pack", "3", "http://www.minecraftforum.net/topic/368468-173-peaceful-pack-v3/", "bin/minecraft.jar/mod_PeacefulPack.class"));
	yogbox.addMod(new Mod("Inventory Tweaks", "1.22", "http://www.minecraftforum.net/topic/323444-173spsmp-inventory-tweaks-130-august-16/",
		"bin/minecraft.jar/mod_InvTweaks.class"));
	yogbox.addMod(new Mod("Convenient Inventory Mod", "1.9", "http://www.minecraftforum.net/topic/504091-v173-convenient-inventory-19/",
		"bin/minecraft.jar/ConvenientInventory.class"));
	yogbox.addMod(new Mod("Ninja Ghost", yogbox.getVersion(), "http://yogiverse.com/showthread.php?11841-YogBox-A-carefully-chosen-compilation-of-good-mods",
		"bin/minecraft.jar/mod_Ghost.class"));
	yogbox.addMod(new Mod("Israphel", yogbox.getVersion(), "http://yogiverse.com/showthread.php?11841-YogBox-A-carefully-chosen-compilation-of-good-mods",
		"bin/minecraft.jar/mod_Ifix.class"));
	yogbox.addMod(new Mod("Tree, Nether, Pyramid and Floating Dungeons", "4", "http://www.minecraftforum.net/topic/388302-173-tree-nether-pyramid-and-floating-dungeons-v4/",
		"bin/minecraft.jar/mod_netherdungeon.class"));
	yogbox.addMod(new Mod("Pfaeff's Mod", "0.8.3?", "http://www.minecraftforum.net/topic/200272-v173-pfaeffs-mod-ssp-v083-allocator-jump-pad-and-more/", "mods/Pfaeff.zip"));
	yogbox.addMod(new Mod("Scokeev9's mods: BiomeWater", "3.0?",
		"http://www.minecraftforum.net/topic/228940-173scokeev9-premiumwood-v144-12-more-thanks-for-250000-dls/#water", "bin/minecraft.jar/oh.class"));
	yogbox.addMod(new Mod("Scokeev9's mods: ScotTools API and Fix)", "10.1?",
		"http://www.minecraftforum.net/topic/228940-173scokeev9-premiumwood-v144-12-more-thanks-for-250000-dls/#tools", "bin/minecraft.jar/ScotTools.class"));
	yogbox.addMod(new Mod("ModOptionsAPI", "0.7?", "http://www.minecraftforum.net/topic/191591-v173-moapi-modoptionsapi-textkey-options-07/", "bin/minecraft.jar/modoptionsapi"));
	yogbox.addMod(new Mod("GuiAPI", "0.10.4", "http://www.minecraftforum.net/topic/91544-173jul-8-lahwrans-mods-zanmini-worldeditgui-guiapi-no-more-adfly/#SettingsAPI",
		"bin/minecraft.jar/WidgetClassicWindow.class"));
	yogbox.addMod(new Mod("Risugami's Mods: AudioMod Beta", "1.7_01", "http://www.minecraftforum.net/topic/75440-v173-risugamis-mods-recipe-book-updated/",
		"bin/minecraft.jar/ibxm/"));
	yogbox.addMod(new Mod("Risugami's Mods: Death Chest Beta", "1.7_01", "http://www.minecraftforum.net/topic/75440-v173-risugamis-mods-recipe-book-updated/"));// no check
	yogbox.addMod(new Mod("Biome Caves", "3?", "http://www.minecraftforum.net/topic/311162-173-biome-caves-ice-and-sand-dungeons-v3/", "bin/minecraft.jar/mod_Icecave.class"));

	// how to check if these mods are installed in the yogbox IF they are installed at all
	yogbox.addMod(new Mod("Buried Treasures", "3.3?", "http://www.minecraftforum.net/topic/151762-v15-01-buried-treasures-33-thanks-ncrawler/"));
	yogbox.addMod(new Mod("Doors open Doors tweak", "?",
		"http://forums.somethingawful.com/showthread.php?threadid=3383735&amp;userid=0&perpage=40&pagenumber=552#post393496394"));
	yogbox.addMod(new Mod("Mystic Mods: Mystic Stones", "0.4?", "http://www.minecraftforum.net/topic/303729-173-mystic-mods-dungeons-ruins-ores-vines-stones/"));
	yogbox.addMod(new Mod("SteamCraft: Pirate", "1.7.3", "http://www.steam-craft.com/pirate"));

	return yogbox;
    }

    private void removeOriginalFiles(int s, File source, File duplicateBase) throws IOException {
	if (source.isFile()) {
	    String relativePath = source.getAbsolutePath().substring(s);
	    File duplicate = new File(duplicateBase, relativePath);
	    if (IOMethods.fileEquals(source, duplicate)) {
		// ("delete backup duplicate " + relativePath);
		source.delete();
	    }

	}
	for (File child : IOMethods.list(source)) {
	    this.removeOriginalFiles(s, child, duplicateBase);
	}
    }

    @Override
    public void setCfg(Config cfg) {
	this.cfg = cfg;
    }

    @Override
    public void setStatusWindow(StatusWindow statusWindow) {
	this.statusWindow = statusWindow;
    }

    @Override
    public void start(String[] args) throws Exception {
	DBService db;

	try {
	    this.statusWindow.getDbstatus().setStatus("Database and Hibernate: starting", null);
	    db = DBService.getInstance(this.cfg);
	    this.cfg.setDb(db);
	    this.mc = db.getOrCreate(new MC(this.cfg.getMcVersion()));
	    this.statusWindow.getDbstatus().setStatus("Database and Hibernate: ready", true);
	} catch (Exception e) {
	    this.statusWindow.getDbstatus().setStatus("Database and Hibernate: starting failed", false);
	    throw e;
	}

	XmlService xml;

	try {
	    this.statusWindow.getXmlstatus().setStatus("XML service: starting", null);
	    xml = new XmlService(this.cfg);
	    this.cfg.setXml(xml);
	    this.statusWindow.getXmlstatus().setStatus("XML service: ready", true);
	} catch (Exception e) {
	    this.statusWindow.getXmlstatus().setStatus("XML service: starting failed", false);
	    throw e;
	}

	boolean allSuccess = false;

	while (!allSuccess) {
	    boolean mccheck = false;
	    String error = "";

	    if (this.checkMCInstall()) {
		try {
		    String url = "https://s3.amazonaws.com/MinecraftDownload/launcher/";
		    String[] files = { "Minecraft.exe", "minecraft.jar", "Minecraft_Server.exe", "minecraft_server.jar" };
		    int i = 0;
		    for (; i < files.length; i++) {
			this.statusWindow.getMcstatus().setStatus("Minecraft: downloading " + files[i], null);
			try {
			    error = "downloading " + files[i];
			    IOMethods.downloadURL(new URL(url + files[i]), new File(this.cfg.getThisFolder(), files[i]));
			    error = "";
			} catch (Exception e) {
			    throw e;
			}
		    }
		    this.statusWindow.getMcstatus().setStatus("Minecraft: downloaded", null);
		    JOptionPane.showMessageDialog(FancySwing.getCurrentFrame(), "Minecraft will now start up.\nLog in and let it update all files.\nClick Ok to start Minecraft.",
			    "", JOptionPane.INFORMATION_MESSAGE, this.cfg.getIcon());
		    this.startMC(true);
		    MMMR.writeMCBat(this.cfg);
		    if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(FancySwing.getCurrentFrame(), "After you started Minecraft once.\nDid Minecraft run properly?", "",
			    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, this.cfg.getIcon())) {
			error = "installing and running Minecraft";
			IOMethods.deleteDirectory(this.cfg.getMcBaseFolder());
			for (i = 0; i < files.length; i++) {
			    new File(this.cfg.getThisFolder(), files[i]).delete();
			}
			throw new IOException("first run failed");
		    }
		    ArchiveService.extract(this.cfg.getMcJar(), this.cfg.getBackupOriginalJar());
		    if (!new File(this.cfg.getBackupOriginalJar(), "META-INF/MOJANG_C.SF").exists()) {
			IOMethods.deleteDirectory(this.cfg.getBackupOriginalJar());
			throw new RuntimeException("not a clean install");
		    }
		    this.statusWindow.getMcstatus().setStatus("Minecraft: ready", mccheck = true);
		} catch (Exception e) {
		    this.statusWindow.getMcstatus().setStatus("Minecraft: initialization failed", mccheck = false);
		}
	    } else {
		this.statusWindow.getMcstatus().setStatus("Minecraft: ready", mccheck = true);
	    }

	    boolean ybcheck = false;

	    if (mccheck) {
		if (!this.cfg.getMcJarBackup().exists() && !"true".equals(this.cfg.getProperty("jogbox.ignore", "?"))) {
		    if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(FancySwing.getCurrentFrame(),
			    "Do you want to install YogBox?\nYou need to have it downloaded already.", "YogBox", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
			    this.cfg.getIcon())) {
			try {
			    File jbinstaller = IOMethods.selectFile(this.cfg.getThisFolder(), new javax.swing.filechooser.FileFilter() {
				@Override
				public boolean accept(File f) {
				    if (f.isDirectory()) {
					return true;
				    }
				    String name = f.getName().toLowerCase();
				    if (!name.endsWith(".jar")) {
					return false;
				    }
				    return name.contains("yogbox") && name.contains("install") && !name.contains("uninstall");
				}

				@Override
				public String getDescription() {
				    return "YogBox Installer";
				}
			    });

			    if (jbinstaller == null) {
				throw new IOException("no file selected");
			    }

			    List<String> command = new ArrayList<String>();
			    command.add("javaw.exe");
			    command.add("-jar");
			    command.add(jbinstaller.getAbsolutePath());
			    ExceptionAndLogHandler.log(command);
			    ProcessBuilder pb = new ProcessBuilder(command);
			    pb.environment().put("AppData", this.cfg.getThisFolder().getAbsolutePath());
			    pb.environment().put("APPDATA", this.cfg.getThisFolder().getAbsolutePath());
			    pb.start();

			    JOptionPane.showMessageDialog(FancySwing.getCurrentFrame(), "After you installed the YogBox.\nContinue.", "", JOptionPane.INFORMATION_MESSAGE,
				    this.cfg.getIcon());

			    ArchiveService.extract(this.cfg.getMcJar(), this.cfg.getMcJogboxBackup());
			    this.removeOriginalFiles(this.cfg.getMcJogboxBackup().getAbsolutePath().length() + 1, this.cfg.getMcJogboxBackup(), this.cfg.getBackupOriginalJar());

			    ModPack jb = db.getOrCreate(new ModPack("YogBox", "1.1"));
			    jb = this.initJogBox(this.cfg.getData());
			    db.save(jb);

			    File file = new File(this.cfg.getMods(), "YogBox_1.7.3_v1.1.zip.xml");
			    xml.save(new FileOutputStream(file), jb);

			    jb.setInstallationDate(new Date());
			    db.save(jb);
			    this.statusWindow.getYbstatus().setStatus("YogBox: ready", ybcheck = true);
			    this.cfg.setProperty("jogbox.ignore", "false");
			} catch (Exception e) {
			    this.statusWindow.getYbstatus().setStatus("YogBox: failed", ybcheck = false);
			}
		    } else {
			this.statusWindow.getYbstatus().setStatus("YogBox: not installed", ybcheck = true);
			this.cfg.setProperty("jogbox.ignore", "true");
		    }
		} else {
		    if (!"true".equals(this.cfg.getProperty("jogbox.ignore", "?"))) {
			this.statusWindow.getYbstatus().setStatus("YogBox: ready", ybcheck = true);
		    } else {
			this.statusWindow.getYbstatus().setStatus("YogBox: not installed", ybcheck = true);
		    }
		}
	    }

	    if (ybcheck) {
		if (this.cfg.getMcJar().isFile()) {
		    File minecraftZip = new File(this.cfg.getMcJar().getAbsolutePath() + ".zip");
		    this.cfg.getMcJar().renameTo(minecraftZip);
		    this.cfg.getMcJar().mkdirs();
		    ArchiveService.extract(minecraftZip, this.cfg.getMcJar());
		}
		if ((this.mc.getFiles() == null) || (this.mc.getFiles().size() == 0)) {
		    this.addContents(this.mc, "bin/minecraft.jar/", this.cfg.getBackupOriginalJar().getAbsolutePath().length() + 1, this.cfg.getBackupOriginalJar());
		    this.addContents(this.mc, this.cfg.getMcResources().getName() + "/", this.cfg.getMcResources().getAbsolutePath().length() + 1, this.cfg.getMcResources());
		    db.save(this.mc);
		}
		if (!"true".equals(this.cfg.getProperty("jogbox.ignore", "?"))) {
		    ModPack jb = db.get(new ModPack("YogBox", "1.1"));
		    if (jb != null) {
			for (Mod mod : jb.getMods()) {
			    Date now = new Date();
			    if (mod.getResourceCheck() == null) {
				mod.setInstallationDate(now);
			    } else {
				File file = new File(this.cfg.getMcBaseFolder(), mod.getResourceCheck());
				if (file.exists()) {
				    mod.setInstallationDate(now);
				} else {
				    ExceptionAndLogHandler.log("mod not installed: " + mod);
				}
			    }
			}
			if ((jb.getResources() == null) || (jb.getResources().size() == 0)) {
			    Resource resource = new Resource("*", "*");
			    jb.addResource(resource);
			    int pos = this.cfg.getMcJogboxBackup().getAbsolutePath().length() + 1;
			    for (File file : IOMethods.listRecursive(this.cfg.getMcJogboxBackup())) {
				if (file.isDirectory()) {
				    continue;
				}
				String path = "bin/minecraft.jar/" + file.getAbsolutePath().substring(pos);
				resource.addFile(new MCFile(path, new Date(file.lastModified()), IOMethods.crc32File(file)));
			    }
			}
			db.save(jb);
		    }
		}
	    }

	    allSuccess = mccheck && ybcheck;

	    if (!allSuccess) {
		if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(FancySwing.getCurrentFrame(), "Error during: " + error + "\nDo you want to try again?", "Error",
			JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, this.cfg.getIcon())) {
		    System.exit(0);
		}
	    } else {
		this.statusWindow.setReadyToGoOn();
	    }
	}
    }

    private void startMC(boolean plain) throws IOException {
	String commandline;
	if (plain) {
	    commandline = "javaw.exe -Xms1024m -Xmx1024m -jar minecraft.jar";
	} else {
	    commandline = this.cfg.getMcCommandline();
	}

	List<String> command = new ArrayList<String>(Arrays.asList(commandline.split(" ")));
	command.set(0, command.get(0));
	ExceptionAndLogHandler.log(command);
	ProcessBuilder pb = new ProcessBuilder(command);
	pb.environment().put("AppData", this.cfg.getThisFolder().getAbsolutePath());
	pb.environment().put("APPDATA", this.cfg.getThisFolder().getAbsolutePath());
	@SuppressWarnings("unused")
	Process javap = pb.start();
    }
}
