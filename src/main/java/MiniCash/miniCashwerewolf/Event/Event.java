package MiniCash.miniCashwerewolf.Event;

import MiniCash.miniCashwerewolf.MiniCashWereWolf;
import MiniCash.miniCashwerewolf.RoleManager;
import MiniCash.miniCashwerewolf.GameManager;
import MiniCash.miniCashwerewolf.gui.VoteGuiHolder;
import MiniCash.miniCashwerewolf.gui.FortuneGuiHolder;
import MiniCash.miniCashwerewolf.gui.KnightGuiHolder;
import MiniCash.miniCashwerewolf.timer.KnightItemTimer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

import static MiniCash.miniCashwerewolf.Event.Item.*;
import static MiniCash.miniCashwerewolf.timer.KnightItemTimer.setTarget;
import static MiniCash.miniCashwerewolf.MiniCashWereWolf.*;
import static MiniCash.miniCashwerewolf.GameManager.villagerlistcount;
import static MiniCash.miniCashwerewolf.GameManager.wolflistcount;


public class Event implements Listener {

    private final MiniCashWereWolf plugin;
    private final GameManager wolfmain;

    public Event(MiniCashWereWolf plugin, GameManager wolfMain) {
        this.plugin = plugin;
        this.wolfmain = wolfMain;
    }

    public Map<UUID,String> headpickupHeadName =new HashMap<>();

    //ゲーム実行中のホワイトリスト
    @EventHandler
    public void playerLogin(PlayerLoginEvent event){
        if (gamePlaying) {
            if (Bukkit.hasWhitelist()) {
                Player player = event.getPlayer();

                if (!player.isWhitelisted()) {
                    event.disallow(
                            PlayerLoginEvent.Result.KICK_WHITELIST,
                            "§4§lあなたは人狼ゲームに参加されていません。 \n§r§lゲームが終了するまでお待ちください。"
                    );

                }
            }
        }


    }


    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {

        //空のスロットをクリックしていたら処理を停止
        if (event.getCurrentItem() == null) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        InventoryHolder holder = event.getInventory().getHolder();

        if(!(holder instanceof VoteGuiHolder ||  holder instanceof FortuneGuiHolder || holder instanceof KnightGuiHolder)) {
            return;
        }

        event.setCancelled(true);

        //会議投票！！
        //もしクリックしたinventoryのHolderがVoteGUIHolder....
        if (event.getInventory().getHolder() instanceof VoteGuiHolder) {

            ItemStack clickItem = event.getCurrentItem();
            ItemMeta itemmeta = clickItem.getItemMeta();

            String itemName = PlainTextComponentSerializer.plainText().serialize(itemmeta.itemName());

            if (itemmeta.hasItemName()) {  //投票をキャンセルをクリックした場合
                if (itemName.equals("投票をキャンセル")) {
                    player.closeInventory();
                    player.sendMessage(MiniCashWereWolf.getMessage("投票をキャンセルしました"));

                } else {
                    //呼び出し
                    String voteResult = wolfmain.voteGo(itemName);

                    player.closeInventory();

                    player.sendMessage(voteResult);
                }
            }

        } else if (event.getInventory().getHolder() instanceof KnightGuiHolder) {

            //アイテム削除
            ItemStack mainItem = player.getInventory().getItemInMainHand();
            mainItem.setAmount(mainItem.getAmount() - 1);

            String nameVote = PlainTextComponentSerializer.plainText().serialize(event.getCurrentItem().getItemMeta().itemName());

            Player target = Bukkit.getPlayer(nameVote);

            if(target == null){
                player.sendMessage(
                        MiniCashWereWolf.getMessage(Component.text("ターゲットが不明です").color(NamedTextColor.RED))
                );
                return;
            }

            player.sendMessage("§d§n" + nameVote + "§r§dを選択しました\n1ターン守られます");

            player.closeInventory();


            //targetを送信
            setTarget(target);

            UUID targetid = target.getUniqueId();
            knightProtectedPlayers.add(targetid);

            new KnightItemTimer().runTaskTimer(plugin, 0L, 20L);


        } else if (event.getInventory().getHolder() instanceof FortuneGuiHolder) {

            //アイテム削除
            ItemStack mainItem = player.getInventory().getItemInMainHand();
            mainItem.setAmount(mainItem.getAmount() - 1);

            String nameVote = PlainTextComponentSerializer.plainText().serialize(mainItem.getItemMeta().itemName());

            Player target = Bukkit.getPlayer(nameVote);

            if(target == null){

                player.sendMessage(
                        MiniCashWereWolf.getMessage(
                                Component.text("ターゲットが不明です").color(NamedTextColor.RED)
                        )
                );

                return;
            }

            UUID uuid = target.getUniqueId();

            RoleManager.RoleType targetRole = RoleManager.getPlayerRole().getOrDefault(uuid, RoleManager.RoleType.NO);

            String message;

            //占い結果
            if (targetRole.equals(RoleManager.RoleType.WOLF)) {
                //黒陣営処理
                message = "§4黒陣営";

            }else if (targetRole.equals(RoleManager.RoleType.NO)) {

                message = "§b§k....§r";

            }else {
                //白陣営
                message = "§b白陣営";

            }

            player.sendMessage(  "§l" + target.getName() + "§rの役職は" +  message + " §r§lです");


            player.closeInventory();


        }
    }





