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
package net.sf.savedirtyeditors.listeners;

import net.sf.savedirtyeditors.PluginActivator;
import net.sf.savedirtyeditors.PluginConstants;
import net.sf.savedirtyeditors.jobs.SaveSnapshotJob;
import net.sf.savedirtyeditors.utils.Messages;
import net.sf.savedirtyeditors.utils.ResourceUtils;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;

/**
 * An implementation of {@link IPartListener} that is used to start and stop {@link SaveSnapshotJob} instances for each
 * {@link IEditorPart} that was opened/closed.
 */
public final class PartListener implements IPartListener {
    /**
     * If the {@link IWorkbenchPart} passed in is an instance of {@link IEditorPart}, then it creates a new instance of
     * {@link SaveSnapshotJob} for that specific {@link IEditorPart}
     * 
     * @param part
     *            The {@link IWorkbenchPart} that was opened
     * @see IPartListener#partOpened(IWorkbenchPart)
     */
    public void partOpened(final IWorkbenchPart part) {
        // if the part is not an EditorPart - just quit
        if (!canProcess(part)) {
            return;
        }

        PluginActivator.logDebug(Messages.getString("PartListener.part.monitoring")); //$NON-NLS-1$

        // create a scheduled job which will in turn call the save action at specified time intervals
        new SaveSnapshotJob((IEditorPart) part);
    }

    /**
     * If the {@link IWorkbenchPart} passed in is an instance of {@link IEditorPart}, then it finds the specific
     * {@link Job} associated with that {@link IEditorPart} and marks it as completed.
     * 
     * @param part
     *            The {@link IWorkbenchPart} that was closed
     * @see IPartListener#partClosed(IWorkbenchPart)
     */
    public void partClosed(final IWorkbenchPart part) {
        // if the part is not an EditorPart - just quit
        if (!canProcess(part)) {
            return;
        }

        PluginActivator.logDebug(Messages.getString("PartListener.part.unmonitoring")); //$NON-NLS-1$

        // Remove job
        final SaveSnapshotJob job = findJobFor((IEditorPart) part);
        if (job != null) {
            job.complete();
        }
    }

    /**
     * If the {@link IWorkbenchPart} passed in is an instance of {@link IEditorPart}, then it finds the specific
     * {@link Job} associated with that {@link IEditorPart} and wakes it up.
     * 
     * @param part
     *            The {@link IWorkbenchPart} that was activated
     * @see IPartListener#partActivated(IWorkbenchPart)
     */
    public void partActivated(final IWorkbenchPart part) {
        // NOTE: IF performance is an issue - we could put the job to sleep/wake cycles
        // if the part is not an EditorPart - just quit
        if (!canProcess(part)) {
            return;
        }

        PluginActivator.logDebug(Messages.getString("PartListener.part.monitoring")); //$NON-NLS-1$

        // Remove job
        final SaveSnapshotJob job = findJobFor((IEditorPart) part);
        if (job != null) {
            job.wakeUp();
        }
    }

    /**
     * If the {@link IWorkbenchPart} passed in is an instance of {@link IEditorPart}, then it finds the specific
     * {@link Job} associated with that {@link IEditorPart} and put it to sleep.
     * 
     * @param part
     *            The {@link IWorkbenchPart} that was deactivated
     * @see IPartListener#partDeactivated(IWorkbenchPart)
     */
    public void partDeactivated(final IWorkbenchPart part) {
        // NOTE: IF performance is an issue - we could put the job to sleep/wake cycles
        // if the part is not an EditorPart - just quit
        if (!canProcess(part)) {
            return;
        }

        PluginActivator.logDebug(Messages.getString("PartListener.part.unmonitoring")); //$NON-NLS-1$

        // Remove job
        final SaveSnapshotJob job = findJobFor((IEditorPart) part);
        if (job != null) {
            job.sleep();
        }
    }

    /**
     * Dummy implementation since we are not interested in this life-cycle event.
     * 
     * @see IPartListener#partBroughtToTop(IWorkbenchPart)
     */
    public void partBroughtToTop(final IWorkbenchPart part) {
    }

    private SaveSnapshotJob findJobFor(final IEditorPart part) {
        final Job[] jobs = Job.getJobManager().find(PluginConstants.JOB_FAMILY_NAME);
        for (int i = 0; i < jobs.length; i++) {
            final SaveSnapshotJob job = (SaveSnapshotJob) jobs[i];
            if (job.isForInput(part)) {
                return job;
            }
        }
        return null;
    }

    private boolean canProcess(final IWorkbenchPart part) {
        return (part instanceof IEditorPart) && (ResourceUtils.getFile((IEditorPart) part) != null);
    }
}