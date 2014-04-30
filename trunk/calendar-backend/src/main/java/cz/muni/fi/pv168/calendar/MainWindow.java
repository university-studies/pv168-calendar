package cz.muni.fi.pv168.calendar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Mario Kudolani on 29.4.2014.
 */
public class MainWindow {

    private JPanel topPanel;
    private JTabbedPane tabPane;
    private JTextField loginTextField;
    private JButton removeUserButton;
    private JButton addUserButton;
    private JTable table2;
    private JButton addEventButton;
    private JButton removeEventButton;
    private JTable table1;

    public MainWindow(){

    }

    public static void main(String[] args){
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Calendar");
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setContentPane(new MainWindow().topPanel);
                frame.setPreferredSize(new Dimension(800, 600));
                frame.setJMenuBar(new MainWindow().createMenu());
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    private JMenuBar createMenu(){
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        final JMenu helpMenu = new JMenu("Help");
        menuBar.add(fileMenu);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(helpMenu);
        //menu file
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        fileMenu.add(exitMenuItem);
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        //menu help
        JMenuItem aboutMenuItem = new JMenuItem("About");
        helpMenu.add(aboutMenuItem);
        aboutMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(helpMenu,"Calendar application","About",JOptionPane.INFORMATION_MESSAGE);
            }
        });

        return menuBar;
    }

}
