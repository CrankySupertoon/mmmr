package org.mmmr.services;

import static org.mmmr.services.ArchiveService.extract;
import static org.mmmr.services.IOMethods.copyFile;
import static org.mmmr.services.IOMethods.newDir;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.mmmr.MCFile;
import org.mmmr.Mod;
import org.mmmr.Resource;

public class InstallationService {
    private List<MCFile> copy(List<MCFile> mcfs, Pattern include, Pattern exclude, int flen, File from, int tlen, File to) throws IOException {
	if (from.isFile()) {
	    String fromR = from.getAbsolutePath().substring(flen);
	    if (include == null || include.matcher(fromR).find()) {
		if (exclude == null || !exclude.matcher(fromR).find()) {
		    String toR = to.getAbsolutePath().substring(tlen);
		    System.out.println(fromR + " > " + toR + " :: " + (to.exists() ? "overwrite" : "new"));
		    long crc32 = copyFile(from, to);
		    MCFile mcf = new MCFile(toR, new Date(), crc32);
		    mcfs.add(mcf);
		}
	    }
	}
	File[] children = from.listFiles();
	if (children != null) {
	    for (File child : children) {
		copy(mcfs, include, exclude, flen, child, tlen, new File(to, child.getName()));
	    }
	}
	return mcfs;
    }

    @SuppressWarnings("unused")
    public void installMod(DBService db, Mod mod, File mods, File tmp, File minecraftBaseFolder) throws IOException {
	String archive = mod.getArchive();
	String name = mod.getName();
	String version = mod.getVersion();
	File outdir = newDir(tmp, archive);
	extract(new File(mods, archive), outdir);
	for (Resource resource : mod.getResources()) {
	    String source = null;
	    String target = null;
	    File from = new File(outdir, source);
	    File to = new File(minecraftBaseFolder, target);
	    Pattern include = resource.getInclude() == null ? null : Pattern.compile(resource.getInclude());
	    Pattern exclude = resource.getExclude() == null ? null : Pattern.compile(resource.getExclude());
	    List<MCFile> mcfs = new ArrayList<MCFile>();
	    copy(mcfs, include, exclude, tmp.getAbsolutePath().length() + 1, from, minecraftBaseFolder.getAbsolutePath().length() + 1, to);
	    for (MCFile mcf : mcfs) {
		resource.addFile(mcf);
	    }
	}
	tmp.delete();
	mod.setInstallationDate(new Date());
	db.save(mod);
    }

    public void uninstallMod(DBService db, Mod mod, File mods, File tmp, File minecraftBaseFolder) {
	//
    }
}
