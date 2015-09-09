package net.sh4869.extensionandroidapp.message;

import net.sh4869.extensionandroidapp.R;

/**
 * Created by Nobuhiro on 2015/09/09.
 */
public enum HandlerMessageCodeEnum {
    LOGIN_SUCCESS(1,"Login Complete!"),
    LOGIN_FAILED(2,"Login Failed"),
    CHILD_FOUND(3,"Target Child is found"),
    CHILD_FOUND_MULTIPLE(4,"Target Children are found"),
    CHILD_NOT_FOUND(5,"Target Children not fount"),
    CALL_FAIL(6,"fail to call function");

    private String message;
    final private int codeNumber;

    private HandlerMessageCodeEnum(int num,String message){
        this.codeNumber = num;
        this.message = message;
    }

    final public int codeNumber(){
        return this.codeNumber;
    }
    public String message(){
        return this.message;
    }
}
