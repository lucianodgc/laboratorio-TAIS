package uy.edu.utec.laboratoriotais.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ordenes")
public class Orden {
    private String id;
    private String email;
    private String direccion;
    private String telefono;
    private Estado estado;
    private LocalDateTime fechaCreacion;
    private List<OrdenItem> items;
}
