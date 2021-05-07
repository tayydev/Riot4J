package io.github.nathannorth.riotWrapper;

import io.github.nathannorth.riotWrapper.json.valContent.ActData;
import io.github.nathannorth.riotWrapper.json.valContent.ContentData;
import io.github.nathannorth.riotWrapper.util.Exceptions;

public class TestingHelper {
    public static ActData getAct(int episode, int act, ContentData contentData) {
        String parentId = null;
        for(ActData data: contentData.acts()) {
            if(data.type().equals("episode") && data.name().contains(episode + "")) {
                parentId = data.id();
                break;
            }
        }
        for(ActData data: contentData.acts()) {
            if(data.parentId().equals(parentId) && data.name().contains(act + "")) {
                return data;
            }
        }
        return null;
    }
}
