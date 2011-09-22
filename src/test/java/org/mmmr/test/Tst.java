package org.mmmr.test;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.mmmr.services.IOMethods;
import org.mmmr.services.swing.common.FancySwing;

public class Tst {

    public static void main(String[] args) throws Exception {
        FancySwing.lookAndFeel();
        JDialog.setDefaultLookAndFeelDecorated(false);
        JFrame.setDefaultLookAndFeelDecorated(false);
        IOMethods.showInformation(null, "title", "message");
        IOMethods.showWarning(null, "title", "message");
        IOMethods.showConfirmation(null, "title", "message");
        IOMethods.showOptions(null, "title", "message", new String[] { "1", "2" }, null);
    }
}
