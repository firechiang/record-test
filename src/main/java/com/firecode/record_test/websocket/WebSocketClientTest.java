package com.firecode.record_test.websocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import com.alibaba.fastjson.JSONObject;
import com.firecode.record_test.util.ZipUtil;

/**
 * 模拟WebSocket 客户端
 * 
 * @author JIANG
 */
public class WebSocketClientTest {
	
	private static final Pattern PING_PATTERN = Pattern.compile("\\{(\"ping\"|\"pong\")[:].*\\}");
	private static final String HEARTBEAT_NAME_PING = "ping";
	private static final String HEARTBEAT_NAME_PONG = "pong";

	public static void main(String[] args) throws IOException {
		
		/*Date date = new Date(1572750397487L);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//2019-11-03 11:04:32
		//2019-11-03 11:06:02
		System.err.println(sdf.format(date));*/
		String url = "wss://api.huobiasia.vip/ws";
		//String url = "ws://121.40.165.18:8800";
		StandardWebSocketClient client = new StandardWebSocketClient();
	    WebSocketConnectionManager manager = new WebSocketConnectionManager(client, new MyHandler(), url);
	    manager.setAutoStartup(true);
	    manager.setOrigin("https://www.huobi.vn");
	    manager.start();
	    System.in.read();
	}

	private static class MyHandler extends BinaryWebSocketHandler {

		@Override
		protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
			System.err.println(message);
			byte[] array = message.getPayload().array();
			String data = new String(ZipUtil.decompress(array), "UTF-8");
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
					System.err.println("向服务器发送: "+heartbeatValue);
				}
				session.sendMessage(heartbeatMessage);
				
			}else{
				System.out.println("接收到数据: "+data);
			}
		}
		
		
		
		


		/*@Override
		public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
			
			byte[] array = ((ByteBuffer) message.getPayload()).array();
			String data = new String(ZipUtil.decompress(array), "UTF-8");
			System.out.println("接收到服务端发来的消息："+data);
		}*/




		@Override
		protected void handleTextMessage(WebSocketSession session, TextMessage message) {
			System.err.println("接收到文本消息："+message.getPayload());
			ByteBuffer buffer = ByteBuffer.allocate(8); 
			buffer.putLong(0,121212121L);
			BinaryMessage heartbeatMessage = new BinaryMessage(buffer,true);
			try {
				session.sendMessage(heartbeatMessage);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(buffer);
		}






		@Override
		public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
			System.err.println("关闭连接："+status);
		}

		@Override
		protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
			byte[] array = message.getPayload().array();
			ByteBuffer buffer = ByteBuffer.allocate(8); 
	        buffer.put(array, 0, array.length);
	        buffer.flip();//need flip 
			System.out.println("接收到心跳信息: "+buffer.getLong());
		}
		
		
		
		
		
		/*@Override
		public void afterConnectionEstablished(WebSocketSession session) throws Exception {
			System.out.println("connected...........");
			session.sendMessage(new TextMessage("hello, web socket"));
			super.afterConnectionEstablished(session);
		}*/

		/*@Override
		protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
			System.out.println("receive: " + message.getPayload());
			super.handleTextMessage(session, message);
		}*/
	}
}
