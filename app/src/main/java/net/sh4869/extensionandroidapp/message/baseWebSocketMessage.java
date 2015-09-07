package net.sh4869.extensionandroidapp.message;

/**
 * Created by Nobuhiro on 2015/08/28.
 * Base Abstract Class Of Websoket Message
 */
public abstract class baseWebSocketMessage {
    public String type;
    /** convert message to String */
    public abstract String toString();
}
