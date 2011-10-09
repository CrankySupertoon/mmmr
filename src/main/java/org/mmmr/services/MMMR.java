package org.mmmr.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.mmmr.MC;
import org.mmmr.MCFile;
import org.mmmr.Mod;
import org.mmmr.ModPack;
import org.mmmr.Resource;
import org.mmmr.services.swing.StatusWindow;

/**
 * @author Jurgen
 */
public class MMMR implements MMMRI {
    public static void writeMCBat(Config cfg) throws IOException {
        {
            String commandline = cfg.getMcCommandline();
            {
                FileOutputStream out = new FileOutputStream(new File(cfg.getThisFolder(), "start minecraft console.bat")); //$NON-NLS-1$
                out.write(("SET APPDATA=" + cfg.getClientFolder().getAbsolutePath() + "\r\n" + //$NON-NLS-1$ //$NON-NLS-2$ 
                        commandline + "\r\npause").getBytes() //$NON-NLS-1$ 
                );
                out.close();
            }
            {
                FileOutputStream out = new FileOutputStream(new File(cfg.getThisFolder(), "start minecraft no-console.bat")); //$NON-NLS-1$
                out.write(("SET APPDATA=" + cfg.getClientFolder().getAbsolutePath() + "\r\n" + //$NON-NLS-1$ //$NON-NLS-2$ 
                commandline.replaceAll("java.exe", "javaw.exe") //$NON-NLS-1$ //$NON-NLS-2$ 
                ).getBytes());
                out.close();
            }
        }
        {
            String commandline = cfg.getMcServerCommandline();
            {
                FileOutputStream out = new FileOutputStream(new File(cfg.getThisFolder(), "start minecraft server console.bat")); //$NON-NLS-1$
                out.write(("SET APPDATA=" + cfg.getServerFolder().getAbsolutePath() + "\r\n" + //$NON-NLS-1$//$NON-NLS-2$
                        "cd " + UtilityMethods.getDrive(cfg.getServerFolder()) + "\r\n" + //$NON-NLS-1$//$NON-NLS-2$
                        "cd " + cfg.getServerFolder().getAbsolutePath() + "\r\n" + //$NON-NLS-1$//$NON-NLS-2$
                        commandline + "\r\n" + //$NON-NLS-1$
                "pause" //$NON-NLS-1$
                ).getBytes());
                out.close();
            }
            {
                FileOutputStream out = new FileOutputStream(new File(cfg.getThisFolder(), "start minecraft server no-console.bat")); //$NON-NLS-1$
                out.write(("SET APPDATA=" + cfg.getServerFolder().getAbsolutePath() + "\r\n" + //$NON-NLS-1$//$NON-NLS-2$
                        "cd " + UtilityMethods.getDrive(cfg.getServerFolder()) + "\r\n" + //$NON-NLS-1$//$NON-NLS-2$
                        "cd " + cfg.getServerFolder().getAbsolutePath() + "\r\n" + //$NON-NLS-1$//$NON-NLS-2$
                        commandline.replaceAll("java.exe", "javaw.exe") + "\r\n" + //$NON-NLS-1$
                "pause" //$NON-NLS-1$
                ).getBytes());
                out.close();
            }
        }
        {
            String commandline = cfg.getMcBukkitCommandline();
            {
                FileOutputStream out = new FileOutputStream(new File(cfg.getThisFolder(), "start minecraft bukkit server console.bat")); //$NON-NLS-1$
                out.write(("SET APPDATA=" + cfg.getServerFolder().getAbsolutePath() + "\r\n" + //$NON-NLS-1$//$NON-NLS-2$
                        "cd " + UtilityMethods.getDrive(cfg.getServerFolder()) + "\r\n" + //$NON-NLS-1$//$NON-NLS-2$
                        "cd " + cfg.getServerFolder().getAbsolutePath() + "\r\n" + //$NON-NLS-1$//$NON-NLS-2$
                        commandline + "\r\n" + //$NON-NLS-1$
                "pause" //$NON-NLS-1$
                ).getBytes());
                out.close();
            }
            {
                FileOutputStream out = new FileOutputStream(new File(cfg.getThisFolder(), "start minecraft bukkit server no-console.bat")); //$NON-NLS-1$
                out.write(("SET APPDATA=" + cfg.getServerFolder().getAbsolutePath() + "\r\n" + //$NON-NLS-1$//$NON-NLS-2$
                        "cd " + UtilityMethods.getDrive(cfg.getServerFolder()) + "\r\n" + //$NON-NLS-1$//$NON-NLS-2$
                        "cd " + cfg.getServerFolder().getAbsolutePath() + "\r\n" + //$NON-NLS-1$//$NON-NLS-2$
                        commandline.replaceAll("java.exe", "javaw.exe") + "\r\n" + //$NON-NLS-1$
                "pause" //$NON-NLS-1$
                ).getBytes());
                out.close();
            }
        }
    }

