package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class CreateUserService {

    private final Connection connection;

    CreateUserService() throws SQLException {
        String url = "jdbc:sqlite:target/users_database.db";
        this.connection = DriverManager.getConnection(url);
        try (var stmt = connection.createStatement()) {
            stmt.execute("create table if not exists " +
                    "users (uuid varchar(200) primary key, email varchar)");
        }
    }

    public static void main(String[] args) throws SQLException, ExecutionException, InterruptedException {
        var userService = new CreateUserService();
        try (var service = new KafkaService<>(CreateUserService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER",
                userService::parse,
                Map.of())) {
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, Message<Order>> record) throws SQLException {
        System.out.println("------------------------------------------");
        System.out.println("Processing new order, checking for new user");
        System.out.println(record.value());

        var message = record.value();
        var order = message.getPayload();
        if (isNewUser(order.getEmail())) {
            insertNewUser(order.getEmail());
        }
    }

    private void insertNewUser(String email) throws SQLException {
        try (var insert = connection.prepareStatement("insert into users (uuid, email) values (?, ?)")) {
            String uuid = UUID.randomUUID().toString();
            insert.setString(1, uuid);
            insert.setString(1, email);
            insert.execute();

            System.out.println("New user inserted: " + uuid);
        }
    }

    private boolean isNewUser(String email) throws SQLException {
        try (var exists = connection.prepareStatement("select uuid from users where email = ? limit 1")) {
            exists.setString(1, email);
            var results = exists.executeQuery();

            return !results.next();
        }
    }
}
