package cz.muni.fi.pv168.calendar;

import javax.swing.*;
import java.awt.event.*;

public class AddEventDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField txtTitle;
    private JTextField txtDescription;
    private JTextField txtLocation;
    private JSpinner startDay;
    private JComboBox startMonth;
    private JSpinner startYear;
    private JComboBox endMonth;
    private JSpinner endYear;
    private JSpinner endDay;
    private JButton addReminderButton;
    private JSpinner spinner1;
    private JComboBox comboBox1;
    private JSpinner spinner2;

    public AddEventDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
// add your code here
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    /*
    public static void main(String[] args) {
        AddEventDialog dialog = new AddEventDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
    */
}
