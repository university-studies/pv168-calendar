package cz.muni.fi.pv168.calendar;

import cz.muni.fi.pv168.calendar.entity.Event;
import cz.muni.fi.pv168.calendar.common.DBUtils;
import cz.muni.fi.pv168.calendar.service.EventManager;
import cz.muni.fi.pv168.calendar.service.impl.EventManagerImpl;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by xloffay on 19.3.14.
 */
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    public static final String DB_PROPERTIES = "/database.properties";
    public static final String DB_CREATE = "/create-tables.sql";
    public static final String DB_DROP = "/drop-tables.sql";

    public static void main(String[] args) {
        log.info("Main method");

        Properties conf = new Properties();
        InputStream inputStream = Main.class.getResourceAsStream(DB_PROPERTIES);
        try {
            conf.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PoolProperties p = new PoolProperties();
        p.setPassword(conf.getProperty("db.password"));
        p.setUsername(conf.getProperty("db.user"));
        p.setDriverClassName(conf.getProperty("db.driver"));
        p.setUrl(conf.getProperty("db.url"));
        DataSource dataSource = new DataSource(p);
        try {
            //DBUtils.executeSqlScript(dataSource, Main.class.getResource(Main.DB_DROP));
            DBUtils.executeSqlScript(dataSource, Main.class.getResource(Main.DB_CREATE));
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }

        EventManager eventManager = new EventManagerImpl(dataSource);
        for (Event event : eventManager.findAllEvents()) {
            log.debug(event.toString());
        }

        ApplicationContext ctx = new ClassPathXmlApplicationContext("spring-context.xml");
        EventManager springEvent = (EventManager)ctx.getBean("eventManager");
        Event event = new Event();
        event.setTitle("sa");
        event.setDescription("sa");
        event.setStartDate(DateTime.now());
        event.setEndDate(DateTime.now());
        springEvent.createEvent(event);
    }
}
