package org.mmmr.services.swing;

import javax.swing.JFrame;

import org.mmmr.Mod;
import org.mmmr.services.Config;

/**
 * @author Jurgen
 */
public class ModWizard extends JFrame {
    private static final long serialVersionUID = -6261674801873385201L;

    public static void main(String[] args) {
        try {
            new ModWizard(new Config(), null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public ModWizard(Config cfg, Mod mod) {
        if (mod == null) {
            mod = new Mod();
        }
    }
}
