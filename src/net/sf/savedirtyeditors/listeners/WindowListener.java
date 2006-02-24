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
package net.sf.savedirtyeditors.listeners;

import net.sf.savedirtyeditors.PluginActivator;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchWindow;

public final class WindowListener implements IWindowListener {
    private PartListener partListener = new PartListener();

    public void windowOpened(final IWorkbenchWindow window) {
        PluginActivator.logDebug("Monitoring window"); //$NON-NLS-1$
        window.getPartService().addPartListener(partListener);
    }

    public void windowClosed(final IWorkbenchWindow window) {
        PluginActivator.logDebug("Unmonitoring window"); //$NON-NLS-1$
        window.getPartService().removePartListener(partListener);
    }

    /**
     * When the window is deactivated, finds any jobs created by this plugin and "wakes" them forcing them to perform
     * whatever their operation is outside of their scheduled time.
     * 
     * @see IWindowListener#windowDeactivated(IWorkbenchWindow)
     */
    public void windowDeactivated(final IWorkbenchWindow window) {
        window.getShell().getDisplay().asyncExec(new Runnable() {
            public void run() {
                Job[] jobs = Platform.getJobManager().find(PluginActivator.JOB_FAMILY_NAME);
                for (int i = 0; i < jobs.length; i++) {
                    jobs[i].wakeUp();
                }
            }
        });
    }

    /**
     * Dummy implementation since we are not interested in this life-cycle event.
     * 
     * @see IWindowListener#windowActivated(IWorkbenchWindow)
     */
    public void windowActivated(final IWorkbenchWindow window) {
    }
}