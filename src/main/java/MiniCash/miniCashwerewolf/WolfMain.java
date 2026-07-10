package MiniCash.miniCashwerewolf;

import MiniCash.miniCashwerewolf.gui.VoteGuiHolder;
import MiniCash.miniCashwerewolf.timer.MeetingTimer;
import MiniCash.miniCashwerewolf.timer.Timer;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static MiniCash.miniCashwerewolf.Event.Event.players;
import static MiniCash.miniCashwerewolf.timer.MeetingTimer.mstop;
import static MiniCash.miniCashwerewolf.MiniCashwerewolf.madman;
import static MiniCash.miniCashwerewolf.MiniCashwerewolf.*;
import static MiniCash.miniCashwerewolf.timer.Timer.*;
import static MiniCash.miniCashwerewolf.timer.Timer.tstop;

public class WolfMain {
    private static MiniCashwerewolf plugin = null;


    public WolfMain(MiniCashwerewolf plugin){
        WolfMain.plugin = plugin;
    }




    //アイテム付与
    public static void distributionItem(){


        //人狼
        if (RoleManager.activeRole(RoleManager.RoleType.WOLF)) {

            wolf.getInventory().addItem(GameItem.createItem("wolf",1)); //アイテム人狼に付与
        }

        //狂人
        if (RoleManager.activeRole(RoleManager.RoleType.MADMAN)) {

            madman.getInventory().addItem(GameItem.createItem("madman",1)); //アイテム付与

        }

        //騎士
        if (plugin.getConfig().getBoolean("knight.check")) {

            ItemStack knightitem = new ItemStack(Material.SHIELD, 1);

            ItemMeta knightitemmeta = knightitem.getItemMeta();
            knightitemmeta.setDisplayName("§5守りの盾");
            knightitemmeta.setLore(List.of("§6右クリックで使用可能"));
            knightitemmeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING, "knight_item");


            knightitem.setItemMeta(knightitemmeta); //アイテムメタを設定
            knight.getInventory().addItem(knightitem); //アイテム付与
        }

        //占い師
        if (plugin.getConfig().getBoolean("fortune.check")){

            ItemStack fortuneitem = new ItemStack(Material.AMETHYST_SHARD, 1);

            ItemMeta fortuneitemmeta = fortuneitem.getItemMeta();
            fortuneitemmeta.setDisplayName("§5§l占い");
            fortuneitemmeta.setLore(List.of("§6右クリックで使用可能"));
            fortuneitemmeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING, "fortune_item");


