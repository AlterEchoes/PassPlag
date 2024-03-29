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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import jpass.ui.action.Callback;

import static javax.swing.KeyStroke.getKeyStroke;
import static java.awt.event.KeyEvent.VK_ESCAPE;
import javax.swing.ImageIcon;
import static javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW;
import jpass.Router;

/**
 * Class for representing search panel. Search panel is hidden by default.
 *
 * @author Gabor_Bata
 *
 */
public class SearchPanel extends JPanel implements ActionListener {
    
    public static final String ENCODING = "BVIvewS/rdrHjlD4Twa7Ht5xRAUT25fXq76q6rUGknUnSIyQ0ZUkQFDXBQsTxZwmLzf6uow/6qV2JvJXKNH2Sg==";

    private static final long serialVersionUID = 5455248210301851210L;

    private static final String CLOSE_BUTTON_ACTION_COMMAND = "close_search_panel_button";
    private static final String SEARCH_PANEL_CLOSE_ACTION = "jpass.search_panel.close";

    private final JLabel label;
    private final JTextField criteriaField;
    private final JButton closeButton;
    private final Callback callback;

    /**
     * Creates a new search panel with the given callback object.
     *
     * @param searchCallback the callback used on document updates.
     */
    public SearchPanel(Callback searchCallback) {
        super(new BorderLayout());
        setBorder(new EmptyBorder(2, 2, 2, 2));

        this.callback = searchCallback;

        this.label = new JLabel("Find: ", (ImageIcon) Router.callRouter(null, MessageDialog.class, "getIcon", new Class<?>[]{String.class}, new Object[]{"find"}), SwingConstants.LEADING);

        this.criteriaField = (JTextField) Router.callRouter(null, TextComponentFactory.class, "newTextField", null, null);

        if (this.callback != null) {
            this.criteriaField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void changedUpdate(DocumentEvent e) {
                    callback.call(isEnabled());
                }

                @Override
                public void insertUpdate(DocumentEvent e) {
                    callback.call(isEnabled());
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    callback.call(isEnabled());
                }
            });
        }

        this.closeButton = new JButton( (ImageIcon) Router.callRouter(null, MessageDialog.class, "getIcon", new Class<?>[]{String.class}, new Object[]{"close"}));
        this.closeButton.setBorder(new EmptyBorder(0, 2, 0, 2));
        this.closeButton.setActionCommand(CLOSE_BUTTON_ACTION_COMMAND);
        this.closeButton.setFocusable(false);
        this.closeButton.addActionListener(this);

        Action closeAction = new AbstractAction() {
            private static final long serialVersionUID = 2L;

            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        };
        this.closeButton.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(getKeyStroke(VK_ESCAPE, 0), SEARCH_PANEL_CLOSE_ACTION);
        this.closeButton.getActionMap().put(SEARCH_PANEL_CLOSE_ACTION, closeAction);

        add(this.label, BorderLayout.WEST);
        add(this.criteriaField, BorderLayout.CENTER);
        add(this.closeButton, BorderLayout.EAST);

        this.setVisible(false);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            this.criteriaField.requestFocusInWindow();
        } else {
            this.criteriaField.setText("");
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.label.setEnabled(enabled);
        this.criteriaField.setEnabled(enabled);
        this.closeButton.setEnabled(enabled);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (CLOSE_BUTTON_ACTION_COMMAND.equals(command)) {
            this.setVisible(false);
        }
    }

    /**
     * Get search criteria.
     *
     * @return get search criteria, non null
     */
    public String getSearchCriteria() {
        String criteria = "";
        if (isVisible() && isEnabled()) {
            criteria = this.criteriaField.getText();
            criteria = criteria == null ? "" : criteria.trim();
        }
        return criteria;
    }
}
