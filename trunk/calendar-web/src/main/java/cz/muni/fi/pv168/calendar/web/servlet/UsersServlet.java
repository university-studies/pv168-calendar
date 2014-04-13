package cz.muni.fi.pv168.calendar.web.servlet;

/**
 * Created by xloffay on 2.4.14.
 */

import cz.muni.fi.pv168.calendar.service.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "users", urlPatterns = {"/users/*", "*.users"})
public class UsersServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(UsersServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.trace("users doGet");
        response.setContentType("text/html;charset=UTF-8");

        UserManager userManager = getUserManager();
        request.setAttribute("events", userManager.getAllUsers());

        PrintWriter out = response.getWriter();
        out.println("<h1>Muj prvni servlet</h1>");

        int n = 10;
        String np = request.getParameter("n");
        if (np != null) n = Integer.parseInt(np);

        for (int i = 0; i < n; i++) {
            out.println("i=" + i + "<br/>");
        }

        String fromListener = (String) getServletContext().getAttribute("atribut");
        out.println(fromListener);
    }

    private UserManager getUserManager() {
        return (UserManager) getServletContext().getAttribute("userManager");
    }
}