    private Config cfg;

    private MC mc;

    private StatusWindow statusWindow;

    private void addContents(String prefix, int pos, File fd) throws IOException {
        if (fd.isFile()) {
            this.mc.addFile(new MCFile(prefix + fd.getAbsolutePath().substring(pos), new Date(fd.lastModified()), UtilityMethods.crc32File(fd)));
        }
        for (File child : UtilityMethods.list(fd)) {
            this.addContents(prefix, pos, child);
        }
    }

    /**
     * returns true if needs reinstalling
     */
    private boolean checkMCInstall() {
        if (UtilityMethods.list(this.cfg.getMcBaseFolder()).size() == 0) {
            return true;
        }
        if (!new File(this.cfg.getClientFolder(), "minecraft.jar").exists()) { //$NON-NLS-1$
            return true;
        }
        if (!new File(this.cfg.getServerFolder(), "minecraft_server.jar").exists()) { //$NON-NLS-1$
            return true;
        }
        if (!new File(this.cfg.getMcBaseFolder(), "bin/minecraft.jar").exists()) { //$NON-NLS-1$
            return true;
        }

        return false;
    }

    private void removeOriginalFiles(int s, File source, File duplicateBase) throws IOException {
        if (source.isFile()) {
            String relativePath = source.getAbsolutePath().substring(s);
            File duplicate = new File(duplicateBase, relativePath);
            if (UtilityMethods.fileEquals(source, duplicate)) {
                // ("delete backup duplicate " + relativePath);
                source.delete();
            }

        }
        for (File child : UtilityMethods.list(source)) {
            this.removeOriginalFiles(s, child, duplicateBase);
        }
    }

    /**
     * 
     * @see org.mmmr.services.MMMRI#setCfg(org.mmmr.services.Config)
     */
    @Override
    public void setCfg(Config cfg) {
        this.cfg = cfg;
    }

    /**
     * 
     * @see org.mmmr.services.MMMRI#setStatusWindow(org.mmmr.services.swing.StatusWindow)
     */
    @Override
    public void setStatusWindow(StatusWindow statusWindow) {
        this.statusWindow = statusWindow;
    }

