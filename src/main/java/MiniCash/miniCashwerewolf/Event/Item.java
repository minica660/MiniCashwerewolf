package MiniCash.miniCashwerewolf.Event;

import MiniCash.miniCashwerewolf.GameManager;
import MiniCash.miniCashwerewolf.MiniCashWereWolf;
import MiniCash.miniCashwerewolf.RoleManager;
import MiniCash.miniCashwerewolf.gui.FortuneGuiHolder;
import MiniCash.miniCashwerewolf.gui.KnightGuiHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static MiniCash.miniCashwerewolf.timer.Timer.nowtime;
import static MiniCash.miniCashwerewolf.GameManager.villagerlistcount;
import static MiniCash.miniCashwerewolf.GameManager.wolflistcount;

public class Item {

    private static MiniCashWereWolf plugin;

    public Item(MiniCashWereWolf plugin) {
        this.plugin = plugin;
    }

    /**
     * 指定したプレイヤーの周囲にいるプレイヤーへメッセージを送る
     * * @param centerPlayer 中心となるプレイヤー
     * @param radius 範囲（マス数・ブロック数）
     * @param message 送信するメッセージ
     */
    public void sendMessageToNearbyPlayers(Player centerPlayer, double radius, String message) {
        // 指定した半径(X, Y, Z)の中にいるエンティティを取得
        for (Entity entity : centerPlayer.getNearbyEntities(radius, radius, radius)) {

            // エンティティが「プレイヤー」であるか確認
            if (entity instanceof Player nearbyPlayer) {

                // 範囲内のプレイヤーにメッセージを送信
                nearbyPlayer.sendMessage("§e[周囲メッセージ] " + message);
            }
        }
    }

