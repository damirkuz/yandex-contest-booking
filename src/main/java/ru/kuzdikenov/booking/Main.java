package ru.kuzdikenov.booking;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.kuzdikenov.booking.servlet.BookListServlet;
import ru.kuzdikenov.booking.servlet.BookServlet;
import ru.kuzdikenov.booking.servlet.PingServlet;
import ru.kuzdikenov.booking.util.DatabaseConfig;

public class Main {
    public static void main(String[] args) {
        int port = 8080;

        for (int i = 0; i < args.length; i++) {
            if ("--port".equals(args[i]) && i + 1 < args.length) {
                port = Integer.parseInt(args[i + 1]);
            }
        }

        try {
            DatabaseConfig.initialize();

            Server server = new Server(port);
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/");
            server.setHandler(context);

            context.addServlet(new ServletHolder(new PingServlet()), "/ping");
            context.addServlet(new ServletHolder(new BookServlet()), "/book");
            context.addServlet(new ServletHolder(new BookListServlet()), "/booklist");

            server.start();
            System.out.println("Service started on port " + port);
            server.join();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
