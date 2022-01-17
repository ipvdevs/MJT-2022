package bg.sofia.uni.fmi.mjt.cocktail.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static final int SERVER_PORT = 62535;

    // Allowed clients to connect
    private static final int THREADS = 10;

    private static final ExecutorService executorService = Executors.newFixedThreadPool(THREADS);

    // Forbids instantiation of a Server
    private Server() {
    }

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {

            while (true) {
                Socket socket = serverSocket.accept();

                System.out.println("Accepted connection request from client " + socket.getPort());

                RequestHandler handler = new RequestHandler(socket);

                executorService.execute(handler);
            }

        } catch (IOException e) {
            throw new RuntimeException("A problem occurred with the server socket.", e);
        }

    }

}
