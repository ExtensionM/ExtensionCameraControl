package net.sh4869.extensionandroidapp.websokcetdata;

/**
 * Created by Nobuhiro on 2015/08/28.
 * Base Abstract Class Of Websoket Message
 */
public abstract class ExBaseWebSocketMessage {
    public String type;

    /**
     * convert message to String
     */
    public abstract String toString();
}
