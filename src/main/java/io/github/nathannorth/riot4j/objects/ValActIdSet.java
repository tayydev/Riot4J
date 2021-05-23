package io.github.nathannorth.riot4j.objects;

import io.github.nathannorth.riot4j.json.valContent.ActData;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValActIdSet {
    private final Map<Integer, Map<Integer, ValActId>> internalMap = new HashMap<>();
    public ValActIdSet(List<ActData> data) {
        for(ActData episode: data) {
            if(episode.type().equals("episode") && !episode.name().equals("Closed Beta")) { //ignore closed beta
                HashMap<Integer, ValActId> ids = new HashMap<>();
                for(ActData act: data) {
                    if(act.parentId().equals(episode.id())) {
                        int num = Integer.parseInt(act.name().substring(4));
                        ids.put(num, ValActId.createUnvalidated(act.id()));
                    }
                }
                int num = Integer.parseInt(episode.name().substring(8));
                internalMap.put(num, ids);
            }
        }
    }

    /**
     * Gets an act id from a given episode and act
     * @param episode
     * @param act
     * @return ValActId, will return null if episode and act dont exist
     */
    public ValActId getActId(int episode, int act) {
        Map<Integer, ValActId> epMap = internalMap.get(episode);
        if(epMap == null) return null;
        else return epMap.get(act);
    }
    public ValActId getLatestActId() {
        int episodeMax = Collections.max(internalMap.keySet());
        int actMax = Collections.max(internalMap.get(episodeMax).keySet());
        return internalMap.get(episodeMax).get(actMax);
    }
}
