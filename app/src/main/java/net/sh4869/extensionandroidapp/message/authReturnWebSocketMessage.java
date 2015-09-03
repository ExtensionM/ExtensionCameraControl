package net.sh4869.extensionandroidapp.message;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nobuhiro on 2015/09/03.
 */
public class authReturnWebSocketMessage extends baseWebSocketMessage {
    // Message Type
    public String type = "webAuth";

    // Message Value
    public Map value = new HashMap<String,Object>();

    public authReturnWebSocketMessage(int result,String ErrorMessage){
        this.value.put("result",result);
        this.value.put("error",ErrorMessage);
    }

    /// Return authResult
    public boolean authResult(){
        if(this.value.containsKey("result")){
            if((int)this.value.get("result") == 0){
                return true;
            } else {
                return false;
            }
        } else {
            throw new IllegalStateException("this instance don't have result value");
        }
    }

    /// Return Auth Error Message
    public String getErrorMessage(){
        if(this.value.containsKey("error")){
            return this.value.get("error").toString();
        } else {
            throw new IllegalStateException("this instance don't have error value");
        }
    }

    public String toString(){
        return null;
    }
}
