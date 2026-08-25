package uy.edu.utec.laboratoriotais;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uy.edu.utec.laboratoriotais.dtos.OrdenDTO;
import uy.edu.utec.laboratoriotais.dtos.OrdenItemDTO;
import uy.edu.utec.laboratoriotais.dtos.ProductoDTO;
import uy.edu.utec.laboratoriotais.repositories.OrdenRepository;
import uy.edu.utec.laboratoriotais.repositories.ProductoRepository;
import uy.edu.utec.laboratoriotais.services.OrdenService;
import uy.edu.utec.laboratoriotais.services.ProductoService;

import java.util.List;

@SpringBootTest
class LaboratorioTaisApplicationTests {

    @Autowired
    private ProductoService productoService;
    @Autowired
    private OrdenService ordenService;

    //@Autowired
    //private MongoTemplate mongoTemplate;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private OrdenRepository ordenRepository;
    @BeforeEach
    void setUp() {
        // Opción A: Borra la base de datos entera de prueba
        //mongoTemplate.getDb().drop();

        // Opción B: Borra solo los registros de la colección específica
        productoRepository.deleteAll();
        ordenRepository.deleteAll();
    }
    @Test
    void probarCrearProducto() {
        ProductoDTO productoDto = new ProductoDTO("Teclado Test", "Teclado mecánico", 80.0, 10, List.of("link.jpg"));

        productoDto = productoService.createProducto(productoDto);

        List<OrdenItemDTO> ordenItemDtos = List.of(new OrdenItemDTO(productoDto.getId(), 160));

        OrdenDTO ordenDto = new OrdenDTO("lucianodg.candido@gmail.com", "Playa Verde", "092098912", null, null, ordenItemDtos);

        ordenService.createOrden(ordenDto);

        List<ProductoDTO> productos = productoService.findProductos();
        for (ProductoDTO producto : productos) {
            System.out.println(producto.toString());
        }
        List<OrdenDTO> ordenes = ordenService.findOrdenes();
        for (OrdenDTO orden : ordenes) {
            System.out.println(orden.toString());
        }

    }

}
