package br.com.alura.ecommerce;

import java.util.UUID;

public class CorrelationId {

    private final String id;

    public CorrelationId(String title) {
        this.id = title + "[" + UUID.randomUUID().toString() + "]";
    }

    public String getId() {
        return id;
    }

    public CorrelationId continueWith(String title) {
        return new CorrelationId(id + ">" + title);
    }

    @Override
    public String toString() {
        return "CorrelationId{" +
                "id='" + id + '\'' +
                '}';
    }
}
