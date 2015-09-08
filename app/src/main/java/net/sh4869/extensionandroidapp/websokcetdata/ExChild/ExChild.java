package net.sh4869.extensionandroidapp.websokcetdata.ExChild;

import net.sh4869.extensionandroidapp.utility.IterableMap;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by Nobuhiro on 2015/09/08.
 */
public class ExChild implements IterableMap<String,ExChildFunction> {

    /// Functions
    public Map<String,ExChildFunction> functions;

    /// name of Children
    public String name;

    @Override
    public Iterator<Map.Entry<String,ExChildFunction>> iterator(){
        return this.functions.entrySet().iterator();
    }

}
