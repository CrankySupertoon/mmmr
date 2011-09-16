package org.mmmr.services.impl;

import java.io.IOException;

import javax.swing.JOptionPane;

import org.mmmr.services.Config;
import org.mmmr.services.interfaces.ExceptionAndLogHandlerI;
import org.mmmr.services.swing.FancySwing;

public class ExceptionAndLogHandlerSimple implements ExceptionAndLogHandlerI {
    /**
     * 
     * @see org.mmmr.services.interfaces.ExceptionAndLogHandlerI#adjustLogging(org.mmmr.services.Config)
     */
    @Override
    public void adjustLogging(Config cfg) throws IOException {
        //
    }

    /**
     * 
     * @see org.mmmr.services.interfaces.ExceptionAndLogHandlerI#handle(org.mmmr.services.Config, java.lang.String, java.lang.String,
     *      java.lang.Exception)
     */
    @Override
    public void handle(Config cfg, String title, String message, Exception ex) {
        this.log(ex);
        JOptionPane.showMessageDialog(FancySwing.getCurrentFrame(), message, title, JOptionPane.ERROR_MESSAGE, cfg.getIcon());
    }

    /**
     * 
     * @see org.mmmr.services.interfaces.ExceptionAndLogHandlerI#log(java.lang.Exception)
     */
    @Override
    public void log(Exception ex) {
        ex.printStackTrace();
    }

    /**
     * $
     * 
     * @see org.mmmr.services.interfaces.ExceptionAndLogHandlerI#log(java.lang.Object)
     */
    @Override
    public void log(Object object) {
        System.out.println(object);
    }
}
