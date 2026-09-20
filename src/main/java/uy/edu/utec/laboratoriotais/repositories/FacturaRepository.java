package uy.edu.utec.laboratoriotais.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import uy.edu.utec.laboratoriotais.models.Factura;

import java.util.Optional;

public interface FacturaRepository extends MongoRepository<Factura, String> {
    Optional<Factura> findByOrdenId(String ordenId);
}