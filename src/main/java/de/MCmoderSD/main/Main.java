package de.MCmoderSD.main;

import de.MCmoderSD.UI.Frame;
import de.MCmoderSD.utilities.Calculate;

public class Main {

    // Constants
    public static final String[] CONFIGURATIONS = {"lena", "911", "lenabeta", "911beta", "alpha"};

    public static void main(String[] args) {

        // Init Config
        Config.init(args);

        // Init Frame
        new Frame();
    }
}