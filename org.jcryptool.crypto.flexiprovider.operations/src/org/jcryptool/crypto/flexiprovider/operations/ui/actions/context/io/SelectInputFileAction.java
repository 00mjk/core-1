// -----BEGIN DISCLAIMER-----
/*******************************************************************************
 * Copyright (c) 2008 JCrypTool Team and Contributors
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
// -----END DISCLAIMER-----
package org.jcryptool.crypto.flexiprovider.operations.ui.actions.context.io;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.jcryptool.core.logging.utils.LogUtil;
import org.jcryptool.core.util.directories.DirectoryService;
import org.jcryptool.crypto.flexiprovider.descriptors.IFlexiProviderOperation;
import org.jcryptool.crypto.flexiprovider.operations.ui.listeners.ISelectedOperationListener;

public class SelectInputFileAction extends Action {
    private IFlexiProviderOperation entryNode;
    private ISelectedOperationListener listener;

    public SelectInputFileAction(ISelectedOperationListener listener) {
        this.setText(Messages.SelectInputFileAction_0);
        this.setToolTipText(Messages.SelectInputFileAction_1);
        this.listener = listener;
    }

    public void run() {
        this.entryNode = listener.getFlexiProviderOperation();
        LogUtil.logInfo("select input file... (" + entryNode.getTimestamp() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
        dialog.setFilterPath(DirectoryService.getUserHomeDir());

        String filename = dialog.open();

        if (filename != null) {
            entryNode.setInput(filename);
        }
    }

}
