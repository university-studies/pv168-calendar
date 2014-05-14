package cz.muni.fi.pv168.calendar.gui;

import cz.muni.fi.pv168.calendar.entity.Event;
import cz.muni.fi.pv168.calendar.service.EventManager;
import org.jdesktop.swingx.JXMonthView;
import org.joda.time.DateTime;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

//import java.awt.*;

/**
 * Created by pavol on 9.5.2014.
 */
public class EventsTableModel extends AbstractTableModel {
    private ResourceBundle texts = ResourceBundle.getBundle("Texts");

    private Long userId;
    private DateTime date;
    private EventManager eventManager;
    private java.util.List<Event> events;
    private JXMonthView jxMonthView;

    public EventsTableModel(Long userId, DateTime date, EventManager eventManager) {
        this.userId = userId;
        this.date = date;
        this.eventManager = eventManager;

        if (userId == null || date == null) {
            events = Collections.<Event>emptyList();
        } else {
            updateData();
//            events = eventManager.findEventByStartDateAndUser(date, userId);
        }
    }

    public void updateData() {
        if (userId == null || date == null) {
            events = Collections.<Event>emptyList();
        } else {
            //load data from background
            SwingWorker <List<Event>, Void>swingWorker = new SwingWorker<List<Event>, Void>() {
                @Override
                protected List<Event> doInBackground() throws Exception {
                    events = eventManager.findEventByStartDateAndUser(date, userId);
                    return events;
                }

                @Override
                protected void done() {
                    fireTableCellUpdated(0, 99);
                }
            };

            swingWorker.execute();

            //events = eventManager.findEventByStartDateAndUser(date, userId);
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

    public void setMonthView(JXMonthView monthView) {
        this.jxMonthView = monthView;
    }
}

class ButtonCellRenderer extends JButton implements TableCellRenderer {

    public ButtonCellRenderer() {
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

class ButtonCellEditor extends DefaultCellEditor {

    protected JButton button;
    private String label;
    private boolean isPushed;

    int row;
    int column;
    JTable table;

    public ButtonCellEditor(JCheckBox checkBox) {
        super(checkBox);
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });
    }

    @Override
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

    @Override
    public Object getCellEditorValue() {
        if (isPushed) {
            final EventsTableModel model = (EventsTableModel) table.getModel();
            final Event eventToDelete = model.getEventAtRow(row);

            //delete in background
            SwingWorker<Void,Void> swingWorker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    model.getEventManager().deleteEvent(eventToDelete);
                    return null;
                }
            };
            swingWorker.execute();

            model.deleteEvent(row);
            model.fireTableRowsDeleted(row, row);
        }
        isPushed = false;
        return new String(label);
    }

    @Override
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }

    @Override
    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }
}
