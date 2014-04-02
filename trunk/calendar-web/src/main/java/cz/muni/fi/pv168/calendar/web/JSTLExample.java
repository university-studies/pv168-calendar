package cz.muni.fi.pv168.calendar.web;

/**
 * Created by xloffay on 2.4.14.
 */
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(urlPatterns = {"/jstl"})
public class JSTLExample extends HttpServlet {

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

}