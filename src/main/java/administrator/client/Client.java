package administrator.client;

import administrator.client.logic.CommandExecutor;
import utils.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Client {
    public static void main(String[] args) {
        Logger.info("Starting client...");
        System.out.println("Welcome to the Greenfield Administrator Client!");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        CommandExecutor commandExecutor = new CommandExecutor();

        while (true) {
            System.out.print("Enter a command (or 'help' for available commands, 'exit' to quit): ");
            try {
                String command = reader.readLine();
                if (command.equalsIgnoreCase("exit")) {
                    break;
                }

                String response = commandExecutor.executeCommand(command);
                System.out.println(response + "\n");
            } catch (IOException e) {
                Logger.logException(e);
                System.err.println("Error reading input: " + e.getMessage());
            }
        }

        System.out.println("Exiting the API Client...");
        Logger.info("Client stopped");
    }
}
