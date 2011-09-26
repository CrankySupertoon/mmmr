package org.mmmr.services;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.mmmr.Dependency;
import org.mmmr.MCFile;
import org.mmmr.Mod;
import org.mmmr.ModPack;
import org.mmmr.Resource;

// select path,
// case when mod.id is null then modpack.installationdate else mod.installationdate end,
// case when mod.id is null then modpack.name else mod.name end,
// case when mod.id is null then modpack.version else mod.version end
// from mcfile
// inner join resource on resource_id=resource.id
// left join mod on mod.id=mod_id
// left join modpack on modpack.id=resource.modpack_id
// where path in ( select path from mcfile mci where mci.mc_id is null group by path having count(*) > 1 )
// and mcfile.mc_id is null
// order by path
/**
 * @author Jurgen
 */
public class InstallationService {
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
            for (File fromFile : IOMethods.listRecursive(from)) {
                if (fromFile.isDirectory()) {
                    continue;
                }
                String relative = IOMethods.relativePath(from, fromFile);
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
                String mcRelative = IOMethods.relativePath(cfg.getMcBaseFolder(), toFile);
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
        Integer max1 = cfg.getDb().hql1("select max(installOrder) from Mod", Integer.class); //$NON-NLS-1$
        Integer max2 = cfg.getDb().hql1("select max(installOrder) from ModPack", Integer.class); //$NON-NLS-1$
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
        IOMethods.showInformation(cfg,
                Messages.getString("InstallationService.install_mods"), Messages.getString("InstallationService.mod_installed")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @SuppressWarnings("unused")
    public void uninstallMod(Config cfg, Mod mod) {
        String hql = "select f, (case when m.installOrder is null then p.installOrder else m.installOrder end as) installOrder, m, p as from MCFile f inner join f.resource r left outer join r.mod m left outer join r.modPack p where f.path=?";
        for (Resource resource : mod.getResources()) {
            for (MCFile mcfile : resource.getFiles()) {
                int maxOrder = -1;
                Mod maxConflictingMod = null;
                ModPack maxConflictingModPack = null;
                List<Object[]> results = cfg.getDb().hql(hql, Object[].class, mcfile.getPath());
                if (results.size() == 0) {
                    ExceptionAndLogHandler.log("no conflict, remove: " + mcfile.getPath());
                    continue;
                }
                Collections.sort(results, new Comparator<Object[]>() {
                    @Override
                    public int compare(Object[] o1, Object[] o2) {
                        return new CompareToBuilder().append(Integer.class.cast(o1[1]), Integer.class.cast(o2[1])).toComparison();
                    }
                });
                Object[] last = results.get(results.size() - 1);
                if (Integer.class.cast(last[1]) != mod.getInstallOrder()) {
                    Mod conflictingMod = Mod.class.cast(last[2]);
                    ModPack conflictingModPack = ModPack.class.cast(last[3]);
                    ExceptionAndLogHandler.log("another mod(pack) is already overwriting this file, no change: " + mcfile.getPath() + " "
                            + (conflictingModPack == null ? conflictingMod : conflictingModPack));
                    continue;
                }
                Object[] previous = results.get(results.size() - 2);
                Mod conflictingMod = Mod.class.cast(previous[2]);
                ModPack conflictingModPack = ModPack.class.cast(previous[3]);
                ExceptionAndLogHandler.log("restoring file from conflicting mod(pack): " + mcfile.getPath() + " "
                        + (conflictingModPack == null ? conflictingMod : conflictingModPack));
            }
        }
    }
}
