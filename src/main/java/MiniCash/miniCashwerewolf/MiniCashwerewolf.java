package MiniCash.miniCashwerewolf;


import MiniCash.miniCashwerewolf.Event.ItemTimer;
import MiniCash.miniCashwerewolf.Event.MyEvent;
import MiniCash.miniCashwerewolf.command.Main;
import MiniCash.miniCashwerewolf.command.Tab;
import MiniCash.miniCashwerewolf.gui.VoteGuiHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;


import java.util.*;
import java.util.List;

import static MiniCash.miniCashwerewolf.Event.MyEvent.players;
import static MiniCash.miniCashwerewolf.MeetingTimer.mstop;
import static MiniCash.miniCashwerewolf.Timer.*;

public final class MiniCashwerewolf extends JavaPlugin {
   public static Plugin plugin;
    //List
    public static List<String> checklist = new ArrayList<>();     //役職が設定されているかのチェック用リスト
    @Override
    public void onEnable() {
        // Plugin startup logic
        getCommand("mwgame").setExecutor(new Main(this));
        getCommand("mwgame").setTabCompleter(new Tab());

        saveDefaultConfig();
        FileConfiguration config = getConfig();

        plugin = this;
        //例： config.set("spawnpoint.x",2);


        //getConfig().set("gamePlaying",false);
        //セーブ
        //saveConfig();



        getServer().getPluginManager().registerEvents(new MyEvent(this),this);


        checklist.add("wolf"); //人狼
        checklist.add("madman"); //狂人
        checklist.add("knight"); //騎士
        checklist.add("fortune"); //占い師
        checklist.add("medium"); //霊媒師
        checklist.add("villager"); //市民
        checklist.add("spectator"); //観戦者用




    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        //ゲーム実行状態をfalseに
        getConfig().set("gamePlaying",false);
        //セーブ
        saveConfig();
    }


    //変数
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



    public void help(Player player){
        player.sendMessage("§2§lhelpを表示§r§2/mwgame help");
        player.sendMessage("§2§l役職を強制設定§r§2/mwgame playerset <役職名>");
        player.sendMessage("§2§l役職人数設定§r§2/mwgame positionset <役職名> <人数>");
        player.sendMessage("§2§lゲームスタート§r§2/mwgame start");

    }

    public void mreload(Player player) {
        saveConfig();
        player.sendMessage("§2config.yml再読み込みが完了しました");
    }


    public void list(String posiargs){

        for (UUID id : position.keySet()){

            int playercheck = position.get(id);

            int goukei = 0;


        }

    }


    //役職があるかどうかのチェック
    //入力された役職名を受け取ります
    public  boolean check(String ps){
        boolean check = false;

//        checklist.clear();
//        checklist.add("wolf"); //人狼
//        checklist.add("madman"); //狂人
//        checklist.add("knight"); //騎士
//        checklist.add("fortune"); //占い師
//        checklist.add("medium"); //霊媒師
//        checklist.add("villager"); //市民
//        checklist.add("spectator"); //観戦者用


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
            } else if (ro.equals("villager")) {
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
        boolean checkresult = true;
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
            if (wocheck >= 1 && wolfgoukei == wocheck) {
                checkresult = false;
            }
        }    //設定された人狼人数と等しくなかったらリターンtrue

        if (config.getBoolean("madman.check")) {
            if (mdmcheck >= 1 && madmangoukei == mdmcheck) {
                checkresult = false;
            }
        }   //設定された狂人人数と等しくなかったらリターンtrue

        if (config.getBoolean("knight.check")) {
        if (knicheck >= 1 && knightgoukei == knicheck){
            checkresult = false;
            }
        }   //設定された騎士人数と等しくなかったらリターンtrue
        if (config.getBoolean("fortune.check")) {
        if (ftcheck >= 1 && fortunegoukei == ftcheck){
            checkresult = false;
            }
        }   //設定された占い師人数と等しくなかったらリターンtrue

        if (config.getBoolean("medium.check")) {
            if (mdiumcheck >= 1 && mediumgoukei == mdiumcheck) {
                checkresult = false;
            }
        }   //設定された霊媒師人数と等しくなかったらリターンtrue

        if (config.getBoolean("villager.check")) {
            if (vlgrcheck >= 1 && villagergoukei == vlgrcheck) {
                checkresult = false;
            }
        }   //設定された市民人数と等しくなかったらリターンtrue

