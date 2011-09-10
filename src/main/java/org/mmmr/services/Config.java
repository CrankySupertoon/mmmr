package org.mmmr.services;

import static org.mmmr.services.IOMethods.newDir;
import static org.mmmr.services.IOMethods.parseParams;

import java.awt.Font;
import java.io.File;
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

    private File mcMods;

    private File mcResources;

    private File mcJogboxBackup;

    private File mods;

    private Map<String, String> parameterValues;

    private Properties properties;

    private File thisFolder;

    private File tmp;

    private XmlService xml;

    public Config(String[] args, File thisFolder) throws IOException {
	this.thisFolder = thisFolder;

	parameterValues = parseParams(args);

	// not used anymore: mcBaseFolder = new File(System.getenv("APPDATA"), ".minecraft");
	// we use a locally installed minecraft so you can mod at your heart's content
	mcBaseFolder = new File(thisFolder, ".minecraft");

	mcBin = new File(mcBaseFolder, "bin");
	mcMods = new File(mcBaseFolder, "mods");
	mcResources = new File(mcBaseFolder, "resources");
	mcJar = new File(mcBin, "minecraft.jar");
	mcJarBackup = new File(mcBin, "minecraft.jar.backup");

	data = newDir(thisFolder, "data");

	cfg = newDir(data, "cfg");
	properties = new Properties();
	properties.store(new FileOutputStream(new File(cfg, "config.properties")), null);

	backup = newDir(data, "backup");
	mods = newDir(data, "mods");
	libs = newDir(data, "libs");
	tmp = newDir(data, "tmp");
	dbdir = new File(data, "db");
	backupOriginalJar = newDir(backup, "minecraft.jar");
	mcJogboxBackup = newDir(backup, "jogbox");
    }

    public File getBackup() {
	return this.backup;
    }

    public File getBackupOriginalJar() {
	return backupOriginalJar;
    }

    public File getCfg() {
	return cfg;
    }

    public File getData() {
	return this.data;
    }

    public DBService getDb() {
	return db;
    }

    public File getDbdir() {
	return this.dbdir;
    }

    public Font getFont() {
	return font;
    }

    public Font getFont18() {
	return font18;
    }

    public File getLibs() {
	return libs;
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

    public File getMcMods() {
	return this.mcMods;
    }

    public File getMcResources() {
	return this.mcResources;
    }

    public File getMcJogboxBackup() {
	return mcJogboxBackup;
    }

    public File getMods() {
	return this.mods;
    }

    public Map<String, String> getParameterValues() {
	return this.parameterValues;
    }

    public String getProperty(String key, String defaultValue) throws IOException {
	String value = properties.getProperty(key);
	if (value == null) {
	    value = setProperty(key, defaultValue);
	}
	return value;
    }

    public File getThisFolder() {
	return thisFolder;
    }

    public File getTmp() {
	return this.tmp;
    }

    public XmlService getXml() {
	return xml;
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
	properties.put(key, value);
	properties.store(new FileOutputStream(new File(cfg, "config.properties")), null);
	return value;
    }

    public void setXml(XmlService xml) {
	this.xml = xml;
    }
}
