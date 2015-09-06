package net.sh4869.extensionandroidapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.preference.DialogPreference;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import net.sh4869.extensionandroidapp.R;
import net.sh4869.extensionandroidapp.message.authReturnWebSocketMessage;
import net.sh4869.extensionandroidapp.message.authWebSocketMessage;
import net.sh4869.extensionandroidapp.message.webSocketMessage;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.NotYetConnectedException;

public class MainActivity extends AppCompatActivity {
    public static String WSTAG = "webSocket";
    private String FILENAME = "userdata.json";

    private WebSocketClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        File file = this.getFileStreamPath(FILENAME);
        if(file.exists()) {
            if ("sdk".equals(Build.PRODUCT)) {
                // エミュレータの場合�?�IPv6を無効
                java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
                java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
            }

            try {
                URI uri = new URI("ws://ec2-52-68-77-61.ap-northeast-1.compute.amazonaws.com:3000");
                mClient = new WebSocketClient(uri) {
                    @Override
                    public void onOpen (ServerHandshake handshakedata){
                        Log.d(WSTAG, "onOpen");
                        sendLoginRequest();
                    }

                    @Override
                    public void onMessage (String message){
                        Log.d(WSTAG, "onMessage");
                        Log.d(WSTAG, "Message : " + message);
                        messageParser(message);
                    }

                    @Override
                    public void onClose ( int code, String reason,boolean remote){
                        Log.d(WSTAG, "onClose");
                    }

                    @Override
                    public void onError (Exception ex){
                        Log.d(WSTAG, "onError");
                        ex.printStackTrace();
                    }
                };

                mClient.connect();

            } catch (URISyntaxException e){
                e.printStackTrace();
            }
        } else {
            Intent intent = new Intent(MainActivity.this,loginActivity.class);
            startActivity(intent);
        }
    }

    private void messageParser(String message){
        Gson gson = new Gson();
        webSocketMessage wsMessage = gson.fromJson(message, webSocketMessage.class);
        switch(wsMessage.type){
            case "webAuth":
                authReturnWebSocketMessage authReturnMessage = gson.fromJson(message,authReturnWebSocketMessage.class);
                checkAuthResult(authReturnMessage);
                break;
            case "list":
                break;
            default:
                break;
        }
    }

    private void callLoginAcitivty(){
        Intent intent = new Intent(getApplication(),loginActivity.class);
        startActivity(intent);
    }

    // ----------------------- * Web Auth * --------------------------------//

    /// Send Login Request Method
    private void sendLoginRequest(){
        InputStream fileInputStream;
        boolean sendsuccess = true;
        try {
            /// Read Userdata file
            fileInputStream = openFileInput(FILENAME);
            byte[] ReadByte = new byte[fileInputStream.available()];
            fileInputStream.read(ReadByte);
            String userDataStr = new String(ReadByte);

            /// Create UserData Json
            JsonParser parser = new JsonParser();
            try {
                JsonElement userDataElement = parser.parse(userDataStr);
                JsonObject userDataObject = userDataElement.getAsJsonObject();

                String username = userDataObject.get("username").toString();
                String password = userDataObject.get("password").toString();
                authWebSocketMessage authWSMessage = new authWebSocketMessage(username,password);
                try {
                    mClient.send(authWSMessage.toString());
                } catch(NotYetConnectedException e){
                    e.printStackTrace();
                }
            } catch (JsonParseException e){
                e.printStackTrace();
                sendsuccess = false;
            } catch (IllegalStateException e){
                e.printStackTrace();
                sendsuccess = false;
            }
        } catch (IOException e){
            e.printStackTrace();
            sendsuccess = false;
        }
        if(!sendsuccess){
            callLoginAcitivty();
        }
    }

    /// Onclick Lisnter for Dialog
    DialogInterface.OnClickListener errorDialogButton = new DialogInterface.OnClickListener(){
        @Override
        public void onClick(DialogInterface dialog, int which){
           callLoginAcitivty();
        }
    };

    /// Check Result of Login
    private void checkAuthResult(authReturnWebSocketMessage authResultMessage){
        try {
            if (authResultMessage.authResult()) {
                Log.d(WSTAG, "Result Success");
                sendChildListRequest();
            } else {
                Log.d(WSTAG, "Auth Result Fail");
                new AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("message: " + authResultMessage.getErrorMessage())
                        .setPositiveButton("OK", errorDialogButton)
                        .show();
            }
        } catch(IllegalStateException e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