    @EventHandler
    public void itemClick(PlayerInteractEvent event) {


        ItemStack item = event.getItem();
        NamespacedKey namekey = new NamespacedKey(plugin, "wolfitem");
        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        //人狼
        String wolfItemValue = item.getPersistentDataContainer().get(namekey, PersistentDataType.STRING);



        if (item.getType() == Material.DIAMOND_AXE && item.getPersistentDataContainer().has(namekey) && wolfItemValue.equals("wolf_item") && event.getAction() == Action.RIGHT_CLICK_AIR) {
            //人狼用の斧アイテム処理用メソッド呼び出し
            Player player = event.getPlayer();
            wolfItemSkill(player);


        }else if (item.getType() == Material.ECHO_SHARD && item.getPersistentDataContainer().has(namekey) && wolfItemValue.equals("madman_item") && event.getAction() == Action.RIGHT_CLICK_AIR  ) {

            //狂人アイテム
            Player player = event.getPlayer();

            if (!MiniCashWereWolf.gamePlaying){

                player.sendMessage(
                        MiniCashWereWolf.getMessage(
                                Component.text("現在ゲームが進行中ではありません").color(NamedTextColor.RED)
                        )
                );
                return;
            }

            event.getItem().setAmount(event.getItem().getAmount() - 1);

            madmanItem(event.getPlayer());



        }else if (item.getType() == Material.SHIELD && item.getPersistentDataContainer().has(namekey) && wolfItemValue.equals("knight_item") && event.getAction() == Action.RIGHT_CLICK_AIR  ||  item.getType() == Material.SHIELD && item.getPersistentDataContainer().has(namekey) && wolfItemValue.equals("knight_item") && event.getAction() == Action.RIGHT_CLICK_BLOCK  ) {

            //騎士アイテム
            Player player = event.getPlayer();

            if (!MiniCashWereWolf.gamePlaying){

                player.sendMessage(
                        MiniCashWereWolf.getMessage(
                                Component.text("現在ゲームが進行中ではありません").color(NamedTextColor.RED)
                        )
                );
                return;
            }

            //盾無効化
            event.setCancelled(true);

            event.getItem().setAmount(event.getItem().getAmount() - 1);

            knightitem(player);




        }else if ( item.getType() == Material.AMETHYST_SHARD && item.getPersistentDataContainer().has(namekey) && wolfItemValue.equals("fortune_item") && event.getAction() == Action.RIGHT_CLICK_AIR ) {
            //占い師アイテム
            Player player = event.getPlayer();


            if (!MiniCashWereWolf.gamePlaying){

                player.sendMessage(
                        MiniCashWereWolf.getMessage(
                                Component.text("現在ゲームが進行中ではありません").color(NamedTextColor.RED)
                        )
                );
                return;
            }

            event.getItem().setAmount(event.getItem().getAmount() - 1);


            fortuneItem(player);



        }else if ( item.getType() == Material.NETHER_STAR && item.getPersistentDataContainer().has(namekey) && wolfItemValue.equals("medium_item") && event.getAction() == Action.RIGHT_CLICK_AIR ) {
            //霊媒師アイテム
            Player player = event.getPlayer();

            if (!MiniCashWereWolf.gamePlaying){

                player.sendMessage(
                        MiniCashWereWolf.getMessage(
                                Component.text("現在ゲームが進行中ではありません").color(NamedTextColor.RED)
                        )
                );
                return;
            }

            event.getItem().setAmount(event.getItem().getAmount() - 1);

            UUID  uuid = player.getUniqueId();
            if (headpickupHeadName.containsKey(uuid)) {

                String headname = headpickupHeadName.get(uuid);

                if (headname != null) {

                    player.sendMessage(headname + "を占おうとしてるよ！");
                    Player target = Bukkit.getPlayer(headname);

                    if(target == null){
                        player.sendMessage(
                                MiniCashWereWolf.getMessage(
                                        Component.text("ターゲットが不明です").color(NamedTextColor.RED)
                                )
                        );
                        return;
                    }

                    UUID targetUniqueId = target.getUniqueId();
                    RoleManager.RoleType targetRoleType = RoleManager.getPlayerRole().getOrDefault(targetUniqueId, RoleManager.RoleType.NO);
                    String message;

                    //アイテム削除
                    ItemStack mainItem = player.getInventory().getItemInMainHand();
                    mainItem.setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);

                    //霊媒結果
                    if (targetRoleType.equals(RoleManager.RoleType.WOLF) ) {        //黒陣営処理
                        message = "§4黒陣営";
                    }else if (targetRoleType.equals(RoleManager.RoleType.NO)) {     //白陣営
                        message = "§b§k....§r";
                    }else {
                        message = "§b白陣営";

                    }

                    player.sendMessage(  "§l" + target.getName() + "§rは" +  message + " §r§lでした");


                }


            }else {
                event.getPlayer().sendMessage("§eプレイヤーの墓の付近でアイテムを使用してください!");
            }



        }else if ( item.getType() == Material.LEATHER_HORSE_ARMOR && item.getPersistentDataContainer().has(namekey) && wolfItemValue.equals("people_check") && event.getAction() == Action.RIGHT_CLICK_AIR ) {

            Player player = event.getPlayer();

            if (!MiniCashWereWolf.gamePlaying){

                player.sendMessage(
                        MiniCashWereWolf.getMessage(
                                Component.text("現在ゲームが進行中ではありません").color(NamedTextColor.RED)
                        )
                );
                return;
            }

            event.getItem().setAmount(event.getItem().getAmount() - 1);

            peopleCheck(event.getPlayer());


        } else if (item.getType() == Material.GLOW_INK_SAC && item.getPersistentDataContainer().has(namekey) && wolfItemValue.equals("glowin_item") && event.getAction() == Action.RIGHT_CLICK_AIR ) {

            Player player =  event.getPlayer();

            if (!MiniCashWereWolf.gamePlaying){

                player.sendMessage(
                        MiniCashWereWolf.getMessage(
                                Component.text("現在ゲームが進行中ではありません").color(NamedTextColor.RED)
                        )
                );
                return;
            }

            event.getItem().setAmount(event.getItem().getAmount() - 1);


            //itemの発光用メソッド呼び出し
            Item.glowing();

        }else if (item.getType() == Material.COAL && item.getPersistentDataContainer().has(namekey) && wolfItemValue.equals("smoke_item") && event.getAction() == Action.RIGHT_CLICK_AIR ) {

            //煙幕
            event.getItem().setAmount(event.getItem().getAmount() - 1);

            Player player = event.getPlayer();

            Item.smoke(player);

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
            if (entity instanceof Player player) {

                UUID id = player.getUniqueId();

                RoleManager.RoleType playerRole = RoleManager.getPlayerRole().getOrDefault(id, RoleManager.RoleType.NO);

                if (playerRole.equals(RoleManager.RoleType.WOLF)) {
                    wolflistcount--;
                } else if (!playerRole.equals(RoleManager.RoleType.NO) && !playerRole.equals(RoleManager.RoleType.SPECTATOR)) {
                    villagerlistcount--;
                }

                //勝敗用

                if (wolflistcount == 0) {    //市民勝利

                    wolfmain.villagerWin();

                } else if (villagerlistcount == 0) {

                    wolfmain.wolfWin();

                }




                //死亡地点座標取得
                Location location = event.getEntity().getLocation();

                Player deathpl = (Player) event.getEntity();

                ItemStack playerhead = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta phskullmeta = (SkullMeta) playerhead.getItemMeta();
                phskullmeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING, "Death_Player_Head");

                phskullmeta.setOwningPlayer(deathpl);

                phskullmeta.itemName(Component.text(deathpl.getName()));    //現在処理中のプレイヤー名を入手しそのプレイヤー名を頭にセット

                playerhead.setItemMeta(phskullmeta); //アイテムメタを設定

                location.getWorld().dropItem(location, playerhead);     //PlayerHeadスポーン





            }
        }



        if (event.getEntity() instanceof Skeleton) {
            Random rm = new Random();

            int rmresult = rm.nextInt(3);

            if (event.getEntity() instanceof Skeleton skeleton) {

                String skeltonmvalue = skeleton.getPersistentDataContainer().get(namekey, PersistentDataType.STRING);

                if (rmresult == 1) {   //３分の１の確率でコインをドロップ
                    if (skeltonmvalue.equals("spawncoinskelton")) {
                        // このスケルトンだけ特別処理
                        ItemStack spawngolditem = new ItemStack(Material.GOLD_INGOT);
                        ItemMeta spawngolditemmeta = spawngolditem.getItemMeta();
                        spawngolditemmeta.itemName(Component.text("コイン").color(NamedTextColor.GOLD));
                        spawngolditemmeta.lore(List.of(Component.text("人狼ゲーム専用コイン").color(NamedTextColor.GREEN)));
                        spawngolditemmeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING, "spawn_gold_ingot");
                        spawngolditem.setItemMeta(spawngolditemmeta); //アイテムメタを設定

                        event.getDrops().clear();
                        event.getDrops().add(spawngolditem);
                    }
                } else {     //違う場合でも初期ドロップをなくす
                    event.getDrops().clear();
                }


            }


        }

    }


    @EventHandler
    public void EntityPickup(EntityPickupItemEvent event) {

        if (event.getEntity() instanceof Player player) {

            UUID uuid = player.getUniqueId();

            NamespacedKey namekey = new NamespacedKey(plugin, "wolfitem");

            String headNamespacedKey = event.getItem().getItemStack().getItemMeta().getPersistentDataContainer().get(namekey, PersistentDataType.STRING);

            if (headNamespacedKey != null && headNamespacedKey.equals("Death_Player_Head")) {

                Component itemName = event.getItem().getItemStack().getItemMeta().itemName();

                String headName = PlainTextComponentSerializer.plainText().serialize(itemName);

                event.setCancelled(true);
                headpickupHeadName.put(uuid, headName);

                // プレイヤーがすでに「最近メッセージを受け取ったか」を確認
                if (!player.hasMetadata("grave_msg_cooldown")) {


                    player.sendMessage("§4" + headName + "§rの墓");

                    // プレイヤーに「印」を付ける（値は何でも良い）
                    player.setMetadata("grave_msg_cooldown", new FixedMetadataValue(plugin, true));


                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        player.removeMetadata("grave_msg_cooldown", plugin);

                        headpickupHeadName.put(uuid, null);

                    }, 20 * 2); // 100ティック = 5秒

                }
            }


        }


    }





    @EventHandler
    public void SignChange(SignChangeEvent event) {

        String line1 = PlainTextComponentSerializer.plainText().serialize(event.line(0));
        String line2 = PlainTextComponentSerializer.plainText().serialize(event.line(1));

        if (line1.equals("mwgame")) {

            if (line2.equals("Join")) {

                Component setline1 = Component.text("[mwerewolf]").color(NamedTextColor.GOLD);
                Component setline2 = Component.text("右クリックで").color(NamedTextColor.GREEN);
                Component setline3 = Component.text("人狼ゲーム").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD);
                Component setline4 = Component.text("に参加").color(NamedTextColor.GREEN);


                event.line(0,setline1);
                event.line(1,setline2);
                event.line(2,setline3);
                event.line(3,setline4);
            }else if (line2.equals("Spectator")) {
                //観戦者用

                event.line(0,Component.text("[mwerewolf]").color(NamedTextColor.GOLD));
                event.line(1,Component.text("右クリックで").color(NamedTextColor.GREEN));
                event.line(2,Component.text("観戦者として登録").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
            }else if (line2.equals("Cancel")){
                //登録キャンセル
                event.line(0,Component.text("[mwerewolf]").color(NamedTextColor.GOLD));
                event.line(1,Component.text("右クリックで").color(NamedTextColor.GREEN));
                event.line(2,Component.text("登録をキャンセル").color(NamedTextColor.RED));
            }

        }


    }

