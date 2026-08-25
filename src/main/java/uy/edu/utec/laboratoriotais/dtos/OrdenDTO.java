package uy.edu.utec.laboratoriotais.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uy.edu.utec.laboratoriotais.models.Estado;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenDTO {
    private String id;
    @Email
    private String email;
    @NotEmpty
    private String direccion;
    @NotBlank
    private String telefono;
    private Estado estado;
    private LocalDateTime fechaCreacion;
    @NotEmpty
    private List<OrdenItemDTO> items;

    public OrdenDTO(String email, String direccion, String telefono, Estado estado, LocalDateTime fechaCreacion, List<OrdenItemDTO> items) {
        this.email = email;
        this.direccion = direccion;
        this.telefono = telefono;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.items = items;
    }
}
