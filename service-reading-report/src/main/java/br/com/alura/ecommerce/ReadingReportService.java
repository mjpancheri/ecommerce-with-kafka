package br.com.alura.ecommerce;

import br.com.alura.ecommerce.consumer.ConsumerService;
import br.com.alura.ecommerce.consumer.KafkaService;
import br.com.alura.ecommerce.consumer.ServiceRunner;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ReadingReportService implements ConsumerService<User> {

    private static final Path SOURCE = Path.of("src", "main", "resources", "report.txt");

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        new ServiceRunner(ReadingReportService::new).start(5);
    }

    @Override
    public String getConsumerGroup() {
        return ReadingReportService.class.getSimpleName();
    }

    @Override
    public String getTopic() {
        return "USER_GENERATE_READING_REPORT";
    }

    public void parse(ConsumerRecord<String, Message<User>> record) throws IOException {
        System.out.println("------------------------------------------");
        System.out.println("Processing report for " + record.value());

        var message = record.value();
        var user = message.getPayload();
        var target = new File(user.getreportPath());
        IO.copyTo(SOURCE, target);
        IO.append(target, "Created for " + user.getUuid());

        System.out.println("Report created: " + target.getAbsolutePath());
    }
}
