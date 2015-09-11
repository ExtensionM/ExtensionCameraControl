package net.sh4869.extensionandroidapp.websokcetdata.ExChild;

/**
 * Created by Nobuhiro on 2015/09/10.
 */
public class ExChildFunctionResult {
    /**
     * Fucntion name
     */
    public String functionName;

    /**
     * Whether Function has error or not
     */
    public boolean hasError;

    /**
     * Whether Function was cancelled or not
     */
    public boolean cancelled;

    /**
     * Error Object
     */
    public ExChildFunctionResultError error;

    /**
     * result of Function
     */
    public Object result;
}
