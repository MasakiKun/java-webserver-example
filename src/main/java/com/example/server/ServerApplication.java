package com.example.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

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
			String method = "", host = "", protocol = "";
			Map<String, String> queryStrings = new HashMap<>();
			Map<String, String> requestHeaders = new HashMap<>();
			while(!"".equals(httpData)) {
				lineCnt++;
				httpData = bufferedReader.readLine();
				if(lineCnt == 1) {
					StringTokenizer token = new StringTokenizer(httpData, " ");
					method = token.nextToken();
					host = token.nextToken();
					protocol = token.nextToken();

					if(host.indexOf('?') > -1) {
						String queryString = host.substring(host.indexOf('?') + 1);
						StringTokenizer queryStringToken = new StringTokenizer(queryString, "&");
						while(queryStringToken.hasMoreTokens()) {
							String key = queryStringToken.nextToken();
							String value = queryStringToken.nextToken();
							value = URLDecoder.decode(value);
							queryStrings.put(key, value);
						}
						System.out.println("this request has query strings...");
						System.out.println(queryStrings);
						host = host.substring(0, host.indexOf('?'));
						System.out.println("requested host replacement because has query string...");
						System.out.println("host: " + host);
					}
					if("/".equals(host)) {
						host = "/index.html";
					}
				} else {
					if(!httpData.equals("")) {
						String key = httpData.substring(0, httpData.indexOf(':'));
						String value = httpData.substring(httpData.indexOf(':') + 1, httpData.length()).trim();
						requestHeaders.put(key, value);
					}
				}
				System.out.println(lineCnt + ": " + httpData);
			}
			int httpRespCode = -1;
			String contentType = "";
			StringBuilder stringBuilder = new StringBuilder();
			switch(host) {
				case "/index.html":
					switch(method) {
						case "GET":
							httpRespCode = 200;
							contentType = "text/html";
							stringBuilder.append("<html>\r\n");
							stringBuilder.append("<head>\r\n");
							stringBuilder.append("\t<title>SimpleWebserver</title>\r\n");
							stringBuilder.append("</head>\r\n");
							stringBuilder.append("<body>\r\n");
							stringBuilder.append("\t<h1>OK</h1>\r\n");
							stringBuilder.append("</body>\r\n");
							stringBuilder.append("</html>\r\n");
							break;

						case "POST":
						default:
							httpRespCode = 405;
					}
					break;

				case "/getBirthdayZodiac":
					switch(method) {
						case "GET":
							String year = queryStrings.getOrDefault("year", "");
							String month = queryStrings.getOrDefault("month", "");
							String day = queryStrings.getOrDefault("day", "");
							if(
								("".equals(year) || year == null) &&
								("".equals(month) || month == null) &&
								("".equals(day) || day == null)
							) {
								httpRespCode = 400;
								break;
							}

							int nYear, nMonth, nDay;
							try {
								nYear = Integer.valueOf(year);
								nMonth = Integer.valueOf(month);
								nDay = Integer.valueOf(day);
							} catch(NumberFormatException nfe) {
								httpRespCode = 400;
								break;
							}

							contentType = "application/json";


							LocalDate birthday = LocalDate.of(nYear, nMonth, nDay);
							LocalDate startDate, endDate;

							// Aries (3.21 ~ 4.19)
							startDate = LocalDate.of(nYear, 3, 21);
							endDate = LocalDate.of(nYear, 4, 19);
							if(birthday.isAfter(startDate) && birthday.isBefore(endDate)) {
								stringBuilder.append("{");
								stringBuilder.append("	zodiac: {");
								stringBuilder.append("		name: Aries,");
								stringBuilder.append("		korean: 양자리");
								stringBuilder.append("	}");
								stringBuilder.append("}");
								break;
							}
							// Taurus (4.20 ~ 5.20)
							startDate = LocalDate.of(nYear, 4, 20);
							endDate = LocalDate.of(nYear, 5, 20);
							if(birthday.isAfter(startDate) && birthday.isBefore(endDate)) {
								stringBuilder.append("{");
								stringBuilder.append("	zodiac: {");
								stringBuilder.append("		name: Taurus,");
								stringBuilder.append("		korean: 황소자리");
								stringBuilder.append("	}");
								stringBuilder.append("}");
								break;
							}
							// Cancer (5.21
							// Leo (7.2. ~ 8.22)
							startDate = LocalDate.of(nYear, 7, 23);
							endDate = LocalDate.of(nYear, 8, 22);
							if(birthday.isAfter(startDate) && birthday.isBefore(endDate)) {
								stringBuilder.append("{");
								stringBuilder.append("	zodiac: {");
								stringBuilder.append("		name: Leo,");
								stringBuilder.append("		korean: 사자자리");
								stringBuilder.append("	}");
								stringBuilder.append("}");
								break;
							}
							// Virgo (8.23 ~ 9.23)
							startDate = LocalDate.of(nYear, 8, 23);
							endDate = LocalDate.of(nYear, 9, 23);
							if(birthday.isAfter(startDate) && birthday.isBefore(endDate)) {
								stringBuilder.append("{");
								stringBuilder.append("	zodiac: {");
								stringBuilder.append("		name: Leo,");
								stringBuilder.append("		korean: 사자자리");
								stringBuilder.append("	}");
								stringBuilder.append("}");
								break;
							}
							// Libra (9.24 ~ 10.22)
							startDate = LocalDate.of(nYear, 9, 22);
							endDate = LocalDate.of(nYear, 10, 22);
							if(birthday.isAfter(startDate) && birthday.isBefore(endDate)) {
								stringBuilder.append("{");
								stringBuilder.append("	zodiac: {");
								stringBuilder.append("		name: Virgo,");
								stringBuilder.append("		korean: 처녀자리");
								stringBuilder.append("	}");
								stringBuilder.append("}");
								break;
							}
							// Scorpius (10.23 ~ 11.22)
							startDate = LocalDate.of(nYear, 10, 23);
							endDate = LocalDate.of(nYear, 11, 22);
							if(birthday.isAfter(startDate) && birthday.isBefore(endDate)) {
								stringBuilder.append("{");
								stringBuilder.append("	zodiac: {");
								stringBuilder.append("		name: Scorpius,");
								stringBuilder.append("		korean: 전갈자리");
								stringBuilder.append("	}");
								stringBuilder.append("}");
								break;
							}

							// Sagittarius (10.23 ~ 11.22)
							startDate = LocalDate.of(nYear, 10, 23);
							endDate = LocalDate.of(nYear, 11, 22);
							if(birthday.isAfter(startDate) && birthday.isBefore(endDate)) {
								stringBuilder.append("{");
								stringBuilder.append("	zodiac: {");
								stringBuilder.append("		name: Sagittarius,");
								stringBuilder.append("		korean: 전갈자리");
								stringBuilder.append("	}");
								stringBuilder.append("}");
								break;
							}
							// TODO: 나머지도 인터넷에서 별자리 생년월일 표 찾아서 하나하나 넣어놓고 테스트를 해볼것
					}

				default:
					httpRespCode = 404;
			}
			OutputStream outputStream = socket.getOutputStream();
			DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
			switch(httpRespCode) {
				case 200:
					dataOutputStream.writeBytes("HTTP/1.0 200 OK\r\n");
					break;

				case 400:
					dataOutputStream.writeBytes("HTTP/1.0 503 Bad Request\r\n");
					stringBuilder.append("<h1>503 Bad Request</h1>");
					break;

				case 404:
					dataOutputStream.writeBytes("HTTP/1.0 404 Not Found\r\n");
					stringBuilder.append("<h1>404 Not Found</h1>");
					break;

				case 405:
					dataOutputStream.writeBytes("HTTP/1.0 405 Not Allowed\r\n");
					stringBuilder.append("<h1>405 Not Allowed</h1>");
					break;

				default:
					dataOutputStream.writeBytes("HTTP/1.0 501 Not Implemented\r\n");
					stringBuilder.append("<h1>501 Not Implemented</h1>");
			}
			byte[] httpRespBodyBytes = stringBuilder.toString().getBytes(StandardCharsets.UTF_8);
			dataOutputStream.writeBytes("Content-Type: " + contentType + "\r\n");
			dataOutputStream.writeBytes("Server: SimpleJavaWebServer\r\n");
			dataOutputStream.writeBytes("Content-Length: " + httpRespBodyBytes.length + "\r\n");
			dataOutputStream.writeBytes("\r\n");
			if(httpRespBodyBytes.length > 0) {
				dataOutputStream.write(httpRespBodyBytes, 0, httpRespBodyBytes.length);
			}
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
