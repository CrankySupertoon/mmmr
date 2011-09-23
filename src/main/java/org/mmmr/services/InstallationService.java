package org.mmmr.services;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.mmmr.Dependency;
import org.mmmr.MCFile;
import org.mmmr.Mod;
import org.mmmr.Resource;

/**
 * @author Jurgen
 */
public class InstallationService {
    public static String getUrl(String url) {
        try {
            ByteArrayOutputStream readOnly = new ByteArrayOutputStream() {
                @Override
                public void write(byte[] b) throws IOException {
                    throw new UnsupportedOperationException();
                }

                @Override
                public synchronized void write(byte[] b, int off, int len) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public synchronized void write(int b) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public synchronized void writeTo(OutputStream out) throws IOException {
                    throw new UnsupportedOperationException();
                }
            };
            Map<String, Object> downloadURL = DownloadingService.downloadURL(new URL(url), readOnly);
            String redirect = String.valueOf(downloadURL.get("redirect")); //$NON-NLS-1$
            return redirect;
        } catch (Exception ex) {
            ExceptionAndLogHandler.log(ex);
            return url;
        }
    }

    public static void main(String[] args) {
        try {
            String string = "http://www.minecraftforum.net/topic/75440-"; //$NON-NLS-1$
            new InstallationService();
            System.out.println(string + " >> " + InstallationService.getUrl(string)); //$NON-NLS-1$
            string = "http://www.minecraftforum.net/topic/124117-18-daftpvfs-mods/#starting_inventory"; //$NON-NLS-1$
            new InstallationService();
            System.out.println(string + " >> " + InstallationService.getUrl(string)); //$NON-NLS-1$
            string = "http://www.minecraftforum.net/topic/124117-/#starting_inventory"; //$NON-NLS-1$
            new InstallationService();
            System.out.println(string + " >> " + InstallationService.getUrl(string)); //$NON-NLS-1$
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void copy(Config cfg, Mod mod, Map<File, Resource> fileResource, Map<File, File> toCopy, List<File> ignored) throws IOException {
        int posmcb = cfg.getMcBaseFolder().getAbsolutePath().length() + 1;
        Date now = new Date();
        for (Map.Entry<File, File> entry : toCopy.entrySet()) {
            if (ignored.contains(entry.getKey())) {
                continue;
            }
            long crc32 = IOMethods.copyFile(entry.getKey(), entry.getValue());
            String path = entry.getValue().getCanonicalFile().getAbsolutePath().substring(posmcb);
            fileResource.get(entry.getKey()).addFile(new MCFile(path, now, crc32));
        }
        mod.setInstallationDate(now);
        cfg.getDb().save(mod);
    }

    @SuppressWarnings("unused")
    private void installMod(Config cfg, boolean check, Mod mod) throws IOException {
        if (mod.getDependencies() != null) {
            for (Dependency dependency : mod.getDependencies()) {
                dependency.setMod(mod);
            }
        }
        String archive = mod.getArchive();
        String name = mod.getName();
        String version = mod.getVersion();
        File outdir = IOMethods.newDir(cfg.getTmp(), archive);
        ArchiveService.extract(new File(cfg.getMods(), archive), outdir);
        int posmcb = cfg.getMcBaseFolder().getAbsolutePath().length() + 1;
        Set<String> conflicts = new HashSet<String>();
        Map<File, File> toCopy = new HashMap<File, File>();
        List<File> ignored = new ArrayList<File>();
        Map<File, Resource> fileResource = new HashMap<File, Resource>();
        for (Resource resource : mod.getResources()) {
            resource.setMod(mod); // fix link when loaded from xml
            String source = resource.getSourcePath();
            String target = resource.getTargetPath();
            File from = new File(outdir, source).getCanonicalFile();
            File to = new File(cfg.getMcBaseFolder(), target).getCanonicalFile();
            int pos = from.getCanonicalFile().getAbsolutePath().length() + 1;
            List<Pattern> includes = new ArrayList<Pattern>();
            List<Pattern> excludes = new ArrayList<Pattern>();
            if (resource.getInclude() != null) {
                for (String include : resource.getInclude().split(",")) { //$NON-NLS-1$
                    includes.add(Pattern.compile(include, Pattern.CASE_INSENSITIVE));
                }
            }
            if (resource.getExclude() != null) {
                for (String exclude : resource.getExclude().split(",")) { //$NON-NLS-1$
                    excludes.add(Pattern.compile(exclude, Pattern.CASE_INSENSITIVE));
                }
            }
            for (File fromFile : IOMethods.listRecursive(from)) {
                if (fromFile.isDirectory()) {
                    continue;
                }
                String relative = fromFile.getCanonicalFile().getAbsolutePath().substring(pos);
                boolean ignore = false;
                for (Pattern include : includes) {
                    if (!include.matcher(relative).find()) {
                        ignore = true;
                    }
                }
                for (Pattern exclude : excludes) {
                    if (exclude.matcher(relative).find()) {
                        ignore = true;
                    }
                }
                if (ignore) {
                    ignored.add(fromFile);
                    continue;
                }
                File toFile = new File(to, relative);
                String mcRelative = toFile.getCanonicalFile().getAbsolutePath().substring(posmcb);
                toCopy.put(fromFile, toFile);
                fileResource.put(fromFile, resource);
                for (MCFile existing : cfg.getDb().getAll(new MCFile(mcRelative))) {
                    if (existing.getMc() != null) {
                        // ("conflict MC: " + mcRelative);
                        // this doesn't interest us now as almost all mods change mc class files
                        // conflicts.add("Minecraft v" + existing.getMc().getVersion());
                    }
                    // TODO conflicted files should be ordered as the are installed by mod(pack)s
                    if (existing.getResource() != null) {
                        if (existing.getResource().getMod() != null) {
                            ExceptionAndLogHandler.log("conflict " + existing.getResource().getMod().getName() + ": " + mcRelative); //$NON-NLS-1$ //$NON-NLS-2$
                            conflicts.add(existing.getResource().getMod().getName() + " v" + existing.getResource().getMod().getVersion()); //$NON-NLS-1$
                        } else {
                            ExceptionAndLogHandler.log("conflict " + existing.getResource().getModPack().getName() + ": " + mcRelative); //$NON-NLS-1$ //$NON-NLS-2$
                            conflicts.add(existing.getResource().getModPack().getName() + " v" + existing.getResource().getModPack().getVersion()); //$NON-NLS-1$
                        }
                    }
                }
            }
        }
        if (check) {
            if (conflicts.size() > 0) {
                StringBuilder sb = new StringBuilder(Messages.getString("InstallationService.conflict") + ":\n"); //$NON-NLS-1$ //$NON-NLS-2$
                for (String conflict : conflicts) {
                    sb.append("    ").append(conflict).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
                }
                sb.append("\n" + Messages.getString("InstallationService.install_force")); //$NON-NLS-1$ //$NON-NLS-2$
                if (IOMethods.showConfirmation(cfg, Messages.getString("InstallationService.conflicts"), sb.toString())) { //$NON-NLS-1$
                    this.installMod(cfg, mod, toCopy, ignored, fileResource);
                }
            } else {
                this.installMod(cfg, mod, toCopy, ignored, fileResource);
            }
        } else {
            this.installMod(cfg, mod, toCopy, ignored, fileResource);
        }
        cfg.getTmp().delete();
    }

    public void installMod(Config cfg, Mod mod) throws IOException {
        this.installMod(cfg, true, mod);
    }

    private void installMod(Config cfg, Mod mod, Map<File, File> toCopy, List<File> ignored, Map<File, Resource> fileResource) throws IOException {
        this.copy(cfg, mod, fileResource, toCopy, ignored);
        Integer max1 = cfg.getDb().hql("select max(installOrder) from Mod", Integer.class).get(0); //$NON-NLS-1$
        Integer max2 = cfg.getDb().hql("select max(installOrder) from ModPack", Integer.class).get(0); //$NON-NLS-1$
        if (max1 == null) {
            max1 = 0;
        }
        if (max2 == null) {
            max2 = 0;
        }
        int max = Math.max(max1, max2) + 1;
        mod.setInstallOrder(max);
        mod.setActualUrl(InstallationService.getUrl(mod.getUrl()));
        cfg.getDb().save(mod);
        IOMethods.showInformation(cfg, Messages.getString("InstallationService.install_mods"), Messages.getString("InstallationService.mod_installed")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @SuppressWarnings("unused")
    public void uninstallMod(Config cfg, Mod mod) {
        //
    }
}
