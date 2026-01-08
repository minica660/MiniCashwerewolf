package MiniCash.miniCashwerewolf.command;

import MiniCash.miniCashwerewolf.MiniCashwerewolf;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static MiniCash.miniCashwerewolf.MiniCashwerewolf.position;
import static MiniCash.miniCashwerewolf.command.Publick.*;

public class Main implements CommandExecutor {

    private final MiniCashwerewolf plugin;

    public Main(MiniCashwerewolf plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        Player player = (Player) sender;
        if (args.length == 0) {
            plugin.help(player);
            return true;
        }

        //help
        if (args[0].equals("help") && player.hasPermission("minicashwerewolf.commands.help")) {
            plugin.help(player);
            return true;
        } else if (args[0].equals("reload")) {    //config.ymlリロード
            plugin.mreload(player);
            return true;
        } else if (args[0].equals("start") && player.hasPermission("minicashwerewolf.commands.start")) { //ゲームスタート
            //ゲームスタート用メソッド呼び出し
            //役職決定
            plugin.roleset();   //ランダム役職設定メソッド呼び出し

            //ゲームがすでにスタートしていたら処理を停止
            if (plugin.getConfig().getBoolean("gamePlaying")){       //ゲーム実行中だったら処理を終了する（エラー防止）

                player.sendMessage("§c§l現在進行中の人狼ゲームがあります\nこのコマンドを実行させる場合は/mwgame stop\nと打ちゲームを一度終了させてください");

                return true;

            }
            //人数が等しくなかったら処理を止める
            if (plugin.playercheck()) {
                player.sendMessage("§c§l設定人数に役職人数が達していないためゲームが開始できません");
                return true;
            }

            plugin.player();
            plugin.gstart(player);
            plugin.giveitem();

            return true;
        }else if (args[0].equals("position") && player.hasPermission("minicashwerewolf.commands.position")) {     //役職人数設定
            if (args.length < 3) {
                player.sendMessage("§c引数を確認してください");
                return true;
            }

            //何をしたいかチェック
            String check = args[1];
            if (check.equals("set")){
                if (args.length == 4) {
                    String positionch = args[2];
                    //役職人数を設定
                    String speople = args[3];
                    int people;
                    try {
                        people = Integer.parseInt(speople);

                    } catch (NumberFormatException e) {
                        // 変換に失敗（数字以外の文字が入力された）した場合の処理

                        // ユーザーにエラーメッセージを送信
                        sender.sendMessage("§c§l" + speople + "§r§cは有効な数字ではありません");

                        // コマンド処理を中断し、終了する
                        return true;
                    }


                    //役職名が正しいかのチェック
                    if (plugin.check(positionch)) {
                        //メソッド呼び出し
                        String returnmessage = plugin.positionset(positionch, people);

                        player.sendMessage(returnmessage);

                    }
                }else {
                    player.sendMessage("§c役職の設定人数コマンドの入力方法を確認してください");
                    return true;
                }
            }else if (check.equals("check")){
                String positionch = args[2];
                if (plugin.check(positionch)) {
                    String bsetcg = positionch + ".check";
                    String isetcg = positionch + ".count";
                    boolean bcheckconfig = plugin.getConfig().getBoolean(bsetcg);
                    int scheckconfig = plugin.getConfig().getInt(isetcg);

                    player.sendMessage("========  §b現在の" + positionch + "人数  §r========");
                    player.sendMessage("            役職有無 : " + bcheckconfig);
                    player.sendMessage("         役職設定人数 : " + scheckconfig);
                    player.sendMessage("========================================");

                }else {
                    player.sendMessage("§c有効な役職名を入力してください");
                    return true;
                }
            }else if (check.equals("unset")){

                String positionch = args[2];
                if (plugin.check(positionch)) {

                    //メソッド呼び出し
                    String returnmessage = plugin.positionunset(positionch);

                    player.sendMessage(returnmessage);

                } else {
                    return true;
                }

            }



        }else if (args[0].equals("player") && player.hasPermission("minicashwerewolf.commands.player")) {      //手動で自分の役職決定するよう(管理者向け)

            if (args.length < 2){
                player.sendMessage("§4§l管理者用コマンド入力方法を確認してください");
                return true;
            }

            if (args[1].equals("set")) {
                String positionargs = args[2];

                plugin.playerset(player, positionargs);

            }else if (args[1].equals("check")){

                if (args.length < 3){
                    player.sendMessage("§c引数を確認してください");
                    return true;
                }

                String a2 = args[2];
                String japosition = null;
                Player target = null;
                try {
                    target = Bukkit.getPlayerExact(a2);
                    UUID id = target.getUniqueId();
                    int getposition = position.getOrDefault(id,0);
                    japosition = plugin.numberposition(getposition);


                    player.sendMessage("§6§l現在" + target.getName() + "の役職は§r" + japosition + "§§6です");
                }catch (Exception e){
                    player.sendMessage("§cそのプレイヤーは現在オンラインではありません");
                    return true;
                }

                return true;




            }


            return true;
        }else if (args[0].equals("give") && player.hasPermission("minicashwerewolf.commands.give")){
            if (args.length < 2){
                plugin.help(player);
                return true;
            }
            String itemname = args[1];
            plugin.givecommanditem(player,itemname);
        } else if (args[0].equals("stop") && player.hasPermission("minicashwerewolf.commands.stop")) {
            plugin.gstop(player);
            return true;

        } else if (args[0].equals("villagerspawn") && player.hasPermission("minicashwerewolf.commands.villagerspawn")) {


            Location location = player.getLocation();

            World world = player.getWorld();
            plugin.villagerspawn(player,world,location);



        } else {
            plugin.help(player);
            return true;
        }


        return true;
    }
}
