package MiniCash.miniCashwerewolf.command;

import MiniCash.miniCashwerewolf.DB.DB;
import MiniCash.miniCashwerewolf.MiniCashWereWolf;
import MiniCash.miniCashwerewolf.Villager;
import MiniCash.miniCashwerewolf.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static MiniCash.miniCashwerewolf.MiniCashWereWolf.position;

public class Main implements CommandExecutor {

    private final MiniCashWereWolf plugin;
    private final DB databasemanager;
    private final GameManager wolfmain;
    private final Villager cvillager;
    public Main(MiniCashWereWolf plugin, DB databasemanager, GameManager wolfmain, Villager cvillager) {
        this.plugin = plugin;
        this.databasemanager = databasemanager;
        this.wolfmain = wolfmain;
        this.cvillager = cvillager;
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
            //DBlog追加
            databasemanager.addlog(player.getName(), String.valueOf(player.getUniqueId()),"helpコマンド使用");
            return true;
        } else if (args[0].equals("start") && player.hasPermission("minicashwerewolf.commands.start")) { //ゲームスタート

            //ゲームスタート用メソッド呼び出し
            //役職決定


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

                        //dbログ用にreturnmessageを編集
                        String dbmessage = returnmessage.replaceAll("§.","");
                        //DBlog追加
                        databasemanager.addlog(player.getName(), String.valueOf(player.getUniqueId()), positionch + "設定cmd使用");

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
                    player.sendMessage("==============================");
                    //DBlog追加
                    databasemanager.addlog(player.getName(), String.valueOf(player.getUniqueId()),positionch + "の役職チェックcmd使用");
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

                    //dbログ用にreturnmessageを編集
                    String dbmessage = returnmessage.replaceAll("§.","");
                    //DBlog追加
                    databasemanager.addlog(player.getName(), String.valueOf(player.getUniqueId()),positionch + "設定をfalse cmd使用");

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
                //DBlog追加
                databasemanager.addlog(player.getName(), String.valueOf(player.getUniqueId()),"プレイヤーの役職設定：" + positionargs);
            }else if (args[1].equals("check")){

                if (args.length < 3){
                    player.sendMessage("§c引数を確認してください");
                    return true;
                }

                String a2 = args[2];
                String japosition;
                Player target;
                try {
                    target = Bukkit.getPlayerExact(a2);
                    UUID id = target.getUniqueId();
                    int getposition = position.getOrDefault(id,0);
                    japosition = plugin.numberposition(getposition);


                    player.sendMessage("§6§l現在" + target.getName() + "の役職は§r" + japosition + "§§6です");

                    //DBlog追加
                    databasemanager.addlog(player.getName(), String.valueOf(player.getUniqueId()),target.getName() + "の役職確認cmd使用");
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

            //DBlog追加
            databasemanager.addlog(player.getName(), String.valueOf(player.getUniqueId()),itemname + "を入手しようとコマンド実行");
        } else if (args[0].equals("stop") && player.hasPermission("minicashwerewolf.commands.stop")) {
            wolfmain.gstop(player);

            //DBlog追加
            databasemanager.addlog(player.getName(), String.valueOf(player.getUniqueId()),"ゲーム停止コマンド実行");
            return true;

        } else if (args[0].equals("villagerspawn") && player.hasPermission("minicashwerewolf.commands.villagerspawn")) {


            Location location = player.getLocation();

            World world = player.getWorld();
            cvillager.villagerspawn(player,world,location);

            //DBlog追加
            databasemanager.addlog(player.getName(), String.valueOf(player.getUniqueId()),"村人スポーンコマンドを実行");

            return true;

        } else {
            plugin.help(player);

            //DBlog追加
            databasemanager.addlog(player.getName(), String.valueOf(player.getUniqueId()),"その他コマンド実行上でのエラー");

            return true;
        }


        return true;
    }
}
