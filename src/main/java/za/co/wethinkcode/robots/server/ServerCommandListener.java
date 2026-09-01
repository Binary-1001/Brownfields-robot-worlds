package za.co.wethinkcode.robots.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import za.co.wethinkcode.robots.command.Command;
import za.co.wethinkcode.robots.world.World;

import java.util.Scanner;

public class ServerCommandListener implements Runnable {

    private final World world;

    public ServerCommandListener(World world) {
        this.world = world;
    }

    @Override
    public void run() {

        if (System.console() == null) {
            System.out.println("Command listener disabled (headless mode).");
            return;
        }

        try (Scanner input = new Scanner(System.in)) {

            while (true) {
                if (!input.hasNextLine()) break;

                String line = input.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s+", 2); // split into command + argument
                String cmd = parts[0].toLowerCase();
                String argument = (parts.length > 1) ? parts[1] : null;

                switch (cmd) {

                    // -----------------------------
                    // SAVE <worldName>
                    // -----------------------------
                    case "save" -> {
                        if (argument == null) {
                            System.out.println("Usage: save <worldName>");
                            continue;
                        }

                        JsonObject request = new JsonObject();
                        request.addProperty("robot", "admin");
                        request.addProperty("command", "save");

                        JsonArray args = new JsonArray();
                        args.add(argument);
                        request.add("arguments", args);

                        try {
                            JsonObject response = Command.manage(world, request);
                            System.out.println("Server: " + response);
                        } catch (InterruptedException e) {
                            System.err.println("Save command failed: " + e.getMessage());
                        }
                    }

                    // -----------------------------
                    // RESTORE <worldName>
                    // -----------------------------
                    case "restore" -> {
                        if (argument == null) {
                            System.out.println("Usage: restore <worldName>");
                            continue;
                        }

                        System.out.println("⚠ Restoring will override the current world. Are you sure? (yes/no)");
                        String confirm = input.nextLine().trim().toLowerCase();
                        if (!confirm.equals("yes")) {
                            System.out.println("Restore cancelled.");
                            continue;
                        }

                        JsonObject request = new JsonObject();
                        request.addProperty("robot", "admin");
                        request.addProperty("command", "restore");

                        JsonArray args = new JsonArray();
                        args.add(argument);
                        request.add("arguments", args);

                        try {
                            JsonObject response = Command.manage(world, request);
                            System.out.println("Server: " + response);
                        } catch (InterruptedException e) {
                            System.err.println("Restore command failed: " + e.getMessage());
                        }
                    }

                    // -----------------------------
                    // BUILT-IN COMMANDS
                    // -----------------------------
                    case "robots", "dump", "quit" -> {
                        JsonObject request = new JsonObject();
                        request.addProperty("robot", "admin");
                        request.addProperty("command", cmd);
                        request.add("arguments", new JsonArray());

                        try {
                            JsonObject response = Command.manage(world, request);
                            System.out.println("Server: " + response);
                            if ("quit".equals(cmd)) System.exit(0);
                        } catch (InterruptedException e) {
                            System.err.println("Command failed: " + e.getMessage());
                        }
                    }

                    case "help" -> Server.help();

                    default -> System.out.println("Unknown command. Try: robots, dump, quit, save <name>, restore <name>");
                }
            }
        }
    }
}
