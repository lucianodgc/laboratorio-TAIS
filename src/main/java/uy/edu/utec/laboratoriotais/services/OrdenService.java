package uy.edu.utec.laboratoriotais.services;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpException;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import uy.edu.utec.laboratoriotais.dtos.*;
import uy.edu.utec.laboratoriotais.models.Estado;
import uy.edu.utec.laboratoriotais.models.Orden;
import uy.edu.utec.laboratoriotais.models.OrdenItem;
import uy.edu.utec.laboratoriotais.repositories.OrdenRepository;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@Profile("api")
@RequiredArgsConstructor
public class OrdenService {

    private final OrdenRepository ordenRepository;
    private final OrdenPublisherService ordenPublisherService;
    private final ProductoService productoService;

    public OrdenDTO createOrden(OrdenDTO dto){

        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La orden debe contener al menos un ítem");
        }

        List<OrdenItem> items = dto.getItems().stream().map(itemDTO -> {
            ProductoDTO producto = productoService.findProductoOptional(itemDTO.getProductoId()).orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.CONFLICT, "Producto no encontrado con ID: " + itemDTO.getProductoId()));

            Double precio = (producto.getPrecio() != null) ? producto.getPrecio() : 0.0;
            return new OrdenItem(itemDTO.getCantidad(), itemDTO.getProductoId(), precio);
        }).toList();

        Orden orden = new Orden();
        orden.setEmail(dto.getEmail());
        orden.setDireccion(dto.getDireccion());
        orden.setTelefono(dto.getTelefono());
        orden.setEstado(Estado.CREATED);
        orden.setFechaCreacion(LocalDateTime.now());
        orden.setItems(items);

        orden = ordenRepository.save(orden);

        try {
            OrdenEventoDTO evento = new OrdenEventoDTO(orden.getId(), orden.getEstado(), OffsetDateTime.now());
            ordenPublisherService.publicarOrdenCreada(evento);
        } catch (AmqpException e) {
            orden.setEstado(Estado.PENDING_PUBLISH);
            ordenRepository.save(orden);
        }

        return mapToDTO(orden);
    }

    public List<OrdenDTO> findOrdenes(){
        return ordenRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public OrdenDTO findOrden(String id){
        return ordenRepository.findById(id).map(this::mapToDTO).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Orden no encontrada con ID: " + id
        ));
    }

    public OrdenDetalleDTO findOrdenDetalle(String id){
        Orden orden = ordenRepository.findById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Orden no encontrada con ID: " + id
        ));

        List<OrdenItemDetalleDTO> itemsDetalle = orden.getItems().stream().map(item -> {
            ProductoDTO productoDTO = productoService.findProducto(item.getProductoId());
            productoDTO.setPrecio(item.getPrecioUnitario());
            return new OrdenItemDetalleDTO(productoDTO, item.getCantidad());
        }).toList();

        return new OrdenDetalleDTO(
                orden.getId(),
                orden.getEmail(),
                orden.getDireccion(),
                orden.getTelefono(),
                orden.getEstado(),
                orden.getFechaCreacion(),
                itemsDetalle
        );
    }

    public void deleteOrden(String id){
        ordenRepository.deleteById(id);
    }

    private OrdenDTO mapToDTO(Orden o) {
        List <OrdenItemDTO> itemsDTO = o.getItems().stream().map(item -> new OrdenItemDTO(item.getProductoId(), item.getCantidad()))
                .toList();

        return new OrdenDTO(
                o.getId(),
                o.getEmail(),
                o.getDireccion(),
                o.getTelefono(),
                o.getEstado(),
                o.getFechaCreacion(),
                itemsDTO
        );
    }
}
