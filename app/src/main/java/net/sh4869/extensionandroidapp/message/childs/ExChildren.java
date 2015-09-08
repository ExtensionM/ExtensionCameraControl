package net.sh4869.extensionandroidapp.message.childs;

import java.util.Iterator;
import java.util.Map;

import net.sh4869.extensionandroidapp.utility.IterableMap;

/**
 * Created by Nobuhiro on 2015/09/08.
 */
public class ExChildren implements IterableMap<String,ExChild> {

    /// Map of GUID String and ExChild Class
    public Map<String,ExChild> commands;

    @Override
    public Iterator<Map.Entry<String,ExChild>> iterator(){
        return this.commands.entrySet().iterator();
    }
}
