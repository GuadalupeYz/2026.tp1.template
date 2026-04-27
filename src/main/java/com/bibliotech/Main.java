package com.bibliotech;

import com.bibliotech.exception.BibliotecaException;
import com.bibliotech.model.TipoSocio;
import com.bibliotech.repository.*;
import com.bibliotech.service.*;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // Crear repositorios
        LibroRepository libroRepo = new LibroRepository();
        SocioRepository socioRepo = new SocioRepository();
        PrestamoRepository prestamoRepo = new PrestamoRepository();

        // Crear servicios inyectando los repositorios
        LibroService libroService = new LibroService(libroRepo);
        SocioService socioService = new SocioService(socioRepo);
        PrestamoService prestamoService = new PrestamoService(libroRepo, socioRepo, prestamoRepo);

        Scanner scanner = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("\n=== BiblioTech ===");
            System.out.println("1. Registrar libro");
            System.out.println("2. Listar libros");
            System.out.println("3. Buscar libro");
            System.out.println("4. Registrar socio");
            System.out.println("5. Listar socios");
            System.out.println("6. Realizar préstamo");
            System.out.println("7. Registrar devolución");
            System.out.println("0. Salir");
            System.out.print("Opción: ");
            opcion = scanner.nextInt();
            scanner.nextLine(); // limpiar buffer

            try {
                switch (opcion) {
                    case 1 -> {
                        System.out.print("ISBN: "); String isbn = scanner.nextLine();
                        System.out.print("Título: "); String titulo = scanner.nextLine();
                        System.out.print("Autor: "); String autor = scanner.nextLine();
                        System.out.print("Año: "); int anio = scanner.nextInt(); scanner.nextLine();
                        System.out.print("Categoría: "); String cat = scanner.nextLine();
                        libroService.registrarLibro(isbn, titulo, autor, anio, cat);
                    }
                    case 2 -> libroService.listarTodos().forEach(l ->
                            System.out.println("- [" + l.isbn() + "] " + l.titulo() + " - " + l.autor()));
                    case 3 -> {
                        System.out.print("Criterio de búsqueda: "); String criterio = scanner.nextLine();
                        libroService.buscar(criterio).forEach(l ->
                                System.out.println("- [" + l.isbn() + "] " + l.titulo()));
                    }
                    case 4 -> {
                        System.out.print("Nombre: "); String nombre = scanner.nextLine();
                        System.out.print("DNI: "); String dni = scanner.nextLine();
                        System.out.print("Email: "); String email = scanner.nextLine();
                        System.out.print("Tipo (1=Estudiante, 2=Docente): "); int tipo = scanner.nextInt(); scanner.nextLine();
                        socioService.registrarSocio(nombre, dni, email, tipo == 1 ? TipoSocio.ESTUDIANTE : TipoSocio.DOCENTE);
                    }
                    case 5 -> socioService.listarTodos().forEach(s ->
                            System.out.println("- [" + s.getId() + "] " + s.getNombre() + " (" + s.getTipo() + ")"));
                    case 6 -> {
                        System.out.print("ISBN del libro: "); String isbn = scanner.nextLine();
                        System.out.print("ID del socio: "); int id = scanner.nextInt(); scanner.nextLine();
                        prestamoService.realizarPrestamo(isbn, id);
                    }
                    case 7 -> {
                        System.out.print("ISBN del libro a devolver: "); String isbn = scanner.nextLine();
                        System.out.print("ID del socio: "); int id = scanner.nextInt(); scanner.nextLine();
                        prestamoService.registrarDevolucion(isbn, id);
                    }
                    case 0 -> System.out.println("¡Hasta luego!");
                    default -> System.out.println("Opción inválida.");
                }
            } catch (BibliotecaException e) {
                // Mostramos el mensaje del error personalizado, no el stack trace
                System.out.println("Error: " + e.getMessage());
            }

        } while (opcion != 0);

        scanner.close();
    }
}