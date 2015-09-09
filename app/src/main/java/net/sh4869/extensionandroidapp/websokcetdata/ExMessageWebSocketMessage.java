package net.sh4869.extensionandroidapp.websokcetdata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import net.sh4869.extensionandroidapp.websokcetdata.ExChild.ExChildFunction;
import net.sh4869.extensionandroidapp.websokcetdata.ExChild.ExChildFunctionResult;
import net.sh4869.extensionandroidapp.websokcetdata.ExChild.ExChildFunctionResultData;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nobuhiro on 2015/09/10.
 */
public class ExMessageWebSocketMessage extends ExBaseWebSocketMessage {

    public Map<String, Object> value = new HashMap<>();

    public ExMessageWebSocketMessage(String sourceString) {
        Gson gson = new GsonBuilder().serializeNulls().create();
        JsonParser parser = new JsonParser();
        try {
            JsonObject messageObject = parser.parse(sourceString).getAsJsonObject();
            this.type = messageObject.get("type").getAsString();
            JsonObject valueObject = messageObject.get("value").getAsJsonObject();
            this.value.put("result", valueObject.get("result").getAsInt());
            this.value.put("data", gson.fromJson(valueObject.get("data").getAsJsonObject(), ExChildFunctionResultData.class));
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
    }

    public ExChildFunctionResultData getData(){
        return (ExChildFunctionResultData)this.value.get("data");
    }

    public boolean getResult(){
        if((Integer)this.value.get("result") == 0){
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return null;
    }
}
