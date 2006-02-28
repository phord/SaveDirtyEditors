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
package net.sf.savedirtyeditors.filters;

import net.sf.savedirtyeditors.PluginActivator;
import net.sf.savedirtyeditors.PluginConstants;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * Filter out resources created by this plugin based on user preference settings for the name prefix and suffix.
 */
public class SnapshotResourceFilters extends ViewerFilter {
    private final String snapshotNamePrefix;
    private final String snapshotNameSuffix;
    private boolean shouldApply = false;

    /**
     * Constructor for SnapshotResourceFilters.
     */
    public SnapshotResourceFilters() {
        super();

        // since there's only one filter per target (registered view) - its better performance-wise to retrieve these
        // settings here - of course the caveat is that once the user changes these in the preference dialog, these will
        // need to be refreshed by closing and opening the view again
        snapshotNamePrefix = PluginActivator.getStringPreference(PluginConstants.KEY_SNAPSHOT_NAME_PREFIX);
        snapshotNameSuffix = PluginActivator.getStringPreference(PluginConstants.KEY_SNAPSHOT_NAME_SUFFIX);
        shouldApply = isNotTrivial(snapshotNamePrefix) || isNotTrivial(snapshotNameSuffix);
    }

    /**
     * Returns whether the given element makes it through this filter.
     * 
     * @param viewer
     *            the viewer
     * @param parentElement
     *            the parent element
     * @param element
     *            the element
     * @return <code>true</code> if element is included in the filtered set, and <code>false</code> if excluded
     * @see ViewerFilter#select(Viewer, Object, Object)
     */
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        // if both the prefix and suffix are empty - then dont proceed (or else we will filter out every member!)
        if (shouldApply && (element instanceof IResource)) {
            String name = ((IResource) element).getName();
            return !(name.startsWith(snapshotNamePrefix) && name.endsWith(snapshotNameSuffix));
        }
        return true;
    }

    private boolean isNotTrivial(String str) {
        return str != null && str.trim().length() > 0;
    }
}