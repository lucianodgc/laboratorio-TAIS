package uy.edu.utec.laboratoriotais.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uy.edu.utec.laboratoriotais.dtos.OrdenDTO;
import uy.edu.utec.laboratoriotais.dtos.OrdenItemDTO;
import uy.edu.utec.laboratoriotais.models.Estado;
import uy.edu.utec.laboratoriotais.models.Orden;
import uy.edu.utec.laboratoriotais.models.OrdenItem;
import uy.edu.utec.laboratoriotais.repositories.OrdenRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdenService {

    private final OrdenRepository ordenRepository;

    public OrdenDTO createOrden(OrdenDTO dto){
        Orden orden = new Orden();
        orden.setEmail(dto.getEmail());
        orden.setDireccion(dto.getDireccion());
        orden.setTelefono(dto.getTelefono());
        orden.setEstado(Estado.CREADO);
        orden.setFechaCreacion(LocalDateTime.now());
        List<OrdenItem> items = dto.getItems().stream()
                .map(itemDTO -> new OrdenItem(itemDTO.getCantidad(), itemDTO.getProductoId()))
                .toList();

        orden.setItems(items);
        ordenRepository.save(orden);
        return  convertirADTO(orden);
    }

    public List<OrdenDTO> findOrdenes(){
        return ordenRepository.findAll().stream()
                .map(this::convertirADTO)
                .toList();
    }

    public OrdenDTO findOrden(String id){
        return ordenRepository.findById(id).map(this::convertirADTO).orElse(null);
    }

    public OrdenDTO saveOrden(Orden orden){
        ordenRepository.save(orden);
        return convertirADTO(orden);
    }

    public void deleteOrden(String id){
        ordenRepository.deleteById(id);
    }

    private OrdenDTO convertirADTO(Orden o) {
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
