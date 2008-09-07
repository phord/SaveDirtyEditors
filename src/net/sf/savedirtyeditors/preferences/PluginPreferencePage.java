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

import net.sf.savedirtyeditors.PluginActivator;
import net.sf.savedirtyeditors.PluginConstants;
import net.sf.savedirtyeditors.utils.Messages;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * A simple implementation of {@link PreferencePage} for setting user-editable values for this plugin.
 */
public class PluginPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
    private FieldEditor snapshotNamePrefix;
    private FieldEditor snapshotNameSuffix;
    private FieldEditor rescheduleDelay;

    /**
     * @see PreferencePage#createContents(Composite)
     */
    @Override
    protected Control createContents(final Composite parent) {
        final Composite composite = createComposite(parent);

        Composite spacingComposite = SWTHelper.createGroupComposite(composite, 2, Messages
                .getString("snapshot.file.group")); //$NON-NLS-1$
        snapshotNamePrefix = createStringFieldEditor(spacingComposite, PluginConstants.KEY_SNAPSHOT_NAME_PREFIX,
                PluginConstants.LABEL_SNAPSHOT_NAME_PREFIX);
        snapshotNameSuffix = createStringFieldEditor(spacingComposite, PluginConstants.KEY_SNAPSHOT_NAME_SUFFIX,
                PluginConstants.LABEL_SNAPSHOT_NAME_SUFFIX);

        SWTHelper.createSpacer(composite, 2);

        spacingComposite = SWTHelper.createGroupComposite(composite, 2, Messages.getString("reschedule.group")); //$NON-NLS-1$
        rescheduleDelay = createIntegerFieldEditor(spacingComposite, PluginConstants.KEY_RESCHEDULE_DELAY,
                PluginConstants.LABEL_RESCHEDULE_DELAY);

        applyDialogFont(composite);

        return composite;
    }

    /**
     * Resets values in the widgets to their defaults.
     * 
     * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
     */
    @Override
    protected void performDefaults() {
        snapshotNamePrefix.loadDefault();
        snapshotNameSuffix.loadDefault();
        rescheduleDelay.loadDefault();
        super.performDefaults();
    }

    /**
     * Applies the the values entered to the in-memory configuration and stores them in the preference store.
     * 
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
    @Override
    public boolean performOk() {
        snapshotNamePrefix.store();
        snapshotNameSuffix.store();
        rescheduleDelay.store();
        updateApplyButton();
        return super.performOk();
    }

    private FieldEditor createStringFieldEditor(final Composite spacingComposite, final String preferenceKey,
            final String labelText) {
        final FieldEditor editor = new StringFieldEditor(preferenceKey, labelText, spacingComposite);
        editor.setPreferenceStore(PluginActivator.getDefault().getPreferenceStore());
        editor.setPage(this);
        editor.load();
        return editor;
    }

    private FieldEditor createIntegerFieldEditor(final Composite spacingComposite, final String preferenceKey,
            final String labelText) {
        final IntegerFieldEditor editor = new IntegerFieldEditor(preferenceKey, labelText, spacingComposite);
        editor.setPreferenceStore(PluginActivator.getDefault().getPreferenceStore());
        editor.setValidRange(5000, Integer.MAX_VALUE);
        editor.setErrorMessage(editor.getErrorMessage()
                + Messages.getString("reschedule.delay.invalid") + Integer.MAX_VALUE); //$NON-NLS-1$
        editor.setPage(this);
        editor.load();
        return editor;
    }

    /**
     * Creates the composite which will contain all the preference controls for this page.
     * 
     * @param parent
     *            the parent composite
     * @return the composite for this page
     */
    private Composite createComposite(final Composite parent) {
        final Composite composite = new Composite(parent, SWT.NONE);
        final GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        return composite;
    }

    /**
     * @see IWorkbenchPreferencePage#init(IWorkbench)
     */
    public void init(final IWorkbench workbench) {
        // do nothing
    }

    /**
     * Disposes any internal undisposed SWT widgets.
     */
    @Override
    public void dispose() {
        if (snapshotNamePrefix != null) {
            snapshotNamePrefix.dispose();
            snapshotNamePrefix = null;
        }
        if (snapshotNameSuffix != null) {
            snapshotNameSuffix.dispose();
            snapshotNameSuffix = null;
        }
        if (rescheduleDelay != null) {
            rescheduleDelay.dispose();
            rescheduleDelay = null;
        }

        super.dispose();
    }
}