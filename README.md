# E-commerce with Kafka

This project is based on Alura's Kafka training and implements an e-commerce system using Apache Kafka.

## Project Structure

- **common-kafka**: Common utilities and configurations for Kafka.
- **service-email**: Service for sending emails.
- **service-fraud-detector**: Service for detecting fraud.
- **service-log**: Service for logging events.
- **service-new-order**: Service for handling new orders.

## Prerequisites

- Java 11
- Apache Kafka
- Maven

## Running the Project

1. Clone the repository:
    ```sh
    git clone https://github.com/mjpancheri/ecommerce-with-kafka.git
    cd ecommerce-with-kafka
    ```

2. Start Kafka and Zookeeper.

3. Build the project using Maven:
    ```sh
    mvn clean install
    ```

4. Run each service:
    ```sh
    java -jar service-email/target/service-email.jar
    java -jar service-fraud-detector/target/service-fraud-detector.jar
    java -jar service-log/target/service-log.jar
    java -jar service-new-order/target/service-new-order.jar
    ```

## License

This project is licensed under the MIT License.
