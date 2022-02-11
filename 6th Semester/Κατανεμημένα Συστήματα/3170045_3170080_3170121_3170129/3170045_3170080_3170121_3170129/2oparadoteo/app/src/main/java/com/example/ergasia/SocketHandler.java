package com.example.ergasia;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketHandler {
    private static Socket socket;
    private static ObjectInputStream input;
    private static ObjectOutputStream output;
    private static boolean offline=false;


    public static  Socket getSocket(){
        return socket;
    }

    public static void setSocket(Socket socket){
        SocketHandler.socket = socket;
    }

    public static ObjectInputStream getInput() {
        return input;
    }

    public static ObjectOutputStream getOutput() {
        return output;
    }

    public static void setInput(ObjectInputStream input) {
        SocketHandler.input = input;
    }

    public static void setOutput(ObjectOutputStream output) {
        SocketHandler.output = output;
    }

    public static boolean isOffline() {
        return offline;
    }

    public static void setOffline(){
        offline = true;
    }
    public static void setOnline(){
        offline=false;
    }
}