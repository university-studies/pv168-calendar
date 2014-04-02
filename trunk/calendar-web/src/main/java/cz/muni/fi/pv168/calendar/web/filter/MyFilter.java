package cz.muni.fi.pv168.calendar.web.filter;

/**
 * Created by xloffay on 2.4.14.
 */
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter("/*")
public class MyFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("filter inicializován");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        request.setCharacterEncoding("utf8");

        System.out.println("požadavek na "+((HttpServletRequest)req).getRequestURI());
        chain.doFilter(req,res);
    }

    @Override
    public void destroy() {
    }
}