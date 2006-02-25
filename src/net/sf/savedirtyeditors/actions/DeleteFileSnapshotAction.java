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
package net.sf.savedirtyeditors.actions;

import net.sf.savedirtyeditors.PluginActivator;
import net.sf.savedirtyeditors.utils.Messages;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IEditorPart;

/**
 * A <code>DeleteFileSnapshotAction</code> will delete the {@link IEditorPart} that it is associated with from a
 * temporary area if there is a file for the same {@link IEditorPart} in the temp area, this will overwrite the contents
 * of that file with the contents of the {@link IEditorPart}
 */
public final class DeleteFileSnapshotAction extends BaseFileSnapshotAction {
    /**
     * Constructor for DeleteFileSnapshotAction.
     * 
     * @param editorPart
     *            The non-null {@link IEditorPart} that this action performs the operation on.
     */
    public DeleteFileSnapshotAction(final IEditorPart editorPart) {
        super(editorPart);
    }

    /**
     * Deletes the snapshot that was created by the {@link SaveFileSnapshotAction} for the <code>editorPart</code>.
     * 
     * @see ISafeRunnable#run
     */
    public void run() {
        PluginActivator.logDebug(buildLog(Messages.getString("DeleteFileSnapshotAction.delete"))); //$NON-NLS-1$

        // NOTE: dont verify if the editorPart is dirty - it might have been dirty at some point, but subsequently
        // saved, and the snapshot file would still be present since this is the only place where we delete the snapshot

        try {
            // TODO: ONLY delete if the timestamp of the original file is >= the timestamp of the snapshot
            getSnapshotFile().delete(true, new NullProgressMonitor());
        } catch (CoreException exc) {
            PluginActivator.logError(exc);
        }
    }
}