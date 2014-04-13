package cz.muni.fi.pv168.calendar.web.listener;

import cz.muni.fi.pv168.calendar.service.EventManager;
import cz.muni.fi.pv168.calendar.service.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Created by xloffay on 2.4.14.
 */
@WebListener
public class ContextListener implements ServletContextListener {

    private static final Logger log = LoggerFactory.getLogger(ContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent ev) {
        log.info("Apikacia inicializovana");

        ServletContext servletContext = ev.getServletContext();

        // AnnotationConfigApplicationContext(SpringConfig.class);
        ApplicationContext springContext = new ClassPathXmlApplicationContext("spring-context.xml");
        EventManager eventManager = springContext.getBean("eventManager", EventManager.class);
        if (eventManager != null)
            log.info("Bean eventManager nacitana korektne");
        servletContext.setAttribute("eventManager", eventManager);
        servletContext.setAttribute("userManager", springContext.getBean("userManager", UserManager.class));
    }

    @Override
    public void contextDestroyed(ServletContextEvent ev) {
        log.info("Aplikacia konci");
    }
}
