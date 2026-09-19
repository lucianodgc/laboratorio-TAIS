package uy.edu.utec.laboratoriotais.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_ORDENES = "ordenes.procesamiento.queue";
    public static final String EXCHANGE_ORDENES = "ordenes.exchange";
    public static final String ROUTING_KEY_ORDENES = "ordenes.routingkey";

    @Bean
    public Queue queue() {
        return new Queue(QUEUE_ORDENES, true);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_ORDENES);
    }

    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY_ORDENES);
    }

    @Bean
    public JacksonJsonMessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}