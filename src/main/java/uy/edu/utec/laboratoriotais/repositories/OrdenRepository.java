package uy.edu.utec.laboratoriotais.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import uy.edu.utec.laboratoriotais.models.Orden;

public interface OrdenRepository extends MongoRepository<Orden,String> {
}
