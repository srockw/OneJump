package io.srock.onejump.data;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record Jump(int id, int difficulty, String name, Location spawn) implements ConfigurationSerializable {
  public Jump(int difficulty, String name, Location spawn) {
    this(JumpData.getNextJumpId(), difficulty, name, spawn);
  }

  public Material getMaterial() {
    return switch (this.difficulty) {
      case 1 -> Material.LIGHT_BLUE_CONCRETE;
      case 2 -> Material.LIME_CONCRETE;
      case 3 -> Material.GREEN_CONCRETE;
      case 4 -> Material.YELLOW_CONCRETE;
      case 5 -> Material.ORANGE_CONCRETE;
      case 6 -> Material.RED_CONCRETE;
      case 7 -> Material.PINK_CONCRETE;
      case 8 -> Material.PURPLE_CONCRETE;
      case 9 -> Material.LIGHT_GRAY_CONCRETE;
      default -> Material.BLACK_CONCRETE;
    };
  }

  public int getColor() {
    return switch (this.difficulty) {
      case 1 -> 0x3AB3DA;
      case 2 -> 0x80C71F;
      case 3 -> 0x5E7C16;
      case 4 -> 0xFED83D;
      case 5 -> 0xF9801D;
      case 6 -> 0xB02E26;
      case 7 -> 0xF38BAA;
      case 8 -> 0x8932B8;
      case 9 -> 0x9D9D97;
      default -> 0x1D1D21;
    };
  }

  @Override
  public @NotNull Map<String, Object> serialize() {
    return Map.of(
      "id", id,
      "difficulty", difficulty,
      "name", name,
      "spawn", spawn.serialize()
    );
  }

  public static Jump deserialize(Map<String, Object> data) {
    return new Jump(
      (int) data.get("id"),
      (int) data.get("difficulty"),
      data.get("name").toString(),
      Location.deserialize((Map<String, Object>) data.get("spawn"))
    );
  }
}
