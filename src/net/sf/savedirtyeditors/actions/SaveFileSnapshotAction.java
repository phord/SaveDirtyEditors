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

import org.eclipse.ui.IFileEditorInput;

/**
 * A <code>SaveFileSnapshotAction</code> will save the {@link IFileEditorInput} that it is associated with into a
 * temporary area. If there is already a file for the same {@link IFileEditorInput} in the temp area, this will
 * overwrite the contents of that file with the contents of the {@link IFileEditorInput}
 */
public final class SaveFileSnapshotAction extends BaseFileSnapshotAction {
    /**
     * Constructor for SaveFileSnapshotAction.
     * 
     * @param fileEditorInput
     *            The non-null {@link IFileEditorInput} that this action performs the operation on.
     */
    public SaveFileSnapshotAction(final IFileEditorInput fileEditorInput) {
        super(fileEditorInput);
    }

    /**
     * Updates the snapshot for the <code>fileEditorInput</code> with the latest contents when this method is called.
     * Calling this method repeatedly will just keep overwriting the snapshot.
     */
    public void run() {
        PluginActivator.logDebug(buildLog("Save")); //$NON-NLS-1$
        // vrarem: flesh this out
    }
}