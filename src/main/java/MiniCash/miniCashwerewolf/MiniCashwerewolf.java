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
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;


import java.util.*;
import java.util.List;

import static MiniCash.miniCashwerewolf.Event.MyEvent.players;
import static MiniCash.miniCashwerewolf.MeetingTimer.mstop;
import static MiniCash.miniCashwerewolf.Timer.*;

public final class MiniCashwerewolf extends JavaPlugin {
   public static Plugin plugin;
    public static List<String> checklist = new ArrayList<>();    
    @Override
    public void onEnable() {
        // Plugin startup logic
        getCommand("mwgame").setExecutor(new Main(this));
        getCommand("mwgame").setTabCompleter(new Tab());

        saveDefaultConfig();
        FileConfiguration config = getConfig();
        plugin = this;
        getServer().getPluginManager().registerEvents(new MyEvent(this),this);
        checklist.add("wolf"); 
        checklist.add("madman"); 
        checklist.add("knight"); 
        checklist.add("fortune"); 
        checklist.add("medium"); 
        checklist.add("villager"); 
        checklist.add("spectator"); 
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getConfig().set("gamePlaying",false);
        saveConfig();
    }
    public FileConfiguration config = getConfig();
    public String startpointworld = getConfig().getString("startpoint.world");
    public int startpointX = getConfig().getInt("startpoint.x");
    public int startpointY = getConfig().getInt("startpoint.y");
    public int startpointZ = getConfig().getInt("startpoint.z");
    public int range = getConfig().getInt("range");
    public String meetingpointworld = getConfig().getString("meetingpoint.world");
    public int meetingpointX = getConfig().getInt("meetingpoint.x");
    public int meetingpointY = getConfig().getInt("meetingpoint.y");
    public int meetingpointZ = getConfig().getInt("meetingpoint.z");


    public static Plugin getPlugin(){
        return plugin;
    }

    public static Map<UUID,Integer> position = new HashMap<>();
    public static Map<UUID,Integer> guicheck = new HashMap<>(); 



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
    public  boolean check(String ps){
        boolean check = false;
        if (checklist.contains(ps)){

            check = true;
        }

        return check;

    }

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
    public String positionset(String positionch,int people){

        String returns = "§c§l役職人数エラー";

        
        if (check(positionch)){

            String checktrue = positionch + ".check";
            String pscount = positionch + ".count";

            getConfig().set(checktrue, true);

            getConfig().set(pscount,people);
            
            saveConfig();

            returns = "§a" + positionch + "の設定人数を§l" + people + "人§r§aに設定しました！";

        }

        return returns;

    }
    public String positionunset(String positionch){
            String returns = "§c役職をfalseに出来ませんでした　役職名が正しいか確認してください";

        if (check(positionch)) {
            String positioncheck = positionch + ".check";

            getConfig().set(positioncheck, false);
            
            saveConfig();

            returns = "§a" + positionch + "の設定を§6false§r§aにしました！";


        }
        return returns;
    }

