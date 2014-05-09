package cz.muni.fi.pv168.calendar.gui;

import cz.muni.fi.pv168.calendar.entity.Event;
import cz.muni.fi.pv168.calendar.entity.User;
import cz.muni.fi.pv168.calendar.service.EventManager;
import cz.muni.fi.pv168.calendar.service.UserManager;
import cz.muni.fi.pv168.calendar.service.UsersEventManager;
import org.jdesktop.swingx.JXMonthView;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

/**
 * Created by pavol on 7.5.2014.
 */
public class Application extends JFrame{
    private final static Logger log = LoggerFactory.getLogger(Application.class);

    private static ResourceBundle texts = ResourceBundle.getBundle("Texts");

    private ApplicationContext applicationContext;
    private EventManager eventManager;
    private UserManager userManager;
    private UsersEventManager usersEventManager;
    private User userLogged;
    private DateTime selectedDate;

    private JXMonthView jxMonthView;
    private JButton loginButton;
    private JPanel contentPanel;
    private JButton createAccountButton;
    private JPanel calendarPane;
    private JButton createEventButton;
    private JTable eventsTable;
    private EventsTableModel tableModel;

    private static JFrame frame;

    public Application(ApplicationContext applicationContext) {
        if (applicationContext == null) {
            throw new IllegalArgumentException("applicationContext");
        }

        userLogged = null;
        selectedDate = null;
        selectedDate = DateTime.now();
        this.applicationContext =  applicationContext;
        eventManager = (EventManager) applicationContext.getBean("eventManager");
        userManager = (UserManager) applicationContext.getBean("userManager");
        usersEventManager = (UsersEventManager) applicationContext.getBean("usersEventManager");

        tableModel = new EventsTableModel(null, selectedDate, eventManager);
        eventsTable.setModel(tableModel);

        createEventButton.setVisible(false);
        jxMonthView = new JXMonthView();

        selectedDate = new DateTime(jxMonthView.getToday());
        calendarPane.add(jxMonthView);
        jxMonthView.setShowingTrailingDays(true);
        jxMonthView.setTraversable(true);

        frame = new JFrame();
        frame.setLocationRelativeTo(null);
        // Zajistíme, aby se po zavření okna ukončila i aplikace
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle(texts.getString("window_title"));
        frame.setContentPane(contentPanel);
        frame.pack();
        frame.setVisible(true);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                LoginDialog loginDialog = new LoginDialog(frame, userManager);
                loginDialog.setVisible(true);

                if (loginDialog.isLoggedSuccessfully()) {
                    userLogged = loginDialog.getUserLogged();
                    log.debug("logged user id = {}", userLogged.getId());
                    tableModel.setUserId(userLogged.getId());
                    tableModel.setDate(selectedDate);
                    tableModel.updateData();
                    eventsTable.updateUI();

                    jxMonthView.clearFlaggedDates();
                    for (Event event : usersEventManager.findEventsByUserId(userLogged.getId())) {
                        jxMonthView.addFlaggedDates(event.getStartDate().toDate());
                    }
                    createEventButton.setVisible(true);
                    frame.pack();
                }
            }
        });
        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                CreateAccountDialog createAccountDialog = new CreateAccountDialog(frame, userManager);
                createAccountDialog.setVisible(true);
            }
        });
        createEventButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                CreateEventDialog createEventDialog = new CreateEventDialog(frame, eventManager,
                        selectedDate.toDate(), userLogged.getId());

                createEventDialog.setVisible(true);
                if (createEventDialog.getNewEventDate() != null) {
                    tableModel.updateData();
                    eventsTable.updateUI();
                    jxMonthView.addFlaggedDates(createEventDialog.getNewEventDate());
                }
            }
        });
        jxMonthView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                selectedDate = new DateTime(jxMonthView.getSelectionDate());
                tableModel.setDate(selectedDate);
                tableModel.updateData();
                eventsTable.updateUI();
            }
        });

        eventsTable.getColumn(texts.getString("event_table_header_actions")).setCellRenderer(new ButtonCellRenderer());
        eventsTable.getColumn(texts.getString("event_table_header_actions")).setCellEditor(new ButtonCellEditor(new JCheckBox()));
        tableModel.setMonthView(jxMonthView);
    }
}


