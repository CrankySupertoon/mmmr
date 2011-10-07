package org.mmmr.services.interfaces;

public class ArchiveEntry {
    public final String path;

    public ArchiveEntry(String path) {
        this.path = path;
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
