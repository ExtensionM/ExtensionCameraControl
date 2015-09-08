package net.sh4869.extensionandroidapp.message;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import net.sh4869.extensionandroidapp.message.childs.ExChildren;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Nobuhiro on 2015/09/08.
 */
public class ExChildListMessage extends ExBaseWebSocketMessage {

    public Map<String, Object> value = new HashMap<>();

    public ExChildListMessage(String message) {
        this.type = "list";

        JsonParser parser = new JsonParser();

        try {
            JsonObject valueObject = parser.parse(message).getAsJsonObject().getAsJsonObject("value");
            this.value.put("result", valueObject.get("result").getAsInt());
            if (this.value.get("result") == 0) {
                JsonObject commandsObject = valueObject.get("commands").getAsJsonObject();
                Set<Map.Entry<String, JsonElement>> entrySet = commandsObject.entrySet();
                JsonObject fixedCommandsObject = new JsonObject();

                Gson gson = new GsonBuilder().serializeNulls().create();
                /// Check Each Map<String,JsonElement> Object - whether or not it have "name" key
                for (Map.Entry<String, JsonElement> entry : entrySet) {
                    try {
                        if (entry.getValue().getAsJsonObject().has("name")) {
                            fixedCommandsObject.add(entry.getKey(),entry.getValue());
                        }
                    } catch (IllegalStateException e) {
                        /// Reomve Object that don't have name or can't get as JsonObject
                        Log.d("ChildList", "Remove error json object : " + entry.getKey());
                    }
                }
                JsonObject finalJsonObject = new JsonObject();
                finalJsonObject.add("commands", fixedCommandsObject);
                try {
                    ExChildren exChildren = gson.fromJson(finalJsonObject, ExChildren.class);
                    this.value.putAll(exChildren.commands);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            } else {
                this.value.put("error",valueObject.get("error").getAsString());
            }
        } catch (JsonParseException e) {
            Log.d("JsonParse", e.getMessage());
        }
    }

    @Override
    public String toString() {
        return null;
    }
}
