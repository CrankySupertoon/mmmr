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
import org.mmmr.services.interfaces.Path;

public class ModInstallHelper implements ArchiveEntryMatcher, ArchiveOutputStreamBuilder {
    private enum FilterType {
        Resources, Paths;
    }

    private Map<Path, Path> pathing = new HashMap<Path, Path>();

    private Map<Path, List<Pattern>> includes = new HashMap<Path, List<Pattern>>();

    private Map<Path, List<Pattern>> excludes = new HashMap<Path, List<Pattern>>();

    private List<Path> paths = new ArrayList<Path>();

    private FilterType type;

    private File target;

    public ModInstallHelper(File target, Mod mod) {
        this.target = target;
        this.type = FilterType.Resources;
        for (Resource resource : mod.getResources()) {
            Path sp = new Path(resource.getSourcePath());
            Path tp = new Path(resource.getTargetPath());
            this.pathing.put(sp, tp);
            List<Pattern> _includes = new ArrayList<Pattern>();
            if (resource.getInclude() != null) {
                for (String include : resource.getInclude().split(",")) {
                    _includes.add(Pattern.compile(new Path(include).getPath(), Pattern.CASE_INSENSITIVE));
                }
            } else {
                _includes.add(Pattern.compile("."));
            }
            this.includes.put(sp, _includes);
            List<Pattern> _excludes = new ArrayList<Pattern>();
            if (resource.getExclude() != null) {
                for (String exclude : resource.getExclude().split(",")) {
                    _excludes.add(Pattern.compile(new Path(exclude).getPath(), Pattern.CASE_INSENSITIVE));
                }
            }
            this.excludes.put(sp, _excludes);
        }
    }

    public ModInstallHelper(File target, Mod mod, List<String> paths) {
        this.target = target;
        this.type = FilterType.Paths;
        for (Resource resource : mod.getResources()) {
            Path sp = new Path(resource.getSourcePath());
            Path tp = new Path(resource.getTargetPath());
            this.pathing.put(sp, tp);
        }
        for (String path : paths) {
            this.paths.add(new Path(path));
        }
    }

    /**
     * 
     * @see org.mmmr.services.interfaces.ArchiveOutputStreamBuilder#createOutputStream(org.mmmr.services.interfaces.ArchiveEntry)
     */
    @Override
    public OutputStream createOutputStream(ArchiveEntry entry) throws IOException {
        Path path = this.resolvePath(entry.path);
        ExceptionAndLogHandler.log("'" + entry.path + "' >> '" + path + "'");
        if (this.target == null) {
            // in debug mode and testing
            return new ByteArrayOutputStream();
        }
        File file = new File(this.target, path.getPath());
        file.getParentFile().mkdirs();
        return new FileOutputStream(file);
    }

    /**
     * 
     * @see org.mmmr.services.interfaces.ArchiveEntryMatcher#matches(org.mmmr.services.interfaces.ArchiveEntry)
     */
    @Override
    public boolean matches(ArchiveEntry entry) {
        Path path = entry.path;
        Path keyPath = null;
        boolean success = false;
        for (Path resourcepath : this.pathing.keySet()) {
            if (path.startsWith(resourcepath)) {
                keyPath = resourcepath;
                break;
            }
        }
        switch (this.type) {
            case Paths:
                for (Path p : this.paths) {
                    if (path.endsWith(p)) {
                        for (Path key : this.pathing.keySet()) {
                            if (key.append(p).equals(path)) {
                                success = true;
                                break;
                            }
                        }
                    }
                }
                break;
            case Resources:
                for (Pattern pattern : this.includes.get(keyPath)) {
                    if (pattern.matcher(path.getPath()).find()) {
                        success = true;
                        break;
                    }
                }
                for (Pattern pattern : this.excludes.get(keyPath)) {
                    if (pattern.matcher(path.getPath()).find()) {
                        success = false;
                        break;
                    }
                }
                break;
        }
        ExceptionAndLogHandler.log("'" + path + "' [" + keyPath + "]=" + success);
        return success;
    }

    public Path resolvePath(Path path) {
        Path key = null;
        for (Path resourcepath : this.pathing.keySet()) {
            if (path.startsWith(resourcepath)) {
                key = resourcepath;
                break;
            }
        }
        if (key == null) {
            throw new NullPointerException();
        }
        Path relativePathToKey = path.relativePathTo(key);
        return this.pathing.get(key).append(relativePathToKey);
    }
}