    /**
     * 
     * @see org.mmmr.services.MMMRI#start(java.lang.String[])
     */
    @Override
    public void start(String[] args) throws Exception {
        DBService db;

        String dbhib = Messages.getString("MMMR.db_hibernate"); //$NON-NLS-1$
        String starting = Messages.getString("MMMR.starting"); //$NON-NLS-1$
        String ready = Messages.getString("MMMR.ready"); //$NON-NLS-1$
        String startfailed = Messages.getString("MMMR.start_failed"); //$NON-NLS-1$
        String notinstalled = Messages.getString("MMMR.not_installed"); //$NON-NLS-1$
        String xmlser = Messages.getString("MMMR.xml_service"); //$NON-NLS-1$
        String downloading = Messages.getString("MMMR.downloaded"); //$NON-NLS-1$
        String initfailed = Messages.getString("MMMR.init_failed"); //$NON-NLS-1$
        String failed = Messages.getString("MMMR.failed"); //$NON-NLS-1$

        try {
            this.statusWindow.getDbstatus().setStatus(dbhib + ": " + starting, null);//$NON-NLS-1$
            db = DBService.getInstance(this.cfg);
            this.cfg.setDb(db);
            this.mc = db.getOrCreate(new MC(this.cfg.getMcVersion()));
            this.statusWindow.getDbstatus().setStatus(dbhib + ": " + ready, true);//$NON-NLS-1$
        } catch (Exception e) {
            this.statusWindow.getDbstatus().setStatus(dbhib + ": " + startfailed, false);//$NON-NLS-1$
            throw e;
        }

        XmlService xml;

        try {
            this.statusWindow.getXmlstatus().setStatus(xmlser + ":  " + starting, null);//$NON-NLS-1$
            xml = new XmlService(this.cfg);
            this.cfg.setXml(xml);
            this.statusWindow.getXmlstatus().setStatus(xmlser + ":  " + ready, true);//$NON-NLS-1$
        } catch (Exception e) {
            this.statusWindow.getXmlstatus().setStatus(xmlser + ":  " + startfailed, false);//$NON-NLS-1$
            throw e;
        }

        boolean allSuccess = false;

        while (!allSuccess) {
            boolean mccheck = false;
            String error = ""; //$NON-NLS-1$

            if (this.checkMCInstall()) {
                try {
                    String url = "https://s3.amazonaws.com/MinecraftDownload/launcher/"; //$NON-NLS-1$
                    String[] files = { Config.MINECRAFT_EXE, Config.MINECRAFT_JAR, Config.MINECRAFT_SERVER_EXE, Config.MINECRAFT_SERVER_JAR };
                    int i = 0;
                    for (; i < files.length; i++) {
                        this.statusWindow.getMcstatus().setStatus(String.format("Minecraft: %s %s", downloading, files[i]), null); //$NON-NLS-1$ 
                        try {
                            error = "downloading " + files[i]; //$NON-NLS-1$
                            File target = i < 2 ? this.cfg.getClientFolder() : this.cfg.getServerFolder();
                            DownloadingService.downloadURL(new URL(url + files[i]), new File(target, files[i]));
                            error = ""; //$NON-NLS-1$
                        } catch (Exception e) {
                            throw e;
                        }
                    }
                    this.statusWindow.getMcstatus().setStatus("Minecraft: " + downloading, null);//$NON-NLS-1$
                    UtilityMethods.showInformation(this.cfg, "Minecraft.",//$NON-NLS-1$
                            Messages.getString("MMMR.pre_minecraft_run")); //$NON-NLS-1$
                    this.startMC(true);
                    MMMR.writeMCBat(this.cfg);
                    if (!UtilityMethods.showConfirmation(this.cfg, "Minecraft.", //$NON-NLS-1$
                            Messages.getString("MMMR.minecraft_run_properly"))) { //$NON-NLS-1$
                        error = "installing and running Minecraft";//$NON-NLS-1$
                        UtilityMethods.delete(this.cfg.getMcBaseFolder());
                        for (i = 0; i < files.length; i++) {
                            File target = i < 2 ? this.cfg.getClientFolder() : this.cfg.getServerFolder();
                            new File(target, files[i]).delete();
                        }
                        throw new IOException("first run failed");//$NON-NLS-1$
                    }
                    ArchiveService.extract(this.cfg.getMcJar(), this.cfg.getBackupOriginalJar());
                    if (!new File(this.cfg.getBackupOriginalJar(), "META-INF/MOJANG_C.SF").exists()) { //$NON-NLS-1$
                        UtilityMethods.delete(this.cfg.getBackupOriginalJar());
                        throw new RuntimeException("not a clean install"); //$NON-NLS-1$
                    }
                    this.statusWindow.getMcstatus().setStatus("Minecraft: " + ready, mccheck = true);//$NON-NLS-1$
                } catch (Exception e) {
                    this.statusWindow.getMcstatus().setStatus("Minecraft: " + initfailed, mccheck = false);//$NON-NLS-1$
                }
            } else {
                this.statusWindow.getMcstatus().setStatus("Minecraft: " + ready, mccheck = true);//$NON-NLS-1$
            }

            boolean ybcheck = false;

            if (mccheck) {
                final String ybstr1 = "YogBox: ";//$NON-NLS-1$
                final String ybstr2 = "YogBox.";//$NON-NLS-1$
                final String ybstr3 = "YogBox ";//$NON-NLS-1$
                if (!this.cfg.getMcJarBackup().exists() && !"true".equals(this.cfg.getProperty("jogbox.ignore", "?"))) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    if (UtilityMethods.showConfirmation(this.cfg, ybstr2, "Do you want to install YogBox?\nYou need to have it downloaded already.")) { //$NON-NLS-1$
                        try {
                            File jbinstaller = UtilityMethods.selectFile(this.cfg.getMods(), new javax.swing.filechooser.FileFilter() {
                                @Override
                                public boolean accept(File f) {
                                    if (f.isDirectory()) {
                                        return true;
                                    }
                                    String name = f.getName().toLowerCase();
                                    if (!name.endsWith(".jar")) { //$NON-NLS-1$
                                        return false;
                                    }
                                    return name.contains("yogbox") && name.contains("install") && !name.contains("uninstall"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                }

                                @Override
                                public String getDescription() {
                                    return ybstr3 + Messages.getString("MMMR.installer"); //$NON-NLS-1$
                                }
                            });

                            if (jbinstaller == null) {
                                throw new IOException("no file selected");//$NON-NLS-1$
                            }

                            List<String> command = new ArrayList<String>();
                            command.add("javaw.exe"); //$NON-NLS-1$
                            command.add("-jar"); //$NON-NLS-1$
                            command.add(jbinstaller.getAbsolutePath());
                            ExceptionAndLogHandler.log(command);
                            ProcessBuilder pb = new ProcessBuilder(command);
                            pb.environment().put("AppData", this.cfg.getThisFolder().getAbsolutePath()); //$NON-NLS-1$
                            pb.environment().put("APPDATA", this.cfg.getThisFolder().getAbsolutePath()); //$NON-NLS-1$
                            pb.start();

                            UtilityMethods.showInformation(this.cfg, ybstr2, "After you installed the YogBox.\nContinue.");//$NON-NLS-1$

                            ArchiveService.extract(this.cfg.getMcJar(), this.cfg.getMcJogboxBackup());
                            this.removeOriginalFiles(this.cfg.getMcJogboxBackup().getAbsolutePath().length() + 1, this.cfg.getMcJogboxBackup(),
                                    this.cfg.getBackupOriginalJar());

                            ModPack jb = db.getOrCreate(new ModPack("YogBox", "1.1")); //$NON-NLS-1$ //$NON-NLS-2$
                            if (jb.getId() == null) {
                                jb = this.cfg.getXml().load(MMMR.class.getClassLoader().getResourceAsStream("YogBox_1.7.3_v1.1.zip.xml"), //$NON-NLS-1$
                                        ModPack.class);
                            }
                            db.save(jb);

                            File file = new File(this.cfg.getMods(), "YogBox_1.7.3_v1.1.zip.xml"); //$NON-NLS-1$
                            xml.save(new FileOutputStream(file), jb);

                            jb.setInstallationDate(new Date());
                            jb.setInstallOrder(1);
                            db.save(jb);
                            this.statusWindow.getYbstatus().setStatus(ybstr1 + ready, ybcheck = true);
                            this.cfg.setProperty("jogbox.ignore", "false"); //$NON-NLS-1$ //$NON-NLS-2$
                            // TODO delete install log
                        } catch (Exception e) {
                            this.statusWindow.getYbstatus().setStatus(ybstr1 + failed, ybcheck = false);
                        }
                    } else {
                        this.statusWindow.getYbstatus().setStatus(ybstr1 + notinstalled, ybcheck = true);
                        this.cfg.setProperty("jogbox.ignore", "true"); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                } else {
                    if (!"true".equals(this.cfg.getProperty("jogbox.ignore", "?"))) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        this.statusWindow.getYbstatus().setStatus(ybstr1 + ready, ybcheck = true);
                    } else {
                        this.statusWindow.getYbstatus().setStatus(ybstr1 + notinstalled, ybcheck = true);
                    }
                }
            }

            if (ybcheck) {
                File minecraftZip = new File(this.cfg.getMcJar().getAbsolutePath() + ".zip"); //$NON-NLS-1$
                if (this.cfg.getMcJar().isFile()) {
                    this.cfg.getMcJar().renameTo(minecraftZip);
                    this.cfg.getMcJar().mkdirs();
                    ArchiveService.extract(minecraftZip, this.cfg.getMcJar());
                }
                if ((this.mc.getFiles() == null) || (this.mc.getFiles().size() == 0)) {
                    if (this.cfg.getMcJogboxBackup().exists()) {
                        // jogbox was not installed
                        ArchiveService.extract(minecraftZip, this.cfg.getBackupOriginalJar());
                    }
                    this.addContents("bin/minecraft.jar/", this.cfg.getBackupOriginalJar().getAbsolutePath().length() + 1, //$NON-NLS-1$
                            this.cfg.getBackupOriginalJar());
                    this.addContents(this.cfg.getMcResources().getName() + "/", this.cfg.getMcResources().getAbsolutePath().length() + 1, //$NON-NLS-1$
                            this.cfg.getMcResources());
                    db.save(this.mc);
                }
                File meta_inf = new File(this.cfg.getMcJar(), "META-INF");
                if (meta_inf.exists()) {
                    if (!UtilityMethods.delete(meta_inf)) {
                        throw new IOException("could not delete META-INF");
                    }
                }
                if (!"true".equals(this.cfg.getProperty("jogbox.ignore", "?"))) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    ModPack jb = db.get(new ModPack("YogBox", "1.1")); //$NON-NLS-1$ //$NON-NLS-2$
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
                                    ExceptionAndLogHandler.log("mod not installed: " + mod); //$NON-NLS-1$
                                }
                            }
                        }
                        if ((jb.getResources() == null) || (jb.getResources().size() == 0)) {
                            Resource resource = new Resource("*", "*"); //$NON-NLS-1$ //$NON-NLS-2$
                            jb.addResource(resource);
                            int pos = this.cfg.getMcJogboxBackup().getAbsolutePath().length() + 1;
                            for (File file : UtilityMethods.listRecursive(this.cfg.getMcJogboxBackup())) {
                                if (file.isDirectory()) {
                                    continue;
                                }
                                String path = "bin/minecraft.jar/" + file.getAbsolutePath().substring(pos); //$NON-NLS-1$
                                resource.addFile(new MCFile(path, new Date(file.lastModified()), UtilityMethods.crc32File(file)));
                            }
                        }
                        db.save(jb);
                    }
                }
                {
                    File[] packs = this.cfg.getTexturePacks().listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String name) {
                            return name.endsWith(".zip");
                        }
                    });
                    if (packs != null) {
                        for (File pack : packs) {
                            File tp = new File(this.cfg.getMcTexturePacks(), pack.getName());
                            if (!tp.exists()) {
                                ExceptionAndLogHandler.log("copy texturepack " + pack.getName());
                                UtilityMethods.copyFile(pack, tp);
                            }
                        }
                    }
                }
            }

            allSuccess = mccheck && ybcheck;

            if (!allSuccess) {
                if (!UtilityMethods
                        .showConfirmation(
                                this.cfg,
                                Messages.getString("MMMR.error"), Messages.getString("MMMR.error_during") + ": " + error + "\n" + Messages.getString("MMMR.try_again"))) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
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
            commandline = "javaw.exe -Xms1024m -Xmx1024m -jar \"" + new File(this.cfg.getClientFolder(), Config.MINECRAFT_JAR).getAbsolutePath() + "\""; //$NON-NLS-1$
        } else {
            commandline = this.cfg.getMcCommandline();
        }

        List<String> command = new ArrayList<String>(Arrays.asList(commandline.split(" "))); //$NON-NLS-1$
        command.set(0, command.get(0));
        ExceptionAndLogHandler.log(command);
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.environment().put("AppData", this.cfg.getClientFolder().getAbsolutePath()); //$NON-NLS-1$
        pb.environment().put("APPDATA", this.cfg.getClientFolder().getAbsolutePath()); //$NON-NLS-1$
        @SuppressWarnings("unused")
        Process javap = pb.start();
    }
}
