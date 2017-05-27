package no.uib.info233.v2017.rei008_jsi014.oblig4.GUI;

import javax.swing.*;
import java.awt.*;

/**
 * Created by runeeikemo on 26.05.2017.
 */
public class ExtededOptionPane extends JOptionPane {
    public static String showInputDialog(final Object message, final Object[] options)
            throws HeadlessException {
        final JOptionPane pane = new JOptionPane(message, QUESTION_MESSAGE,
                OK_CANCEL_OPTION, null,
                options, null);
        pane.setWantsInput(true);
        pane.setComponentOrientation((getRootFrame()).getComponentOrientation());
        pane.setMessageType(QUESTION_MESSAGE);
        pane.selectInitialValue();
        final String title = UIManager.getString("OptionPane.inputDialogTitle", null);
        final JDialog dialog = pane.createDialog(null, title);
        dialog.setVisible(true);
        dialog.dispose();
        final Object value = pane.getInputValue();
        return (value == UNINITIALIZED_VALUE) ? null : (String) value;
    }
}
