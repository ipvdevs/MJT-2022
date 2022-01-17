package bg.sofia.uni.fmi.mjt.cocktail.server;

import bg.sofia.uni.fmi.mjt.cocktail.server.command.Command;
import bg.sofia.uni.fmi.mjt.cocktail.server.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.cocktail.server.command.CommandParser;
import bg.sofia.uni.fmi.mjt.cocktail.server.command.CommandType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class RequestHandler implements Runnable {

    private final Socket socket;

    public RequestHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        boolean autoFlush = true;

        try (var br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             var pw = new PrintWriter(socket.getOutputStream(), autoFlush)) {

            while (true) {
                Command command = CommandParser.of(br.readLine());

                pw.println(CommandExecutor.execute(command));

                if (command.type() == CommandType.DISCONNECT) {
                    break;
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("A problem occurred with the socket connection.", e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
