package uy.edu.utec.laboratoriotais.models;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "facturas")
public class Factura {
    @Id
    private String id;
    private String ordenId;
    private List<FacturaItem> items;
    private Double montoTotal;
}