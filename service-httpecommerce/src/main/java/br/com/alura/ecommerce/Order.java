package br.com.alura.ecommerce;

import java.math.BigDecimal;

public class Order {

    private final String email;
    private final String orderId;
    private final BigDecimal amount;

    public Order(String email, String orderId, BigDecimal amount) {
        this.email = email;
        this.orderId = orderId;
        this.amount = amount;
    }

    public String getOrderId() {
        return orderId;
    }
}
