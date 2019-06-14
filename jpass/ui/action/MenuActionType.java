/*
 * JPass
 *
 * Copyright (c) 2009-2019 Gabor Bata
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package jpass.ui.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import jpass.ui.GeneratePasswordDialog;
import jpass.ui.JPassFrame;
import jpass.ui.MessageDialog;
import jpass.ui.helper.EntryHelper;
import jpass.ui.helper.FileHelper;
import jpass.xml.bind.Entry;

import static javax.swing.KeyStroke.getKeyStroke;
import static java.awt.event.InputEvent.CTRL_MASK;
import static java.awt.event.InputEvent.ALT_MASK;
import javax.swing.ImageIcon;
import jpass.Router;
import jpass.data.DataModel;
import jpass.ui.SearchPanel;

/**
 * Enumeration which holds menu actions and related data.
 *
 * @author Gabor_Bata
 *
 */
public enum MenuActionType {
    
    NEW_FILE(new AbstractMenuAction("New", (ImageIcon) Router.callRouter(null, MessageDialog.class, "getIcon", new Class<?>[]{String.class}, new Object[]{"new"}), getKeyStroke(KeyEvent.VK_N, CTRL_MASK)) {
        private static final long serialVersionUID = -8823457568905830188L;

        @Override
        public void actionPerformed(ActionEvent ev) {
            Router.callRouter(null, FileHelper.class, "createNew", new Class<?>[]{JPassFrame.class}, new Object[]{(JPassFrame) Router.callRouter(null, JPassFrame.class, "getInstance", null, null)});
        }
    }),
    OPEN_FILE(new AbstractMenuAction("Open File...", (ImageIcon) Router.callRouter(null, MessageDialog.class, "getIcon", new Class<?>[]{String.class}, new Object[]{"open"}), getKeyStroke(KeyEvent.VK_O, CTRL_MASK)) {
        private static final long serialVersionUID = -441032579227887886L;

        @Override
        public void actionPerformed(ActionEvent ev) {
            Router.callRouter(null, FileHelper.class, "openFile", new Class<?>[]{JPassFrame.class}, new Object[]{(JPassFrame) Router.callRouter(null, JPassFrame.class, "getInstance", null, null)});
        }
    }),
    SAVE_FILE(new AbstractMenuAction("Save", (ImageIcon) Router.callRouter(null, MessageDialog.class, "getIcon", new Class<?>[]{String.class}, new Object[]{"save"}), getKeyStroke(KeyEvent.VK_S, CTRL_MASK)) {    
        private static final long serialVersionUID = 8657273941022043906L;

        @Override
        public void actionPerformed(ActionEvent ev) {
            Router.callRouter(null, FileHelper.class, "saveFile", new Class<?>[]{JPassFrame.class, boolean.class}, new Object[]{(JPassFrame) Router.callRouter(null, JPassFrame.class, "getInstance", null, null), false});
        }
    }),
    SAVE_AS_FILE(new AbstractMenuAction("Save As...", (ImageIcon) Router.callRouter(null, MessageDialog.class, "getIcon", new Class<?>[]{String.class}, new Object[]{"save_as"}), null) {
        private static final long serialVersionUID = 1768189708479045321L;

        @Override
        public void actionPerformed(ActionEvent ev) {
            Router.callRouter(null, FileHelper.class, "saveFile", new Class<?>[]{JPassFrame.class, boolean.class}, new Object[]{(JPassFrame) Router.callRouter(null, JPassFrame.class, "getInstance", null, null), true});
        }
    }),
    EXPORT_XML(new AbstractMenuAction("Export to XML...", (ImageIcon) Router.callRouter(null, MessageDialog.class, "getIcon", new Class<?>[]{String.class}, new Object[]{"export"}), null) {    
        private static final long serialVersionUID = 7673408373934859054L;

        @Override
        public void actionPerformed(ActionEvent ev) {
            Router.callRouter(null, FileHelper.class, "exportFile", new Class<?>[]{JPassFrame.class}, new Object[]{(JPassFrame) Router.callRouter(null, JPassFrame.class, "getInstance", null, null)});
        }
    }),
    IMPORT_XML(new AbstractMenuAction("Import from XML...", (ImageIcon) Router.callRouter(null, MessageDialog.class, "getIcon", new Class<?>[]{String.class}, new Object[]{"import"}), null) {
        private static final long serialVersionUID = -1331441499101116570L;

        @Override
        public void actionPerformed(ActionEvent ev) {
            Router.callRouter(null, FileHelper.class, "importFile", new Class<?>[]{JPassFrame.class}, new Object[]{(JPassFrame) Router.callRouter(null, JPassFrame.class, "getInstance", null, null)});
        }
    }),
    CHANGE_PASSWORD(new AbstractMenuAction("Change Password...", (ImageIcon) Router.callRouter(null, MessageDialog.class, "getIcon", new Class<?>[]{String.class}, new Object[]{"lock"}), null) {
        private static final long serialVersionUID = 616220526614500130L;

        @Override
        public void actionPerformed(ActionEvent ev) {
                JPassFrame parent = (JPassFrame) Router.callRouter(null, JPassFrame.class, "getInstance", null, null);
                byte[] password = (byte[]) Router.callRouter(null, MessageDialog.class, "showPasswordDialog", new Class<?>[]{Component.class, boolean.class}, new Object[]{parent, true});
                if (password == null) {
                    Router.callRouter(null, MessageDialog.class, "showInformationMessage", new Class<?>[]{Component.class, String.class}, new Object[]{parent, "Password has not been modified."});

                } else {
                    Router.callRouter((DataModel) Router.callRouter(parent, JPassFrame.class, "getModel", null, null), DataModel.class, "setPassword", new Class<?>[]{byte[].class}, new Object[]{password});
                    Router.callRouter((DataModel) Router.callRouter(parent, JPassFrame.class, "getModel", null, null), DataModel.class, "setModified", new Class<?>[]{boolean.class}, new Object[]{true});
                    Router.callRouter(parent, JPassFrame.class, "refreshFrameTitle", null, null);
                    Router.callRouter(null, MessageDialog.class, "showInformationMessage", new Class<?>[]{Component.class, String.class}, new Object[]{parent, "Password has been successfully modified.\n\nSave the file now in order to\nget the new password applied."});
                }
        }
    }),
    GENERATE_PASSWORD(new AbstractMenuAction("Generate Password...", (ImageIcon) Router.callRouter(null, MessageDialog.class, "getIcon", new Class<?>[]{String.class}, new Object[]{"generate"}), getKeyStroke(KeyEvent.VK_Z, CTRL_MASK)) {
    
        private static final long serialVersionUID = 2865402858056954304L;

        @Override
        public void actionPerformed(ActionEvent ev) {
            new GeneratePasswordDialog( (JPassFrame) Router.callRouter(null, JPassFrame.class, "getInstance", null, null));
        }
    }),
    EXIT(new AbstractMenuAction("Exit", (ImageIcon) Router.callRouter(null, MessageDialog.class, "getIcon", new Class<?>[]{String.class}, new Object[]{"exit"}), getKeyStroke(KeyEvent.VK_F4, ALT_MASK)) {
        private static final long serialVersionUID = -2741659403416846295L;

        @Override
        public void actionPerformed(ActionEvent ev) {
            Router.callRouter((JPassFrame) Router.callRouter(null, JPassFrame.class, "getInstance", null, null), JPassFrame.class, "exitFrame", null, null);
        }
    }),
    ABOUT(new AbstractMenuAction("About JPass...", (ImageIcon) Router.callRouter(null, MessageDialog.class, "getIcon", new Class<?>[]{String.class}, new Object[]{"info"}), getKeyStroke(KeyEvent.VK_F1, 0)) {
        private static final long serialVersionUID = -8935177434578353178L;

        @Override
        public void actionPerformed(ActionEvent ev) {
            StringBuilder sb = new StringBuilder();
            sb.append("<b>" + JPassFrame.PROGRAM_NAME + "</b>\n");
            sb.append("version: " + JPassFrame.PROGRAM_VERSION + "\n");
            sb.append("Copyright &copy; 2009-2019 G\u00e1bor Bata\n");
            sb.append("\n");
            sb.append("Java version: ").append(System.getProperties().getProperty("java.version")).append("\n");
            sb.append(System.getProperties().getProperty("java.vendor"));
            Router.callRouter(null, MessageDialog.class, "showInformationMessage", new Class<?>[]{Component.class, String.class}, new Object[]{(JPassFrame) Router.callRouter(null, JPassFrame.class, "getInstance", null, null), sb.toString()});
        }
    }),
    LICENSE(new AbstractMenuAction("License", (ImageIcon) Router.callRouter(null, MessageDialog.class, "getIcon", new Class<?>[]{String.class}, new Object[]{"license"}), null) {    
        private static final long serialVersionUID = 2476765521818491911L;

        @Override
        public void actionPerformed(ActionEvent ev) {
            Router.callRouter(null, MessageDialog.class, "showTextFile", new Class<?>[]{Component.class, String.class, String.class}, new Object[]{(JPassFrame) Router.callRouter(null, JPassFrame.class, "getInstance", null, null), "License", "license.txt"});
        }
    }),
    ADD_ENTRY(new AbstractMenuAction("Add Entry...", (ImageIcon) Router.callRouter(null, MessageDialog.class, "getIcon", new Class<?>[]{String.class}, new Object[]{"entry_new"}), getKeyStroke(KeyEvent.VK_Y, CTRL_MASK)) {    
        private static final long serialVersionUID = 6793989246928698613L;

        @Override
        public void actionPerformed(ActionEvent ev) {
            Router.callRouter(null, EntryHelper.class, "addEntry", new Class<?>[]{JPassFrame.class}, new Object[]{(JPassFrame) Router.callRouter(null, JPassFrame.class, "getInstance", null, null)});
        }
    }),
    EDIT_ENTRY(new AbstractMenuAction("Edit Entry...", (ImageIcon) Router.callRouter(null, MessageDialog.class, "getIcon", new Class<?>[]{String.class}, new Object[]{"entry_edit"}), getKeyStroke(KeyEvent.VK_E, CTRL_MASK)) {
        private static final long serialVersionUID = -3234220812811327191L;

        @Override
        public void actionPerformed(ActionEvent ev) {
            Router.callRouter(null, EntryHelper.class, "editEntry", new Class<?>[]{JPassFrame.class}, new Object[]{(JPassFrame) Router.callRouter(null, JPassFrame.class, "getInstance", null, null)});
        }
    }),
    DUPLICATE_ENTRY(new AbstractMenuAction("Duplicate Entry...", (ImageIcon) Router.callRouter(null, MessageDialog.class, "getIcon", new Class<?>[]{String.class}, new Object[]{"entry_duplicate"}), getKeyStroke(KeyEvent.VK_K, CTRL_MASK)) {
        private static final long serialVersionUID = 6728896867346523861L;

        @Override
        public void actionPerformed(ActionEvent ev) {
            Router.callRouter(null, EntryHelper.class, "duplicateEntry", new Class<?>[]{JPassFrame.class}, new Object[]{(JPassFrame) Router.callRouter(null, JPassFrame.class, "getInstance", null, null)});
        }
    }),
    DELETE_ENTRY(new AbstractMenuAction("Delete Entry...", (ImageIcon) Router.callRouter(null, MessageDialog.class, "getIcon", new Class<?>[]{String.class}, new Object[]{"entry_delete"}), getKeyStroke(KeyEvent.VK_D, CTRL_MASK)) {
        private static final long serialVersionUID = -1306116722130641659L;

        @Override
        public void actionPerformed(ActionEvent ev) {
            Router.callRouter(null, EntryHelper.class, "deleteEntry", new Class<?>[]{JPassFrame.class}, new Object[]{(JPassFrame) Router.callRouter(null, JPassFrame.class, "getInstance", null, null)});
        }
    }),
    COPY_URL(new AbstractMenuAction("Copy URL", (ImageIcon) Router.callRouter(null, MessageDialog.class, "getIcon", new Class<?>[]{String.class}, new Object[]{"url"}), getKeyStroke(KeyEvent.VK_U, CTRL_MASK)) {
        private static final long serialVersionUID = 3321559756310744862L;

        @Override
        public void actionPerformed(ActionEvent ev) {
                JPassFrame parent = (JPassFrame) Router.callRouter(null, JPassFrame.class, "getInstance", null, null);
                Entry entry = (Entry) Router.callRouter(null, EntryHelper.class, "getSelectedEntry", new Class<?>[]{JPassFrame.class}, new Object[]{parent});
                if (entry != null) {
                    Router.callRouter(null, EntryHelper.class, "copyEntryField", new Class<?>[]{JPassFrame.class, String.class}, new Object[]{parent, (String) Router.callRouter(entry, Entry.class, "getUrl", null, null)});
                }
        }
    }),
    COPY_USER(new AbstractMenuAction("Copy User Name", (ImageIcon) Router.callRouter(null, MessageDialog.class, "getIcon", new Class<?>[]{String.class}, new Object[]{"user"}), getKeyStroke(KeyEvent.VK_B, CTRL_MASK)) {
        private static final long serialVersionUID = -1126080607846730912L;

        @Override
        public void actionPerformed(ActionEvent ev) {
                JPassFrame parent = (JPassFrame) Router.callRouter(null, JPassFrame.class, "getInstance", null, null);
                Entry entry = (Entry) Router.callRouter(null, EntryHelper.class, "getSelectedEntry", new Class<?>[]{JPassFrame.class}, new Object[]{parent});
                if (entry != null) {
                    Router.callRouter(null, EntryHelper.class, "copyEntryField", new Class<?>[]{JPassFrame.class, String.class}, new Object[]{parent, (String) Router.callRouter(entry, Entry.class, "getUser", null, null)});
                }
        }
    }),
    COPY_PASSWORD(new AbstractMenuAction("Copy Password", (ImageIcon) Router.callRouter(null, MessageDialog.class, "getIcon", new Class<?>[]{String.class}, new Object[]{"keyring"}), getKeyStroke(KeyEvent.VK_C, CTRL_MASK)) {
        private static final long serialVersionUID = 2719136744084762599L;

        @Override
        public void actionPerformed(ActionEvent ev) {
                JPassFrame parent = (JPassFrame) Router.callRouter(null, JPassFrame.class, "getInstance", null, null);
                Entry entry = (Entry) Router.callRouter(null, EntryHelper.class, "getSelectedEntry", new Class<?>[]{JPassFrame.class}, new Object[]{parent});
                if (entry != null) {
                    Router.callRouter(null, EntryHelper.class, "copyEntryField", new Class<?>[]{JPassFrame.class, String.class}, new Object[]{parent, (String) Router.callRouter(entry, Entry.class, "getPassword", null, null)});
                }
        }
    }),
    CLEAR_CLIPBOARD(new AbstractMenuAction("Clear Clipboard", (ImageIcon) Router.callRouter(null, MessageDialog.class, "getIcon", new Class<?>[]{String.class}, new Object[]{"clear"}), getKeyStroke(KeyEvent.VK_X, CTRL_MASK)) {
        private static final long serialVersionUID = -7621614933053924326L;

        @Override
        public void actionPerformed(ActionEvent ev) {
            Router.callRouter(null, EntryHelper.class, "copyEntryField", new Class<?>[]{JPassFrame.class, String.class}, new Object[]{(JPassFrame) Router.callRouter(null, JPassFrame.class, "getInstance", null, null), null});
        }
    }),
    FIND_ENTRY(new AbstractMenuAction("Find Entry", (ImageIcon) Router.callRouter(null, MessageDialog.class, "getIcon", new Class<?>[]{String.class}, new Object[]{"find"}), getKeyStroke(KeyEvent.VK_F, CTRL_MASK)) {
        private static final long serialVersionUID = -7621614933053924326L;

        @Override
        public void actionPerformed(ActionEvent ev) {
            Router.callRouter((SearchPanel) Router.callRouter((JPassFrame) Router.callRouter(null, JPassFrame.class, "getInstance", null, null), JPassFrame.class, "getSearchPanel", null, null), SearchPanel.class, "setVisible", new Class<?>[]{boolean.class}, new Object[]{true});
        }
    });
    
    public static final String ENCODING = "IJEkgdFa81UT72xTDljGNW2KGfPtapLloWtiJNisY8ZjZcCX9qgoTa7872rzLDS9au8jngJVsAxy5GCgylJzfw==";
    
    private final String name;
    private final AbstractMenuAction action;
    private JPassFrame JP;
    private GeneratePasswordDialog PD;
    private Entry E;
    private EntryHelper EH;
    private FileHelper FH;

    private MenuActionType(AbstractMenuAction action) {
        this.name = String.format("jpass.menu.%s_action", this.name().toLowerCase());
        this.action = action;
    }

    public String getName() {
        return this.name;
    }

    public AbstractMenuAction getAction() {
        return this.action;
    }

    public KeyStroke getAccelerator() {
        return (KeyStroke) this.action.getValue(Action.ACCELERATOR_KEY);
    }

    public static final void bindAllActions(JComponent component) {
        ActionMap actionMap = component.getActionMap();
        InputMap inputMap = component.getInputMap();
        for (MenuActionType type : values()) {
            actionMap.put(type.getName(), type.getAction());
            KeyStroke acc = type.getAccelerator();
            if (acc != null) {
                inputMap.put(type.getAccelerator(), type.getName());
            }
        }
    }
}
