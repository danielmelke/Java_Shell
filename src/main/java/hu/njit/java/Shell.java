package hu.njit.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Shell class
 *
 * Getting user input run through the CommandHandler class as a command
 *
 * @author Daniel Melke
 */
public class Shell {

    /**
     * getting the current path as a Path variable
     */
    static Path path = Paths.get("").toAbsolutePath();

    /**
     * converting the path variable to a String named directory
     */
    static String directory = path.toString();

    /**
     * getting the initial working path as the default directory
     */
    static String defaultDir = path.toString();

    /**
     * welcomeMessage String: contains a formatted welcome message
     */
    static String welcomeMessage = "\u001B[32m" +
            "###### SHELL by Melke Daniel ######\n" +
            "######## version 1.0, 2021 ########\n" +
            "built-in commands: 'cd', 'ls', 'echo'\n" +
            "type in '-help' for a guide\n" +
            "type in 'exit' to stop the program" +
            "\u001B[0m";

    /**
     * prompt() function: displays the current directory as the prompt
     */
    static void prompt() {
        System.out.print(directory + "\\# ");
    }

    /**
     * userInput() function: listening to user input after displaying the prompt.
     * The listening continues until the program exit.
     * User input is passed into the CommandHandler.
     * Each command is ran on a new thread.
     * @throws IOException for catching possible user input errors
     */
    static void userInput() throws IOException {
        while (true) {
            prompt();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line;
            while ((line = reader.readLine()) != null){
                if (line.equals("exit")) {
                    System.exit(0);
                } else {
                    CommandHandler commandHandler = new CommandHandler(line);
                    Thread t = new Thread(commandHandler);
                    t.start();
                }
            }
        }
    }

    /**
     * main() function: displaying the welcome message, then listening to user input.
     * @param args String[] for user input
     * @throws IOException for catching possible user input errors
     */
    public static void main(String[] args) throws IOException {
        System.out.println(welcomeMessage);
        userInput();
    }
}
