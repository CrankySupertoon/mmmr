package org.mmmr.services;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.mmmr.Mod;
import org.mmmr.Resource;
import org.mmmr.services.interfaces.ArchiveEntry;
import org.mmmr.services.interfaces.ArchiveEntryMatcher;
import org.mmmr.services.interfaces.ArchiveOutputStreamBuilder;

public class ModInstallHelper implements ArchiveEntryMatcher, ArchiveOutputStreamBuilder {
    private enum FilterType {
        Resources, Paths;
    }

    private Map<String, String> pathing = new HashMap<String, String>();

    private Map<String, List<Pattern>> includes = new HashMap<String, List<Pattern>>();

    private Map<String, List<Pattern>> excludes = new HashMap<String, List<Pattern>>();

    private List<String> paths = new ArrayList<String>();

    private FilterType type;

    private File target;

    public ModInstallHelper(File target, Mod mod) {
        this.target = target;
        this.type = FilterType.Resources;
        for (Resource resource : mod.getResources()) {
            String sp = resource.getSourcePath().replace('\\', '/').replaceFirst("\\./", "");
            String tp = resource.getTargetPath().replace('\\', '/').replaceFirst("\\./", "");
            this.pathing.put(sp, tp);
            List<Pattern> _includes = new ArrayList<Pattern>();
            if (resource.getInclude() != null) {
                for (String include : resource.getInclude().split(",")) {
                    _includes.add(Pattern.compile(include.replace('\\', '/'), Pattern.CASE_INSENSITIVE));
                }
            } else {
                _includes.add(Pattern.compile("."));
            }
            this.includes.put(sp, _includes);
            List<Pattern> _excludes = new ArrayList<Pattern>();
            if (resource.getExclude() != null) {
                for (String exclude : resource.getExclude().split(",")) {
                    _excludes.add(Pattern.compile(exclude.replace('\\', '/'), Pattern.CASE_INSENSITIVE));
                }
            }
            this.excludes.put(sp, _excludes);
        }
    }

    public ModInstallHelper(File target, Mod mod, List<String> paths) {
        this.target = target;
        this.type = FilterType.Paths;
        for (Resource resource : mod.getResources()) {
            String sp = resource.getSourcePath().replace('\\', '/').replaceFirst("\\./", "");
            String tp = resource.getTargetPath().replace('\\', '/').replaceFirst("\\./", "");
            this.pathing.put(sp, tp);
        }
        for (String path : paths) {
            this.paths.add(path.replace('\\', '/').replaceFirst("\\./", ""));
        }
    }

    /**
     * 
     * @see org.mmmr.services.interfaces.ArchiveOutputStreamBuilder#createOutputStream(org.mmmr.services.interfaces.ArchiveEntry)
     */
    @Override
    public OutputStream createOutputStream(ArchiveEntry entry) throws IOException {
        String path = this.resolvePath(entry.path);
        ExceptionAndLogHandler.log("'" + entry.path + "' >> '" + path + "'");
        if (this.target == null) {
            // in debug mode and testing
            return new ByteArrayOutputStream();
        }
        File file = new File(this.target, path);
        file.getParentFile().mkdirs();
        return new FileOutputStream(file);
    }

    /**
     * 
     * @see org.mmmr.services.interfaces.ArchiveEntryMatcher#matches(org.mmmr.services.interfaces.ArchiveEntry)
     */
    @Override
    public boolean matches(ArchiveEntry entry) {
        String path = entry.path.replace('\\', '/');
        String key = null;
        boolean success = false;
        for (String resourcepath : this.pathing.keySet()) {
            if (path.startsWith(resourcepath)) {
                key = resourcepath;
                break;
            }
        }
        switch (this.type) {
            case Paths:
                for (String p : this.paths) {
                    if (path.endsWith(p)) {
                        success = true;
                        break;
                    }
                }
                break;
            case Resources:
                for (Pattern pattern : this.includes.get(key)) {
                    if (pattern.matcher(path).find()) {
                        success = true;
                        break;
                    }
                }
                for (Pattern pattern : this.excludes.get(key)) {
                    if (pattern.matcher(path).find()) {
                        success = false;
                        break;
                    }
                }
                break;
        }
        ExceptionAndLogHandler.log("'" + path + "' [" + key + "]=" + success);
        return success;
    }

    public String resolvePath(String path) {
        path = path.replace('\\', '/');
        String key = null;
        for (String resourcepath : this.pathing.keySet()) {
            if (path.startsWith(resourcepath)) {
                key = resourcepath;
                break;
            }
        }
        if (key == null) {
            throw new NullPointerException();
        }
        path = (this.pathing.get(key.toString()) + "/" + path.substring(key.length())).replaceAll("//", "/").replaceAll("//", "/");
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return path;
    }
}