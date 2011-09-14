package org.mmmr.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.mmmr.Dependency;
import org.mmmr.MCFile;
import org.mmmr.Mod;
import org.mmmr.Resource;

/**
 * @author Jurgen
 */
public class InstallationService {
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
		for (String include : resource.getInclude().split(",")) {
		    includes.add(Pattern.compile(include, Pattern.CASE_INSENSITIVE));
		}
	    }
	    if (resource.getExclude() != null) {
		for (String exclude : resource.getExclude().split(",")) {
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
			    ExceptionAndLogHandler.log("conflict " + existing.getResource().getMod().getName() + ": " + mcRelative);
			    conflicts.add(existing.getResource().getMod().getName() + " v" + existing.getResource().getMod().getVersion());
			} else {
			    ExceptionAndLogHandler.log("conflict " + existing.getResource().getModPack().getName() + ": " + mcRelative);
			    conflicts.add(existing.getResource().getModPack().getName() + " v" + existing.getResource().getModPack().getVersion());
			}
		    }
		}
	    }
	}
	if (check) {
	    if (conflicts.size() > 0) {
		StringBuilder sb = new StringBuilder("Conflicting with:\n");
		for (String conflict : conflicts) {
		    sb.append("    ").append(conflict).append("\n");
		}
		sb.append("\nInstall anyways?");
		if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(FancySwing.getCurrentFrame(), sb.toString(), "Conflicts", JOptionPane.YES_NO_OPTION,
			JOptionPane.WARNING_MESSAGE, cfg.getIcon())) {
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
	cfg.getDb().save(mod);
	JOptionPane.showMessageDialog(FancySwing.getCurrentFrame(), "Mod installed.", "", JOptionPane.INFORMATION_MESSAGE, cfg.getIcon());
    }

    public void uninstallMod(Config cfg, Mod mod) {
	//
    }
}
