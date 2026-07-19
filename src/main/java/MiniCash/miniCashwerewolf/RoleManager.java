package MiniCash.miniCashwerewolf;

import MiniCash.miniCashwerewolf.Event.Event;
import MiniCash.miniCashwerewolf.model.Role;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;


public class RoleManager {

    private static Map<RoleType , Role> roles = new HashMap<>();    // 使用できるロール valueによりRoleModelを使用
    private static Map<UUID, RoleType> playerRole = new HashMap<>();
    public static List<RoleType> checklist = new ArrayList<>();     //役職が設定されているかのチェック用リスト

    public static Map<RoleType , Role> getRoles() {
        return roles;
    }

    public static boolean activeRole(RoleType role){
        return roles.containsKey(role);
    }

    public static boolean playerHasGameRole(UUID uuid){
        return playerRole.containsKey(uuid);
    }

    public static void setCheckList(){
        checklist.clear();
        checklist.add(RoleType.WOLF); //人狼
        checklist.add(RoleType.MADMAN); //狂人
        checklist.add(RoleType.KNIGHT); //騎士
        checklist.add(RoleType.FORTUNE); //占い師
        checklist.add(RoleType.MEDIUM); //霊媒師
        checklist.add(RoleType.MEDIUM); //市民+

        
        checklist.add(RoleType.SPECTATOR); //観戦者用

    }

    public enum RoleType{
        NO("no","役職無し"),
        WOLF("wolf","人狼"),
        MADMAN("madman","狂人"),
        KNIGHT("knight","騎士"),
        FORTUNE("fortune","占い師"),
        MEDIUM("medium","霊媒師"),
        VILLAGER("villager","村人"),
        SPECTATOR("spectator","観戦者");

        private String roleName;
        private String roleJapanaseName;
        RoleType(String roleName, String roleJapanaseName){
            this.roleName = roleName;
            this.roleJapanaseName = roleJapanaseName;
        }


        public String getJapaneseName() {
            return roleJapanaseName;
        }

        public String getRoleName() {
            return this.roleName;
        }
    }

    //引数として受け取った役職名の役職があるかどうかのチェック
    public static boolean check(String ps){

        //リストに入っている役職名だったらtrueを返す
        return checklist.contains(RoleType.valueOf(ps.toUpperCase()));

    }

    public static Map<UUID ,RoleType> getPlayerRole(){
        return playerRole;
    }

    //役職設定人数分プレイヤーがいるかをチェック
    //いなかったらtrue,いたらfalse　を返します
    public static boolean playercheck(){

        //変数
        int wolfgoukei = 0;   //人狼実際の合計人数チェック
        int madmangoukei = 0; //狂人合計
        int knightgoukei = 0; //騎士合計
        int fortunegoukei = 0; //占い師合計
        int mediumgoukei = 0; //霊媒師合計
        int villagergoukei = 0; //村人合計
        int spectatorgoukei = 0; //観戦者の合計

        int wocheck = roles.get(RoleType.WOLF).getTotal();
        int mdmcheck = roles.get(RoleType.MADMAN).getTotal();
        int knicheck = roles.get(RoleType.KNIGHT).getTotal();
        int ftcheck = roles.get(RoleType.FORTUNE).getTotal();
        int mdiumcheck = roles.get(RoleType.MEDIUM).getTotal();
        int vlgrcheck = roles.get(RoleType.VILLAGER).getTotal();
        int sprcheck = roles.get(RoleType.SPECTATOR).getTotal();
        //案2
        for (UUID id : playerRole.keySet()){
            RoleType playercheck = playerRole.get(id);

            //人数確認（役職Mapの値が1だったら人狼合計確認変数の値を+1）
            if (playercheck.equals(RoleType.WOLF)) {
                wolfgoukei++;

            }else if (playercheck.equals(RoleType.MADMAN)) {
                madmangoukei++;
            }else if (playercheck.equals(RoleType.KNIGHT)) {
                knightgoukei++;
            } else if (playercheck.equals(RoleType.FORTUNE)) {
                fortunegoukei++;
            }else if (playercheck.equals(RoleType.MEDIUM)) {
                mediumgoukei++;
            }else if (playercheck.equals(RoleType.VILLAGER)) {
                villagergoukei++;
            }else if (playercheck.equals(RoleType.SPECTATOR)) {
                spectatorgoukei++;
            }

        }


        //最終人数確認
        //人狼(先ほど処理したものを使用)

        if (wolfgoukei != wocheck) {        //設定された人狼人数と等しくなかったらfalseを返す
            return false;
        }


        if (madmangoukei != mdmcheck) {
            return false;
        }


        if (knightgoukei != knicheck){
            return false;
        }

        if (fortunegoukei != ftcheck){
            return false;
        }


        if (mediumgoukei != mdiumcheck) {
            return false;
        }


        if (villagergoukei != vlgrcheck) {
            return false;
        }




        return true;
    }


