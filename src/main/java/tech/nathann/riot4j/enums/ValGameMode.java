package tech.nathann.riot4j.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ValGameMode {
    BOMB("/Game/GameModes/Bomb/BombGameMode.BombGameMode_C"),
    DEATHMATCH("/Game/GameModes/Deathmatch/DeathmatchGameMode.DeathmatchGameMode_C"),
    ESCALATION("/Game/GameModes/GunGame/GunGameTeamsGameMode.GunGameTeamsGameMode_C"),
    REPLICATION("/Game/GameModes/OneForAll/OneForAll_GameMode.OneForAll_GameMode_C"),
    SPIKE_RUSH("/Game/GameModes/QuickBomb/QuickBombGameMode.QuickBombGameMode_C"),
    SNOWBALL_FIGHT("/Game/GameModes/SnowballFight/SnowballFightGameMode.SnowballFightGameMode_C"); //todo default val?

    private final String value;

    ValGameMode(String value) {
        this.value = value;
    }


    @Override
    @JsonValue
    public String toString() {
        return value;
    }
}
