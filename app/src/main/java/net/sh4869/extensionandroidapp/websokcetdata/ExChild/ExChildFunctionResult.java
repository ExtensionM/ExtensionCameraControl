package net.sh4869.extensionandroidapp.websokcetdata.ExChild;

import java.util.Objects;

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
    public Object error;

    /**
     * result of Function
     */
    public Object result;
}
