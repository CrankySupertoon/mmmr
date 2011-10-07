package org.mmmr.services;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.mmmr.Dependency;
import org.mmmr.MC;
import org.mmmr.MCFile;
import org.mmmr.Mod;
import org.mmmr.ModPack;
import org.mmmr.Resource;
import org.mmmr.services.interfaces.ArchiveOutputStreamBuilderImpl;

/**
 * @author Jurgen
 */
public class InstallationService {
    public static class InstallDependencyCheck {
        public Mod mod;

        public final List<Mod> installed = new ArrayList<Mod>();

        public final Map<Mod, Dependency> installedButWrongVersion = new HashMap<Mod, Dependency>();

        public final List<Dependency> missing = new ArrayList<Dependency>();
    }

    public static class InstallMod {
        public Mod mod;

        public final Map<Object, List<String>> conflicting = new HashMap<Object, List<String>>();
    }

    public static class UninstallDependencyCheck {
        public Mod mod;

        public final List<Dependency> isDependencyFor = new ArrayList<Dependency>();
    }

    public static class UninstallMod {
        public Mod mod;

        public final List<String> keep = new ArrayList<String>();

        public final List<String> delete = new ArrayList<String>();

        public final Map<String, Object> restore = new HashMap<String, Object>();

        public final Map<Object, List<String>> restoreGrouped = new HashMap<Object, List<String>>();
    }

    public static InstallDependencyCheck checkDependencyOnInstall(DBService dbService, Mod mod) {
        InstallDependencyCheck info = new InstallDependencyCheck();
        info.mod = mod;

        if (mod.getDependencies() != null) {
            for (Dependency dependency : mod.getDependencies()) {
                Mod installed = dbService.hql1("select mod from Mod mod where mod.sortableName=?", Mod.class, dependency.getSortableName()); //$NON-NLS-1$
                if (installed == null) {
                    info.missing.add(dependency);
                    continue;
                }
                if (installed.getVersion().equals(dependency.getVersion())) {
                    info.installed.add(installed);
                    continue;
                }
                info.installedButWrongVersion.put(installed, dependency);
            }
        }

        return info;
    }

    public static UninstallDependencyCheck checkDependencyOnUninstall(DBService dbService, Mod mod) {
        UninstallDependencyCheck info = new UninstallDependencyCheck();
        info.mod = mod;

        String hql = "select dependency from Dependency dependency inner join fetch dependency.mod mod where dependency.sortableName=?";
        for (Dependency isDependencyFor : dbService.hql(hql, Dependency.class, UtilityMethods.sortable(mod.getName()))) {
            info.isDependencyFor.add(isDependencyFor);
        }

        return info;
    }

    public static String getUrl(String url) {
        try {
            URL asUrl = new URL(url);
            String htmlAnchor = asUrl.toURI().getFragment(); // if not set = null
            String newUrl = DownloadingService.trace(asUrl).toString();
            if (htmlAnchor != null) {
                // when set: append anchor because it is removed from the new url
                newUrl = newUrl + "#" + htmlAnchor; //$NON-NLS-1$
            }
            return newUrl;
        } catch (Exception ex) {
            ExceptionAndLogHandler.log(ex);
            return url;
        }
    }

    @SuppressWarnings("unused")
    public static InstallMod installMod(DBService dbService, Mod mod) {
        InstallMod info = new InstallMod();

        return info;
    }

    public static UninstallMod uninstallMod(DBService dbService, Mod mod) {
        UninstallMod info = new UninstallMod();
        info.mod = mod;
        String hql = DBService.getNamedQuery("earlier_installed_files");
        Conflict dummy = new Conflict(null, null, null, mod);
        ExceptionAndLogHandler.log(hql);
        for (Resource resource : mod.getResources()) {
            for (MCFile mcfile : resource.getFiles()) {
                ExceptionAndLogHandler.log(mcfile.getPath());
                List<Conflict> results = dbService.hql(hql, Conflict.class, mcfile.getPath());
                Collections.sort(results);
                int myOrder = results.indexOf(dummy);
                int listSize = results.size();
                if (listSize == 1) {
                    ExceptionAndLogHandler.log("one and only => remove");
                    info.delete.add(mcfile.getPath());
                } else if ((myOrder + 1) == listSize) {
                    Conflict conflict = results.get(listSize - 2);
                    ExceptionAndLogHandler.log("last and not only one => restore :: " + conflict);
                    info.restore.put(mcfile.getPath(), conflict.get());
                } else {
                    Conflict conflict = results.get(listSize - 1);
                    ExceptionAndLogHandler.log("not last => overwritten by more recent => no change :: " + conflict);
                    info.keep.add(mcfile.getPath());
                }
            }
        }

        for (Map.Entry<String, Object> el : info.restore.entrySet()) {
            List<String> list = info.restoreGrouped.get(el.getValue());
            if (list == null) {
                list = new ArrayList<String>();
                info.restoreGrouped.put(el.getValue(), list);
            }
            list.add(el.getKey());
        }

        return info;
    }

