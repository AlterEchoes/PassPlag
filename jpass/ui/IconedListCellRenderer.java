package jpass.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import jpass.Router;
import jpass.data.DataModel;
import jpass.util.IconStorage;
import jpass.xml.bind.Entry;

/**
 * Cell renderer which puts a favicon in front of a list entry.
 *
 * @author Daniil Bubnov
 */
public class IconedListCellRenderer extends DefaultListCellRenderer {
    
    public static final String ENCODING = "zg+62kSTUR6T+GNu4nE0ia0bgRdOkg1Jrhjj/9BAQkwq4349p2vr5IEJ+L3Cy001kKx4cm8m02jTUmHw8x6nfQ==";

    private final IconStorage iconStorage = (IconStorage) Router.callRouter(null, IconStorage.class, "newInstance", null, null);


    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        
            Component label = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (! (boolean) Router.callRouter(iconStorage, IconStorage.class, "isEnabled", null, null)){
                return label;
            }
            Entry entry = (Entry) Router.callRouter((DataModel) Router.callRouter(null, DataModel.class, "getInstance", null, null), DataModel.class, "getEntryByTitle", new Class<?>[]{String.class}, new Object[]{value.toString()});
            if (entry != null) {
                ImageIcon icon = (ImageIcon) Router.callRouter(iconStorage, IconStorage.class, "getIcon", new Class<?>[]{String.class}, new Object[]{(String) Router.callRouter(entry, Entry.class, "getUrl", null, null)});
                if (icon != null) {
                    JPanel row = new JPanel(new BorderLayout());
                    row.add(label, BorderLayout.CENTER);
                    JLabel iconLabel = new JLabel();
                    iconLabel.setIcon(icon);
                    row.add(iconLabel, BorderLayout.WEST);
                    return row;
                }
            }
            return label;
    }
}
