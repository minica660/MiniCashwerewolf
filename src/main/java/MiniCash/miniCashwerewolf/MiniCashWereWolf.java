package MiniCash.miniCashwerewolf;


import MiniCash.miniCashwerewolf.Event.Event;
import MiniCash.miniCashwerewolf.Event.Item;
import MiniCash.miniCashwerewolf.command.GameCommand;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;


import java.util.*;

public final class MiniCashWereWolf extends JavaPlugin {

    private GameManager wolfmain;
    private Villager Cvillager;
    private GameItem gameitem;

    private List<String> helpMessage = new ArrayList<>();

    @Override
    public void onEnable() {
        this.wolfmain = new GameManager(this);
        this.Cvillager = new Villager(this);
        this.gameitem = new GameItem(this);
        new RoleManager();
        new Item(this);

        // Plugin startup logic

        saveDefaultConfig();


        getServer().getPluginManager().registerEvents(new Event(this, wolfmain), this);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            event.registrar().register(
                    "mwgame",
                    "コマンドの説明文です",
                    new GameCommand(this,wolfmain)
            );
        });



//        try {
//            dbManager.connect();     // 接続メソッド
//            dbManager.setupTable();  // テーブル作成メソッド
//
//            getLogger().info("MySQLのセットアップが完了しました。");
//        } catch (Exception e) {
//            getLogger().severe("MySQLの接続に失敗しました\nプラグインを無効化します。");
//            getServer().getPluginManager().disablePlugin(this);
//            getLogger().severe(e.getMessage());
//        }



        helpMessage.clear();
        helpMessage.add("§2§lゲームスタート§r§2/mwgame start");
        helpMessage.add("§2§lゲーム強制終了§r§2/mwgame stop");
        helpMessage.add("§2§lユーザーの役職強制設定§r§2/mwgame player set <roleName>");
        helpMessage.add("§2§lユーザーの役職確認§r§2/mwgame player check");
        helpMessage.add("§2§l役職人数設定§r§2/mwgame role set <roleName> <人数>");
        helpMessage.add("§2§l役職の無効化§r§2/mwgame role unset <roleName> <人数>");
        helpMessage.add("§2§l役職人数チェック§r§2/mwgame role check <roleName>");
        helpMessage.add("§2§lアイテムを付与§r§2/mwgame give <itemName>");
        helpMessage.add("§2§l取引村人を召喚§r§2/mwgame villager");
        helpMessage.add("§2§lヘルプを表示§r§2/mwgame help");


        getServer().getPluginManager().registerEvents(new Event(this,wolfmain),this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic


//        if (dbManager != null) {
//            dbManager.closeConnection();
//            getLogger().info("データベースとの接続を切断しました");
//        }

    }


    //変数
    public static boolean gamePlaying = false;
    public FileConfiguration config = getConfig();
    public String startpointworld = getConfig().getString("startpoint.world");
    public int startpointX = getConfig().getInt("startpoint.x");
    public int startpointY = getConfig().getInt("startpoint.y");
    public int startpointZ = getConfig().getInt("startpoint.z");

    public int range = getConfig().getInt("range");

    //会議
    public String meetingpointworld = getConfig().getString("meetingpoint.world");
    public int meetingpointX = getConfig().getInt("meetingpoint.x");
    public int meetingpointY = getConfig().getInt("meetingpoint.y");
    public int meetingpointZ = getConfig().getInt("meetingpoint.z");


    //Map
    public static Map<UUID, Integer> guicheck = new HashMap<>(); //GUIを開いているときのクリックイベントを戻す用


    public void help(CommandSender sender) {

        for(String message : helpMessage){

            sender.sendMessage(
                    getMessage(message)
            );

        }


    }

    public Component help(int index) {

        return getMessage(helpMessage.get(index));

    }

    // 指定した役職になっているプレイヤー一覧を表示します
//    public void list(String posiargs) {
//
//        for (UUID id : position.keySet()) {
//
//            int playercheck = position.get(id);
//
//            int goukei = 0;
//
//            //ゲーム参加者（登録受付中だったら）
//            //・希望者一覧（プレイヤー役職をコマンドできめていれば　例：MCID　：　役職名）
//            //ゲーム実行中
//            //・参加者　：　役職名
//        }
//
//    }










    //役職
