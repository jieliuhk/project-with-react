package server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import sevlet.*;

/**
 * This class uses Jetty & servlets to implement server serving hotel info
 */
public class WebServer {
    private static final int PORT = 8080;

    public static void main(String[] args) throws Exception {
        Server server = new Server(PORT);

        FilterHolder filterHolder = new FilterHolder(CrossOriginFilter.class);
        filterHolder.setInitParameter("allowedOrigins", "*");
        filterHolder.setInitParameter("allowedMethods", "GET, POST, PUT, DELETE");

        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        handler.addServlet(HotelInfoServlet.class, "/hotels");
        handler.addServlet(ReviewInfoServlet.class, "/reviews");
        handler.addServlet(AttractionsServlet.class, "/attractions");
        handler.addServlet(SavedHotelServlet.class, "/savedhotel");
        handler.addServlet(UserInfoServlet.class, "/users");
        handler.addServlet(UserRegisterServlet.class, "/registration");
        handler.addServlet(UserVerifyServlet.class, "/verify");
        handler.addServlet(UserLogoutServlet.class, "/logout");
        handler.addFilter(filterHolder, "/*", null);


        server.setHandler(handler);

        server.start();
        server.join();
    }
}
