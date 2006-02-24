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

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.ui.IFileEditorInput;

/**
 * A <code>DeleteFileSnapshotAction</code> will delete the {@link IFileEditorInput} that it is associated with from a
 * temporary area if there is a file for the same {@link IFileEditorInput} in the temp area, this will overwrite the
 * contents of that file with the contents of the {@link IFileEditorInput}
 */
public final class DeleteFileSnapshotAction extends BaseFileSnapshotAction {
    /**
     * Constructor for DeleteFileSnapshotAction.
     * 
     * @param fileEditorInput
     *            The non-null {@link IFileEditorInput} that this action performs the operation on.
     */
    public DeleteFileSnapshotAction(final IFileEditorInput fileEditorInput) {
        super(fileEditorInput);
    }

    /**
     * Deletes the snapshot that was created by the {@link SaveFileSnapshotAction} for the <code>fileEditorInput</code>.
     * 
     * @see ISafeRunnable#run
     */
    public void run() {
        PluginActivator.logDebug(buildLog("Clean")); //$NON-NLS-1$
        // todo: flesh this out
        // ONLY delete if the timestamp of the underlying file is >= the timestamp of the snapshot
    }
}