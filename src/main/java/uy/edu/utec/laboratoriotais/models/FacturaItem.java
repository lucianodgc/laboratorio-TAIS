package uy.edu.utec.laboratoriotais.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacturaItem {
    private String productoId;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;
}