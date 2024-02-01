package io.srock.onejump.data;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record Jump(int difficulty, String name, Location spawn) implements ConfigurationSerializable {
  @Override
  public @NotNull Map<String, Object> serialize() {
    return Map.of(
      "difficulty", difficulty,
      "name", name,
      "spawn", spawn.serialize()
    );
  }

  public static Jump deserialize(Map<String, Object> data) {
    return new Jump(
      (int) data.get("difficulty"),
      data.get("name").toString(),
      Location.deserialize((Map<String, Object>) data.get("spawn"))
    );
  }
}
