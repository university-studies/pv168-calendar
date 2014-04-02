package cz.muni.fi.pv168.calendar.web.listener;

/**
 * Created by xloffay on 2.4.14.
 */
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class MyContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent ev) {
        System.out.println("aplikace inicializována");
        ServletContext servletContext = ev.getServletContext();
        servletContext.setAttribute("atribut", "konstanta");
    }

    @Override
    public void contextDestroyed(ServletContextEvent ev) {
        System.out.println("aplikace končí");
    }
}