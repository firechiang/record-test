package com.firecode.record_test.websocket;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.WebSocketMessage;

import com.alibaba.fastjson.JSONObject;
import com.firecode.record_test.util.ZipUtil;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

public class NvWebsocketClient {
	
	private static final Pattern PING_PATTERN = Pattern.compile("\\{(\"ping\"|\"pong\")[:].*\\}");
	private static final String HEARTBEAT_NAME_PING = "ping";
	private static final String HEARTBEAT_NAME_PONG = "pong";
	
	public static void main(String[] args) throws IOException, URISyntaxException, WebSocketException {
		//创建一个WebSocketFactory实例。
		WebSocketFactory factory =  new WebSocketFactory();
		SocketFactory socketFactory = SSLSocketFactory.getDefault();
		factory.setSSLSocketFactory((SSLSocketFactory)socketFactory);
		
		factory.setConnectionTimeout(5000);
		WebSocket ws = factory.createSocket(new URI("wss://api.huobiasia.vip/ws"));
		ws.setAutoFlush(true);
		ws.setPongSenderName("pong");
		ws.addListener(new WebSocketAdapter(){

			@Override
			public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
				System.out.println("连接成功");
			}

			@Override
			public void onTextMessage(WebSocket websocket, String text) throws Exception {
				System.out.println("onTextMessage 接收到服务器的消息："+text);
			}

			@Override
			public void onPingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
				System.out.println("获取到服务器端发来的心跳："+frame);
				//WebSocketFrame createPongFrame = WebSocketFrame.createPongFrame();
				//websocket.sendFrame(createPongFrame);
			}

			@Override
			public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception {
				String data = new String(ZipUtil.decompress(binary), "UTF-8");
				System.out.println("onBinaryMessage 接收到服务器的消息："+data);
				// 是否心跳数据
				if(PING_PATTERN.matcher(data).matches()){
					JSONObject json = JSONObject.parseObject(data);
					Long heartbeatValue = json.getLong(HEARTBEAT_NAME_PING);
					WebSocketMessage<ByteBuffer> heartbeatMessage = null;
					if(heartbeatValue == null){
						heartbeatValue = json.getLong(HEARTBEAT_NAME_PONG);
						String heartbeatName = HEARTBEAT_NAME_PING;
						Map<String,Long> map = new HashMap<>(3);
						map.put(heartbeatName, heartbeatValue);
						String heartbeat = JSONObject.toJSONString(map);
						heartbeatMessage = new PingMessage(ByteBuffer.wrap(heartbeat.getBytes(StandardCharsets.UTF_8)));
					}else{
						String heartbeatName = HEARTBEAT_NAME_PONG;
						Map<String,Long> map = new HashMap<>(3);
						map.put(heartbeatName, heartbeatValue);
						String heartbeat = JSONObject.toJSONString(map);
						ByteBuffer buffer = ByteBuffer.allocate(8); 
						buffer.putLong(0,heartbeatValue);
						heartbeatMessage = new PongMessage(buffer);
						//websocket.sendPong(binary);
						websocket.sendPing(buffer.array());
						System.err.println("向服务器发送: "+heartbeatValue);
						//websocket.sendPong(ZipUtil.gZip(buffer.array()));
						//WebSocketFrame createPongFrame = WebSocketFrame.createPongFrame(ZipUtil.gZip(buffer.array()));
						//websocket.sendFrame(createPongFrame).addHeader("Host", "api.huobiasia.vip").addHeader("Origin", "https://www.huobi.vn").sendPong();
						//websocket.sendPong(buffer.array());
					}
				}else{
					System.out.println("接收到数据: "+data);
				}
				//websocket.sendPong();
			}

			@Override
			public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame,
					WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
				String payloadText = serverCloseFrame.getPayloadText();
				//String data = new String(ZipUtil.decompress(payload), "UTF-8");
				System.err.println("连接关闭了，是否Server关闭连接："+closedByServer+"，原因："+payloadText);
			}
		});
		ws.connect();
		System.in.read();
	}

}