    protected final Config cfg;

    public InstallationService(Config cfg) {
        this.cfg = cfg;
    }

    private void copy(Mod mod, Map<File, Resource> fileResource, Map<File, File> toCopy, List<File> ignored) throws IOException {
        int posmcb = this.cfg.getMcBaseFolder().getAbsolutePath().length() + 1;
        Date now = new Date();
        for (Map.Entry<File, File> entry : toCopy.entrySet()) {
            if (ignored.contains(entry.getKey())) {
                continue;
            }
            long crc32 = UtilityMethods.copyFile(entry.getKey(), entry.getValue());
            String path = entry.getValue().getCanonicalFile().getAbsolutePath().substring(posmcb);
            fileResource.get(entry.getKey()).addFile(new MCFile(path, now, crc32));
        }
        mod.setInstallationDate(now);
        this.cfg.getDb().save(mod);
    }

    private void installMod(boolean check, Mod mod) throws IOException {
        if (mod.getDependencies() != null) {
            for (Dependency dependency : mod.getDependencies()) {
                dependency.setMod(mod);
            }
        }
        String archive = mod.getArchive();
        @SuppressWarnings("unused")
        String name = mod.getName();
        @SuppressWarnings("unused")
        String version = mod.getVersion();
        File outdir = UtilityMethods.newDir(this.cfg.getTmp(), archive);
        ArchiveService.extract(new File(this.cfg.getMods(), archive), outdir);
        Set<String> conflicts = new HashSet<String>();
        Map<File, File> toCopy = new HashMap<File, File>();
        List<File> ignored = new ArrayList<File>();
        Map<File, Resource> fileResource = new HashMap<File, Resource>();
        for (Resource resource : mod.getResources()) {
            resource.setMod(mod); // fix link when loaded from xml
            String source = resource.getSourcePath();
            String target = resource.getTargetPath();
            File from = new File(outdir, source).getCanonicalFile();
            File to = new File(this.cfg.getMcBaseFolder(), target).getCanonicalFile();
            List<Pattern> includes = new ArrayList<Pattern>();
            List<Pattern> excludes = new ArrayList<Pattern>();
            if (resource.getInclude() != null) {
                for (String include : resource.getInclude().split(",")) { //$NON-NLS-1$
                    includes.add(Pattern.compile(include.replace('\\', '/'), Pattern.CASE_INSENSITIVE));
                }
            }
            if (resource.getExclude() != null) {
                for (String exclude : resource.getExclude().split(",")) { //$NON-NLS-1$
                    excludes.add(Pattern.compile(exclude.replace('\\', '/'), Pattern.CASE_INSENSITIVE));
                }
            }
            for (File fromFile : UtilityMethods.listRecursive(from)) {
                if (fromFile.isDirectory()) {
                    continue;
                }
                String relative = UtilityMethods.relativePath(from, fromFile);
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
                String mcRelative = UtilityMethods.relativePath(this.cfg.getMcBaseFolder(), toFile);
                toCopy.put(fromFile, toFile);
                fileResource.put(fromFile, resource);
                for (MCFile existing : this.cfg.getDb().getAll(new MCFile(mcRelative))) {
                    if (existing.getMc() != null) {
                        // ("conflict MC: " + mcRelative);
                        // this doesn't interest us now as almost all mods change mc class files
                        // conflicts.add("Minecraft v" + existing.getMc().getVersion());
                        continue;
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
                if (UtilityMethods.showConfirmation(this.cfg, Messages.getString("InstallationService.conflicts"), sb.toString())) { //$NON-NLS-1$
                    this.installMod(mod, toCopy, ignored, fileResource);
                }
            } else {
                this.installMod(mod, toCopy, ignored, fileResource);
            }
        } else {
            this.installMod(mod, toCopy, ignored, fileResource);
        }
        this.cfg.getTmp().delete();
    }

    public void installMod(Mod mod) throws IOException {
        InstallDependencyCheck info = InstallationService.checkDependencyOnInstall(this.cfg.getDb(), mod);
        if ((info.installedButWrongVersion.size() > 0) || (info.missing.size() > 0)) {
            StringBuilder sb1 = new StringBuilder();
            for (Dependency missing : info.missing) {
                sb1.append("  - ").append(missing.getName()).append(" v").append(missing.getVersion());
            }
            StringBuilder sb2 = new StringBuilder();
            for (Map.Entry<Mod, Dependency> installedButWrongVersion : info.installedButWrongVersion.entrySet()) {
                sb2.append("  - ").append(installedButWrongVersion.getKey().getName()).append(" v")
                        .append(installedButWrongVersion.getKey().getVersion()).append(" installed but v")
                        .append(installedButWrongVersion.getValue().getVersion()).append(" needed");
            }
            if (!UtilityMethods.showConfirmation(this.cfg, Messages.getString("InstallationService.install_mods_dependency"), //$NON-NLS-1$
                    String.format(Messages.getString("InstallationService.install_mods_dependency_check"), sb1.toString(), sb2.toString()))) {
                return;
            }
        }
        this.installMod(true, mod);
    }

    private void installMod(Mod mod, Map<File, File> toCopy, List<File> ignored, Map<File, Resource> fileResource) throws IOException {
        this.copy(mod, fileResource, toCopy, ignored);
        Integer max1 = this.cfg.getDb().hql1("select max(installOrder) from Mod", Integer.class); //$NON-NLS-1$
        Integer max2 = this.cfg.getDb().hql1("select max(installOrder) from ModPack", Integer.class); //$NON-NLS-1$
        if (max1 == null) {
            max1 = 0;
        }
        if (max2 == null) {
            max2 = 0;
        }
        int max = Math.max(max1, max2) + 1;
        mod.setInstallOrder(max);
        mod.setActualUrl(InstallationService.getUrl(mod.getUrl()));
        this.cfg.getDb().save(mod);
        UtilityMethods.showInformation(this.cfg,
                Messages.getString("InstallationService.install_mods"), Messages.getString("InstallationService.mod_installed")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private void uninstallMod(@SuppressWarnings("unused") boolean check, Mod mod) throws IOException {
        UninstallMod info = InstallationService.uninstallMod(this.cfg.getDb(), mod);

        for (Map.Entry<Object, List<String>> entry : info.restoreGrouped.entrySet()) {
            if (entry.getKey() instanceof MC) {
                for (String path : entry.getValue()) {
                    if (path.startsWith("bin/minecraft.jar")) {
                        path = path.replaceFirst("bin/minecraft.jar/", "");
                        UtilityMethods.copyFile(new File(this.cfg.getBackupOriginalJar(), path), new File(this.cfg.getMcBaseFolder(), path));
                    } else {
                        // FIXME cannot restore because no backup
                    }
                }
            } else if (entry.getKey() instanceof ModPack) {
                // TODO restore from modpack
            } else {
                Mod restoring = Mod.class.cast(entry.getKey());
                File archive = new File(this.cfg.getMods(), restoring.getArchive());
                ArchiveService.extract(archive, new ArchiveOutputStreamBuilderImpl(this.cfg.getMcBaseFolder()),
                        new ArchiveEntryMatcherImpl(entry.getValue()));
            }
        }

        for (String path : info.delete) {
            new File(this.cfg.getMcBaseFolder(), path).delete();
        }
    }

    public void uninstallMod(Mod mod) throws IOException {
        UninstallDependencyCheck info = InstallationService.checkDependencyOnUninstall(this.cfg.getDb(), mod);
        if (info.isDependencyFor.size() > 0) {
            StringBuilder sb1 = new StringBuilder();
            for (Dependency element : info.isDependencyFor) {
                sb1.append("  - ").append(element.getName());
            }
            if (!UtilityMethods.showConfirmation(this.cfg, Messages.getString("InstallationService.uninstall_mods_dependency"), //$NON-NLS-1$
                    String.format(Messages.getString("InstallationService.InstallationService.uninstall_mods_dependency_check"), sb1.toString()))) {//$NON-NLS-1$
                return;
            }
        }

        this.uninstallMod(true, mod);

        this.cfg.getDb().delete(mod);
    }
}
