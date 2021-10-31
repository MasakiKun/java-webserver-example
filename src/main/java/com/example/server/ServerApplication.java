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
import java.util.Set;
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
				httpData = bufferedReader.readLine();
				if(httpData == null) {
					System.out.println("null");
					// continue;
					break;
				}
				lineCnt++;
				if(lineCnt == 1) {
					StringTokenizer token = new StringTokenizer(httpData, " ");
					method = token.nextToken();
					host = token.nextToken();
					protocol = token.nextToken();

					if(host.indexOf('?') > -1) {
						String queryString = host.substring(host.indexOf('?') + 1);
						StringTokenizer queryStringToken = new StringTokenizer(queryString, "&");
						while(queryStringToken.hasMoreTokens()) {
							String qsToken = queryStringToken.nextToken();
							String key = qsToken.substring(0, qsToken.indexOf('='));
							String value = qsToken.substring(qsToken.indexOf('=') + 1);
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
						String value = httpData.substring(httpData.indexOf(':') + 1).trim();
						requestHeaders.put(key, value);
					}
				}
				System.out.println(lineCnt + ": " + httpData);
			}
			if(httpData == null) {
				socket.close();
				continue;
			}
			int httpRespCode = -1;
			String contentType = "";
			Map<String, String> additionalResponseHeader = new HashMap<>();
			StringBuilder stringBuilder = new StringBuilder();
			switch(host) {
				case "/index.html":
					switch(method) {
						case "GET":
							httpRespCode = 200;
							contentType = "text/html";
							stringBuilder.append("<!DOCTYPE html>\n" +
									"<head>\n" +
									"\t<title>생일 정보 조회</title>\n" +
									"\t<meta charset=\"utf-8\" />\n" +
									"</head>\n" +
									"<body>\n" +
									"\t<h1>생일 정보 얻기</h1>\n" +
									"\t<h2>생일 입력</h2>\n" +
									"\t<dl>\n" +
									"\t\t<dt>탄생 년도</dt>\n" +
									"\t\t<dd><input type=\"text\" id=\"txtBirthYear\" /></dd>\n" +
									"\t\t<dt>탄생월</dt>\n" +
									"\t\t<dd><input type=\"text\" id=\"txtBirthMonth\" /></dd>\n" +
									"\t\t<dt>탄생일</dt>\n" +
									"\t\t<dd><input type=\"text\" id=\"txtBirthDay\" /></dd>\n" +
									"\t</dl>\n" +
									"\t<p><button id=\"btnGetBirthData\">확인하기</button></p>\n" +
									"\t<span id=\"lblResult\"></span>\n" +
									"</body>\n" +
									"<script type=\"text/javascript\">\n" +
									"\tdocument.getElementById(\"btnGetBirthData\").addEventListener(\"click\", function() {\n" +
									"\t\tconst year = document.getElementById(\"txtBirthYear\").value;\n" +
									"\t\tconst month = document.getElementById(\"txtBirthMonth\").value;\n" +
									"\t\tconst day = document.getElementById(\"txtBirthDay\").value;\n" +
									"\t\tconst zodiacReqUrl = `http://localhost:8080/getBirthZodiac?year=${year}&month=${month}&day=${day}`;\n" +
									"\n" +
									"\t\tconst zodiacReq = new XMLHttpRequest();\n" +
									"\t\tlet zodiacData = null;\n" +
									"\t\tzodiacReq.onreadystatechange = function() {\n" +
									"\t\t\tif(zodiacReq.readyState === zodiacReq.DONE) {\n" +
									"\t\t\t\tif(zodiacReq.status === 200) {\n" +
									"\t\t\t\t\tzodiacData = JSON.parse(zodiacReq.responseText);\n" +
									"\t\t\t\t} else {\n" +
									"\t\t\t\t\tconsole.error(zodiac.responseText);\n" +
									"\t\t\t\t}\n" +
									"\n" +
									"\t\t\t\tconst flowerReqUrl = `http://localhost:8080/getBirthFlower?month=${month}`;\n" +
									"\n" +
									"\t\t\t\tconst flowerReq = new XMLHttpRequest();\n" +
									"\t\t\t\tlet flowerData = null;\n" +
									"\t\t\t\tflowerReq.onreadystatechange = function() {\n" +
									"\t\t\t\t\tif(flowerReq.readyState === flowerReq.DONE) {\n" +
									"\t\t\t\t\t\tif(flowerReq.status === 200) {\n" +
									"\t\t\t\t\t\t\tflowerData = JSON.parse(flowerReq.responseText);\n" +
									"\n" +
									"\t\t\t\t\t\t\tlet msg = `당신의 별자리는 ${zodiacData.zodiac.korean}(${zodiacData.zodiac.name})이며, 탄생화는 ${flowerData.flower.name}입니다.`;\n" +
									"\t\t\t\t\t\t\tlblResult.innerHTML = msg;\n" +
									"\t\t\t\t\t\t} else {\n" +
									"\t\t\t\t\t\t\tconsole.error(flowerData.responseText);\n" +
									"\t\t\t\t\t\t}\n" +
									"\t\t\t\t\t}\n" +
									"\t\t\t\t}\n" +
									"\t\t\t\tflowerReq.open(\"GET\", flowerReqUrl);\n" +
									"\t\t\t\tflowerReq.send();\n" +
									"\t\t\t}\n" +
									"\t\t}\n" +
									"\n" +
									"\t\tzodiacReq.open(\"GET\", zodiacReqUrl);\n" +
									"\t\tzodiacReq.send();\n" +
									"\t});\n" +
									"</script>\n" +
									"</html>");
							break;

						case "POST":
						default:
							httpRespCode = 405;
					}
					break;

				case "/getBirthZodiac":
					switch(method) {
						case "GET":
							String year = queryStrings.getOrDefault("year", "");
							String month = queryStrings.getOrDefault("month", "");
							String day = queryStrings.getOrDefault("day", "");
							if(
								("".equals(year) || year == null) ||
								("".equals(month) || month == null) ||
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
								stringBuilder.append("	\"zodiac\": {");
								stringBuilder.append("		\"name\": \"Aries\",");
								stringBuilder.append("		\"korean\": \"양자리\"");
								stringBuilder.append("	}");
								stringBuilder.append("}");
							}
							// Taurus (4.20 ~ 5.20)
							startDate = LocalDate.of(nYear, 4, 20);
							endDate = LocalDate.of(nYear, 5, 20);
							if(birthday.isAfter(startDate) && birthday.isBefore(endDate)) {
								stringBuilder.append("{");
								stringBuilder.append("	\"zodiac\": {");
								stringBuilder.append("		\"name\": \"Taurus\",");
								stringBuilder.append("		\"korean: \"황소자리\"");
								stringBuilder.append("	}");
								stringBuilder.append("}");
							}
							// Cancer (5.21 ~ 7.22)
							startDate = LocalDate.of(nYear, 5, 21);
							endDate = LocalDate.of(nYear, 7, 22);
							if(birthday.isAfter(startDate) && birthday.isBefore(endDate)) {
								stringBuilder.append("{");
								stringBuilder.append("	\"zodiac\": {");
								stringBuilder.append("		\"name\": \"Cancer\",");
								stringBuilder.append("		\"korean\": \"게자리\"");
								stringBuilder.append("	}");
								stringBuilder.append("}");
							}
							// Leo (7.23 ~ 8.22)
							startDate = LocalDate.of(nYear, 7, 23);
							endDate = LocalDate.of(nYear, 8, 22);
							if(birthday.isAfter(startDate) && birthday.isBefore(endDate)) {
								stringBuilder.append("{");
								stringBuilder.append("	\"zodiac\": {");
								stringBuilder.append("		\"name\": \"Leo\",");
								stringBuilder.append("		\"korean\": \"사자자리\"");
								stringBuilder.append("	}");
								stringBuilder.append("}");
							}
							// Virgo (8.23 ~ 9.23)
							startDate = LocalDate.of(nYear, 8, 23);
							endDate = LocalDate.of(nYear, 9, 23);
							if(birthday.isAfter(startDate) && birthday.isBefore(endDate)) {
								stringBuilder.append("{");
								stringBuilder.append("	\"zodiac\": {");
								stringBuilder.append("		\"name\": \"Leo\",");
								stringBuilder.append("		\"korean\": \"사자자리\"");
								stringBuilder.append("	}");
								stringBuilder.append("}");
							}
							// Libra (9.24 ~ 10.22)
							startDate = LocalDate.of(nYear, 9, 24);
							endDate = LocalDate.of(nYear, 10, 22);
							if(birthday.isAfter(startDate) && birthday.isBefore(endDate)) {
								stringBuilder.append("{");
								stringBuilder.append("	\"zodiac\": {");
								stringBuilder.append("		\"name\": \"Virgo\",");
								stringBuilder.append("		\"korean\": \"처녀자리\"");
								stringBuilder.append("	}");
								stringBuilder.append("}");
							}
							// Scorpius (10.23 ~ 11.22)
							startDate = LocalDate.of(nYear, 10, 23);
							endDate = LocalDate.of(nYear, 11, 22);
							if(birthday.isAfter(startDate) && birthday.isBefore(endDate)) {
								stringBuilder.append("{");
								stringBuilder.append("	\"zodiac\": {");
								stringBuilder.append("		\"name\": \"Scorpius\",");
								stringBuilder.append("		\"korean\": \"전갈자리\"");
								stringBuilder.append("	}");
								stringBuilder.append("}");
							}
							// Sagittarius (11.23 ~ 12.24)
							startDate = LocalDate.of(nYear, 11, 23);
							endDate = LocalDate.of(nYear, 12, 24);
							if(birthday.isAfter(startDate) && birthday.isBefore(endDate)) {
								stringBuilder.append("{");
								stringBuilder.append("	\"zodiac\": {");
								stringBuilder.append("		\"name\": \"Sagittarius\",");
								stringBuilder.append("		\"korean\": \"궁수자리\"");
								stringBuilder.append("	}");
								stringBuilder.append("}");
							}
							// Capricornus (12.25 ~ 1.19)
							startDate = LocalDate.of(nYear, 12, 25);
							endDate = LocalDate.of(nYear, 12, 31);
							LocalDate boundaryStartDate = LocalDate.of(nYear, 1, 1);
							LocalDate boundaryEndDate = LocalDate.of(nYear, 1, 19);
							if(
									(birthday.isAfter(startDate) && birthday.isBefore(endDate)) ||
									(birthday.isAfter(boundaryStartDate) && birthday.isBefore(boundaryEndDate))
							) {
								stringBuilder.append("{");
								stringBuilder.append("	\"zodiac\": {");
								stringBuilder.append("		\"name\": \"Capricornus\",");
								stringBuilder.append("		\"korean\": \"염소자리\"");
								stringBuilder.append("	}");
								stringBuilder.append("}");
							}
							// Aquarius (1.20 ~ 2.18)
							startDate = LocalDate.of(nYear, 1, 20);
							endDate = LocalDate.of(nYear, 2, 18);
							if(birthday.isAfter(startDate) && birthday.isBefore(endDate)) {
								stringBuilder.append("{");
								stringBuilder.append("	\"zodiac\": {");
								stringBuilder.append("		\"name\": \"Aquarius\",");
								stringBuilder.append("		\"korean\": \"물병자리\"");
								stringBuilder.append("	}");
								stringBuilder.append("}");
							}
							// Pisces (2.19 ~ 3.20)
							startDate = LocalDate.of(nYear, 2, 19);
							endDate = LocalDate.of(nYear, 3, 20);
							if(birthday.isAfter(startDate) && birthday.isBefore(endDate)) {
								stringBuilder.append("{");
								stringBuilder.append("	\"zodiac\": {");
								stringBuilder.append("		\"name\": \"Capricornus\",");
								stringBuilder.append("		\"korean\": \"물고기자리\"");
								stringBuilder.append("	}");
								stringBuilder.append("}");
							}

							if(stringBuilder.length() == 0) {
								httpRespCode = 500;
							} else {
								additionalResponseHeader.put("Access-Control-Allow-Origin", "*");
								httpRespCode = 200;
							}
							break;

						case "OPTIONS":
							httpRespCode = 200;
							additionalResponseHeader.put("Access-Control-Allow-Origin", "*");
							break;

						default:
							httpRespCode = 405;
					}
					break;

				case "/getBirthFlower":
					switch(method) {
						case "GET":
							String month = queryStrings.getOrDefault("month", "");
							if("".equals(month) || month == null) {
								httpRespCode = 400;
								break;
							}
							int birthMonth = -1;
							try {
								birthMonth = Integer.valueOf(month);
								if(birthMonth < 1 || birthMonth > 12) {
									httpRespCode = 503;
									break;
								}
							} catch(NumberFormatException nfe) {
								httpRespCode = 503;
								break;
							}
							stringBuilder.append("{");
							stringBuilder.append("	\"flower\": {");
							if(birthMonth == 1) {
								stringBuilder.append("	\"name\": \"수선화\",");
								stringBuilder.append("	\"meaning\": [");
								stringBuilder.append("		\"사랑\", \"매력\", \"어머니의 날\", \"스승의 날\"");
								stringBuilder.append("	]");
							} else if(birthMonth == 2) {
								stringBuilder.append("	\"name\": \"제비꽃\",");
								stringBuilder.append("	\"meaning\": [");
								stringBuilder.append("		\"신의\", \"지혜와 희망\"");
								stringBuilder.append("	]");
							} else if(birthMonth == 3) {
								stringBuilder.append("	\"name\": \"수선화\",");
								stringBuilder.append("	\"meaning\": [");
								stringBuilder.append("		\"봄\", \"부활\", \"가정의 행복\", \"존경\", \"존중\", \"우정\"");
								stringBuilder.append("	]");
							} else if(birthMonth == 4) {
								stringBuilder.append("	\"name\": \"스위트 피\",");
								stringBuilder.append("	\"meaning\": [");
								stringBuilder.append("		\"사랑\", \"젊음\", \"순결\"");
								stringBuilder.append("	]");
							} else if(birthMonth == 5) {
								stringBuilder.append("	\"name\": \"은방울 꽃\",");
								stringBuilder.append("	\"meaning\": [");
								stringBuilder.append("		\"사랑\", \"감사\", \"열정\", \"아름다음\", \"완벽함\"");
								stringBuilder.append("	]");
							} else if(birthMonth == 6) {
								stringBuilder.append("	\"name\": \"나리꽃\",");
								stringBuilder.append("	\"meaning\": [");
								stringBuilder.append("		\"사랑\", \"감사\", \"고마움\"");
								stringBuilder.append("	]");
							} else if(birthMonth == 7) {
								stringBuilder.append("	\"name\": \"제비고깔\",");
								stringBuilder.append("	\"meaning\": [");
								stringBuilder.append("		\"우직함\", \"자연적인 아름다움\"");
								stringBuilder.append("	]");
							} else if(birthMonth == 8) {
								stringBuilder.append("	\"name\": \"글라디올러스\",");
								stringBuilder.append("	\"meaning\": [");
								stringBuilder.append("		\"의지의 힘\", \"도덕적 고결함\", \"명예\", \"기억\"");
								stringBuilder.append("	]");
							} else if(birthMonth == 9) {
								stringBuilder.append("	\"name\": \"물망초\",");
								stringBuilder.append("	\"meaning\": [");
								stringBuilder.append("		\"인내\", \"기억\", \"정신적인 사랑\", \"애정\"");
								stringBuilder.append("	]");
							} else if(birthMonth == 10) {
								stringBuilder.append("	\"name\": \"금잔화\",");
								stringBuilder.append("	\"meaning\": [");
								stringBuilder.append("		\"맹렬함\", \"우아함\", \"헌신\"");
								stringBuilder.append("	]");
							} else if(birthMonth == 11) {
								stringBuilder.append("	\"name\": \"국화\",");
								stringBuilder.append("	\"meaning\": [");
								stringBuilder.append("		\"연민\", \"우정\", \"기쁨\"");
								stringBuilder.append("	]");
							} else if(birthMonth == 12) {
								stringBuilder.append("	\"name\": \"포인세티아\",");
								stringBuilder.append("	\"meaning\": [");
								stringBuilder.append("		\"용기\", \"성공\", \"당신은 특별한 존재\"");
								stringBuilder.append("	]");
							}
							stringBuilder.append("}}");
							contentType = "application/json";

							if(stringBuilder.length() == 0) {
								httpRespCode = 500;
							} else {
								additionalResponseHeader.put("Access-Control-Allow-Origin", "*");
								httpRespCode = 200;
							}
							break;

						case "OPTIONS":
							httpRespCode = 200;
							additionalResponseHeader.put("Access-Control-Allow-Origin", "*");
							break;

						default:
							httpRespCode = 405;

					}
					break;

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
					stringBuilder.delete(0, stringBuilder.length());
					stringBuilder.append("<h1>503 Bad Request</h1>");
					break;

				case 404:
					dataOutputStream.writeBytes("HTTP/1.0 404 Not Found\r\n");
					stringBuilder.delete(0, stringBuilder.length());
					stringBuilder.append("<h1>404 Not Found</h1>");
					break;

				case 405:
					dataOutputStream.writeBytes("HTTP/1.0 405 Not Allowed\r\n");
					stringBuilder.delete(0, stringBuilder.length());
					stringBuilder.append("<h1>405 Not Allowed</h1>");
					break;

				case 500:
					dataOutputStream.writeBytes("HTTP/1.0 500 Internal Server Error\r\n");
					stringBuilder.delete(0, stringBuilder.length());
					stringBuilder.append("<h1>500 Internal Server Error");
					break;

				default:
					dataOutputStream.writeBytes("HTTP/1.0 501 Not Implemented\r\n");
					stringBuilder.delete(0, stringBuilder.length());
					stringBuilder.append("<h1>501 Not Implemented</h1>");
			}
			byte[] httpRespBodyBytes = stringBuilder.toString().getBytes(StandardCharsets.UTF_8);
			dataOutputStream.writeBytes("Content-Type: " + contentType + "\r\n");
			dataOutputStream.writeBytes("Server: SimpleJavaWebServer\r\n");
			dataOutputStream.writeBytes("Content-Length: " + httpRespBodyBytes.length + "\r\n");
			if(!additionalResponseHeader.isEmpty()) {
				Set<String> headerKeys = additionalResponseHeader.keySet();
				for(String key : headerKeys) {
					dataOutputStream.writeBytes(key + ": " + additionalResponseHeader.get(key) + "\r\n");
				}
			}
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
