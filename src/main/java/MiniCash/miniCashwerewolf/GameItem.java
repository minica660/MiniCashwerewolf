package MiniCash.miniCashwerewolf;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class GameItem {

    private static MiniCashWereWolf plugin;

    public GameItem(MiniCashWereWolf plugin){
        GameItem.plugin = plugin;
    }

    public static ItemStack createItem(String itemn , int amount){


        NamespacedKey namekey = new NamespacedKey(plugin,"wolfitem");
        //人狼
        if (itemn.equals("wolf")){

            ItemStack wolfitem = new ItemStack(Material.DIAMOND_AXE, 1);

            ItemMeta wolfitemmeta = wolfitem.getItemMeta();
            wolfitemmeta.setDisplayName("§c人狼の斧");
            wolfitemmeta.setUnbreakable(true);
            NamespacedKey keytwo = new NamespacedKey(plugin, "no_damage");

            AttributeModifier modifier = new AttributeModifier(
                    keytwo,
                    -100.0,
                    AttributeModifier.Operation.ADD_NUMBER
            );
            wolfitemmeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE,modifier);
            wolfitemmeta.setLore(List.of("§6右クリックで使用可能"));
            wolfitemmeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING,"wolf_item");


            wolfitem.setItemMeta(wolfitemmeta); //アイテムメタを設定

            return  wolfitem;


        }

        //狂人
        if (itemn.equals("madman")) {
            ItemStack madmanitem = new ItemStack(Material.ECHO_SHARD, 1);

            ItemMeta madmanitemeta = madmanitem.getItemMeta();
            madmanitemeta.setDisplayName("§c§l味方を探せ！");
            madmanitemeta.setLore(List.of("§6右クリックで使用可能"));
            madmanitemeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING, "madman_item");


            madmanitem.setItemMeta(madmanitemeta); //アイテムメタを設定

            return madmanitem;



        }

        //騎士
        if (itemn.equals("knight")){
            ItemStack knightitem = new ItemStack(Material.SHIELD, 1);

            ItemMeta knightitemmeta = knightitem.getItemMeta();
            knightitemmeta.setDisplayName("§c守りの盾");
            knightitemmeta.setLore(List.of("§6右クリックで使用可能"));
            knightitemmeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING, "knight_item");


            knightitem.setItemMeta(knightitemmeta); //アイテムメタを設定

            return  knightitem;


        }

        //占い師
        if (itemn.equals("fortunecheck")){

            ItemStack fortuneitem = new ItemStack(Material.AMETHYST_SHARD, 1);

            ItemMeta fortuneitemmeta = fortuneitem.getItemMeta();
            fortuneitemmeta.setDisplayName("§5§l占い");
            fortuneitemmeta.setLore(List.of("§6右クリックで使用可能"));
            fortuneitemmeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING, "fortune_item");


            fortuneitem.setItemMeta(fortuneitemmeta); //アイテムメタを設定

            return fortuneitem;

        }

        //霊媒師
        if (itemn.equals("mediumcheck")){

            ItemStack mediumitem = new ItemStack(Material.NETHER_STAR, 1);

            ItemMeta mediumitemmeta = mediumitem.getItemMeta();
            mediumitemmeta.setDisplayName("§5§l霊媒師用のアイテム");
            mediumitemmeta.setLore(List.of("§6右クリックで使用可能"));
            mediumitemmeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING, "medium_item");


            mediumitem.setItemMeta(mediumitemmeta); //アイテムメタを設定

            return mediumitem;

        }


        if (itemn.equals("pcheck")){

            //残り人数確認の書
            ItemStack pcheckitem = new ItemStack(Material.LEATHER_HORSE_ARMOR);
            ItemMeta pcheckitemmeta = pcheckitem.getItemMeta();
            pcheckitemmeta.setDisplayName("§6残り人数確認の書");
            pcheckitemmeta.setLore(List.of("§a右クリックで使用可能"));
            pcheckitemmeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING, "people_check");
            pcheckitem.setItemMeta(pcheckitemmeta); //アイテムメタを設定

            return pcheckitem;

        }



        if (itemn.equals("coin")) {
            //コイン
            ItemStack coin = new ItemStack(Material.GOLD_INGOT);
            ItemMeta spawngolditemmeta = coin.getItemMeta();
            spawngolditemmeta.setDisplayName("§6コイン");
            spawngolditemmeta.setLore(List.of("§a人狼ゲーム専用コイン"));
            spawngolditemmeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING, "spawn_gold_ingot");
            coin.setItemMeta(spawngolditemmeta); //アイテムメタを設定


            return coin;


        }else if (itemn.equals("glowing")){
            ItemStack glowingitem = new ItemStack(Material.GLOW_INK_SAC, amount);
            ItemMeta glowingitemmeta = glowingitem.getItemMeta();
            glowingitemmeta.setDisplayName("§e全員発光");
            glowingitemmeta.setLore(List.of("§a右クリックで使用可能"));
            glowingitemmeta.getPersistentDataContainer().set(namekey,PersistentDataType.STRING,"glowin_item");
            glowingitem.setItemMeta(glowingitemmeta);

            return glowingitem;

        }else if (itemn.equals("speed")) {

            ItemStack potion = new ItemStack(Material.SPLASH_POTION);
            PotionMeta potionmeta = (PotionMeta) potion.getItemMeta();
            potionmeta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 2, 10), true);


            potionmeta.setDisplayName("§b俊敏のポーション");


            potion.setItemMeta(potionmeta);

            return potion;
        } else if (itemn.equals("invisibility")) {

            ItemStack invisibilitypotion = new ItemStack(Material.SPLASH_POTION);
            PotionMeta potionmeta = (PotionMeta) invisibilitypotion.getItemMeta();
            potionmeta.addCustomEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 15, 1),true);


            potionmeta.setDisplayName("§l透明化のポーション");


            invisibilitypotion.setItemMeta(potionmeta);

            return invisibilitypotion;

        }else if (itemn.equals("smoke")){
            ItemStack smoke = new ItemStack(Material.COAL);
            ItemMeta smokeItemMeta = smoke.getItemMeta();
            smokeItemMeta.setDisplayName("§6§l煙幕");
            smokeItemMeta.setLore(List.of("§a右クリックで使用可能"));
            smokeItemMeta.getPersistentDataContainer().set(namekey,PersistentDataType.STRING,"smoke_item");

            smoke.setItemMeta(smokeItemMeta);

            return smoke;
        }

        return new ItemStack(Material.DIRT);
    }
}
