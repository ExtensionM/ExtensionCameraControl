package net.sh4869.extensionandroidapp;

import android.util.Log;

import com.google.gson.Gson;

import net.sh4869.extensionandroidapp.message.authReturnWebSocketMessage;
import net.sh4869.extensionandroidapp.message.webSocketMessage;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created by Nobuhiro on 2015/09/03.
 */
public class extensionWebSocketClient  {

    public static String TAG = "webSocket";

    private WebSocketClient mClient;

    private Handler mHandler;

    public extensionWebSocketClient(URI uri) {
        mClient = new WebSocketClient(uri){
            @Override
            public void onOpen (ServerHandshake handshakedata){
                Log.d(TAG, "onOpen");
            }

            @Override
            public void onMessage (String message){
                Log.d(TAG, "onMessage");
                Log.d(TAG, "Message : " + message);
                messageParser(message);
            }

            @Override
            public void onClose ( int code, String reason,boolean remote){
                Log.d(TAG, "onClose");
            }

            @Override
            public void onError (Exception ex){
                Log.d(TAG, "onError");
                ex.printStackTrace();
            }
        };
        mClient.connect();
    }

    public void messageSend(String message){
        if(mClient.getReadyState() == WebSocket.READYSTATE.OPEN) {
            mClient.send(message);
        }
    }

    private void messageParser(String message){
        Gson gson = new Gson();
        webSocketMessage wsMessage = gson.fromJson(message,webSocketMessage.class);
        switch(wsMessage.type){
            case "webAuth":
                authReturnWebSocketMessage authReturnMessage = gson.fromJson(message,authReturnWebSocketMessage.class);
                break;
            default:
                break;
        }
    }

    private void checkAuthResult(authReturnWebSocketMessage authResultMessage){
        try {
            if (authResultMessage.authResult()) {
                Log.d(TAG,"Result Success");
                //TODO
            }
        } catch(IllegalStateException e){
            e.printStackTrace();
        }

    }
}