    //人狼アイテム効果
    public static void wolfItemSkill(Player player) {
        //人狼ゲームが夜だったら使用可能に
        if (!nowtime) {
            Location location = player.getLocation();


            // プレイヤーの位置
            Location eyeLocation = player.getEyeLocation();

            // 視線方向ベクトル
            Vector dir = eyeLocation.getDirection().normalize();

            // 2マス先へ移動
            Location particleLoc = eyeLocation.add(dir.multiply(2));


            player.getWorld().spawnParticle(
                    Particle.SWEEP_ATTACK,
                    particleLoc,
                    10,               // 個数（多いほど密度が上がる）
                    0.05,             // X軸の広がり（細くする）
                    0.8,              // Y軸の広がり（ここを大きくすると縦長になる）
                    0.05,             // Z軸の広がり（細くする）
                    0.1               // 動きの速さ
            );

            //playsound
            location.getWorld().playSound(location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 2.0f, 0.5f);
            location.getWorld().playSound(location, Sound.ENTITY_WITHER_BREAK_BLOCK, 2.0f, 0.4f);
            location.getWorld().playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 1.1f, 0.1f);




            RayTraceResult result = player.getWorld().rayTraceEntities(
                    player.getEyeLocation(),
                    player.getEyeLocation().getDirection(),
                    2.0,
                    1,                                             // 判定の横幅
                    entity -> !entity.equals(player) && entity instanceof Player //ターゲット
            );

            if (result != null && result.getHitEntity() instanceof Player targetPlayer) {

                if (targetPlayer.getGameMode() == GameMode.ADVENTURE) {

                    targetPlayer.setHealth(0.0);

                }

            }






        }
    }



    //狂人専用アイテム
    // どういうアイテム？何の目的で作った？
    public static void madmanItem(Player player) {
        StringBuilder resturnm = new StringBuilder("§9味方：§r§l");

        if (MiniCashWereWolf.gamePlaying) {

            for (Player p : GameManager.getGameplayers()) {
                UUID uuid = p.getUniqueId();
                RoleManager.RoleType roleType = RoleManager.getPlayerRole().getOrDefault(uuid, RoleManager.RoleType.NO);

                if (roleType.equals(RoleManager.RoleType.WOLF)) {
                    String plus = p.getName() + "、";
                    resturnm.append(plus);
                }


            }

        }else {
            resturnm = new StringBuilder("§c現在ゲームが進行中ではありません");
        }


        player.sendMessage(MiniCashWereWolf.getMessage(
                resturnm.toString()
        ));

    }

    public static Set<UUID> knightProtectedPlayers = new HashSet<>();
    public static UUID id = null;
    public static Entity target = null;
    //騎士アイテム
    public static void knightitem(Player player) {


        //GUI関連処理
        Inventory knightGUI = Bukkit.createInventory(new KnightGuiHolder(),27, Component.text("守るプレイヤーの選択").color(NamedTextColor.RED));  //サイズ9*○○


        int count = 0; //GUIに設置した数カウント用

        int gamePlayerCount = GameManager.getGameplayers().size();

        for (Player targetPlayer: Bukkit.getOnlinePlayers()) {
            if (count <= gamePlayerCount) {
                ItemStack playerhead = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta phskullmeta = (SkullMeta) playerhead.getItemMeta();

                phskullmeta.setOwningPlayer(targetPlayer);

                phskullmeta.itemName(Component.text(targetPlayer.getName()));    //現在処理中のプレイヤー名を入手しそのプレイヤー名を頭にセット
                phskullmeta.lore(List.of(Component.text("クリックで" + targetPlayer.getName() + "を守護").color(NamedTextColor.GOLD)));

                playerhead.setItemMeta(phskullmeta); //アイテムメタを設定

                //今のアイテムをスロットに設定
                knightGUI.setItem(count, playerhead);


                count++;


            }

        }

        //GUIをイベント実行者に開かせる
        player.sendMessage(Component.text("守りたい相手を選んでください").color(NamedTextColor.BLUE));
        player.openInventory(knightGUI);



    }



    //占い師アイテム
    public static void fortuneItem(Player player){

        //GUI関連処理
        Inventory fortuneGUI = Bukkit.createInventory(new FortuneGuiHolder(),27,Component.text("占うプレイヤー選択").color(NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD));

        int count = 0; //GUIに設置した数カウント用

        int gamePlayerCount = GameManager.getGameplayers().size();

        for (Player targetPlayer: GameManager.getGameplayers()) {
            if (count <= gamePlayerCount) {
                ItemStack playerhead = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta phskullmeta = (SkullMeta) playerhead.getItemMeta();

                phskullmeta.setOwningPlayer(targetPlayer);

                phskullmeta.itemName(Component.text(targetPlayer.getName()));    //現在処理中のプレイヤー名を入手しそのプレイヤー名を頭にセット
                phskullmeta.lore(List.of(Component.text("クリックで" + targetPlayer.getName() + "を占う").color(NamedTextColor.GOLD)));

                playerhead.setItemMeta(phskullmeta); //アイテムメタを設定

                fortuneGUI.setItem(count, playerhead);

                count++;


            }

        }

        //GUIをイベント実行者に開かせる
        player.sendMessage(Component.text("占いたい相手を選んでください").color(NamedTextColor.BLUE));
        player.openInventory(fortuneGUI);



    }






    //残り人数確認アイテム
    public static void peopleCheck(Player player){

        ItemStack item = player.getInventory().getItemInMainHand();
        item.setAmount(item.getAmount() - 1);

        int people = wolflistcount + villagerlistcount;

        player.sendMessage(
                MiniCashWereWolf.getMessage(
                        Component.text("残り人数は").color(NamedTextColor.DARK_PURPLE).decorate(TextDecoration.BOLD)
                                        .append(Component.text(people))
                                                .append(Component.text("人です").color(NamedTextColor.DARK_PURPLE))
                )
        );

    }



    //発光エフェクト
    public static void glowing(){


        for (Player targetPlayer : GameManager.getGameplayers()){

            PotionEffect glowing = new PotionEffect(PotionEffectType.GLOWING, 20 * 6, 1);

            targetPlayer.addPotionEffect(glowing);



        }


    }

    //煙幕
    public static void smoke(Player player){
        new BukkitRunnable() {
            final Location location = player.getLocation();
            int time = 5;  //煙幕時間
            @Override
            public void run() {
                if (time <= 0) {
                    this.cancel();
                    return;
                }

                player.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, location, 1000, 3, 2.5, 3, 0);
                //範囲内のプレイヤーにエフェクト付与
                location.getWorld().getNearbyEntities(location, 3, 3, 3)
                        .stream()
                        .filter(entity -> entity instanceof Player)
                        .forEach(pla -> ((Player)pla).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1)));

                time--;
            }

        }.runTaskTimer(plugin, 0L, 20L);

    }




}
