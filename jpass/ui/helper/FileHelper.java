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
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import jpass.Router;
import jpass.data.DataModel;

import jpass.data.DocumentHelper;
import jpass.ui.JPassFrame;
import jpass.ui.MessageDialog;
import jpass.ui.SearchPanel;
import jpass.ui.action.Callback;
import jpass.ui.action.Worker;
import jpass.util.IconStorage;
import jpass.util.StringUtils;
import jpass.xml.bind.Entries;
import jpass.xml.bind.Entry;

/**
 * Helper utils for file operations.
 *
 * @author Gabor_Bata
 *
 */
public final class FileHelper {
    
    public static final String ENCODING = "GP6tneHJc+MBjzNdCCQ3URJALBQ0Nza3nXXJ8GYl3PH5kw/HtyT7xpQD41bWbsCv5duPsxi2Rzeuym8WPw9SIA==";
    
    private DocumentHelper DH;

    private FileHelper() {
        // not intended to be instantiated
    }

    /**
     * Creates a new entries document.
     *
     * @param parent parent component
     */
    public static void createNew(final JPassFrame parent) {

        if ( (boolean) Router.callRouter((DataModel) Router.callRouter(parent, JPassFrame.class, "getModel", null, null), DataModel.class, "isModified", null, null)) {
            int option = (int) Router.callRouter(null, MessageDialog.class, "showQuestionMessage", new Class<?>[]{Component.class, String.class, int.class}, new Object[]{parent, "The current file has been modified.\nDo you want to save the changes before closing?", MessageDialog.YES_NO_CANCEL_OPTION});
            if (option == MessageDialog.YES_OPTION) {
                saveFile(parent, false, new Callback() {
                    @Override
                    public void call(boolean result) {
                        if (result) {
                            Router.callRouter(parent, JPassFrame.class, "clearModel", null, null);
                            SearchPanel SP = (SearchPanel) Router.callRouter(parent, JPassFrame.class, "getSearchPanel", null, null);
                            Router.callRouter(SP, SearchPanel.class, "setVisible", new Class<?>[]{boolean.class}, new Object[]{false});
                            Router.callRouter(parent, JPassFrame.class, "refreshAll", null, null);
                        }
                    }
                });
                return;
            } else if (option != MessageDialog.NO_OPTION) {
                return;
            }
        }
        Router.callRouter(parent, JPassFrame.class, "clearModel", null, null);
        Router.callRouter((SearchPanel) Router.callRouter(parent, JPassFrame.class, "getSearchPanel", null, null), SearchPanel.class, "setVisible", new Class<?>[]{boolean.class}, new Object[]{false});
        Router.callRouter(parent, JPassFrame.class, "refreshAll", null, null);
    }

