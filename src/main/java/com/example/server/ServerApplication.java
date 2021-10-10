package com.example.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

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
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("<html>\r\n");
			stringBuilder.append("<head>\r\n");
			stringBuilder.append("\t<title>SimpleWebserver</title>\r\n");
			stringBuilder.append("</head>\r\n");
			stringBuilder.append("<body>\r\n");
			stringBuilder.append("\t<h1>OK</h1>\r\n");
			stringBuilder.append("</body>\r\n");
			stringBuilder.append("</html>\r\n");
			byte[] httpRespBodyBytes = stringBuilder.toString().getBytes(StandardCharsets.UTF_8);
			OutputStream outputStream = socket.getOutputStream();
			DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
			dataOutputStream.writeBytes("HTTP/1.0 200 OK\r\n");
			dataOutputStream.writeBytes("Content-Type: text/html\r\n");
			dataOutputStream.writeBytes("Server: SimpleJavaWebServer\r\n");
			dataOutputStream.writeBytes("Content-Length: " + httpRespBodyBytes.length + "\r\n");
			dataOutputStream.writeBytes("\r\n");
			dataOutputStream.write(httpRespBodyBytes, 0, httpRespBodyBytes.length);
			dataOutputStream.writeBytes("\r\n");
			dataOutputStream.flush();
			dataOutputStream.close();
			outputStream.close();
			bufferedReader.close();
			inputStream.close();
			socket.close();
		}
	}
}
