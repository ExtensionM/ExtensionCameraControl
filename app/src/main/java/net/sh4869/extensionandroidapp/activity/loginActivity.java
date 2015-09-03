package net.sh4869.extensionandroidapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import net.sh4869.extensionandroidapp.R;
import net.sh4869.extensionandroidapp.extensionWebSocketClient;
import net.sh4869.extensionandroidapp.message.authWebSocketMessage;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.NotYetConnectedException;
import java.util.IllegalFormatCodePointException;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class loginActivity extends AppCompatActivity {
    private Handler mHandler;

    private extensionWebSocketClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mHandler = new Handler();
        if ("sdk".equals(Build.PRODUCT)) {
            // エミュレータの場合はIPv6を無効
            java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
            java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
        }

        try {
            URI uri = new URI("ws://ec2-52-68-77-61.ap-northeast-1.compute.amazonaws.com:3000");
            mClient = new extensionWebSocketClient(uri);
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
                mClient.messageSend(sendText);
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
}
