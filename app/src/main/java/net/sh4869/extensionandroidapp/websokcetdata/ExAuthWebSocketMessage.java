package net.sh4869.extensionandroidapp.websokcetdata;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by Nobuhiro on 2015/08/28.
 */
public class ExAuthWebSocketMessage extends ExBaseWebSocketMessage {
    /// Message value
    public Map<String,String> value = new HashMap<String,String>();

    public ExAuthWebSocketMessage(String username, String password){
        this.type = "webAuth";
        this.value.put("username",username);
        this.value.put("password",password);
    }

    public String toString(){
        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.toJson(this);
    }
}
