// -----BEGIN DISCLAIMER-----
/*******************************************************************************
 * Copyright (c) 2008 JCrypTool Team and Contributors
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
// -----END DISCLAIMER-----
package org.jcryptool.crypto.keystore.ui.views.nodes;

import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jcryptool.core.logging.utils.LogUtil;
import org.jcryptool.crypto.keys.KeyType;
import org.jcryptool.crypto.keystore.KeyStorePlugin;
import org.jcryptool.crypto.keystore.backend.KeyStoreAlias;
import org.jcryptool.crypto.keystore.backend.KeyStoreManager;
import org.jcryptool.crypto.keystore.descriptors.interfaces.IContactDescriptor;
import org.jcryptool.crypto.keystore.ui.views.interfaces.IKeyStoreListener;

public class ContactManager {
    /** Singleton instance */
    private static ContactManager instance;

    /** All contacts and their respective meta information */
    private Map<String, IContactDescriptor> contacts = Collections
            .synchronizedMap(new HashMap<String, IContactDescriptor>());

    private static List<IKeyStoreListener> listeners = new ArrayList<IKeyStoreListener>();

    private ITreeNode invisibleRoot;

    private ContactManager() {
    }

    public synchronized static ContactManager getInstance() {
        if (instance == null) {
            instance = new ContactManager();
            instance.getTreeModel(); // init
        }
        return instance;
    }

    public void addKeyStoreListener(IKeyStoreListener listener) {
        listeners.add(listener);
    }

    public void removeKeyStoreListener(IKeyStoreListener listener) {
        listeners.remove(listener);
    }

    public Iterator<IKeyStoreListener> getKeyStoreListeners() {
        return listeners.iterator();
    }

    public synchronized ITreeNode getTreeModel() { // singleton

        if (invisibleRoot == null) {
            init();
            invisibleRoot = new TreeNode("INVISIBLE_ROOT"); //$NON-NLS-1$
            for (IContactDescriptor desc : contacts.values()) {
                if (desc instanceof ContactDescriptorNode) {
                    LogUtil.logInfo("adding: " + desc.getName()); //$NON-NLS-1$
                    invisibleRoot.addChild((ContactDescriptorNode) desc);
                }
            }
            LogUtil.logInfo("children.length: " + invisibleRoot.getChildrenArray().length); //$NON-NLS-1$
        }
        return invisibleRoot;
    }

    private void init() {
        contacts.clear();
        Enumeration<String> en = null;
        try {
            KeyStoreManager manager = KeyStoreManager.getInstance();
            en = manager.getAliases();
        } catch (KeyStoreException e) {
            LogUtil.logError(KeyStorePlugin.PLUGIN_ID, "KeyStoreException while accessing the aliases", e, true);
        }
        while (en != null && en.hasMoreElements()) {
            LogUtil.logInfo("Adding an Entry"); //$NON-NLS-1$
            String tmp = en.nextElement();
            LogUtil.logInfo("TMP: " + tmp); //$NON-NLS-1$
            addEntry(new KeyStoreAlias(tmp));
        }
    }

    private void addEntry(KeyStoreAlias alias) {
        if (contacts.containsKey(alias.getContactName())) {
            // contact is already known
            addEntryToContact(contacts.get(alias.getContactName()), alias);
        } else {
            // contact is not known
            IContactDescriptor contact = new ContactDescriptorNode(alias.getContactName());
            addEntryToContact(contact, alias);
            contacts.put(alias.getContactName(), contact);
        }
    }

    private void addEntryToContact(IContactDescriptor contact, KeyStoreAlias alias) {
        LogUtil.logInfo("ALIAS: " + alias.toString()); //$NON-NLS-1$
        if (alias.getKeyStoreEntryType().equals(KeyType.SECRETKEY)) {
            contact.addSecretKey(alias);
        } else if (alias.getKeyStoreEntryType().equals(KeyType.PUBLICKEY)) {
            contact.addCertificate(alias);
        } else if (alias.getKeyStoreEntryType().equals(KeyType.KEYPAIR_PRIVATE_KEY)) {
            contact.addKeyPair(alias, null);
        } else if (alias.getKeyStoreEntryType().equals(KeyType.KEYPAIR_PUBLIC_KEY)) {
            contact.addKeyPair(null, alias);
        }
    }

    public void removeContact(String contactName) {
        invisibleRoot.removeChild((ContactDescriptorNode) contacts.get(contactName));
        contacts.remove(contactName);
        Iterator<IKeyStoreListener> it = getKeyStoreListeners();
        while (it.hasNext()) {
            it.next().fireKeyStoreModified(invisibleRoot);
        }
    }

    public void removeEntry(KeyStoreAlias alias) {
        if (alias.getKeyStoreEntryType().equals(KeyType.SECRETKEY)) {
            LogUtil.logInfo("removing a secret key"); //$NON-NLS-1$
            contacts.get(alias.getContactName()).removeSecretKey(alias);
        } else if (alias.getKeyStoreEntryType().equals(KeyType.PUBLICKEY)) {
            LogUtil.logInfo("removing a certificate"); //$NON-NLS-1$
            contacts.get(alias.getContactName()).removeCertificate(alias);
        } else if (alias.getKeyStoreEntryType().equals(KeyType.KEYPAIR_PRIVATE_KEY)) {
            LogUtil.logInfo("removing a key pair"); //$NON-NLS-1$
            contacts.get(alias.getContactName()).removeKeyPair(alias);
        } else if (alias.getKeyStoreEntryType().equals(KeyType.KEYPAIR_PUBLIC_KEY)) {
            LogUtil.logInfo("removing a key pair"); //$NON-NLS-1$
            contacts.get(alias.getContactName()).removeKeyPair(alias);
        }
    }

    public void addCertificate(KeyStoreAlias alias) {
        if (contacts.containsKey(alias.getContactName())) {
            contacts.get(alias.getContactName()).addCertificate(alias);
        } else {
            IContactDescriptor contact = new ContactDescriptorNode(alias.getContactName());
            contact.addCertificate(alias);
            contacts.put(alias.getContactName(), contact);
            invisibleRoot.addChild((ContactDescriptorNode) contact);
            Iterator<IKeyStoreListener> it = getKeyStoreListeners();
            while (it.hasNext()) {
                it.next().fireKeyStoreModified(invisibleRoot);
            }
        }
    }

    public void addKeyPair(KeyStoreAlias privateKey, KeyStoreAlias publicKey) {
        if (contacts.containsKey(privateKey.getContactName())) {
            contacts.get(privateKey.getContactName()).addKeyPair(privateKey, publicKey);
        } else {
            IContactDescriptor contact = new ContactDescriptorNode(privateKey.getContactName());
            contact.addKeyPair(privateKey, publicKey);
            contacts.put(privateKey.getContactName(), contact);
            invisibleRoot.addChild((ContactDescriptorNode) contact);
            Iterator<IKeyStoreListener> it = getKeyStoreListeners();
            while (it.hasNext()) {
                it.next().fireKeyStoreModified(invisibleRoot);
            }
        }
    }

    public void addSecretKey(KeyStoreAlias alias) {
        if (contacts.containsKey(alias.getContactName())) {
            contacts.get(alias.getContactName()).addSecretKey(alias);
        } else {
            IContactDescriptor contact = new ContactDescriptorNode(alias.getContactName());
            contact.addSecretKey(alias);
            contacts.put(alias.getContactName(), contact);
            invisibleRoot.addChild((ContactDescriptorNode) contact);
            Iterator<IKeyStoreListener> it = getKeyStoreListeners();
            while (it.hasNext()) {
                it.next().fireKeyStoreModified(invisibleRoot);
            }
        }
    }

    public Iterator<IContactDescriptor> getContacts() {
        LogUtil.logInfo("returning contacts"); //$NON-NLS-1$
        return contacts.values().iterator();
    }

    public int getContactSize() {
        return contacts.size();
    }

}
