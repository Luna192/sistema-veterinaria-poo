package com.veterinaria.persistencia;

import com.veterinaria.modelo.Cliente;
import com.veterinaria.modelo.Especie;
import com.veterinaria.modelo.Mascota;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class ClienteMascotaPersistenciaTest {

    @TempDir
    Path carpetaTemporal;

    @Test
    void guardaClienteYMascotaYLosRecuperaAlReabrir() {
        String url = "jdbc:h2:file:"
            + carpetaTemporal.resolve("veterinaria")
                .toAbsolutePath()
                .toString()
                .replace('\\', '/');

        Cliente ana = new Cliente(
            "Ana", "Perez", "123", "123456", ""
        );

        LocalDate nacimiento = LocalDate.of(2020, 3, 15);

        Mascota toby = ana.registrarMascota(
            "F-1", "Toby", Especie.PERRO, "",
            nacimiento
        );

        Long clienteId;

        // Primera apertura: crear la base temporal y guardar.
        try (
            EntityManagerFactory fabrica =
                Persistence.createEntityManagerFactory(
                    "veterinariaPU",
                    Map.of(
                        "jakarta.persistence.jdbc.url", url,
                        "hibernate.hbm2ddl.auto", "create",
                        "hibernate.show_sql", "false"
                    )
                );
            EntityManager em = fabrica.createEntityManager()
        ) {
            em.getTransaction().begin();

            try {
                em.persist(ana);
                em.getTransaction().commit();
            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw e;
            }

            clienteId = ana.getId();
            assertNotNull(clienteId);
        }

        // Segunda apertura: recuperar desde la misma base.
        try (
            EntityManagerFactory fabrica =
                Persistence.createEntityManagerFactory(
                    "veterinariaPU",
                    Map.of(
                        "jakarta.persistence.jdbc.url", url,
                        "hibernate.hbm2ddl.auto", "validate",
                        "hibernate.show_sql", "false"
                    )
                );
            EntityManager em = fabrica.createEntityManager()
        ) {
            Cliente recuperado = em.find(Cliente.class, clienteId);

            assertNotNull(recuperado);
            assertNotSame(ana, recuperado);
            assertEquals("Ana", recuperado.getNombre());
            assertEquals("Perez", recuperado.getApellido());
            assertEquals("123", recuperado.getDni());
            assertEquals(
                "123456", recuperado.getTelefono().orElseThrow()
            );
            assertTrue(recuperado.getEmail().isEmpty());

            List<Mascota> mascotas = recuperado.getMascotas();

            assertEquals(1, mascotas.size());

            Mascota mascotaRecuperada = mascotas.get(0);

            assertNotSame(toby, mascotaRecuperada);
            assertEquals("F-1", mascotaRecuperada.getNumeroFicha());
            assertEquals("Toby", mascotaRecuperada.getNombre());
            assertEquals(Especie.PERRO, mascotaRecuperada.getEspecie());
            assertEquals(
                nacimiento, mascotaRecuperada.getFechaNacimiento()
            );
            assertTrue(mascotaRecuperada.getRaza().isEmpty());
            assertTrue(mascotaRecuperada.estaActiva());
            assertSame(recuperado, mascotaRecuperada.getCliente());
        }
    }
}