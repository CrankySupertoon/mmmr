package org.mmmr.services;

import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * @author Jurgen
 */
public class Config {
    private File backup;

    private File backupOriginalJar;

    private File cfg;

    private File data;

    private DBService db;

    private File dbdir;

    private Font font;

    private Font font18;

    private File libs;

    private File mcBaseFolder;

    private File mcBin;

    private File mcJar;

    private File mcJarBackup;

    private File mcJogboxBackup;

    private File mcMods;

    private File mcResources;

    private File mods;

    private Map<String, String> parameterValues;

    private Properties properties;

    private File thisFolder;

    private File tmp;

    private XmlService xml;
    
    private String title = "Minecraft Mod Manager Reloaded 1.0b For Minecraft 1.7.3b";

    public String getTitle() {
        return title;
    }

    public Config(String[] args, File thisFolder) throws IOException {
	this.thisFolder = thisFolder;

	this.parameterValues = IOMethods.parseParams(args);

	// not used anymore: mcBaseFolder = new File(System.getenv("APPDATA"), ".minecraft");
	// we use a locally installed minecraft so you can mod at your heart's content
	this.mcBaseFolder = new File(thisFolder, ".minecraft");

	this.mcBin = new File(this.mcBaseFolder, "bin");
	this.mcMods = new File(this.mcBaseFolder, "mods");
	this.mcResources = new File(this.mcBaseFolder, "resources");
	this.mcJar = new File(this.mcBin, "minecraft.jar");
	this.mcJarBackup = new File(this.mcBin, "minecraft.jar.backup");

	this.data = IOMethods.newDir(thisFolder, "data");

	this.cfg = IOMethods.newDir(this.data, "cfg");
	this.properties = new Properties();
	File file = new File(this.cfg, "config.properties");
	if (file.exists()) {
	    this.properties.load(new FileInputStream(file));
	}
	this.backup = IOMethods.newDir(this.data, "backup");
	this.mods = IOMethods.newDir(this.data, "mods");
	this.libs = IOMethods.newDir(this.data, "libs");
	this.tmp = IOMethods.newDir(this.data, "tmp");
	this.dbdir = new File(this.data, "db");
	this.backupOriginalJar = IOMethods.newDir(this.backup, "minecraft.jar");
	this.mcJogboxBackup = IOMethods.newDir(this.backup, "jogbox");
    }

    public File getBackup() {
	return this.backup;
    }

    public File getBackupOriginalJar() {
	return this.backupOriginalJar;
    }

    public File getCfg() {
	return this.cfg;
    }

    public File getData() {
	return this.data;
    }

    public DBService getDb() {
	return this.db;
    }

    public File getDbdir() {
	return this.dbdir;
    }

    public Font getFont() {
	return this.font;
    }

    public Font getFont18() {
	return this.font18;
    }

    public File getLibs() {
	return this.libs;
    }

    public File getMcBaseFolder() {
	return this.mcBaseFolder;
    }

    public File getMcBin() {
	return this.mcBin;
    }

    public File getMcJar() {
	return this.mcJar;
    }

    public File getMcJarBackup() {
	return this.mcJarBackup;
    }

    public File getMcJogboxBackup() {
	return this.mcJogboxBackup;
    }

    public File getMcMods() {
	return this.mcMods;
    }

    public File getMcResources() {
	return this.mcResources;
    }

    public File getMods() {
	return this.mods;
    }

    public Map<String, String> getParameterValues() {
	return this.parameterValues;
    }

    public String getProperty(String key, String defaultValue) throws IOException {
	String value = this.properties.getProperty(key);
	if (value == null) {
	    value = this.setProperty(key, defaultValue);
	}
	return value;
    }

    public File getThisFolder() {
	return this.thisFolder;
    }

    public File getTmp() {
	return this.tmp;
    }

    public XmlService getXml() {
	return this.xml;
    }

    public void setDb(DBService db) {
	this.db = db;
    }

    public void setFont(Font font) {
	this.font = font;
    }

    public void setFont18(Font font18) {
	this.font18 = font18;
    }

    public String setProperty(String key, String value) throws IOException {
	this.properties.put(key, value);
	this.properties.store(new FileOutputStream(new File(this.cfg, "config.properties")), null);
	return value;
    }

    public void setXml(XmlService xml) {
	this.xml = xml;
    }
}
