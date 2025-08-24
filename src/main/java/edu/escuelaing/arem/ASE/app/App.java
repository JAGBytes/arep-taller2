package edu.escuelaing.arem.ASE.app;

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) {

        HttpServer.get("/app/hello", (req, res) -> {
            String name = req.getQueryParam("name");

            if (name != null && !name.isEmpty()) {
                boolean userExists = HttpServer.getUsers().containsValue(name);
                String message = userExists
                        ? "Hola " + name
                        : "No estás registrado en el sistema.";

                return new Response.Builder() // Crear nueva instancia del Builder
                        .withStatus(200)
                        .withBody("{\"message\": \"" + message + "\"}")
                        .build();
            } else {
                return new Response.Builder() // Crear nueva instancia del Builder
                        .withStatus(400)
                        .withBody("{\"message\": \"Parámetro inválido en la petición.\"}")
                        .build();
            }
        });

        HttpServer.get("/pi", (req, res) -> {
            return new Response.Builder()
                    .withContentType("text/plain")
                    .withBody(String.valueOf(Math.PI))
                    .build();
        });

        HttpServer.get("/e", (req, res) -> {
            return new Response.Builder()
                    .withContentType("text/plain")
                    .withBody(String.valueOf(Math.E))
                    .build();
        });

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

        try {
            HttpServer.startServer(args);
        } catch (Exception e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }
}