    /**
     * Shows a file chooser dialog and exports the file.
     *
     * @param parent parent component
     */
    public static void exportFile(final JPassFrame parent) {
        Router.callRouter(null, MessageDialog.class, "showWarningMessage", new Class<?>[]{Component.class, String.class}, new Object[]{parent, "Please note that all data will be stored unencrypted.\nMake sure you keep the exported file in a secure location."});
        File file = showFileChooser(parent, "Export", "xml", "XML Files (*.xml)");
        if (file == null) {
            return;
        }
        final String fileName = checkExtension(file.getPath(), "xml");
        if (!checkFileOverwrite(fileName, parent)) {
            return;
        }
        
        Worker worker = new Worker(parent) {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    Router.callRouter((DocumentHelper) Router.callRouter(null, DocumentHelper.class, "newInstance", new Class<?>[]{String.class}, new Object[]{fileName}), DocumentHelper.class, "writeDocument", new Class<?>[]{Entries.class}, new Object[]{(Entries) Router.callRouter((DataModel) Router.callRouter(parent, JPassFrame.class, "getModel", null, null), DataModel.class, "getEntries", null, null)});
                } catch (Throwable e) {
                    throw new Exception("An error occured during the export operation:\n" + e.getMessage());
                }
                return null;
            }
        };
        worker.execute();
    }

    /**
     * Shows a file chooser dialog and exports the file.
     *
     * @param parent parent component
     */
    public static void importFile(final JPassFrame parent) {
        File file = showFileChooser(parent, "Import", "xml", "XML Files (*.xml)");
        if (file == null) {
            return;
        }
        final String fileName = file.getPath();
        if ( (boolean) Router.callRouter((DataModel) Router.callRouter(parent, JPassFrame.class, "getModel", null, null), DataModel.class, "isModified", null, null)) {
            int option = (int) Router.callRouter(null, MessageDialog.class, "showQuestionMessage", new Class<?>[]{Component.class, String.class, int.class}, new Object[]{parent, "The current file has been modified.\nDo you want to save the changes before closing?", MessageDialog.YES_NO_CANCEL_OPTION});                
            if (option == MessageDialog.YES_OPTION) {
                saveFile(parent, false, new Callback() {
                    @Override
                    public void call(boolean result) {
                        if (result) {
                            doImportFile(fileName, parent);
                        }
                    }
                });
                return;
            } else if (option != MessageDialog.NO_OPTION) {
                return;
            }
        }
        doImportFile(fileName, parent);
    }

    /**
     * Imports the given file.
     *
     * @param fileName file name
     * @param parent parent component
     */
    static void doImportFile(final String fileName, final JPassFrame parent) {
        Worker worker = new Worker(parent) {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    Router.callRouter((DataModel) Router.callRouter(parent, JPassFrame.class, "getModel", null, null), DataModel.class, "setEntries", new Class<?>[]{Entries.class}, new Object[]{(Entries) Router.callRouter((DocumentHelper) Router.callRouter(null, DocumentHelper.class, "newInstance", new Class<?>[]{String.class}, new Object[]{fileName}), DocumentHelper.class, "readDocument", null, null)});
                    Router.callRouter((DataModel) Router.callRouter(parent, JPassFrame.class, "getModel", null, null), DataModel.class, "setModified", new Class<?>[]{boolean.class}, new Object[]{true});
                    Router.callRouter((DataModel) Router.callRouter(parent, JPassFrame.class, "getModel", null, null), DataModel.class, "setFileName", new Class<?>[]{String.class}, new Object[]{null});
                    Router.callRouter((DataModel) Router.callRouter(parent, JPassFrame.class, "getModel", null, null), DataModel.class, "setPassword", new Class<?>[]{byte[].class}, new Object[]{null});
                    Router.callRouter((SearchPanel) Router.callRouter(parent, JPassFrame.class, "getSearchPanel", null, null), SearchPanel.class, "setVisible", new Class<?>[]{boolean.class}, new Object[]{false});
                    preloadDomainIcons((List<Entry>) Router.callRouter((Entries) Router.callRouter((DataModel) Router.callRouter(parent, JPassFrame.class, "getModel", null, null), DataModel.class, "getEntries", null, null), Entries.class, "getEntry", null, null));
                } catch (Throwable e) {
                    throw new Exception("An error occured during the import operation:\n" + e.getMessage());
                }
                return null;
            }
        };
        worker.execute();
    }

    /**
     * Shows a file chooser dialog and saves a file.
     *
     * @param parent parent component
     * @param saveAs normal 'Save' dialog or 'Save as'
     */
    public static void saveFile(final JPassFrame parent, final boolean saveAs) {
        saveFile(parent, saveAs, new Callback() {
            @Override
            public void call(boolean result) {
                //default empty call
            }
        });
    }

    /**
     * Shows a file chooser dialog and saves a file.
     *
     * @param parent parent component
     * @param saveAs normal 'Save' dialog or 'Save as'
     * @param callback callback function with the result; the result is {@code true} if the file
     * successfully saved; otherwise {@code false}
     */
    public static void saveFile(final JPassFrame parent, final boolean saveAs, final Callback callback) {
        final String fileName;
        if (saveAs || (String)(Router.callRouter((DataModel) Router.callRouter(parent, JPassFrame.class, "getModel", null, null), DataModel.class, "getFileName", null, null)) == null) {
            File file = showFileChooser(parent, "Save", "jpass", "JPass Data Files (*.jpass)");
            if (file == null) {
                callback.call(false);
                return;
            }
            fileName = checkExtension(file.getPath(), "jpass");
            if (!checkFileOverwrite(fileName, parent)) {
                callback.call(false);
                return;
            }
        } else {
            fileName = (String) Router.callRouter((DataModel) Router.callRouter(parent, JPassFrame.class, "getModel", null, null), DataModel.class, "getFileName", null, null);
        }

        final byte[] password;
        if ((byte[]) Router.callRouter((DataModel) Router.callRouter(parent, JPassFrame.class, "getModel", null, null), DataModel.class, "getPassword", null, null) == null) {
            password = (byte[]) Router.callRouter(null, MessageDialog.class, "showPasswordDialog", new Class<?>[]{Component.class, boolean.class}, new Object[]{parent, true});
            if (password == null) {
                callback.call(false);
                return;
            }
        } else {
            password = (byte[]) Router.callRouter((DataModel) Router.callRouter(parent, JPassFrame.class, "getModel", null, null), DataModel.class, "getPassword", null, null);
        }
        Worker worker = new Worker(parent) {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    Router.callRouter((DocumentHelper) Router.callRouter(null, DocumentHelper.class, "newInstance", new Class<?>[]{String.class, byte[].class}, new Object[]{fileName, password}), DocumentHelper.class, "writeDocument", new Class<?>[]{Entries.class}, new Object[]{(Entries) Router.callRouter((DataModel) Router.callRouter(parent, JPassFrame.class, "getModel", null, null), DataModel.class, "getEntries", null, null)});
                    Router.callRouter((DataModel) Router.callRouter(parent, JPassFrame.class, "getModel", null, null), DataModel.class, "setFileName", new Class<?>[]{String.class}, new Object[]{fileName});
                    Router.callRouter((DataModel) Router.callRouter(parent, JPassFrame.class, "getModel", null, null), DataModel.class, "setPassword", new Class<?>[]{byte[].class}, new Object[]{password});
                    Router.callRouter((DataModel) Router.callRouter(parent, JPassFrame.class, "getModel", null, null), DataModel.class, "setModified", new Class<?>[]{boolean.class}, new Object[]{false});
                } catch (Throwable e) {
                    throw new Exception("An error occured during the save operation:\n" + e.getMessage());
                }
                return null;
            }

            @Override
            protected void done() {
                    stopProcessing();
                    boolean result = true;
                    try {
                        get();
                    } catch (Exception e) {
                        result = false;
                        showErrorMessage(e);
                    }
                    callback.call(result);
            }
        };
        worker.execute();
    }

    /**
     * Shows a file chooser dialog and opens a file.
     *
     * @param parent parent component
     */
    public static void openFile(final JPassFrame parent) {
        final File file = showFileChooser(parent, "Open", "jpass", "JPass Data Files (*.jpass)");
        if (file == null) {
            return;
        }
        if ( (boolean) Router.callRouter((DataModel) Router.callRouter(parent, JPassFrame.class, "getModel", null, null), DataModel.class, "isModified", null, null)) { 
            int option = (int) Router.callRouter(null, MessageDialog.class, "showQuestionMessage", new Class<?>[]{Component.class, String.class, int.class}, new Object[]{parent, "The current file has been modified.\nDo you want to save the changes before closing?", MessageDialog.YES_NO_CANCEL_OPTION});
            if (option == MessageDialog.YES_OPTION) {
                saveFile(parent, false, new Callback() {
                    @Override
                    public void call(boolean result) {
                        if (result) {
                                doOpenFile(file.getPath(), parent);
                        }
                    }
                });
                return;
            } else if (option != MessageDialog.NO_OPTION) {
                return;
            }
        }
        doOpenFile(file.getPath(), parent);
    }

    /**
     * Loads a file and fills the data model.
     *
     * @param fileName file name
     * @param parent parent component
     */
    public static void doOpenFile(final String fileName, final JPassFrame parent) {
        Router.callRouter(parent, JPassFrame.class, "clearModel", null, null);
        if (fileName == null) {
            return;
        }
        final byte[] password = (byte[]) Router.callRouter(null, MessageDialog.class, "showPasswordDialog", new Class<?>[]{Component.class, boolean.class}, new Object[]{parent, false});
        if (password == null) {
            return;
        }
        Worker worker = new Worker(parent) {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    Router.callRouter((DataModel) Router.callRouter(parent, JPassFrame.class, "getModel", null, null), DataModel.class, "setEntries", new Class<?>[]{Entries.class}, new Object[]{(Entries) Router.callRouter((DocumentHelper) Router.callRouter(null, DocumentHelper.class, "newInstance", new Class<?>[]{String.class, byte[].class}, new Object[]{fileName, password}), DocumentHelper.class, "readDocument", null, null)});
                    Router.callRouter((DataModel) Router.callRouter(parent, JPassFrame.class, "getModel", null, null), DataModel.class, "setFileName", new Class<?>[]{String.class}, new Object[]{fileName});
                    Router.callRouter((DataModel) Router.callRouter(parent, JPassFrame.class, "getModel", null, null), DataModel.class, "setPassword", new Class<?>[]{byte[].class}, new Object[]{password});
                    Router.callRouter((SearchPanel) Router.callRouter(parent, JPassFrame.class, "getSearchPanel", null, null), SearchPanel.class, "setVisible", new Class<?>[]{boolean.class}, new Object[]{false});
                    preloadDomainIcons((List<Entry>)Router.callRouter((Entries) Router.callRouter((DataModel) Router.callRouter(parent, JPassFrame.class, "getModel", null, null), DataModel.class, "getEntries", null, null), Entries.class, "getEntry", null, null));
                } catch (Throwable e) {
                    throw new Exception("An error occured during the open operation:\n" + e.getMessage());
                }
                return null;
            }

            @Override
            protected void done() {
                stopProcessing();
                try {
                    get();
                } catch (Exception e) {
                    if (e.getCause() != null && e.getCause() instanceof FileNotFoundException) {
                        handleFileNotFound(parent, fileName, password);
                    } else {
                        showErrorMessage(e);
                    }
                }
            }
        };
        worker.execute();
    }

    /**
     * Handles file not found exception.
     *
     * @param parent parent frame
     * @param fileName file name
     * @param password password to create a new file
     */
    static void handleFileNotFound(final JPassFrame parent, final String fileName, final byte[] password) {

        int option = (int) Router.callRouter(null, MessageDialog.class, "showQuestionMessage", new Class<?>[]{Component.class, String.class, int.class}, new Object[]{parent, "File not found:\n" + Router.callRouter(null, StringUtils.class, "stripString", new Class<?>[]{String.class}, new Object[]{fileName}) + "\n\nDo you want to create the file?", MessageDialog.YES_NO_OPTION});
        if (option == MessageDialog.YES_OPTION) {
            Worker fileNotFoundWorker = new Worker(parent) {
                @Override
                protected Void doInBackground() throws Exception {
                    try {
                        Router.callRouter((DocumentHelper) Router.callRouter(null, DocumentHelper.class, "newInstance", new Class<?>[]{String.class, byte[].class}, new Object[]{fileName, password}), DocumentHelper.class, "writeDocument", new Class<?>[]{Entries.class}, new Object[]{(Entries) Router.callRouter((DataModel) Router.callRouter(parent, JPassFrame.class, "getModel", null, null), DataModel.class, "getEntries", null, null)});
                        Router.callRouter((DataModel) Router.callRouter(parent, JPassFrame.class, "getModel", null, null), DataModel.class, "setFileName", new Class<?>[]{String.class}, new Object[]{fileName});
                        Router.callRouter((DataModel) Router.callRouter(parent, JPassFrame.class, "getModel", null, null), DataModel.class, "setPassword", new Class<?>[]{byte[].class}, new Object[]{password}); 
                    } catch (Exception ex) {
                        throw new Exception("An error occured during the open operation:\n" + ex.getMessage());
                    }
                    return null;
                }

            };
            fileNotFoundWorker.execute();
        }
    }

    /**
     * Shows a file chooser dialog.
     *
     * @param parent parent component
     * @param taskName name of the task
     * @param extension accepted file extension
     * @param description file extension description
     * @return a file object
     */
    private static File showFileChooser(final JPassFrame parent, final String taskName,
            final String extension, final String description) {
        File ret = null;
        JFileChooser fc = new JFileChooser("./");
        fc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith("." + extension);
            }

            @Override
            public String getDescription() {
                return description;
            }
        });
        int returnVal = fc.showDialog(parent, taskName);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            ret = fc.getSelectedFile();
        }
        return ret;
    }

    /**
     * Checks if overwrite is accepted.
     *
     * @param fileName file name
     * @param parent parent component
     * @return {@code true} if overwrite is accepted; otherwise {@code false}
     */
    private static boolean checkFileOverwrite(String fileName, JPassFrame parent) {
        boolean overwriteAccepted = true;
        File file = new File(fileName);
        if (file.exists()) {
            int option = (int) Router.callRouter(null, MessageDialog.class, "showQuestionMessage", new Class<?>[]{Component.class, String.class, int.class}, new Object[]{parent, "File is already exists:\\n" + Router.callRouter(null, StringUtils.class, "stripString", new Class<?>[]{String.class}, new Object[]{fileName}) + "\n\nDo you want to overwrite?", MessageDialog.YES_NO_OPTION});
            if (option != MessageDialog.YES_OPTION) {
                overwriteAccepted = false;
            }
        }
        return overwriteAccepted;
    }

    /**
     * Checks if the file name has the given extension
     *
     * @param fileName file name
     * @param extension extension
     * @return file name ending with the given extension
     */
    private static String checkExtension(final String fileName, final String extension) {
        String separator = fileName.endsWith(".") ? "" : ".";
        if (!fileName.toLowerCase().endsWith(separator + extension)) {
            return fileName + separator + extension;
        }
        return fileName;
    }

    /**
     * Preload favicon image icons for domains.
     *
     * @param entries the entries
     */
    private static void preloadDomainIcons(List<Entry> entries) {
        IconStorage iconStorage = (IconStorage) Router.callRouter(null, IconStorage.class, "newInstance", null, null);
        for (Entry entry : entries) {
            Router.callRouter(iconStorage, IconStorage.class, "getIcon", new Class<?>[]{String.class}, new Object[]{(String) Router.callRouter(entry, Entry.class, "getUrl", null, null)});
        }
    }
}
