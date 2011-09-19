package org.mmmr.services.swing.common;

import java.util.Collection;

/**
 * @author jdlandsh
 */
public interface ETableI {
    public abstract void addRecord(final ETableRecord record);

    public abstract void addRecords(final Collection<ETableRecord> r);

    public abstract void clear();

    public abstract ETableRecord getRecordAtVisualRow(int i);

    public abstract void removeAllRecords();

    public abstract void removeRecord(final ETableRecord record);

    public abstract void removeRecordAtVisualRow(final int i);

    public abstract void setHeaders(final ETableHeaders headers);

    public abstract void sort(final int col);

    public abstract void unsort();
}