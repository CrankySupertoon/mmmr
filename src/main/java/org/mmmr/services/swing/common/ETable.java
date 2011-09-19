package org.mmmr.services.swing.common;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.gui.AbstractTableComparatorChooser;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.matchers.AbstractMatcherEditor;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;

/**
 * @author jdlandsh
 */
public class ETable extends JTable implements ETableI {
    protected class EFiltering {
        /**
         * J_DOC
         */
        protected class FilterPopup extends JWindow {
            /**
             * J_DOC
             */
            protected class RecordMatcher implements Matcher<ETableRecord> {
                protected final Pattern pattern;

                protected final int column;

                /**
                 * Instantieer een nieuwe RecordMatcher
                 * 
                 * @param column
                 * @param text
                 */
                protected RecordMatcher(int column, String text) {
                    this.column = column;
                    this.pattern = text == null ? null : Pattern.compile(text, Pattern.CASE_INSENSITIVE);
                }

                /**
                 * @see ca.odell.glazedlists.matchers.Matcher#matches(java.lang.Object)
                 */
                @Override
                public boolean matches(ETableRecord item) {
                    if (this.pattern == null) {
                        return true;
                    }
                    String value = item.getStringValue(this.column);
                    if (value == null) {
                        return false;
                    }
                    return this.pattern.matcher(value).find();
                }
            }

            /** serialVersionUID */
            private static final long serialVersionUID = 5033445579635687866L;

            protected JTextField popupTextfield = new JTextField();

            protected int popupForColumn = -1;

            protected Map<Integer, String> popupFilters = new HashMap<Integer, String>();

