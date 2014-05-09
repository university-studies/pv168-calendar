package cz.muni.fi.pv168.calendar.gui;

import cz.muni.fi.pv168.calendar.entity.*;
import cz.muni.fi.pv168.calendar.entity.Event;
import cz.muni.fi.pv168.calendar.service.EventManager;
import cz.muni.fi.pv168.calendar.service.UserManager;
import cz.muni.fi.pv168.calendar.service.UsersEventManager;
import org.jdesktop.swingx.JXMonthView;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;

import java.util.Collections;
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

    public Application(ApplicationContext applicationContext) throws UnsupportedEncodingException {
        if (applicationContext == null) {
            throw new IllegalArgumentException("applicationContext");
        }

        userLogged = null;
        selectedDate = null;
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

        eventsTable.getColumn(texts.getString("event_table_header_actions")).setCellRenderer(new ButtonRenderer());
        eventsTable.getColumn(texts.getString("event_table_header_actions")).setCellEditor(new ButtonEditor(new JCheckBox()));
    }

    public class EventsTableModel extends AbstractTableModel {
        private ResourceBundle texts = ResourceBundle.getBundle("Texts");

        private Long userId;
        private DateTime date;
        private EventManager eventManager;
        private java.util.List<Event> events;

        public EventsTableModel(Long userId, DateTime date, EventManager eventManager) {
            this.userId = userId;
            this.date = date;
            this.eventManager = eventManager;

            if (userId == null || date == null) {
                events = Collections.<Event>emptyList();
            } else {
                events = eventManager.findEventByStartDateAndUser(date, userId);
            }
        }

        public void updateData() {
            if (userId == null || date == null) {
                events = Collections.<Event>emptyList();
            } else {
                events = eventManager.findEventByStartDateAndUser(date, userId);
            }
        }

        @Override
        public int getRowCount() {
            return events.size();
        }

        @Override
        public int getColumnCount() {
            return 5;
        }

        @Override
        public Object getValueAt(int row, int column) {
            Event event = events.get(row);

            switch (column) {
                case 0:
                    return event.getTitle();
                case 1:
                    return event.getDescription();
                case 2:
                    return event.getLocation();
                case 3:
                    return event.getStartDate();
                case 4:
                    return texts.getString("event_table_button_delete");
                default:
                    throw new IllegalArgumentException("column");
            }
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case 0:
                    return texts.getString("event_table_header_title");
                case 1:
                    return texts.getString("event_table_header_description");
                case 2:
                    return texts.getString("event_table_header_location");
                case 3:
                    return texts.getString("event_table_header_start_date");
                case 4:
                    return texts.getString("event_table_header_actions");
                default:
                    throw new IllegalArgumentException("column");
            }
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public DateTime getDate() {
            return date;
        }

        public void setDate(DateTime date) {
            this.date = date;
        }

        public EventManager getEventManager() {
            return eventManager;
        }

        public Event getEventAtRow(int row) {
            return  events.size() >= row ? events.get(row) : null;
        }

        public void deleteEvent(int row) {
            if (events.size() >= row) {
                events.remove(row);
                if (events.size() == 0) {
                    jxMonthView.removeFlaggedDates(date.toDate());
                }
            }


        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
//            Event event = events.get(rowIndex);
            switch (columnIndex) {
                case 0:
//                    event.setTitle((String) aValue);
                    break;
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    break;
                default:
                    throw new IllegalArgumentException("columnIndex");
            }
            fireTableCellUpdated(rowIndex, columnIndex);
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            switch (columnIndex) {
                case 0:
                case 1:
                case 2:
                case 3:
                    return false;
                case 4:
                    return true;
                default:
                    throw new IllegalArgumentException("columnIndex");
            }
        }
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {

        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {

            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(table.getBackground());
            }

            setText(value == null  ?  "" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {

        protected JButton button;
        private String label;
        private boolean isPushed;

        int row;
        int column;
        JTable table;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    log.debug("clicked at button");
                    fireEditingStopped();
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            if (isSelected) {
                button.setForeground(table.getSelectionForeground());
                button.setBackground(table.getSelectionBackground());
            } else {
                button.setForeground(table.getForeground());
                button.setBackground(table.getBackground());
            }
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;

            this.row = row;
            this.column = column;
            this.table = table;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                EventsTableModel model = (EventsTableModel) table.getModel();
                Event eventToDelete = model.getEventAtRow(row);
                model.getEventManager().deleteEvent(eventToDelete);

                model.deleteEvent(row);
                model.fireTableRowsDeleted(row, row);
            }
            isPushed = false;
            return new String(label);
        }

        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }
}


