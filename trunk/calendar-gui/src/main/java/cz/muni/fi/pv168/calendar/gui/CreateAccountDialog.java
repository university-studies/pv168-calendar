package cz.muni.fi.pv168.calendar.gui;

import cz.muni.fi.pv168.calendar.entity.User;
import cz.muni.fi.pv168.calendar.service.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

public class CreateAccountDialog extends JDialog {
    private final static Logger log = LoggerFactory.getLogger(CreateAccountDialog.class);
    public static final String PASSWORD_TEXT = "password";
    private static ResourceBundle texts = ResourceBundle.getBundle("Texts");
    private static final String TITLE = "Create account";

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textEmail;
    private JTextField textLogin;
    private JPasswordField textPassword;

    private UserManager userManager;
    private JFrame parent;

    private User editedUser;

    public CreateAccountDialog(JFrame parent, final UserManager userManager) {
        super(parent, true);
        this.parent = parent;
        this.userManager = userManager;

        setTitle(TITLE);
        getContentPane().add(contentPane);
        // ENTER submit button
        getRootPane().setDefaultButton(buttonOK);
        pack();
        setLocationRelativeTo(parent);
        setResizable(false);



        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                dispose();
            }
        });

        buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(editedUser == null) {
                    final String login = textLogin.getText();
                    String email = textEmail.getText();
                    String password = new String(textPassword.getPassword());

                    log.debug("login: {}", login);
                    log.debug("email: {}", email);
                    log.debug("password: {}", password);

                    if (login.length() < 1 || email.length() < 1 ||
                            password.length() < 1) {

                        JOptionPane.showMessageDialog(CreateAccountDialog.this,
                                texts.getString("create_account_dialog_error_dialog_fill_in"),
                                texts.getString("error_dialog_title"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (userManager.getUserByLogin(login) != null) {
                        JOptionPane.showMessageDialog(CreateAccountDialog.this,
                                texts.getString("create_account_dialog_error_dialog_login"),
                                texts.getString("error_dialog_title"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    final User userNew;
                    try {
                        userNew = new User(login, password, email);
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(CreateAccountDialog.this,
                                texts.getString("create_account_dialog_error_dialog_email"),
                                texts.getString("error_dialog_title"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            userManager.createUser(userNew);

                            return null;
                        }
                    };
                    swingWorker.execute();


                }else{
                    editedUser.setLogin(textLogin.getText());
                    if(!textPassword.getPassword().toString().equals(PASSWORD_TEXT)){
                        editedUser.setPassword(textPassword.getPassword().toString());
                    }
                    editedUser.setEmail(textEmail.getText());

                    SwingWorker<Void,Void> swingWorker = new SwingWorker<Void, Void>() {
                        private User user = editedUser;

                        @Override
                        protected Void doInBackground() throws Exception {
                            userManager.updateUser(user);

                            return null;
                        }
                    };
                }

                dispose();
            }
        });
    }

    public void setEditedUser(User editedUser) {
        this.editedUser = editedUser;

        this.textPassword.setText(PASSWORD_TEXT);
        this.textLogin.setText(editedUser.getLogin());
        this.textEmail.setText(editedUser.getEmail());
    }
}
