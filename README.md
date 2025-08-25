# Framework Web en Java para Servicios REST y Gestión de Archivos Estáticos

Este proyecto implementa un **framework web completo en Java** que evoluciona desde un servidor web básico hacia una plataforma robusta para el desarrollo de aplicaciones web con servicios REST backend. El framework permite a los desarrolladores definir servicios REST usando funciones lambda, gestionar parámetros de consulta y especificar la ubicación de archivos estáticos.

## Características Principales

###  **Framework de Servicios REST**

- **Método GET estático**: Define servicios REST usando expresiones lambda
- **Extracción de parámetros**: Acceso fácil a query parameters en las peticiones
- **Gestión de archivos estáticos**: Configuración flexible de directorios para recursos estáticos
- **Arquitectura distribuida**: Comprensión profunda del protocolo HTTP y aplicaciones distribuidas

###  **Funcionalidades Implementadas**

1. **Servicios REST con Lambda Functions**

2. **Configuración de Archivos Estáticos**

3. **Gestión de Usuarios**
   - Registro de nuevos usuarios
   - Verificación de usuarios existentes
   - Respuestas en formato JSON

---

## Requisitos Previos

- **Java 21** [Descargar Java](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
- **Apache Maven 3.8+** [Instalar Maven](https://maven.apache.org/install.html)
- **Git** [Instalar Git](https://git-scm.com/downloads)

---

## Instalación y Ejecución

### Pasos para ejecutar el proyecto:

1. **Clonar el repositorio:**

   ```bash
   git clone https://github.com/JAGBytes/arep-taller2.git
   cd arep-taller2
   ```

2. **Compilar el proyecto:**

   ```bash
   mvn clean package
   ```

3. **Ejecutar el servidor:**

   ```bash
   java -cp target/arep-taller2-1.0-SNAPSHOT.jar edu.escuelaing.arem.ASE.app.App
   ```

4. **Acceder a la aplicación:**
   ```
   http://localhost:35000
   ```

---

## Arquitectura del Framework

### **Componentes Principales:**

####  **HttpServer (Núcleo del Framework)**

- **Puerto**: 35000 por defecto
- **Procesamiento**: Secuencial (no concurrente)
- **Protocolos**: HTTP/1.1 completo
- **Seguridad**: Protección contra path traversal

####  **Métodos del Framework**

1. **`get(String path, Function<Request, Response> handler)`**

   - Define servicios REST GET con funciones lambda
   - Mapeo flexible de URLs a manejadores

2. **`post(String path, Function<Request, Response> handler)`**

   - Define servicios REST POST
   - Procesamiento de cuerpos JSON

3. **`staticfiles(String directory)`**
   - Configura directorio de archivos estáticos
   - Búsqueda en `target/classes + directory`

#### 📨 **Clases de Soporte**

- **Request**: Acceso a parámetros, headers, body JSON
- **Response**: Constructor de respuestas HTTP con Builder Pattern

---

## Ejemplos de Uso

### **Aplicación de Ejemplo Completa (App.java):**

```java
public class App {
    public static void main(String[] args) {
        // Configurar archivos estáticos en la raíz
        HttpServer.staticfiles("/");

        });

        // GET /pi -> Devuelve el valor de PI
        HttpServer.get("/pi", (req, res) -> {
            return new Response.Builder()
                .withContentType("text/plain")
                .withBody(String.valueOf(Math.PI))
                .build();
        });

        // GET /e -> Devuelve el valor de e (número de Euler)
        HttpServer.get("/e", (req, res) -> {
            return new Response.Builder()
                .withContentType("text/plain")
                .withBody(String.valueOf(Math.E))
                .build();
        });

        // POST /app/hello -> Registra un nuevo usuario
        HttpServer.post("/app/hello", (req, res) -> {
            if (!req.hasBody()) {
                return new Response.Builder()
                    .withStatus(400)
                    .withBody("{\"error\": \"Cuerpo de la petición requerido\"}")
                    .build();
            }

            if (req.isJson()) {
                String name = req.getJsonValue("name");
                if (name != null && !name.isEmpty()) {
                    HttpServer.addUser(name);
                    return new Response.Builder()
                        .withStatus(200)
                        .withBody("{\"message\": \"Hola " + name + " fuiste registrado exitosamente!\"}")
                        .build();
                } else {
                    return new Response.Builder()
                        .withStatus(400)
                        .withBody("{\"error\": \"Nombre de usuario requerido en el campo 'name'\"}")
                        .build();
                }
            } else {
                return new Response.Builder()
                    .withStatus(400)
                    .withBody("{\"error\": \"Content-Type debe ser application/json\"}")
                    .build();
            }
        });

        // Iniciar el servidor
        try {
            HttpServer.startServer(args);
        } catch (Exception e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }
}
```

---

## Pruebas y Validación

### **Ejecutar pruebas:**

```bash
mvn test
```

### **Pruebas Implementadas:**

#### ✅ **Servicios REST con Lambda Functions**

Para validar el funcionamiento del framework, se crearon **funciones lambda específicas de prueba** en `HttpServerTest.java`:

```java
// Lambda para servicio de saludo personalizado (pruebas)
HttpServer.get("/api/hello", (req, res) -> {
    String name = req.getQueryParam("name");
    String message = name != null ? "Hello " + name + "!" : "Hello World!";
    return new Response.Builder()
        .withBody("{\"message\": \"" + message + "\"}")
        .build();
});

// Lambda para obtener lista de usuarios (pruebas)
HttpServer.get("/api/users", (req, res) -> {
    StringBuilder json = new StringBuilder("{\"users\": [");
    Map<String, String> users = HttpServer.getUsers();
    // ... construcción dinámica de JSON ...
    return new Response.Builder().withBody(json.toString()).build();
});

// Lambda POST para crear usuarios con validación (pruebas)
HttpServer.post("/api/users", (req, res) -> {
    if (!req.hasBody() || !req.isJson()) {
        return new Response.Builder()
            .withStatus(400)
            .withBody("{\"error\": \"JSON body required\"}")
            .build();
    }
    // ... lógica de procesamiento y validación ...
});
```

**Servicios principales implementados en App.java:**

- `GET /app/hello?name=X` → Verifica si un usuario está registrado
- `GET /pi` → Devuelve el valor de PI (3.141592...)
- `GET /e` → Devuelve el número de Euler (2.718281...)
- `POST /app/hello` → Registra nuevos usuarios con JSON

**Pruebas ejecutadas:**

- `GET /api/hello?name=Juan` → Respuesta personalizada con lambda (pruebas)
- `GET /api/users` → Lista de usuarios generada dinámicamente (pruebas)
- `POST /api/users` → Creación de usuarios con validación JSON (pruebas)
- `GET /app/hello?name=Maria` → Verificación de usuarios registrados (App real)
- `GET /pi` → Constante matemática PI (App real)
- `GET /e` → Número de Euler (App real)
- `POST /app/hello` → Registro de nuevos usuarios (App real)

#### ✅ **Archivos Estáticos**

- `GET /` → `index.html` (con archivos de prueba creados automáticamente)
- `GET /style.css` → Archivos CSS con Content-Type correcto
- `GET /data.json` → Archivos JSON estáticos
- `GET /subdir/nested.txt` → Archivos en subdirectorios

#### ✅ **Seguridad**

- Protección contra path traversal (`../../../etc/passwd`)
- Validación de URL encoding (`%2E%2E%2F`)
- Bloqueo de acceso a directorios
- Validación de rutas canónicas

#### ✅ **Integración Completa**

- Pruebas de servidor completo con lambdas reales
- Verificación de respuestas HTTP con contenido generado por lambdas
- Tests de archivos estáticos creados dinámicamente

---

## Ejemplos de Peticiones

### **REST Services:**

```bash
# Verificar usuario registrado
curl "http://localhost:35000/app/hello?name=Pedro"
# Respuesta: {"message": "Hola Pedro"} o {"message": "No estás registrado en el sistema."}

# Constante PI
curl "http://localhost:35000/pi"
# Respuesta: 3.141592653589793

# Número de Euler
curl "http://localhost:35000/e"
# Respuesta: 2.718281828459045

# Registrar nuevo usuario
curl -X POST -H "Content-Type: application/json" \
     -d '{"name":"NuevoUsuario"}' \
     "http://localhost:35000/app/hello"
# Respuesta: {"message": "Hola NuevoUsuario fuiste registrado exitosamente!"}
```


---

## Estructura del Proyecto

```
arep-taller2/
│
├── src/main/
│   ├── java/edu/escuelaing/arem/ASE/app/
│   │   ├── App.java              # Aplicación principal con servicios reales
│   │   ├── HttpServer.java       # Núcleo del framework
│   │   ├── Request.java          # Clase para manejar peticiones HTTP
│   │   └── Response.java         # Clase para construir respuestas HTTP
│   │
│   └── resources/                # Archivos estáticos
│       ├── index.html
│       ├── styles.css
│       ├── scripts.js
│       └── servicio-web.jpg
│
├── src/test/java/edu/escuelaing/arem/ASE/app/
│   └── HttpServerTest.java       # Suite completa de pruebas (unitarias, integración y seguridad)
│
├── target/classes/               # Archivos compilados y recursos
├── pom.xml                       # Configuración Maven
├── README.md                     # Documentación del proyecto
└── .gitignore

```

---

## Características Técnicas

### **Protocolo HTTP Implementado:**

- Headers completos (Content-Type, Content-Length)
-  Status codes apropiados (200, 400, 404, 500)
-  Métodos GET y POST
-  JSON parsing

### **Seguridad:**

-  Path traversal protection
-  Input validation
-  Canonical path verification
-  Content-Type validation

---

## Autor

**Jorge Andrés Gamboa Sierra**


