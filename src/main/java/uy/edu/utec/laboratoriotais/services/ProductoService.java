package uy.edu.utec.laboratoriotais.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import uy.edu.utec.laboratoriotais.dtos.ProductoDTO;
import uy.edu.utec.laboratoriotais.models.Producto;
import uy.edu.utec.laboratoriotais.repositories.ProductoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    @Transactional
    public ProductoDTO createProducto(ProductoDTO dto){
        if (productoRepository.existsByNombre(dto.getNombre())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Ya existe un producto registrado con ese nombre"
            );
        }
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setImagenes(dto.getImagenes());
        productoRepository.save(producto);
        return mapToDTO(producto);
    }

    public List<ProductoDTO> findProductos(){
        List<Producto> productos = productoRepository.findAll();
        return productos.stream().map(this::mapToDTO).toList();
    }

    public Optional<ProductoDTO> findProductoOptional(String id){
        return productoRepository.findById(id).map(this::mapToDTO);
    }

    public ProductoDTO findProducto(String id){
        return findProductoOptional(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Producto no encontrado con ID: " + id
                ));
    }

    public ProductoDTO updateProducto(String id, ProductoDTO dto){
        if (!productoRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Producto no encontrado con ID: " + id
            );
        }
        if (productoRepository.existsByNombreAndIdNot(dto.getNombre(), id)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Ya existe otro producto registrado con ese nombre"
            );
        }
        dto.setId(id);
        Producto guardado = productoRepository.save(mapToEntity(dto));
        return mapToDTO(guardado);
    }


    public ProductoDTO patchProducto(String id, ProductoDTO dto) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Producto no encontrado con ID: " + id
                ));

        if (dto.getNombre() != null) {
            if (dto.getNombre().isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "El nombre no puede estar vacío"
                );
            }
            if (productoRepository.existsByNombreAndIdNot(dto.getNombre(), id)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Ya existe un producto con ese nombre"
                );
            }
            producto.setNombre(dto.getNombre());
        }

        if (dto.getDescripcion() != null) {
            if (dto.getDescripcion().isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "La descripción no puede estar vacía"
                );
            }
            producto.setDescripcion(dto.getDescripcion());
        }

        if (dto.getPrecio() != null) {
            if (dto.getPrecio() <= 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "El precio debe ser mayor a 0"
                );
            }
            producto.setPrecio(dto.getPrecio());
        }

        if (dto.getStock() != null) {
            if (dto.getStock() < 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "El stock no puede ser negativo"
                );
            }
            producto.setStock(dto.getStock());
        }

        if (dto.getImagenes() != null) {
            producto.setImagenes(dto.getImagenes());
        }

        Producto guardado = productoRepository.save(producto);
        return mapToDTO(guardado);
    }

    public void deleteProducto(String id){
        productoRepository.deleteById(id);
    }

    private ProductoDTO mapToDTO(Producto producto) {
        List<String> imagenesUrl = (producto.getImagenes() == null)
                ? List.of()
                : new ArrayList<>(producto.getImagenes());
        return new ProductoDTO(producto.getId(), producto.getNombre(), producto.getDescripcion(), producto.getPrecio(), producto.getStock(), imagenesUrl);
    }

    private Producto mapToEntity(ProductoDTO dto) {
        Producto producto = new Producto();
        producto.setId(dto.getId());
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setImagenes(dto.getImagenes());
        return producto;
    }

}
