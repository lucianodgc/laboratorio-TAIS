package uy.edu.utec.laboratoriotais.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uy.edu.utec.laboratoriotais.dtos.OrdenDTO;
import uy.edu.utec.laboratoriotais.dtos.OrdenDetalleDTO;
import uy.edu.utec.laboratoriotais.services.OrdenService;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes")
@RequiredArgsConstructor
public class OrdenController {

    private final OrdenService ordenService;

    @GetMapping()
    public ResponseEntity<List<OrdenDTO>> getAllOrdenes() {
        return ResponseEntity.ok(ordenService.findOrdenes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenDTO> getOrden(@PathVariable String id) {
        return ResponseEntity.ok(ordenService.findOrden(id));
    }

    @GetMapping("/{id}/detalle")
    public ResponseEntity<OrdenDetalleDTO> getOrdenDetalle(@PathVariable String id) {
        return ResponseEntity.ok(ordenService.findOrdenDetalle(id));
    }

    @PostMapping()
    public ResponseEntity<OrdenDTO> postOrden(@Valid @RequestBody OrdenDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ordenService.createOrden(dto));
    }
}
