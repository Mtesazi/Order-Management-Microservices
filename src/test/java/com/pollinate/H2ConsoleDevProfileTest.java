package com.pollinate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class H2ConsoleDevProfileTest {

    @LocalServerPort
    private int port;

    @Test
    void shouldExposeH2ConsoleInDefaultProfile() throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();

        HttpRequest rootRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/h2-console"))
                .GET()
                .build();

        HttpRequest pageRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/h2-console/"))
                .GET()
                .build();

        HttpResponse<String> consoleRootResponse = client.send(rootRequest, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> consolePageResponse = client.send(pageRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, consoleRootResponse.statusCode());
        assertEquals(200, consolePageResponse.statusCode());
        assertTrue(consolePageResponse.body() != null && consolePageResponse.body().contains("H2 Console"));
    }
}

