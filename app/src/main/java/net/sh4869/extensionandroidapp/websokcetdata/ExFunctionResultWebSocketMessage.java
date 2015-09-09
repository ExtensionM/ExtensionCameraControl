package net.sh4869.extensionandroidapp.websokcetdata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.sh4869.extensionandroidapp.websokcetdata.ExChild.ExChildFunctionResult;

/**
 * Created by Nobuhiro on 2015/09/10.
 */
public class ExFunctionResultWebSocketMessage extends ExBaseWebSocketMessage {

    public ExChildFunctionResult value;

    public ExFunctionResultWebSocketMessage(String messageStr) {
        Gson gson = new GsonBuilder().serializeNulls().create();
        JsonParser parser = new JsonParser();
        try {
            JsonObject baseObject = parser.parse(messageStr).getAsJsonObject();
            this.type = baseObject.get("type").getAsString();
            this.value = gson.fromJson(baseObject.get("value").getAsJsonObject(), ExChildFunctionResult.class);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        return new GsonBuilder().serializeNulls().create().toJson(this);
    }

}
