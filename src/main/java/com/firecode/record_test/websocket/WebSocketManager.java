package com.firecode.record_test.websocket;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

public class WebSocketManager {

    private static final int DEFAULT_SOCKET_CONNECTTIMEOUT = 3000;
    private static final int DEFAULT_SOCKET_RECONNECTINTERVAL = 3000;
    private static final int FRAME_QUEUE_SIZE = 5;

    WebSocketListener mWebSocketListener;
    WebSocketFactory mWebSocketFactory;
    WebSocket mWebSocket;

    private ConnectStatus mConnectStatus = ConnectStatus.CONNECT_DISCONNECT;
    private Timer mReconnectTimer = new Timer();
    private TimerTask mReconnectTimerTask;

    private String mUri;

    public interface WebSocketListener {
        void onConnected(Map<String, List<String>> headers);
        void onTextMessage(String text);
    }

    public enum ConnectStatus {
        CONNECT_DISCONNECT,// 断开连接
        CONNECT_SUCCESS,//连接成功
        CONNECT_FAIL,//连接失败
        CONNECTING;//正在连接
    }

    public WebSocketManager(String uri) {
        this(uri, DEFAULT_SOCKET_CONNECTTIMEOUT);
    }

    public WebSocketManager(String uri, int timeout) {
        mUri = uri;//Constants.WEB_SOCKET_URL + deviceToken;
        mWebSocketFactory = new WebSocketFactory().setConnectionTimeout(timeout);
    }

    public void setWebSocketListener(WebSocketListener webSocketListener) {
        mWebSocketListener = webSocketListener;
    }

    public void connect() {
        try {
            mWebSocket = mWebSocketFactory.createSocket(mUri)
                    .setFrameQueueSize(FRAME_QUEUE_SIZE)//设置帧队列最大值为5
                    .setMissingCloseFrameAllowed(false)//设置不允许服务端关闭连接却未发送关闭帧
                    .addListener(new NVWebSocketListener())
                    .connect();  // 这里我改成了同步调用 异步调用请使用connectAsynchronously()
            setConnectStatus(ConnectStatus.CONNECTING);
        } catch (IOException e) {
            e.printStackTrace();
            reconnect();
        } catch (WebSocketException e) {
            e.printStackTrace();
            reconnect();
        }
    }
    
    public void reconnect() {
        if (mWebSocket != null && !mWebSocket.isOpen() && getConnectStatus() != ConnectStatus.CONNECTING) {
            mReconnectTimerTask = new TimerTask() {
                @Override
                public void run() {
                    connect();
                }
            };
            mReconnectTimer.schedule(mReconnectTimerTask, DEFAULT_SOCKET_RECONNECTINTERVAL);
        }
    }

    // 客户端像服务器发送消息
    public void sendMessage(int deviceStatus, int appUpdateFlag) {
       /* try {
            JSONObject json = new JSONObject();
            json.put("xxx", xxx);
            json.put("xxx", xxx);
            mWebSocket.sendText(json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }

    private void setConnectStatus(ConnectStatus connectStatus) {
        mConnectStatus = connectStatus;
    }

    public ConnectStatus getConnectStatus() {
        return mConnectStatus;
    }

    public void disconnect() {
        if (mWebSocket != null) {
            mWebSocket.disconnect();
        }
        setConnectStatus(null);
    }
}

// Adapter的回调中主要做三件事：1.设置连接状态2.回调websocketlistener3.连接失败重连
class NVWebSocketListener extends WebSocketAdapter {

    @Override
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
        super.onConnected(websocket, headers);
        System.out.println("OS. WebSocket onConnected");
       /* setConnectStatus(ConnectStatus.CONNECT_SUCCESS);
        if (mWebSocketListener != null) {
            mWebSocketListener.onConnected(headers);
        }*/
    }

    @Override
    public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
        super.onConnectError(websocket, exception);
        System.out.println("OS. WebSocket onConnectError");
        //setConnectStatus(ConnectStatus.CONNECT_FAIL);
    }

    @Override
    public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
        super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
        System.out.println("OS. WebSocket onDisconnected");
        //setConnectStatus(ConnectStatus.CONNECT_DISCONNECT);
        //reconnect();重连
    }

    @Override
    public void onTextMessage(WebSocket websocket, String text) throws Exception {
        super.onTextMessage(websocket, text);
        System.out.println("OS. WebSocket onTextMessage");
       /* if (mWebSocketListener != null) {
            mWebSocketListener.onTextMessage(text);
        }*/
    }
}
