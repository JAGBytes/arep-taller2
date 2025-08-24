

import edu.escuelaing.arem.ASE.app.HttpServer;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.net.*;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 *
 * @author jgamb
 */
public class HttpServerTest {

    public HttpServer server;
    public HashMap<String, String> users;

    @BeforeEach
    public void setUp() {
        server = new HttpServer();
        users = new HashMap<>();
        HttpServer.getUsers().clear();
        HttpServer.loadInitialData();
    }

    @Test
    void testLoadInitialData() {
        assertEquals(3, HttpServer.getUsers().size());
        assertTrue(HttpServer.getUsers().containsValue("Andres"));
    }

    @Test
    void testAddUsers() {

        assertEquals(3, HttpServer.getUsers().size());
        HttpServer.addUser("Jorge");
        HttpServer.addUser("Sergio");
        HttpServer.addUser("Laura");
        HttpServer.addUser("Pedro");

        assertEquals(7, HttpServer.getUsers().size());
        assertTrue(HttpServer.getUsers().containsValue("Andres"));
        assertTrue(HttpServer.getUsers().containsValue("Maria"));
        assertTrue(HttpServer.getUsers().containsValue("Carlos"));
        assertTrue(HttpServer.getUsers().containsValue("Jorge"));
        assertTrue(HttpServer.getUsers().containsValue("Sergio"));
        assertTrue(HttpServer.getUsers().containsValue("Laura"));
        assertTrue(HttpServer.getUsers().containsValue("Pedro"));

    }

    @Test
    public void testHelloServiceWithRegisteredUser() throws Exception {
        URI uri = new URI("/app/hello?name=Andres");
        byte[] response = HttpServer.helloService(uri);
        String responseStr = new String(response, StandardCharsets.UTF_8);

        System.out.println(responseStr);
        assertTrue(responseStr.contains("200 OK"));
        assertTrue(responseStr.contains("Hola Andres"));
    }

    @Test
    public void testHelloServiceWithUnregisteredUser() throws Exception {
        URI uri = new URI("/app/hello?name=Pedro");
        byte[] response = HttpServer.helloService(uri);
        String responseStr = new String(response, StandardCharsets.UTF_8);

        assertTrue(responseStr.contains("200 OK"));
        assertTrue(responseStr.contains("No estás registrado"));
    }

    @Test
    public void testHelloServiceWithInvalidParam() throws Exception {
        URI uri = new URI("/app/hello?invalidParam=true");
        byte[] response = HttpServer.helloService(uri);
        String responseStr = new String(response, StandardCharsets.UTF_8);
        System.out.println(responseStr);

        assertTrue(responseStr.contains("400 Bad Request"));
    }

    @Test
    public void testHandleGetRequestFileNotFound() throws Exception {
        URI uri = new URI("/nonexistent.html");
        byte[] response = HttpServer.handleGetRequest(uri);
        String responseStr = new String(response, StandardCharsets.UTF_8);

        assertTrue(responseStr.contains("404 Not Found"));
    }

    @Test
    void testHandlePostRequestAddNewUser() throws Exception {
        // Simular la petición HTTP POST con cabeceras + body
        assertFalse(HttpServer.getUsers().containsValue("Pepe"));
        String simulatedRequest = "POST /app/hello HTTP/1.1\r\n"
                + "Host: localhost\r\n"
                + "Content-Type: application/json\r\n"
                + "Content-Length: 17\r\n"
                + "\r\n"
                + "{\"name\":\"Pepe\"}";

        BufferedReader in = new BufferedReader(new StringReader(simulatedRequest));

        URI uri = new URI("/app/hello");

        byte[] responseBytes = HttpServer.handlePostRequest(uri, in);
        String response = new String(responseBytes);
        assertTrue(response.contains("200 OK"));
        assertTrue(response.contains("Hola Pepe fuiste registrado exitosamente!"));
        assertTrue(HttpServer.getUsers().containsValue("Pepe"));
    }

    @Test
    void testHandlePostRequestInvalidPath() throws Exception {
        String simulatedRequest = "POST /invalid HTTP/1.1\r\n"
                + "Host: localhost\r\n"
                + "Content-Type: application/json\r\n"
                + "Content-Length: 17\r\n"
                + "\r\n"
                + "{\"name\":\"Juan\"}";

        BufferedReader in = new BufferedReader(new StringReader(simulatedRequest));

        URI uri = new URI("/invalid");
        byte[] responseBytes = HttpServer.handlePostRequest(uri, in);
        String response = new String(responseBytes);

        assertTrue(response.contains("404 Not Found"));
    }

    @Test
    public void testHelloServiceWithoutNameParam() throws Exception {
        URI uri = new URI("/app/hello");
        byte[] response = HttpServer.helloService(uri);
        String responseStr = new String(response, StandardCharsets.UTF_8);

        assertTrue(responseStr.contains("400 Bad Request"));
    }

    @Test
    public void testHandleClientIntegration() throws Exception {
        String simulatedRequest = "GET /app/hello?name=Andres HTTP/1.1\r\n"
                + "Host: localhost\r\n"
                + "\r\n";

        ByteArrayInputStream input = new ByteArrayInputStream(simulatedRequest.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        Socket fakeSocket = new Socket() {
            @Override
            public InputStream getInputStream() {
                return input;
            }

            @Override
            public OutputStream getOutputStream() {
                return output;
            }
        };

        HttpServer.handleClient(fakeSocket);

        String responseStr = output.toString(StandardCharsets.UTF_8);
        assertTrue(responseStr.contains("200 OK"));
        assertTrue(responseStr.contains("Hola Andres"));
    }

}
