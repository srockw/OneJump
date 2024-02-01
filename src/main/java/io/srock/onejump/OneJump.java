package io.srock.onejump;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import io.srock.onejump.commands.OneJumpCommand;
import io.srock.onejump.data.Jump;
import io.srock.onejump.data.JumpData;
import io.srock.onejump.inventory.JumpInventory;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class OneJump extends JavaPlugin implements Listener {
  public static final String PLUGIN_ID = "onejump";

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
    getServer().getPluginManager().registerEvents(this, this);
  }

  @Override
  public void onDisable() {
    CommandAPI.unregister("onejump");
    CommandAPI.onDisable();
    JumpData.save();
  }

  @EventHandler
  public void onInventoryClicked(InventoryClickEvent event) {
    var inventory = event.getInventory();

    if (inventory.getHolder(false) instanceof JumpInventory jumpInventory) {
      jumpInventory.onClick(event);
    }
  }
}
