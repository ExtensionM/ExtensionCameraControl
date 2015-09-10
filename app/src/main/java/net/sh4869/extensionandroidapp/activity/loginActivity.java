package net.sh4869.extensionandroidapp.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.sh4869.extensionandroidapp.R;
import net.sh4869.extensionandroidapp.websokcetdata.ExAuthResultWebSocketMessage;
import net.sh4869.extensionandroidapp.websokcetdata.ExAuthWebSocketMessage;
import net.sh4869.extensionandroidapp.websokcetdata.ExWebSocketMessage;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.NotYetConnectedException;
import java.util.logging.LogRecord;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ServerHandshake;
import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {
    public static String WSTAG = "webSocket";
    private String FILENAME = "userdata.json";

    private WebSocketClient mClient;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        deleteFile(FILENAME);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message message){
                ((TextView)findViewById(R.id.loginMessageTextView)).setText((String)message.obj);
                ((TextView)findViewById(R.id.loginMessageTextView)).setTextColor(Color.RED);
            }
        };
        if ("sdk".equals(Build.PRODUCT)) {
            // エミュレータの場合はIPv6を無効
            java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
            java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
        }

        try {
            URI uri = new URI("ws://ec2-52-68-77-61.ap-northeast-1.compute.amazonaws.com:3000");
            mClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    Log.d(WSTAG, "onOpen");
                }

                @Override
                public void onMessage(String message) {
                    Log.d(WSTAG, "onMessage");
                    Log.d(WSTAG, "Message : " + message);
                    messageParser(message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.d(WSTAG, "onClose");
                }

                @Override
                public void onError(Exception ex) {
                    Log.d(WSTAG, "onError");
                    ex.printStackTrace();
                }
            };

            mClient.connect();

        } catch (URISyntaxException e) {
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

    public void sendLoginRequest(View v) {
        Log.d("MainActivity", "Push Login button");
        EditText usernameEditText = (EditText) findViewById(R.id.usernameEdit);
        EditText passwrodEditText = (EditText) findViewById(R.id.passwordEdit);

        String username = usernameEditText.getText().toString();
        String password = passwrodEditText.getText().toString();
        if (username != " " && password != " ") {
            ExAuthWebSocketMessage webSocketMessage = new ExAuthWebSocketMessage(username, password);
            String sendText = webSocketMessage.toString();
            Log.d("MainActivity", "text-send: " + sendText);
            try {
                mClient.send(sendText);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (WebsocketNotConnectedException e){
                e.printStackTrace();
                mClient.connect();
            }
        } else {

        }
    }

    private void messageParser(String message) {
        Gson gson = new Gson();
        ExWebSocketMessage wsMessage = gson.fromJson(message, ExWebSocketMessage.class);
        switch (wsMessage.type) {
            case "webAuth":
                ExAuthResultWebSocketMessage authReturnMessage = gson.fromJson(message, ExAuthResultWebSocketMessage.class);
                checkAuthResult(authReturnMessage);
                break;
            default:
                break;
        }
    }


    private void checkAuthResult(ExAuthResultWebSocketMessage authResultMessage) {
        try {
            if (authResultMessage.authResult()) {
                Log.d(WSTAG, "Result Success");
                saveNameAndPass();
                Intent intent = new Intent(getApplication(), MainActivity.class);
                startActivity(intent);
            } else {
                Message message = Message.obtain();
                message.obj = "Fail to Login : " + authResultMessage.getErrorMessage();
                mHandler.sendMessage(message);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void saveNameAndPass() {
        Gson gson = new Gson();
        EditText usernameEditText = (EditText) findViewById(R.id.usernameEdit);
        EditText passwrodEditText = (EditText) findViewById(R.id.passwordEdit);

        String username = usernameEditText.getText().toString();
        String password = passwrodEditText.getText().toString();

        JsonObject jObject = new JsonObject();
        jObject.addProperty("username", username);
        jObject.addProperty("password", password);

        String saveString = gson.toJson(jObject);

        FileOutputStream fileOutputStream;

        try {
            fileOutputStream = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fileOutputStream.write(saveString.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