        //観戦者はいらないかも？
//        if (config.getBoolean("spectator.check")){
//            if (sprcheck >= 1 && spectatorgoukei == sprcheck) {
//                checkresult = false;
//            }
//
//        }


        return checkresult;
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

                //ここからの処理の意味
                // 例：人狼になりたい人が多すぎなかったら...
                //人狼決定(役職Mapの値が1かつ、役職の最大人数より今まで処理した人狼の人数より少なければそのプレイヤーの役職を人狼に設定)
                if (setpositionplayer == 1  && wolfgoukei < config.getInt("wolf.count")){
                    wolf = Bukkit.getPlayer(id);
                }else if (setpositionplayer == 2 && madmangoukei < config.getInt("madman.count")){  //狂人
                    madman = Bukkit.getPlayer(id);
                }else if (setpositionplayer == 3 && knightgoukei < config.getInt("knight.count")){  //騎士
                    knight = Bukkit.getPlayer(id);
                }else if (setpositionplayer == 4 && fortunegoukei < config.getInt("fortune.count")) { //占い師
                    fortune = Bukkit.getPlayer(id);
                }else if (setpositionplayer == 5 && mediumgoukei < config.getInt("medium.count")) {  //霊媒師
                    medium = Bukkit.getPlayer(id);
                } else if (setpositionplayer == 6 && villagergoukei < config.getInt("villager.count")) { //村人
                    villager = Bukkit.getPlayer(id);
                }else if (setpositionplayer == 100 && spectatorgoukei < getConfig().getInt("spectator.count")) {
                    spectator = Bukkit.getPlayer(id);
                }

            }


        }


        //アイテム付与
        public void giveitem(){
            NamespacedKey namekey = new NamespacedKey(plugin,"wolfitem");


            //人狼
            if (config.getBoolean("wolf.check")) {

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
                wolfitemmeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, modifier);
                wolfitemmeta.setLore(List.of("§6右クリックで使用可能"));
                wolfitemmeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING, "wolf_item");


                wolfitem.setItemMeta(wolfitemmeta); //アイテムメタを設定
                wolf.getInventory().addItem(wolfitem); //アイテム人狼に付与
            }

            //狂人
            if (config.getBoolean("madman.check")) {

                ItemStack madmanitem = new ItemStack(Material.ECHO_SHARD, 1);

                ItemMeta madmanitemeta = madmanitem.getItemMeta();
                madmanitemeta.setDisplayName("§c§l味方を探せ！");
                madmanitemeta.setLore(List.of("§6右クリックで使用可能"));
                madmanitemeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING, "madman_item");


                madmanitem.setItemMeta(madmanitemeta); //アイテムメタを設定
                madman.getInventory().addItem(madmanitem); //アイテム付与

            }

            //騎士
            if (config.getBoolean("knight.check")) {

                ItemStack knightitem = new ItemStack(Material.SHIELD, 1);

                ItemMeta knightitemmeta = knightitem.getItemMeta();
                knightitemmeta.setDisplayName("§5守りの盾");
                knightitemmeta.setLore(List.of("§6右クリックで使用可能"));
                knightitemmeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING, "knight_item");


                knightitem.setItemMeta(knightitemmeta); //アイテムメタを設定
                knight.getInventory().addItem(knightitem); //アイテム付与
            }

            //占い師
            if (config.getBoolean("fortune.check")){

                ItemStack fortuneitem = new ItemStack(Material.AMETHYST_SHARD, 1);

                ItemMeta fortuneitemmeta = fortuneitem.getItemMeta();
                fortuneitemmeta.setDisplayName("§5§l占い");
                fortuneitemmeta.setLore(List.of("§6右クリックで使用可能"));
                fortuneitemmeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING, "fortune_item");


                fortuneitem.setItemMeta(fortuneitemmeta); //アイテムメタを設定
                fortune.getInventory().addItem(fortuneitem); //アイテム付与
            }

            //霊媒師
            if (config.getBoolean("medium.check")){

                ItemStack mediumitem = new ItemStack(Material.NETHER_STAR, 1);

                ItemMeta mediumitemmeta = mediumitem.getItemMeta();
                mediumitemmeta.setDisplayName("§5§l霊媒師用のアイテム");
                mediumitemmeta.setLore(List.of("§6右クリックで使用可能"));
                mediumitemmeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING, "medium_item");


                mediumitem.setItemMeta(mediumitemmeta); //アイテムメタを設定
                medium.getInventory().addItem(mediumitem); //アイテム付与
            }





        }

        Player gameplayer;

        //ホワイトリスト関連
        public void whitelistp(){

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
            int stpX = startpointX;
            int stpY = startpointY;
            int stpZ = startpointZ;

            World world = Bukkit.getWorld(startpointworld);
            Location location = new Location(world,stpX,stpY,stpZ);

            //テレポート
            for (Player onlineplayer : Bukkit.getOnlinePlayers()) {
                onlineplayer.teleport(location);
                onlineplayer.setGameMode(GameMode.ADVENTURE);
            }


            //観戦者のみスペクテイターモードに変更(観戦者がいたら...)
            if (getConfig().getBoolean("spectator.check")) {
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
            FileConfiguration config = getConfig();
            getConfig().set("gamePlaying",true);
            //セーブ
            saveConfig();




            //役職ごとのメッセージ
            //人狼
            if (config.getBoolean("wolf.check")) {
                wolf.sendMessage("§c§lあなたは人狼になりました");
                wolf.sendMessage("        §7[§a§l役職説明§r§7]         ");
                wolf.sendMessage("他陣営に気づかれないよう倒しましょう!");
                wolf.sendMessage("§lアイテムが配られました");
            }

            //狂人
            if (config.getBoolean("madman.check")) {
                madman.sendMessage("§4あなたは狂人になりました");
                madman.sendMessage("        §7[§a§l役職説明§r§7]         ");
                madman.sendMessage("他陣営に気づかれないよう味方の人狼を見つけ出し協力して他陣営を倒そう！");
                madman.sendMessage("§lアイテムが配られました");

            }

            //騎士
            if (config.getBoolean("knight.check")) {
                knight.sendMessage("§bあなたは騎士になりました");
                knight.sendMessage("        §7[§a§l役職説明§r§7]         ");
                knight.sendMessage("味方を守ろう！");
                knight.sendMessage("§lアイテムが配られました");

            }

            //占い師
            if (config.getBoolean("fortune.check")){
                fortune.sendMessage("§bあなたは占い師になりました");
                fortune.sendMessage("        §7[§a§l役職説明§r§7]         ");
                fortune.sendMessage("怪しいプレイヤーを見つけろ");
                fortune.sendMessage("§lアイテムが配られました");

            }

            //霊媒師
            if (config.getBoolean("medium.check")){
                medium.sendMessage("§bあなたは霊媒師になりました");
                medium.sendMessage("        §7[§a§l役職説明§r§7]         ");
                medium.sendMessage("怪しいプレイヤー....§kaaaaaa");
                medium.sendMessage("§lアイテムが配られました");

            }

            //市民
            if (config.getBoolean("villager.check")){
                villager.sendMessage("§bあなたは市民になりました");
                villager.sendMessage("        §7[§a§l役職説明§r§7]         ");
                villager.sendMessage("              逃げろ");

            }


            //観戦者
            if (config.getBoolean("spectator.check")){

                spectator.sendMessage("§aあなたは観戦者になりました");
                spectator.sendMessage("        §7[§a§l役職説明§r§7]         ");
                spectator.sendMessage("              §kaaaaaa");
            }







            //タイマースタート
            new Timer(this).runTaskTimer(MiniCashwerewolf.getPlugin(),0L,20L);
                nowtime = true;

        }

        //stopコマンド実装
        public void gstop(Player player){

            if (config.getBoolean("gamePlaying")) {


                tstop = true;
                mstop = true;


                //オンラインプレイヤー全員にタイトルを表示
                for (Player onlinep : Bukkit.getOnlinePlayers()) {
                    onlinep.sendTitle("§kaaa§r§e引き分け！！§kaaa§r", "", 10, 70, 20);
                    onlinep.setGameMode(GameMode.SPECTATOR);
                    onlinep.setWhitelisted(false);
                }


                player.sendMessage("§6§lゲームを停止させました！");

                //ホワイトリスト解除
                Bukkit.setWhitelist(false);

                //ゲーム実行中をfalseに変更
                getConfig().set("gamePlaying", false);
                //セーブ
                saveConfig();
            }else {
                player.sendMessage("§c§l現在ゲームが進行中ではありません!\nゲームが進行中のみこのコマンドを実行できます");
            }
        }


        //昼と夜の移り変わり
        public void day(){

            Bukkit.broadcastMessage("§6昼になりました");
            Bukkit.broadcastMessage("§lマイクをONにして話し合いましょう");

            //タイマースタート
            new Timer(this).runTaskTimer(MiniCashwerewolf.getPlugin(),0L,20L);

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
            World world = Bukkit.getWorld(startpointworld);
            world.setTime(1000);






        }

        public void noon(){

            Bukkit.broadcastMessage("§5夜になりました");
            Bukkit.broadcastMessage("§lマイクをOFFにしてください");

            //タイマースタート
            new Timer(this).runTaskTimer(MiniCashwerewolf.getPlugin(),0L,20L);

            //オンラインプレイヤー全員にタイトルを表示
            for (Player titleonlinep : Bukkit.getOnlinePlayers()){
                titleonlinep.sendTitle("§8マイクをOFFにしましょう","",10,70,20);
            }


            //時間を夜に変更

            World world = Bukkit.getWorld(startpointworld);
            world.setTime(18000);


            //人狼に対して


        }

        //会議
        public void meeting(){

            //会議地点へテレポート
            int mtgX = meetingpointX;
            int mtgY = meetingpointY;
            int mtgZ = meetingpointZ;

            World world = Bukkit.getWorld(meetingpointworld);
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
            new MeetingTimer(this).runTaskTimer(MiniCashwerewolf.getPlugin(),0L,20L);



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


        //ホワイトリスト解除
        Bukkit.setWhitelist(false);

        Bukkit.getLogger().info("[§aMiniCashwerewolf§r] §lホワイトリストをoffにしました");


        //ゲーム実行中をfalseに変更
        getConfig().set("gamePlaying",false);
        //セーブ
        saveConfig();

        Bukkit.getLogger().info("[§aMiniCashwerewolf§r] §lゲーム終了処理がすべて完了しました");

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

        //ホワイトリスト解除
        Bukkit.setWhitelist(false);

        //ゲーム実行中をfalseに変更
        getConfig().set("gamePlaying",false);
        //セーブ
        saveConfig();
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
            }

        }


        //村人スポーン
        public void villagerspawn(Player player,World world,Location location){
            NamespacedKey namekey = new NamespacedKey(plugin,"villagergui");
            String vid = "villager";
            Villager villager = (Villager) world.spawn(location, Villager.class);


            villager.getPersistentDataContainer().set(
                    namekey,
                    PersistentDataType.STRING,
                    vid
            );

            villager.setAI(false);        // 動かない
            villager.setInvulnerable(true); // 無敵
            villager.setCollidable(false);  // 押されない
            villager.setSilent(true);       // 音を出さない
            villager.setProfession(Villager.Profession.NONE);


            String id = villager.getPersistentDataContainer().get(
                    namekey,
                    PersistentDataType.STRING
            );

            if ("villager".equals(id)){

                List<MerchantRecipe> recipes = new ArrayList<>();

                //販売アイテム
                ItemStack nitem1 = new ItemStack(Material.COOKED_BEEF, 2);
                ItemStack diamondsword = new ItemStack(Material.DIAMOND_SWORD, 1);

                MerchantRecipe recipe = new MerchantRecipe(
                        new ItemStack(nitem1), //品物
                        9999 // 使用回数（実質無限）
                );
                MerchantRecipe recipe2 = new MerchantRecipe(
                        new ItemStack(createItem("pcheck",1)), //品物
                        9999 // 使用回数（実質無限）
                );
                MerchantRecipe recipe3 = new MerchantRecipe(
                        new ItemStack(diamondsword), //品物
                        9999 // 使用回数（実質無限）
                );

                ItemStack cost4 = createItem("coin",4);
                ItemStack cost6 = createItem("coin",6);
                ItemStack cost10 = createItem("coin",10);



//                //取引必要アイテムコピー
//                ItemStack cost1 = spawngolditem.clone();
//
//                cost1.setAmount(4);   //コイン必要数4枚の場合

                recipe.addIngredient(new ItemStack(cost4)); // 必要アイテム
                recipe2.addIngredient(new ItemStack(cost10));
                recipe3.addIngredient(new ItemStack(cost6));

                recipes.add(recipe);
                recipes.add(recipe2);
                recipes.add(recipe3);
                villager.setRecipes(recipes);



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
            }
            return ritem;
        }



}
