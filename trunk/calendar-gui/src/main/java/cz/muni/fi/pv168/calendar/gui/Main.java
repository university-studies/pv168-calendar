package cz.muni.fi.pv168.calendar.gui;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.awt.*;


/**
 * Created by xloffay on 19.3.14.
 */
public class Main {

    public static void main(String[] args) {
//      Inicializaci GUI provedeme ve vlákně message dispatcheru,
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                    Application app = new Application(new ClassPathXmlApplicationContext("spring-context.xml"));
            }
        });
    }
}
