package MiniCash.miniCashwerewolf;


import MiniCash.miniCashwerewolf.DB.DB;
import MiniCash.miniCashwerewolf.Event.MyEvent;
import MiniCash.miniCashwerewolf.command.Main;
import MiniCash.miniCashwerewolf.command.Tab;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
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

import static MiniCash.miniCashwerewolf.Event.MyEvent.players;

public final class MiniCashwerewolf extends JavaPlugin {
   public static Plugin plugin;
    //List
    public static List<String> checklist = new ArrayList<>();     //役職が設定されているかのチェック用リスト

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



        getServer().getPluginManager().registerEvents(new MyEvent(this,wolfmain),this);


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
    public static Map<UUID,Integer> position = new HashMap<>();
    public static Map<UUID,Integer> guicheck = new HashMap<>(); //GUIを開いているときのクリックイベントを戻す用


    public void addchecklist(){
        checklist.clear();
        checklist.add("wolf"); //人狼
        checklist.add("madman"); //狂人
        checklist.add("knight"); //騎士
        checklist.add("fortune"); //占い師
        checklist.add("medium"); //霊媒師
        checklist.add("villager"); //市民+
        
        checklist.add("spectator"); //観戦者用

    }


    public void help(Player player){
        player.sendMessage("§2§lhelpを表示§r§2/mwgame help");
        player.sendMessage("§2§l役職を強制設定§r§2/mwgame playerset <役職名>");
        player.sendMessage("§2§l役職人数設定§r§2/mwgame positionset <役職名> <人数>");
        player.sendMessage("§2§lゲームスタート§r§2/mwgame start");

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


    //役職があるかどうかのチェック
    //入力された役職名を受け取ります
    public  boolean check(String ps){
        boolean check = false;


        //リストに入っている役職名だったらtrueを返す
        if (checklist.contains(ps)){

            check = true;
        }

        return check;

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


    //コマンド役職決定
    //処理内容
    //1:入力された役職名をチェック
    //2:その役職の設定人数が１人以上かをチェック
    //3:もし１人以上なら役職を設定
    //違うなら設定せずエラーメッセージを実行者に送信
    public void playerset(Player player,String positionargs){

        UUID id = player.getUniqueId();

        if (positionargs.equals("wolf")) {
            if (config.getBoolean("wolf.check") && config.getInt("wolf.count") >= 1) {
                //プレイヤー役職に人狼番号を設定
                position.put(id, 1);
                player.sendMessage("§6役職を§l人狼§r§6に設定しました");

            }else {
                player.sendMessage("§4§l役職設定人数を確認してください");
            }
        }else if (positionargs.equals("madman")){
            if (config.getBoolean("madman.check") && config.getInt("madman.count") >= 1) {
                //プレイヤー役職に狂人番号を設定
                position.put(id, 2);
                player.sendMessage("§6役職を§l狂人§r§6に設定しました");

            }else {
                player.sendMessage("§4§l役職設定人数を確認してください");
            }
        }else if (positionargs.equals("knight")){
            if (config.getBoolean("knight.check") && config.getInt("knight.count") >= 1) {
                //プレイヤー役職に騎士番号を設定
                position.put(id, 3);
                player.sendMessage("§6役職を§l騎士§r§6に設定しました");

            }else {
                player.sendMessage("§4§l役職設定人数を確認してください");
            }
        }else if (positionargs.equals("fortune")){
            if (config.getBoolean("fortune.check") && config.getInt("fortune.count") >= 1) {
                //プレイヤー役職に占い師番号を設定
                position.put(id, 4);
                player.sendMessage("§6役職を§l占い師§r§6に設定しました");

            }else {
                player.sendMessage("§4§l役職設定人数を確認してください");
            }
        }else if (positionargs.equals("medium")){
            if (config.getBoolean("medium.check") && config.getInt("medium.count") >= 1) {
                //プレイヤー役職に霊媒師番号を設定
                position.put(id, 5);
                player.sendMessage("§6役職を§l霊媒師§r§6に設定しました");

            }else {
                player.sendMessage("§4§l役職設定人数を確認してください");
            }
        }else if (positionargs.equals("villager")){
            if (config.getBoolean("villager.check") && config.getInt("villager.count") >= 1) {
                //プレイヤー役職に市民番号を設定
                position.put(id, 6);
                player.sendMessage("§6役職を§l市民§r§6に設定しました");

            }else {
                player.sendMessage("§4§l役職設定人数を確認してください");
            }
        }else if (positionargs.equals("spectator")){
            if (config.getBoolean("spectator.check") && config.getInt("spectator.count") >= 1) {
                position.put(id, 100);
                player.sendMessage("§6役職を§5§l観戦者§r§6に設定しました");
            }else {
                player.sendMessage("§4§l役職設定人数を確認してください");
            }

        }else {
            player.sendMessage("§c§lサブコマンド入力方法を確認してください！");
        }


    }


    //ランダムな役職設定
    public void roleset(){


        //役職設定最大人数を取得
        int wolfcount = getConfig().getInt("wolf.count");
        int madmancount = getConfig().getInt("madman.count");
        int knightcount = getConfig().getInt("knight.count");
        int fortunecount = getConfig().getInt("fortune.count");
        int mediumcount = getConfig().getInt("medium.count");
        int villagercount = getConfig().getInt("villager.count");


        List<String> shufflerole = new ArrayList<>(checklist);

        //その役職を使わない設定になっていたらリストから削除
        if (!config.getBoolean("wolf.check")) {
            shufflerole.remove("wolf");
        }

        if (!config.getBoolean("madman.check")) {
            shufflerole.remove("madman");
        }

        if (!config.getBoolean("knight.check")) {
            shufflerole.remove("knight");
        }

        if (!config.getBoolean("fortune.check")) {
            shufflerole.remove("fortune");
        }

        if (!config.getBoolean("medium.check")) {
            shufflerole.remove("medium");
        }

        if (!config.getBoolean("villager.check")) {
            shufflerole.remove("villager");
        }

        //観戦者役職はランダム設定役職で入らないため削除   市民は残りの人に振り分けるため削除
        shufflerole.remove("spectator");

        //役職を一旦シャッフル
        Collections.shuffle(shufflerole);



        int playerCount = players.size();


        for (int i = 0; i < playerCount; i++) {

            int rolesize = shufflerole.size();

            Random random = new Random();

            Player player = players.get(i);
            String ro = shufflerole.get(random.nextInt(rolesize));

            //役職がもうないのに参加しようとしているプレイヤーがいればスペクテイターに
            if (wolfcount == 0 && madmancount == 0 && knightcount == 0 && fortunecount == 0 && mediumcount == 0 && villagercount == 0) {
                position.put(player.getUniqueId(), 100);
            }



            int role = 0;
            if (ro.equals("wolf")) {
                role = 1;
                //役職が増えたらその役職の設定最大人数をー１（設定できないように）
                wolfcount--;

            }else if (ro.equals("madman")) {
                role = 2;
                madmancount--;
            }else if (ro.equals("knight")) {
                role = 3;
                knightcount--;
            }else if (ro.equals("fortune")) {
                role = 4;
                fortunecount--;
            }else if (ro.equals("medium")) {
                role = 5;
                mediumcount--;
            }else if (ro.equals("villager")) {
                role = 6;
                villagercount--;

            }

            position.put(player.getUniqueId(),role);


            //もし役職の設定人数がもう０になっていたらListからその役職を削除
            try {
                if (wolfcount == 0) {
                    shufflerole.remove("wolf");
                }
                if (madmancount == 0) {
                    shufflerole.remove("madman");
                }
                if (knightcount == 0) {
                    shufflerole.remove("knight");
                }
                if (fortunecount == 0) {
                    shufflerole.remove("fortune");
                }
                if (mediumcount == 0) {
                    shufflerole.remove("medium");
                }
                if (villagercount == 0) {
                    shufflerole.remove("villager");
                }

            }finally {
                getLogger().info( player.getName() + "の役職を " +  ro + " に設定しました");

            }


        }

    }











    //役職設定人数分プレイヤーがいるかをチェック
    //いなかったらtrue,いたらfalse　を返します
    public boolean playercheck(){

        //変数
        int wolfgoukei = 0;   //人狼実際の合計人数チェック
        int madmangoukei = 0; //狂人合計
        int knightgoukei = 0; //騎士合計
        int fortunegoukei = 0; //占い師合計
        int mediumgoukei = 0; //霊媒師合計
        int villagergoukei = 0; //村人合計
        int spectatorgoukei = 0; //観戦者の合計

        int wocheck = config.getInt("wolf.count");
        int mdmcheck = config.getInt("madman.count");
        int knicheck = config.getInt("knight.count");
        int ftcheck = config.getInt("fortune.count");
        int mdiumcheck = config.getInt("medium.count");
        int vlgrcheck = config.getInt("villager.count");
        int sprcheck = config.getInt("spectator.count");
        //案2
        for (UUID id : position.keySet()){
            int playercheck = position.get(id);

            //人数確認（役職Mapの値が1だったら人狼合計確認変数の値を+1）
            if (playercheck == 1) {
                wolfgoukei++;

            }else if (playercheck == 2){
                madmangoukei++;
            }else if (playercheck == 3){
                knightgoukei++;
            } else if (playercheck == 4) {
                fortunegoukei++;
            }else if (playercheck == 5){
                mediumgoukei++;
            }else if (playercheck == 6){
                villagergoukei++;
            }else if (playercheck == 100){
                spectatorgoukei++;
            }

        }


        //最終人数確認
        //人狼(先ほど処理したものを使用)
        if (config.getBoolean("wolf.check")) {
            if (wolfgoukei != wocheck) {        //設定された人狼人数と等しくなかったらリターンtrue
                return true;
            }
        }

        if (config.getBoolean("madman.check")) {
            if (madmangoukei != mdmcheck) {
                return true;
            }
        }   //設定された狂人人数と等しくなかったらリターンtrue

        if (config.getBoolean("knight.check")) {
        if (knightgoukei != knicheck){
            return true;
            }
        }   //設定された騎士人数と等しくなかったらリターンtrue
        if (config.getBoolean("fortune.check")) {
        if (fortunegoukei != ftcheck){
            return true;
            }
        }   //設定された占い師人数と等しくなかったらリターンtrue

        if (config.getBoolean("medium.check")) {
            if (mediumgoukei != mdiumcheck) {
                return true;
            }
        }   //設定された霊媒師人数と等しくなかったらリターンtrue

        if (config.getBoolean("villager.check")) {
            if (villagergoukei != vlgrcheck) {
                return true;
            }
        }   //設定された市民人数と等しくなかったらリターンtrue

        //観戦者はいらないかも？
//        if (config.getBoolean("spectator.check")){
//            if (sprcheck >= 1 && spectatorgoukei == sprcheck) {
//                checkresult = false;
//            }
//
//        }


        return false;
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






    //アイテム(コマンドでの入手用)
        public void givecommanditem(Player player,String itemn){


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
                player.getInventory().addItem(wolfitem); //アイテム付与

                player.sendMessage("§4" + player.getName() + "に「人狼の斧」を付与しました");

            }

            //狂人
            if (itemn.equals("madman")) {
                ItemStack madmanitem = new ItemStack(Material.ECHO_SHARD, 1);

                ItemMeta madmanitemeta = madmanitem.getItemMeta();
                madmanitemeta.setDisplayName("§c§l味方を探せ！");
                madmanitemeta.setLore(List.of("§6右クリックで使用可能"));
                madmanitemeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING, "madman_item");


                madmanitem.setItemMeta(madmanitemeta); //アイテムメタを設定
                player.getInventory().addItem(madmanitem); //アイテム人狼に付与

                player.sendMessage("§4" + player.getName() + "に「味方を探せ！」を付与しました");

            }

            //騎士
            if (itemn.equals("knight")){
                ItemStack knightitem = new ItemStack(Material.SHIELD, 1);

                ItemMeta knightitemmeta = knightitem.getItemMeta();
                knightitemmeta.setDisplayName("§c守りの盾");
                knightitemmeta.setLore(List.of("§6右クリックで使用可能"));
                knightitemmeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING, "knight_item");


                knightitem.setItemMeta(knightitemmeta); //アイテムメタを設定
                player.getInventory().addItem(knightitem); //アイテム付与

                player.sendMessage("§4" + player.getName() + "に「守りの盾」を付与しました");
            }

            //占い師
            if (itemn.equals("fortunecheck")){

                ItemStack fortuneitem = new ItemStack(Material.AMETHYST_SHARD, 1);

                ItemMeta fortuneitemmeta = fortuneitem.getItemMeta();
                fortuneitemmeta.setDisplayName("§5§l占い");
                fortuneitemmeta.setLore(List.of("§6右クリックで使用可能"));
                fortuneitemmeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING, "fortune_item");


                fortuneitem.setItemMeta(fortuneitemmeta); //アイテムメタを設定
                player.getInventory().addItem(fortuneitem); //アイテム付与

                player.sendMessage("§4" + player.getName() + "に「占い」を付与しました");
            }

