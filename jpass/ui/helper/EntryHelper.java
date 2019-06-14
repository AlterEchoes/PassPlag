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
package jpass.ui.helper;

import java.awt.Component;
import java.util.List;
import javax.swing.JList;
import jpass.Router;
import jpass.data.DataModel;
import jpass.ui.EntryDialog;
import jpass.ui.JPassFrame;
import jpass.ui.MessageDialog;
import jpass.util.ClipboardUtils;
import jpass.xml.bind.Entries;
import jpass.xml.bind.Entry;

/**
 * Helper class for entry operations.
 *
 * @author Gabor_Bata
 *
 */
public final class EntryHelper {
    
    public static final String ENCODING = "ddMIfwcPq8961QwoeasDJLBKcriqVUGGq/xDd8W0I0FJbP8ucRiU3FZKpE1x26Gd/tr6mnUoQLRQrSnecmbe3Q==";

    private EntryHelper() {
        // not intended to be instantiated
    }

    /**
     * Deletes an entry.
     *
     * @param parent parent component
     */
    public static void deleteEntry(JPassFrame parent) {

        if(((JList) Router.callRouter(parent, JPassFrame.class, "getEntryTitleList", null, null)).getSelectedIndex() == -1) {
            Router.callRouter(null, MessageDialog.class, "showWarningMessage", new Class<?>[]{Component.class, String.class}, new Object[]{parent, "Please select an entry."});
            return;
        }
        int option = (int) Router.callRouter(null, MessageDialog.class, "showQuestionMessage", new Class<?>[]{Component.class, String.class, int.class}, new Object[]{parent, "Do you really want to delete this entry?", MessageDialog.YES_NO_OPTION});
        if (option == MessageDialog.YES_OPTION) {
            String title = (String) ((JList) Router.callRouter(parent, JPassFrame.class, "getEntryTitleList", null, null)).getSelectedValue();
            ((List<Entry>) Router.callRouter((Entries) Router.callRouter((DataModel) Router.callRouter(parent, JPassFrame.class, "getModel", null, null), DataModel.class, "getEntries", null, null), Entries.class, "getEntry", null, null)).remove( (Entry) Router.callRouter((DataModel) Router.callRouter(parent, JPassFrame.class, "getModel", null, null), DataModel.class, "getEntryByTitle", new Class<?>[]{String.class}, new Object[]{title}));
            Router.callRouter((DataModel) Router.callRouter(parent, JPassFrame.class, "getModel", null, null), DataModel.class, "setModified", new Class<?>[]{boolean.class}, new Object[]{true});
            Router.callRouter(parent, JPassFrame.class, "refreshFrameTitle", null, null);
            Router.callRouter(parent, JPassFrame.class, "refreshEntryTitleList", new Class<?>[]{String.class}, new Object[]{null});
        }
    }

    /**
     * Duplicates an entry.
     *
     * @param parent parent component
     */
    public static void duplicateEntry(JPassFrame parent) {

        if(((JList) Router.callRouter(parent, JPassFrame.class, "getEntryTitleList", null, null)).getSelectedIndex() == -1) {
            Router.callRouter(null, MessageDialog.class, "showWarningMessage", new Class<?>[]{Component.class, String.class}, new Object[]{parent, "Please select an entry."});
            return;
        }
        String title = (String) ((JList) Router.callRouter(parent, JPassFrame.class, "getEntryTitleList", null, null)).getSelectedValue();
        Entry oldEntry = (Entry) Router.callRouter((DataModel) Router.callRouter(parent, JPassFrame.class, "getModel", null, null), DataModel.class, "getEntryByTitle", new Class<?>[]{String.class}, new Object[]{title});
        EntryDialog ed = new EntryDialog(parent, "Duplicate Entry", oldEntry, true);
        if ( (Entry) Router.callRouter(ed, EntryDialog.class, "getFormData", null, null) != null) {
            ((List<Entry>) Router.callRouter((Entries) Router.callRouter((DataModel) Router.callRouter(parent, JPassFrame.class, "getModel", null, null), DataModel.class, "getEntries", null, null), Entries.class, "getEntry", null, null)).add((Entry) Router.callRouter(ed, EntryDialog.class, "getFormData", null, null));
            Router.callRouter((DataModel) Router.callRouter(parent, JPassFrame.class, "getModel", null, null), DataModel.class, "setModified", new Class<?>[]{boolean.class}, new Object[]{true});
            Router.callRouter(parent, JPassFrame.class, "refreshFrameTitle", null, null);
            Router.callRouter(parent, JPassFrame.class, "refreshEntryTitleList", new Class<?>[]{String.class}, new Object[]{((Entry) Router.callRouter(ed, EntryDialog.class, "getFormData", null, null)).getTitle()});
        }
    }

