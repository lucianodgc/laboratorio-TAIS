package uy.edu.utec.laboratoriotais.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacturaDTO {
    private String id;
    private String ordenId;
    private List<FacturaItemDTO> items;
    private Double montoTotal;
}