            //霊媒師
            if (itemn.equals("mediumcheck")){

                ItemStack mediumitem = new ItemStack(Material.NETHER_STAR, 1);

                ItemMeta mediumitemmeta = mediumitem.getItemMeta();
                mediumitemmeta.setDisplayName("§5§l霊媒師用のアイテム");
                mediumitemmeta.setLore(List.of("§6右クリックで使用可能"));
                mediumitemmeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING, "medium_item");


                mediumitem.setItemMeta(mediumitemmeta); //アイテムメタを設定
                player.getInventory().addItem(mediumitem); //アイテム付与

                player.sendMessage("§4" + player.getName() + "に「霊媒師のアイテム」を付与しました");
            }


            if (itemn.equals("pcheck")){

                player.getInventory().addItem(createItem("pcheck",1));

                player.sendMessage("§4" + player.getName() + "に「残り人数確認の書」を付与しました");



            }




            if (itemn.equals("coin")) {
                //コイン
                ItemStack coin = new ItemStack(Material.GOLD_INGOT);
                ItemMeta spawngolditemmeta = coin.getItemMeta();
                spawngolditemmeta.setDisplayName("§6コイン");
                spawngolditemmeta.setLore(List.of("§a人狼ゲーム専用コイン"));
                spawngolditemmeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING, "spawn_gold_ingot");
                coin.setItemMeta(spawngolditemmeta); //アイテムメタを設定


                player.getInventory().addItem(coin);

                player.sendMessage("§4" + player.getName() + "に「コイン」を付与しました");
            }else if (itemn.equals("glow")){
                ItemStack glowitem = createItem("glowing",1);


                player.getInventory().addItem(glowitem);

                player.sendMessage("§4" + player.getName() + "に「全員発光」を付与しました");

            }else if (itemn.equals("speed")){

                ItemStack speedpotion = createItem("speed",1);

                player.getInventory().addItem(speedpotion);

                player.sendMessage("§4" + player.getName() + "に「俊敏のスプラッシュポーション」を付与しました");

            }else if (itemn.equals("smoke")){
                player.getInventory().addItem(createItem("smoke",1));

                player.sendMessage("§4" + player.getName() + "に「煙幕」を付与しました");
            }

        }




        public ItemStack createItem(String item,int amount) {
            NamespacedKey namekey = new NamespacedKey(plugin,"wolfitem");
            ItemStack ritem = null;


            if (item.equals("coin")) {
                ItemStack coin = new ItemStack(Material.GOLD_INGOT, amount);
                ItemMeta coinmeta = coin.getItemMeta();
                coinmeta.setDisplayName("§6コイン");
                coinmeta.setLore(List.of("§a人狼ゲーム専用コイン"));
                coinmeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING, "spawn_gold_ingot");
                coin.setItemMeta(coinmeta);

                ritem = coin;
            }else if (item.equals("pcheck")){
                //残り人数確認の書
                ItemStack pcheckitem = new ItemStack(Material.LEATHER_HORSE_ARMOR);
                ItemMeta pcheckitemmeta = pcheckitem.getItemMeta();
                pcheckitemmeta.setDisplayName("§6残り人数確認の書");
                pcheckitemmeta.setLore(List.of("§a右クリックで使用可能"));
                pcheckitemmeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING, "people_check");
                pcheckitem.setItemMeta(pcheckitemmeta); //アイテムメタを設定

                ritem = pcheckitem;
            } else if (item.equals("glowing")) {
                ItemStack glowingitem = new ItemStack(Material.GLOW_INK_SAC, amount);
                ItemMeta glowingitemmeta = glowingitem.getItemMeta();
                glowingitemmeta.setDisplayName("§e全員発光");
                glowingitemmeta.setLore(List.of("§a右クリックで使用可能"));
                glowingitemmeta.getPersistentDataContainer().set(namekey,PersistentDataType.STRING,"glowin_item");
                glowingitem.setItemMeta(glowingitemmeta);

                ritem = glowingitem;

            }else if (item.equals("speed")) {


                ItemStack potion = new ItemStack(Material.SPLASH_POTION);
                PotionMeta potionmeta = (PotionMeta) potion.getItemMeta();
                potionmeta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 2, 10),true);

               
                potionmeta.setDisplayName("§b俊敏のポーション");

               
                potion.setItemMeta(potionmeta);
                
                ritem = potion;

            } else if (item.equals("invisibility")) {

                ItemStack invisibilitypotion = new ItemStack(Material.SPLASH_POTION);
                PotionMeta potionmeta = (PotionMeta) invisibilitypotion.getItemMeta();
                potionmeta.addCustomEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 15, 1),true);


                potionmeta.setDisplayName("§l透明化のポーション");


                invisibilitypotion.setItemMeta(potionmeta);

                ritem = invisibilitypotion;

                
            } else if (item.equals("smoke")) {
                ItemStack smoke = new ItemStack(Material.COAL);
                ItemMeta smokeItemMeta = smoke.getItemMeta();
                smokeItemMeta.setDisplayName("§6§l煙幕");
                smokeItemMeta.setLore(List.of("§a右クリックで使用可能"));
                smokeItemMeta.getPersistentDataContainer().set(namekey,PersistentDataType.STRING,"smoke_item");

                smoke.setItemMeta(smokeItemMeta);

                ritem = smoke;
                
            }
            return ritem;
        }



}
