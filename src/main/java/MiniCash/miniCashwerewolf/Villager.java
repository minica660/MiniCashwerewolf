package MiniCash.miniCashwerewolf;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class Villager {
    private static MiniCashWereWolf plugin = null;
    public Villager(MiniCashWereWolf plugin){
        Villager.plugin = plugin;
    }
    //村人スポーン
    public static void villagerSpawn(Player player, Location location){
        NamespacedKey namekey = new NamespacedKey(plugin,"villagergui");
        String vid = "villager";
        org.bukkit.entity.Villager villager = location.getWorld().spawn(location, org.bukkit.entity.Villager.class);


        villager.getPersistentDataContainer().set(
                namekey,
                PersistentDataType.STRING,
                vid
        );

        villager.setAI(false);        // 動かない
        villager.setInvulnerable(true); // 無敵
        villager.setCollidable(false);  // 押されない
        villager.setSilent(true);       // 音を出さない
        villager.setProfession(org.bukkit.entity.Villager.Profession.NONE);


        String id = villager.getPersistentDataContainer().get(
                namekey,
                PersistentDataType.STRING
        );

        if ("villager".equals(id)){

            List<MerchantRecipe> recipes = new ArrayList<>();

            //販売アイテム
            ItemStack nitem1 = new ItemStack(Material.COOKED_BEEF, 2);
            ItemStack diamondsword = new ItemStack(Material.DIAMOND_SWORD, 1);

            MerchantRecipe recipe = new MerchantRecipe(
                    new ItemStack(nitem1), //品物
                    9999 // 使用回数（実質無限）
            );
            MerchantRecipe recipe2 = new MerchantRecipe(
                    new ItemStack(GameItem.createItem("pcheck",1)), //品物
                    9999 // 使用回数（実質無限）
            );
            MerchantRecipe recipe3 = new MerchantRecipe(
                    new ItemStack(diamondsword), //品物
                    9999 // 使用回数（実質無限）
            );
            MerchantRecipe recipe4 = new MerchantRecipe(
                    new ItemStack(GameItem.createItem("glowing",1)), //品物
                    9999 // 使用回数（実質無限）
            );
            MerchantRecipe recipe5 = new MerchantRecipe(
                    new ItemStack(GameItem.createItem("speed",1)), //品物
                    9999 // 使用回数（実質無限）
            );
            MerchantRecipe recipe6 = new MerchantRecipe(
                    new ItemStack(GameItem.createItem("invisibility",1)), //品物
                    9999 // 使用回数（実質無限）
            );
            MerchantRecipe recipe7 = new MerchantRecipe(
                    new ItemStack(GameItem.createItem("smoke",1)), //品物
                    9999 // 使用回数（実質無限）
            );



            ItemStack cost4 = GameItem.createItem("coin",4);
            ItemStack cost6 = GameItem.createItem("coin",6);
            ItemStack cost10 = GameItem.createItem("coin",10);
            ItemStack cost15 = GameItem.createItem("coin",15);



//                //取引必要アイテムコピー
//                ItemStack cost1 = spawngolditem.clone();
//
//                cost1.setAmount(4);   //コイン必要数4枚の場合

            recipe.addIngredient(cost4); // 必要アイテム
            recipe2.addIngredient(cost10);
            recipe3.addIngredient(cost6);
            recipe4.addIngredient(cost10);
            recipe5.addIngredient(cost6);
            recipe6.addIngredient(cost15);
            recipe7.addIngredient(cost6);

            recipes.add(recipe);
            recipes.add(recipe2);
            recipes.add(recipe3);
            recipes.add(recipe4);
            recipes.add(recipe5);
            recipes.add(recipe6);
            recipes.add(recipe7);
            villager.setRecipes(recipes);

            player.sendMessage(
                    MiniCashWereWolf.getMessage(
                            Component.text("人狼プラグイン用村人を召喚しました").color(NamedTextColor.GOLD)
                    )
            );

        }



    }


}
