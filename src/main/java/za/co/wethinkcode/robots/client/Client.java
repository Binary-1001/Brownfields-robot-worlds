package za.co.wethinkcode.robots.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import com.google.gson.JsonObject;

import za.co.wethinkcode.robots.client.config.ConfigLoader;
import za.co.wethinkcode.robots.client.config.SimpleConfig;

public class Client {
    private String host;
    private int port;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private static SimpleConfig loadConfig() {
        try {
            return ConfigLoader.load("clientConfig.json");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void connect() {
        connect(true);
    }

    public void connect(boolean interactive) {
        int attempts = 0;
        int maxAttempts = 5;
        while (attempts < maxAttempts) {
            try {
                clientSocket = new Socket(host, port);
                System.out.println("Connected to Robot World server at " + host + ":" + port);

                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                if (interactive) {
                    Scanner console = new Scanner(System.in);
                    new Thread(() -> {
                        try {
                            String response;
                            while ((response = in.readLine()) != null) {
                                System.out.println(response);
                            }
                        } catch (IOException e) {
                            System.out.println("Disconnected from the server");
                        }
                    }).start();

                    String userInput;
                    while ((userInput = console.nextLine()) != null) {
                        boolean isValid = CommandHandler.validateUserInput(userInput);
                        if (!isValid) continue;

                        JsonObject request = CommandHandler.handle(userInput);
                        if (request == null) continue;

                        out.println(request);
                    }

                    console.close();
                }
                return; // success, exit retry loop

            } catch (IOException e) {
                attempts++;
                System.err.println("Connection attempt " + attempts + " failed: " + e.getMessage());
                try {
                    Thread.sleep(1000); // wait 1 second before retry
                } catch (InterruptedException ignored) {}
            }
        }
        System.err.println("❌ Could not connect to Robot World server after " + maxAttempts + " attempts.");
    }


    public boolean isConnected() {
        return clientSocket != null && clientSocket.isConnected() && !clientSocket.isClosed();
    }

    public String sendRequest(JsonObject request) throws IOException {
        if (!isConnected()) throw new IOException("Client not connected to server.");
        out.println(request.toString());
        return in.readLine(); // waits for the server’s JSON response
    }

    public void disconnect() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
                System.out.println("Connection closed.");
            }
        } catch (IOException e) {
            System.err.println("Error closing socket: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SimpleConfig config = loadConfig();
        if (config == null) {
            System.err.println("Failed to load configuration.");
            return;
        }

        Client client = new Client(config.host, config.port);
        client.connect();
    }


    public JsonObject requestWorldInfo() throws IOException {
        JsonObject request = new JsonObject();
        request.addProperty("command","world_info");
        String responseString = sendRequest(request);

        return com.google.gson.JsonParser.parseString(responseString).getAsJsonObject();
    }
}

