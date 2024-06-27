package br.com.alura.ecommerce;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderService {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try (var orderDispatcher = new KafkaDispatcher<Order>()) {
            try (var emailDispatcher = new KafkaDispatcher<String>()) {
                for (var i = 0; i < 10; i++) {

                    var email = "nome@email.com";
                    var orderId = UUID.randomUUID().toString();
                    var amount = BigDecimal.valueOf(Math.random() * 5000 + 1);

                    var order = new Order(email, orderId, amount);
                    orderDispatcher.send("ECOMMERCE_NEW_ORDER",
                            new CorrelationId(NewOrderService.class.getSimpleName()), email, order);

                    var emailCode = "Thank you for your order! We are processing your order!";
                    emailDispatcher.send("ECOMMERCE_SEND_EMAIL",
                            new CorrelationId(NewOrderService.class.getSimpleName()), email, emailCode);
                }
            }
        }
    }

}
