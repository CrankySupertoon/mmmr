package org.mmmr.services.interfaces;

import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class ArchiveEntry {
    public final Path path;

    public final long size;

    public final Date creation;

    public final Date modification;

    public final long compressedSize;

    public final String group;

    public final String user;

    private transient int hashCode;

    public ArchiveEntry(String path, long size, Date creation, Date modification, long compressedSize, String group, String user) {
        this.path = new Path(path);
        this.size = size;
        this.creation = creation;
        this.modification = modification;
        this.compressedSize = compressedSize;
        this.group = group;
        this.user = user;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof ArchiveEntry)) {
            return false;
        }
        ArchiveEntry castOther = (ArchiveEntry) other;
        return new EqualsBuilder().append(this.path, castOther.path).isEquals();
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

    /**
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.path.toString();
    }
}
