package uy.edu.utec.laboratoriotais.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacturaItemDTO {
    private String productoId;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;
}