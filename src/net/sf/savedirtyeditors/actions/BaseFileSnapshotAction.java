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
import org.eclipse.jface.util.Assert;
import org.eclipse.ui.IFileEditorInput;

/**
 * A base implementation of a snapshot action - providing common functionality for both the
 * {@link SaveFileSnapshotAction} and {@link DeleteFileSnapshotAction} concrete implementations.
 */
abstract class BaseFileSnapshotAction implements ISafeRunnable {
    /**
     * The file editor input that this action performs the operation on.
     */
    protected final IFileEditorInput fileEditorInput;

    /**
     * Constructor for BaseFileSnapshotAction.
     * 
     * @param fileEditorInput
     *            The non-null {@link IFileEditorInput} that this action performs the operation on.
     */
    BaseFileSnapshotAction(final IFileEditorInput fileEditorInput) {
        super();
        Assert.isNotNull(fileEditorInput, "Cannot associate an action with a null file editor input");
        this.fileEditorInput = fileEditorInput;
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
     * @return The more "rounded-out" log message that includes information about the fileEditorInput that this action
     *         is associated with.
     */
    protected final String buildLog(String message) {
        return message + " : " + fileEditorInput.getToolTipText(); //$NON-NLS-1$
    }
}