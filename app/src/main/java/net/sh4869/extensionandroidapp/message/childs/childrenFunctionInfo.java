package net.sh4869.extensionandroidapp.message.childs;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by Nobuhiro on 2015/09/08.
 */
public class childrenFunctionInfo {

    /// Args of this function
    public List<childrenFunctionArg> args;

    /// Description of Function
    public String desc;

    /// Name of Display
    public String name;

    /// Permision of This Function
    public String perm;

    /** Whether you can call this function or not */
    public boolean status;

    /// Whether this function hope to call auto or not
    public boolean auto;

    /// Whether push or not
    public boolean push;
    /// Sync or not return value
    public boolean sync;

    /// Result of this Funtion
    public childrenFunctionArg result;

}
