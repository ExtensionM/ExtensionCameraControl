package net.sh4869.extensionandroidapp.websokcetdata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nobuhiro on 2015/09/09.
 */
public class ExCallWebSocketMessage extends ExBaseWebSocketMessage {

    // Message Value
    public Map<String, Object> value = new HashMap<>();

    public ExCallWebSocketMessage(String childId, String funcName, Map<String, Object> args) {
        this.type = "call";
        this.value.put("id", childId);
        this.value.put("func", funcName);
        if (args != null) {
            this.value.putAll(args);
        }
    }


    public String toString() {
        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.toJson(this);
    }
}
