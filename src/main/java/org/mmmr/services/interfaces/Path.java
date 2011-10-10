package org.mmmr.services.interfaces;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Path implements Comparable<Path> {
    private final String path;

    private transient int hashCode;

    /**
     * create a Path
     * 
     * @param path
     */
    public Path(String path) {
        path = path.replace('\\', '/');
        if (path.startsWith("./")) {
            path = path.substring(2);
        }
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        this.path = path;
    }

    public Path append(Path p) {
        return new Path(this.path + "/" + p.path);
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final Path other) {
        return new CompareToBuilder().append(this.path, other.path).toComparison();
    }

    public boolean endsWith(Path p) {
        return this.path.endsWith(p.getPath());
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof Path)) {
            return false;
        }
        Path castOther = (Path) other;
        return new EqualsBuilder().append(this.path, castOther.path).isEquals();
    }

    public String getPath() {
        return this.path;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = new HashCodeBuilder().append(this.path).toHashCode();
        }
        return this.hashCode;
    }

    public Path relativePathTo(Path p) {
        if (!this.startsWith(p)) {
            throw new IllegalArgumentException();
        }
        return new Path(this.path.substring(p.path.length()));
    }

    public boolean startsWith(Path p) {
        return this.path.startsWith(p.getPath());
    }

    /**
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.path;
    }
}
