package org.mmmr.services;

import java.io.File;

import javax.swing.UIManager;

public class MMMRStart {
    public static void main(String[] args) {
        try {
            File libs = new File("data/libs");
            libs.mkdirs();
            DynamicLoading.init(libs);
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            StartMe.class.cast(Class.forName("org.mmmr.services.MMMR").newInstance()).start(args);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
