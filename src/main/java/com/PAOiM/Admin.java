package com.PAOiM;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Admin {
    private int request_for_server;
    private String server_ip_address;
    private int server_port;
    private Socket socket;

    public static void main(String[] args){

    }

    public Admin() {
        request_for_server = 0;
        server_ip_address = "localhost";
        server_port = 9999;
    }

    public void connectToServer() {
        try {
            socket = new Socket(server_ip_address, server_port);
            System.out.println("A : successfully connected to server");
        }
        catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendRequestToServer(){
        try {
            OutputStreamWriter os = new OutputStreamWriter(socket.getOutputStream());
            PrintWriter out = new PrintWriter(os);
            String str = Integer.toString(request_for_server);
            out.println(request_for_server);
            os.flush();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String listenFromServer(){
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String result_from_server = br.readLine();
            return result_from_server;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getRequest_for_server() {
        return request_for_server;
    }
    public void setRequest_for_server(int request_for_server) {
        this.request_for_server = request_for_server;
    }

    public String getServer_ip_address() {
        return server_ip_address;
    }
    public void setServer_ip_address(String server_ip_address) {
        this.server_ip_address = server_ip_address;
    }

    public int getServer_port() {
        return server_port;
    }
    public void setServer_port(int server_port) {
        this.server_port = server_port;
    }

    public Socket getSocket() {
        return socket;
    }
    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
