package org.mmmr.services;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author Jurgen
 */
public class Messages {
    private static final String BUNDLE_NAME = "org.mmmr.services.messages"; //$NON-NLS-1$

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(Messages.BUNDLE_NAME);

    public static String getString(String key) {
        try {
            return Messages.RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    private Messages() {
        super();
    }
}
