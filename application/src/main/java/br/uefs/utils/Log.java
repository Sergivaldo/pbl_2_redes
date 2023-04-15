package br.uefs.utils;

public class Log implements Colors{

    public static void info(String message){
        System.out.println(YELLOW + "INFO: "+ message + DEFAULT);
        System.out.flush();
    }

    public static void error(String message) {

        System.out.println(RED + "ERRO: " + message + DEFAULT);
        System.out.flush();
    }
}