//    public static List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

    @EventHandler
    public void clickblock(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {

            Block block = event.getClickedBlock();

            if (block != null && block.getState() instanceof Sign sign){

                List<Component> allLines = sign.getSide(Side.FRONT).lines();

                String line1 = PlainTextComponentSerializer.plainText().serialize(allLines.get(0));
                String line2 = PlainTextComponentSerializer.plainText().serialize(allLines.get(1));
                String line3 = PlainTextComponentSerializer.plainText().serialize(allLines.get(2));
                String line4 = PlainTextComponentSerializer.plainText().serialize(allLines.get(3));

                if (line1.equals("[mwerewolf]") && line2.equals("右クリックで")
                        && line3.equals("人狼ゲーム")
                        && line4.equals("に参加")) {

                    //すでに登録していないかのチェック
                    if (GameManager.getGameplayers().contains(player)) {
                        player.sendMessage(MiniCashWereWolf.getMessage(
                                Component.text("すでに人狼ゲームに登録しています!").color(NamedTextColor.RED)
                        ));
                        return;
                    } else {

                        //ここからゲームに参加準備をした後の処理記述！

                        //参加したいプレイヤーをplayersListに入れる
                        player.sendMessage(MiniCashWereWolf.getMessage(
                                Component.text("人狼ゲームに登録しました").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD)
                        ));
                        GameManager.addGamePlayer(player);

                    }

                }else if (line1.equals("[mwerewolf]") && line2.equals("右クリックで")
                        && line3.equals("観戦者として登録")){
                    //先に普通の登録をしていないかのチェック
                    if (GameManager.getGameplayers().contains(player)) {

                        player.sendMessage(MiniCashWereWolf.getMessage(
                                Component.text("すでに人狼ゲームに登録しています!").color(NamedTextColor.RED)
                        ));

                    }else {

                        //プレイヤーを観戦者として登録
                        RoleManager.playerRoleSet(player , RoleManager.RoleType.SPECTATOR);
                        player.sendMessage(MiniCashWereWolf.getMessage(
                                Component.text("人狼ゲームに").color(NamedTextColor.GREEN)
                                        .append(Component.text("観戦者").color(NamedTextColor.GOLD))
                                        .append(Component.text("として登録しました").color(NamedTextColor.GREEN))
                                        .decorate(TextDecoration.BOLD)
                        ));

                    }
                }else if (line1 .equals("[mwerewolf]") && line2.equals("右クリックで")
                        && line3.equals("登録をキャンセル")){
                        //登録キャンセル
                        //ゲームに登録又は、役職を設定しているのかをチェック

                    if (GameManager.getGameplayers().contains(player)) {

                        GameManager.removeGamePlayer(player);
                        player.sendMessage("§b人狼ゲームの登録を解除しました");

                    }else {

                        //まだ登録していなかったら
                        player.sendMessage(MiniCashWereWolf.getMessage(
                                Component.text("まだ人狼ゲームに登録していません！").color(NamedTextColor.RED)
                        ));

                    }

                }

            }



        }
    }




    @EventHandler
    public void PlayerDeathEvent(PlayerDeathEvent event) {

        if(MiniCashWereWolf.gamePlaying) {
            event.deathMessage(null);
        }
    }



}
