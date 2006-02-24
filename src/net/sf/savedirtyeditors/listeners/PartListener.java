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
import net.sf.savedirtyeditors.jobs.SaveFileSnapshotJob;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IWorkbenchPart;

public final class PartListener implements IPartListener {
    public void partOpened(final IWorkbenchPart part) {
        // if the part is not an EditorPart - just quit
        if (!canProcess(part)) {
            return;
        }

        PluginActivator.logDebug("Monitoring part"); //$NON-NLS-1$

        // create a scheduled job which will in turn call the save action at specified time intervals
        IEditorPart editorPart = (IEditorPart) part;
        IPersistableElement persistable = editorPart.getEditorInput().getPersistable();
        if (persistable instanceof IFileEditorInput) {
            new SaveFileSnapshotJob((IFileEditorInput) persistable);
        }
    }

    public void partClosed(final IWorkbenchPart part) {
        // if the part is not an EditorPart - just quit
        if (!canProcess(part)) {
            return;
        }

        PluginActivator.logDebug("Unmonitoring part"); //$NON-NLS-1$

        // Remove job
        IEditorPart editorPart = (IEditorPart) part;
        IPersistableElement persistable = editorPart.getEditorInput().getPersistable();
        if (persistable instanceof IFileEditorInput) {
            Job[] jobs = Platform.getJobManager().find(PluginActivator.JOB_FAMILY_NAME);
            for (int i = 0; i < jobs.length; i++) {
                SaveFileSnapshotJob job = (SaveFileSnapshotJob) jobs[i];
                if (job.isForInput((IFileEditorInput) persistable)) {
                    job.complete();
                }
            }
        }
    }

    private boolean canProcess(final IWorkbenchPart part) {
        return part instanceof IEditorPart;
    }

    /**
     * Dummy implementation since we are not interested in this life-cycle event.
     * 
     * @see IPartListener#partActivated(IWorkbenchPart)
     */
    public void partActivated(final IWorkbenchPart part) {
    }

    /**
     * Dummy implementation since we are not interested in this life-cycle event.
     * 
     * @see IPartListener#partBroughtToTop(IWorkbenchPart)
     */
    public void partBroughtToTop(final IWorkbenchPart part) {
    }

    /**
     * Dummy implementation since we are not interested in this life-cycle event.
     * 
     * @see IPartListener#partDeactivated(IWorkbenchPart)
     */
    public void partDeactivated(final IWorkbenchPart part) {
    }
}