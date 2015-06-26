package cz.muni.fi.pv168.calendar.web.servlet;

/**
 * Created by xloffay on 2.4.14.
 */
import cz.muni.fi.pv168.calendar.service.UserManager;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(urlPatterns = {"/jstl"})
public class JSTLExample extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Map<String, Map<String,Object>> m = new TreeMap<>();

        for(Locale l :Locale.getAvailableLocales()) {
            Map<String, Object> v = new HashMap<>();
            m.put(l.toString(), v);

            v.put("name", l.getDisplayName());
            v.put("origname", l.getDisplayName(l));
            v.put("loc", l);
        }
        request.setAttribute("jazyky", m);

        request.getRequestDispatcher("/jstl.jsp").forward(request, response);
    }

//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        log.trace("users doGet");
//        response.setContentType("text/html;charset=UTF-8");
//
//        UserManager userManager = getUserManager();
//        request.setAttribute("events", userManager.getAllUsers());
//
//        PrintWriter out = response.getWriter();
//        out.println("<h1>Muj prvni servlet</h1>");
//
//        int n = 10;
//        String np = request.getParameter("n");
//        if (np != null) n = Integer.parseInt(np);
//
//        for (int i = 0; i < n; i++) {
//            out.println("i=" + i + "<br/>");
//        }
//
//        String fromListener = (String) getServletContext().getAttribute("atribut");
//        out.println(fromListener);
//    }
}