    public void playerset(Player player,String positionargs){

        UUID id = player.getUniqueId();

        if (positionargs.equals("wolf")) {
            if (config.getBoolean("wolf.check") && config.getInt("wolf.count") >= 1) {
                
                position.put(id, 1);
                player.sendMessage("§6役職を§l人狼§r§6に設定しました");

            }else {
                player.sendMessage("§4§l役職設定人数を確認してください");
            }
        }else if (positionargs.equals("madman")){
            if (config.getBoolean("madman.check") && config.getInt("madman.count") >= 1) {
                
                position.put(id, 2);
                player.sendMessage("§6役職を§l狂人§r§6に設定しました");

            }else {
                player.sendMessage("§4§l役職設定人数を確認してください");
            }
        }else if (positionargs.equals("knight")){
            if (config.getBoolean("knight.check") && config.getInt("knight.count") >= 1) {
                
                position.put(id, 3);
                player.sendMessage("§6役職を§l騎士§r§6に設定しました");

            }else {
                player.sendMessage("§4§l役職設定人数を確認してください");
            }
        }else if (positionargs.equals("fortune")){
            if (config.getBoolean("fortune.check") && config.getInt("fortune.count") >= 1) {
                
                position.put(id, 4);
                player.sendMessage("§6役職を§l占い師§r§6に設定しました");

            }else {
                player.sendMessage("§4§l役職設定人数を確認してください");
            }
        }else if (positionargs.equals("medium")){
            if (config.getBoolean("medium.check") && config.getInt("medium.count") >= 1) {
                
                position.put(id, 5);
                player.sendMessage("§6役職を§l霊媒師§r§6に設定しました");

            }else {
                player.sendMessage("§4§l役職設定人数を確認してください");
            }
        }else if (positionargs.equals("villager")){
            if (config.getBoolean("villager.check") && config.getInt("villager.count") >= 1) {
                
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

    public void roleset(){
        int wolfcount = getConfig().getInt("wolf.count");
        int madmancount = getConfig().getInt("madman.count");
        int knightcount = getConfig().getInt("knight.count");
        int fortunecount = getConfig().getInt("fortune.count");
        int mediumcount = getConfig().getInt("medium.count");
        int villagercount = getConfig().getInt("villager.count");


        List<String> shufflerole = new ArrayList<>(checklist);
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
        shufflerole.remove("spectator");

  
        Collections.shuffle(shufflerole);



        int playerCount = players.size();


        for (int i = 0; i < playerCount; i++) {

            int rolesize = shufflerole.size();

            Random random = new Random();

            Player player = players.get(i);
            String ro = shufflerole.get(random.nextInt(rolesize));
            if (wolfcount == 0 && madmancount == 0 && knightcount == 0 && fortunecount == 0 && mediumcount == 0 && villagercount == 0) {
                position.put(player.getUniqueId(), 100);
            }



            int role = 0;
            if (ro.equals("wolf")) {
                role = 1;
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

    public boolean playercheck(){

        
        boolean checkresult = true;
        int wolfgoukei = 0;   
        int madmangoukei = 0; 
        int knightgoukei = 0; 
        int fortunegoukei = 0; 
        int mediumgoukei = 0; 
        int villagergoukei = 0; 
        int spectatorgoukei = 0; 

        int wocheck = config.getInt("wolf.count");
        int mdmcheck = config.getInt("madman.count");
        int knicheck = config.getInt("knight.count");
        int ftcheck = config.getInt("fortune.count");
        int mdiumcheck = config.getInt("medium.count");
        int vlgrcheck = config.getInt("villager.count");
        int sprcheck = config.getInt("spectator.count");
        
        for (UUID id : position.keySet()){
            int playercheck = position.get(id);
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

        if (config.getBoolean("wolf.check")) {
            if (wocheck >= 1 && wolfgoukei == wocheck) {
                checkresult = false;
            }
        }    

        if (config.getBoolean("madman.check")) {
            if (mdmcheck >= 1 && madmangoukei == mdmcheck) {
                checkresult = false;
            }
        }   

        if (config.getBoolean("knight.check")) {
        if (knicheck >= 1 && knightgoukei == knicheck){
            checkresult = false;
            }
        }   
        if (config.getBoolean("fortune.check")) {
        if (ftcheck >= 1 && fortunegoukei == ftcheck){
            checkresult = false;
            }
        }   

        if (config.getBoolean("medium.check")) {
            if (mdiumcheck >= 1 && mediumgoukei == mdiumcheck) {
                checkresult = false;
            }
        } 

        if (config.getBoolean("villager.check")) {
            if (vlgrcheck >= 1 && villagergoukei == vlgrcheck) {
                checkresult = false;
            }
        }   


        return checkresult;
    }

    public static Player wolf; 
    public static Player madman;
    public static Player knight;
    public static Player fortune;
    public static Player medium;
    public static Player villager;
    public static Player spectator;

        public void player(){
            int wolfgoukei = 0;   
            int madmangoukei = 0; 
            int knightgoukei = 0; 
            int fortunegoukei = 0; 
            int mediumgoukei = 0; 
            int villagergoukei = 0; 
            int spectatorgoukei = 0; 

            for (UUID id : position.keySet()){
                int setpositionplayer = position.get(id);

                if (setpositionplayer == 1  && wolfgoukei < config.getInt("wolf.count")){
                    wolf = Bukkit.getPlayer(id);
                }else if (setpositionplayer == 2 && madmangoukei < config.getInt("madman.count")){ 
                    madman = Bukkit.getPlayer(id);
                }else if (setpositionplayer == 3 && knightgoukei < config.getInt("knight.count")){  
                    knight = Bukkit.getPlayer(id);
                }else if (setpositionplayer == 4 && fortunegoukei < config.getInt("fortune.count")) {
                    fortune = Bukkit.getPlayer(id);
                }else if (setpositionplayer == 5 && mediumgoukei < config.getInt("medium.count")) {  
                    medium = Bukkit.getPlayer(id);
                } else if (setpositionplayer == 6 && villagergoukei < config.getInt("villager.count")) { 
                    villager = Bukkit.getPlayer(id);
                }else if (setpositionplayer == 100 && spectatorgoukei < getConfig().getInt("spectator.count")) {
                    spectator = Bukkit.getPlayer(id);
                }

            }


        }

        public void giveitem(){
            NamespacedKey namekey = new NamespacedKey(plugin,"wolfitem");
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


                wolfitem.setItemMeta(wolfitemmeta);
                wolf.getInventory().addItem(wolfitem); 
            }

            if (config.getBoolean("madman.check")) {

                ItemStack madmanitem = new ItemStack(Material.ECHO_SHARD, 1);

                ItemMeta madmanitemeta = madmanitem.getItemMeta();
                madmanitemeta.setDisplayName("§c§l味方を探せ！");
                madmanitemeta.setLore(List.of("§6右クリックで使用可能"));
                madmanitemeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING, "madman_item");


                madmanitem.setItemMeta(madmanitemeta);
                madman.getInventory().addItem(madmanitem); 

            }
            if (config.getBoolean("knight.check")) {

                ItemStack knightitem = new ItemStack(Material.SHIELD, 1);

                ItemMeta knightitemmeta = knightitem.getItemMeta();
                knightitemmeta.setDisplayName("§5守りの盾");
                knightitemmeta.setLore(List.of("§6右クリックで使用可能"));
                knightitemmeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING, "knight_item");


                knightitem.setItemMeta(knightitemmeta); 
                knight.getInventory().addItem(knightitem);
            }
            if (config.getBoolean("fortune.check")){

                ItemStack fortuneitem = new ItemStack(Material.AMETHYST_SHARD, 1);

                ItemMeta fortuneitemmeta = fortuneitem.getItemMeta();
                fortuneitemmeta.setDisplayName("§5§l占い");
                fortuneitemmeta.setLore(List.of("§6右クリックで使用可能"));
                fortuneitemmeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING, "fortune_item");


                fortuneitem.setItemMeta(fortuneitemmeta); 
                fortune.getInventory().addItem(fortuneitem); 
            }

            if (config.getBoolean("medium.check")){

                ItemStack mediumitem = new ItemStack(Material.NETHER_STAR, 1);

                ItemMeta mediumitemmeta = mediumitem.getItemMeta();
                mediumitemmeta.setDisplayName("§5§l霊媒師用のアイテム");
                mediumitemmeta.setLore(List.of("§6右クリックで使用可能"));
                mediumitemmeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING, "medium_item");


                mediumitem.setItemMeta(mediumitemmeta); 
                medium.getInventory().addItem(mediumitem); 
            }





        }

        Player gameplayer;

        public void whitelistp(){

            for (Player player : Bukkit.getOnlinePlayers()){

                UUID id = player.getUniqueId();

                int positioncheck = position.getOrDefault(id,0);
                if (positioncheck >= 1){
                    gameplayer = Bukkit.getPlayer(id);
                    gameplayer.setWhitelisted(true);    
                }else if (positioncheck == 0){     
                    player.setWhitelisted(false);
                    player.kick(Component.text("§cゲームが開始されました。ゲーム終了までお待ちください。"));
                }

                Bukkit.setWhitelist(true); 

            }



        }

           public static int wolflistcount = 0;
           public static int villagerlistcount = 0;
        public void gstart(Player player){
            whitelistp();

            player.sendMessage("§e人狼ゲームを開始させました！");
            day++;
            Bukkit.broadcastMessage(day + "日目になりました");

            int stpX = startpointX;
            int stpY = startpointY;
            int stpZ = startpointZ;

            World world = Bukkit.getWorld(startpointworld);
            Location location = new Location(world,stpX,stpY,stpZ);
            for (Player onlineplayer : Bukkit.getOnlinePlayers()) {
                onlineplayer.teleport(location);
                onlineplayer.setGameMode(GameMode.ADVENTURE);
            }

            if (getConfig().getBoolean("spectator.check")) {
                spectator.setGameMode(GameMode.SPECTATOR);
            }
            world.setTime(1000);
            for (Player onlineplayer : Bukkit.getOnlinePlayers()){

                UUID id  = onlineplayer.getUniqueId();

                int getpotision = position.get(id);

       
                if (getpotision == 1){
                    wolflistcount++;
                }else if (getpotision >= 3 && getpotision <=6){    
                    villagerlistcount++;
                }



            }

            FileConfiguration config = getConfig();
            getConfig().set("gamePlaying",true);
         
            saveConfig();
            if (config.getBoolean("wolf.check")) {
                wolf.sendMessage("§c§lあなたは人狼になりました");
                wolf.sendMessage("        §7[§a§l役職説明§r§7]         ");
                wolf.sendMessage("他陣営に気づかれないよう倒しましょう!");
                wolf.sendMessage("§lアイテムが配られました");
            }
            if (config.getBoolean("madman.check")) {
                madman.sendMessage("§4あなたは狂人になりました");
                madman.sendMessage("        §7[§a§l役職説明§r§7]         ");
                madman.sendMessage("他陣営に気づかれないよう味方の人狼を見つけ出し協力して他陣営を倒そう！");
                madman.sendMessage("§lアイテムが配られました");

            }
            if (config.getBoolean("knight.check")) {
                knight.sendMessage("§bあなたは騎士になりました");
                knight.sendMessage("        §7[§a§l役職説明§r§7]         ");
                knight.sendMessage("味方を守ろう！");
                knight.sendMessage("§lアイテムが配られました");

            }
            if (config.getBoolean("fortune.check")){
                fortune.sendMessage("§bあなたは占い師になりました");
                fortune.sendMessage("        §7[§a§l役職説明§r§7]         ");
                fortune.sendMessage("怪しいプレイヤーを見つけろ");
                fortune.sendMessage("§lアイテムが配られました");

            }
            if (config.getBoolean("medium.check")){
                medium.sendMessage("§bあなたは霊媒師になりました");
                medium.sendMessage("        §7[§a§l役職説明§r§7]         ");
                medium.sendMessage("怪しいプレイヤー....§kaaaaaa");
                medium.sendMessage("§lアイテムが配られました");

            }
            if (config.getBoolean("villager.check")){
                villager.sendMessage("§bあなたは市民になりました");
                villager.sendMessage("        §7[§a§l役職説明§r§7]         ");
                villager.sendMessage("              逃げろ");

            }
            if (config.getBoolean("spectator.check")){

                spectator.sendMessage("§aあなたは観戦者になりました");
                spectator.sendMessage("        §7[§a§l役職説明§r§7]         ");
                spectator.sendMessage("              §kaaaaaa");
            }

            new Timer(this).runTaskTimer(MiniCashwerewolf.getPlugin(),0L,20L);
                nowtime = true;

        }
        public void gstop(Player player){

            if (config.getBoolean("gamePlaying")) {


                tstop = true;
                mstop = true;
                for (Player onlinep : Bukkit.getOnlinePlayers()) {
                    onlinep.sendTitle("§kaaa§r§e引き分け！！§kaaa§r", "", 10, 70, 20);
                    onlinep.setGameMode(GameMode.SPECTATOR);
                    onlinep.setWhitelisted(false);
                }


                player.sendMessage("§6§lゲームを停止させました！");

                Bukkit.setWhitelist(false);
                getConfig().set("gamePlaying", false);
                saveConfig();
            }else {
                player.sendMessage("§c§l現在ゲームが進行中ではありません!\nゲームが進行中のみこのコマンドを実行できます");
            }
        }

        public void day(){

            Bukkit.broadcastMessage("§6昼になりました");
            Bukkit.broadcastMessage("§lマイクをONにして話し合いましょう");
            new Timer(this).runTaskTimer(MiniCashwerewolf.getPlugin(),0L,20L);
            for (Player titleonlinep : Bukkit.getOnlinePlayers()){
                titleonlinep.sendTitle("§eマイクをONにして話し合いましょう","",10,70,20);
            }
            World world = Bukkit.getWorld(startpointworld);
            world.setTime(1000);

        }

        public void noon(){

            Bukkit.broadcastMessage("§5夜になりました");
            Bukkit.broadcastMessage("§lマイクをOFFにしてください");

            new Timer(this).runTaskTimer(MiniCashwerewolf.getPlugin(),0L,20L);
            for (Player titleonlinep : Bukkit.getOnlinePlayers()){
                titleonlinep.sendTitle("§8マイクをOFFにしましょう","",10,70,20);
            }


            World world = Bukkit.getWorld(startpointworld);
            world.setTime(18000);

        }

        //会議
        public void meeting(){

            int mtgX = meetingpointX;
            int mtgY = meetingpointY;
            int mtgZ = meetingpointZ;

            World world = Bukkit.getWorld(meetingpointworld);
            Location location = new Location(world,mtgX,mtgY,mtgZ);
            for (Player onlineplayer : Bukkit.getOnlinePlayers()) {
                onlineplayer.teleport(location);
            }

            Bukkit.broadcastMessage("§a§l会議が開始されました");
            Bukkit.broadcastMessage("§a残り２０秒で投票が行われます");
            Bukkit.broadcastMessage("§c怪しいと思うプレイヤーに投票してください");
            new MeetingTimer(this).runTaskTimer(MiniCashwerewolf.getPlugin(),0L,20L);

        }
        int addmeetingvotecheckmap;

    public void vote(){

        addmeetingvotecheckmap = 0;

        Bukkit.broadcastMessage("§a§l投票が開始されました");
        Bukkit.broadcastMessage("§a時間内に投票を行いましょう");
        Bukkit.broadcastMessage("§c怪しいと思うプレイヤーに投票してください");

        Inventory voteGUI = Bukkit.createInventory(new VoteGuiHolder(),27,"プレイヤー投票");  //サイズ9*○○

        int count = 0; 
        int onlinecount = Bukkit.getOnlinePlayers().size();
        for (Player player: Bukkit.getOnlinePlayers()){
            if (count <= onlinecount) {
                ItemStack playerhead = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta phskullmeta = (SkullMeta) playerhead.getItemMeta();
                phskullmeta.setOwningPlayer(player);
                phskullmeta.setDisplayName(player.getName());  
                phskullmeta.setLore(List.of("§6クリックで" + player.getName() + "に投票"));

                playerhead.setItemMeta(phskullmeta); 
                voteGUI.setItem(count,playerhead);
                count++;
                UUID id = player.getUniqueId();
                guicheck.put(id,1); 
            }

            ItemStack cancelvote= new ItemStack(Material.REDSTONE);
            ItemMeta cancelitem = cancelvote.getItemMeta();
            cancelitem.setDisplayName("§c投票をキャンセル");
            cancelvote.setItemMeta(cancelitem);
            voteGUI.setItem(26,cancelvote);


        }

        for (Player py:Bukkit.getOnlinePlayers()){
            py.sendMessage("投票用GUIOpenまで到達したよ！");
            py.openInventory(voteGUI);
        }
    }

        private Map<String,Integer> meetingvotecheck = new HashMap<>();
        public String votego(String nameVote){
            addmeetingvotecheckmap = meetingvotecheck.getOrDefault(nameVote,0);

            Bukkit.broadcastMessage("現在：" + addmeetingvotecheckmap);

            addmeetingvotecheckmap++;
            meetingvotecheck.put(nameVote,addmeetingvotecheckmap);
            String retrunstring = "§e" + nameVote + "§r§7に投票しました";
            return retrunstring;

        }


        public void voteresult(){

            String maxname = null;
            int maxvalue = 0;
            for (Map.Entry<String, Integer> entry : meetingvotecheck.entrySet()){

                String name = entry.getKey();
                int valuecount = entry.getValue();
                if (valuecount > maxvalue){
                    maxvalue = valuecount;
                    maxname = name;

                }
            }
            if (maxname != null) {

                Player targetplayer = Bukkit.getPlayer(maxname);
                if (targetplayer != null) {
                    targetplayer.setHealth(0.0);
                    targetplayer.setGameMode(GameMode.SPECTATOR);
                    Bukkit.broadcastMessage("§e" + targetplayer.getName() + "§r§cは投票によって追放されました");
                }
            }else {
                Bukkit.broadcastMessage("§c§lプレイヤーが見つからなかったため誰も追放されませんでした");
            }

            for (Player player : Bukkit.getOnlinePlayers()){

                player.closeInventory();

            }

        }
    public void wolfwin(){

        tstop = true;
        mstop = true;
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
        Bukkit.setWhitelist(false);
        Bukkit.getLogger().info("[§aMiniCashwerewolf§r] §lホワイトリストをoffにしました");
        getConfig().set("gamePlaying",false);
        saveConfig();
        Bukkit.getLogger().info("[§aMiniCashwerewolf§r] §lゲーム終了処理がすべて完了しました");

    }

    public void villagerwin(){

        tstop = true;
        mstop = true;
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
        Bukkit.setWhitelist(false);
        getConfig().set("gamePlaying",false);
        saveConfig();
    }
        public void givecommanditem(Player player,String itemn){


            NamespacedKey namekey = new NamespacedKey(plugin,"wolfitem");
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

                wolfitem.setItemMeta(wolfitemmeta); 
                player.getInventory().addItem(wolfitem); 

                player.sendMessage("§4" + player.getName() + "に「人狼の斧」を付与しました");

            }
            if (itemn.equals("madman")) {
                ItemStack madmanitem = new ItemStack(Material.ECHO_SHARD, 1);

                ItemMeta madmanitemeta = madmanitem.getItemMeta();
                madmanitemeta.setDisplayName("§c§l味方を探せ！");
                madmanitemeta.setLore(List.of("§6右クリックで使用可能"));
                madmanitemeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING, "madman_item");


                madmanitem.setItemMeta(madmanitemeta); 
                player.getInventory().addItem(madmanitem); 

                player.sendMessage("§4" + player.getName() + "に「味方を探せ！」を付与しました");

            }

            if (itemn.equals("knight")){
                ItemStack knightitem = new ItemStack(Material.SHIELD, 1);

                ItemMeta knightitemmeta = knightitem.getItemMeta();
                knightitemmeta.setDisplayName("§c守りの盾");
                knightitemmeta.setLore(List.of("§6右クリックで使用可能"));
                knightitemmeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING, "knight_item");


                knightitem.setItemMeta(knightitemmeta); 
                player.getInventory().addItem(knightitem); 

                player.sendMessage("§4" + player.getName() + "に「守りの盾」を付与しました");
            }
            if (itemn.equals("fortunecheck")){

                ItemStack fortuneitem = new ItemStack(Material.AMETHYST_SHARD, 1);

                ItemMeta fortuneitemmeta = fortuneitem.getItemMeta();
                fortuneitemmeta.setDisplayName("§5§l占い");
                fortuneitemmeta.setLore(List.of("§6右クリックで使用可能"));
                fortuneitemmeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING, "fortune_item");


