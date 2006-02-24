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

public final class SaveFileSnapshotJob extends Job {
    private static final long RESCHEDULE_DELAY = 300000;// 5 minutes

    private final Bundle systemBundle = Platform.getBundle("org.eclipse.osgi"); //$NON-NLS-1$

    private final IFileEditorInput fileEditorInput;
    private boolean completed = false;

    public SaveFileSnapshotJob(final IFileEditorInput fileEditorInput) {
        super(fileEditorInput.getToolTipText());
        this.fileEditorInput = fileEditorInput;
        setSystem(true);
        setPriority(SHORT);
        schedule();
    }

    public boolean belongsTo(final Object family) {
        return PluginActivator.JOB_FAMILY_NAME.equals(family);
    }

    public boolean isForInput(IFileEditorInput fileEditorInput) {
        return this.fileEditorInput == fileEditorInput;
    }

    public boolean shouldRun() {
        return !completed;
    }

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

    public void complete() {
        PluginActivator.logDebug("Completing snapshot for: " + fileEditorInput.getToolTipText()); //$NON-NLS-1$
        // Need to clean the temp area
        run(new DeleteFileSnapshotAction(fileEditorInput));
        completed = true;
        sleep();
        cancel();
    }

    private void run(ISafeRunnable runnable) {
        // HACKTAG: Once we move to 3.2, remove deprecation
        Platform.run(runnable);
    }
}