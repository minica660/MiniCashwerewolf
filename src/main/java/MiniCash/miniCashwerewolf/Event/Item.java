package MiniCash.miniCashwerewolf.Event;

import MiniCash.miniCashwerewolf.MiniCashwerewolf;
import MiniCash.miniCashwerewolf.Timer;
import MiniCash.miniCashwerewolf.gui.VoteGuiHolder;
import MiniCash.miniCashwerewolf.gui.fortuneGuiHolder;
import MiniCash.miniCashwerewolf.gui.knightGuiHolder;
import org.bukkit.*;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static MiniCash.miniCashwerewolf.Event.ItemTimer.check;
import static MiniCash.miniCashwerewolf.MiniCashwerewolf.*;
import static MiniCash.miniCashwerewolf.Timer.nowtime;
import static MiniCash.miniCashwerewolf.WolfMain.villagerlistcount;
import static MiniCash.miniCashwerewolf.WolfMain.wolflistcount;

public class Item {

    //人狼アイテム効果
    public static void wolfitem(Player player) {
        //人狼ゲームが夜だったら使用可能に
        if (!nowtime) {
            Location location = player.getLocation();

            //player.getWorld().spawnParticle(Particle.FLASH,location,
            //        1, //個数
            //        0.5,0.5,0.5,// 散らばる範囲 (x, y, z のオフセット)
            //                 0.1,  // 速度 (0だとその場に留まる)
            //        Color.WHITE
            //);


            // プレイヤーの位置
            Location loc = player.getEyeLocation();

            // 視線方向ベクトル
            Vector dir = loc.getDirection().normalize();

            // 2マス先へ移動
            Location particleLoc = loc.add(dir.multiply(2));


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


            //周囲プレイヤーへの効果

                Player me = player;
                Player target = null;
                double minDistance = 2.0;
                double distance = 0;
                for (Player p : Bukkit.getOnlinePlayers()) {

                    if (p.equals(me)) continue;
                    if (target != null) {
                        distance = me.getLocation().distance(target.getLocation());

                    }
                    if (distance <= minDistance) {
                        minDistance = distance;
                        target = p;
                    }

                }

            if (target != null) {
                //一応一撃斧の対策(守護されていたら処理停止)
                if (knightProtectedPlayers.contains(target.getUniqueId())) {
                    return;
                }

                target.sendMessage("あなたは2マス以内にいます！");
                target.setHealth(0.0);
                //選ばれた1人に処理？
                //if (target != null) {
                //    target.sendMessage("あなたが選ばれました");

            }else {
                return;
            }





//            player.getWorld().spawnParticle(
//                    Particle.SWEEP_ATTACK,
//                    player.getLocation().add(player.getLocation().getDirection().multiply(2)), // 2マス先
//                    10, 0, +1, 0, 0
//            );


        }
    }



    //狂人専用アイテム
    public static String madmanitem(Player player) {
        String resturnm = "§9味方：§r§l";

        if (gamePlaying) {
            for (Player p : Bukkit.getOnlinePlayers()){
            UUID id = p.getUniqueId();
            int geti = position.getOrDefault(id, 0);

                if (geti == 1) {
                    String plus = p.getName() + "、";
                    resturnm = resturnm + plus;
                }

            }
        }else {
            resturnm = "§c現在ゲームが進行中ではありません";
        }


        return resturnm;

    }

    public static Set<UUID> knightProtectedPlayers = new HashSet<>();
    public static UUID id = null;
    public static Entity target = null;
    //騎士アイテム
    public static void knightitem(Player player) {


        //GUI関連処理
        Inventory knightGUI = Bukkit.createInventory(new knightGuiHolder(),27,"プレイヤー選択");  //サイズ9*○○


        int count = 0; //GUIに設置した数カウント用

        int onlinecount = Bukkit.getOnlinePlayers().size();

        for (Player pl: Bukkit.getOnlinePlayers()) {
            if (count <= onlinecount) {
                ItemStack playerhead = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta phskullmeta = (SkullMeta) playerhead.getItemMeta();

                phskullmeta.setOwningPlayer(pl);

                phskullmeta.setDisplayName(pl.getName());    //現在処理中のプレイヤー名を入手しそのプレイヤー名を頭にセット
                phskullmeta.setLore(List.of("§6クリックで" + pl.getName() + "を守護"));

                playerhead.setItemMeta(phskullmeta); //アイテムメタを設定

                //今のアイテムをスロットに設定
                knightGUI.setItem(count, playerhead);

                //処理した数を次のために１＋
                count++;


            }

        }

        //GUIをイベント実行者に開かせる
        player.sendMessage("§9守りたい相手を選んでください");
        player.openInventory(knightGUI);
        //Map
        UUID id = player.getUniqueId();
        guicheck.put(id,1);  //イベントキャンセル用チェック




//        if (entity != null) {
//
//            target = entity;
//            UUID id = target.getUniqueId();
//            knightProtectedPlayers.add(id);
//
//            if (!check){
//                //タイマースタート
//                new ItemTimer().runTaskTimer(getPlugin(), 0L, 20L);
//                check = true;
//            }
//        }else {
//            getPlugin().getServer().getLogger().info("騎士：周囲にエンティティがいません");
//        }
    }



    //占い師アイテム
    public static void fortuneitem(Player player){

        //GUI関連処理
        Inventory fortuneGUI = Bukkit.createInventory(new fortuneGuiHolder(),27,"§d§l占うプレイヤー選択");  //サイズ9*○○

        int count = 0; //GUIに設置した数カウント用

        int onlinecount = Bukkit.getOnlinePlayers().size();

        for (Player pl: Bukkit.getOnlinePlayers()) {
            if (count <= onlinecount) {
                ItemStack playerhead = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta phskullmeta = (SkullMeta) playerhead.getItemMeta();

                phskullmeta.setOwningPlayer(pl);

                phskullmeta.setDisplayName(pl.getName());    //現在処理中のプレイヤー名を入手しそのプレイヤー名を頭にセット
                phskullmeta.setLore(List.of("§6クリックで" + pl.getName() + "を占う"));

                playerhead.setItemMeta(phskullmeta); //アイテムメタを設定

                //今のアイテムをスロットに設定
                fortuneGUI.setItem(count, playerhead);

                //処理した数を次のために１＋
                count++;


            }

        }

        //GUIをイベント実行者に開かせる
        player.sendMessage("§9占いたい相手を選んでください");
        player.openInventory(fortuneGUI);
        //Map
        UUID id = player.getUniqueId();
        guicheck.put(id,1);  //イベントキャンセル用チェック



    }






    //残り人数確認アイテム
    public static int peoplecheck(Player player){

        ItemStack item = player.getInventory().getItemInMainHand();
        item.setAmount(item.getAmount() - 1);

        int ipeoplecheck = wolflistcount + villagerlistcount;


        return ipeoplecheck;
    }



    //発光エフェクト
    public static void glowing(Player player){


        for (Player pl : Bukkit.getOnlinePlayers()){

            PotionEffect glowing = new PotionEffect(PotionEffectType.GLOWING, 20 * 6, 1);

            pl.addPotionEffect(glowing);



        }


    }

    //煙幕
    public static void smoke(Player player){
        new BukkitRunnable() {
            int time = 5;  //煙幕時間
            @Override
            public void run() {
                if (time >= 0) {
                    this.cancel();
                }

                Location loc = player.getLocation();
                player.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, loc, 50, 0.5, 0.5, 0.5, 0.1);
                time--;
            }

        }.runTaskTimer(plugin, 0L, 20L);

    }




}

