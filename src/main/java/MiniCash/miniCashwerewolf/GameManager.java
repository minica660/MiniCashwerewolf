package MiniCash.miniCashwerewolf;

import MiniCash.miniCashwerewolf.gui.VoteGuiHolder;
import MiniCash.miniCashwerewolf.timer.MeetingTimer;
import MiniCash.miniCashwerewolf.timer.Timer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

import static MiniCash.miniCashwerewolf.timer.MeetingTimer.mstop;
import static MiniCash.miniCashwerewolf.MiniCashWereWolf.*;
import static MiniCash.miniCashwerewolf.timer.Timer.*;
import static MiniCash.miniCashwerewolf.timer.Timer.tstop;

public class GameManager {
    private static MiniCashWereWolf plugin;

    // ゲームに参加する(している)プレイヤー保存
    // ゲーム開始前は参加しようとしているプレイヤー一覧
    // ゲーム開始後は観戦者を除いたゲームに参加しているプレイヤー
    private static List<Player> gameplayers = new ArrayList<>();

    //各役職のプレイヤーが保存されている
    private static List<Player> wolfs = new ArrayList<>(); //人狼定義
    private static List<Player> madmans = new ArrayList<>();
    private static List<Player> knights = new ArrayList<>();
    private static List<Player> fortunes = new ArrayList<>();
    private static List<Player> mediums = new ArrayList<>();
    private static List<Player> villagers = new ArrayList<>();
    private static List<Player> spectators = new ArrayList<>();

    private World gameWorld;

    public GameManager(MiniCashWereWolf plugin){
        GameManager.plugin = plugin;
    }

    public static List<Player> getGameplayers(){
        return gameplayers;
    }

    public static void addGamePlayer(Player player){
        gameplayers.add(player);
    }

    public static void removeGamePlayer(Player player){
        gameplayers.remove(player);
    }



    //アイテム付与
    public static void distributionItem(){


        //人狼
        if (RoleManager.activeRole(RoleManager.RoleType.WOLF)) {

            wolfs.forEach(player ->  player.getInventory().addItem(GameItem.createItem("wolf",1))); //アイテム人狼に付与
        }

        //狂人
        if (RoleManager.activeRole(RoleManager.RoleType.MADMAN)) {

            madmans.forEach(player ->  player.getInventory().addItem(GameItem.createItem("madman",1))); //アイテム付与

        }

        //騎士
        if (RoleManager.activeRole(RoleManager.RoleType.KNIGHT)) {

            knights.forEach(player -> player.getInventory().addItem(GameItem.createItem("knight",1))); //アイテム付与

        }

        //占い師
        if (RoleManager.activeRole(RoleManager.RoleType.FORTUNE)) {

            fortunes.forEach(player ->  player.getInventory().addItem(GameItem.createItem("fortunecheck",1))); //アイテム付与
        }

        //霊媒師
        if (RoleManager.activeRole(RoleManager.RoleType.MEDIUM)){

            mediums.forEach(player ->  player.getInventory().addItem(GameItem.createItem("mediumcheck",1))); //アイテム付与
        }





    }


    //ホワイトリスト関連
    public static void gameSetWhitelist(){

        for (Player player : Bukkit.getOnlinePlayers()){

            UUID uuid = player.getUniqueId();

            if (RoleManager.playerHasGameRole(uuid)){

                gameplayers.add(player);
                player.setWhitelisted(true);

            }else {

                player.setWhitelisted(false);
                player.kick(
                        Component.text("ゲームが開始されました。\nゲーム終了までお待ちください。").color(NamedTextColor.RED)
                );
            }


            Bukkit.setWhitelist(true);  //ホワイトリスト有効化

        }



    }

