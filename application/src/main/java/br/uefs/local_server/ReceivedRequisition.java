package br.uefs.local_server;

import br.uefs.dto.CarDTO;
import br.uefs.dto.GasStationDTO;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Map;

public class ReceivedRequisition extends Thread{
    private Socket socket;
    private static Map<String, GasStationDTO> gasStations;
    public ReceivedRequisition(Socket socket, Map<String, GasStationDTO> gasStations){
        this.socket = socket;
        this.gasStations = gasStations;
    }

    @Override
    public void run(){
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
            BufferedReader messageReader = new BufferedReader(inputStreamReader);
            String message = messageReader.readLine();
            System.out.println("Recebeu do cliente" + message);
            CarDTO car = new Gson().fromJson(messageReader, CarDTO.class);

            PrintStream outMessage = new PrintStream(socket.getOutputStream());
            outMessage.println(new Gson().toJson(LocalServer.selectBestGasStation(car, gasStations)));

        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }



}
