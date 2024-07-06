package br.com.alura.ecommerce;

import br.com.alura.ecommerce.consumer.ConsumerService;
import br.com.alura.ecommerce.consumer.ServiceRunner;
import br.com.alura.ecommerce.database.LocalDatabase;
import br.com.alura.ecommerce.dispatcher.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class FraudDetectorService implements ConsumerService<Order> {

    private final LocalDatabase database;

    private FraudDetectorService() throws SQLException {
        this.database = new LocalDatabase("frauds_database");

        database.createIfNotExists("orders (uuid varchar(200) primary key, is_fraud boolean)");
    }

    public static void main(String[] args) {
        new ServiceRunner<>(FraudDetectorService::new).start(1);
    }

    private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();

    @Override
    public String getConsumerGroup() {
        return FraudDetectorService.class.getSimpleName();
    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_NEW_ORDER";
    }

    public void parse(ConsumerRecord<String, Message<Order>> record)
            throws ExecutionException, InterruptedException, SQLException {
        System.out.println("------------------------------------------");
        System.out.println("Processing new order, checking for fraud");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());
        var message = record.value();
        var order = message.getPayload();
        if (wasProcessed(order)) {
            System.out.println("Order already processed: " + order);
            return;
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // ignoring
            e.printStackTrace();
        }
        if (isFraud(order)) {
            database.update("insert into orders (uuid, is_fraud) values (?, true)", order.getOrderId());
            System.out.println("Order is fraudulent: " + order);
            orderDispatcher.send("ECOMMERCE_ORDER_REJECTED",
                    message.getId().continueWith(FraudDetectorService.class.getSimpleName()), order.getEmail(), order);
        } else {
            database.update("insert into orders (uuid, is_fraud) values (?, false)", order.getOrderId());
            System.out.println("Order approved: " + order);
            orderDispatcher.send("ECOMMERCE_ORDER_APPROVED",
                    message.getId().continueWith(FraudDetectorService.class.getSimpleName()), order.getEmail(), order);
        }
    }

    private boolean wasProcessed(Order order) throws SQLException {
        try (var rst = database.query("select uuid from orders where uuid = ? limit 1", order.getOrderId())) {
            return rst.next();
        }
    }

    private boolean isFraud(Order order) {
        return order.getAmount().compareTo(new BigDecimal("4500")) >= 0;
    }

}
