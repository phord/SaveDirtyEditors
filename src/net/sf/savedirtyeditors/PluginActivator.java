/*******************************************************************************
 * Copyright (c) 2006 Vijay Aravamudhan
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Vijay Aravamudhan - initial API and implementation
 *******************************************************************************/
package net.sf.savedirtyeditors;

import net.sf.savedirtyeditors.listeners.PartListener;
import net.sf.savedirtyeditors.listeners.WindowListener;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class PluginActivator extends AbstractUIPlugin implements IStartup {
    /**
     * A flag used by the plugin to determine whether to log info messages.
     */
    private static final boolean INFO = false;

    // The shared instance.
    private static PluginActivator plugin;

    private WindowListener windowListener;
    private PartListener partListener;

    /**
     * The constructor.
     */
    public PluginActivator() {
        super();
        Assert.isTrue(plugin == null);
        plugin = this;
        windowListener = new WindowListener();
        partListener = new PartListener();
    }

    /**
     * This method is called after the workbench starts to perform extra operations.
     * 
     * @see IStartup#earlyStartup()
     */
    public void earlyStartup() {
        // add a window listener to the current workbench - for future windows opened from this
        PlatformUI.getWorkbench().addWindowListener(windowListener);

        // add a part listener to the parts in each workbench window in the current workbench
        final IWorkbenchWindow[] workbenchWindows = PlatformUI.getWorkbench().getWorkbenchWindows();
        for (int i = 0; i < workbenchWindows.length; i++) {
            final IWorkbenchWindow workbenchWindow = workbenchWindows[i];
            workbenchWindow.getPartService().addPartListener(partListener);
            // iff there's already an active editor - fire an event for part opened
            // NOTE: We dont need to recurse through all open editors, since the partOpened event will be fired when
            // each editor gets focus
            final IEditorPart activeEditor = workbenchWindow.getActivePage().getActiveEditor();
            if (activeEditor != null) {
                partListener.partOpened(activeEditor);
            }
        }
    }

    /**
     * This method is called when the plug-in is stopped
     * 
     * @see AbstractUIPlugin#stop(BundleContext)
     */
    public void stop(final BundleContext context) throws Exception {
        try {
            // remove the part listener from the parts in the current workbench
            final IWorkbenchWindow[] workbenchWindows = PlatformUI.getWorkbench().getWorkbenchWindows();
            for (int i = 0; i < workbenchWindows.length; i++) {
                workbenchWindows[i].getPartService().removePartListener(partListener);
            }

            // remove the window listener from the current workbench
            PlatformUI.getWorkbench().removeWindowListener(windowListener);

            windowListener = null;
            partListener = null;
            plugin = null;
        } finally {
            super.stop(context);
        }
    }

    /**
     * Returns the shared instance.
     * 
     * @return the shared instance.
     */
    public static final PluginActivator getDefault() {
        return plugin;
    }

    /**
     * A utility method to retrieve the primitive long preference value for the specified key.
     * 
     * @param key
     *            The key whose preference value has to be retrieved
     * @return The long preference value from the user preferences or default (as specified by
     *         {@link IPreferenceStore#getLong(String)}
     * @see IPreferenceStore#getLong(String)
     */
    public static final long getLongPreference(final String key) {
        return getDefault().getPreferenceStore().getLong(key);
    }

    /**
     * A utility method to retrieve the String preference value for the specified key.
     * 
     * @param key
     *            The key whose preference value has to be retrieved
     * @return The String preference value from the user preferences or default (as specified by
     *         {@link IPreferenceStore#getString(String)}
     * @see IPreferenceStore#getString(String)
     */
    public static final String getStringPreference(final String key) {
        return getDefault().getPreferenceStore().getString(key);
    }

    /**
     * Utility method to log an error represented by/in {@link Throwable}
     * 
     * @param throwable
     *            The {@link Throwable} to log
     */
    public static final void logError(final Throwable throwable) {
        log(new Status(IStatus.ERROR, PluginConstants.PLUGIN_ID, IStatus.ERROR, PluginConstants.ERROR, throwable));
    }

    /**
     * Utility method to log an information message
     * 
     * @param message
     *            The text to be logged
     */
    public static final void logInfo(final String message) {
        if (INFO) {
            log(new Status(IStatus.INFO, PluginConstants.PLUGIN_ID, IStatus.INFO, message, null));
        }
    }

    /**
     * Utility method to log a debug message
     * 
     * @param message
     *            The text to be logged
     */
    public static final void logDebug(final String message) {
        // HACKTAG: Dont know why - but we need to do a null check (for 3.1)
        if ((getDefault() != null) && getDefault().isDebugging()) {
            log(new Status(IStatus.WARNING, PluginConstants.PLUGIN_ID, IStatus.WARNING, message, null));
        }
    }

    /**
     * Utility method to log {@link IStatus}
     * 
     * @param status
     */
    private static final void log(final IStatus status) {
        getDefault().getLog().log(status);
    }
}