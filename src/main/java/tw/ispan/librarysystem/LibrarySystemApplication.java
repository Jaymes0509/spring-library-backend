package tw.ispan.librarysystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class LibrarySystemApplication {

    public static void main(String[] args) {
        System.out.println("======================== Library System Starting ========================");
        SpringApplication.run(LibrarySystemApplication.class, args);
        System.out.println("======================== Library System Starup ok ========================");
    }
}
