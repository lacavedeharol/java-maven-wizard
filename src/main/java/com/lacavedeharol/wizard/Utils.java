package com.lacavedeharol.wizard;

abstract class Utils {

    static String getLastWord(String input) {
        if (input == null || input.isEmpty())
            return "";

        String[] splitted = input.split("[-_.]");
        String[] camelParts = splitted[splitted.length - 1].split("(?=[A-Z])");
        return splitted.length > 1 ? splitted[splitted.length - 1].toLowerCase()
                : camelParts[camelParts.length - 1].toLowerCase();
    }

    static String setToUppercase(String input) {
        return (input != null && !input.isBlank())
                ? String.valueOf(input.charAt(0)).toUpperCase() + input.substring(1)
                : "";
    }

}
