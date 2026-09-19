package uy.edu.utec.laboratoriotais.services;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import uy.edu.utec.laboratoriotais.config.RabbitMQConfig;
import uy.edu.utec.laboratoriotais.dtos.OrdenEventoDTO;
import uy.edu.utec.laboratoriotais.models.*;
import uy.edu.utec.laboratoriotais.repositories.FacturaRepository;
import uy.edu.utec.laboratoriotais.repositories.OrdenRepository;
import uy.edu.utec.laboratoriotais.repositories.ProductoRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdenConsumerService {

    private final OrdenRepository ordenRepository;
    private final ProductoRepository productoRepository;
    private final FacturaRepository facturaRepository;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_ORDENES)
    public void procesarOrden(OrdenEventoDTO evento) {

        Orden orden = ordenRepository.findById(evento.getId()).orElse(null);
        if (orden == null) {
            return;
        }

        if (!orden.getEstado().equals(Estado.CREATED)) {
            return;
        }

        boolean hayStockSuficiente = true;
        for (OrdenItem item : orden.getItems()) {
            Producto producto = productoRepository.findById(item.getProductoId()).orElse(null);
            if (producto == null || producto.getStock() < item.getCantidad()) {
                hayStockSuficiente = false;
                break;
            }
        }

        if (hayStockSuficiente) {
            double montoTotal = 0.0;
            List<FacturaItem> facturaItems = new ArrayList<>();

            for (OrdenItem item : orden.getItems()) {
                Producto producto = productoRepository.findById(item.getProductoId()).get();

                producto.setStock(producto.getStock() - item.getCantidad());
                productoRepository.save(producto);

                double subtotal = producto.getPrecio() * item.getCantidad();
                montoTotal = montoTotal + subtotal;

                FacturaItem fItem = new FacturaItem();
                fItem.setProductoId(producto.getId());
                fItem.setCantidad(item.getCantidad());
                fItem.setPrecioUnitario(producto.getPrecio());
                fItem.setSubtotal(subtotal);
                facturaItems.add(fItem);
            }

            orden.setEstado(Estado.READY_TO_DELIVERY);
            ordenRepository.save(orden);

            Factura factura = new Factura();
            factura.setOrdenId(orden.getId());
            factura.setFechaEmision(LocalDateTime.now());
            factura.setItems(facturaItems);
            factura.setMontoTotal(montoTotal);
            facturaRepository.save(factura);

        } else {
            orden.setEstado(Estado.NO_STOCK);
            ordenRepository.save(orden);
        }
    }
}