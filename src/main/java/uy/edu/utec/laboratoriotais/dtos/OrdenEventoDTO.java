package uy.edu.utec.laboratoriotais.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uy.edu.utec.laboratoriotais.models.Estado;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenEventoDTO {
    private String id;
    private Estado estado;
    private OffsetDateTime fechaCreacion;
}