//-----BEGIN DISCLAIMER-----
/*******************************************************************************
* Copyright (c) 2008 JCrypTool Team and Contributors
* 
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/
//-----END DISCLAIMER-----
package org.jcryptool.crypto.keystore.ui.views.nodes.keys;

import org.eclipse.jface.resource.ImageDescriptor;
import org.jcryptool.crypto.keystore.KeyStorePlugin;
import org.jcryptool.crypto.keystore.backend.KeyStoreAlias;

public class CertificateNode extends AbstractKeyNode {
	
	private KeyStoreAlias alias;

	public CertificateNode(KeyStoreAlias publicAlias) {
		super(Messages.getString("Label.PublicKey")); //$NON-NLS-1$
		this.alias = publicAlias;
	}

	public KeyStoreAlias getAlias() {
		return alias;
	}

	/**
	 * @see org.jcryptool.crypto.keystore.ui.views.nodes.TreeNode#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor() {
		return KeyStorePlugin.getImageDescriptor("icons/16x16/kgpg_identity.png"); //$NON-NLS-1$
	}

}
