package MiniCash.miniCashwerewolf.Event;

import MiniCash.miniCashwerewolf.MiniCashwerewolf;
import MiniCash.miniCashwerewolf.Timer;
import MiniCash.miniCashwerewolf.WolfMain;
import MiniCash.miniCashwerewolf.gui.VoteGuiHolder;
import MiniCash.miniCashwerewolf.gui.fortuneGuiHolder;
import MiniCash.miniCashwerewolf.gui.knightGuiHolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

import static MiniCash.miniCashwerewolf.Event.Item.*;
import static MiniCash.miniCashwerewolf.Event.ItemTimer.settarget;
import static MiniCash.miniCashwerewolf.MiniCashwerewolf.*;
import static MiniCash.miniCashwerewolf.WolfMain.villagerlistcount;
import static MiniCash.miniCashwerewolf.WolfMain.wolflistcount;
import static MiniCash.miniCashwerewolf.gui.ShopGUI.openshopgui;


public class MyEvent implements Listener {
    private final MiniCashwerewolf plugin;
    private final WolfMain wolfmain;

    public MyEvent(MiniCashwerewolf plugin,WolfMain wolfmain) {
        this.plugin = plugin;
        this.wolfmain = wolfmain;
    }
    public Map<UUID, Boolean> headpickupcheck = new HashMap<>();
    public Map<UUID,String> headpickupHeadName =new HashMap<>();

    //ゲーム実行中のホワイトリスト
    @EventHandler
    public void playerlogin(PlayerLoginEvent event){
        if (gamePlaying) {
            if (Bukkit.hasWhitelist()) {
                Player player = event.getPlayer();

                if (!player.isWhitelisted()) {
                    event.disallow(
                            PlayerLoginEvent.Result.KICK_WHITELIST,
                            "\n§a[MiniCashwerewolf]\n §4§lあなたは人狼ゲームに参加されていません。 \n§r§lゲームが終了するまでお待ちください。"
                    );
                }
            }
        }


    }


    @EventHandler
    public void click(InventoryClickEvent event) {

        //空のスロットをクリックしていたら処理を停止
        if (event.getCurrentItem() == null) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        UUID id = player.getUniqueId();

        int check = guicheck.getOrDefault(id, 0);
        if (check >= 1) {
            player.sendMessage("開発時チェック用");
            event.setCancelled(true);
        }

        //会議投票！！
        //もしクリックしたinventoryのHolderがVoteGUIHolder....
        if (event.getInventory().getHolder() instanceof VoteGuiHolder) {

            ItemStack clickitem = event.getCurrentItem();
            ItemMeta itemmeta = clickitem.getItemMeta();


            if (itemmeta != null && itemmeta.hasDisplayName()) {  //投票をキャンセルをクリックした場合
                if (itemmeta.getDisplayName().equals("§c投票をキャンセル")) {
                    player.closeInventory();
                    player.sendMessage("§5投票をキャンセルしました");

                } else {
                    String nameVote = event.getCurrentItem().getItemMeta().getDisplayName();
                    //呼び出し
                    String voteretrunstring = wolfmain.votego(nameVote);

                    player.closeInventory();

                    player.sendMessage(voteretrunstring);
                }
            }

        } else if (event.getInventory().getHolder() instanceof knightGuiHolder) {

            //アイテム削除
            ItemStack mainitem = player.getInventory().getItemInMainHand();
            mainitem.setAmount(mainitem.getAmount() - 1);

            String nameVote = event.getCurrentItem().getItemMeta().getDisplayName();

            Player target = Bukkit.getPlayer(nameVote);

            player.sendMessage("§d§n" + nameVote + "§r§dを選択しました\n1ターン守られます");

            player.closeInventory();


            //targetを送信
            settarget(target);

            UUID targetid = target.getUniqueId();
            knightProtectedPlayers.add(targetid);

            new ItemTimer().runTaskTimer(MiniCashwerewolf.getPlugin(), 0L, 20L);


        } else if (event.getInventory().getHolder() instanceof fortuneGuiHolder) {

            //アイテム削除
            ItemStack mainitem = player.getInventory().getItemInMainHand();
            mainitem.setAmount(mainitem.getAmount() - 1);

            String nameVote = event.getCurrentItem().getItemMeta().getDisplayName();

            Player target = Bukkit.getPlayer(nameVote);

            UUID tid = target.getUniqueId();

            int getposition = position.getOrDefault(tid, 0);
            String message;

            //占い結果
            if (getposition == 1 || getposition == 2) {        //黒陣営処理
                message = "§4黒陣営";
            }else if (getposition >= 3 &&  getposition <= 99) {     //白陣営
                message = "§b白陣営";
            }else {
                message = "§b§k....§r";
            }

            player.sendMessage(  "§l" + target.getName() + "§rの役職は" +  message + " §r§lです");


            player.closeInventory();


        }
    }

