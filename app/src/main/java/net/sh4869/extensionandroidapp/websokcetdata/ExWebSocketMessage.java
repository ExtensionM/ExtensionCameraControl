package net.sh4869.extensionandroidapp.websokcetdata;

import com.google.gson.Gson;

/**
 * Created by Nobuhiro on 2015/09/03.
 */
public class ExWebSocketMessage extends ExBaseWebSocketMessage {

    // Message Value
    public Object value;

    public ExWebSocketMessage(String type, Object value) {
        this.type = type;
        this.value = value;
    }

    public String toString() {
        Gson gson = new Gson();
        String messageString = gson.toJson(this);
        return messageString;
    }
}
