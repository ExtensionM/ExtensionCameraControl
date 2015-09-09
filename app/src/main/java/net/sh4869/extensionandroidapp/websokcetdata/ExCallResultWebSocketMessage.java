package net.sh4869.extensionandroidapp.websokcetdata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nobuhiro on 2015/09/09.
 */
public class ExCallResultWebSocketMessage extends ExBaseWebSocketMessage {

    public Map<String, Object> value = new HashMap<>();

    public ExCallResultWebSocketMessage(String jsonString) {
        this.type = "call";
        JsonParser parser = new JsonParser();
        try {
            JsonObject valueObject = parser.parse(jsonString).getAsJsonObject().getAsJsonObject("value");
            this.value.put("result", valueObject.get("result").getAsInt());
            if (this.value.get("result") == 0) {
                this.value.put("commands", valueObject.get("commands"));
            } else {
                this.value.put("error", valueObject.get("error"));
            }
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
    }

    // get Call Function Reuslt
    public boolean getCallResult() {
        if (this.value.containsKey("result")) {
            if ((Double) this.value.get("result") == 0) {
                return true;
            } else {
                return false;
            }
        } else {
            throw new IllegalStateException("this instance don't have result value");
        }
    }

    // Get Error Message
    public String getErrorMessage() {
        if (this.value.containsKey("error")) {
            return this.value.get("error").toString();
        } else {
            return null;
        }
    }

    public String toString() {
        return new GsonBuilder().serializeNulls().create().toJson(this);
    }

}
