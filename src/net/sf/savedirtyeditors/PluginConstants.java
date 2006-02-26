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
package net.sf.savedirtyeditors;

import net.sf.savedirtyeditors.utils.Messages;

/**
 * An interface storing all constants used within this plugin.
 */
public interface PluginConstants {
    /**
     * The id of the plugin (as defined in the plugin.xml or manifest.mf file)
     */
    String PLUGIN_ID = "SaveDirtyEditors"; //$NON-NLS-1$

    /**
     * The name of the family to which all jobs created by this plugin belong to.
     */
    String JOB_FAMILY_NAME = PLUGIN_ID + ":Jobs"; //$NON-NLS-1$

    /**
     * Error status message.
     */
    String ERROR = "Error"; //$NON-NLS-1$

    /**
     * Key for storing the reschedule delay.
     */
    String KEY_RESCHEDULE_DELAY = "reschedule.delay"; //$NON-NLS-1$

    /**
     * Default value for reschedule delay.
     */
    long DEFAULT_RESCHEDULE_DELAY = 300000;// 5 mins

    /**
     * Key for storing snapshot name prefix.
     */
    String KEY_SNAPSHOT_NAME_PREFIX = "snapshot.name.prefix"; //$NON-NLS-1$

    /**
     * Default value for snapshot name prefix.
     */
    String DEFAULT_SNAPSHOT_NAME_PREFIX = Messages.getString(KEY_SNAPSHOT_NAME_PREFIX);

    /**
     * Key for storing snapshot name suffix.
     */
    String KEY_SNAPSHOT_NAME_SUFFIX = "snapshot.name.suffix"; //$NON-NLS-1$

    /**
     * Default value for snapshot name suffix.
     */
    String DEFAULT_SNAPSHOT_NAME_SUFFIX = Messages.getString(KEY_SNAPSHOT_NAME_SUFFIX);
}