//    public void player() {
//        int wolfgoukei = 0;   //人狼実際の合計人数チェック
//        int madmangoukei = 0; //狂人合計
//        int knightgoukei = 0; //騎士合計
//        int fortunegoukei = 0; //占い師合計
//        int mediumgoukei = 0; //霊媒師合計
//        int villagergoukei = 0; //村人合計
//        int spectatorgoukei = 0; //観戦者合計
//
//        //役職確認
//        for (UUID id : position.keySet()) {
//            int setpositionplayer = position.get(id);
//            Player player = Bukkit.getPlayer(id);
//            //ここからの処理の意味
//            // 例：人狼になりたい人が多すぎなかったら...
//            //人狼決定(役職Mapの値が1かつ、役職の最大人数より今まで処理した人狼の人数より少なければそのプレイヤーの役職を人狼に設定)
//            //game_dataテーブルにプレイヤーごとの役職を設定
//            if (setpositionplayer == 1 && wolfgoukei < config.getInt("wolf.count")) {
//                wolf = Bukkit.getPlayer(id);
//                dbManager.addlog(player.getName(), id.toString(), "[GAME]人狼に設定");
//                dbManager.addRoleLog(player.getName(), id.toString(), "人狼");
//                plugin.getLogger().info(player.getName() + "game_dataテーブルに設定");
//            } else if (setpositionplayer == 2 && madmangoukei < config.getInt("madman.count")) {  //狂人
//                madman = Bukkit.getPlayer(id);
//                dbManager.addlog(player.getName(), id.toString(), "[GAME]狂人に設定");
//                dbManager.addRoleLog(player.getName(), id.toString(), "狂人");
//
//                plugin.getLogger().info(player.getName() + "game_dataテーブルに設定");
//            } else if (setpositionplayer == 3 && knightgoukei < config.getInt("knight.count")) {  //騎士
//                knight = Bukkit.getPlayer(id);
//                dbManager.addlog(player.getName(), id.toString(), "[GAME]騎士に設定");
//                dbManager.addRoleLog(player.getName(), id.toString(), "騎士");
//
//                plugin.getLogger().info(player.getName() + "game_dataテーブルに設定");
//            } else if (setpositionplayer == 4 && fortunegoukei < config.getInt("fortune.count")) { //占い師
//                fortune = Bukkit.getPlayer(id);
//                dbManager.addlog(player.getName(), id.toString(), "[GAME]占い師に設定");
//                dbManager.addRoleLog(player.getName(), id.toString(), "占い師");
//
//                getLogger().info(player.getName() + "game_dataテーブルに設定");
//            } else if (setpositionplayer == 5 && mediumgoukei < config.getInt("medium.count")) {  //霊媒師
//                medium = Bukkit.getPlayer(id);
//                dbManager.addlog(player.getName(), id.toString(), "[GAME]霊媒師に設定");
//                dbManager.addRoleLog(player.getName(), id.toString(), "霊媒師");
//
//                getLogger().info(player.getName() + "game_dataテーブルに設定");
//            } else if (setpositionplayer == 6 && villagergoukei < config.getInt("villager.count")) { //村人
//                villager = Bukkit.getPlayer(id);
//                dbManager.addlog(player.getName(), id.toString(), "[GAME]村人に設定");
//                dbManager.addRoleLog(player.getName(), id.toString(), "村人");
//
//                getLogger().info(player.getName() + "game_dataテーブルに設定");
//            } else if (setpositionplayer == 100 && spectatorgoukei < getConfig().getInt("spectator.count")) {
//                spectator = Bukkit.getPlayer(id);
//                dbManager.addlog(player.getName(), id.toString(), "[GAME]観戦者に設定");
//                dbManager.addRoleLog(player.getName(), id.toString(), "観戦者");
//
//                getLogger().info(player.getName() + "game_dataテーブルに設定");
//            }
//
//        }
//
//
//    }



    public static Component getMessage(Component message){
        return Component.text("[").color(NamedTextColor.GRAY).append(Component.text("mwereWolf").color(NamedTextColor.GREEN).append(Component.text("]").color(NamedTextColor.GRAY)
                .append(message)
        ));
    }


    public static Component getMessage(String message){
        return Component.text("[").color(NamedTextColor.GRAY).append(Component.text("mwereWolf").color(NamedTextColor.GREEN).append(Component.text("]").color(NamedTextColor.GRAY)
                .append(Component.text(message))
        ));
    }

}
