package net.sh4869.extensionandroidapp.message;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import net.sh4869.extensionandroidapp.activity.MainActivity;
import net.sh4869.extensionandroidapp.message.childs.childrenInfo;
import net.sh4869.extensionandroidapp.message.childs.extensionChildren;

import org.json.JSONException;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Created by Nobuhiro on 2015/09/08.
 */
public class childListResultMessage extends baseWebSocketMessage {

    public Map<String, Object> value = new HashMap<>();

    public childListResultMessage(String message) {
        this.type = "list";

        JsonParser parser = new JsonParser();
        try {
            JsonObject messageObject = parser.parse(message).getAsJsonObject();
            JsonObject valueObject = messageObject.getAsJsonObject("value");

            this.value.put("result", valueObject.get("result").getAsInt());
            if (valueObject.get("result").getAsInt() == 0) {
                JsonObject commandsObject = valueObject.get("commands").getAsJsonObject();
                Set<Map.Entry<String, JsonElement>> entrySet = commandsObject.entrySet();
                JsonObject fixedCommandsObject = new JsonObject();

                GsonBuilder gBuilder = new GsonBuilder().serializeNulls();
                Gson gson = gBuilder.create();

                for (Map.Entry<String, ?> entry : entrySet) {
                    if (entry.getValue() instanceof JsonElement) {
                        if (((JsonElement) entry.getValue()).isJsonObject()) {
                            if(( (JsonElement) entry.getValue()).getAsJsonObject().has("name")){
                                fixedCommandsObject.add(entry.getKey(),(JsonElement)entry.getValue());
                                Log.d("JsonParse", gson.toJson(gson.fromJson((JsonElement) entry.getValue(),childrenInfo.class),childrenInfo.class));
                            }
                        }
                    }
                }

                JsonObject finalJsonObject = new JsonObject();
                finalJsonObject.add("commands",fixedCommandsObject);
                try {
                    extensionChildren exChildren = gson.fromJson(finalJsonObject, extensionChildren.class);
                    Log.d("JsonParse",gson.toJson(finalJsonObject));
                    Log.d("JsonParse",gson.toJson(exChildren,extensionChildren.class));
                    this.value.putAll(exChildren.commands);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            } else {

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
