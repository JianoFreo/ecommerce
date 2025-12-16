package src; // Package declaration — tells Java this class is in the 'src' folder

import src.ui.LoginFrame; // Import LoginFrame class from the UI package

public class Main { // Main class, program entry point
    public static void main(String[] args) { // Main method, JVM starts here
           LoginFrame frame = new LoginFrame(); // Explicit reference to avoid 'New instance ignored' warning
    }
}

/**
 * src/
│
├── dao/       → handles database operations (Data Access)
├── db/        → handles the database connection setup
├── model/     → defines data objects or entities (like Product, user, order, real world)
└── ui/        → handles user interface (the visual part, like forms & buttons)
 * 
 */ 