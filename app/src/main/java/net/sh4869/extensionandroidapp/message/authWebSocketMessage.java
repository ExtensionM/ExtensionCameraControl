package net.sh4869.extensionandroidapp.message;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

/**
 * Created by Nobuhiro on 2015/08/28.
 */
public class authWebSocketMessage extends baseWebSocketMessage {
    /// Message value
    public Map<String,String> value = new HashMap<String,String>();

    public authWebSocketMessage(String username,String password){
        this.type = "webAuth";
        this.value.put("username",username);
        this.value.put("password",password);
    }

    public String toString(){
        Gson gson = new Gson();
        String messageString = "";
        try {
            messageString = gson.toJson(this);
        } catch( NullPointerException e) {
            e.printStackTrace();
        }
        Log.d("LOGIN",messageString);
        return messageString;
    }
}
