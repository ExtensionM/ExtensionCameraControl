package net.sh4869.extensionandroidapp.message;

import com.google.gson.Gson;

import java.util.Objects;

/**
 * Created by Nobuhiro on 2015/09/03.
 */
public class webSocketMessage extends baseWebSocketMessage {

    // Message Value
    public Object value;

    public webSocketMessage(String type,Object value) {
        this.type = type;
        this.value = value;
    }

    public String toString() {
        Gson gson = new Gson();
        String messageString = gson.toJson(this);
        return messageString;
    }
}
