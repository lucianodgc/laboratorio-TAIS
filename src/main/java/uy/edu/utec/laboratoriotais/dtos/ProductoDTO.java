package uy.edu.utec.laboratoriotais.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({ "id", "nombre", "descripcion", "precio", "stock", "imagenes" })
public class ProductoDTO {
    private String id;
    @NotBlank
    private String nombre;
    @NotBlank
    private String descripcion;
    @Positive
    @NotNull
    private Double precio;
    @Positive
    @NotNull
    private Integer stock;
    private List<String> imagenes;

    public ProductoDTO(String nombre, String descripcion, Double precio, Integer stock, List<String> imagenes) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.imagenes = imagenes;
    }
}
