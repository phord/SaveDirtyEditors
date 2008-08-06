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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Widget;

/**
 * A class containing common utility methods.
 */
public class SWTHelper {
    /**
     * Private constructor for SWTHelper so that no one instantiates this class.
     */
    private SWTHelper() {
        super();
    }

    /**
     * Creates a <code>Composite</code> with the specified parent and the specified label.
     * 
     * @param composite
     *            The <code>Composite</code> inside which the spacer cell has to be created
     * @param numColumns
     *            The number of columns that will be present in the composite
     * @param label
     *            The label text of the group composite
     * @return Returns the composite with the specified parent and label.
     */
    public static final Composite createGroupComposite(final Composite composite, final int numColumns,
            final String label) {
        final Font font = composite.getFont();

        final Group groupComposite = new Group(composite, SWT.NONE);
        groupComposite.setLayout(new GridLayout());
        groupComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        groupComposite.setText(label);
        groupComposite.setFont(font);

        // Add in an intermediate composite to allow for spacing
        final Composite spacingComposite = new Composite(groupComposite, SWT.NONE);
        spacingComposite.setLayout(new GridLayout(numColumns, false));
        spacingComposite.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL));
        spacingComposite.setFont(font);
        return spacingComposite;
    }

    /**
     * Creates a spacer and sets the default configuration data.
     * 
     * @param composite
     *            The <code>Composite</code> inside which the spacer cell has to be created
     * @param columnSpan
     *            The number of columns that the spacer has to span
     */
    public static final void createSpacer(final Composite composite, final int columnSpan) {
        final Label label = new Label(composite, SWT.NONE);
        final GridData gd = new GridData();
        gd.horizontalSpan = columnSpan;
        gd.grabExcessHorizontalSpace = true;
        label.setLayoutData(gd);
    }

    /**
     * Safely dispose a <code>Widget</code> instance
     * 
     * @param widget
     *            The widget to be disposed
     */
    public static final void nullSafeDispose(final Widget widget) {
        if (widget != null) {
            widget.dispose();
        }
    }
}