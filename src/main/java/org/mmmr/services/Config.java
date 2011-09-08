package org.mmmr.services;

import static org.mmmr.services.IOMethods.is64Bit;
import static org.mmmr.services.IOMethods.newDir;
import static org.mmmr.services.IOMethods.parseParams;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class Config {
    private boolean bit64;

    private Map<String, String> parameterValues;

    private File mcBaseFolder;

    private File mcBin;

    private File mcMods;

    private File mcResources;

    private File mcJar;

    private File mcJarBackup;

    private File data;

    private File backup;

    private File mods;

    private File tmp;

    private File dbdir;

    private Properties properties;

    private File cfg;

    private File thisFolder;

    private File backupOriginalJar;

    private File minecraftJogboxBackup;

    public Config(String[] args, File thisFolder) throws IOException {
        this.thisFolder = thisFolder;

        bit64 = is64Bit();

        parameterValues = parseParams(args);

        mcBaseFolder = new File(thisFolder, ".minecraft");
        // mcBaseFolder = new File(System.getenv("APPDATA"), ".minecraft");

        mcBin = new File(mcBaseFolder, "bin");
        mcMods = new File(mcBaseFolder, "mods");
        mcResources = new File(mcBaseFolder, "resources");
        mcJar = new File(mcBin, "minecraft.jar");
        mcJarBackup = new File(mcBin, "minecraft.jar.backup");

        cfg = newDir(data, "cfg");
        properties = new Properties();
        properties.store(new FileOutputStream(new File(cfg, "config.properties")), null);

        data = newDir(thisFolder, "data");
        backup = newDir(data, "backup");
        mods = newDir(data, "mods");
        tmp = newDir(data, "tmp");
        dbdir = new File(data, "db");
        backupOriginalJar = newDir(backup, "minecraft.jar");
        minecraftJogboxBackup = newDir(backup, "jogbox");
    }

    public File getMinecraftJogboxBackup() {
        return minecraftJogboxBackup;
    }

    public File getBackupOriginalJar() {
        return backupOriginalJar;
    }

    public boolean isBit64() {
        return this.bit64;
    }

    public Map<String, String> getParameterValues() {
        return this.parameterValues;
    }

    public File getMcBaseFolder() {
        return this.mcBaseFolder;
    }

    public File getMcBin() {
        return this.mcBin;
    }

    public File getMcMods() {
        return this.mcMods;
    }

    public File getMcResources() {
        return this.mcResources;
    }

    public File getMcJar() {
        return this.mcJar;
    }

    public File getMcJarBackup() {
        return this.mcJarBackup;
    }

    public File getData() {
        return this.data;
    }

    public File getBackup() {
        return this.backup;
    }

    public File getMods() {
        return this.mods;
    }

    public File getTmp() {
        return this.tmp;
    }

    public File getDbdir() {
        return this.dbdir;
    }

    public String getProperty(String key, String defaultValue) throws IOException {
        String value = properties.getProperty(key);
        if (value == null) {
            value = setProperty(key, defaultValue);
        }
        return value;
    }

    public String setProperty(String key, String value) throws IOException {
        properties.put(key, value);
        properties.store(new FileOutputStream(new File(cfg, "config.properties")), null);
        return value;
    }

    public File getThisFolder() {
        return thisFolder;
    }
}
