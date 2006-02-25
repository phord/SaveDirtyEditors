package net.sf.savedirtyeditors.utils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * A utility class with commonly used static methods.
 */
public final class ResourceUtils {
    /**
     * Not intended for instantiation.
     */
    private ResourceUtils() {
        super();
    }

    /**
     * A convenience method to run {@link ISafeRunnable} instances.
     * 
     * @param runnable
     *            The code to run
     * @see Platform#run(ISafeRunnable)
     */
    public static final void run(final ISafeRunnable runnable) {
        // HACKTAG: for 3.1 compatibility - cant use SafeRunner.run(ISafeRunnable)
        // Once we move to 3.2, remove deprecation
        Platform.run(runnable);
    }

    /**
     * Convenience method to retrieve the underlying {@link IFile} object behind an {@link IEditorPart}
     * 
     * @param part
     *            The IEditorPart whose IFile has to be found
     * @return the file corresponding to the editorInput inside the editorPart, or <code>null</code>
     * @see ResourceUtil#getFile(org.eclipse.ui.IEditorInput)
     */
    public static final IFile getFile(final IEditorPart part) {
        return ResourceUtil.getFile(part.getEditorInput());
    }

    /**
     * Null-safe convenience method to retrieve the full path of the {@link IFile} (as returned by
     * {@link #getFile(IEditorPart)}) as an OS-portable string.
     * 
     * @param part
     *            The IEditorPart whose IFile's full path has to be found
     * @return The OS-portable full path of the IFile within the specified IEditorPart or <code>null</code>
     * @see org.eclipse.core.runtime.IPath#toPortableString()
     */
    public static final String getFullPathAsString(final IEditorPart part) {
        IFile file = getFile(part);
        return file == null ? null : file.getFullPath().toPortableString();
    }

    /**
     * Null-safe convenience method to retrieve the dirty contents of the {@link IEditorPart}.
     * 
     * @param part
     *            The IEditorPart whose content has to be found
     * @return The (dirty) contents of the editorPart or <code>null</code> if the editorPart is not a
     *         {@link ITextEditor} instance
     */
    public static final String getDirtyContents(final IEditorPart part) {
        if (!(part instanceof ITextEditor)) {
            return null;
        }
        ITextEditor editor = (ITextEditor) part;
        IDocumentProvider provider = editor.getDocumentProvider();
        IDocument document = provider.getDocument(editor.getEditorInput());
        return document == null ? null : document.get();
    }
}