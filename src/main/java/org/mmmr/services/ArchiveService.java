package org.mmmr.services;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.mmmr.services.impl.ArchiveServiceSimple;
import org.mmmr.services.interfaces.ArchiveEntry;
import org.mmmr.services.interfaces.ArchiveEntryMatcher;
import org.mmmr.services.interfaces.ArchiveOutputStreamBuilder;
import org.mmmr.services.interfaces.ArchiveServiceI;

/**
 * @author Jurgen
 */
public class ArchiveService {
    private static ArchiveServiceI archiveService = new ArchiveServiceSimple();

    public static void compress(File basedir, List<String> files, File archive) throws IOException {
        ArchiveService.getArchiveService().compress(basedir, files, archive);
    }

    public static Collection<ArchiveEntry> extract(File archive, ArchiveOutputStreamBuilder out, ArchiveEntryMatcher matcher) throws IOException {
        return ArchiveService.getArchiveService().extract(archive, out, matcher);
    }

    public static Collection<ArchiveEntry> extract(File archive, File out) throws IOException {
        return ArchiveService.getArchiveService().extract(archive, out);
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

    public static List<ArchiveEntry> list(File archive) throws IOException {
        return ArchiveService.getArchiveService().list(archive);
    }
}
