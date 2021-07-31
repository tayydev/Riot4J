package io.github.nathannorth.riot4j.objects;

import io.github.nathannorth.riot4j.json.valContent.ActData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValActIdGroup {
    private final Map<Integer, Map<Integer, ValActId>> internalMap = new HashMap<>();
    private final List<ActData> data;
    public ValActIdGroup(List<ActData> data) {
        this.data = data;

        for(ActData episode: data) { //loop through unsorted acts
            if(episode.type().equals("episode") && !episode.name().equals("Closed Beta")) { //ignore closed beta
                HashMap<Integer, ValActId> ids = new HashMap<>();
                for(ActData act: data) { //loop through the same unsorted acts again
                    if(act.parentId().equals(episode.id())) { //look for acts with the correct parentId
                        int num = Integer.parseInt(act.name().substring(4)); //todo this should be regex
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
    public ValActId getLatestActId() { //todo this returns nonexistent acts
        for(ActData id: data) {
            if(id.type().equals("act") && id.isActive()) {
                return ValActId.createUnvalidated(id.id());
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "ValActIdSet{" +
                "internalMap=" + internalMap +
                '}';
    }
}
