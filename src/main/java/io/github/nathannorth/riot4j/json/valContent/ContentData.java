package io.github.nathannorth.riot4j.json.valContent;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

/**
 * wraps a ContentDto
 */
@Value.Immutable
@JsonSerialize(as = ImmutableContentData.class)
@JsonDeserialize(as = ImmutableContentData.class)
public interface ContentData {
    String version();
    List<ContentItemData> characters();
    List<ContentItemData> maps();
    List<ContentItemData> chromas();
    List<ContentItemData> skins();
    List<ContentItemData> skinLevels();
    List<ContentItemData> equips();
    List<ContentItemData> gameModes();
    List<ContentItemData> sprays();
    List<ContentItemData> sprayLevels();
    List<ContentItemData> charms();
    List<ContentItemData> charmLevels();
    List<ContentItemData> playerCards();
    List<ContentItemData> playerTitles();
    List<ActData> acts();
}
