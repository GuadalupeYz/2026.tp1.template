package com.bibliotech;

import com.bibliotech.exception.BibliotecaException;
import com.bibliotech.model.TipoSocio;
import com.bibliotech.repository.*;
import com.bibliotech.service.*;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        LibroRepository libroRepo = new LibroRepository();
        SocioRepository socioRepo = new SocioRepository();
        PrestamoRepository prestamoRepo = new PrestamoRepository();

        LibroService libroService = new LibroService(libroRepo);
        SocioService socioService = new SocioService(socioRepo);
        PrestamoService prestamoService = new PrestamoService(libroRepo, socioRepo, prestamoRepo);

        Scanner scanner = new Scanner(System.in);
        int opcion = -1;

        do {
            System.out.println("\n=== BiblioTech ===");
            System.out.println("1. Registrar libro");
            System.out.println("2. Listar libros");
            System.out.println("3. Buscar libro");
            System.out.println("4. Registrar socio");
            System.out.println("5. Listar socios");
            System.out.println("6. Realizar préstamo");
            System.out.println("7. Registrar devolución");
            System.out.println("8. Ver préstamos activos");
            System.out.println("0. Salir");
            System.out.print("Opción: ");

            try {
                opcion = scanner.nextInt();
                scanner.nextLine(); // limpiar buffer

                switch (opcion) {
                    case 1 -> {
                        System.out.print("ISBN (13 dígitos): ");
                        String isbn = scanner.nextLine();
                        System.out.print("Título: ");
                        String titulo = scanner.nextLine();
                        System.out.print("Autor: ");
                        String autor = scanner.nextLine();
                        System.out.print("Año: ");
                        int anio = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Categoría: ");
                        String cat = scanner.nextLine();
                        libroService.registrarLibro(isbn, titulo, autor, anio, cat);
                    }
                    case 2 -> {
                        var libros = libroService.listarTodos();
                        if (libros.isEmpty()) {
                            System.out.println("No hay libros registrados.");
                        } else {
                            libros.forEach(l -> System.out.println(
                                    "- [" + l.isbn() + "] " + l.titulo() + " — " + l.autor() + " (" + l.anio() + ")"));
                        }
                    }
                    case 3 -> {
                        System.out.print("Criterio (título, autor o categoría): ");
                        String criterio = scanner.nextLine();
                        var resultados = libroService.buscar(criterio);
                        if (resultados.isEmpty()) {
                            System.out.println("No se encontraron libros.");
                        } else {
                            resultados.forEach(l -> System.out.println(
                                    "- [" + l.isbn() + "] " + l.titulo() + " — " + l.autor()));
                        }
                    }
                    case 4 -> {
                        System.out.print("Nombre: ");
                        String nombre = scanner.nextLine();
                        System.out.print("DNI (8 dígitos): ");
                        String dni = scanner.nextLine();
                        System.out.print("Email: ");
                        String email = scanner.nextLine();
                        System.out.print("Tipo (1=Estudiante, 2=Docente): ");
                        int tipo = scanner.nextInt();
                        scanner.nextLine();
                        TipoSocio tipoSocio = (tipo == 1) ? TipoSocio.ESTUDIANTE : TipoSocio.DOCENTE;
                        socioService.registrarSocio(nombre, dni, email, tipoSocio);
                    }
                    case 5 -> {
                        var socios = socioService.listarTodos();
                        if (socios.isEmpty()) {
                            System.out.println("No hay socios registrados.");
                        } else {
                            socios.forEach(s -> System.out.println(
                                    "- [ID: " + s.getId() + "] " + s.getNombre() +
                                            " | DNI: " + s.getDni() +
                                            " | " + s.getTipo() +
                                            " | Préstamos: " + s.getCantidadPrestamos() + "/" + s.getLimitePrestamos()));
                        }
                    }
                    case 6 -> {
                        System.out.print("ISBN del libro: ");
                        String isbn = scanner.nextLine();
                        System.out.print("ID del socio: ");
                        int id = scanner.nextInt();
                        scanner.nextLine();
                        prestamoService.realizarPrestamo(isbn, id);
                    }
                    case 7 -> {
                        System.out.print("ISBN del libro a devolver: ");
                        String isbn = scanner.nextLine();
                        System.out.print("ID del socio: ");
                        int id = scanner.nextInt();
                        scanner.nextLine();
                        prestamoService.registrarDevolucion(isbn, id);
                    }
                    case 8 -> {
                        var activos = prestamoService.listarPrestamosActivos();
                        if (activos.isEmpty()) {
                            System.out.println("No hay préstamos activos.");
                        } else {
                            System.out.println("Préstamos activos:");
                            activos.forEach(p -> System.out.println(
                                    "- ISBN: " + p.isbn() +
                                            " | Socio ID: " + p.socioId() +
                                            " | Vence: " + p.fechaDevolucionEsperada()));
                        }
                    }
                    case 0 -> System.out.println("¡Hasta luego!");
                    default -> System.out.println("Opción inválida. Ingresá un número del 0 al 8.");
                }

            } catch (InputMismatchException e) {
                // El usuario escribió una letra donde iba un número
                System.out.println("Error: ingresá un número válido.");
                scanner.nextLine(); // limpiar el buffer para no quedar en loop
                opcion = -1;
            } catch (BibliotecaException e) {
                System.out.println("Error: " + e.getMessage());
            }

        } while (opcion != 0);

        scanner.close();
    }
}
