package MiniCash.miniCashwerewolf.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

import static MiniCash.miniCashwerewolf.MiniCashWereWolf.guicheck;

public class ShopGUI {

    public static void openshopgui(Player player){

        player.sendMessage("openshopguiメソッドが呼び出されたことのチェック");

        Inventory shop = Bukkit.createInventory(null,27,"shop");  //サイズ9*○○



        ItemStack test = new ItemStack(Material.DIAMOND_AXE, 1);

        ItemMeta testmeta = test.getItemMeta();
        testmeta.setDisplayName("test斧");
        testmeta.setLore(List.of("§6右クリックで使用可能"));

        test.setItemMeta(testmeta); //アイテムメタを設定


        //アイテムセット
        shop.setItem(0,test);



        // プレイヤーにGUIを表示
        player.openInventory(shop);

        //Map
        UUID id = player.getUniqueId();
        guicheck.put(id,1);  //イベントリセット






    }

}
