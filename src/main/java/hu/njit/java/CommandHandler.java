package hu.njit.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * CommandHandler class
 *
 * Processing user input as a command
 *
 * @author Daniel Melke
 */
public class CommandHandler implements Runnable{

    /**
     * local String variable for passed in user input
     */
    static String commandPass;

    /**
     * help String: contains a formatted help message
     */
    static String help = "Built in commands:\n" +
            "   cd <path>   |   '..' -> change directory to parent directory, <directory name> -> if it exists, change directory\n" +
            "   echo <String>    |   <String> writes the input on the screen\n" +
            "   ls  |   lists the current directory's elements\n" +
            "External commands which are not listed here are run through the Windows Command line";

    /**
     * determining if the initial directory or defaultDir has changed
     */
    static boolean dirChanged = false;

    /**
     * determining if the user input (command) is a built-in command or not
     * @param command String, from user input
     * @return true: if the command is found in the built-in command list | false: if the command is not found in the built-in list
     */
    static boolean isCommandIn(String command) {
        return command.startsWith("cd") || command.startsWith("echo") || command.startsWith("ls") || command.startsWith("-help");
    }

    /**
     * class constructor, takes 1 param as a command
     * @param command String, from user input
     */
    public CommandHandler(String command) {
        commandPass = command;
    }

    /**
     * Used for the external command execution's result as a reader, displays it on screen line by line.
     * @param process Process, containing the external command's execution details
     * @throws IOException for catching possible user input error
     */
    static void readFromProcess(Process process) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = "";
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }

    /**
     * Executes the passed in external command through a process creator and reader.
     * If it fails, it displays an error message.
     * Finishes with displaying the prompt.
     * @param command String, from user input
     * @throws IOException for catching possible user input error
     * @throws InterruptedException for catching possible process runtime errors
     */
    static void executeOut(String command) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec("cmd /c " + command);
        readFromProcess(process);
        if (process.waitFor() != 0) {
            System.out.println("Command did not execute properly");
        }
        process.destroy();
        Shell.prompt();
    }

    /**
     * Takes the passed in command param and inspects it to determine which built-in command should be executed.
     * Accepts piped commands within the limits (grep)
     * @param command String, from user input
     * @throws IOException for catching possible user input error
     */
    static void executeIn(String command) throws IOException{
        if (command.contains(" | ")){
            String firstCommand = command.split("\\|")[0];
            String secondCommand = command.split("\\|")[1];
            if (firstCommand.startsWith("echo")) {
                String line = command.split("\\|")[0].substring(5);
                System.out.println(line);
                if (secondCommand.startsWith(" grep") || secondCommand.startsWith("grep")){
                    System.out.println("---- grep ----");
                    String toMatch = secondCommand.substring(5).replaceFirst(" ", "");
                    if (line.contains(toMatch.toLowerCase()) || line.contains(toMatch.toUpperCase())){
                        System.out.println(line);
                    } else {
                        System.out.println("grep: no match found");
                    }
                } else {
                    System.out.println("Unknown second command");
                }
            } else if (firstCommand.replaceFirst(" ", "").equals("ls")) {
                File f = new File(Shell.directory);
                String[] dirNames = f.list();
                for (int i = 0; i < dirNames.length; ++i) {
                    System.out.print(dirNames[i] + "    ");
                    if (i > 0 && i%3 == 0) {
                        System.out.print("\n");
                    }
                }
                System.out.print("\n");
                if (secondCommand.startsWith(" grep") || secondCommand.startsWith("grep")){
                    System.out.println("---- grep ----");
                    String toMatch = secondCommand.substring(5).replaceFirst(" ", "");
                    int matches = 0;
                    for (String dirName : dirNames) {
                        if (dirName.contains(toMatch.toLowerCase()) || dirName.contains(toMatch.toUpperCase())) {
                            System.out.println(dirName);
                            matches++;
                        }
                    }
                    if (matches == 0) {
                        System.out.println("grep: no match found");
                    }
                } else {
                    System.out.println("Unknown second command");
                }
            } else if (firstCommand.startsWith("-help")) {
                System.out.println(help);
                System.out.print("\n");
                if (secondCommand.startsWith(" grep") || secondCommand.startsWith("grep")){
                    System.out.println("---- grep ----");
                    String toMatch = secondCommand.substring(5).replaceFirst(" ", "");
                    String[] lines = help.split("\\n");
                    int matches = 0;
                    for (String line : lines) {
                        if (line.contains(toMatch.toLowerCase()) || line.contains(toMatch.toUpperCase())) {
                            System.out.println(line);
                            matches++;
                        }
                    }
                    if (matches == 0) {
                        System.out.println("grep: no match found");
                    }
                } else {
                    System.out.println("Unknown second command");
                }
            }
        } else {
            if (command.startsWith("echo")) {
                String line = command.substring(5);
                System.out.println(line);
            } else if (command.startsWith("cd")){
                if (command.equals("cd ..")) {
                    if (Shell.directory.length() >= 5) {
                        int index = Shell.directory.lastIndexOf("\\");
                        Shell.directory = Shell.directory.substring(0, index);
                    }
                } else {
                    if (command.length() < 4) {
                        System.out.println("Command did not execute properly");
                    }
                    else {
                        File f = new File(Shell.directory + "\\" + command.substring(3));
                        if (f.isDirectory()) {
                            Shell.directory = Shell.directory + "\\" + command.substring(3);
                        } else {
                            System.out.println("Directory not found!");
                        }
                    }
                }
            } else if (command.equals("ls")) {
                File f = new File(Shell.directory);
                String[] dirNames = f.list();
                for (int i = 0; i < dirNames.length; ++i) {
                    System.out.print(dirNames[i] + "    ");
                    if (i > 0 && i%3 == 0) {
                        System.out.print("\n");
                    }
                }
                System.out.print("\n");
            } else if (command.equals("-help")) {
                System.out.println(help);
            }
        }
        Shell.prompt();
    }

    /**
     * Executing the passed in command in the passed in directory, if the default directory has changed.
     * Decides if the command is built-in or external first.
     * @param command String, from user input
     * @param dir String, directory, the current working path in String format
     * @throws IOException for catching possible user input error
     * @throws InterruptedException for catching possible process runtime errors
     */
    static void executeDirChanged(String command, String dir) throws IOException, InterruptedException {
        if (isCommandIn(command)) {
            executeIn(command);
        } else {
            Process process = Runtime.getRuntime().exec("cmd /c " + command, null, new File(dir));
            readFromProcess(process);
            if (process.waitFor() != 0) {
                System.out.println("Command did not execute properly");
            }
            process.destroy();
            Shell.prompt();
        }
    }

    /**
     * Implements the run() function from the Runnable class.
     * First determines if the default directory is changed, then executes the command with the correct function and correct parameters.
     * Catches IOException or InterruptedException for possible errors during execution.
     */
    @Override
    public void run() {
        if (!Shell.directory.equals(Shell.defaultDir)) {
            dirChanged = true;
        }
        try {
            if (dirChanged) {
                executeDirChanged(commandPass, Shell.directory);
            } else {
                if ((isCommandIn(commandPass))) {
                    executeIn(commandPass);
                } else {
                    executeOut(commandPass);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
