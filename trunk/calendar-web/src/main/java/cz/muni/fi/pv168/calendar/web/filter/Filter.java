package cz.muni.fi.pv168.calendar.web.filter;

/**
 * Created by xloffay on 2.4.14.
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter("/*")
public class Filter implements javax.servlet.Filter {

    private static final Logger log = LoggerFactory.getLogger(Filter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("Filter inicializovany");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
//        HttpServletRequest request = (HttpServletRequest) req;
//        request.setCharacterEncoding("utf8");

        log.debug("poziadavka na filter :{}", ((HttpServletRequest) req).getRequestURI());
        chain.doFilter(req,res);
    }

    @Override
    public void destroy() {
        log.info("Filter zniceny");
    }
}
