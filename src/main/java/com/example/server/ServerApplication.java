package com.example.server;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerApplication {
	public static void main(String[] args) throws Exception {
		ServerSocket serverSocket = new ServerSocket(8080);
		System.out.println("Server listen in port 8080...");
		while(true) {
			Socket socket = serverSocket.accept();
			InputStream inputStream = socket.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String httpData = "NODATA";
			int lineCnt = 0;
			while(!"".equals(httpData)) {
				httpData = bufferedReader.readLine();
				lineCnt++;
				System.out.println(lineCnt + ": " + httpData);
			}
			socket.close();
		}
	}
}
