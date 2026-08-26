package uy.edu.utec.laboratoriotais.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({ "producto", "cantidad" })
public class OrdenItemDetalleDTO {
    private ProductoDTO producto;
    @Min(1)
    @NotNull
    private Integer cantidad;
}
