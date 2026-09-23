package uy.edu.utec.laboratoriotais.services;

import lombok.AllArgsConstructor;
import org.springframework.amqp.AmqpException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Profile;
import uy.edu.utec.laboratoriotais.dtos.OrdenEventoDTO;
import uy.edu.utec.laboratoriotais.models.Estado;
import uy.edu.utec.laboratoriotais.models.Orden;
import uy.edu.utec.laboratoriotais.repositories.OrdenRepository;

import java.time.OffsetDateTime;

@Service
@Profile("api")
@AllArgsConstructor
public class RetryScheduler {

    private final OrdenRepository ordenRepository;
    private final OrdenPublisherService ordenPublisher;

    @Scheduled(fixedDelay = 10000)
    public void reintentarOrdenesPendientes() {
        var pendientes = ordenRepository.findByEstado(Estado.PENDING_PUBLISH);

        for (Orden orden : pendientes) {
            try {
                OrdenEventoDTO evento = new OrdenEventoDTO(
                        orden.getId(),
                        Estado.CREATED,
                        OffsetDateTime.now()
                );
                ordenPublisher.publicarOrdenCreada(evento);

                orden.setEstado(Estado.CREATED);
                ordenRepository.save(orden);
            } catch (AmqpException e) {
                break;
            }
        }
    }
}