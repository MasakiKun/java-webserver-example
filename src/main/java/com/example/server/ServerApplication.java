package com.example.server;

import java.net.ServerSocket;
import java.net.Socket;

public class ServerApplication {
	public static void main(String[] args) throws Exception {
		ServerSocket serverSocket = new ServerSocket(8080);
		Socket socket = serverSocket.accept();
		socket.close();
		serverSocket.close();
	}
}
