package org.mmmr.services;

import static org.mmmr.services.ArchiveService.extract;
import static org.mmmr.services.IOMethods.copyFile;
import static org.mmmr.services.IOMethods.listRecursive;
import static org.mmmr.services.IOMethods.newDir;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

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
	    String source = resource.getSourcePath();
	    String target = resource.getTargetPath();
	    File from = new File(outdir, source).getCanonicalFile();
	    File to = new File(minecraftBaseFolder, target).getCanonicalFile();
	    int pos = from.getCanonicalFile().getAbsolutePath().length() + 1;
	    Pattern include = resource.getInclude() == null ? null : Pattern.compile(resource.getInclude());
	    Pattern exclude = resource.getExclude() == null ? null : Pattern.compile(resource.getExclude());
	    for (File fromFile : listRecursive(from)) {
		String relative = fromFile.getCanonicalFile().getAbsolutePath().substring(pos);
		if (include != null) {
		    if (!include.matcher(relative).find()) {
			continue;
		    }
		}
		if (exclude != null) {
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
			// System.out.println("\tMC " + existing.getMc().getVersion());
			conflicts.add("Minecraft v" + existing.getMc().getVersion());
		    }
		    if (existing.getResource() != null) {
			if (existing.getResource().getMod() != null) {
			    // System.out.println("\t" + existing.getResource().getMod().getName());
			    conflicts.add(existing.getResource().getMod().getName() + " v" + existing.getResource().getMod().getVersion());
			} else {
			    // System.out.println("\t" + existing.getResource().getModPack().getName());
			    conflicts.add(existing.getResource().getModPack().getName() + " v" + existing.getResource().getModPack().getVersion());
			}
		    }
		}
		// System.out.println(fromFile);
		// System.out.println("\t\t> " + toFile.getAbsolutePath());
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
		    copy(db, mod, fileResource, toCopy, minecraftBaseFolder);
		}
	    } else {
		copy(db, mod, fileResource, toCopy, minecraftBaseFolder);
	    }
	} else {
	    copy(db, mod, fileResource, toCopy, minecraftBaseFolder);
	}
	tmp.delete();
    }

    public void installMod(DBService db, Mod mod, File mods, File tmp, File minecraftBaseFolder) throws IOException {
	installMod(true, db, mod, mods, tmp, minecraftBaseFolder);
    }

    public void uninstallMod(DBService db, Mod mod, File mods, File tmp, File minecraftBaseFolder) {
	//
    }
}
