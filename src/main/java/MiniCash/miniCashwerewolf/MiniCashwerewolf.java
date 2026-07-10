package MiniCash.miniCashwerewolf;


import MiniCash.miniCashwerewolf.DB.DB;
import MiniCash.miniCashwerewolf.Event.Event;
import MiniCash.miniCashwerewolf.command.Main;
import MiniCash.miniCashwerewolf.command.Tab;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


import java.util.*;
import java.util.List;

import static MiniCash.miniCashwerewolf.Event.Event.players;

public final class MiniCashwerewolf extends JavaPlugin {
   public static Plugin plugin;

    private DB dbManager;
    private WolfMain wolfmain;
    private Villager Cvillager;

    @Override
    public void onEnable() {
        this.dbManager = new DB(this);
        this.wolfmain = new WolfMain(this);
        this.Cvillager = new Villager(this);

        // Plugin startup logic
        Objects.requireNonNull(getCommand("mwgame")).setExecutor(new Main(this,this.dbManager,wolfmain,Cvillager));
        Objects.requireNonNull(getCommand("mwgame")).setTabCompleter(new Tab());

        saveDefaultConfig();


        plugin = this;
        //例： config.set("spawnpoint.x",2);


        //getConfig().set("gamePlaying",false);
        //セーブ
        //saveConfig();



        getServer().getPluginManager().registerEvents(new Event(this,wolfmain),this);


        addchecklist();


        try {
            dbManager.connect();     // 接続メソッド
            dbManager.setupTable();  // テーブル作成メソッド

            getLogger().info("MySQLのセットアップが完了しました。");
        } catch (Exception e) {
            getLogger().severe("MySQLの接続に失敗しました\nプラグインを無効化します。");
            getServer().getPluginManager().disablePlugin(this);
            getLogger().severe(e.getMessage());
        }



        //データベースのmlogテーブルにプラグイン起動を記録
        dbManager.addlog("MPLUGIN","MiniPL","プラグインが起動しました");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        if (dbManager != null) {
            //データベースのmlogテーブルにプラグイン停止を記録
            // addlog非同期なため、書き終わる前にcloseConnectionが走る可能性があるため非同期ではない別のaddlogメソッド必要かも?
            dbManager.addlog("MPLUGIN", "MiniPL", "プラグインが停止しました");
        }

        if (dbManager != null) {
            dbManager.closeConnection();
            getLogger().info("データベースとの接続を切断しました");
        }

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


    public static Plugin getPlugin(){
        return plugin;
    }




    //Map
    public static Map<UUID,Integer> guicheck = new HashMap<>(); //GUIを開いているときのクリックイベントを戻す用





    public void help(CommandSender sender){
        sender.sendMessage("§2§lhelpを表示§r§2/mwgame help");
        sender.sendMessage("§2§l役職を強制設定§r§2/mwgame playerset <役職名>");
        sender.sendMessage("§2§l役職人数設定§r§2/mwgame positionset <役職名> <人数>");
        sender.sendMessage("§2§lゲームスタート§r§2/mwgame start");

    }

    public void list(String posiargs){

        for (UUID id : position.keySet()){

            int playercheck = position.get(id);

            int goukei = 0;

            //ゲーム参加者（登録受付中だったら）
            //・希望者一覧（プレイヤー役職をコマンドできめていれば　例：MCID　：　役職名）
            //ゲーム実行中
            //・参加者　：　役職名
        }

    }




    //役職名（日本語）チェック
    public String numberposition(int pposition){
        String japosi = null;
        if (pposition == 1){
            japosi = "人狼";
        }else if (pposition == 2){
            japosi = "狂人";
        }else if (pposition == 3){
            japosi = "騎士";
        }else if (pposition == 4){
            japosi = "占い師";
        }else if (pposition == 5){
            japosi = "霊媒師";
        }else if (pposition == 6){
            japosi = "市民";
        }else if (pposition == 100){
            japosi = "観戦者";
        }
        return japosi;
    }

    //使用役職決定(役職人数設定)
    public String positionset(String positionch,int people){

        String returns = "§c§l役職人数エラー";

        //設定しようとしていたら
        if (check(positionch)){

            String checktrue = positionch + ".check";
            String pscount = positionch + ".count";

            getConfig().set(checktrue, true);

            getConfig().set(pscount,people);
            //セーブ
            saveConfig();

            returns = "§a" + positionch + "の設定人数を§l" + people + "人§r§aに設定しました！";

        }

        return returns;

    }

    //役職設定解除
    //今後再設定予定
    public String positionunset(String positionch){
            String returns = "§c役職をfalseに出来ませんでした　役職名が正しいか確認してください";

        if (check(positionch)) {
            String positioncheck = positionch + ".check";

            getConfig().set(positioncheck, false);
            //セーブ
            saveConfig();

            returns = "§a" + positionch + "の設定を§6false§r§aにしました！";


        }
        return returns;
    }










    //変数
    public static Player wolf; //人狼定義
    public static Player madman;
    public static Player knight;
    public static Player fortune;
    public static Player medium;
    public static Player villager;
    public static Player spectator;


        //役職
        public void player(){
            int wolfgoukei = 0;   //人狼実際の合計人数チェック
            int madmangoukei = 0; //狂人合計
            int knightgoukei = 0; //騎士合計
            int fortunegoukei = 0; //占い師合計
            int mediumgoukei = 0; //霊媒師合計
            int villagergoukei = 0; //村人合計
            int spectatorgoukei = 0; //観戦者合計

            //役職確認
            for (UUID id : position.keySet()){
                int setpositionplayer = position.get(id);
                Player player = Bukkit.getPlayer(id);
                //ここからの処理の意味
                // 例：人狼になりたい人が多すぎなかったら...
                //人狼決定(役職Mapの値が1かつ、役職の最大人数より今まで処理した人狼の人数より少なければそのプレイヤーの役職を人狼に設定)
                //game_dataテーブルにプレイヤーごとの役職を設定
                if (setpositionplayer == 1  && wolfgoukei < config.getInt("wolf.count")){
                    wolf = Bukkit.getPlayer(id);
                    dbManager.addlog(player.getName(),id.toString(),"[GAME]人狼に設定");
                    dbManager.addRoleLog(player.getName(),id.toString(),"人狼");
                    plugin.getLogger().info(player.getName() + "game_dataテーブルに設定");
                }else if (setpositionplayer == 2 && madmangoukei < config.getInt("madman.count")){  //狂人
                    madman = Bukkit.getPlayer(id);
                    dbManager.addlog(player.getName(),id.toString(),"[GAME]狂人に設定");
                    dbManager.addRoleLog(player.getName(),id.toString(),"狂人");

                    plugin.getLogger().info(player.getName() + "game_dataテーブルに設定");
                }else if (setpositionplayer == 3 && knightgoukei < config.getInt("knight.count")){  //騎士
                    knight = Bukkit.getPlayer(id);
                    dbManager.addlog(player.getName(),id.toString(),"[GAME]騎士に設定");
                    dbManager.addRoleLog(player.getName(),id.toString(),"騎士");

                    plugin.getLogger().info(player.getName() + "game_dataテーブルに設定");
                }else if (setpositionplayer == 4 && fortunegoukei < config.getInt("fortune.count")) { //占い師
                    fortune = Bukkit.getPlayer(id);
                    dbManager.addlog(player.getName(),id.toString(),"[GAME]占い師に設定");
                    dbManager.addRoleLog(player.getName(),id.toString(),"占い師");

                    plugin.getLogger().info(player.getName() + "game_dataテーブルに設定");
                }else if (setpositionplayer == 5 && mediumgoukei < config.getInt("medium.count")) {  //霊媒師
                    medium = Bukkit.getPlayer(id);
                    dbManager.addlog(player.getName(),id.toString(),"[GAME]霊媒師に設定");
                    dbManager.addRoleLog(player.getName(),id.toString(),"霊媒師");

                    plugin.getLogger().info(player.getName() + "game_dataテーブルに設定");
                } else if (setpositionplayer == 6 && villagergoukei < config.getInt("villager.count")) { //村人
                    villager = Bukkit.getPlayer(id);
                    dbManager.addlog(player.getName(),id.toString(),"[GAME]村人に設定");
                    dbManager.addRoleLog(player.getName(),id.toString(),"村人");

                    plugin.getLogger().info(player.getName() + "game_dataテーブルに設定");
                }else if (setpositionplayer == 100 && spectatorgoukei < getConfig().getInt("spectator.count")) {
                    spectator = Bukkit.getPlayer(id);
                    dbManager.addlog(player.getName(),id.toString(),"[GAME]観戦者に設定");
                    dbManager.addRoleLog(player.getName(),id.toString(),"観戦者");

                    plugin.getLogger().info(player.getName() + "game_dataテーブルに設定");
                }

            }


        }









}
