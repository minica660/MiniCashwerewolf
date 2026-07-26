package MiniCash.miniCashwerewolf.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

// インベントリ判別用のラベルとして使うだけのクラス
public class VoteGuiHolder implements InventoryHolder {
    @Override
    public Inventory getInventory() { return null; }
}

