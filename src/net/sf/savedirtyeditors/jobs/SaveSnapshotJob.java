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
package net.sf.savedirtyeditors.jobs;

import net.sf.savedirtyeditors.PluginActivator;
import net.sf.savedirtyeditors.PluginConstants;
import net.sf.savedirtyeditors.actions.DeleteSnapshotAction;
import net.sf.savedirtyeditors.actions.ReconcileSnapshotAction;
import net.sf.savedirtyeditors.actions.SaveSnapshotAction;
import net.sf.savedirtyeditors.utils.Messages;
import net.sf.savedirtyeditors.utils.ResourceUtils;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IEditorPart;
import org.osgi.framework.Bundle;

/**
 * A custom implementation of {@link Job} to schedule either the {@link SaveSnapshotAction} or the
 * {@link DeleteSnapshotAction}. This belongs to the Job family defined by {@link PluginConstants#JOB_FAMILY_NAME}.
 */
public final class SaveSnapshotJob extends Job {
    private final Bundle systemBundle = Platform.getBundle("org.eclipse.osgi"); //$NON-NLS-1$

    private final IEditorPart editorPart;
    private boolean completed = false;
    private boolean firstTime = true;

    /**
     * Constructor for SaveSnapshotJob.
     * 
     * @param editorPart
     *            The non-null {@link IEditorPart} for which the scheduled tasks need to be run.
     */
    public SaveSnapshotJob(final IEditorPart editorPart) {
        super(Messages.getString("SaveSnapshotJob.running") + ResourceUtils.getFullPathAsString(editorPart)); //$NON-NLS-1$
        this.editorPart = editorPart;
        setSystem(true);
        setPriority(SHORT);
        schedule();
    }

    /**
     * Returns true if family is the same as defined by {@link PluginConstants#JOB_FAMILY_NAME}
     * 
     * @see Job#belongsTo(Object)
     */
    public boolean belongsTo(final Object family) {
        return PluginConstants.JOB_FAMILY_NAME.equals(family);
    }

    /**
     * Returns true if the specified {@link IEditorPart} is the <b>same</b> as the one that this Job was created for.
     * 
     * @param editorPart
     *            The IEditorPart that needs to be tested
     * @return True if the <code>editorPart</code> is the same exact instance as the one that this Job was created
     *         for; false otherwise
     */
    public boolean isForInput(final IEditorPart editorPart) {
        return this.editorPart == editorPart;
    }

    /**
     * @see Job#shouldRun()
     */
    public boolean shouldRun() {
        return !completed;
    }

    /**
     * @see Job#shouldSchedule()
     */
    public boolean shouldSchedule() {
        return !completed;
    }

    /**
     * @see Job#run(IProgressMonitor)
     */
    protected IStatus run(final IProgressMonitor monitor) {
        // if the system is shutting down, don't build
        if (systemBundle.getState() == Bundle.STOPPING) {
            return Status.OK_STATUS;
        }

        PluginActivator
                .logDebug(Messages.getString("SaveSnapshotJob.running") + ResourceUtils.getFullPathAsString(editorPart)); //$NON-NLS-1$

        final IJobManager jobManager = Platform.getJobManager();
        final ISchedulingRule rule = ResourcesPlugin.getWorkspace().getRoot();
        try {
            jobManager.beginRule(rule, monitor);

            if (firstTime) {
                // Need to run the reconcile action
                ResourceUtils.run(new ReconcileSnapshotAction(editorPart));
                firstTime = false;
            } else {
                // Do the actual save by delegating to an action
                ResourceUtils.run(new SaveSnapshotAction(editorPart));
            }
        } finally {
            jobManager.endRule(rule);
        }
        schedule(PluginActivator.getLongPreference(PluginConstants.KEY_RESCHEDULE_DELAY));
        return Status.OK_STATUS;
    }

    /**
     * Called when this Job needs to be shutdown. This method will run the {@link DeleteSnapshotAction} for the
     * <code>editorPart</code> from this Job and then stop itself from being scheduled/run any more.
     */
    public void complete() {
        PluginActivator
                .logDebug(Messages.getString("SaveSnapshotJob.complete") + ResourceUtils.getFullPathAsString(editorPart)); //$NON-NLS-1$
        // Need to clean the temp area
        ResourceUtils.run(new DeleteSnapshotAction(editorPart));
        completed = true;
        sleep();
        cancel();
    }
}