package org.mmmr.services.swing.common;

/**
 * @author jdlandsh
 */
public class ETableConfig {
    protected boolean threadSafe = true;

    protected boolean sortable;

    protected boolean filterable;

    protected boolean locked;

    protected boolean editable;

    protected boolean reorderable;

    protected boolean resizable;

    protected boolean vertical;

    protected boolean draggable;

    public ETableConfig() {
        super();
        this.threadSafe = true;
    }

    public ETableConfig(boolean all) {
        this(all, all, all, all, all, all, all, all);
        this.threadSafe = true;
        this.vertical = false;
    }

    public ETableConfig(boolean threadSafe, boolean sortable, boolean filterable, boolean editable, boolean reorderable, boolean resizable,
            boolean vertical, boolean draggable) {
        super();
        this.threadSafe = threadSafe;
        this.sortable = sortable;
        this.filterable = filterable;
        this.editable = editable;
        this.reorderable = reorderable;
        this.resizable = resizable;
        this.vertical = vertical;
        this.draggable = draggable;
    }

    public boolean isDraggable() {
        return this.draggable;
    }

    public boolean isEditable() {
        return this.editable;
    }

    public boolean isFilterable() {
        return this.filterable;
    }

    public boolean isLocked() {
        return this.locked;
    }

    public boolean isReorderable() {
        return this.reorderable;
    }

    public boolean isResizable() {
        return this.resizable;
    }

    public boolean isSortable() {
        return this.sortable;
    }

    public boolean isThreadSafe() {
        return this.threadSafe;
    }

    public boolean isVertical() {
        return this.vertical;
    }

    public void lock() {
        this.setLocked(true);
    }

    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public void setFilterable(boolean filterable) {
        if (this.isLocked()) {
            throw new IllegalArgumentException();
        }
        this.filterable = filterable;
    }

    private void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void setReorderable(boolean reorderable) {
        if (this.isLocked()) {
            throw new IllegalArgumentException();
        }
        this.reorderable = reorderable;
    }

    public void setResizable(boolean resizable) {
        if (this.isLocked()) {
            throw new IllegalArgumentException();
        }
        this.resizable = resizable;
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

    public void setVertical(boolean vertical) {
        if (this.isLocked()) {
            throw new IllegalArgumentException();
        }
        this.vertical = vertical;
    }
}
