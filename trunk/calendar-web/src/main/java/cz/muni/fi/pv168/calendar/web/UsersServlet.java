package cz.muni.fi.pv168.calendar.web;

/**
 * Created by xloffay on 2.4.14.
 */

        import javax.servlet.ServletException;
        import javax.servlet.annotation.WebServlet;
        import javax.servlet.http.HttpServlet;
        import javax.servlet.http.HttpServletRequest;
        import javax.servlet.http.HttpServletResponse;
        import java.io.IOException;
        import java.io.PrintWriter;

@WebServlet(urlPatterns = {"/users/*", "*.users"})
public class UsersServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
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

}