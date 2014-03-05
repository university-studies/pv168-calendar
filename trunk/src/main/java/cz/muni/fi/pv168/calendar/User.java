package cz.muni.fi.pv168.calendar;

/**
 * Created by Mario on 5.3.2014.
 */
public class User {
    /**
     * User's id
     */
    private int id;

    /**
     * User's login to calendar
     */
    private String login;
    /**
     * User's password
     */
    private String password;
    /**
     * User's email. It will be used, when user forgot password.
     */
    private String email;

    public User(){}

    public User(int id,String login,String password, String email){
        this.id = id;
        this.login = login;
        this.password = password;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
