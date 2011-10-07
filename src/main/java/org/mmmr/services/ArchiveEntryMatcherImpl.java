package org.mmmr.services;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.mmmr.services.interfaces.ArchiveEntry;
import org.mmmr.services.interfaces.ArchiveEntryMatcher;

public class ArchiveEntryMatcherImpl implements ArchiveEntryMatcher {
    private List<Pattern> includes;

    private List<Pattern> excludes;

    private List<String> paths;

    /**
     * 
     * create a ArchiveEntryMatcherImpl
     * 
     */
    public ArchiveEntryMatcherImpl() {
        super();
    }

    /**
     * 
     * create a ArchiveEntryMatcherImpl
     * 
     * @param paths
     */
    public ArchiveEntryMatcherImpl(List<String> paths) {
        paths = new ArrayList<String>();
        for (String path : paths) {
            this.paths.add(path.replace('\\', '/'));
        }
    }

    /**
     * 
     * create a ArchiveEntryMatcherImpl
     * 
     * @param includes
     */
    public ArchiveEntryMatcherImpl(String includes) {
        this(includes, null);
    }

    /**
     * 
     * create a ArchiveEntryMatcherImpl
     * 
     * @param includes
     * @param excludes
     */
    public ArchiveEntryMatcherImpl(String includes, String excludes) {
        if (includes != null) {
            this.includes = new ArrayList<Pattern>();
            for (String include : includes.split(",")) { //$NON-NLS-1$
                this.includes.add(Pattern.compile(include.replace('\\', '/'), Pattern.CASE_INSENSITIVE));
            }
        }
        if (excludes != null) {
            this.excludes = new ArrayList<Pattern>();
            for (String exclude : excludes.split(",")) { //$NON-NLS-1$
                this.excludes.add(Pattern.compile(exclude.replace('\\', '/'), Pattern.CASE_INSENSITIVE));
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
            return this.paths.contains(entry.path.replace('\\', '/'));
        }
        if (this.excludes != null) {
            for (Pattern exclude : this.excludes) {
                if (exclude.matcher(entry.path.replace('\\', '/')).find()) {
                    return false;
                }
            }
        }
        if (this.includes != null) {
            for (Pattern include : this.includes) {
                if (!include.matcher(entry.path.replace('\\', '/')).find()) {
                    return false;
                }
            }
        }
        return true;
    }
}
