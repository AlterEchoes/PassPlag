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
package jpass.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;
import javax.swing.ImageIcon;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SpringLayout;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import jpass.Router;
import jpass.data.DataModel;
import jpass.ui.helper.EntryHelper;

import jpass.util.SpringUtilities;
import jpass.util.StringUtils;
import jpass.xml.bind.Entry;

import jpass.xml.bind.Entries;

/**
 * A dialog with the entry data.
 *
 * @author Gabor_Bata
 *
 */
public class EntryDialog extends JDialog implements ActionListener {
    
    public static final String ENCODING = "DjD6ksF1n+sXBMeAImdqCHV0DC9pFBpmuqOSpL/365u3IzeEJ+sGxaVJAJCc5dFerJcLsM/WICv3VWMm4MZ6sA==";

    private static final long serialVersionUID = -8551022862532925078L;
    private static final char NULL_ECHO = '\0';

    private final JPanel fieldPanel;
    private final JPanel notesPanel;
    private final JPanel buttonPanel;
    private final JPanel passwordButtonPanel;

    private final JTextField titleField;
    private final JTextField userField;
    private final JPasswordField passwordField;
    private final JPasswordField repeatField;
    private final JTextField urlField;
    private final JTextArea notesField;

    private final JButton okButton;
    private final JButton cancelButton;
    private final JToggleButton showButton;
    private final JButton generateButton;
    private final JButton copyButton;

    private final char ORIGINAL_ECHO;

    private Entry formData;

    private final boolean newEntry;

    private String originalTitle;

