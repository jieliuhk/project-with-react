package server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import sevlet.as.RegisterServlet;
import sevlet.as.TGTServlet;
import sevlet.kdc.TicketServlet;

public class IdentityServer {
    private static final int PORT = 8090;

    public static void main(String[] args) throws Exception {
        Server server = new Server(PORT);

        FilterHolder filterHolder = new FilterHolder(CrossOriginFilter.class);
        filterHolder.setInitParameter("allowedOrigins", "*");
        filterHolder.setInitParameter("allowedMethods", "GET, POST");

        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        handler.addServlet(RegisterServlet.class, "/registration");
        handler.addServlet(TGTServlet.class, "/tgs");
        handler.addServlet(TicketServlet.class, "/ticket");
        handler.addFilter(filterHolder, "/*", null);

        server.setHandler(handler);
        server.start();
        server.join();
    }
}
