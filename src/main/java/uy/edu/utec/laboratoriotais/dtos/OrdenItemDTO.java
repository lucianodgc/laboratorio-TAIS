package uy.edu.utec.laboratoriotais.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenItemDTO {
    @NotEmpty
    private String productoId;
    @Positive
    private Integer cantidad;
}
