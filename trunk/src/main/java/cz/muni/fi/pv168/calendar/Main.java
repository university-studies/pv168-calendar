package cz.muni.fi.pv168.calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by xloffay on 19.3.14.
 */
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static final String DB_PROPERTIES = "/database.properties";

    public static void Main(String[] args) {
        log.info("Main method");

        Properties properties = new Properties();
        InputStream inputStream = Main.class.getResourceAsStream(DB_PROPERTIES);

        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        log.debug(properties.getProperty("db.user"));
    }
}
