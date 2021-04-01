package hu.njit.java;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Shell {
    static Path path = Paths.get("").toAbsolutePath();
    static String directory = path.toString();
    static String defaultDir = path.toString();

    static String welcomeMessage = "\u001B[32m" +
            "###### SHELL by Melke Daniel ######\n" +
            "######## version 1.0, 2021 ########\n" +
            "built-in commands: 'cd', 'ls', 'echo'\n" +
            "type in '-help' for a guide\n" +
            "type in 'exit' to stop the program" +
            "\u001B[0m";

    static void prompt() {
        System.out.print(directory + "\\# ");
    }

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

    public static void main(String[] args) throws IOException {
        System.out.println(welcomeMessage);
        userInput();
    }
}