                fortuneitem.setItemMeta(fortuneitemmeta); 
                player.getInventory().addItem(fortuneitem); 

                player.sendMessage("§4" + player.getName() + "に「占い」を付与しました");
            }

            if (itemn.equals("mediumcheck")){

                ItemStack mediumitem = new ItemStack(Material.NETHER_STAR, 1);

                ItemMeta mediumitemmeta = mediumitem.getItemMeta();
                mediumitemmeta.setDisplayName("§5§l霊媒師用のアイテム");
                mediumitemmeta.setLore(List.of("§6右クリックで使用可能"));
                mediumitemmeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING, "medium_item");


                mediumitem.setItemMeta(mediumitemmeta);
                player.getInventory().addItem(mediumitem); 

                player.sendMessage("§4" + player.getName() + "に「霊媒師のアイテム」を付与しました");
            }


            if (itemn.equals("pcheck")){

                player.getInventory().addItem(createItem("pcheck",1));

                player.sendMessage("§4" + player.getName() + "に「残り人数確認の書」を付与しました");



            }




            if (itemn.equals("coin")) {
                ItemStack coin = new ItemStack(Material.GOLD_INGOT);
                ItemMeta spawngolditemmeta = coin.getItemMeta();
                spawngolditemmeta.setDisplayName("§6コイン");
                spawngolditemmeta.setLore(List.of("§a人狼ゲーム専用コイン"));
                spawngolditemmeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING, "spawn_gold_ingot");
                coin.setItemMeta(spawngolditemmeta); 


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

            }

        }

        public void villagerspawn(Player player,World world,Location location){
            NamespacedKey namekey = new NamespacedKey(plugin,"villagergui");
            String vid = "villager";
            Villager villager = (Villager) world.spawn(location, Villager.class);


            villager.getPersistentDataContainer().set(
                    namekey,
                    PersistentDataType.STRING,
                    vid
            );

            villager.setAI(false);        
            villager.setInvulnerable(true); 
            villager.setCollidable(false);  
            villager.setSilent(true);       
            villager.setProfession(Villager.Profession.NONE);


            String id = villager.getPersistentDataContainer().get(
                    namekey,
                    PersistentDataType.STRING
            );

            if ("villager".equals(id)){

                List<MerchantRecipe> recipes = new ArrayList<>();
                ItemStack nitem1 = new ItemStack(Material.COOKED_BEEF, 2);
                ItemStack diamondsword = new ItemStack(Material.DIAMOND_SWORD, 1);

                MerchantRecipe recipe = new MerchantRecipe(
                        new ItemStack(nitem1),
                        9999
                );
                MerchantRecipe recipe2 = new MerchantRecipe(
                        new ItemStack(createItem("pcheck",1)), //品物
                        9999 
                );
                MerchantRecipe recipe3 = new MerchantRecipe(
                        new ItemStack(diamondsword), //品物
                        9999 
                );
                MerchantRecipe recipe4 = new MerchantRecipe(
                        new ItemStack(createItem("glowing",1)), //品物
                        9999 
                );
                MerchantRecipe recipe5 = new MerchantRecipe(
                        new ItemStack(createItem("speed",1)), //品物
                        9999 
                );
                MerchantRecipe recipe6 = new MerchantRecipe(
                        new ItemStack(createItem("invisibility",1)), //品物
                        9999 
                );
                ItemStack cost4 = createItem("coin",4);
                ItemStack cost6 = createItem("coin",6);
                ItemStack cost10 = createItem("coin",10);
                ItemStack cost15 = createItem("coin",15);

                recipe.addIngredient(new ItemStack(cost4)); 
                recipe2.addIngredient(new ItemStack(cost10));
                recipe3.addIngredient(new ItemStack(cost6));
                recipe4.addIngredient(new ItemStack(cost15));
                recipe5.addIngredient(new ItemStack(cost6));
                recipe6.addIngredient(new ItemStack(cost15));

                recipes.add(recipe);
                recipes.add(recipe2);
                recipes.add(recipe3);
                recipes.add(recipe4);
                recipes.add(recipe5);
                recipes.add(recipe6);
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
                
                ItemStack pcheckitem = new ItemStack(Material.LEATHER_HORSE_ARMOR);
                ItemMeta pcheckitemmeta = pcheckitem.getItemMeta();
                pcheckitemmeta.setDisplayName("§6残り人数確認の書");
                pcheckitemmeta.setLore(List.of("§a右クリックで使用可能"));
                pcheckitemmeta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING, "people_check");
                pcheckitem.setItemMeta(pcheckitemmeta); 

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

                
            }
            return ritem;
        }



}
