package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private ServerSocket server;
    private Socket socket;
    private final int PORT = 18189;
    private List<ClientHandler> clients;
    private AuthService authService;


    public List<ClientHandler> getClients() {
        return clients;
    }

    public Server() {
        clients = new CopyOnWriteArrayList<>();
        authService = new SimpleAuthService();

        try {
            server = new ServerSocket(PORT);
            System.out.println("Server started");

            while (true) {
                socket = server.accept();
                System.out.println("Client connected");
                new ClientHandler(this, socket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void broadcastMsg(ClientHandler clientHandler, String msg){
        String message = String.format("[ %s ]: %s", clientHandler.getNickname(), msg);
        for (ClientHandler c : clients) {
            c.sendMsg(message);
        }
    }

    public void privateMsg(ClientHandler sender, String receiver, String msg){
        String message = String.format("[ %s to %s ]: %s", sender.getNickname(), receiver, msg);
        for (ClientHandler c : clients) {
            if (c.getNickname().equals(receiver)) {
                c.sendMsg(message);
            }
            return;
        }
    }

    void subscribe(ClientHandler clientHandler){
        clients.add(clientHandler);
    }

    void unsubscribe(ClientHandler clientHandler){
        clients.remove(clientHandler);
    }

    public AuthService getAuthService() {
        return authService;
    }
}
