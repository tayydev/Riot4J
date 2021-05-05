package io.github.nathannorth.riotWrapper.json.platform;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonSerialize(as = ImmutableUpdateData.class)
@JsonDeserialize(as = ImmutableUpdateData.class)
public interface UpdateData {
    int id();
    String author();
    boolean publish();
    List<String> publish_locations();
    List<ContentData> translations();
    String created_at();
    String updated_at();
}
