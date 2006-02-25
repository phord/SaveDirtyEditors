/*******************************************************************************
 * Copyright (c) 2005 Vijay Aravamudhan
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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.util.Assert;
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
     * The id of the plugin (as defined in the plugin.xml or manifest.mf file)
     */
    private static final String PLUGIN_ID = "SaveDirtyEditors"; //$NON-NLS-1$

    /**
     * The name of the family to which all jobs created by this plugin belong to.
     */
    public static final String JOB_FAMILY_NAME = PLUGIN_ID + ":Jobs"; //$NON-NLS-1$

    private static final String ERROR = "Error"; //$NON-NLS-1$

    /**
     * A flag used by the plugin to determine whether to log info messages.
     */
    private static boolean INFO = false;

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
        // add a part listener to the parts in the current workbench
        IWorkbenchWindow[] workbenchWindows = PlatformUI.getWorkbench().getWorkbenchWindows();
        for (int i = 0; i < workbenchWindows.length; i++) {
            IWorkbenchWindow workbenchWindow = workbenchWindows[i];
            workbenchWindow.getPartService().addPartListener(partListener);
            // iff there's already an active editor - fire an event for part opened
            IEditorPart activeEditor = workbenchWindow.getActivePage().getActiveEditor();
            if (activeEditor != null) {
                partListener.partOpened(activeEditor);
            }
        }
    }

    /**
     * This method is called upon plug-in activation
     * 
     * @see AbstractUIPlugin#start(BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
    }

    /**
     * This method is called when the plug-in is stopped
     * 
     * @see AbstractUIPlugin#stop(BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        try {
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
    public static PluginActivator getDefault() {
        return plugin;
    }

    /**
     * Utility method to log an error represented by/in {@link Throwable}
     * 
     * @param throwable
     *            The {@link Throwable} to log
     */
    public static void logError(Throwable throwable) {
        log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, ERROR, throwable));
    }

    /**
     * Utility method to log an information message
     * 
     * @param message
     *            The text to be logged
     */
    public static void logInfo(String message) {
        if (INFO) {
            log(new Status(IStatus.INFO, PLUGIN_ID, IStatus.INFO, message, null));
        }
    }

    /**
     * Utility method to log a debug message
     * 
     * @param message
     *            The text to be logged
     */
    public static void logDebug(String message) {
        // HACKTAG: Dont know why - but we need to do a null check (for 3.1)
        if (getDefault() != null && getDefault().isDebugging()) {
            log(new Status(IStatus.WARNING, PLUGIN_ID, IStatus.WARNING, message, null));
        }
    }

    /**
     * Utility method to log {@link IStatus}
     * 
     * @param status
     */
    private static void log(IStatus status) {
        getDefault().getLog().log(status);
    }
}