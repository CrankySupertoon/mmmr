package org.mmmr.services.swing.common;

public enum TristateState {
    SELECTED {
        @Override
        public TristateState next() {
            return INDETERMINATE;
        }
    },
    INDETERMINATE {
        @Override
        public TristateState next() {
            return DESELECTED;
        }
    },
    DESELECTED {
        @Override
        public TristateState next() {
            return SELECTED;
        }
    };

    public abstract TristateState next();
}
