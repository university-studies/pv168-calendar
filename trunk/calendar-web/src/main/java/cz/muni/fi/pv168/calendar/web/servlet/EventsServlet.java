package cz.muni.fi.pv168.calendar.web.servlet;

import cz.muni.fi.pv168.calendar.common.ServiceFailureException;
import cz.muni.fi.pv168.calendar.entity.Event;
import cz.muni.fi.pv168.calendar.service.EventManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by pavol on 12.4.2014.
 */
@WebServlet(name = "events", urlPatterns = {"/events/*"})
public class EventsServlet extends HttpServlet{

    private static final Logger log = LoggerFactory.getLogger(EventsServlet.class);
    private static final String JSP_LIST = "/events_list.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.trace("doGet");
        EventManager eventManager = getEventManager();
        try {
            request.setAttribute("events", getEventManager().findAllEvents());
//            getServletContext().setAttribute("event", getEventManager().findEventById(4l));
            //log.trace(getEventManager().findEventById(4l).toString());
            request.getRequestDispatcher(JSP_LIST).forward(request, response);
        }
        catch (ServiceFailureException ex) {
            log.error("Cannot show list of events");
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        }

        response.setContentType("text/html;charset=UTF-8");

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String action = request.getPathInfo();
        switch (action)
        {
            case "/add": {
                log.trace("add");
                String title = request.getParameter("title");
                String description = request.getParameter("description");
                String location = request.getParameter("location");
                String id = request.getParameter("id");
                log.debug("title {}", title);
                log.debug("description {}", description);
                log.debug("location {}", location);
               log.debug("id {}", id);

                try {
                    if (StringUtils.isNotBlank(title)) {
                        Event event = new Event();
                        event.setTitle(title);
                        event.setDescription(description);
                        event.setLocation(location);

                        if (id == null || StringUtils.isBlank(id))
                            getEventManager().createEvent(event);
                        else {
                            event.setId(Long.parseLong(id));
                            getEventManager().updateEvent(event);
                        }
                        response.sendRedirect(request.getContextPath() + "events");
                        return;
                    }
                } catch (ServiceFailureException ex) {
                    log.error("Cannot add event", ex);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
                    return;
                }
                break;
            }
            case "/delete" : {
                log.trace("delete");
                Long id = Long.valueOf(request.getParameter("id"));
                try {
                    Event event = new Event();
                    event.setId(id);
                    getEventManager().deleteEvent(event);
                    response.sendRedirect(request.getContextPath() + "events");
                    return;
                }
                catch (ServiceFailureException ex) {
                    log.error("Cannod remove event", ex);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
                    return;
                }
            }
            case "/update": {
                log.trace("update id = {}", request.getParameter("id"));
                Long id = Long.valueOf(request.getParameter("id"));
                request.setAttribute("event", getEventManager().findEventById(id));
                request.setAttribute("events", getEventManager().findAllEvents());
                log.trace(getEventManager().findEventById(id).toString());
                request.getRequestDispatcher(JSP_LIST).forward(request, response);
                break;
            }
            default: {
                log.trace("default");
            }
        }
    }

    private EventManager getEventManager() {
        return (EventManager) getServletContext().getAttribute("eventManager");
    }
}
