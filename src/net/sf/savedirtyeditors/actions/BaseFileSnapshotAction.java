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
import net.sf.savedirtyeditors.utils.ResourceUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.jface.util.Assert;
import org.eclipse.ui.IEditorPart;

/**
 * A base implementation of a snapshot action - providing common functionality for both the
 * {@link SaveFileSnapshotAction} and {@link DeleteFileSnapshotAction} concrete implementations. This class should not
 * be visible outside this package.
 */
abstract class BaseFileSnapshotAction implements ISafeRunnable {
    /**
     * The file editor input that this action performs the operation on.
     */
    protected final IEditorPart editorPart;

    /**
     * Constructor for BaseFileSnapshotAction. Should not be visible from outside this package - so no qualifier
     * 
     * @param editorPart
     *            The non-null {@link IEditorPart} that this action performs the operation on.
     */
    BaseFileSnapshotAction(final IEditorPart editorPart) {
        super();
        Assert.isNotNull(editorPart, Messages.getString("BaseFileSnapshotAction.null_editorPart")); //$NON-NLS-1$
        this.editorPart = editorPart;
    }

    /**
     * Common place to create the snapshot {@link IFile} based on the underlying {@link IFile} from the
     * <code>editorPath</code>
     * 
     * @return A reference to the IFile (at the same physical folder location as the original file, but with the name
     *         having a preceding '~'
     */
    protected final IFile getSnapshotFile() {
        IFile origFile = ResourceUtils.getFile(editorPart);
        IPath origFileFolder = origFile.getParent().getFullPath();
        IPath snapshotPath = origFileFolder.addTrailingSeparator().append(Messages.getString("BaseFileSnapshotAction.snapshot.name.prefix") + origFile.getName()); //$NON-NLS-1$
        return ResourcesPlugin.getWorkspace().getRoot().getFile(snapshotPath);
    }

    /**
     * @see ISafeRunnable#handleException(Throwable)
     */
    public final void handleException(final Throwable throwable) {
        PluginActivator.logError(throwable);
    }

    /**
     * Utility method to build similar log messages for all subclasses.
     * 
     * @param message
     *            The text prefix for this log message.
     * @return The more "rounded-out" log message that includes information about the editorPart's underlying IFile that
     *         this action is associated with.
     */
    protected final String buildLog(final String message) {
        return message + " : " + ResourceUtils.getFullPathAsString(editorPart); //$NON-NLS-1$
    }
}