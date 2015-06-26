package cz.muni.fi.pv168.calendar;

/**
 * Created by Pavol Loffay, p.loffay@gmail.com on 4/2/14.
 */

import cz.muni.fi.pv168.calendar.service.EventManager;
import cz.muni.fi.pv168.calendar.service.impl.EventManagerImpl;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.IOException;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class SpringTestConfig {
    Logger logger = LoggerFactory.getLogger(SpringTestConfig.class);


    @Bean
    public DataSource dataSource() throws IOException {
        Resource resource = new ClassPathResource("database.properties");
        Properties props = PropertiesLoaderUtils.loadProperties(resource);

        DataSource dataSource = new DataSource();
        dataSource.setDriverClassName(props.getProperty("db.driver.embedded"));
        dataSource.setUrl(props.getProperty("db.url.embedded"));
//        dataSource.setPassword("db.password");
//        dataSource.setUsername("db.user");

        JdbcTemplate template = new JdbcTemplate(dataSource);
        //caused error
//        JdbcTestUtils.dropTables(template, "users", "reminder", "event");
        JdbcTestUtils.executeSqlScript(template, new ClassPathResource
                ("create-tables.sql"), false);

        return dataSource;
    }



    @Value("classpath:create-tables.sql.sql")
    private Resource createTables;

    @Value("classpath:drop-tables.sql")
    private Resource dropTables;

    @Bean
    public DataSourceInitializer dataSourceInitializer(final DataSource dataSource) {
        final DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(databasePopulator());
        return initializer;
    }
    private DatabasePopulator databasePopulator() {
        final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(createTables);
        populator.addScript(dropTables);
        return populator;
    }

    @Bean
    public PlatformTransactionManager transactionManager() throws IOException {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public EventManager eventManager() throws IOException {
        EventManager eventManager = new EventManagerImpl(dataSource());
        return eventManager;
    }

//      @Bean
//    public DataSource dataSource() {
//        JdbcTestUtils./
    //sítová databáze
//        BasicDataSource bds = new BasicDataSource();
//        bds.setDriverClassName("org.apache.derby.jdbc.ClientDriver");
//        bds.setUrl("jdbc:derby://localhost:1527/MojeDB");
//        bds.setUsername("makub");
//        bds.setPassword("heslo");
//        JdbcTemplate template = new JdbcTemplate(bds);
//        JdbcTestUtils.dropTables(template, "leases", "books", "customers");
//        JdbcTestUtils.executeSqlScript(template, new ClassPathResource("my-schema.sql"), false);
//        JdbcTestUtils.executeSqlScript(template, new ClassPathResource("my-test-data.sql"), false);
//        return bds;

//    }

//      @Bean
//    public EmbeddedDatabase dataSourceEmbedded() {
//        embedded databáze
//        return new EmbeddedDatabaseBuilder()
//                .setType(DERBY)
//                .addScript("classpath:my-schema.sql")
//                .addScript("classpath:my-test-data.sql")
//                .build();
//    }
}
