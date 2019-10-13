package com.kidscademy.atlas.http;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import js.log.Log;
import js.log.LogFactory;
import js.util.Files;

public class HttpConnector implements Connector, Runnable {
    private static final Log log = LogFactory.getLog(HttpServer.class);

    private static final int RECEIVE_TIMEOUT = 8000;


    private final ServletFactory servletFactory;
    private final int port;

    private volatile boolean running;

    HttpConnector(ServletFactory servletFactory, int port) {
        this.servletFactory = servletFactory;
        this.port = port;
    }

    public void start() {
        log.trace("start()");
        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

    public void stop() {
        log.trace("stop()");
        Socket socket = null;
        try {
            running = false;
            socket = new Socket(InetAddress.getLocalHost(), port);
        } catch (Exception e) {
            log.error(e);
        } finally {
            close(socket);
        }
    }


    @Override
    public void run() {
        log.debug("Start HTTP thread |%s|", Thread.currentThread());

        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            log.error(e);
            log.debug("Error creating sync socket. Stop sync thread |%s|.", Thread.currentThread());
            return;
        }
        log.debug("Listen on |%s:%d| for HTTP requests.", serverSocket.getInetAddress().getHostAddress(), port);

        ExecutorService executor = Executors.newCachedThreadPool();
        running = true;
        for (; ; ) {
            try {
                final Socket socket = serverSocket.accept();
                if (!running) {
                    log.debug("Got shutdown command. Break sync loop.");
                    socket.close();
                    break;
                }

                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        Request request = null;
                        Response response = null;
                        long start = 0;
                        try {
                            start = System.currentTimeMillis();
                            socket.setSoTimeout(RECEIVE_TIMEOUT);
                            request = new Request(socket);
                            response = new Response(socket);

                            request.parse();
                            if (request.isEof()) {
                                log.debug("Remote socket close. Close current connection and continue waiting for new ones.");
                                socket.close();
                                return;
                            }

                            Servlet servlet = servletFactory.createServlet(request.getType());
                            servlet.service(request, response);

                        } catch (FileNotFoundException e) {
                            log.warn("Resource |%s| not found.", request.getRequestURI());
                            response.setStatus(ResponseStatus.NO_FOUND);
                            exception(response, e);
                        } catch (Throwable t) {
                            log.dump("Error processing request.", t);
                            log.debug(request.dump());
                            response.setStatus(ResponseStatus.INTERNAL_SERVER_ERROR);
                            exception(response, t);
                        } finally {
                            close(socket);
                        }
                        log.debug("%s %s processed in %d msec.", request.getType(), request.getRequestURI(), System.currentTimeMillis() - start);
                    }
                });

            } catch (Throwable t) {
                log.dump("Fatal error on HTTP connector.", t);
            }
        }

        executor.shutdown();
        try {
            serverSocket.close();
        } catch (IOException e) {
            log.error(e);
        }
        log.debug("Exit HTTP connector thread.");
    }

    // ---------------------------------------------------------------------------------------------
    // UTILITY METHODS

    private static void close(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                log.error(e);
            }
        }
    }

    private static void exception(Response response, Throwable throwable) {
        if (response == null || response.isCommitted()) {
            return;
        }

        StringWriter writer = new StringWriter();
        throwable.printStackTrace(new PrintWriter(writer));
        String trace = writer.toString();

        response.setContentType(ContentType.TEXT_PLAIN);
        response.setContentLength(trace.getBytes().length);

        try {
            Files.copy(new ByteArrayInputStream(trace.getBytes()), response.getStream());
        } catch (IOException e) {
            log.error(e);
        }
    }
}
