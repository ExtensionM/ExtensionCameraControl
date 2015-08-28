package net.sh4869.extensionandroidapp.message;

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
    /// Message type
    public String type = "webAuth";
    /// Message value
    public String username,password;

    public authWebSocketMessage(String username,String password){
        this.username = username;
        this.password = password;
    }

    public String toString(){
        JsonObject valueElement = new JsonObject();
        valueElement.addProperty("username",username);
        valueElement.addProperty("password",password);

        JsonObject messageString = new JsonObject();
        messageString.addProperty("type", type);
        messageString.add("value",valueElement);
        Gson gson = new Gson();
        return gson.toJson(messageString);
    }
}
