package org.mmmr.services;

import static org.mmmr.services.ArchiveService.extract;
import static org.mmmr.services.IOMethods.copyFile;
import static org.mmmr.services.IOMethods.listRecursive;
import static org.mmmr.services.IOMethods.newDir;

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
    private void copy(DBService db, Mod mod, Map<File, Resource> fileResource, Map<File, File> toCopy, File minecraftBaseFolder) throws IOException {
	int posmcb = minecraftBaseFolder.getAbsolutePath().length() + 1;
	Date now = new Date();
	for (Map.Entry<File, File> entry : toCopy.entrySet()) {
	    long crc32 = copyFile(entry.getKey(), entry.getValue());
	    String path = entry.getValue().getCanonicalFile().getAbsolutePath().substring(posmcb);
	    fileResource.get(entry.getKey()).addFile(new MCFile(path, now, crc32));
	}
	mod.setInstallationDate(now);
	db.save(mod);
    }

    @SuppressWarnings("unused")
    private void installMod(boolean check, DBService db, Mod mod, File mods, File tmp, File minecraftBaseFolder) throws IOException {
	if (mod.getDependencies() != null) {
	    for (Dependency dependency : mod.getDependencies()) {
		dependency.setMod(mod);
	    }
	}
	String archive = mod.getArchive();
	String name = mod.getName();
	String version = mod.getVersion();
	File outdir = newDir(tmp, archive);
	extract(new File(mods, archive), outdir);
	int posmcb = minecraftBaseFolder.getAbsolutePath().length() + 1;
	Set<String> conflicts = new HashSet<String>();
	Map<File, File> toCopy = new HashMap<File, File>();
	Map<File, Resource> fileResource = new HashMap<File, Resource>();
	for (Resource resource : mod.getResources()) {
	    resource.setMod(mod); // fix link when loaded from xml
	    String source = resource.getSourcePath();
	    String target = resource.getTargetPath();
	    File from = new File(outdir, source).getCanonicalFile();
	    File to = new File(minecraftBaseFolder, target).getCanonicalFile();
	    int pos = from.getCanonicalFile().getAbsolutePath().length() + 1;
	    List<Pattern> includes = new ArrayList<Pattern>();
	    List<Pattern> excludes = new ArrayList<Pattern>();
	    if (resource.getInclude() != null) {
		for (String include : resource.getInclude().split(",")) {
		    includes.add(Pattern.compile(include));
		}
	    }
	    if (resource.getExclude() != null) {
		for (String exclude : resource.getExclude().split(",")) {
		    excludes.add(Pattern.compile(exclude));
		}
	    }
	    for (File fromFile : listRecursive(from)) {
		if (fromFile.isDirectory())
		    continue;
		String relative = fromFile.getCanonicalFile().getAbsolutePath().substring(pos);
		for (Pattern include : includes) {
		    if (!include.matcher(relative).find()) {
			continue;
		    }
		}
		for (Pattern exclude : excludes) {
		    if (exclude.matcher(relative).find()) {
			continue;
		    }
		}
		File toFile = new File(to, relative);
		String mcRelative = toFile.getCanonicalFile().getAbsolutePath().substring(posmcb);
		toCopy.put(fromFile, toFile);
		fileResource.put(fromFile, resource);
		for (MCFile existing : db.getAll(new MCFile(mcRelative))) {
		    if (existing.getMc() != null) {
			System.out.println("conflict MC: " + mcRelative);
			conflicts.add("Minecraft v" + existing.getMc().getVersion());
		    }
		    if (existing.getResource() != null) {
			if (existing.getResource().getMod() != null) {
			    System.out.println("conflict " + existing.getResource().getMod().getName() + ": " + mcRelative);
			    conflicts.add(existing.getResource().getMod().getName() + " v" + existing.getResource().getMod().getVersion());
			} else {
			    System.out.println("conflict " + existing.getResource().getModPack().getName() + ": " + mcRelative);
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
		if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, sb.toString(), "Conflicts", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)) {
		    installMod(db, mod, minecraftBaseFolder, toCopy, fileResource);
		}
	    } else {
		installMod(db, mod, minecraftBaseFolder, toCopy, fileResource);
	    }
	} else {
	    installMod(db, mod, minecraftBaseFolder, toCopy, fileResource);
	}
	tmp.delete();
    }

    public void installMod(DBService db, Mod mod, File mods, File tmp, File minecraftBaseFolder) throws IOException {
	installMod(true, db, mod, mods, tmp, minecraftBaseFolder);
    }

    private void installMod(DBService db, Mod mod, File minecraftBaseFolder, Map<File, File> toCopy, Map<File, Resource> fileResource) throws IOException {
	copy(db, mod, fileResource, toCopy, minecraftBaseFolder);
	db.save(mod);
	JOptionPane.showMessageDialog(null, "Mod installed.");
    }

    public void uninstallMod(DBService db, Mod mod, File mods, File tmp, File minecraftBaseFolder) {
	//
    }
}
