package com.firecode.record_test.websocket;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JAVAWebSocketClientTest {
	
	private final static Logger logger = LoggerFactory.getLogger(JAVAWebSocketClientTest.class);
	
	public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
		WebSocketClient client = new WebSocketClient(new URI("wss://api.huobiasia.vip/ws"), new Draft_6455()) {
			
			@Override
			public void onOpen(ServerHandshake arg0) {
				logger.debug("开始建立链接...");
			}


			@Override
			public void onMessage(String arg0) {
				logger.debug("检测到服务器请求..."+arg0);
				//这个方法自动接收服务器发过来的信息,直接在此处调用自己写的方法即可.
			}
			
			@Override
			public void onMessage(ByteBuffer bytes) {
				System.out.println("获取到服务器端的消息："+bytes);
			}


			@Override
			public void onError(Exception arg0) {
				arg0.printStackTrace();
				logger.debug("客户端发生错误,即将关闭!");
			}
			

			@Override
			public void onWebsocketPing(WebSocket conn, Framedata f) {
				System.out.println("onWebsocketPing 获取到服务端的心跳信息："+f);
			}


			@Override
			public void onWebsocketPong(WebSocket conn, Framedata f) {
				System.out.println("onWebsocketPong 获取到服务端的心跳信息："+f);
			}


			@Override
			public void onClose(int arg0, String arg1, boolean arg2) {
				System.err.println("状态："+arg0+"，原因："+arg1);
				/*try {
					Client.createConnect();
				} catch (Exception e) {
					e.printStackTrace();
					logger.debug("重新连接失败,请检查网络!");
				}*/
				//重启客户端后创建4个定时任务线程
				/*new SendHeartThread().start();
				new SendInThread().start();
				new SendOutThread().start();
				new SendMonthCardThread().start();*/
			}
		};
		SocketFactory socketFactory = SSLSocketFactory.getDefault();
		client.setSocketFactory(socketFactory);
		client.connect();
		//判断连接状态,
		/*while (client.getReadyState().equals(READYSTATE.OPEN)) {
			logger.debug("成功建立链接!");
		}*/
		System.in.read();
	}

}
