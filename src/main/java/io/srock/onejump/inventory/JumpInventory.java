package io.srock.onejump.inventory;

import io.srock.onejump.data.JumpData;
import io.srock.onejump.item.Items;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class JumpInventory implements InventoryHolder {
  private final Inventory inventory;
  private final ItemStack sortItem;
  private final SortType sortType;
  private final int page;

  public JumpInventory(int page, SortType sortType) {
    this.page = page;
    this.sortItem = Items.createSortItem(sortType);
    this.sortType = sortType;

    var jumps = sortType.sort(JumpData.getJumps());
    var maxPages = (int) Math.ceil((double) jumps.size() / 45);

    if (page > maxPages - 1) {
      page = maxPages - 1;
    }

    inventory = Bukkit.createInventory(
      this, 54,
      Component.text("Onejump Menu (%d/%d)".formatted(page + 1, maxPages))
    );

    int jumpIndex = page * 45;
    for (int i = 0; i < 54; i++) {
      if (i < 45) {
        inventory.setItem(i, jumpIndex < jumps.size() ? Items.createJumpItem(jumps.get(jumpIndex++)) : Items.MENU_EMPTY);
      } else {
        inventory.setItem(i, Items.MENU_BORDER);
      }
    }

    if (page < maxPages - 1) {
      inventory.setItem(53, Items.MENU_NEXT_PAGE);
    }

    if (page > 0) {
      inventory.setItem(45, Items.MENU_PREVIOUS_PAGE);
    }

    inventory.setItem(49, sortItem);
  }

  public JumpInventory(int page) {
    this(page, SortType.ID);
  }

  public void onClick(InventoryClickEvent event) {
    event.setCancelled(true);
    var item = event.getCurrentItem();

    if (item != null) {
      var id = item.getItemMeta().getPersistentDataContainer().get(Items.JUMP_ID_KEY, PersistentDataType.INTEGER);

      if (id != null) {
        var jump = JumpData.get(id);

        if (jump != null) {
          event.getWhoClicked().teleport(jump.spawn());
          return;
        }
      }

      JumpInventory newInventory = null;

      if (item.equals(Items.MENU_NEXT_PAGE)) {
        newInventory = new JumpInventory(page + 1, sortType);
      } else if (item.equals(Items.MENU_PREVIOUS_PAGE)) {
        newInventory = new JumpInventory(page - 1, sortType);
      } else if (item.equals(sortItem)) {
        newInventory = new JumpInventory(0, sortType.next());
      }

      if (newInventory != null) {
        event.getWhoClicked().openInventory(newInventory.getInventory());
      }
    }
  }

  @Override
  public @NotNull Inventory getInventory() {
    return inventory;
  }
}
