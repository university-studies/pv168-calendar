package cz.muni.fi.pv168.calendar;

import org.junit.Before;

/**
 * Created by Mario on 5.3.2014.
 */
public class UserManagerTest {

    UserManager userManager;

    @Before
    public void setUp() throws Exception {
        userManager = new UserManagerImpl();
    }
}
