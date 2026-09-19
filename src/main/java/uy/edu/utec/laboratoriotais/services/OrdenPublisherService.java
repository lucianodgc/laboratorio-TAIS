package uy.edu.utec.laboratoriotais.services;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import uy.edu.utec.laboratoriotais.config.RabbitMQConfig;
import uy.edu.utec.laboratoriotais.dtos.OrdenEventoDTO;

@Service
@RequiredArgsConstructor
public class OrdenPublisherService {

    private final RabbitTemplate rabbitTemplate;

    public void publicarOrdenCreada(OrdenEventoDTO evento) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_ORDENES,
                RabbitMQConfig.ROUTING_KEY_ORDENES,
                evento
        );
        System.out.println("-> Evento de orden publicado en RabbitMQ: " + evento.getId());
    }
}