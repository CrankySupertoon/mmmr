package org.mmmr.services;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.mmmr.services.interfaces.ArchiveEntry;
import org.mmmr.services.interfaces.ArchiveEntryMatcher;
import org.mmmr.services.interfaces.Path;

/**
 * @author Jurgen
 */
public class DefaultArchiveEntryMatcher implements ArchiveEntryMatcher {
    private List<Pattern> includes;

    private List<Pattern> excludes;

    private List<Path> paths;

    /**
     * 
     * create a ArchiveEntryMatcherImpl
     * 
     */
    public DefaultArchiveEntryMatcher() {
        super();
    }

    /**
     * 
     * create a ArchiveEntryMatcherImpl
     * 
     * @param paths
     */
    public DefaultArchiveEntryMatcher(List<String> p) {
        this.paths = new ArrayList<Path>();
        for (String path : p) {
            this.paths.add(new Path(path));
        }
    }

    /**
     * 
     * create a ArchiveEntryMatcherImpl
     * 
     * @param includes
     */
    public DefaultArchiveEntryMatcher(String includes) {
        this(includes, null);
    }

    /**
     * 
     * create a ArchiveEntryMatcherImpl
     * 
     * @param includes
     * @param excludes
     */
    public DefaultArchiveEntryMatcher(String includes, String excludes) {
        if (includes != null) {
            this.includes = new ArrayList<Pattern>();
            for (String include : includes.split(",")) { //$NON-NLS-1$
                this.includes.add(Pattern.compile(new Path(include).getPath(), Pattern.CASE_INSENSITIVE));
            }
        }
        if (excludes != null) {
            this.excludes = new ArrayList<Pattern>();
            for (String exclude : excludes.split(",")) { //$NON-NLS-1$
                this.excludes.add(Pattern.compile(new Path(exclude).getPath(), Pattern.CASE_INSENSITIVE));
            }
        }
    }

    /**
     * 
     * @see org.mmmr.services.interfaces.ArchiveEntryMatcher#matches(org.mmmr.services.interfaces.ArchiveEntry)
     */
    @Override
    public boolean matches(ArchiveEntry entry) {
        if (this.paths != null) {
            return this.paths.contains(entry.path);
        }
        if (this.excludes != null) {
            for (Pattern exclude : this.excludes) {
                if (exclude.matcher(entry.path.getPath()).find()) {
                    return false;
                }
            }
        }
        if (this.includes != null) {
            for (Pattern include : this.includes) {
                if (!include.matcher(entry.path.getPath()).find()) {
                    return false;
                }
            }
        }
        return true;
    }
}
