package uy.edu.utec.laboratoriotais.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import uy.edu.utec.laboratoriotais.dtos.FacturaDTO;
import uy.edu.utec.laboratoriotais.dtos.FacturaItemDTO;
import uy.edu.utec.laboratoriotais.models.Factura;
import uy.edu.utec.laboratoriotais.models.FacturaItem;
import uy.edu.utec.laboratoriotais.repositories.FacturaRepository;
import uy.edu.utec.laboratoriotais.repositories.OrdenRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final OrdenRepository ordenRepository;

    public FacturaDTO findFacturaPorOrden(String ordenId) {
        if (!ordenRepository.existsById(ordenId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Orden no encontrada con ID: " + ordenId
            );
        }

        Factura factura = facturaRepository.findByOrdenId(ordenId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "La orden con ID " + ordenId + " todavía no tiene una factura generada"
                ));

        return mapToDTO(factura);
    }

    private FacturaDTO mapToDTO(Factura factura) {
        List<FacturaItemDTO> itemsDTO = factura.getItems().stream()
                .map(this::mapItemToDTO)
                .toList();

        return new FacturaDTO(
                factura.getId(),
                factura.getOrdenId(),
                itemsDTO,
                factura.getMontoTotal()
        );
    }

    private FacturaItemDTO mapItemToDTO(FacturaItem item) {
        double subtotal = item.getPrecioUnitario() * item.getCantidad();
        return new FacturaItemDTO(
                item.getProductoId(),
                item.getCantidad(),
                item.getPrecioUnitario(),
                subtotal
        );
    }
}