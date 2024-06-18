package br.com.alura.ecommerce;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class HttpEcomerceService {

    public static void main(String[] args) {
        var server = new Server(8080);

        var context = new ServletContextHandler();
        context.setContextPath("/");
        context.addServlet(new ServletHolder(new NewOrderServlet()), "/new");

        server.setHandler(context);
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
