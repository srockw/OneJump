package io.srock.onejump.data;

import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JumpData {
  private static final File dataFile = new File("./plugins/onejump/jumps.yml");
  private static final ArrayList<Jump> jumps = new ArrayList<>();
  private static int nextJumpId = 0;
  private static YamlConfiguration config;

  public static void load() {
    config = YamlConfiguration.loadConfiguration(dataFile);
    var data = config.get("jumps");

    if (data != null) {
      jumps.addAll((List<Jump>) data);
      nextJumpId = jumps.size();
    }
  }

  public static void save() {
    if (config != null) {
      try {
        config.set("jumps", jumps);
        config.save(dataFile);
      } catch (Exception ignored) {
      }
    }
  }

  public static List<Jump> getJumps() {
    return jumps;
  }

  @Nullable
  public static Jump get(int id) {
    for (Jump jump : jumps) {
      if (id == jump.id()) return jump;
    }

    return null;
  }

  @Nullable
  public static Jump get(String name) {
    name = name.toLowerCase();

    for (Jump jump : jumps) {
      if (jump.name().toLowerCase().equals(name)) {
        return jump;
      }
    }

    return null;
  }

  public static void add(Jump jump) {
    jumps.add(jump);
  }

  @Nullable
  public static Jump remove(String name) {
    var jump = get(name);

    if (jump != null) {
      jumps.remove(jump);
      nextJumpId = jump.id();
      return jump;
    }

    return null;
  }

  public static boolean exists(String name) {
    return get(name) != null;
  }

  public static int getNextJumpId() {
    while (get(nextJumpId) != null) {
      nextJumpId++;
    }
    return nextJumpId;
  }
}
