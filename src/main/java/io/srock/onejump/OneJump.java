package io.srock.onejump;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import io.srock.onejump.commands.OneJumpCommand;
import io.srock.onejump.data.Jump;
import io.srock.onejump.data.JumpData;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

public final class OneJump extends JavaPlugin {
  @Override
  public void onLoad() {
    CommandAPI.onLoad(new CommandAPIBukkitConfig(this));
  }

  @Override
  public void onEnable() {
    ConfigurationSerialization.registerClass(Jump.class);
    CommandAPI.onEnable();
    Bukkit.getScheduler().runTask(this, () -> {
      JumpData.load();
      OneJumpCommand.register();
    });
  }

  @Override
  public void onDisable() {
    CommandAPI.unregister("onejump");
    CommandAPI.onDisable();
    JumpData.save();
  }
}
