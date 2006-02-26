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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import net.sf.savedirtyeditors.PluginActivator;
import net.sf.savedirtyeditors.utils.Messages;
import net.sf.savedirtyeditors.utils.ResourceUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ui.IEditorPart;

/**
 * A <code>SaveSnapshotAction</code> will save the {@link IEditorPart} that it is associated with into a temporary
 * area. If there is already a file for the same {@link IEditorPart} in the temp area, this will overwrite the contents
 * of that file with the contents of the {@link IEditorPart}
 */
public final class SaveSnapshotAction extends BaseSnapshotAction {
    /**
     * Constructor for SaveSnapshotAction.
     * 
     * @param editorPart
     *            The non-null {@link IEditorPart} that this action performs the operation on.
     */
    public SaveSnapshotAction(final IEditorPart editorPart) {
        super(editorPart);
    }

    /**
     * Updates the snapshot for the <code>editorPart</code> with the latest contents when this method is called.
     * Calling this method repeatedly will just keep overwriting the snapshot.
     * 
     * @exception CoreException
     *                if this method fails. Reasons include:
     *                <ul>
     *                <li> The parent of this resource does not exist.</li>
     *                <li> The project of this resource is not accessible.</li>
     *                <li> The parent contains a resource of a different type at the same path as this resource.</li>
     *                <li> The name of this resource is not valid (according to <code>IWorkspace.validateName</code>).</li>
     *                <li> The corresponding location in the local file system is occupied by a directory.</li>
     *                <li> The corresponding location in the local file system is occupied by a file and
     *                <code>force </code> is <code>false</code>.</li>
     *                <li> Resource changes are disallowed during certain types of resource change event notification.
     *                See <code>IResourceChangeEvent</code> for more details.</li>
     *                </ul>
     *                <li> The workspace is not in sync with the corresponding location in the local file system and
     *                <code>force </code> is <code>false</code>.</li>
     *                <li> The file modification validator disallowed the change.</li>
     * @exception UnsupportedEncodingException
     *                If the named charset is not supported
     * @exception OperationCanceledException
     *                if the operation is canceled. Cancelation can occur even if no progress monitor is provided.
     * @see IFile#create(InputStream, boolean, IProgressMonitor)
     * @see IFile#setContents(InputStream, boolean, boolean, IProgressMonitor)
     * @see ISafeRunnable#run
     */
    public void run() throws CoreException, UnsupportedEncodingException {
        PluginActivator.logDebug(buildLog(Messages.getString("SaveSnapshotAction.save"))); //$NON-NLS-1$

        // if the editorPart is not dirty - dont proceed any further
        if (!editorPart.isDirty()) {
            return;
        }

        IFile origFile = getOriginalFile();
        IFile snapshotFile = getSnapshotFile();
        InputStream inputStream = null;
        String dirtyContents = ResourceUtils.getDirtyContents(editorPart);
        try {
            inputStream = new ByteArrayInputStream(dirtyContents.getBytes(origFile.getCharset()));
            if (!snapshotFile.exists()) {
                snapshotFile.create(inputStream, true, new NullProgressMonitor());
            } else {
                snapshotFile.setContents(inputStream, true, false, new NullProgressMonitor());
            }
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException exc) {
                    PluginActivator.logError(exc);
                }
            }
        }
    }
}