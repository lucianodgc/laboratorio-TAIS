package uy.edu.utec.laboratoriotais.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "productos")
public class Producto {
    private String id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private Integer stock;
    private List<String> imagenes;
}
