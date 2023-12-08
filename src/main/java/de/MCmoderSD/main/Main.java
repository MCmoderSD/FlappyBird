package de.MCmoderSD.main;

import de.MCmoderSD.UI.Frame;
import de.MCmoderSD.utilities.Calculate;

public class Main {

    // Constants
    public static String[] CONFIGURATIONS = {"lena", "911", "alpha", "lenabeta", "911beta"};
    public static boolean IS_RUNNING = true;

    public static void main(String[] args) {
        if (args.length > 1 && Calculate.doesFileExist(args[0]) && Calculate.doesFileExist(args[1])) new Frame(new Config(args)); // Custom
        else if (Calculate.doesFileExist("/languages/en.json")) new Frame(new Config(args)); // Default
        else new Frame(new Config(args, "https://raw.githubusercontent.com/MCmoderSD/FlappyBird/v3/src/main/resources")); // Asset streaming
    }
}