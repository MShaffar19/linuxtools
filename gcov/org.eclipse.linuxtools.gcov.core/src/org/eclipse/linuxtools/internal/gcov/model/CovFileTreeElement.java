/*******************************************************************************
 * Copyright (c) 2009, 2018 STMicroelectronics and others.
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Xavier Raynaud <xavier.raynaud@st.com> - initial API and implementation
 *******************************************************************************/
package org.eclipse.linuxtools.internal.gcov.model;


public class CovFileTreeElement extends AbstractTreeElement {

    private static final long serialVersionUID = -5017234616535899796L;

    public CovFileTreeElement(TreeElement parent,
            String name, int totalLines, int executedLines, int instrumentedLines) {
        super(parent, name, totalLines, executedLines, instrumentedLines);
    }
}
