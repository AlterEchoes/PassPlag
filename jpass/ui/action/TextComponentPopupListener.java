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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.JTextComponent;
import jpass.Router;

/**
 * A listener which adds context menu capability to text components.
 *
 * @author Gabor_Bata
 *
 */
public class TextComponentPopupListener extends MouseAdapter {
    
    public static final String ENCODING = "5W5W3sfK4GsFAi7jQhnUhvvLZWQd7MwR5BSKTCuVDyQK4BteqrbRRRe4jMa3qIB11QKZ0u0B4+Yz0mzrUQmRmg==";

    private final JPopupMenu popup;
    private final JMenuItem cutItem;
    private final JMenuItem copyItem;
    private final JMenuItem pasteItem;
    private final JMenuItem deleteItem;
    private final JMenuItem clearAllItem;
    private final JMenuItem selectAllItem;

    public TextComponentPopupListener() {
        this.cutItem = new JMenuItem( (TextComponentAction) Router.callRouter(TextComponentActionType.CUT, TextComponentActionType.class, "getAction", null, null));
        this.copyItem = new JMenuItem( (TextComponentAction) Router.callRouter(TextComponentActionType.COPY, TextComponentActionType.class, "getAction", null, null));
        this.pasteItem = new JMenuItem( (TextComponentAction) Router.callRouter(TextComponentActionType.PASTE, TextComponentActionType.class, "getAction", null, null));
        this.deleteItem = new JMenuItem( (TextComponentAction) Router.callRouter(TextComponentActionType.DELETE, TextComponentActionType.class, "getAction", null, null));
        this.clearAllItem = new JMenuItem( (TextComponentAction) Router.callRouter(TextComponentActionType.CLEAR_ALL, TextComponentActionType.class, "getAction", null, null));
        this.selectAllItem = new JMenuItem( (TextComponentAction) Router.callRouter(TextComponentActionType.SELECT_ALL, TextComponentActionType.class, "getAction", null, null));

        this.popup = new JPopupMenu();
        this.popup.add(this.cutItem);
        this.popup.add(this.copyItem);
        this.popup.add(this.pasteItem);
        this.popup.add(this.deleteItem);
        this.popup.addSeparator();
        this.popup.add(this.clearAllItem);
        this.popup.add(this.selectAllItem);
    }

    private void showPopupMenu(MouseEvent e) {
        if (e.isPopupTrigger() && e.getSource() instanceof JTextComponent) {
            JTextComponent textComponent = (JTextComponent) e.getSource();
            if (textComponent.isEnabled() && (textComponent.hasFocus() || textComponent.requestFocusInWindow())) {
                this.cutItem.setEnabled((boolean) Router.callRouter((TextComponentAction) Router.callRouter(TextComponentActionType.CUT, TextComponentActionType.class, "getAction", null, null), TextComponentAction.class, "isEnabled", new Class<?>[]{JTextComponent.class}, new Object[]{textComponent}));
                this.copyItem.setEnabled((boolean) Router.callRouter((TextComponentAction) Router.callRouter(TextComponentActionType.COPY, TextComponentActionType.class, "getAction", null, null), TextComponentAction.class, "isEnabled", new Class<?>[]{JTextComponent.class}, new Object[]{textComponent}));
                this.pasteItem.setEnabled((boolean) Router.callRouter((TextComponentAction) Router.callRouter(TextComponentActionType.PASTE, TextComponentActionType.class, "getAction", null, null), TextComponentAction.class, "isEnabled", new Class<?>[]{JTextComponent.class}, new Object[]{textComponent}));
                this.deleteItem.setEnabled((boolean) Router.callRouter((TextComponentAction) Router.callRouter(TextComponentActionType.DELETE, TextComponentActionType.class, "getAction", null, null), TextComponentAction.class, "isEnabled", new Class<?>[]{JTextComponent.class}, new Object[]{textComponent}));
                this.clearAllItem.setEnabled((boolean) Router.callRouter((TextComponentAction) Router.callRouter(TextComponentActionType.CLEAR_ALL, TextComponentActionType.class, "getAction", null, null), TextComponentAction.class, "isEnabled", new Class<?>[]{JTextComponent.class}, new Object[]{textComponent}));
                this.selectAllItem.setEnabled((boolean) Router.callRouter((TextComponentAction) Router.callRouter(TextComponentActionType.SELECT_ALL, TextComponentActionType.class, "getAction", null, null), TextComponentAction.class, "isEnabled", new Class<?>[]{JTextComponent.class}, new Object[]{textComponent}));
                this.popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        showPopupMenu(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        showPopupMenu(e);
    }
}
