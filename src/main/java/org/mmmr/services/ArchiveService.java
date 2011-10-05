package org.mmmr.services;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.mmmr.services.impl.ArchiveServiceSimple;
import org.mmmr.services.interfaces.ArchiveServiceI;

/**
 * @author Jurgen
 */
public class ArchiveService {
    private static ArchiveServiceI archiveService = new ArchiveServiceSimple();

    public static void compress(File basedir, List<String> files, File archive) throws IOException {
        ArchiveService.getArchiveService().compress(basedir, files, archive);
    }

    public static void extract(File archive, File out) throws IOException {
        ArchiveService.getArchiveService().extract(archive, out);
    }

    public static void extract(File archive, File out, List<String> paths) throws IOException {
        ArchiveService.getArchiveService().extract(archive, out, paths);
    }

    private static ArchiveServiceI getArchiveService() {
        if (ArchiveService.archiveService instanceof ArchiveServiceSimple) {
            try {
                ArchiveService.archiveService = (ArchiveServiceI) Class.forName("org.mmmr.services.impl.ArchiveService7Zip").newInstance(); //$NON-NLS-1$
            } catch (Throwable ex) {
                System.out.println(ex);
            }
        }
        return ArchiveService.archiveService;
    }
}