    public static int wolflistcount = 0;
    public static int villagerlistcount = 0;
    //ゲームスタート
    public void gameStart(CommandSender sender){

        //ホワイトリストとkick処理
        gameSetWhitelist();



        //スタート時のスポーン
        int stpX = plugin.startpointX;
        int stpY = plugin.startpointY;
        int stpZ = plugin.startpointZ;

        gameWorld = Bukkit.getWorld(plugin.startpointworld);
        Location location = new Location(gameWorld,stpX,stpY,stpZ);

        if (gameWorld == null){
            sender.sendMessage(
                    MiniCashWereWolf.getMessage(Component.text("ゲームワールドがnullだったためゲームが開始されませんでした").color(NamedTextColor.RED))
            );
            return;
        }

        //テレポート
        for (Player onlineplayer : Bukkit.getOnlinePlayers()) {
            onlineplayer.teleport(location);
            onlineplayer.setGameMode(GameMode.ADVENTURE);
        }


        //観戦者のみスペクテイターモードに変更(観戦者がいたら...)
        if (RoleManager.activeRole(RoleManager.RoleType.SPECTATOR)) {

            spectators.forEach(player -> player.setGameMode(GameMode.SPECTATOR));
        }


        //時間を昼に変更
        gameWorld.setTime(1000);



        for (Player player : Bukkit.getOnlinePlayers()){

            UUID uuid  = player.getUniqueId();

            RoleManager.RoleType roleType = RoleManager.getPlayerRole().get(uuid);


            //人狼だったら
            if (roleType == RoleManager.RoleType.WOLF){
                wolflistcount++;
            }else if (roleType != RoleManager.RoleType.SPECTATOR){     //市民陣営の人数の合計をチェック
                villagerlistcount++;
            }

            if (roleType.equals(RoleManager.RoleType.WOLF)){
                wolfs.add(player);
            }else if (roleType.equals(RoleManager.RoleType.MADMAN)){
                madmans.add(player);
            }else if (roleType.equals(RoleManager.RoleType.KNIGHT)){
                knights.add(player);
            }else if (roleType.equals(RoleManager.RoleType.FORTUNE)){
                fortunes.add(player);
            }else if (roleType.equals(RoleManager.RoleType.MEDIUM)){
                mediums.add(player);
            } else if (roleType.equals(RoleManager.RoleType.VILLAGER)) {
                villagers.add(player);
            }else if (roleType.equals(RoleManager.RoleType.SPECTATOR)){
                spectators.add(player);
            }


        }




        //1日目に
        day++;
        plugin.getServer().broadcast(Component.text(day + "日目になりました"));

        //ゲームスタート状態に
        gamePlaying = true;
        //タイマースタート
        new Timer(plugin,this).runTaskTimer(plugin,0L,20L);
        nowtime = true;

        sender.sendMessage(
                MiniCashWereWolf.getMessage(
                        Component.text("人狼ゲームを開始させました！").color(NamedTextColor.YELLOW)
                )
        );




        //役職ごとのメッセージ
        //人狼
        if (RoleManager.activeRole(RoleManager.RoleType.WOLF)) {
            for(Player wolf : wolfs) {
                wolf.sendMessage("§c§lあなたは人狼になりました");
                wolf.sendMessage("        §7[§a§l役職説明§r§7]         ");
                wolf.sendMessage("他陣営に気づかれないよう倒しましょう!");
                wolf.sendMessage("§lアイテムが配られました");
            }
        }

        //狂人
        if (RoleManager.activeRole(RoleManager.RoleType.MADMAN)) {
            for(Player madman : madmans) {
                madman.sendMessage("§4あなたは狂人になりました");
                madman.sendMessage("        §7[§a§l役職説明§r§7]         ");
                madman.sendMessage("他陣営に気づかれないよう味方の人狼を見つけ出し協力して他陣営を倒そう！");
                madman.sendMessage("§lアイテムが配られました");

            }
        }

        //騎士
        if (RoleManager.activeRole(RoleManager.RoleType.KNIGHT)) {

            for(Player knight : knights) {
                knight.sendMessage("§bあなたは騎士になりました");
                knight.sendMessage("        §7[§a§l役職説明§r§7]         ");
                knight.sendMessage("味方を守ろう！");
                knight.sendMessage("§lアイテムが配られました");

            }
        }

        //占い師
        if (RoleManager.activeRole(RoleManager.RoleType.FORTUNE)){

            for(Player fortune : fortunes) {
                fortune.sendMessage("§bあなたは占い師になりました");
                fortune.sendMessage("        §7[§a§l役職説明§r§7]         ");
                fortune.sendMessage("怪しいプレイヤーを見つけろ");
                fortune.sendMessage("§lアイテムが配られました");
            }

        }

        //霊媒師
        if (RoleManager.activeRole(RoleManager.RoleType.MEDIUM)) {

            for(Player medium : mediums) {
                medium.sendMessage("§bあなたは霊媒師になりました");
                medium.sendMessage("        §7[§a§l役職説明§r§7]         ");
                medium.sendMessage("怪しいプレイヤー....§kaaaaaa");
                medium.sendMessage("§lアイテムが配られました");
            }

        }

        //市民
        if (RoleManager.activeRole(RoleManager.RoleType.VILLAGER)){

            for(Player villager : villagers) {
                villager.sendMessage("§bあなたは市民になりました");
                villager.sendMessage("        §7[§a§l役職説明§r§7]         ");
                villager.sendMessage("              逃げろ");
            }

        }


        //観戦者
        if (RoleManager.activeRole(RoleManager.RoleType.SPECTATOR)) {

            for (Player spectator : spectators) {
                spectator.sendMessage("§aあなたは観戦者になりました");
                spectator.sendMessage("        §7[§a§l役職説明§r§7]         ");
                spectator.sendMessage("              §kaaaaaa");
            }

        }





    }

