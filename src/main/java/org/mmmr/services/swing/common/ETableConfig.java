package org.mmmr.services.swing.common;

/**
 * @author jdlandsh
 */
public class ETableConfig {
    protected boolean threadSafe;

    protected boolean sortable;

    protected boolean filterable;

    protected boolean locked;

    public ETableConfig(boolean threadSafe, boolean sortable, boolean filterable) {
        super();
        this.threadSafe = threadSafe;
        this.sortable = sortable;
        this.filterable = filterable;
    }

    public boolean isFilterable() {
        return this.filterable;
    }

    public boolean isLocked() {
        return this.locked;
    }

    public boolean isSortable() {
        return this.sortable;
    }

    public boolean isThreadSafe() {
        return this.threadSafe;
    }

    public void lock() {
        this.locked = true;
    }

    public void setFilterable(boolean filterable) {
        if (this.isLocked()) {
            throw new IllegalArgumentException();
        }
        this.filterable = filterable;
    }

    public void setSortable(boolean sortable) {
        if (this.isLocked()) {
            throw new IllegalArgumentException();
        }
        this.sortable = sortable;
    }

    public void setThreadSafe(boolean threadSafe) {
        if (this.isLocked()) {
            throw new IllegalArgumentException();
        }
        this.threadSafe = threadSafe;
    }
}
