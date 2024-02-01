package io.srock.onejump.item;

import io.srock.onejump.OneJump;
import io.srock.onejump.data.Jump;
import io.srock.onejump.inventory.SortType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionType;

import java.util.List;
import java.util.function.Consumer;

public class Items {
  public static final NamespacedKey JUMP_ID_KEY = new NamespacedKey(OneJump.PLUGIN_ID, "jump_id");

  public static final ItemStack MENU_BORDER = createUnnamedItem(Material.BLACK_STAINED_GLASS_PANE);
  public static final ItemStack MENU_EMPTY = createUnnamedItem(Material.BARRIER);
  public static final ItemStack MENU_NEXT_PAGE = createTippedArrow(PotionType.LUCK, "Next Page", 0x59C106);
  public static final ItemStack MENU_PREVIOUS_PAGE = createTippedArrow(PotionType.INSTANT_HEAL, "Previous Page", 0xF82423);

  public static ItemStack createItem(Material material, Consumer<ItemMeta> modifier) {
    var item = new ItemStack(material);
    item.editMeta(modifier);
    return item;
  }

  public static ItemStack createNamedItem(Material material, String name, int color) {
    return createItem(material, meta -> meta.displayName(Component.text(name)
      .color(TextColor.color(color))
      .decoration(TextDecoration.ITALIC, false))
    );
  }

  public static ItemStack createTippedArrow(PotionType type, String name, int color) {
    var item = createNamedItem(Material.TIPPED_ARROW, name, color);
    item.editMeta(meta -> {
      ((PotionMeta) meta).setBasePotionType(type);
      meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
    });
    return item;
  }

  public static ItemStack createUnnamedItem(Material material) {
    return createItem(material, meta -> meta.displayName(Component.empty()));
  }

  public static ItemStack createJumpItem(Jump jump) {
    var item = createNamedItem(jump.getMaterial(), "Jump %d".formatted(jump.id() + 1), jump.getColor());

    item.editMeta(meta -> {
      meta.lore(List.of(
        Component.empty(),
        Component.text("§8Name » §b%s".formatted(jump.name())),
        Component.text("§8Difficulty » §b%d".formatted(jump.difficulty()))
      ));

      meta.getPersistentDataContainer().set(JUMP_ID_KEY, PersistentDataType.INTEGER, jump.id());
    });

    return item;
  }

  public static ItemStack createSortItem(SortType current) {
    var item = createNamedItem(Material.HOPPER, "Sort Type", Color.ORANGE.asRGB());
    var name = current.name().substring(0, 1).toUpperCase() + current.name().substring(1).toLowerCase();

    item.editMeta(meta -> meta.lore(List.of(
      Component.empty(),
      Component.text("§8Current » §b%s".formatted(name)),
      Component.text("§bClick to cycle!")
    )));

    return item;
  }
}
