package MiniCash.miniCashwerewolf;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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

        ItemStack item = null;
        ItemMeta itemMeta;

        NamespacedKey namekey = new NamespacedKey(plugin,"wolfitem");
        //人狼
        if (itemn.equals("wolf")){

            item = new ItemStack(Material.DIAMOND_AXE, 1);

            itemMeta = item.getItemMeta();

            itemMeta.itemName(Component.text("人狼の斧").color(NamedTextColor.RED));
            itemMeta.setUnbreakable(true);
            NamespacedKey keytwo = new NamespacedKey(plugin, "no_damage");

            AttributeModifier modifier = new AttributeModifier(
                    keytwo,
                    -100.0,
                    AttributeModifier.Operation.ADD_NUMBER
            );
            itemMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE,modifier);
            itemMeta.lore(List.of(Component.text("右クリックで使用可能").color(NamedTextColor.GOLD)));
            itemMeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING,"wolf_item");


            item.setItemMeta(itemMeta); //アイテムメタを設定


        }else if (itemn.equals("madman")) {
            //狂人
            item = new ItemStack(Material.ECHO_SHARD, 1);

            itemMeta = item.getItemMeta();

            itemMeta.itemName(Component.text("味方を探せ！").color(NamedTextColor.RED).decorate(TextDecoration.BOLD));
            itemMeta.lore(List.of(Component.text("右クリックで使用可能").color(NamedTextColor.GOLD)));
            itemMeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING, "madman_item");


            item.setItemMeta(itemMeta); //アイテムメタを設定


        }else if (itemn.equals("knight")){
            //騎士

            item = new ItemStack(Material.SHIELD, 1);

            itemMeta = item.getItemMeta();

            itemMeta.itemName(Component.text("守りの盾").color(NamedTextColor.RED));
            itemMeta.lore(List.of(Component.text("右クリックで使用可能").color(NamedTextColor.GOLD)));
            itemMeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING, "knight_item");


            item.setItemMeta(itemMeta); //アイテムメタを設定


        }else if (itemn.equals("fortunecheck")){

            //占い師

            item = new ItemStack(Material.AMETHYST_SHARD, 1);

            itemMeta = item.getItemMeta();

            itemMeta.itemName(Component.text("占い").color(NamedTextColor.DARK_PURPLE).decorate(TextDecoration.BOLD));
            itemMeta.lore(List.of(Component.text("右クリックで使用可能").color(NamedTextColor.GOLD)));
            itemMeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING, "fortune_item");


            item.setItemMeta(itemMeta); //アイテムメタを設定


        }else if (itemn.equals("mediumcheck")){

            //霊媒師

            item = new ItemStack(Material.NETHER_STAR, 1);

            itemMeta = item.getItemMeta();

            itemMeta.itemName(Component.text("霊媒師用のアイテム").color(NamedTextColor.DARK_PURPLE).decorate(TextDecoration.BOLD));
            itemMeta.lore(List.of(Component.text("右クリックで使用可能").color(NamedTextColor.GOLD)));
            itemMeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING, "medium_item");


            item.setItemMeta(itemMeta); //アイテムメタを設定


        }else if (itemn.equals("pcheck")){

            //残り人数確認の書
            item = new ItemStack(Material.LEATHER_HORSE_ARMOR);

            itemMeta = item.getItemMeta();

            itemMeta.itemName(Component.text("残り人数確認の書").color(NamedTextColor.GOLD));
            itemMeta.lore(List.of(Component.text("右クリックで使用可能").color(NamedTextColor.GREEN)));
            itemMeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING, "people_check");
            item.setItemMeta(itemMeta); //アイテムメタを設定

        }else if (itemn.equals("coin")) {
            //コイン
            item = new ItemStack(Material.GOLD_INGOT);

            itemMeta = item.getItemMeta();

            itemMeta.itemName(Component.text("コイン").color(NamedTextColor.GOLD));
            itemMeta.lore(List.of(Component.text("人狼ゲーム専用コイン").color(NamedTextColor.GREEN)));
            itemMeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING, "spawn_gold_ingot");
            item.setItemMeta(itemMeta); //アイテムメタを設定



        }else if (itemn.equals("glowing")){
            item = new ItemStack(Material.GLOW_INK_SAC, amount);

            itemMeta = item.getItemMeta();

            itemMeta.itemName(Component.text("全員発光").color(NamedTextColor.YELLOW));
            itemMeta.lore(List.of(Component.text("右クリックで使用可能").color(NamedTextColor.GREEN)));
            itemMeta.getPersistentDataContainer().set(namekey,PersistentDataType.STRING,"glowin_item");
            item.setItemMeta(itemMeta);


        }else if (itemn.equals("speed")) {

            item = new ItemStack(Material.SPLASH_POTION);

            PotionMeta potionmeta = (PotionMeta) item.getItemMeta();
            potionmeta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 2, 10), true);


            potionmeta.itemName(Component.text("俊敏のポーション").color(NamedTextColor.AQUA));
            potionmeta.lore(null);

            item.setItemMeta(potionmeta);

        } else if (itemn.equals("invisibility")) {

            item = new ItemStack(Material.SPLASH_POTION);
            PotionMeta potionmeta = (PotionMeta) item.getItemMeta();
            potionmeta.addCustomEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 15, 1),true);


            potionmeta.itemName(Component.text("透明化のポーション").decorate(TextDecoration.BOLD));
            potionmeta.lore(null);

            item.setItemMeta(potionmeta);


        }else if (itemn.equals("smoke")){
            item = new ItemStack(Material.COAL);
            itemMeta = item.getItemMeta();
            itemMeta.itemName(Component.text("煙幕").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
            itemMeta.lore(List.of(Component.text("右クリックで使用可能").color(NamedTextColor.GREEN)));
            itemMeta.getPersistentDataContainer().set(namekey,PersistentDataType.STRING,"smoke_item");

            item.setItemMeta(itemMeta);

        }

        return item;
    }
}
