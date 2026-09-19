package uy.edu.utec.laboratoriotais.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import uy.edu.utec.laboratoriotais.models.Factura;

public interface FacturaRepository extends MongoRepository<Factura,String> {
}
