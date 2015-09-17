package net.sh4869.extensionandroidapp.websokcetdata;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nobuhiro on 2015/09/03.
 */
public class ExAuthResultWebSocketMessage extends ExBaseWebSocketMessage {

    /**
     * Value of Message
     */
    public Map<String,Object> value = new HashMap<String, Object>();

    /**
     * Default constructor
     * @param result auth Result
     * @param ErrorMessage Error Message of This Auth
     */
    public ExAuthResultWebSocketMessage(int result, String ErrorMessage) {
        this.type = "webAuth";
        this.value.put("result", result);
        this.value.put("error", ErrorMessage);
    }

    /// Return authResult
    public boolean authResult() {
        return (Double) this.value.get("result") == 0;
    }

    /// Return Auth Error Message
    public String getErrorMessage() {
        if (this.value.containsKey("error")) {
            return this.value.get("error").toString();
        } else {
            return null;
        }
    }

    public String toString() {
        return null;
    }
}
