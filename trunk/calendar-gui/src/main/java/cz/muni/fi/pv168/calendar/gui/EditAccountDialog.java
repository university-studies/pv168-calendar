package cz.muni.fi.pv168.calendar.gui;

import cz.muni.fi.pv168.calendar.common.PasswordHash;
import cz.muni.fi.pv168.calendar.entity.User;
import cz.muni.fi.pv168.calendar.service.UserManager;
import org.apache.juli.logging.Log;

import javax.swing.*;
import java.awt.event.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EditAccountDialog extends JDialog {
    private Logger log = LoggerFactory.getLogger(EditAccountDialog.class);
    private ResourceBundle texts = ResourceBundle.getBundle("Texts");

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textLogin;
    private JPasswordField textOldPassword;
    private JPasswordField textNewPassword;
    private JTextField textEmail;

    private User signedUser;
    private UserManager userManager;
    private JFrame parent;

    public EditAccountDialog(JFrame parent, UserManager userManager) {
        super(parent,true);
        this.parent = parent;

        setContentPane(contentPane);
        //setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle(texts.getString("edit_account_dialog_title"));
        setLocationRelativeTo(parent);
        setResizable(false);
        pack();

        this.userManager = userManager;

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

        final User tmpUser = new User(signedUser.getLogin(), "", signedUser.getEmail());
        tmpUser.setId(signedUser.getId());
        tmpUser.setHashedPassword(signedUser.getPassword());
        try {
            if (textOldPassword.getPassword().length > 0) {
                if (!PasswordHash.validatePassword(textOldPassword.getPassword(), signedUser.getPassword())) {
                    JOptionPane.showMessageDialog(EditAccountDialog.this, texts.getString("edit_account_dialog_error_password_old"), texts.getString("error_dialog_title"),
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (textNewPassword.getPassword().length < 3) {
                    JOptionPane.showMessageDialog(EditAccountDialog.this, texts.getString("edit_account_dialog_error_password_new"), texts.getString("error_dialog_title"),
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                tmpUser.setPassword(String.valueOf(textNewPassword.getPassword()));
            }

            tmpUser.setEmail(textEmail.getText());
            tmpUser.setLogin(textLogin.getText());
            SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {
                private User user = tmpUser;

                @Override
                protected Void doInBackground() throws Exception {
                    userManager.updateUser(user);
                    return null;
                }
            };

            swingWorker.execute();

        } catch (NoSuchAlgorithmException e) {
            log.error("Chyba pri kontrole hesla", e);
        } catch (InvalidKeySpecException e) {
            log.error("Chyba pri kontrole hesla", e);
        }

        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    public void setSignedUser(User signedUser) {
        this.signedUser = signedUser;
        textLogin.setText(signedUser.getLogin());
        textEmail.setText(signedUser.getEmail());
    }
}
