package net.sh4869.extensionandroidapp.websokcetdata.ExChild;

import net.sh4869.extensionandroidapp.websokcetdata.ExChildListMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nobuhiro on 2015/09/08.
 */
public class ExChildFinder {

    public static ExChildren searchChildren(ExChildListMessage message, String FindChildName) {
        Map<String, ExChild> childrenMap = new HashMap<>();
        ExChildren sourceChildren = (ExChildren) message.value.get("commands");
        for (Map.Entry<String, ExChild> childMap : sourceChildren) {
            ExChild child = childMap.getValue();
            if (child.name.equals(FindChildName)) {
                // You can't use putAll because
                childrenMap.put(childMap.getKey(), childMap.getValue());
            }
        }
        return new ExChildren(childrenMap);
    }

    public static ExChildren searchChildren(ExChildren children, String childrenName) {
        Map<String, ExChild> childrenMap = new HashMap<>();
        for (Map.Entry<String, ExChild> childMap : children) {
            ExChild child = childMap.getValue();
            if (child.name == childrenName) {
                // You can't use putAll because
                childrenMap.put(childMap.getKey(), childMap.getValue());
            }
        }
        return new ExChildren(childrenMap);
    }
}
