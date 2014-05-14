package cz.muni.fi.pv168.calendar.gui;

import cz.muni.fi.pv168.calendar.entity.User;
import cz.muni.fi.pv168.calendar.service.UserManager;
import javafx.event.EventDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class UsersListDialog extends JDialog {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private UserManager userManager;
    private ResourceBundle texts = ResourceBundle.getBundle("Texts", Locale.getDefault());

    private JFrame parent;

    private JPanel contentPane;
    private JTable jTableUsers;
    private JButton signUpButton;
    private JButton editAccountButton;

    private User signedUser;

    public UsersListDialog(final JFrame parent, final UserManager userManager, User signedUser) {
        super(parent,true);
        this.parent = parent;
        this.signedUser = signedUser;

        setTitle(texts.getString("users_list_dialog_title"));
        setContentPane(contentPane);
        setModal(true);
        //getRootPane().setDefaultButton(buttonOK);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(parent);

        jTableUsers.setModel(new UserTableModel());

        loadUsersFromDB();

        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CreateAccountDialog dialog = new CreateAccountDialog(parent, userManager);
                dialog.setVisible(true);
            }
        });
        editAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                CreateAccountDialog dialog = new CreateAccountDialog(parent,userManager,((UserTableModel)jTableUsers.getModel()).getUser(jTableUsers.getSelectedRow()));
                dialog.setVisible(true);
            }
        });
    }

    private void loadUsersFromDB(){
        SwingWorker<Collection<User>, Void> swingWorker = new SwingWorker<Collection<User>, Void>() {
            @Override
            protected Collection<User> doInBackground() throws Exception {
                return userManager.getAllUsers();
            }

            @Override
            protected void done() {
                UserTableModel model = (UserTableModel) jTableUsers.getModel();
                try {
                    model.addAllUser(get());
                } catch (InterruptedException e) {
                    log.error("chyba pri nacitavani pouzivatelov do tabulky", e.getMessage());
                    EventQueue.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                JOptionPane.showMessageDialog(contentPane, texts.getString("users_list_dialog_error_message"), texts.getString("error"), JOptionPane.ERROR_MESSAGE);

                            }
                        }
                    );
                } catch (ExecutionException e) {
                    log.error("chyba pri nacitavani pouzivatelov do tabulky", e.getMessage());
                    EventQueue.invokeLater(new Runnable() {
                                               @Override
                                               public void run() {
                                                   JOptionPane.showMessageDialog(contentPane, texts.getString("users_list_dialog_error_message"), texts.getString("error"), JOptionPane.ERROR_MESSAGE);

                                               }
                                           }
                    );
                }
            }
        };

        swingWorker.execute();
    }

    private class UserTableModel extends AbstractTableModel{
        private List<User> userList = new ArrayList<User>();

        @Override
        public int getRowCount() {
            return userList.size();
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        public User getUser(int rowIndex){
            return userList.get(rowIndex);
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            User user = userList.get(rowIndex);
            switch (columnIndex){
                case 0:
                    return user.getId();
                case 1:
                    return user.getLogin();
                case 2:
                    return user.getEmail();
                default:
                    throw new IllegalArgumentException("columnIndex");
            }
        }

        public void addUser(User user){
            if(user == null)
                throw new IllegalArgumentException("user");
            this.userList.add(user);
            int lastRow = userList.size() - 1;
            fireTableRowsInserted(0, lastRow);
        }

        public void addAllUser(Collection<User> users){
            for(User u : users){
                addUser(u);
            }

        }

        @Override
        public String getColumnName(int column) {
            switch (column){
                case 0:
                    return texts.getString("user_list_dialog_id");
                case 1:
                    return texts.getString("user_list_dialog_login");
                case 2:
                    return texts.getString("user_list_dialog_email");
                default:
                    throw new IllegalArgumentException("column");
            }
        }

        private void updateUserToDB(final User user){
            SwingWorker<Void,Void> swingWorker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    userManager.updateUser(user);

                    return null;
                }
            };

            swingWorker.execute();
        }



        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex){
                case 0:
                    return Integer.class;
                case 1:
                case 2:
                    return String.class;
                default:
                    throw new IllegalArgumentException("columnIndex");
            }
        }
    }
}
