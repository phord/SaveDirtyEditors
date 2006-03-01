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
import net.sf.savedirtyeditors.utils.ResourceUtils;

import org.eclipse.compare.internal.CompareAction;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;

/**
 * A <code>ReconcileSnapshotAction</code> will be called when the wo save the {@link IEditorPart} that it is
 * associated with into a temporary area. If there is already a file for the same {@link IEditorPart} in the temp area,
 * this will overwrite the contents of that file with the contents of the {@link IEditorPart}
 */
public class ReconcileSnapshotAction extends BaseSnapshotAction {
    /**
     * Constructor for ReconcileSnapshotAction.
     * 
     * @param editorPart
     *            The non-null {@link IEditorPart} that this action performs the operation on.
     */
    public ReconcileSnapshotAction(final IEditorPart editorPart) {
        super(editorPart);
    }

    /**
     * If the Eclipse workbench was shutdown abnormally, this will reconcile the snapshot that was created by the
     * {@link SaveSnapshotAction} for the <code>editorPart</code> with the underlying contents of the {@link IFile}
     * within the <code>editorPart</code>.
     * 
     * @exception CoreException
     *                if this method fails. Reasons include:
     *                <ul>
     *                <li> Resource changes are disallowed during certain types of resource change event notification.
     *                See <code>IResourceChangeEvent</code> for more details.</li>
     *                </ul>
     * @exception OperationCanceledException
     *                if the operation is canceled. Cancelation can occur even if no progress monitor is provided.
     * @see IResource#refreshLocal(int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void run() throws CoreException {
        // for some reason the snapshot file is not being recognized as being already present unless we refresh
        ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());

        final IFile snapshotFile = getSnapshotFile();
        // if the snapshotFile does not exist OR it is the same as the original file - dont proceed any further
        if (!snapshotFile.exists() || snapshotFile.equals(getOriginalFile())) {
            return;
        }

        PluginActivator.logDebug(buildLog(Messages.getString("ReconcileSnapshotAction.reconcile"))); //$NON-NLS-1$

        // even if the user had decided to rollback the changes, the only way we can come here is if Eclipse crashed and
        // so the snapshot file is still present
        ResourceUtils.getDisplay(editorPart).asyncExec(new Runnable() {
            public void run() {
                final boolean confirmation = MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), Messages
                        .getString("ReconcileSnapshotAction.reconcile.prompt.title"), //$NON-NLS-1$
                        Messages.getString("ReconcileSnapshotAction.reconcile.prompt.message") //$NON-NLS-1$
                                + ResourceUtils.getFullPathAsString(editorPart));
                if (confirmation) {
                    final CompareAction compareAction = new CompareAction();
                    compareAction.selectionChanged(new Action() {
                    }, new StructuredSelection(new Object[] { getOriginalFile(), snapshotFile }));
                    compareAction.run((Action) null);
                }
            }
        });
    }
}