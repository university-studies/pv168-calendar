package cz.muni.fi.pv168.calendar.gui;

import cz.muni.fi.pv168.calendar.common.PasswordHash;
import cz.muni.fi.pv168.calendar.entity.User;
import cz.muni.fi.pv168.calendar.service.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ResourceBundle;

public class LoginDialog extends JDialog {
    private final static Logger log = LoggerFactory.getLogger(LoginDialog.class);
    private static ResourceBundle texts = ResourceBundle.getBundle("Texts");
    private final String TITLE = "Sign in";

    private  JFrame parent;

    public JPanel contentPane;
    private JButton buttonSignIn;
    private JButton buttonCreateAccount;
    private JTextField textLogin;
    private JPasswordField passwordText;

    private UserManager userManager;
    private boolean loggedSuccessfully = false;
    private User userLogged = null;

    public LoginDialog(final JFrame parent, final UserManager userManager) {
        super(parent, true);
        this.parent = parent;
        this.userManager = userManager;

        setTitle(TITLE);
        getContentPane().add(contentPane);
        // ENTER submit button
        getRootPane().setDefaultButton(buttonSignIn);
        pack();
        setLocationRelativeTo(parent);
        setResizable(false);

        buttonSignIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                String login = textLogin.getText();
                User user = userManager.getUserByLogin(login);
                try {
                    if (user == null || !PasswordHash.validatePassword(passwordText.getPassword(), user.getPassword())) {
                        JOptionPane.showMessageDialog(LoginDialog.this, texts.getString("login_dialog_error"),
                                texts.getString("error_dialog_title"), JOptionPane.ERROR_MESSAGE);
                    }
                    else {
                        setLoggedSuccessfully(true);
                        userLogged = user;
                        //closing dialog
                        dispose();
                    }
                } catch (NoSuchAlgorithmException e1) {
                    e1.printStackTrace();
                } catch (InvalidKeySpecException e1) {
                    e1.printStackTrace();
                }
            }
        });

        buttonCreateAccount.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                CreateAccountDialog createAccountDialog = new CreateAccountDialog(parent, userManager);
                createAccountDialog.setVisible(true);
            }
        });
    }

    public boolean isLoggedSuccessfully() {
        return loggedSuccessfully;
    }

    public void setLoggedSuccessfully(boolean loggedSuccessfully) {
        this.loggedSuccessfully = loggedSuccessfully;
    }

    public User getUserLogged() {
        return userLogged;
    }

    public void setUserLogged(User userLogged) {
        this.userLogged = userLogged;
    }
}