    public void gameStop(){
        //ホワイトリスト解除
        Bukkit.setWhitelist(false);

        plugin.getLogger().info("[§aMiniCashwerewolf§r] §lホワイトリストをoffにしました");


        //ゲーム実行中をfalseに変更
        gamePlaying = false;

        RoleManager.setCheckList();

        plugin.getLogger().info("[§aMiniCashwerewolf§r] §lゲーム終了処理がすべて完了しました");


        //役職用Playerデータ型をリセット
        wolfs.clear();
        madmans.clear();
        knights.clear();
        fortunes.clear();
        mediums.clear();
        villagers.clear();
        spectators.clear();

        RoleManager.getPlayerRole().clear();

        gameplayers.clear();

    }



    //stopコマンド実装
    public void commandGameStop(CommandSender sender){

        if (gamePlaying) {


            tstop = true;
            mstop = true;


            //オンラインプレイヤー全員にタイトルを表示
            for (Player onlinep : Bukkit.getOnlinePlayers()) {
                Title title = Title.title(
                        Component.text("aaa").decorate(TextDecoration.OBFUSCATED).append(
                                Component.text("引き分け!!").color(NamedTextColor.YELLOW)
                                        .append(
                                                Component.text("aaa").decorate(TextDecoration.OBFUSCATED)
                                        )
                        ),
                        Component.text("")
                );

                onlinep.showTitle(title);
                onlinep.setGameMode(GameMode.SPECTATOR);
                onlinep.setWhitelisted(false);
            }


            sender.sendMessage(
                    MiniCashWereWolf.getMessage(
                            Component.text("ゲームを終了させました").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD)
                    )
            );

            gameStop();



        }else {
            sender.sendMessage("§c§l現在ゲームが進行中ではありません!\nゲームが進行中のみこのコマンドを実行できます");
        }
    }


    //昼と夜の移り変わり
    public void day(){

        plugin.getServer().broadcast(Component.text("昼になりました").color(NamedTextColor.GOLD));
        plugin.getServer().broadcast(Component.text("マイクをONにして話し合いましょう").decorate(TextDecoration.BOLD));

        //タイマースタート
        new Timer(plugin,this).runTaskTimer(plugin,0L,20L);

        //オンラインプレイヤー全員にタイトルを表示
        for (Player titleonlinep : Bukkit.getOnlinePlayers()){

            Title title = Title.title(
                    Component.text("マイクをONにして話し合いましょう").color(NamedTextColor.YELLOW),
                    Component.text("")
            );

            titleonlinep.showTitle(title);
        }





        //時間を昼に変更
        gameWorld.setTime(1000);






    }

    public void noon(){

        plugin.getServer().broadcast(Component.text("夜になりました").color(NamedTextColor.DARK_PURPLE));
        plugin.getServer().broadcast(Component.text("マイクをOFFにしてください").decorate(TextDecoration.BOLD));

        //タイマースタート
        new Timer(plugin,this).runTaskTimer(plugin,0L,20L);

        //オンラインプレイヤー全員にタイトルを表示
        for (Player titleonlinep : Bukkit.getOnlinePlayers()){
            Title title = Title.title(
                    Component.text("マイクをOFFにしましょう").color(NamedTextColor.DARK_GRAY)
                    , Component.text("")
            );
            titleonlinep.showTitle(title);
        }


        //時間を夜に変更

        gameWorld.setTime(18000);


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

        plugin.getServer().broadcast(Component.text("会議が開始されました").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
        plugin.getServer().broadcast(Component.text("残り20秒で投票が行われます").color(NamedTextColor.GREEN));
        plugin.getServer().broadcast(Component.text("怪しいと思うプレイヤーに投票してください").color(NamedTextColor.RED));




        //タイマー
        new MeetingTimer(plugin,this).runTaskTimer(plugin,0L,20L);



    }

    int addMeetingVoteCheck;

    public void vote(){

        addMeetingVoteCheck = 0;

        plugin.getServer().broadcast(Component.text("投票が開始されました").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
        plugin.getServer().broadcast(Component.text("時間内に投票を行いましょう").color(NamedTextColor.GREEN));
        plugin.getServer().broadcast(Component.text("怪しいと思うプレイヤーに投票してください").color(NamedTextColor.RED));



        Inventory voteGUI = Bukkit.createInventory(new VoteGuiHolder(),27,Component.text("プレイヤー投票"));  //サイズ9*○○

        int count = 0; //GUIに設置した数カウント用
        int onlinecount = Bukkit.getOnlinePlayers().size();
        for (Player player: Bukkit.getOnlinePlayers()){
            if (count <= onlinecount) {
                ItemStack playerhead = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta phskullmeta = (SkullMeta) playerhead.getItemMeta();

                phskullmeta.setOwningPlayer(player);

                phskullmeta.itemName(Component.text(player.getName()));    //現在処理中のプレイヤー名を入手しそのプレイヤー名を頭にセット
                phskullmeta.lore(List.of(Component.text("クリックで" + player.getName() + "に投票").color(NamedTextColor.GOLD)));

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

            cancelitem.itemName(Component.text("投票をキャンセル"));

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

    private final Map<String,Integer> meetingvotecheck = new HashMap<>();
    //会議投票クリックの処理
    public String voteGo(String nameVote){

        //Mapにプレイヤーごと..＋＋
        addMeetingVoteCheck = meetingvotecheck.getOrDefault(nameVote,0);

        plugin.getServer().broadcast(Component.text("現在：" + addMeetingVoteCheck));


        addMeetingVoteCheck++;
        meetingvotecheck.put(nameVote, addMeetingVoteCheck);


        return "§e" + nameVote + "§r§7に投票しました";

    }


    public void voteResult(){

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

                plugin.getServer().broadcast(Component.text(targetplayer.getName() + "§r§cは投票によって追放されました").color(NamedTextColor.YELLOW));
            }
        }else {
            plugin.getServer().broadcast(Component.text("プレイヤーが見つからなかったため誰も追放されませんでした").color(NamedTextColor.RED).decorate(TextDecoration.BOLD));
        }

        plugin.getServer().getOnlinePlayers().forEach(HumanEntity::closeInventory);



    }

    //人狼陣営勝利のストップ
    public void wolfWin(){

        tstop = true;
        mstop = true;

        //オンラインプレイヤー全員にタイトルを表示
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()){

            Title title = Title.title(
                    Component.text("人狼陣営の勝利！！")
                            .color(NamedTextColor.DARK_RED)
                            .decorate(TextDecoration.BOLD),
                    Component.text("市民陣営の敗北...")
                            .color(NamedTextColor.DARK_GRAY)
                            .decoration(TextDecoration.ITALIC, false)

            );
            onlinePlayer.showTitle(title);
            onlinePlayer.setGameMode(GameMode.SPECTATOR);
            onlinePlayer.setWhitelisted(false);
            onlinePlayer.sendMessage("§e§nゲーム終了！！");
            onlinePlayer.sendMessage("§e§nゲーム終了！！");
            onlinePlayer.sendMessage("§e§nゲーム終了！！");
            onlinePlayer.sendMessage("§e§nゲーム終了！！");
            onlinePlayer.sendMessage("§e§nゲーム終了！！");
            onlinePlayer.sendMessage("§e§nゲーム終了！！");
            onlinePlayer.sendMessage("§e§nゲーム終了！！");
            onlinePlayer.sendMessage("§e§nゲーム終了！！");
            onlinePlayer.sendMessage("§e§nゲーム終了！！");
            onlinePlayer.sendMessage("§e§nゲーム終了！！");
            onlinePlayer.sendMessage("§e§nゲーム終了！！");
            onlinePlayer.sendMessage("§e§nゲーム終了！！");

        }

        plugin.getLogger().info("人狼側の勝利！\nゲームが終了しました");

        gameStop();

    }

    //人狼陣営勝利のストップ
    public void villagerWin(){

        tstop = true;
        mstop = true;

        //オンラインプレイヤー全員にタイトルを表示
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()){

            Title title = Title.title(
                    Component.text("市民陣営の勝利！！")
                            .color(NamedTextColor.DARK_PURPLE)
                            .decorate(TextDecoration.BOLD),
                    Component.text("人狼陣営の敗北...")
                            .color(NamedTextColor.DARK_GRAY)
                            .decoration(TextDecoration.ITALIC, false)
            );


            onlinePlayer.showTitle(title);
            onlinePlayer.setGameMode(GameMode.SPECTATOR);
            onlinePlayer.setWhitelisted(false);
            onlinePlayer.sendMessage("§e§nゲーム終了！！");
            onlinePlayer.sendMessage("§e§nゲーム終了！！");
            onlinePlayer.sendMessage("§e§nゲーム終了！！");
            onlinePlayer.sendMessage("§e§nゲーム終了！！");
            onlinePlayer.sendMessage("§e§nゲーム終了！！");
            onlinePlayer.sendMessage("§e§nゲーム終了！！");
            onlinePlayer.sendMessage("§e§nゲーム終了！！");
            onlinePlayer.sendMessage("§e§nゲーム終了！！");
            onlinePlayer.sendMessage("§e§nゲーム終了！！");
            onlinePlayer.sendMessage("§e§nゲーム終了！！");
            onlinePlayer.sendMessage("§e§nゲーム終了！！");
            onlinePlayer.sendMessage("§e§nゲーム終了！！");
        }

        plugin.getLogger().info("村人側の勝利！\nゲームが終了しました");


        gameStop();
    }
}