    //ランダムな役職設定
    public static void randomPlayerRoleSet() {

        List<RoleType> rolePool = new ArrayList<>();

        Map<RoleType, Integer> allocatedCounts = new HashMap<>();

        for (RoleType roleType : playerRole.values()) {

            allocatedCounts.put(roleType, allocatedCounts.getOrDefault(roleType, 0) + 1);

        }

        for (Map.Entry<RoleType, Role> entry : roles.entrySet()) {
            RoleType type = entry.getKey();
            Role role = entry.getValue();

            if (type == RoleType.SPECTATOR || type == RoleType.NO) {
                continue;
            }

            if (role.isActive() && role.getTotal() > 0) {

                int alreadyAllocated = allocatedCounts.getOrDefault(type, 0);
                int remainingSlots = role.getTotal() - alreadyAllocated;

                for (int i = 0; i < remainingSlots; i++) {
                    rolePool.add(type);
                }
            }
        }

        Collections.shuffle(rolePool);

        List<Player> gamePlayers = GameManager.getGameplayers();

        int poolIndex = 0;

        for (Player player : gamePlayers) {
            UUID uuid = player.getUniqueId();

            // 既にそのプレイヤーのロールが決まっていたらスキップ
            if (playerRole.containsKey(uuid)) {
                RoleType currentRole = playerRole.get(uuid);
                player.sendMessage("§6あなたの役職は §l" + currentRole.roleJapanaseName + " §r§6です");
                continue;
            }

            if (poolIndex >= rolePool.size()) {
                playerRole.put(uuid, RoleType.SPECTATOR);
                player.sendMessage(
                        Component.text("役職が満員のため観戦者となりました").color(NamedTextColor.GOLD)
                );
                continue;
            }

            RoleType assignedRole = rolePool.get(poolIndex);
            playerRole.put(uuid, assignedRole);
            poolIndex++;

            player.sendMessage("§6あなたの役職は §l" + assignedRole.roleJapanaseName + " §r§6です");
        }

    }


    //コマンドでの役職決定用
    //処理内容
    //1:入力された役職名をチェック
    //2:その役職の設定人数が１人以上かをチェック
    //3:もし１人以上なら役職を設定
    //違うなら設定せずエラーメッセージを実行者に送信
    public static void playerRoleSet(Player player, RoleType roleType){

        UUID id = player.getUniqueId();

        Role role =  roles.get(roleType);

        if (roleType.equals(RoleType.WOLF)) {
            if (role.isActive() && role.getTotal() >= 1) {
                //プレイヤー役職に人狼番号を設定
                playerRole.put(id, RoleType.WOLF);
                player.sendMessage("§6役職を§l人狼§r§6に設定しました");

            }else {
                player.sendMessage("§4§l役職設定人数を確認してください");
            }
        }else if (roleType.equals(RoleType.MADMAN)) {
            if (role.isActive() && role.getTotal() >= 1) {
                //プレイヤー役職に狂人番号を設定
                playerRole.put(id, RoleType.MADMAN);
                player.sendMessage("§6役職を§l狂人§r§6に設定しました");

            }else {
                player.sendMessage("§4§l役職設定人数を確認してください");
            }
        }else if (roleType.equals(RoleType.KNIGHT)) {

            if (role.isActive() && role.getTotal() >= 1) {
                //プレイヤー役職に騎士番号を設定
                playerRole.put(id, RoleType.KNIGHT);
                player.sendMessage("§6役職を§l騎士§r§6に設定しました");

            }else {
                player.sendMessage("§4§l役職設定人数を確認してください");
            }

        }else if (roleType.equals(RoleType.FORTUNE)) {
            if (role.isActive() && role.getTotal() >= 1) {
                //プレイヤー役職に占い師番号を設定
                playerRole.put(id, RoleType.FORTUNE);
                player.sendMessage("§6役職を§l占い師§r§6に設定しました");

            }else {
                player.sendMessage("§4§l役職設定人数を確認してください");
            }

        }else if (roleType.equals(RoleType.MEDIUM)) {

            if (role.isActive() && role.getTotal() >= 1) {
                //プレイヤー役職に霊媒師番号を設定
                playerRole.put(id, RoleType.MEDIUM);
                player.sendMessage("§6役職を§l霊媒師§r§6に設定しました");

            }else {
                player.sendMessage("§4§l役職設定人数を確認してください");
            }

        }else if (roleType.equals(RoleType.VILLAGER)) {

            if (role.isActive() && role.getTotal() >= 1) {
                //プレイヤー役職に市民番号を設定
                playerRole.put(id, RoleType.VILLAGER);
                player.sendMessage("§6役職を§l市民§r§6に設定しました");

            }else {
                player.sendMessage("§4§l役職設定人数を確認してください");
            }

        }else if (roleType.equals(RoleType.SPECTATOR)) {

            playerRole.put(id, RoleType.SPECTATOR);
            player.sendMessage("§6役職を§5§l観戦者§r§6に設定しました");


        }else {
            player.sendMessage("§c§lサブコマンド入力方法を確認してください！");
        }


    }



    // 指定した役職の最大人数に現在のplayerRole変数を参照して足りているかのチェック
    public static boolean isRoleUnderstaffed(RoleType type) {

        Role role = roles.get(type);
        if (role == null) {
            return false;
        }
        int maxTotal = role.getTotal();

        long currentCount = playerRole.values().stream()
                .filter(roleType -> roleType == type)
                .count();

        return currentCount < maxTotal;
    }




    // 指定した役職の最大人数を変更します
    public static void setRole(RoleType roleType , int value){


        Role targetRole = roles.get(roleType);

        targetRole.setActive(true);
        targetRole.setTotal(value);



    }
}