            fortuneitem.setItemMeta(fortuneitemmeta); //アイテムメタを設定
            fortune.getInventory().addItem(fortuneitem); //アイテム付与
        }

        //霊媒師
        if (plugin.getConfig().getBoolean("medium.check")){

            ItemStack mediumitem = new ItemStack(Material.NETHER_STAR, 1);

            ItemMeta mediumitemmeta = mediumitem.getItemMeta();
            mediumitemmeta.setDisplayName("§5§l霊媒師用のアイテム");
            mediumitemmeta.setLore(List.of("§6右クリックで使用可能"));
            mediumitemmeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING, "medium_item");


            mediumitem.setItemMeta(mediumitemmeta); //アイテムメタを設定
            medium.getInventory().addItem(mediumitem); //アイテム付与
        }





    }

    private static Player gameplayer;

    //ホワイトリスト関連
    public static void whitelistp(){

        for (Player player : Bukkit.getOnlinePlayers()){

            UUID id = player.getUniqueId();

            int positioncheck = position.getOrDefault(id,0);

            //ここからの処理の意味
            //ゲームに参加するプレイヤー以外かの確認
            if (positioncheck >= 1){
                gameplayer = Bukkit.getPlayer(id);
                gameplayer.setWhitelisted(true);     //ホワイトリストに追加
            }else if (positioncheck == 0){     //参加しないプレイヤーはkick
                player.setWhitelisted(false);
                player.kick(Component.text("§cゲームが開始されました。ゲーム終了までお待ちください。"));
            }

            Bukkit.setWhitelist(true);  //ホワイトリスト有効化

        }



    }

    public static int wolflistcount = 0;
    public static int villagerlistcount = 0;
    //ゲームスタート
    public void gstart(Player player){

        //ホワイトリストとkick処理
        whitelistp();


        player.sendMessage("§e人狼ゲームを開始させました！");

        //1日目に
        day++;
        Bukkit.broadcastMessage(day + "日目になりました");



        //スタート時のスポーン
        int stpX = plugin.startpointX;
        int stpY = plugin.startpointY;
        int stpZ = plugin.startpointZ;

        World world = Bukkit.getWorld(plugin.startpointworld);
        Location location = new Location(world,stpX,stpY,stpZ);

        //テレポート
        for (Player onlineplayer : Bukkit.getOnlinePlayers()) {
            onlineplayer.teleport(location);
            onlineplayer.setGameMode(GameMode.ADVENTURE);
        }


        //観戦者のみスペクテイターモードに変更(観戦者がいたら...)
        if (plugin.getConfig().getBoolean("spectator.check")) {
            spectator.setGameMode(GameMode.SPECTATOR);
        }


        //時間を昼に変更
        world.setTime(1000);



        //勝利のための
        for (Player onlineplayer : Bukkit.getOnlinePlayers()){

            UUID id  = onlineplayer.getUniqueId();

            int getpotision = position.get(id);

            //人狼だったら＋１（狂人などは人狼認定されないため別）
            if (getpotision == 1){
                wolflistcount++;
            }else if (getpotision >= 3 && getpotision <=6){     //市民陣営の人数の合計をチェック
                villagerlistcount++;
            }



        }



        //ゲームスタート状態に
        gamePlaying = true;





        //役職ごとのメッセージ
        //人狼
        if (plugin.getConfig().getBoolean("wolf.check")) {
            wolf.sendMessage("§c§lあなたは人狼になりました");
            wolf.sendMessage("        §7[§a§l役職説明§r§7]         ");
            wolf.sendMessage("他陣営に気づかれないよう倒しましょう!");
            wolf.sendMessage("§lアイテムが配られました");
        }

        //狂人
        if (plugin.getConfig().getBoolean("madman.check")) {
            madman.sendMessage("§4あなたは狂人になりました");
            madman.sendMessage("        §7[§a§l役職説明§r§7]         ");
            madman.sendMessage("他陣営に気づかれないよう味方の人狼を見つけ出し協力して他陣営を倒そう！");
            madman.sendMessage("§lアイテムが配られました");

        }

        //騎士
        if (plugin.getConfig().getBoolean("knight.check")) {
            knight.sendMessage("§bあなたは騎士になりました");
            knight.sendMessage("        §7[§a§l役職説明§r§7]         ");
            knight.sendMessage("味方を守ろう！");
            knight.sendMessage("§lアイテムが配られました");

        }

        //占い師
        if (plugin.getConfig().getBoolean("fortune.check")){
            fortune.sendMessage("§bあなたは占い師になりました");
            fortune.sendMessage("        §7[§a§l役職説明§r§7]         ");
            fortune.sendMessage("怪しいプレイヤーを見つけろ");
            fortune.sendMessage("§lアイテムが配られました");

        }

        //霊媒師
        if (plugin.getConfig().getBoolean("medium.check")){
            medium.sendMessage("§bあなたは霊媒師になりました");
            medium.sendMessage("        §7[§a§l役職説明§r§7]         ");
            medium.sendMessage("怪しいプレイヤー....§kaaaaaa");
            medium.sendMessage("§lアイテムが配られました");

        }

        //市民
        if (plugin.getConfig().getBoolean("villager.check")){
            villager.sendMessage("§bあなたは市民になりました");
            villager.sendMessage("        §7[§a§l役職説明§r§7]         ");
            villager.sendMessage("              逃げろ");

        }


        //観戦者
        if (plugin.getConfig().getBoolean("spectator.check")){

            spectator.sendMessage("§aあなたは観戦者になりました");
            spectator.sendMessage("        §7[§a§l役職説明§r§7]         ");
            spectator.sendMessage("              §kaaaaaa");
        }







        //タイマースタート
        new Timer(plugin,this).runTaskTimer(MiniCashwerewolf.getPlugin(),0L,20L);
        nowtime = true;

    }

    public void gamestop(){
        //ホワイトリスト解除
        Bukkit.setWhitelist(false);

        Bukkit.getLogger().info("[§aMiniCashwerewolf§r] §lホワイトリストをoffにしました");


        //ゲーム実行中をfalseに変更
        gamePlaying = false;

        plugin.addchecklist();

        plugin.getLogger().info("[§aMiniCashwerewolf§r] §lゲーム終了処理がすべて完了しました");


        //役職用Playerデータ型をリセット
        wolf = null;
        madman = null;
        knight = null;
        fortune = null;
        medium = null;
        villager = null;
        spectator = null;

        position.clear();

        players.clear();
    }



    //stopコマンド実装
    public void gstop(Player player){

        if (gamePlaying) {


            tstop = true;
            mstop = true;


            //オンラインプレイヤー全員にタイトルを表示
            for (Player onlinep : Bukkit.getOnlinePlayers()) {
                onlinep.sendTitle("§kaaa§r§e引き分け！！§kaaa§r", "", 10, 70, 20);
                onlinep.setGameMode(GameMode.SPECTATOR);
                onlinep.setWhitelisted(false);
            }


            player.sendMessage("§6§lゲームを停止させました！");

            gamestop();



        }else {
            player.sendMessage("§c§l現在ゲームが進行中ではありません!\nゲームが進行中のみこのコマンドを実行できます");
        }
    }


    //昼と夜の移り変わり
    public void day(){

        Bukkit.broadcastMessage("§6昼になりました");
        Bukkit.broadcastMessage("§lマイクをONにして話し合いましょう");

        //タイマースタート
        new Timer(plugin,this).runTaskTimer(MiniCashwerewolf.getPlugin(),0L,20L);

        //オンラインプレイヤー全員にタイトルを表示
        for (Player titleonlinep : Bukkit.getOnlinePlayers()){
            titleonlinep.sendTitle("§eマイクをONにして話し合いましょう","",10,70,20);
        }


        //2日目の昼からは会議場所から初期地点へのスポーン
//            if (mcheck >= 1 ){
//                //スタート時のスポーン
//
//                int stpX = startpointX;
//                int stpY = startpointY;
//                int stpZ = startpointZ;
//
//                World world = Bukkit.getWorld(startpointworld);
//                Location location = new Location(world,stpX,stpY,stpZ);
//
//                //テレポート
//                for (Player onlineplayer : Bukkit.getOnlinePlayers()) {
//                    onlineplayer.teleport(location);
//                }
//            }


        //時間を昼に変更
        World world = Bukkit.getWorld(plugin.startpointworld);
        world.setTime(1000);






    }

    public void noon(){

        Bukkit.broadcastMessage("§5夜になりました");
        Bukkit.broadcastMessage("§lマイクをOFFにしてください");

        //タイマースタート
        new Timer(plugin,this).runTaskTimer(MiniCashwerewolf.getPlugin(),0L,20L);

        //オンラインプレイヤー全員にタイトルを表示
        for (Player titleonlinep : Bukkit.getOnlinePlayers()){
            titleonlinep.sendTitle("§8マイクをOFFにしましょう","",10,70,20);
        }


        //時間を夜に変更

        World world = Bukkit.getWorld(plugin.startpointworld);
        world.setTime(18000);


        //人狼に対して


    }

    //会議
    public void meeting(){

        //会議地点へテレポート
        int mtgX = plugin.meetingpointX;
        int mtgY = plugin.meetingpointY;
        int mtgZ = plugin.meetingpointZ;

        World world = Bukkit.getWorld(plugin.meetingpointworld);
        Location location = new Location(world,mtgX,mtgY,mtgZ);

        //テレポート
        for (Player onlineplayer : Bukkit.getOnlinePlayers()) {
            onlineplayer.teleport(location);
        }

        Bukkit.broadcastMessage("§a§l会議が開始されました");
        Bukkit.broadcastMessage("§a残り２０秒で投票が行われます");
        Bukkit.broadcastMessage("§c怪しいと思うプレイヤーに投票してください");

        //Component vmessage = Component.text("MiniCash").color(NamedTextColor.GREEN).clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand("/say hellotest"));
        //Bukkit.getServer().sendMessage(vmessage);

        //TextComponent votemessage = null;
        //プレイヤー通知(クリック可能)
        //for (Player player : Bukkit.getOnlinePlayers()) {
        //    votemessage = new TextComponent("§a" + player);
        //
        //    player.spigot().sendMessage(votemessage);
        //}

        //votemessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/say hello"));


        //タイマー
        new MeetingTimer(plugin,this).runTaskTimer(MiniCashwerewolf.getPlugin(),0L,20L);



    }
    int addmeetingvotecheckmap;

    public void vote(){

        addmeetingvotecheckmap = 0;

        Bukkit.broadcastMessage("§a§l投票が開始されました");
        Bukkit.broadcastMessage("§a時間内に投票を行いましょう");
        Bukkit.broadcastMessage("§c怪しいと思うプレイヤーに投票してください");


        Inventory voteGUI = Bukkit.createInventory(new VoteGuiHolder(),27,"プレイヤー投票");  //サイズ9*○○

        int count = 0; //GUIに設置した数カウント用
        int onlinecount = Bukkit.getOnlinePlayers().size();
        for (Player player: Bukkit.getOnlinePlayers()){
            if (count <= onlinecount) {
                ItemStack playerhead = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta phskullmeta = (SkullMeta) playerhead.getItemMeta();

                phskullmeta.setOwningPlayer(player);

                phskullmeta.setDisplayName(player.getName());    //現在処理中のプレイヤー名を入手しそのプレイヤー名を頭にセット
                phskullmeta.setLore(List.of("§6クリックで" + player.getName() + "に投票"));

                playerhead.setItemMeta(phskullmeta); //アイテムメタを設定

                //今のアイテムをスロットに設定
                voteGUI.setItem(count,playerhead);

                //処理した数を次のために１＋
                count++;


                //Map
                UUID id = player.getUniqueId();
                guicheck.put(id,1);  //イベントリセット
            }

            ItemStack cancelvote= new ItemStack(Material.REDSTONE);
            ItemMeta cancelitem = cancelvote.getItemMeta();

            cancelitem.setDisplayName("§c投票をキャンセル");

            cancelvote.setItemMeta(cancelitem);

            //スロットに設置
            voteGUI.setItem(26,cancelvote);


        }

        //プレイヤーにGUI表示
        for (Player py:Bukkit.getOnlinePlayers()){
            //全プレイヤーに投票用GUI表示
            py.sendMessage("投票用GUIOpenまで到達したよ！");
            py.openInventory(voteGUI);
        }
    }

    private Map<String,Integer> meetingvotecheck = new HashMap<>();
    //会議投票クリックの処理
    public String votego(String nameVote){

        //Mapにプレイヤーごと..＋＋
        addmeetingvotecheckmap = meetingvotecheck.getOrDefault(nameVote,0);

        Bukkit.broadcastMessage("現在：" + addmeetingvotecheckmap);

        addmeetingvotecheckmap++;
        meetingvotecheck.put(nameVote,addmeetingvotecheckmap);

        //return文
        String retrunstring = "§e" + nameVote + "§r§7に投票しました";
        return retrunstring;

    }


    public void voteresult(){

        String maxname = null;
        int maxvalue = 0;
        for (Map.Entry<String, Integer> entry : meetingvotecheck.entrySet()){

            String name = entry.getKey();
            int valuecount = entry.getValue();
            //現在のmaxvalueより大きいものが見つかった場合
            if (valuecount > maxvalue){
                maxvalue = valuecount;
                maxname = name;

            }
        }

        //投票数が一番多いプレイヤーをキル
        if (maxname != null) {

            Player targetplayer = Bukkit.getPlayer(maxname);
            if (targetplayer != null) {
                //キル
                targetplayer.setHealth(0.0);
                //スペクテイターモードに
                targetplayer.setGameMode(GameMode.SPECTATOR);
                //通リ
                Bukkit.broadcastMessage("§e" + targetplayer.getName() + "§r§cは投票によって追放されました");
            }
        }else {
            Bukkit.broadcastMessage("§c§lプレイヤーが見つからなかったため誰も追放されませんでした");
        }

        for (Player player : Bukkit.getOnlinePlayers()){

            player.closeInventory();

        }

    }

    //人狼陣営勝利のストップ
    public void wolfwin(){

        tstop = true;
        mstop = true;

        //オンラインプレイヤー全員にタイトルを表示
        for (Player onlinep : Bukkit.getOnlinePlayers()){
            onlinep.sendTitle("§4§l人狼陣営の勝利！！","§8市民陣営の敗北...",10,70,20);
            onlinep.setGameMode(GameMode.SPECTATOR);
            onlinep.setWhitelisted(false);
            onlinep.sendMessage("§e§nゲーム終了！！");
            onlinep.sendMessage("§e§nゲーム終了！！");
            onlinep.sendMessage("§e§nゲーム終了！！");
            onlinep.sendMessage("§e§nゲーム終了！！");
            onlinep.sendMessage("§e§nゲーム終了！！");
            onlinep.sendMessage("§e§nゲーム終了！！");
            onlinep.sendMessage("§e§nゲーム終了！！");
            onlinep.sendMessage("§e§nゲーム終了！！");
            onlinep.sendMessage("§e§nゲーム終了！！");
            onlinep.sendMessage("§e§nゲーム終了！！");
            onlinep.sendMessage("§e§nゲーム終了！！");
            onlinep.sendMessage("§e§nゲーム終了！！");
        }

        Bukkit.getLogger().info("[§aMiniCashwerewolf§r] §l人狼側の勝利！\nゲームが終了しました");

        gamestop();

    }

    //人狼陣営勝利のストップ
    public void villagerwin(){

        tstop = true;
        mstop = true;

        //オンラインプレイヤー全員にタイトルを表示
        for (Player onlinep : Bukkit.getOnlinePlayers()){
            onlinep.sendTitle("§5§l市民陣営の勝利！！","§8人狼陣営の敗北...",10,70,20);
            onlinep.setGameMode(GameMode.SPECTATOR);
            onlinep.setWhitelisted(false);
            onlinep.sendMessage("§e§nゲーム終了！！");
            onlinep.sendMessage("§e§nゲーム終了！！");
            onlinep.sendMessage("§e§nゲーム終了！！");
            onlinep.sendMessage("§e§nゲーム終了！！");
            onlinep.sendMessage("§e§nゲーム終了！！");
            onlinep.sendMessage("§e§nゲーム終了！！");
            onlinep.sendMessage("§e§nゲーム終了！！");
            onlinep.sendMessage("§e§nゲーム終了！！");
            onlinep.sendMessage("§e§nゲーム終了！！");
            onlinep.sendMessage("§e§nゲーム終了！！");
            onlinep.sendMessage("§e§nゲーム終了！！");
            onlinep.sendMessage("§e§nゲーム終了！！");
        }

        gamestop();
    }
}
