# Decisiones de Diseño — BiblioTech

## 1. Separación en Capas

El sistema está dividido en cuatro capas con responsabilidades bien definidas:

| Capa | Responsabilidad |
|------|----------------|
| `model` | Representa los datos del dominio (Libro, Socio, Prestamo) |
| `repository` | Almacena y recupera objetos en memoria |
| `service` | Contiene toda la lógica de negocio |
| `Main` | Punto de entrada, menú CLI, sin lógica de negocio |

**Justificación:** cada capa solo conoce a la capa inmediatamente inferior. El `Main` llama a los servicios, los servicios llaman a los repositorios, y los repositorios trabajan con los modelos. Esto hace que un cambio en una capa no rompa las demás.

---

## 2. Uso de `record` para entidades inmutables

Se utilizó `record` para `Libro` y `Prestamo`.

```java
public record Libro(String isbn, String titulo, String autor, int anio, String categoria) {}
public record Prestamo(int id, String isbn, int socioId, LocalDate fechaPrestamo, LocalDate fechaDevolucionEsperada) {}
```

**Justificación:** los datos de un libro y de un préstamo no cambian una vez registrados. Un `record` en Java genera automáticamente constructor, getters, `equals` y `toString`, eliminando código repetitivo y dejando claro que estas entidades son inmutables por diseño.

`Socio` **no** es un record porque su campo `cantidadPrestamos` cambia con cada préstamo y devolución, por lo que necesita métodos mutadores (`incrementarPrestamos`, `decrementarPrestamos`).

---

## 3. Uso de `Optional` para evitar `null`

Todos los métodos de búsqueda en los repositorios retornan `Optional<T>` en lugar de `null`.

```java
public Optional<Libro> buscarPorIsbn(String isbn) { ... }
public Optional<Socio> buscarPorId(int id) { ... }
```

**Justificación:** retornar `null` cuando no se encuentra un objeto es una fuente común de errores (`NullPointerException`). `Optional` obliga explícitamente a quien llama a manejar el caso en que no haya resultado, haciendo el código más seguro y más expresivo.

---

## 4. Inyección de Dependencias por Constructor

Los servicios no crean sus propios repositorios, los reciben por constructor.

```java
public PrestamoService(LibroRepository libroRepository,
                       SocioRepository socioRepository,
                       PrestamoRepository prestamoRepository) {
    this.libroRepository = libroRepository;
    this.socioRepository = socioRepository;
    this.prestamoRepository = prestamoRepository;
}
```

**Justificación:** si el servicio creara sus dependencias internamente, estaría fuertemente acoplado a implementaciones concretas. Al recibirlas por constructor, quien construye el sistema (el `Main`) es el responsable de ensamblar las piezas. Esto respeta el principio de Inversión de Dependencias (SOLID - DIP) y facilita el mantenimiento.

---

## 5. Jerarquía de Excepciones Personalizadas

Se definió una clase base `BibliotecaException` de la cual heredan todas las excepciones del dominio.

```
BibliotecaException
├── LibroNoDisponibleException
├── SocioNoEncontradoException
└── LimitePrestamosException
```

**Justificación:** usar `RuntimeException` genérica no comunica qué salió mal. Con excepciones específicas, cada error del negocio tiene su propio tipo y mensaje claro. La jerarquía permite atrapar todos los errores de negocio con un único `catch (BibliotecaException e)` en el `Main`, sin suprimir errores inesperados del sistema.

---

## 6. Categorización de Socios mediante `enum`

El tipo de socio se representa con un `enum TipoSocio` con valores `ESTUDIANTE` y `DOCENTE`.

```java
public enum TipoSocio { ESTUDIANTE, DOCENTE }
```

El límite de préstamos se calcula dentro del propio `Socio`:

```java
public int getLimitePrestamos() {
    return tipo == TipoSocio.ESTUDIANTE ? 3 : 5;
}
```

**Justificación:** usar un `enum` evita errores de tipeo que ocurrirían si el tipo fuera un `String`. La lógica del límite vive dentro del modelo porque es una regla propia del socio, no del servicio.

---

## 7. Almacenamiento en Memoria

Los repositorios utilizan `ArrayList` como almacenamiento en memoria, sin base de datos ni archivos.

**Justificación:** el alcance del trabajo práctico no requiere persistencia. Usar listas en memoria simplifica la implementación y permite enfocarse en la arquitectura y la lógica de negocio. La estructura por capas permite reemplazar el almacenamiento en memoria por una base de datos en el futuro sin modificar los servicios.