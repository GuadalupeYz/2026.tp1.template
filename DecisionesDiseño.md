# Decisiones de Diseño — BiblioTech

## 1. Separación en Capas

El sistema está dividido en cuatro capas con responsabilidades bien definidas:

| Capa | Paquete | Responsabilidad |
|------|---------|----------------|
| Modelo | `model` | Representa los datos del dominio |
| Repositorio | `repository` | Almacena y recupera objetos en memoria |
| Servicio | `service` | Contiene toda la lógica de negocio y validaciones |
| Entrada | `Main.java` | Menú CLI, lectura de datos del usuario |

**Justificación:** cada capa solo conoce a la capa inmediatamente inferior. El `Main` llama a los servicios, los servicios llaman a los repositorios, y los repositorios trabajan con los modelos. Un cambio en una capa no obliga a modificar las demás. Por ejemplo, si en el futuro se reemplaza el almacenamiento en memoria por una base de datos, los servicios no necesitan modificarse.

---

## 2. Uso de `record` para Entidades Inmutables

Se utilizó `record` para `Libro` y `Prestamo`.

```java
public record Libro(String isbn, String titulo, String autor, int anio, String categoria) {}
public record Prestamo(int id, String isbn, int socioId, LocalDate fechaPrestamo, LocalDate fechaDevolucionEsperada) {}
```

**Justificación:** los datos de un libro y de un préstamo no cambian una vez registrados. Un `record` en Java genera automáticamente constructor, getters, `equals` y `toString`, eliminando código repetitivo y dejando claro que estas entidades son inmutables por diseño.

`Socio` **no** es un `record` porque su campo `cantidadPrestamos` cambia con cada préstamo y devolución, requiriendo los métodos `incrementarPrestamos()` y `decrementarPrestamos()`.

---

## 3. Uso de `Optional` para Evitar `null`

Todos los métodos de búsqueda en los repositorios retornan `Optional<T>` en lugar de `null`.

```java
public Optional<Libro> buscarPorIsbn(String isbn) { ... }
public Optional<Socio> buscarPorId(int id) { ... }
```

En los servicios, `Optional` se encadena directamente con `orElseThrow` para lanzar la excepción correspondiente si no se encuentra el recurso:

```java
libroRepository.buscarPorIsbn(isbn)
    .orElseThrow(() -> new BibliotecaException("Libro con ISBN " + isbn + " no encontrado."));
```

**Justificación:** retornar `null` cuando no se encuentra un objeto es fuente común de `NullPointerException`. `Optional` obliga explícitamente a quien llama a manejar el caso vacío, haciendo el código más seguro y más expresivo.

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

El ensamblado de todas las piezas ocurre únicamente en `Main`:

```java
LibroRepository libroRepo = new LibroRepository();
LibroService libroService = new LibroService(libroRepo);
PrestamoService prestamoService = new PrestamoService(libroRepo, socioRepo, prestamoRepo);
```

**Justificación:** si el servicio creara sus dependencias internamente, estaría fuertemente acoplado a implementaciones concretas y sería difícil de modificar. Al recibirlas por constructor, cada clase solo se ocupa de su propia lógica. Esto respeta el principio de Inversión de Dependencias (SOLID — DIP).

---

## 5. Jerarquía de Excepciones Personalizadas

Se definió una clase base `BibliotecaException` de la cual heredan todas las excepciones del sistema:

```
BibliotecaException
├── LibroNoDisponibleException   → libro ya prestado
├── SocioNoEncontradoException   → ID de socio inexistente
├── LimitePrestamosException     → socio alcanzó su cupo
└── ValidacionException          → datos de entrada inválidos
```

En el `Main`, un único bloque `catch` controla todos los errores de negocio:

```java
catch (BibliotecaException e) {
    System.out.println("Error: " + e.getMessage());
}
```

**Justificación:** usar `RuntimeException` genérica no comunica qué salió mal ni permite distinguir tipos de error. Con excepciones específicas, cada situación tiene su propio tipo y mensaje claro. `ValidacionException` se separó del resto para distinguir errores de datos de entrada de errores de lógica de negocio.

---

## 6. Validaciones Centralizadas en la Capa de Servicio

Todas las validaciones de datos de entrada se realizan en los servicios, no en el `Main`.

| Validación | Regla aplicada |
|------------|---------------|
| Campos vacíos | Se verifica con `isBlank()` antes de procesar |
| ISBN | Exactamente 13 dígitos numéricos (`\d{13}`) |
| DNI | Exactamente 8 dígitos numéricos (`\d{8}`) |
| Email | Formato `usuario@dominio.extensión` validado con expresión regular |
| Año del libro | Mayor a 0 y menor o igual al año actual |
| ISBN duplicado | Se consulta el repositorio antes de guardar |
| DNI duplicado | Se recorre la lista de socios antes de registrar |

**Justificación:** si las validaciones estuvieran en el `Main`, se mezclaría la lógica de negocio con la presentación. Al ubicarlas en el servicio, son reutilizables independientemente de cómo se acceda al sistema. Las validaciones lanzan `ValidacionException`, que se maneja igual que el resto de errores de negocio.

---

## 7. Categorización de Socios mediante `enum`

El tipo de socio se representa con un `enum TipoSocio` con valores `ESTUDIANTE` y `DOCENTE`.

```java
public enum TipoSocio { ESTUDIANTE, DOCENTE }
```

El límite de préstamos se calcula dentro del propio modelo `Socio`:

```java
public int getLimitePrestamos() {
    return tipo == TipoSocio.ESTUDIANTE ? 3 : 5;
}
```

**Justificación:** usar un `enum` evita errores de tipeo que ocurrirían con un `String`. La regla del límite vive dentro del modelo porque es una propiedad del socio, no del servicio. Si el límite de un tipo cambia, se modifica en un único lugar.

---

## 8. Almacenamiento en Memoria

Los repositorios utilizan `ArrayList` como almacenamiento, sin base de datos ni archivos.

**Justificación:** el alcance del trabajo no requiere persistencia entre ejecuciones. Esta decisión simplifica la implementación y permite enfocarse en la arquitectura y la lógica de negocio. Gracias a la separación por capas, reemplazar las listas por una base de datos en el futuro no requeriría modificar ningún servicio.

---

## 9. Manejo de Errores de Entrada en el Menú

El `Main` captura `InputMismatchException` por separado de `BibliotecaException`.

```java
catch (InputMismatchException e) {
    System.out.println("Error: ingresá un número válido.");
    scanner.nextLine(); // limpiar buffer para no quedar en loop infinito
}
catch (BibliotecaException e) {
    System.out.println("Error: " + e.getMessage());
}
```

**Justificación:** sin este manejo, escribir una letra donde se espera un número provoca que el programa entre en un loop infinito o termine abruptamente. Separar este catch del de negocio deja claro que son dos tipos de error distintos: uno de interacción con el usuario y otro de reglas del sistema.