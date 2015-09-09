package net.sh4869.extensionandroidapp.websokcetdata;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nobuhiro on 2015/09/03.
 */
public class ExAuthResultWebSocketMessage extends ExBaseWebSocketMessage {

    // Message Value
    public Map value = new HashMap<String, Object>();

    public ExAuthResultWebSocketMessage(int result, String ErrorMessage) {
        this.type = "webAuth";
        this.value.put("result", result);
        this.value.put("error", ErrorMessage);
    }

    /// Return authResult
    public boolean authResult() {
        if ((Double) this.value.get("result") == 0) {
            return true;
        }
        return false;
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
