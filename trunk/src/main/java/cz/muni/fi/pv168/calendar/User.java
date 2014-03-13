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
    private final String login;
    /**
     * User's password
     */
    private final String hashPassword;
    /**
     * User's email. It will be used, when user forgot password.
     */
    private final String email;

    public User(String login,String password,String email){
        this.login = login;
        this.hashPassword = password;
        this.email = email;
    }


    public User(int id,String login,String password, String email){
        this.id = id;
        this.login = login;
        this.hashPassword = password;
        this.email = email;
    }



    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return hashPassword;
    }

    public String getEmail() {
        return email;
    }



    /**
     * Method validate the email
     * @param email
     * @return true if email is valid, on the other hand it returns false
     */
    private boolean validateEmail(String email){
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != user.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 37*result + id;
        return result;
    }

    @Override
    public String toString(){
        return String.format("id: %d, login: %s, mail: %s",this.id,this.login,this.email);
    }
}
