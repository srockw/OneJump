package io.srock.onejump.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.wrappers.Rotation;
import io.srock.onejump.data.Jump;
import io.srock.onejump.data.JumpData;
import io.srock.onejump.inventory.JumpInventory;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class OneJumpCommand {
  public static final String EDIT_PERMISSION = "onejump.edit";

  private static final Argument<String> jumpNameArgument = new GreedyStringArgument("name")
    .replaceSuggestions(ArgumentSuggestions.strings((sender) -> {
      return JumpData.getJumps().stream().map(Jump::name).toList().toArray(new String[0]);
    }));

  private static final Argument<Integer> pageArgument = new IntegerArgument("page", 1);

  private static final CommandAPICommand add = new CommandAPICommand("add")
    .withPermission(EDIT_PERMISSION)
    .withArguments(new IntegerArgument("difficulty", 0))
    .withArguments(new LocationArgument("spawn", LocationType.PRECISE_POSITION))
    .withArguments(new RotationArgument("angle").replaceSuggestions(ArgumentSuggestions.strings(
      "~",
      "~ ~"
    )))
    .withArguments(new GreedyStringArgument("name"))
    .executes((sender, args) -> {
      var name = args.get("name").toString();

      if (JumpData.exists(name)) {
        sender.sendMessage("§cA jump with the name §b\"%s\" §calready exists.".formatted(name));
        return;
      }

      var diff = (int) args.get("difficulty");
      var angle = (Rotation) args.get("angle");
      var spawn = (Location) args.get("spawn");
      spawn.setYaw(angle.getYaw());
      spawn.setPitch(angle.getPitch());

      JumpData.add(new Jump(diff, name, spawn));
      sender.sendMessage("§aAdded jump §b\"%s\"§a.".formatted(name));
    });

  private static final CommandAPICommand remove = new CommandAPICommand("remove")
    .withPermission(EDIT_PERMISSION)
    .withArguments(jumpNameArgument)
    .executes((sender, args) -> {
      var name = args.get("name").toString();
      var jump = JumpData.remove(name);

      if (jump != null) {
        sender.sendMessage("§aRemoved jump §b\"%s\" §asuccessfully.".formatted(name));
      } else {
        sender.sendMessage("§cJump §b\"%s\" §cdoesn't exist.".formatted(name));
      }
    });

  private static final CommandAPICommand warp = new CommandAPICommand("warp")
    .withArguments(jumpNameArgument)
    .executes((sender, args) -> {
      if (sender instanceof Player player) {
        var name = args.get("name").toString();
        var jump = JumpData.get(name);

        if (jump != null) {
          player.sendMessage("§aTeleporting to §b\"%s\"§a.".formatted(name));
          player.teleport(jump.spawn());
        } else {
          player.sendMessage("§cJump §b\"%s\" §cdoesn't exist.".formatted(name));
        }
      }
    });

  public static final CommandAPICommand menu = new CommandAPICommand("menu")
    .withOptionalArguments(pageArgument)
    .executes((sender, args) -> {
      if (sender instanceof Player player) {
        player.openInventory(new JumpInventory(
          (int) args.getOrDefault("page", 1) - 1
        ).getInventory());
      }
    });

  public static void register() {
    new CommandAPICommand("onejump")
      .withSubcommands(menu, warp, add, remove)
      .register();
  }
}
