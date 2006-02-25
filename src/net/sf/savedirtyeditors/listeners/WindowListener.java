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
import net.sf.savedirtyeditors.utils.Messages;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * An implementation of {@link IWindowListener} that is used to
 * <ul>
 * <li>wake up all {@link Job}s pertaining to this plugin's family and perform their respective operations even though
 * their scheduled times might not have arrived.</li>
 * <li>listen to new {@link IWorkbenchWindow}s being opened or closed and add {@link IPartListener} to their
 * respective {@link IPartService}s
 * </ul>
 */
public final class WindowListener implements IWindowListener {
    private PartListener partListener = new PartListener();

    /**
     * When a new {@link IWorkbenchWindow} is opened, adds the {@link IPartListener} to the {@link IPartService} of that
     * {@link IWorkbenchWindow}
     * 
     * @param window
     *            The {@link IWorkbenchWindow} that was opened
     */
    public void windowOpened(final IWorkbenchWindow window) {
        PluginActivator.logDebug(Messages.getString("WindowListener.window.monitoring")); //$NON-NLS-1$
        window.getPartService().addPartListener(partListener);
    }

    /**
     * When a {@link IWorkbenchWindow} is closed, removes the {@link IPartListener} to the {@link IPartService} of that
     * {@link IWorkbenchWindow}
     * 
     * @param window
     *            The {@link IWorkbenchWindow} that was closed
     */
    public void windowClosed(final IWorkbenchWindow window) {
        PluginActivator.logDebug(Messages.getString("WindowListener.window.unmonitoring")); //$NON-NLS-1$
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