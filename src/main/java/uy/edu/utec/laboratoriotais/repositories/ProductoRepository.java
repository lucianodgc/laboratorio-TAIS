package uy.edu.utec.laboratoriotais.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import uy.edu.utec.laboratoriotais.models.Producto;

public interface ProductoRepository extends MongoRepository<Producto, String>
{
    boolean existsByNombre(String nombre);

    boolean existsByNombreAndIdNot(String nombre, String id);
}
