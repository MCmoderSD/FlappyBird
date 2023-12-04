package de.MCmoderSD.main;

import de.MCmoderSD.UI.Frame;

public class Main {

    // Constants
    public static String[] CONFIGURATIONS = {"lena", "911", "alpha", "lenabeta", "911beta"};
    public static boolean IS_RUNNING = true;

    public static void main(String[] args) {
        new Frame(new Config(args));
    }
}