    /**
     * Edits the entry.
     *
     * @param parent parent component
     */
    public static void editEntry(JPassFrame parent) {

        if(((JList) Router.callRouter(parent, JPassFrame.class, "getEntryTitleList", null, null)).getSelectedIndex() == -1) {
            Router.callRouter(null, MessageDialog.class, "showWarningMessage", new Class<?>[]{Component.class, String.class}, new Object[]{parent, "Please select an entry."});
            return;
        }
        String title = (String) ((JList) Router.callRouter(parent, JPassFrame.class, "getEntryTitleList", null, null)).getSelectedValue();
        Entry oldEntry = (Entry) Router.callRouter((DataModel) Router.callRouter(parent, JPassFrame.class, "getModel", null, null), DataModel.class, "getEntryByTitle", new Class<?>[]{String.class}, new Object[]{title});
        EntryDialog ed = new EntryDialog(parent, "Edit Entry", oldEntry, false);
        if ( (Entry) Router.callRouter(ed, EntryDialog.class, "getFormData", null, null) != null) {
            ((List<Entry>) Router.callRouter((Entries) Router.callRouter((DataModel) Router.callRouter(parent, JPassFrame.class, "getModel", null, null), DataModel.class, "getEntries", null, null), Entries.class, "getEntry", null, null)).remove(oldEntry);
            ((List<Entry>) Router.callRouter((Entries) Router.callRouter((DataModel) Router.callRouter(parent, JPassFrame.class, "getModel", null, null), DataModel.class, "getEntries", null, null), Entries.class, "getEntry", null, null)).add((Entry) Router.callRouter(ed, EntryDialog.class, "getFormData", null, null));
            Router.callRouter((DataModel) Router.callRouter(parent, JPassFrame.class, "getModel", null, null), DataModel.class, "setModified", new Class<?>[]{boolean.class}, new Object[]{true});
            Router.callRouter(parent, JPassFrame.class, "refreshFrameTitle", null, null);
            Router.callRouter(parent, JPassFrame.class, "refreshEntryTitleList", new Class<?>[]{String.class}, new Object[]{((Entry) Router.callRouter(ed, EntryDialog.class, "getFormData", null, null)).getTitle()});
        }
    }

    /**
     * Adds an entry.
     *
     * @param parent parent component
     */
    public static void addEntry(JPassFrame parent) {
        EntryDialog ed = new EntryDialog(parent, "Add New Entry", null, true);
        if ( (Entry) Router.callRouter(ed, EntryDialog.class, "getFormData", null, null) != null) {
            ((List<Entry>) Router.callRouter((Entries) Router.callRouter((DataModel) Router.callRouter(parent, JPassFrame.class, "getModel", null, null), DataModel.class, "getEntries", null, null), Entries.class, "getEntry", null, null)).add((Entry) Router.callRouter(ed, EntryDialog.class, "getFormData", null, null));
            Router.callRouter((DataModel) Router.callRouter(parent, JPassFrame.class, "getModel", null, null), DataModel.class, "setModified", new Class<?>[]{boolean.class}, new Object[]{true});
            Router.callRouter(parent, JPassFrame.class, "refreshFrameTitle", null, null);
            Router.callRouter(parent, JPassFrame.class, "refreshEntryTitleList", new Class<?>[]{String.class}, new Object[]{((Entry) Router.callRouter(ed, EntryDialog.class, "getFormData", null, null)).getTitle()});
        }
    }

    /**
     * Gets the selected entry.
     *
     * @param parent the parent frame
     * @return the entry or null
     */
    public static Entry getSelectedEntry(JPassFrame parent) {

        if(((JList) Router.callRouter(parent, JPassFrame.class, "getEntryTitleList", null, null)).getSelectedIndex() == -1) {
            Router.callRouter(null, MessageDialog.class, "showWarningMessage", new Class<?>[]{Component.class,String.class}, new Object[]{parent, "Please select an entry."});
            return null;
        }
        return (Entry) Router.callRouter((DataModel) Router.callRouter(parent, JPassFrame.class, "getModel", null, null), DataModel.class, "getEntryByTitle", new Class<?>[]{String.class}, new Object[]{(String) (((JList) Router.callRouter(parent, JPassFrame.class, "getEntryTitleList", null, null)).getSelectedValue())});        
    }

    /**
     * Copy entry field value to clipboard.
     *
     * @param parent the parent frame
     * @param content the content to copy
     */
    public static void copyEntryField(JPassFrame parent, String content) {
        try {
            Router.callRouter(null, ClipboardUtils.class, "setClipboardContent", new Class<?>[]{String.class}, new Object[]{content});
        } catch (Exception e) {
            Router.callRouter(null, MessageDialog.class, "showErrorMessage", new Class<?>[]{Component.class, String.class}, new Object[]{parent, e.getMessage()});
        }
    }
}