    @EventHandler
    public void close(InventoryCloseEvent event){
        //インベントリ開いてる中を０に変更
        UUID id = event.getPlayer().getUniqueId();
        guicheck.put(id,0);
    }


    @EventHandler
    public void itemclick(PlayerInteractEvent event) {


        ItemStack item = event.getItem();
        NamespacedKey namekey = new NamespacedKey(plugin, "wolfitem");
        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        //人狼
        String wolfitemvalue = item.getPersistentDataContainer().get(namekey, PersistentDataType.STRING);


        if (item.getType() == Material.CLOCK && event.getAction() == Action.RIGHT_CLICK_AIR) {

            Player player = event.getPlayer();

            player.sendMessage("開発時用shop開くようアイテムクリック確認");

            //メソッド呼び出し
            openshopgui(player);

        } else if (item.getType() == Material.DIAMOND_AXE && item.getPersistentDataContainer().has(namekey) && wolfitemvalue.equals("wolf_item") && event.getAction() == Action.RIGHT_CLICK_AIR) {
            //人狼用の斧アイテム処理用メソッド呼び出し
            Player player = event.getPlayer();
            wolfitem(player);


        }else if (item.getType() == Material.ECHO_SHARD && item.getPersistentDataContainer().has(namekey) && wolfitemvalue.equals("madman_item") && event.getAction() == Action.RIGHT_CLICK_AIR  ) {

            //狂人アイテム

            event.getItem().setAmount(event.getItem().getAmount() - 1);

            Player player = event.getPlayer();
            String rmessage = madmanitem(player);

            player.sendMessage(rmessage);


        }else if (item.getType() == Material.SHIELD && item.getPersistentDataContainer().has(namekey) && wolfitemvalue.equals("knight_item") && event.getAction() == Action.RIGHT_CLICK_AIR  ||  item.getType() == Material.SHIELD && item.getPersistentDataContainer().has(namekey) && wolfitemvalue.equals("knight_item") && event.getAction() == Action.RIGHT_CLICK_BLOCK  ) {

                //騎士アイテム
                Player player = event.getPlayer();

                //盾無効化
                event.setCancelled(true);

                event.getItem().setAmount(event.getItem().getAmount() - 1);

                knightitem(player);

//            Entity target = player.getWorld().getNearbyPlayers(player.getLocation(), 2.0).stream()
//                    .filter(p -> !p.equals(player)) // 自分を除外
//                    .findFirst()               // 最初に見つかった1人を取得
//                    .orElse(null);             // 誰もいなければnull






        }else if ( item.getType() == Material.AMETHYST_SHARD && item.getPersistentDataContainer().has(namekey) && wolfitemvalue.equals("fortune_item") && event.getAction() == Action.RIGHT_CLICK_AIR ) {
            //占い師アイテム

            event.getItem().setAmount(event.getItem().getAmount() - 1);

            Player player = event.getPlayer();
            fortuneitem(player);



        }else if ( item.getType() == Material.NETHER_STAR && item.getPersistentDataContainer().has(namekey) && wolfitemvalue.equals("medium_item") && event.getAction() == Action.RIGHT_CLICK_AIR ) {
            //霊媒師アイテム

            event.getItem().setAmount(event.getItem().getAmount() - 1);

            Player player = event.getPlayer();
            UUID  mediumid = event.getPlayer().getUniqueId();
            if (headpickupcheck.get(mediumid)) {

                String headname = headpickupHeadName.getOrDefault(mediumid,null);

                if (headname != null) {

                    player.sendMessage(headname + "を占おうとしてるよ！");
                    Player target = Bukkit.getPlayer(headname);
                    UUID targetid = target.getUniqueId();
                    int getposition = position.getOrDefault(targetid,0);
                    String message;

                    //アイテム削除
                    ItemStack mainitem = player.getInventory().getItemInMainHand();
                    mainitem.setAmount(mainitem.getAmount() - 1);

                    //霊媒結果
                    if (getposition == 1 || getposition == 2) {        //黒陣営処理
                        message = "§4黒陣営";
                    }else if (getposition >= 3 &&  getposition <= 99) {     //白陣営
                        message = "§b白陣営";
                    }else {
                        message = "§b§k....§r";
                    }

                    player.sendMessage(  "§l" + target.getName() + "§rは" +  message + " §r§lでした");


                }


            }else {
                event.getPlayer().sendMessage("§eプレイヤーの墓の付近でアイテムを使用してください!");
            }



        }else if ( item.getType() == Material.LEATHER_HORSE_ARMOR && item.getPersistentDataContainer().has(namekey) && wolfitemvalue.equals("people_check") && event.getAction() == Action.RIGHT_CLICK_AIR ) {

            event.getItem().setAmount(event.getItem().getAmount() - 1);

            Player player = event.getPlayer();
            int check = peoplecheck(player);

            event.getPlayer().sendMessage("§5§l残り人数は§r" + check + "§5人です");

        } else if (item.getType() == Material.GLOW_INK_SAC && item.getPersistentDataContainer().has(namekey) && wolfitemvalue.equals("glowin_item") && event.getAction() == Action.RIGHT_CLICK_AIR ) {

            event.getItem().setAmount(event.getItem().getAmount() - 1);

            Player player = event.getPlayer();
            //itemの発光用メソッド呼び出し
            glowing(player);

        }else if (item.getType() == Material.COAL && item.getPersistentDataContainer().has(namekey) && wolfitemvalue.equals("smoke_item") && event.getAction() == Action.RIGHT_CLICK_AIR ) {

            //煙幕
            event.getItem().setAmount(event.getItem().getAmount() - 1);

            Player player = event.getPlayer();

            smoke(player);

        }

    }



