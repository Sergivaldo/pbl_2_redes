package br.uefs.utils;

public class Log implements Colors {

    public static void info(String message) {
        System.out.println(YELLOW + "INFO: " + message + DEFAULT);
        System.out.flush();
    }

    public static void error(String message) {

        System.out.println(RED + "ERROR: " + message + DEFAULT);
        System.out.flush();
    }

    public static void success(String message) {
        System.out.println(GREEN + "SUCCESS: " + message + DEFAULT);
    }

}
