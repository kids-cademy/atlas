package com.kidscademy.atlas.poc;

import com.kidscademy.atlas.http.ContentType;
import com.kidscademy.atlas.http.HttpServer;
import com.kidscademy.atlas.http.Resource;
import com.kidscademy.atlas.http.Storage;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import js.util.Files;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@Ignore
public class HttpServerTest {
    @Mock
    private Storage storage;

    private HttpServer server;

    @Before
    public void beforeTest() {
        server = new HttpServer(storage, 80);
        server.start();
    }

    @After
    public void afterTest() {
        server.stop();
    }

    @Test
    public void getHtmlFile() throws IOException {
        Resource resource = Mockito.mock(Resource.class);
        when(resource.getContentType()).thenReturn(ContentType.TEXT_HTML);
        when(resource.getContentLength()).thenReturn(62L);
        when(resource.getInputStream()).thenReturn(getClass().getResourceAsStream("/index.htm"));

        when(storage.getResource("/index.htm")).thenReturn(resource);

        URL url = new URL("http://localhost:8080/test.htm");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        assertThat(connection.getResponseCode(), equalTo(200));
        ByteArrayOutputStream response = new ByteArrayOutputStream();
        Files.copy(connection.getInputStream(), response);
        assertThat(response.toString(), equalTo("<html>\r\n<head></head>\r\n<body>\r\n<h1>Test</h1>\r\n</body>\r\n</html>"));
    }

    @Test
    public void invokeHttpRmi() throws IOException {
        URL url = new URL("http://localhost:8080/com/kidscademy/atlas/poc/RmiService/getString.rmi");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");

        assertThat(connection.getResponseCode(), equalTo(200));
        ByteArrayOutputStream response = new ByteArrayOutputStream();
        Files.copy(connection.getInputStream(), response);
        assertThat(response.toString(), equalTo("\"test\""));

        connection.disconnect();
    }
}
