package net.sh4869.extensionandroidapp.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.sh4869.extensionandroidapp.R;
import net.sh4869.extensionandroidapp.message.authReturnWebSocketMessage;
import net.sh4869.extensionandroidapp.message.authWebSocketMessage;
import net.sh4869.extensionandroidapp.message.webSocketMessage;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.NotYetConnectedException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class loginActivity extends AppCompatActivity {
    public static String WSTAG = "webSocket";
    private String FILENAME = "userdata.json";

    private WebSocketClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if ("sdk".equals(Build.PRODUCT)) {
            // エミュレータの場合はIPv6を無効
            java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
            java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
        }

        try {
            URI uri = new URI("ws://ec2-52-68-77-61.ap-northeast-1.compute.amazonaws.com:3000");
            mClient = new WebSocketClient(uri) {
                @Override
                public void onOpen (ServerHandshake handshakedata){
                    Log.d(WSTAG, "onOpen");
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    public void sendLoginRequest(View v){
        Log.d("MainActivity","Push Login button");
        EditText usernameEditText = (EditText)findViewById(R.id.usernameEdit);
        EditText passwrodEditText = (EditText)findViewById(R.id.passwordEdit);

        String username = usernameEditText.getText().toString();
        String password = passwrodEditText.getText().toString();
        if(username != " " && password != " ") {

            authWebSocketMessage webSocketMessage = new authWebSocketMessage(username,password);
            String sendText = webSocketMessage.toString();
            Log.d("MainActivity","text-send: " + sendText);
            try {
                mClient.send(sendText);
            } catch (NotYetConnectedException e){
                e.printStackTrace();
            } catch (IllegalStateException e){
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
        } else {

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
            default:
                break;
        }
    }


    private void checkAuthResult(authReturnWebSocketMessage authResultMessage){
        try {
            if (authResultMessage.authResult()) {
                Log.d(WSTAG,"Result Success");
                saveNameAndPass();
                Intent intent = new Intent(getApplication(),MainActivity.class);
                startActivity(intent);
            }
        } catch(IllegalStateException e){
            e.printStackTrace();
        }
    }

    private void saveNameAndPass(){
        Gson gson = new Gson();
        EditText usernameEditText = (EditText)findViewById(R.id.usernameEdit);
        EditText passwrodEditText = (EditText)findViewById(R.id.passwordEdit);

        String username = usernameEditText.getText().toString();
        String password = passwrodEditText.getText().toString();

        JsonObject jObject = new JsonObject();
        jObject.addProperty("username",username);
        jObject.addProperty("password",password);

        String saveString = gson.toJson(jObject);

        FileOutputStream fileOutputStream;

        try{
            fileOutputStream = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fileOutputStream.write(saveString.getBytes());
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
