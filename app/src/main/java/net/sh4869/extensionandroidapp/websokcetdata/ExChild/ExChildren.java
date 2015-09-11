package net.sh4869.extensionandroidapp.websokcetdata.ExChild;

import net.sh4869.extensionandroidapp.utility.IterableMap;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by Nobuhiro on 2015/09/08.
 */
public class ExChildren implements IterableMap<String, ExChild> {

    /// Map of GUID String and ExChild Class
    public Map<String, ExChild> commands;

    public ExChildren(Map<String, ExChild> commands) {
        this.commands = commands;
    }

    @Override
    public Iterator<Map.Entry<String, ExChild>> iterator() {
        return this.commands.entrySet().iterator();
    }
}
