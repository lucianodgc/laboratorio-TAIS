package uy.edu.utec.laboratoriotais;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LaboratorioTaisApplication {

    public static void main(String[] args) {
        SpringApplication.run(LaboratorioTaisApplication.class, args);
    }


}
