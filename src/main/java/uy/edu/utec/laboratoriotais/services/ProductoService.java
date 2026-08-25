package uy.edu.utec.laboratoriotais.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import uy.edu.utec.laboratoriotais.dtos.ProductoDTO;
import uy.edu.utec.laboratoriotais.models.Producto;
import uy.edu.utec.laboratoriotais.repositories.ProductoRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

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
        return convertirADTO(producto);
    }

    public List<ProductoDTO> findProductos(){
        List<Producto> productos = productoRepository.findAll();
        return productos.stream().map(this::convertirADTO).toList();
    }

    public ProductoDTO findProducto(String id){
        return productoRepository.findById(id)
                .map(this::convertirADTO)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Producto no encontrado con ID: " + id
                ));
    }

    public ProductoDTO saveProducto(Producto producto){
        productoRepository.save(producto);
        return convertirADTO(producto);
    }

    public void deleteProducto(String id){
        productoRepository.deleteById(id);
    }

    private ProductoDTO convertirADTO(Producto p) {
        List<String> imagenesUrl = (p.getImagenes() == null)
                ? List.of()
                : new ArrayList<>(p.getImagenes());
        return new ProductoDTO(p.getId(), p.getNombre(), p.getDescripcion(), p.getPrecio(), p.getStock(), imagenesUrl);
    }

}
