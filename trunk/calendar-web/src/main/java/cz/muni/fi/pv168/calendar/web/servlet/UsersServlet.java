package cz.muni.fi.pv168.calendar.web.servlet;

import com.sun.javaws.exceptions.InvalidArgumentException;
import cz.muni.fi.pv168.calendar.common.ServiceFailureException;
import cz.muni.fi.pv168.calendar.entity.User;
import cz.muni.fi.pv168.calendar.service.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Mario Kudolani on 16.4.2014.
 */
@WebServlet(UsersServlet.URL_MAPPING + "/*")
public class UsersServlet extends HttpServlet{

    private static final String LIST_JSP = "/users_list.jsp";
    public static final String URL_MAPPING = "/users";
    public static final String PASSWORD = "password";

    private final Logger log = LoggerFactory.getLogger(UsersServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        showUsersList(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");

        String oldPassword = "";
        String action = request.getPathInfo();
        switch (action){
            case "/add": {
                String login = request.getParameter("login");
                String password = request.getParameter("password");
                String email = request.getParameter("email");
                String id = request.getParameter("id");
                if (login == null || login.isEmpty() || password == null || password.isEmpty() || email == null || email.isEmpty()) {
                    request.setAttribute("error", "Je povinné vyplniť všetky hodnoty");
                    showUsersList(request, response);
                    return;
                }
                try {
                    User user = new User(login, password, email);
                    if (id == null || id.isEmpty()) {
                        getUserManager().createUser(user);
                        log.debug("created {}", user);
                    } else {
                        user.setId(Long.valueOf(id));
                        if(password.equals(PASSWORD)){
                            user.setHashedPassword(oldPassword);
                        }
                        getUserManager().updateUser(user);
                        log.debug("updated {}", user);
                    }
                    response.sendRedirect(request.getContextPath() + URL_MAPPING);
                    return;
                } catch (ServiceFailureException e) {
                    log.error("Cannot add user");
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                }catch(IllegalArgumentException e){
                    log.error("Invalid email, cannot add user");
                    request.setAttribute("error","Chybný mail");
                    showUsersList(request,response);
                    return;
                }
            }
            case "/delete":
                try{
                    Long id = Long.valueOf(request.getParameter("id"));
                    getUserManager().removeUser(id);
                    log.debug("deleted user {}",id);
                    response.sendRedirect(request.getContextPath()+URL_MAPPING);
                    return;
                }catch(ServiceFailureException e){
                    log.error("cannot delete user",e);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e.getMessage());
                    return;
                }
            case "/update":
                try {
                    Long id = Long.valueOf(request.getParameter("id"));
                    log.debug("update user id={}", id);
                    User user = getUserManager().getUserById(id);
                    oldPassword = user.getPassword();
                    user.setHashedPassword(PASSWORD);
                    request.setAttribute("user", user);
                    showUsersList(request, response);
                }catch (ServiceFailureException e){
                    log.error("cannot update user",e);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                }
                return;
            default:
                log.error("Unknown action" + action);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown action " + action);
        }
    }


    private UserManager getUserManager(){
        return (UserManager) getServletContext().getAttribute("userManager");
    }

    private void showUsersList(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try{
            request.setAttribute("users", getUserManager().getAllUsers());
            request.getRequestDispatcher(LIST_JSP).forward(request,response);
        }catch (ServiceFailureException e){
            log.error("Cannot show users");
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e.getMessage());
        }
    }
}
