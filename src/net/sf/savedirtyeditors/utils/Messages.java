package net.sf.savedirtyeditors.utils;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Utility class to retrieve locale-specific messages.
 */
public class Messages {
    private static final String BUNDLE_NAME = "net.sf.savedirtyeditors.utils.Messages"; //$NON-NLS-1$

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    /**
     * Not intended for instantiation.
     */
    private Messages() {
    }

    /**
     * Retrieves the locale-specific value for the specified key from the ResourceBundle.
     * 
     * @param key
     *            The key whose value has to be retrieved
     * @return The value corresponding to the key; or '!key!' if not found
     */
    public static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
}