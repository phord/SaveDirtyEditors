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
package net.sf.savedirtyeditors.actions;

import net.sf.savedirtyeditors.PluginActivator;
import net.sf.savedirtyeditors.utils.Messages;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ui.IEditorPart;

/**
 * A <code>DeleteSnapshotAction</code> will delete the {@link IEditorPart} that it is associated with from a temporary
 * area. If there is a file for the same {@link IEditorPart} in the temp area, this will overwrite the contents of that
 * file with the contents of the {@link IEditorPart}.
 */
public final class DeleteSnapshotAction extends BaseSnapshotAction {
    /**
     * Constructor for DeleteSnapshotAction.
     * 
     * @param editorPart
     *            The non-null {@link IEditorPart} that this action performs the operation on.
     */
    public DeleteSnapshotAction(final IEditorPart editorPart) {
        super(editorPart);
    }

    /**
     * Deletes the snapshot that was created by the {@link SaveSnapshotAction} for the <code>editorPart</code>.
     * 
     * @exception CoreException
     *                if this method fails. Reasons include:
     *                <ul>
     *                <li> This resource could not be deleted for some reason.</li>
     *                <li> This resource or one of its descendents is out of sync with the local file system and
     *                <code>force</code> is <code>false</code>.</li>
     *                <li> Resource changes are disallowed during certain types of resource change event notification.
     *                See <code>IResourceChangeEvent</code> for more details.</li>
     *                </ul>
     * @exception OperationCanceledException
     *                if the operation is canceled. Cancelation can occur even if no progress monitor is provided.
     * @see IResource#delete(boolean,IProgressMonitor)
     * @see ISafeRunnable#run
     */
    public void run() throws CoreException {
        // NOTE: dont verify if the editorPart is dirty - it might have been dirty at some point, but subsequently
        // saved, and the snapshot file would still be present since this is the only place where we delete the snapshot

        IFile snapshotFile = getSnapshotFile();
        // if the snapshotFile does not exist OR it is the same as the original file - dont proceed any further
        if (!snapshotFile.exists() || snapshotFile.equals(getOriginalFile())) {
            return;
        }

        PluginActivator.logDebug(buildLog(Messages.getString("DeleteSnapshotAction.delete"))); //$NON-NLS-1$

        // NOTE: dont verify the timestamps - since the snapshot might be newer if the user selected to NOT save the
        // changes, and just closed the editor
        snapshotFile.delete(true, new NullProgressMonitor());
    }
}