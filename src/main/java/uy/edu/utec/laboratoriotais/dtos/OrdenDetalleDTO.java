package uy.edu.utec.laboratoriotais.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.Valid;
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
@JsonPropertyOrder({ "id", "email", "direccion", "telefono", "estado", "fechaCreacion", "items" })
public class OrdenDetalleDTO {
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
    @Valid
    private List<OrdenItemDetalleDTO> items;

    public OrdenDetalleDTO(String email, String direccion, String telefono, Estado estado, LocalDateTime fechaCreacion, List<OrdenItemDetalleDTO> items) {
        this.email = email;
        this.direccion = direccion;
        this.telefono = telefono;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.items = items;
    }
}
