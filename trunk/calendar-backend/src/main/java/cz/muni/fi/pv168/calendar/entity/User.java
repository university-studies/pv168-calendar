package cz.muni.fi.pv168.calendar.entity;

import cz.muni.fi.pv168.calendar.common.PasswordHash;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Mario on 5.3.2014.
 */
public class User {
    /**
     * User's id
     */
    private long id;

    /**
     * User's login to calendar
     */
    private String login;
    /**
     * User's password
     */
    private String hashedPassword;
    /**
     * User's email. It will be used, when user forgot password.
     */
    private String email;

    public User(String login,String password,String email){
        setLogin(login);
        setPassword(password);
        setEmail(email);
    }

    /*
    public User(int id,String login,String password, String email){
        this.id = id;
        this.login = login;
        this.hashedPassword = password;
        this.email = email;
    }

    */

    public long getId() {
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return hashedPassword;
    }

    public String getEmail() {
        return email;
    }


    public void setLogin(String login) {
        if(login == null) throw new IllegalArgumentException("parameter login is null");
        this.login = login;
    }

    public void setEmail(String email) {
        if(!validateEmail(email))
            throw new IllegalArgumentException("invalid email");
        this.email = email;
    }

    public void setPassword(String password) {
        if(password == null)
            throw new IllegalArgumentException("Parameter password is null!");
        try {
            this.hashedPassword = PasswordHash.createHash(password);
//            this.hashedPassword = password;
        }catch(NoSuchAlgorithmException | InvalidKeySpecException ex){
            ex.printStackTrace();
        }
    }

    public void setHashedPassword(String hashedPassword){
        if(hashedPassword == null){
            throw new IllegalArgumentException("Parameter hashedPassword is null!");
        }
        this.hashedPassword = hashedPassword;
    }

    /**
     * Method validate the email
     * @param email
     * @return true if email is valid, on the other hand it returns false
     */
    private boolean validateEmail(String email){
        Pattern p = Pattern.compile("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}");
        Matcher m = p.matcher(email);
        return m.find();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != user.id) return false;
        else return login.equals(user.login);

    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 37*result + (int)(id ^ (id >>> 32));
        result = 37*result + login.hashCode();
        return result;
    }

    @Override
    public String toString(){
        return String.format("id: %d, login: %s, mail: %s",this.id,this.login,this.email);
    }
}
