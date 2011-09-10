package org.mmmr.services;

import static org.mmmr.services.ArchiveService.extract;
import static org.mmmr.services.IOMethods.listRecursive;
import static org.mmmr.services.IOMethods.newDir;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import org.mmmr.MCFile;
import org.mmmr.Mod;
import org.mmmr.Resource;

/**
 * @author Jurgen
 */
public class InstallationService {
    @SuppressWarnings("unused")
    public void installMod(DBService db, Mod mod, File mods, File tmp, File minecraftBaseFolder) throws IOException {
	String archive = mod.getArchive();
	String name = mod.getName();
	String version = mod.getVersion();
	File outdir = newDir(tmp, archive);
	extract(new File(mods, archive), outdir);
	int posmcb = minecraftBaseFolder.getAbsolutePath().length() + 1;
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
		for (MCFile existing : db.getAll(new MCFile(mcRelative))) {
		    if (existing.getMc() != null)
			System.out.println("\tMC " + existing.getMc().getVersion());
		    if (existing.getResource() != null) {
			if (existing.getResource().getMod() != null)
			    System.out.println("\t" + existing.getResource().getMod().getName());
			else
			    System.out.println("\t" + existing.getResource().getModPack().getName());
		    }
		}
		System.out.println(fromFile);
		System.out.println("\t\t> " + toFile.getAbsolutePath());
	    }
	    // File to = new File(minecraftBaseFolder, target);
	    // List<MCFile> mcfs = new ArrayList<MCFile>();
	    // copy(mcfs, include, exclude, tmp.getAbsolutePath().length() + 1, from, minecraftBaseFolder.getAbsolutePath().length() + 1, to);
	    // for (MCFile mcf : mcfs) {
	    // resource.addFile(mcf);
	    // }
	}
	tmp.delete();
	// mod.setInstallationDate(new Date());
	// db.save(mod);
    }

    public void uninstallMod(DBService db, Mod mod, File mods, File tmp, File minecraftBaseFolder) {
	//
    }
}
