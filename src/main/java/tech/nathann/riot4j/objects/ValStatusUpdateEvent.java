package tech.nathann.riot4j.objects;

import tech.nathann.riot4j.json.valPlatform.PlatformStatusData;

import java.util.Optional;

public class ValStatusUpdateEvent {
    private final PlatformStatusData oldData;
    private final PlatformStatusData newData;

    public ValStatusUpdateEvent(PlatformStatusData oldData, PlatformStatusData newData) {
        this.oldData = oldData;
        this.newData = newData;
    }

    public boolean isRemove() {
        if(oldData == null) return false;
        return oldData.incidents().size() + oldData.incidents().size() > newData.incidents().size() + newData.maintenances().size();
    }
    public boolean isEmpty() {
        return newData.maintenances().size() == 0 && newData.incidents().size() == 0;
    }
    public boolean isMaintenance() {
        return newData.maintenances().size() > 0;
    }
    public boolean isIncident() {
        return newData.incidents().size() > 0;
    }
    public PlatformStatusData getNew() {
        return newData;
    }
    public Optional<PlatformStatusData> getOld() {
        return Optional.ofNullable(oldData);
    }

    @Override
    public String toString() {
        return "ValStatusUpdateEvent{" +
                "oldData=" + oldData +
                ", newData=" + newData +
                '}';
    }
}
