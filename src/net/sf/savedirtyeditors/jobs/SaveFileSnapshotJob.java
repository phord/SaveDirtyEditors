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
package net.sf.savedirtyeditors.jobs;

import net.sf.savedirtyeditors.PluginActivator;
import net.sf.savedirtyeditors.actions.DeleteFileSnapshotAction;
import net.sf.savedirtyeditors.actions.SaveFileSnapshotAction;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IFileEditorInput;
import org.osgi.framework.Bundle;

/**
 * A custom implementation of {@link Job} to schedule either the {@link SaveFileSnapshotAction} or the
 * {@link DeleteFileSnapshotAction}. This belongs to the Job family defined by {@link PluginActivator#JOB_FAMILY_NAME}.
 */
public final class SaveFileSnapshotJob extends Job {
    private static final long RESCHEDULE_DELAY = 300000;// 5 minutes

    private final Bundle systemBundle = Platform.getBundle("org.eclipse.osgi"); //$NON-NLS-1$

    private final IFileEditorInput fileEditorInput;
    private boolean completed = false;

    /**
     * Constructor for SaveFileSnapshotJob.
     * 
     * @param fileEditorInput
     *            The non-null {@link IFileEditorInput} for which the scheduled tasks need to be run.
     */
    public SaveFileSnapshotJob(final IFileEditorInput fileEditorInput) {
        super(fileEditorInput.getToolTipText());
        this.fileEditorInput = fileEditorInput;
        setSystem(true);
        setPriority(SHORT);
        schedule();
    }

    /**
     * Returns true if family is the same as defined by {@link PluginActivator#JOB_FAMILY_NAME}
     * 
     * @see Job#belongsTo(Object)
     */
    public boolean belongsTo(final Object family) {
        return PluginActivator.JOB_FAMILY_NAME.equals(family);
    }

    /**
     * Returns true if the specified {@link IFileEditorInput} is the <b>same</b> as the one that this Job was created
     * for.
     * 
     * @param fileEditorInput
     *            The IFileEditorInput that needs to be tested
     * @return True if the <code>fileEditorInput</code> is the same exact instance as the one that this Job was
     *         created for; false otherwise
     */
    public boolean isForInput(final IFileEditorInput fileEditorInput) {
        return this.fileEditorInput == fileEditorInput;
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

        IJobManager jobManager = Platform.getJobManager();
        IFile underlyingFile = fileEditorInput.getFile();
        try {
            jobManager.beginRule(underlyingFile, monitor);
            // HACKTAG: for 3.1 compatibility - cant use SafeRunner.run(ISafeRunnable)
            // Do the actual save by delegating to an action
            run(new SaveFileSnapshotAction(fileEditorInput));
        } finally {
            jobManager.endRule(underlyingFile);
        }
        schedule(RESCHEDULE_DELAY);
        return Status.OK_STATUS;
    }

    /**
     * Called when this Job needs to be shutdown. This method will run the {@link DeleteFileSnapshotAction} for the
     * <code>fileEditorInput</code> from this Job and then stop itself from being scheduled/run any more.
     */
    public void complete() {
        PluginActivator.logDebug("Completing snapshot for: " + fileEditorInput.getToolTipText()); //$NON-NLS-1$
        // Need to clean the temp area
        run(new DeleteFileSnapshotAction(fileEditorInput));
        completed = true;
        sleep();
        cancel();
    }

    private void run(final ISafeRunnable runnable) {
        // HACKTAG: Once we move to 3.2, remove deprecation
        Platform.run(runnable);
    }
}