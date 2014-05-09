package cz.muni.fi.pv168.calendar.gui;

import cz.muni.fi.pv168.calendar.entity.Event;
import cz.muni.fi.pv168.calendar.service.EventManager;
import org.jdesktop.swingx.JXDatePicker;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.ResourceBundle;

public class CreateEventDialog extends JDialog {
    private final static Logger log = LoggerFactory.getLogger(CreateEventDialog.class);
    private static ResourceBundle texts = ResourceBundle.getBundle("Texts");
    private final static String TITLE = "Create Event";

    private JPanel contentPane;
    private JTextField textTitle;
    private JTextField textLocation;
    private JButton createButton;
    private JButton cancelButton;
    private JXDatePicker datePicker;
    private JTextArea textDescription;
    private JFrame parent;

    private EventManager eventManager;
    private Long userId;
    private Date newEventDate = null;

    public CreateEventDialog(JFrame parent, final EventManager eventManager, Date date, final Long userId) {
        super(parent, true);
        this.parent = parent;
        this.eventManager = eventManager;
        this.userId = userId;

        datePicker.setDate(date);
        textDescription.setRows(3);
        setTitle(TITLE);
        getContentPane().add(contentPane);
        // ENTER submit button
        getRootPane().setDefaultButton(createButton);
        pack();
        setLocationRelativeTo(parent);
        setResizable(true);

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                dispose();
            }
        });
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String title = textTitle.getText();
                String description = textDescription.getText();
                String location = textLocation.getText();

                DateTime date = new DateTime(datePicker.getDate());
                if (title.length() < 1 || date == null) {
                    JOptionPane.showMessageDialog(CreateEventDialog.this,
                            texts.getString("create_event_dialog_error_dialog_fill_in"),
                            texts.getString("error_dialog_title"), JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Event event = new Event();
                event.setTitle(title);
                event.setDescription(description);
                event.setLocation(location);
                event.setUserId(userId);
                event.setStartDate(date);
                event.setEndDate(DateTime.now());

                eventManager.createEvent(event);
                newEventDate = date.toDate();
                dispose();
            }
        });
    }

    public Date getNewEventDate() {
        return newEventDate;
    }

    public void setNewEventDate(Date newEventDate) {
        this.newEventDate = newEventDate;
    }
}
