//-----BEGIN DISCLAIMER-----
/*******************************************************************************
* Copyright (c) 2010 JCrypTool Team and Contributors
*
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/
//-----END DISCLAIMER-----
package org.jcryptool.editor.text.action;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.jcryptool.core.logging.utils.LogUtil;
import org.jcryptool.editor.text.JCTTextEditorPlugin;
import org.jcryptool.editor.text.editor.JCTTextEditor;
import org.jcryptool.editor.text.preferences.PreferenceConstants;
import org.jcryptool.editor.text.service.JCTEditorService;

/**
 * Starts the JCT Texteditor on startup when no other editor is opened and there
 * is no view of the <b>org.jcryptool.visual</b> and <b>org.jcryptool.games</b>
 * category.
 *
 * @author mwalthart
 * @author Dominik Schadow
 * @version 0.9.2
 */
public class StartUp implements IStartup {
    /**
     * @see org.eclipse.ui.startup
     */
    public void earlyStartup() {
        PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
            public void run() {
                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                if (!isCryptoViewOpen(page) && page.getEditorReferences().length == 0) {
                    try {
                        page.openEditor(JCTEditorService.createTemporaryFile(), JCTTextEditor.ID);
                        IPreferenceStore store = JCTTextEditorPlugin.getDefault().getPreferenceStore();
                        store.setDefault(PreferenceConstants.FONT_FAMILY, "");
                        store.setDefault(PreferenceConstants.FONT_SIZE, 10);                        
                    } catch (PartInitException e) {
                        LogUtil.logError(JCTTextEditorPlugin.PLUGIN_ID, Messages.NewFileJCTTextEditorAction_0, e, true);
                    }
                }
            }
        });
    }

    /**
     * Checks all opened views for visualizations and games. Returns true if there are any, false otherwise.
     *
     * @param page The active workbench page
     * @return True or false depending on the active views
     */
    private boolean isCryptoViewOpen(final IWorkbenchPage page) {
        for (final IViewReference ref : page.getViewReferences()) {
            if (ref.getId().startsWith("org.jcryptool.visual") || ref.getId().startsWith("org.jcryptool.games")) { //$NON-NLS-1$ //$NON-NLS-2$
                return true;
            }
        }

        return false;
    }
}
