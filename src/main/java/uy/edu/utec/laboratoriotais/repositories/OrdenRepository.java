package uy.edu.utec.laboratoriotais.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import uy.edu.utec.laboratoriotais.models.Estado;
import uy.edu.utec.laboratoriotais.models.Orden;

import java.util.List;

public interface OrdenRepository extends MongoRepository<Orden,String> {
    List<Orden> findByEstado(Estado estado);
}
