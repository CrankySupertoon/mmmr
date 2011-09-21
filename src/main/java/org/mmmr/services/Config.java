package org.mmmr.services;

import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.swing.ImageIcon;

/**
 * @author Jurgen
 */
public class Config {
    public static final Locale LOCALE;

    public static final NumberFormat NUMBER_FORMAT;

    public static final DateFormat DATE_FORMAT;

    static {
        Locale defaultLocale;
        try {
            // (1) Java 1.7 compilable in Java 1.6 but gives Exception at runtimee so we can fall back to (2)
            @SuppressWarnings("rawtypes")
            Class type = Class.forName("java.util.Locale$Category");
            @SuppressWarnings("unchecked")
            Object enumvalue = Enum.valueOf(type, "FORMAT");
            defaultLocale = Locale.class.cast(Locale.class.getMethod("getDefault", type).invoke(null, enumvalue));
        } catch (Exception ex) {
            // (2) Java 1.6 (gives wrong info in Java 1.7)
            defaultLocale = Locale.getDefault();
        }
        LOCALE = defaultLocale;
        NUMBER_FORMAT = NumberFormat.getNumberInstance(Config.LOCALE);
        DATE_FORMAT = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, Config.LOCALE);
    }

    private File backup;

    private File backupOriginalJar;

    private File cfg;

    private File data;

    private DBService db;

    private File dbdir;

    private Font font;

    private Font font18;

    private Font fontNarrow;

    private ImageIcon icon;

    private File libs;

    private File logs;

    private File mcBaseFolder;

    private File mcBin;

    private String mcCommandline;

    private File mcJar;

    private File mcJarBackup;

    private File mcJogboxBackup;

    private File mcMods;

    private File mcResources;

    private String mcVersion;

    private File mods;

    private Map<String, String> parameterValues;

    private Properties properties;

    private File thisFolder;

    private String shortTitle;

    private String title;

    private File tmp;

    private XmlService xml;

    public Config(String[] args, File thisFolder) throws IOException {
        this.thisFolder = thisFolder;

        this.icon = new ImageIcon(Config.class.getClassLoader().getResource("images/Minecraftx256.png"));
        this.mcVersion = "1.8.1";
        this.shortTitle = "Minecraft Mod Manager Reloaded";
        this.title = this.shortTitle + " 1.0b For Minecraft " + this.mcVersion;
        this.mcCommandline = "java.exe -Xms1024m -Xmx1024m -jar minecraft.jar";

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
        this.tmp = new File(System.getProperty("java.io.tmpdir"));
        this.tmp.deleteOnExit();
        this.logs = IOMethods.newDir(this.data, "logs");
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

    public Font getFontNarrow() {
        return this.fontNarrow;
    }

    public ImageIcon getIcon() {
        return this.icon;
    }

    public File getLibs() {
        return this.libs;
    }

    public File getLogs() {
        return this.logs;
    }

    public File getMcBaseFolder() {
        return this.mcBaseFolder;
    }

    public File getMcBin() {
        return this.mcBin;
    }

    public String getMcCommandline() {
        return this.mcCommandline;
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

    public String getMcVersion() {
        return this.mcVersion;
    }

    public File getMods() {
        return this.mods;
    }

    public String getParameterValue(String key) {
        return this.parameterValues.get(key);
    }

    public String getProperty(String key) throws IOException {
        return this.properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) throws IOException {
        String value = this.properties.getProperty(key);
        if (value == null) {
            value = this.setProperty(key, defaultValue);
        }
        return value;
    }

    public String getShortTitle() {
        return this.shortTitle;
    }

    public File getThisFolder() {
        return this.thisFolder;
    }

    public String getTitle() {
        return this.title;
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

    public void setFontNarrow(Font fontNarrow) {
        this.fontNarrow = fontNarrow;
    }

    public void setMcCommandline(String mcCommandline) {
        this.mcCommandline = mcCommandline;
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
