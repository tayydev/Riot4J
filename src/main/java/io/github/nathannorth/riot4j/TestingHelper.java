package io.github.nathannorth.riot4j;

import io.github.nathannorth.riot4j.json.valContent.ActData;
import io.github.nathannorth.riot4j.json.valContent.ContentData;

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
