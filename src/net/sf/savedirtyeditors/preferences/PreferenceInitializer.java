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
package net.sf.savedirtyeditors.preferences;

import net.sf.savedirtyeditors.PluginConstants;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 * An implementation of {@link AbstractPreferenceInitializer} used to initialize preferences when the plugin is started.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {
    /**
     * @see AbstractPreferenceInitializer#initializeDefaultPreferences()
     */
    @Override
    public void initializeDefaultPreferences() {
        final IEclipsePreferences node = new DefaultScope().getNode(PluginConstants.PLUGIN_ID);

        // auto-snapshot default
        node.putLong(PluginConstants.KEY_RESCHEDULE_DELAY, PluginConstants.DEFAULT_RESCHEDULE_DELAY);

        // snapshot file name
        node.put(PluginConstants.KEY_SNAPSHOT_NAME_PREFIX, PluginConstants.DEFAULT_SNAPSHOT_NAME_PREFIX);
        node.put(PluginConstants.KEY_SNAPSHOT_NAME_SUFFIX, PluginConstants.DEFAULT_SNAPSHOT_NAME_SUFFIX);
    }
}