            /**
             * Instantieer een nieuwe FilterPopup
             * 
             * @param frame
             */
            protected FilterPopup(Frame frame) {
                super(frame);

                this.popupTextfield.setBackground(new Color(246, 243, 149));
                this.getContentPane().add(this.popupTextfield, BorderLayout.CENTER);
                this.popupTextfield.setFocusTraversalKeysEnabled(false);
                this.popupTextfield.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent e) {
                        FilterPopup.this.setVisible(false);
                    }
                });
                this.popupTextfield.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyReleased(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            // preview filtering
                            System.out.println("preview filter " + FilterPopup.this.popupTextfield.getText());
                            ETable.this.filtering.matcherEditor.fire(new RecordMatcher(FilterPopup.this.popupForColumn,
                                    FilterPopup.this.popupTextfield.getText()));
                        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                            // revert filtering, close
                            System.out.println("revert filter");
                            ETable.this.filtering.matcherEditor.fire(new RecordMatcher(FilterPopup.this.popupForColumn, FilterPopup.this.popupFilters
                                    .get(FilterPopup.this.popupForColumn)));
                            FilterPopup.this.setVisible(false);
                        } else if (e.getKeyCode() == KeyEvent.VK_TAB) {
                            // commit filtering
                            System.out.println("filter " + FilterPopup.this.popupTextfield.getText());
                            ETable.this.filtering.matcherEditor.fire(new RecordMatcher(FilterPopup.this.popupForColumn,
                                    FilterPopup.this.popupTextfield.getText()));
                            FilterPopup.this.popupFilters.put(FilterPopup.this.popupForColumn, FilterPopup.this.popupTextfield.getText());
                            FilterPopup.this.setVisible(false);
                        } else {
                            //
                        }
                    }
                });
            }

            /**
             * J_DOC
             * 
             * @param p
             */
            protected void activate(Point p) {
                this.popupForColumn = ETable.this.getTableHeader().columnAtPoint(p);
                String filter = this.popupFilters.get(this.popupForColumn);
                this.popupTextfield.setText(filter);
                Rectangle headerRect = ETable.this.getTableHeader().getHeaderRect(this.popupForColumn);
                Point pt = ETable.this.getLocationOnScreen();
                pt.translate(headerRect.x - 1, -headerRect.height - 1);
                this.setLocation(pt);
                this.setSize(headerRect.width, headerRect.height);
                this.toFront();
                this.setVisible(true);
                this.requestFocusInWindow();
                this.popupTextfield.requestFocusInWindow();
            }

            /**
             * J_DOC
             */
            public void clear() {
                this.popupFilters.clear();
                this.popupForColumn = -1;
            }
        }

        /**
         * J_DOC
         */
        protected class FilterPopupActivate extends MouseAdapter {
            /**
             * 
             * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                ETable.this.filtering.mouseClicked(e);
            }
        }

        protected RecordMatcherEditor matcherEditor;

        protected FilterList<ETableRecord> filteredRecords;

        protected FilterPopup filterPopup;

        protected EventList<ETableRecord> source;

        protected EFiltering(EventList<ETableRecord> source) {
            this.source = source;
            if (!ETable.this.cfg.isFilterable()) {
                return;
            }
            this.matcherEditor = new RecordMatcherEditor();
            this.filteredRecords = new FilterList<ETableRecord>(source, this.matcherEditor);
        }

        protected void clear() {
            if (!ETable.this.cfg.isFilterable()) {
                return;
            }
            this.getFilterPopup().clear();
        }

        protected FilterPopup getFilterPopup() {
            if (!ETable.this.cfg.isFilterable()) {
                return null;
            }
            if (this.filterPopup == null) {
                this.filterPopup = new FilterPopup(ETable.this.getFrame(ETable.this));
            }
            return this.filterPopup;
        }

        protected EventList<ETableRecord> getRecords() {
            if (!ETable.this.cfg.isFilterable()) {
                return this.source;
            }
            return this.filteredRecords;
        }

        protected void install() {
            if (!ETable.this.cfg.isFilterable()) {
                return;
            }
            ETable.this.getTableHeader().addMouseListener(new FilterPopupActivate());
        }

        protected void mouseClicked(MouseEvent e) {
            if (!ETable.this.cfg.isFilterable()) {
                return;
            }
            if ((e.getClickCount() == 1) && (e.getButton() == MouseEvent.BUTTON3)) {
                this.getFilterPopup().activate(e.getPoint());
            }
        }

    }

    protected class ESorting {
        protected TableComparatorChooser<ETableRecord> tableSorter;

        protected SortedList<ETableRecord> sortedRecords;

        protected EventList<ETableRecord> source;

        protected ESorting(EventList<ETableRecord> source) {
            this.source = source;
            if (!ETable.this.cfg.isSortable()) {
                return;
            }
            this.sortedRecords = new SortedList<ETableRecord>(source, null);
        }

        protected void dispose() {
            if (!ETable.this.cfg.isSortable()) {
                return;
            }
            this.tableSorter.dispose();
        }

        protected EventList<ETableRecord> getRecords() {
            if (!ETable.this.cfg.isSortable()) {
                return this.source;
            }
            return this.sortedRecords;
        }

        protected void install() {
            if (!ETable.this.cfg.isSortable()) {
                return;
            }
            this.tableSorter = TableComparatorChooser.install(ETable.this, this.sortedRecords, AbstractTableComparatorChooser.MULTIPLE_COLUMN_MOUSE,
                    ETable.this.tableFormat);
        }

        protected void sort(int col) {
            if (!ETable.this.cfg.isSortable()) {
                return;
            }
            this.tableSorter.clearComparator();
            this.tableSorter.appendComparator(col, 0, false);

        }

        protected void unsort() {
            if (!ETable.this.cfg.isSortable()) {
                return;
            }
            this.tableSorter.clearComparator();
        }
    }

    protected class ETableModel extends EventTableModel<ETableRecord> {
        private static final long serialVersionUID = -8936359559294414836L;

        protected ETableModel(EventList<ETableRecord> source, TableFormat<? super ETableRecord> tableFormat) {
            super(source, tableFormat);

        }

        /**
         * 
         * @see ca.odell.glazedlists.swing.EventTableModel#getColumnClass(int)
         */
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return ETable.this.tableFormat.getColumnClass(columnIndex);
        };
    }

    /**
     * J_DOC
     */
    protected class RecordMatcherEditor extends AbstractMatcherEditor<ETableRecord> {
        public void fire(Matcher<ETableRecord> matcher) {
            this.fireChanged(matcher);
        }
    }

    /** serialVersionUID */
    private static final long serialVersionUID = 6515690492295488815L;

    protected final EventList<ETableRecord> records;

    protected final EventTableModel<ETableRecord> tableModel;

    protected final EventSelectionModel<ETableRecord> selectionModel;

    protected final EFiltering filtering;

    protected final ESorting sorting;

    protected final ETableConfig cfg;

    protected ETableHeaders tableFormat;

    public ETable(ETableConfig cfg) {
        this.cfg = cfg;
        cfg.lock();
        this.records = (cfg.isThreadSafe() ? GlazedLists.threadSafeList(new BasicEventList<ETableRecord>()) : new BasicEventList<ETableRecord>());
        this.sorting = new ESorting(this.records);
        this.tableFormat = new ETableHeaders();
        this.filtering = new EFiltering(this.sorting.getRecords());
        this.tableModel = new ETableModel(this.filtering.getRecords(), this.tableFormat);
        this.setModel(this.tableModel);
        this.selectionModel = new EventSelectionModel<ETableRecord>(this.records);
        this.selectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        this.setColumnSelectionAllowed(true);
        this.setRowSelectionAllowed(true);
        this.setSelectionModel(this.selectionModel);
        this.sorting.install();
        this.filtering.install();
    }

    /**
     * 
     * @see org.mmmr.services.swing.common.ETableI#addRecord(org.mmmr.services.swing.common.ETableRecord)
     */
    @Override
    public void addRecord(final ETableRecord record) {
        this.records.add(record);
    }

    /**
     * 
     * @see org.mmmr.services.swing.common.ETableI#addRecords(java.util.Collection)
     */
    @Override
    public void addRecords(final Collection<ETableRecord> r) {
        this.records.addAll(r);
    }

    /**
     * 
     * @see org.mmmr.services.swing.common.ETableI#clear()
     */
    @Override
    public void clear() {
        this.records.clear();
        this.sorting.dispose();
        this.tableModel.setTableFormat(new ETableHeaders());
        this.filtering.clear();
    }

    /**
     * 
     * @see javax.swing.JTable#createDefaultTableHeader()
     */
    @Override
    protected JTableHeader createDefaultTableHeader() {
        return new JTableHeader(this.columnModel) {
            private static final long serialVersionUID = -378778832166135907L;

            @Override
            public String getToolTipText(MouseEvent e) {
                java.awt.Point p = e.getPoint();
                int index = this.columnModel.getColumnIndexAtX(p.x);

                // int realIndex = columnModel.getColumn(index).getModelIndex();
                String headerValue = String.valueOf(this.columnModel.getColumn(index).getHeaderValue());

                if (ETable.this.cfg.isFilterable()) {
                    String filter = ETable.this.filtering.getFilterPopup().popupFilters.get(index);

                    if ((filter != null) && (filter.trim().length() > 0)) {
                        headerValue += "<br/>" + "filter: '" + filter + "'";
                    } else {
                        headerValue += "<br/>" + "no filter";
                    }

                    headerValue += "<br/><br/>right click to edit filter<br/>enter to preview filter<br/>tab to accept filter";
                }

                return "<html><body>" + headerValue + "</body></html>";
            }
        };
    }

    /**
     * J_DOC
     * 
     * @return
     */
    public ETableI getEventSafe() {
        final ETable table = this;
        if (this.cfg.isThreadSafe()) {
            return table;
        }
        javassist.util.proxy.ProxyFactory f = new javassist.util.proxy.ProxyFactory();
        f.setInterfaces(new Class[] { ETableI.class });
        javassist.util.proxy.MethodHandler mi = new javassist.util.proxy.MethodHandler() {
            @Override
            public Object invoke(final Object self, final java.lang.reflect.Method method, final java.lang.reflect.Method paramMethod2,
                    final Object[] args) throws Throwable {
                boolean edt = javax.swing.SwingUtilities.isEventDispatchThread();

                if (edt) {
                    return method.invoke(table, args);
                }

                final Object[] values = new Object[] { null, null };
                Runnable doRun = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            values[0] = method.invoke(table, args);
                        } catch (Exception ex) {
                            values[1] = ex;
                        }
                    }
                };
                boolean wait = !method.getReturnType().equals(Void.TYPE);
                if (!wait) {
                    SwingUtilities.invokeLater(doRun);
                    return Void.TYPE;
                }
                SwingUtilities.invokeAndWait(doRun);
                if (values[1] != null) {
                    throw Exception.class.cast(values[1]);
                }
                return values[0];
            }
        };
        Object proxy;
        try {
            proxy = f.createClass().newInstance();
        } catch (InstantiationException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
        ((javassist.util.proxy.ProxyObject) proxy).setHandler(mi);
        return (ETableI) proxy;
    }

    /**
     * J_DOC
     * 
     * @param comp
     * @return
     */
    protected Frame getFrame(Component comp) {
        if (comp == null) {
            comp = this;
        }
        if (comp.getParent() instanceof Frame) {
            return (Frame) comp.getParent();
        }
        return this.getFrame(comp.getParent());
    }

    /**
     * 
     * @see org.mmmr.services.swing.common.ETableI#getRecordAtVisualRow(int)
     */
    @Override
    public ETableRecord getRecordAtVisualRow(int i) {
        return this.filtering.getRecords().get(i);
    }

    /**
     * 
     * @see javax.swing.JTable#getToolTipText(java.awt.event.MouseEvent)
     */
    @Override
    public String getToolTipText(MouseEvent e) {
        try {
            java.awt.Point p = e.getPoint();
            int rowIndex = this.rowAtPoint(p);
            int colIndex = this.columnAtPoint(p);
            int realColumnIndex = this.convertColumnIndexToModel(colIndex);

            return String.valueOf(this.getModel().getValueAt(rowIndex, realColumnIndex));
        } catch (ArrayIndexOutOfBoundsException ex) {
            return null;
        }
    }

    /**
     * Sets the preferred width of the visible column specified by vColIndex. The column will be just wide enough to show the column head and the
     * widest cell in the column. margin pixels are added to the left and right (resulting in an additional width of 2*margin pixels).
     * 
     * @param table
     * @param vColIndex
     * @param margin
     */
    public void packColumn(int vColIndex, int margin) {
        DefaultTableColumnModel colModel = (DefaultTableColumnModel) this.getColumnModel();
        TableColumn col = colModel.getColumn(vColIndex);
        int width = 0;

        // Get width of column header
        TableCellRenderer renderer = col.getHeaderRenderer();
        if (renderer == null) {
            renderer = this.getTableHeader().getDefaultRenderer();
        }
        Component comp = renderer.getTableCellRendererComponent(this, col.getHeaderValue(), false, false, 0, 0);
        width = comp.getPreferredSize().width;

        // Get maximum width of column data
        for (int r = 0; r < this.getRowCount(); r++) {
            renderer = this.getCellRenderer(r, vColIndex);
            comp = renderer.getTableCellRendererComponent(this, this.getValueAt(r, vColIndex), false, false, r, vColIndex);
            width = Math.max(width, comp.getPreferredSize().width);
        }

        // Add margin
        width += 2 * margin;

        // Set the width
        col.setPreferredWidth(width);
        col.setWidth(width);
        col.setMaxWidth(width);
    }

    /**
     * 
     * @see org.mmmr.services.swing.common.ETableI#removeAllRecords()
     */
    @Override
    public void removeAllRecords() {
        this.records.clear();
    }

    /**
     * 
     * @see org.mmmr.services.swing.common.ETableI#removeRecord(org.mmmr.services.swing.common.ETableRecord)
     */
    @Override
    public void removeRecord(final ETableRecord record) {
        this.records.remove(record);
    }

    /**
     * 
     * @see org.mmmr.services.swing.common.ETableI#removeRecordAtVisualRow(int)
     */
    @Override
    public void removeRecordAtVisualRow(final int i) {
        this.records.remove(this.sorting.getRecords().get(i));
    }

    /**
     * 
     * @see org.mmmr.services.swing.common.ETableI#setHeaders(org.mmmr.services.swing.common.ETableHeaders)
     */
    @Override
    public void setHeaders(final ETableHeaders headers) {
        this.tableFormat = headers;
        this.sorting.dispose();
        this.tableModel.setTableFormat(headers);
        this.sorting.install();
    }

    /**
     * 
     * @see org.mmmr.services.swing.common.ETableI#sort(int)
     */
    @Override
    public void sort(final int col) {
        this.sorting.sort(col);
    }

    /**
     * 
     * @see org.mmmr.services.swing.common.ETableI#unsort()
     */
    @Override
    public void unsort() {
        this.sorting.unsort();
    }
}
