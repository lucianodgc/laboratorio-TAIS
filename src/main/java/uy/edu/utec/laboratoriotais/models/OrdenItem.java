package uy.edu.utec.laboratoriotais.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenItem {
    private Integer cantidad;
    private String productoId;
    private Double precioUnitario;
}
