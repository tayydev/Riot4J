package tech.nathann.riot4j.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.StringJoiner;

public enum ValGameMode {
    BOMB("/Game/GameModes/Bomb/BombGameMode.BombGameMode_C"),
    DEATHMATCH("/Game/GameModes/Deathmatch/DeathmatchGameMode.DeathmatchGameMode_C"),
    ESCALATION("/Game/GameModes/GunGame/GunGameTeamsGameMode.GunGameTeamsGameMode_C"),
    REPLICATION("/Game/GameModes/OneForAll/OneForAll_GameMode.OneForAll_GameMode_C"),
    SPIKE_RUSH("/Game/GameModes/QuickBomb/QuickBombGameMode.QuickBombGameMode_C"),
    SNOWBALL_FIGHT("/Game/GameModes/SnowballFight/SnowballFightGameMode.SnowballFightGameMode_C"),
    NEW_MAP("/Game/GameModes/NewMap/NewMapGameMode.NewMapGameMode_C"),
    SWIFT_PLAY("/Game/GameModes/_Development/Swiftplay_EndOfRoundCredits/Swiftplay_EoRCredits_GameMode.Swiftplay_EoRCredits_GameMode_C"); //todo default val?

    private final String value;

    ValGameMode(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
        return value;
    }

    public String prettyName() {
        StringJoiner returnable = new StringJoiner(" ");
        String[] words = name().split("_");
        for(String word: words) {
            returnable.add(word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase());
        }
        return returnable.toString();
    }
}
