package br.com.alura.ecommerce;

import br.com.alura.ecommerce.dispatcher.KafkaDispatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class NewOrderServlet extends HttpServlet {

    private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();

    @Override
    public void destroy() {
        super.destroy();
        orderDispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            var email = req.getParameter("email");
            var orderId = req.getParameter("uuid");
            var amount = new BigDecimal(req.getParameter("amount"));
            var order = new Order(email, orderId, amount);

            try (var database = new OrdersDatabase()) {
                if (database.saveNew(order)) {
                    orderDispatcher.send("ECOMMERCE_NEW_ORDER",
                            new CorrelationId(NewOrderServlet.class.getSimpleName()), email, order);

                    System.out.println("New order sent successfully!");
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                    resp.getWriter().println("New order sent");
                } else {
                    System.out.println("Old order sent successfully!");
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().println("Old order sent");
                }
            }
        } catch (ExecutionException | InterruptedException | SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
