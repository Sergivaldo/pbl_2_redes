package br.uefs.api_rest;

public class CheckBattery extends Thread{
    @Override
    public void run() {
        int checkInterval = 2000;
        try {
            while (true) {
                Thread.sleep(checkInterval);
                if (ApiRestApplication.getBattery() <= 30) {
                    // Selecionar um posto usando a classe SelectGasStation
                }
            }
        }catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