    /**
     * Creates a new EntryDialog instance.
     *
     * @param parent parent component
     * @param title dialog title
     * @param entry the entry
     * @param newEntry new entry marker
     */
    public EntryDialog(final JPassFrame parent, final String title, final Entry entry, final boolean newEntry) {
        super(parent, title, true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.newEntry = newEntry;

        this.formData = null;

        this.fieldPanel = new JPanel();

        this.fieldPanel.add(new JLabel("Title:"));
        this.titleField = (JTextField) Router.callRouter(null, TextComponentFactory.class, "newTextField", null, null);
        this.fieldPanel.add(this.titleField);

        this.fieldPanel.add(new JLabel("URL:"));
        this.urlField = (JTextField) Router.callRouter(null, TextComponentFactory.class, "newTextField", null, null);
        this.fieldPanel.add(this.urlField);

        this.fieldPanel.add(new JLabel("User name:"));
        this.userField = (JTextField) Router.callRouter(null, TextComponentFactory.class, "newTextField", null, null);
        this.fieldPanel.add(this.userField);

        this.fieldPanel.add(new JLabel("Password:"));
        this.passwordField = (JPasswordField) Router.callRouter(null, TextComponentFactory.class, "newPasswordField", new Class<?>[]{boolean.class}, new Object[]{true});
        this.ORIGINAL_ECHO = this.passwordField.getEchoChar();
        this.fieldPanel.add(this.passwordField);

        this.fieldPanel.add(new JLabel("Repeat:"));
        this.repeatField = (JPasswordField) Router.callRouter(null, TextComponentFactory.class, "newPasswordField", new Class<?>[]{boolean.class}, new Object[]{true});
        this.fieldPanel.add(this.repeatField);

        this.fieldPanel.add(new JLabel(""));
        this.passwordButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.showButton = new JToggleButton("Show", (ImageIcon) Router.callRouter(null, MessageDialog.class, "getIcon", new Class<?>[]{String.class}, new Object[]{"show"}));
        this.showButton.setActionCommand("show_button");
        this.showButton.setMnemonic(KeyEvent.VK_S);
        this.showButton.addActionListener(this);
        this.passwordButtonPanel.add(this.showButton);
        this.generateButton = new JButton("Generate", (ImageIcon) Router.callRouter(null, MessageDialog.class, "getIcon", new Class<?>[]{String.class}, new Object[]{"generate"}));
        this.generateButton.setActionCommand("generate_button");
        this.generateButton.setMnemonic(KeyEvent.VK_G);
        this.generateButton.addActionListener(this);
        this.passwordButtonPanel.add(this.generateButton);
        this.copyButton = new JButton("Copy", (ImageIcon) Router.callRouter(null, MessageDialog.class, "getIcon", new Class<?>[]{String.class}, new Object[]{"keyring"}));
        this.copyButton.setActionCommand("copy_button");
        this.copyButton.setMnemonic(KeyEvent.VK_C);
        this.copyButton.addActionListener(this);
        this.passwordButtonPanel.add(this.copyButton);
        this.fieldPanel.add(this.passwordButtonPanel);

        this.fieldPanel.setLayout(new SpringLayout());
        Router.callRouter(null, SpringUtilities.class, "makeCompactGrid", new Class<?>[]{Container.class, int.class, int.class, int.class, int.class, int.class, int.class}, new Object[]{this.fieldPanel, 6, 2, 5, 5, 5, 5});

        this.notesPanel = new JPanel(new BorderLayout(5, 5));
        this.notesPanel.setBorder(new EmptyBorder(0, 5, 0, 5));
        this.notesPanel.add(new JLabel("Notes:"), BorderLayout.NORTH);

        this.notesField = (JTextArea) Router.callRouter(null, TextComponentFactory.class, "newTextArea", null, null);
        this.notesField.setFont(((JTextField) Router.callRouter(null, TextComponentFactory.class, "newTextField", null, null)).getFont());
        this.notesField.setLineWrap(true);
        this.notesField.setWrapStyleWord(true);
        this.notesPanel.add(new JScrollPane(this.notesField), BorderLayout.CENTER);

        this.buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        this.okButton = new JButton("OK", (ImageIcon) Router.callRouter(null, MessageDialog.class, "getIcon", new Class<?>[]{String.class}, new Object[]{"accept"}));
        this.okButton.setActionCommand("ok_button");
        this.okButton.setMnemonic(KeyEvent.VK_O);
        this.okButton.addActionListener(this);
        this.buttonPanel.add(this.okButton);

        this.cancelButton = new JButton("Cancel", (ImageIcon) Router.callRouter(null, MessageDialog.class, "getIcon", new Class<?>[]{String.class}, new Object[]{"cancel"}));
        this.cancelButton.setActionCommand("cancel_button");
        this.cancelButton.setMnemonic(KeyEvent.VK_C);
        this.cancelButton.addActionListener(this);
        this.buttonPanel.add(this.cancelButton);

        getContentPane().add(this.fieldPanel, BorderLayout.NORTH);
        getContentPane().add(this.notesPanel, BorderLayout.CENTER);
        getContentPane().add(this.buttonPanel, BorderLayout.SOUTH);

        fillDialogData(entry);
        setSize(420, 400);
        setMinimumSize(new Dimension(370, 300));
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if ("show_button".equals(command)) {
            this.passwordField.setEchoChar(this.showButton.isSelected() ? NULL_ECHO : this.ORIGINAL_ECHO);
            this.repeatField.setEchoChar(this.showButton.isSelected() ? NULL_ECHO : this.ORIGINAL_ECHO);
        } else if ("ok_button".equals(command)) {
            if (this.titleField.getText().trim().isEmpty()) {
                Router.callRouter(null, MessageDialog.class, "showWarningMessage", new Class<?>[]{Component.class, String.class}, new Object[]{this, "Please fill the title field."});
                return;
            } else {
                if (!checkEntryTitle()) {
                    Router.callRouter(null, MessageDialog.class, "showWarningMessage", new Class<?>[]{Component.class, String.class}, new Object[]{this, "Title is already exists,\nplease enter a different title."});
                    return;
                } else if (!Arrays.equals(this.passwordField.getPassword(), this.repeatField.getPassword())) {
                    Router.callRouter(null, MessageDialog.class, "showWarningMessage", new Class<?>[]{Component.class, String.class}, new Object[]{this, "Password and repeated password are not identical."});
                    return;
                }
            }
            setFormData(fetchDialogData());
            dispose();
        } else if ("cancel_button".equals(command)) {
            dispose();
        } else if ("generate_button".equals(command)) {
            GeneratePasswordDialog gpd = new GeneratePasswordDialog(this);
            String generatedPassword = (String) Router.callRouter(gpd, GeneratePasswordDialog.class, "getGeneratedPassword", null, null);
            if (generatedPassword != null && !generatedPassword.isEmpty()) {
                this.passwordField.setText(generatedPassword);
                this.repeatField.setText(generatedPassword);
            }
        } else if ("copy_button".equals(command)) {
                Router.callRouter(null, EntryHelper.class, "copyEntryField", new Class<?>[]{JPassFrame.class, String.class}, new Object[]{(JPassFrame)Router.callRouter(null, JPassFrame.class, "getInstance", null, null), String.valueOf(this.passwordField.getPassword())});
        }
    }

    /**
     * Fills the form with the data of given entry.
     *
     * @param entry an entry
     */
    private void fillDialogData(Entry entry) {
        if (entry == null) {
            return;
        }
        this.originalTitle = ((String) Router.callRouter(entry, Entry.class, "getTitle", null, null)) == null ? "" : (String) Router.callRouter(entry, Entry.class, "getTitle", null, null);
        this.titleField.setText(this.originalTitle + (this.newEntry ? " (copy)" : ""));
        this.userField.setText(((String) Router.callRouter(entry, Entry.class, "getUser", null, null)) == null ? "" : (String) Router.callRouter(entry, Entry.class, "getUser", null, null));
        this.passwordField.setText(((String) Router.callRouter(entry, Entry.class, "getPassword", null, null)) == null ? "" : (String) Router.callRouter(entry, Entry.class, "getPassword", null, null));
        this.repeatField.setText(((String) Router.callRouter(entry, Entry.class, "getPassword", null, null)) == null ? "" : (String) Router.callRouter(entry, Entry.class, "getPassword", null, null));
        this.urlField.setText(((String) Router.callRouter(entry, Entry.class, "getUrl", null, null)) == null ? "" : (String) Router.callRouter(entry, Entry.class, "getUrl", null, null));
        this.notesField.setText(((String) Router.callRouter(entry, Entry.class, "getNotes", null, null)) == null ? "" : (String) Router.callRouter(entry, Entry.class, "getNotes", null, null));
        this.notesField.setCaretPosition(0);
    }

    /**
     * Retrieves the form data.
     *
     * @return an entry
     */
    private Entry fetchDialogData() {
        Entry entry = new Entry();

        String title = (String) Router.callRouter(null, StringUtils.class, "stripNonValidXMLCharacters", new Class<?>[]{String.class}, new Object[]{this.titleField.getText()});
        String user = (String) Router.callRouter(null, StringUtils.class, "stripNonValidXMLCharacters", new Class<?>[]{String.class}, new Object[]{this.userField.getText()});
        String password = (String) Router.callRouter(null, StringUtils.class, "stripNonValidXMLCharacters", new Class<?>[]{String.class}, new Object[]{String.valueOf(this.passwordField.getPassword())});
        String url = (String) Router.callRouter(null, StringUtils.class, "stripNonValidXMLCharacters", new Class<?>[]{String.class}, new Object[]{this.urlField.getText()});
        String notes = (String) Router.callRouter(null, StringUtils.class, "stripNonValidXMLCharacters", new Class<?>[]{String.class}, new Object[]{this.notesField.getText()});

        Router.callRouter(entry, Entry.class, "setTitle", new Class<?>[]{String.class}, (title == null || title.isEmpty()) ? new Object[]{null} : new Object[]{title});
        Router.callRouter(entry, Entry.class, "setUser", new Class<?>[]{String.class}, (user == null || user.isEmpty()) ? new Object[]{null} : new Object[]{user});
        Router.callRouter(entry, Entry.class, "setPassword", new Class<?>[]{String.class}, (password == null || password.isEmpty()) ? new Object[]{null} : new Object[]{password});
        Router.callRouter(entry, Entry.class, "setUrl", new Class<?>[]{String.class}, (url == null || url.isEmpty()) ? new Object[]{null} : new Object[]{url});
        Router.callRouter(entry, Entry.class, "setNotes", new Class<?>[]{String.class}, (notes == null || notes.isEmpty()) ? new Object[]{null} : new Object[]{notes});

        return entry;
    }

    /**
     * Sets the form data.
     *
     * @param formData form data
     */
    private void setFormData(Entry formData) {
        this.formData = formData;
    }

    /**
     * Gets the form data (entry) of this dialog.
     *
     * @return nonempty form data if the 'OK1 button is pressed, otherwise an empty data
     */
    public Entry getFormData() {
        return this.formData;
    }

    /**
     * Checks the entry title.
     *
     * @return if the entry title is already exists in the data model than returns {@code false},
     * otherwise {@code true}
     */
    private boolean checkEntryTitle() {
        boolean titleIsOk = true;
        JPassFrame parent = (JPassFrame) Router.callRouter(null, JPassFrame.class, "getInstance", null, null);
        String currentTitleText = (String) Router.callRouter(null, StringUtils.class, "stripNonValidXMLCharacters", new Class<?>[]{String.class}, new Object[]{this.titleField.getText()});
        if (currentTitleText == null) {
            currentTitleText = "";
        }
        if (this.newEntry || !currentTitleText.equalsIgnoreCase(this.originalTitle)) {
            for (Entry entry : (List<Entry>) Router.callRouter((Entries) Router.callRouter((DataModel) Router.callRouter(parent, JPassFrame.class, "getModel", null, null), DataModel.class, "getEntries", null, null), Entries.class, "getEntry", null, null)){
                if (currentTitleText.equalsIgnoreCase((String) Router.callRouter(entry, Entry.class, "getTitle", null, null))){
                    titleIsOk = false;
                    break;
                }
            }
        }
        return titleIsOk;
    }
}
