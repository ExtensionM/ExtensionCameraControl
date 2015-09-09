package net.sh4869.extensionandroidapp.activity;

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
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import net.sh4869.extensionandroidapp.R;
import net.sh4869.extensionandroidapp.message.HandlerMessage;
import net.sh4869.extensionandroidapp.utility.Direction;
import net.sh4869.extensionandroidapp.websokcetdata.ExAuthResultWebSocketMessage;
import net.sh4869.extensionandroidapp.websokcetdata.ExAuthWebSocketMessage;
import net.sh4869.extensionandroidapp.websokcetdata.ExCallResultWebSocketMessage;
import net.sh4869.extensionandroidapp.websokcetdata.ExCallWebSocketMessage;
import net.sh4869.extensionandroidapp.websokcetdata.ExChild.ExChild;
import net.sh4869.extensionandroidapp.websokcetdata.ExChild.ExChildFinder;
import net.sh4869.extensionandroidapp.websokcetdata.ExChild.ExChildren;
import net.sh4869.extensionandroidapp.websokcetdata.ExChildListMessage;
import net.sh4869.extensionandroidapp.websokcetdata.ExFunctionResultWebSocketMessage;
import net.sh4869.extensionandroidapp.websokcetdata.ExWebSocketMessage;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    // TAG FOR WebSocket
    private final String WSTAG = "WebSocket";
    private final String ACTIVITY_TAG = "MainActivity";

    // name of data
    private final String FILENAME = "userdata.json";

    // GUID of Cmaera Client
    private String cameraChildGUID;

    // Value of Camera Child Servo Value
    private int xAngle = 90;
    private int yAngle = 90;

    // Display Message
    private String displayMessage;

    private WebSocketClient mClient;
    private Handler mHandler;

    protected void onCreate(Bundle savedInstanceState) {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                StringBuilder sBuilder = new StringBuilder();
                int messageStringId = R.string.default_message;
                if(((HandlerMessage)message.obj).haveAddMessage){
                    sBuilder.append("\n" + displayMessage);
                }
                switch ((HandlerMessage) message.obj) {
                    case LOGIN_SUCCESS: // LOGIN SUCCESS
                        messageStringId = R.string.login_success;
                        break;
                    case LOGIN_FAILED: // LOGIN FAIL
                        messageStringId = R.string.login_fail;
                        break;
                    case CHILD_FOUND:
                        messageStringId = R.string.child_found;
                        break;
                    case CHILD_FOUND_MULTIPLE:
                        messageStringId = R.string.child_found_multiple;
                        // TODO: 2015/09/10 複数のカメラ子機がある場合どうするかという話
                        break;
                    case CHILD_NOT_FOUND:
                        messageStringId = R.string.child_not_found;
                        break;
                    case CALL_FAIL:
                        messageStringId = R.string.fail_to_call_function;
                        break;
                    case FUNCTION_FAIL:
                        messageStringId = R.string.fail_to_complete_function;
                        break;
                }
                Log.d(ACTIVITY_TAG, getResources().getString(messageStringId));
                String displayString = new String(sBuilder.insert(0,getResources().getString(messageStringId)));
                Toast.makeText(MainActivity.this, displayString, Toast.LENGTH_SHORT).show();
            }
        };
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        File file = this.getFileStreamPath(FILENAME);
        if (file.exists()) {
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
                        sendLoginRequest();
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
        } else {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        // ------------------ Set Onclick Listener

        this.findViewById(R.id.upButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCameraServoMove(Direction.UP, 5);
            }
        });
        this.findViewById(R.id.downButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCameraServoMove(Direction.DOWN, 5);
            }
        });
        this.findViewById(R.id.rightButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCameraServoMove(Direction.RIGHT, 5);
            }
        });
        this.findViewById(R.id.leftButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCameraServoMove(Direction.LEFT, 5);
            }
        });
    }

    // Parse Message from WebSocket
    private void messageParser(String message) {
        Gson gson = new Gson();
        ExWebSocketMessage wsMessage = gson.fromJson(message, ExWebSocketMessage.class);
        switch (wsMessage.type) {
            case "webAuth":
                ExAuthResultWebSocketMessage authReturnMessage = gson.fromJson(message, ExAuthResultWebSocketMessage.class);
                checkAuthResult(authReturnMessage);
                break;
            case "list":
                ExChildListMessage childMessage = new ExChildListMessage(message);
                checkChildListResult(childMessage);
                break;
            case "call":
                ExCallResultWebSocketMessage callResultMessage = new ExCallResultWebSocketMessage(message);
                checkCallResult(callResultMessage);
                break;
            case "result":
                ExFunctionResultWebSocketMessage functionResultMessage = new ExFunctionResultWebSocketMessage(message);
                checkFunctionResult(functionResultMessage);
                break;
            default:
                break;
        }
    }


    private void callLoginActivity() {
        Intent intent = new Intent(getApplication(), LoginActivity.class);
        startActivity(intent);
    }

    Message createMessage(HandlerMessage messageCode, boolean isAddMessage) {
        Message message = Message.obtain();
        messageCode.haveAddMessage = isAddMessage;
        message.obj = messageCode;
        return message;
    }


    // ----------------------- * Web Auth * --------------------------------//

    /// Send Login Request Method
    private void sendLoginRequest() {
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
                JsonObject userDataObject = parser.parse(userDataStr).getAsJsonObject();
                String username = userDataObject.get("username").toString().replace("\"", "");
                String password = userDataObject.get("password").toString().replace("\"", "");
                ExAuthWebSocketMessage authWSMessage = new ExAuthWebSocketMessage(username, password);
                mClient.send(authWSMessage.toString());
            } catch (JsonParseException e) {
                e.printStackTrace();
                sendsuccess = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            sendsuccess = false;
        }
        if (!sendsuccess) {
            callLoginActivity();
        }
    }

    /// Check Result of Login
    private void checkAuthResult(ExAuthResultWebSocketMessage authResultMessage) {
        if (authResultMessage.authResult()) {
            Log.d(WSTAG, "Result Success");
            sendChildListRequest();
            Message resultMessage = createMessage(HandlerMessage.LOGIN_SUCCESS, false);
            mHandler.sendMessage(resultMessage);
        } else {
            Log.d(WSTAG, "Auth Result Fail");
            displayMessage = authResultMessage.getErrorMessage();
            Message resultMessage = createMessage(HandlerMessage.LOGIN_FAILED, true);
            mHandler.sendMessage(resultMessage);
        }
    }


    // ------------------------ * Child List * -------------------------------//

    /// Send Child Rist Request
    private void sendChildListRequest() {
        ExWebSocketMessage wsMessage = new ExWebSocketMessage("list", null);
        mClient.send(wsMessage.toString());
    }

    private void checkChildListResult(ExChildListMessage message) {
        ExChildren children = ExChildFinder.searchChildren(message, "Camera");
        List<String> guidList = new ArrayList<>();
        for (Map.Entry<String, ExChild> child : children) {
            guidList.add(child.getKey());
        }
        Message messageData;
        switch (guidList.size()) {
            case 1:
                messageData = createMessage(HandlerMessage.CHILD_FOUND, false);
                cameraChildGUID = guidList.get(guidList.size() - 1);
                break;
            case 0:
                messageData = createMessage(HandlerMessage.CHILD_NOT_FOUND, false);
                break;
            default:
                messageData = createMessage(HandlerMessage.CHILD_FOUND_MULTIPLE, false);

        }
        mHandler.sendMessage(messageData);
    }

    // -------------------------- Call  ------------------------//

    /**
     * Call Camera Child Servo Movement
     *
     * @param direction direction
     * @param value     angle value
     */
    public void sendCameraServoMove(Direction direction, int value) {
        String funcName = "";
        int angle = 90;
        switch (direction) {
            case UP:
                funcName = "angleY";
                angle = yAngle + value;
                break;
            case DOWN:
                funcName = "angleY";
                angle = yAngle - value;
                break;
            case RIGHT:
                funcName = "angleX";
                angle = xAngle + value;
                break;
            case LEFT:
                funcName = "angleX";
                angle = xAngle - value;
                break;
            default:
                break;
        }
        Map<String, Object> args = new HashMap<>();
        args.put("angle", angle);
        ExCallWebSocketMessage callMessage = new ExCallWebSocketMessage(cameraChildGUID, funcName, args);
        sendCallMessage(callMessage);
    }

    /**
     * Call Request
     *
     * @param callMessage callmessage class that you want to send
     */
    private void sendCallMessage(ExCallWebSocketMessage callMessage) {
        mClient.send(callMessage.toString());
    }

    private void checkCallResult(ExCallResultWebSocketMessage message) {
        if (!message.getCallResult()) {
            displayMessage = message.getErrorMessage();
            Message sendHndlerMessage = createMessage(HandlerMessage.CALL_FAIL, true);
            mHandler.sendMessage(sendHndlerMessage);
        }
    }

    // ------------------------ Check Result of Function --------------------

    private void checkFunctionResult(ExFunctionResultWebSocketMessage message) {
        if (message.value.hasError) {
            Message handlerMessage = createMessage(HandlerMessage.FUNCTION_FAIL, false);
            mHandler.sendMessage(handlerMessage);
        } else {
            if (message.value.functionName.equals("angleX")) {
                xAngle = (Integer) message.value.result;
            } else if (message.value.functionName.equals("angleY")) {
                yAngle = (Integer) message.value.result;
            }
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
