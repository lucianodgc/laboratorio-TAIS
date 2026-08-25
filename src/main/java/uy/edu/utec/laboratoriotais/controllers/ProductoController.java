package uy.edu.utec.laboratoriotais.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uy.edu.utec.laboratoriotais.dtos.ProductoDTO;
import uy.edu.utec.laboratoriotais.services.ProductoService;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping()
    public ResponseEntity<List<ProductoDTO>> getAllProductos() {
        List<ProductoDTO> productos = productoService.findProductos();
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> getProducto(@PathVariable String id) {
        return ResponseEntity.ok(productoService.findProducto(id));
    }

    @PostMapping()
    public ResponseEntity<ProductoDTO> postProducto(@Valid @RequestBody ProductoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.createProducto(dto));
    }

}
