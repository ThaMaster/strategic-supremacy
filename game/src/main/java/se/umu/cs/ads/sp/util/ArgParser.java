package se.umu.cs.ads.sp.util;

import java.util.HashMap;

public class ArgParser {

    private final HashMap<String, String> argsMap = new HashMap<>();

    // Constructor to parse the arguments
    public ArgParser(String[] args) {
        parse(args);
    }

    // Main parsing function
    private void parse(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (arg.startsWith("-")) {
                // Handle flags with values (e.g. --timeout 30 or -t 30)
                if (i + 1 < args.length) {
                    String nextArg = args[i + 1];

                    // Check if nextArg is a valid number (could be negative)
                    if (isNumeric(nextArg)) {
                        argsMap.put(arg, nextArg);  // store the flag with its numeric value
                        i++;  // move to the next argument
                    } else {
                        // If it's not numeric, treat it as a flag without a value
                        argsMap.put(arg, "true");
                    }
                } else {
                    // Handle flags without values (e.g. -v or --verbose)
                    argsMap.put(arg, "true");
                }
            }
        }
    }

    // Helper method to check if a string is a valid number (including negative numbers)
    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);  // Try to parse the string as a number
            return true;  // It is a valid number
        } catch (NumberFormatException e) {
            return false;  // It is not a valid number
        }
    }


    // Method to get a flag's value (returns null if not found)
    public String getValue(String flag) {
        return argsMap.get(flag);
    }

    // Method to check if a flag is present
    public boolean hasFlag(String flag) {
        return argsMap.containsKey(flag);
    }
}