    @EventHandler
    public void EntityDamage(EntityDamageEvent event) {

        //もし守られてるSETにプレイヤーが入っていたら...
        if (knightProtectedPlayers.contains(event.getEntity().getUniqueId())) {

            event.setCancelled(true);

        }

    }




    @EventHandler
    public void entityDeath(EntityDeathEvent event){

        NamespacedKey namekey = new NamespacedKey(plugin,"wolfitem");

        if (gamePlaying) {

            //カウント-
            Entity entity = event.getEntity();
            if (entity instanceof Player) {
                Player player = (Player) entity;

                UUID id = player.getUniqueId();
                int getplayerpotision = position.get(id);

                if (getplayerpotision == 1) {
                    wolflistcount--;
                } else if (getplayerpotision >= 3 && getplayerpotision <= 6) {
                    villagerlistcount--;
                }

                //勝敗用

                if (wolflistcount == 0) {    //市民勝利

                    wolfmain.villagerwin();

                } else if (villagerlistcount == 0) {

                    wolfmain.wolfwin();

                }




                //死亡地点座標取得
                Location location = event.getEntity().getLocation();

                Player deathpl = (Player) event.getEntity();

                ItemStack playerhead = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta phskullmeta = (SkullMeta) playerhead.getItemMeta();
                phskullmeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING, "Death_Player_Head");

                phskullmeta.setOwningPlayer(deathpl);

                phskullmeta.setDisplayName(deathpl.getName());    //現在処理中のプレイヤー名を入手しそのプレイヤー名を頭にセット

                playerhead.setItemMeta(phskullmeta); //アイテムメタを設定

                location.getWorld().dropItem(location, playerhead);     //PlayerHeadスポーン





            }
        }



