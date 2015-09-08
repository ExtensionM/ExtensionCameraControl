package net.sh4869.extensionandroidapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import net.sh4869.extensionandroidapp.R;
import net.sh4869.extensionandroidapp.message.HandlerMessageCodeEnum;
import net.sh4869.extensionandroidapp.websokcetdata.ExAuthResultWebSocketMessage;
import net.sh4869.extensionandroidapp.websokcetdata.ExAuthWebSocketMessage;
import net.sh4869.extensionandroidapp.websokcetdata.ExChild.ExChildFinder;
import net.sh4869.extensionandroidapp.websokcetdata.ExChild.ExChildren;
import net.sh4869.extensionandroidapp.websokcetdata.ExChildListMessage;
import net.sh4869.extensionandroidapp.websokcetdata.ExWebSocketMessage;

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
    private String LOGIN_COMPLETE_MESSAGE = "Login Complete";

    private WebSocketClient mClient;
    private Handler mHandler;

    public enum MessageCode {
        Login_Success,
        Login_fail;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message message){
                switch(message.what){
                    case 0: // LOGIN SUCCESS
                        Toast.makeText(MainActivity.this,getResources().getString(R.string.login_success),Toast.LENGTH_SHORT).show();
                        break;
                    case 1: // LOGIN FAIL
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Error")
                                .setMessage("message: " + (String)message.obj)
                                .setPositiveButton("OK", errorDialogButton)
                                .show();
                        break;
                    case 2:
                        break;
                }
            }
        };
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        File file = this.getFileStreamPath(FILENAME);
        if(file.exists()) {
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
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
        }
    }

    private void messageParser(String message){
        Gson gson = new Gson();
        ExWebSocketMessage wsMessage = gson.fromJson(message, ExWebSocketMessage.class);
        try {
            switch (wsMessage.type) {
                case "webAuth":
                    ExAuthResultWebSocketMessage authReturnMessage = gson.fromJson(message, ExAuthResultWebSocketMessage.class);
                    checkAuthResult(authReturnMessage);
                    break;
                case "list":
                    ExChildListMessage childMessage = new ExChildListMessage(message);
                    checkChildListResult(childMessage);
                    break;
                default:
                    break;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /// Onclick Lisnter for Dialog
    DialogInterface.OnClickListener errorDialogButton = new DialogInterface.OnClickListener(){
        @Override
        public void onClick(DialogInterface dialog, int which) {
            callLoginAcitivty();
        }
    };

    private void callLoginAcitivty(){
        Intent intent = new Intent(getApplication(),LoginActivity.class);
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

                String username = userDataObject.get("username").toString().replace("\"", "");
                String password = userDataObject.get("password").toString().replace("\"","");
                ExAuthWebSocketMessage authWSMessage = new ExAuthWebSocketMessage(username,password);
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



    /// Check Result of Login
    private void checkAuthResult(ExAuthResultWebSocketMessage authResultMessage){
        try {
            if (authResultMessage.authResult()) {
                Log.d(WSTAG, "Result Success");
                sendChildListRequest();
                Message resultMessage = createMessage(LOGIN_COMPLETE_MESSAGE, HandlerMessageCodeEnum.LOGIN_SUCCESS.ordinal());
                mHandler.sendMessage(resultMessage);
            } else {
                Log.d(WSTAG, "Auth Result Fail");
                Message resultMessage = createMessage(authResultMessage.getErrorMessage(),HandlerMessageCodeEnum.LOGIN_FAILED.ordinal());
                mHandler.sendMessage(resultMessage);
            }
        } catch(IllegalStateException e){
            e.printStackTrace();
        }
    }

    Message createMessage(String messageObj,int whatCode){
        Message message = Message.obtain();
        message.obj = messageObj;
        message.what = whatCode;
        return message;
    }


    // ------------------------ * Child List * -------------------------------//

    /// Send Child Rist Request
    private void sendChildListRequest(){
        ExWebSocketMessage wsMessage = new ExWebSocketMessage("list",null);
        try{
            mClient.send(wsMessage.toString());
        } catch (IllegalStateException e){
            e.printStackTrace();
        }
    }


    //// TODO: 2015/09/07
    private void checkChildListResult(ExChildListMessage message){
        ExChildren children = ExChildFinder.searchChildren(message,"Camera");
        if(children.commands.size() == 1){

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
