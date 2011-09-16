package org.mmmr.services;

import javax.swing.JOptionPane;

public class ExceptionAndLogHandlerSimple implements ExceptionAndLogHandlerI {

    @Override
    public void handle(Config cfg, String title, String message, Exception ex) {
        this.log(ex);
        JOptionPane.showMessageDialog(FancySwing.getCurrentFrame(), message, title, JOptionPane.ERROR_MESSAGE, cfg.getIcon());
    }

    @Override
    public void log(Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void log(Object object) {
        System.out.println(object);
    }
}