        if (event.getEntity() instanceof Skeleton) {
            Random rm = new Random();

            int rmresult = rm.nextInt(3);

            Skeleton skelton = (Skeleton) event.getEntity();
            String skeltonmvalue = skelton.getPersistentDataContainer().get(namekey, PersistentDataType.STRING);
            if (rmresult == 1) {   //３分の１の確率でコインをドロップ
                if (skeltonmvalue.equals("spawncoinskelton")) {
                    // このスケルトンだけ特別処理
                    ItemStack spawngolditem = new ItemStack(Material.GOLD_INGOT);
                    ItemMeta spawngolditemmeta = spawngolditem.getItemMeta();
                    spawngolditemmeta.setDisplayName("§6コイン");
                    spawngolditemmeta.setLore(List.of("§a人狼ゲーム専用コイン"));
                    spawngolditemmeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING, "spawn_gold_ingot");
                    spawngolditem.setItemMeta(spawngolditemmeta); //アイテムメタを設定

                    event.getDrops().clear();
                    event.getDrops().add(spawngolditem);
                }
            }else {     //違う場合でも初期ドロップをなくす
                event.getDrops().clear();
            }
        }

    }


    @EventHandler
    public void EntityPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        Player pl = (Player) event.getEntity();
        UUID pid = pl.getUniqueId();

        NamespacedKey namekey = new NamespacedKey(plugin, "wolfitem");

        String headvalue = event.getItem().getItemStack().getItemMeta().getPersistentDataContainer().get(namekey, PersistentDataType.STRING);

        if (headvalue != null && headvalue.equals("Death_Player_Head")) {

            String headname = event.getItem().getItemStack().getItemMeta().getDisplayName();

            event.setCancelled(true);
            headpickupcheck.put(pid, true);
            headpickupHeadName.put(pid, headname);

            // プレイヤーがすでに「最近メッセージを受け取ったか」を確認
            if (!player.hasMetadata("grave_msg_cooldown")) {


                player.sendMessage("§4" + headname + "§rの墓");

                // プレイヤーに「印」を付ける（値は何でも良い）
                player.setMetadata("grave_msg_cooldown", new FixedMetadataValue(plugin, true));

                // 最初に解説した「Timer（Scheduler）」を使って、3秒後に印を消す
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    player.removeMetadata("grave_msg_cooldown", plugin);

                    //headpickupcheckをfalseに
                    headpickupcheck.put(pid, false);
                    headpickupHeadName.put(pid, null);

                }, 20 * 2); // 100ティック = 5秒

            }
        }

    }





    @EventHandler
    public void EntityClick(PlayerInteractEntityEvent event){


        Player player = event.getPlayer();

        if (gamePlaying) {

            if (event instanceof Villager) {

                UUID id = player.getUniqueId();
                int getposition = position.get(id);

            }
        }



//            Entity entity = event.getRightClicked();
//
//            // Interactionエンティティかつ、お墓のメタデータを持っているか確認
//            if (entity instanceof Interaction && entity.hasMetadata("isGrave")) {
//                Player player = event.getPlayer();
//                player.sendMessage("§eお墓を右クリックしました（お参り）。");
//
//                // メタデータから情報を引き出す例（誰の墓か、など）
//                // String owner = entity.getMetadata("graveOwner").get(0).asString();
//            }


    }


    @EventHandler
    public void SignChange(SignChangeEvent event) {

        String line1 = event.getLine(0);
        String line2 = event.getLine(1);

        if (line1 != null && line1.equals("mwgame")) {

            if (line2 != null && line2.equals("Join")) {

                String setline1 = "§6[mwerewolf]";
                String setline2 = "§a右クリックで";
                String setline3 = "§a§l§n人狼ゲーム";
                String setline4 = "§aに参加";

                event.setLine(0,setline1);
                event.setLine(1,setline2);
                event.setLine(2,setline3);
                event.setLine(3,setline4);
            }else if (line2 != null && line2.equals("Spectator")) {
                //観戦者用

                event.setLine(0,"§6[mwerewolf]");
                event.setLine(1,"§a右クリックで");
                event.setLine(2,"§6観戦者として登録");
            }else if (line2 != null && line2.equals("Cancel")){
                //登録キャンセル
                event.setLine(0,"§6[mwerewolf]");
                event.setLine(1,"§a右クリックで");
                event.setLine(2,"§c登録をキャンセル");
            }

        }


    }

    public static List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

    @EventHandler
    public void clickblock(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {

            Block block = event.getClickedBlock();

            if (block != null && block.getState() instanceof Sign){

                Sign sign = (Sign) block.getState();

                    if (sign.getLine(0).equals("§6[mwerewolf]") && sign.getLine(1).equals("§a右クリックで") && sign.getLine(2).equals("§a§l§n人狼ゲーム") && sign.getLine(3).equals("§aに参加")){

                        //すでに登録又は観戦者として登録していないかのチェック
                        int getposition = position.getOrDefault(player.getUniqueId(),0);
                        if (players.contains(player) || getposition >= 1) {
                            player.sendMessage("§cすでに人狼ゲームに登録しています！");
                        }else {

                            //ここからゲームに参加準備をした後の処理記述！

                            //参加したいプレイヤーをplayersListに入れる
                            player.sendMessage("§a§l人狼ゲームに登録しました");
                            players.add(player);
                        }
                    }else if (sign.getLine(0).equals("§6[mwerewolf]") && sign.getLine(1).equals("§a右クリックで") && sign.getLine(2).equals("§6観戦者として登録")){
                    //先に普通の登録をしていないかのチェック
                    int getposition = position.getOrDefault(player.getUniqueId(),0);
                    if (players.contains(player) || getposition >= 1) {
                        player.sendMessage("§cすでに人狼ゲームに登録しています！");
                    }else {

                        //これと同じ看板をクリックしたらそのプレイヤーを観戦者として登録
                        UUID id = player.getUniqueId();
                        position.put(id, 100);
                        player.sendMessage("§a§l人狼ゲームに§6観戦者§a§lとして登録しました");
                    }
                }else if (sign.getLine(0).equals("§6[mwerewolf]") && sign.getLine(1).equals("§a右クリックで") && sign.getLine(2).equals("§c登録をキャンセル")){
                        //登録キャンセル
                        //ゲームに登録又は、役職を設定しているのかをチェック
                    int getposition = position.getOrDefault(player.getUniqueId(),0);
                    if (players.contains(player)) {

                        players.remove(player);
                        player.sendMessage("§b人狼ゲームの登録を解除しました");

                    } else if (getposition >= 1) {
                        position.put(player.getUniqueId(),0);
                        player.sendMessage("§b人狼ゲームの登録を解除しました");
                    }else {        //まだ登録していなかったら

                        player.sendMessage("§cまだ人狼ゲームに登録していません！");

                    }

                }

            }



        }
    }



    @EventHandler
    public void PlayerDeathEvent(PlayerDeathEvent event) {
        event.setDeathMessage(null);
    }



}
