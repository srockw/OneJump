package io.srock.onejump.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.wrappers.Rotation;
import io.srock.onejump.data.Jump;
import io.srock.onejump.data.JumpData;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class OneJumpCommand {
  public static final String EDIT_PERMISSION = "onejump.edit";

  private static final Argument jumpNameArgument = new GreedyStringArgument("name")
    .replaceSuggestions(ArgumentSuggestions.strings((sender) -> {
      return JumpData.getJumps().stream().map(it -> it.name()).toList().toArray(new String[0]);
    }));

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

      var jump = new Jump(diff, name, spawn);
      JumpData.add(jump);
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
          sender.sendMessage("§aTeleporting to §b\"%s\"§a.".formatted(name));
          player.teleport(jump.spawn());
        } else {
          sender.sendMessage("§cJump §b\"%s\" §cdoesn't exist.".formatted(name));
        }
      }
    });

  private static final CommandAPICommand list = new CommandAPICommand("list")
    .withOptionalArguments(new IntegerArgument("page", 1))
    .executes((sender, args) -> {
      var jumps = JumpData.getJumps();

      if (jumps.size() < 1) {
        sender.sendMessage("§cNo jumps are available.");
        return;
      }
      var page = (int) args.getOrDefault("page", 0);
      var maxPage = (int) Math.ceil(jumps.size() / 10);

      if (page > maxPage) {
        page = maxPage;
      }

      var offset = page * 10;
      var lines = new ArrayList<String>();

      for (int i = offset; i < jumps.size(); i++) {
        lines.add("  §6%d §7- §b\"%s\"".formatted(i - offset + 1, jumps.get(i).name()));
      }

      sender.sendMessage("§9Onejumps available §e(page %d)".formatted(page + 1));
      sender.sendMessage(lines.toArray(new String[0]));
    });

  public static void register() {
    new CommandAPICommand("onejump")
      .withSubcommands(warp, list, add, remove)
      .register();
  }
}
