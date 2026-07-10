package MiniCash.miniCashwerewolf.command;

import MiniCash.miniCashwerewolf.DB.DB;
import MiniCash.miniCashwerewolf.MiniCashwerewolf;
import MiniCash.miniCashwerewolf.RoleManager;
import MiniCash.miniCashwerewolf.WolfMain;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

import static MiniCash.miniCashwerewolf.MiniCashwerewolf.gamePlaying;
import static MiniCash.miniCashwerewolf.MiniCashwerewolf.position;
import static MiniCash.miniCashwerewolf.WolfMain.distributionItem;

public class GameCommand implements BasicCommand {
    private MiniCashwerewolf plugin;

    private final DB databasemanager;
    private final WolfMain wolfmain;


    public GameCommand(MiniCashwerewolf plugin , DB databasemanager , WolfMain wolfmain) {
        this.plugin = plugin;
        this.databasemanager = databasemanager;
        this.wolfmain = wolfmain;
    }

    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] args) {

        String sub = args[0];

        CommandSender sender = commandSourceStack.getSender();

        if(sub.equals("help")){

            if(sender.hasPermission("minicashwerewolf.command.game.help")){
                plugin.help(sender);
                return;
            }


        } else if (sub.equals("start")) {

            RoleManager.roleset();   //ランダム役職設定メソッド呼び出し

            //ゲームがすでにスタートしていたら処理を停止
            if (gamePlaying){       //ゲーム実行中だったら処理を終了する（エラー防止）

                sender.sendMessage("§c§l現在進行中の人狼ゲームがあります\nこのコマンドを実行させる場合は/mwgame stop\nと打ちゲームを一度終了させてください");

                return;

            }

            //人数が等しくなかったら処理を止める
            if (!RoleManager.playercheck()) {
                sender.sendMessage("§c§l設定人数に役職人数が達していないためゲームが開始できません");
                return;
            }


            //game_dataテーブルリセット
            databasemanager.cleanGameDataTABLES();
            plugin.getLogger().info("game_dataテーブルをリセットしました");

            plugin.player();



            wolfmain.gstart(player);
            distributionItem();



        }else if (args[0].equals("position") && sender.hasPermission("minicashwerewolf.command.game.position")) {     //役職人数設定
            if (args.length < 3) {
                sender.sendMessage("§c引数を確認してください");
                return;
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

                        return ;
                    }


                    //役職名が正しいかのチェック
                    if (plugin.check(positionch)) {
                        //メソッド呼び出し
                        String returnmessage = plugin.positionset(positionch, people);

                        sender.sendMessage(returnmessage);


                    }
                }else {

                    sender.sendMessage("§c役職の設定人数コマンドの入力方法を確認してください");
                    return;

                }
            }else if (check.equals("check")){
                String positionch = args[2];
                if (plugin.check(positionch)) {
                    String bsetcg = positionch + ".check";
                    String isetcg = positionch + ".count";
                    boolean bcheckconfig = plugin.getConfig().getBoolean(bsetcg);
                    int scheckconfig = plugin.getConfig().getInt(isetcg);

                    sender.sendMessage("========  §b現在の" + positionch + "人数  §r========");
                    sender.sendMessage("            役職有無 : " + bcheckconfig);
                    sender.sendMessage("         役職設定人数 : " + scheckconfig);
                    sender.sendMessage("==============================");

                }else {
                    sender.sendMessage("§c有効な役職名を入力してください");
                    return ;
                }
            }else if (check.equals("unset")){

                String positionch = args[2];
                if (plugin.check(positionch)) {

                    //メソッド呼び出し
                    String returnmessage = plugin.positionunset(positionch);

                    sender.sendMessage(returnmessage);

                    //dbログ用にreturnmessageを編集
                    String dbmessage = returnmessage.replaceAll("§.","");

                } else {
                    return;
                }

            }



        }else if (args[0].equals("player") && sender.hasPermission("minicashwerewolf.command.game.player")) {      //手動で自分の役職決定するよう(管理者向け)

            if (args.length < 2){
                sender.sendMessage("§4§l管理者用コマンド入力方法を確認してください");
                return ;
            }

            if (args[1].equals("set")) {
                String positionargs = args[2];

                plugin.playerset(player, positionargs);

            }else if (args[1].equals("check")){

                if (args.length < 3){
                    sender.sendMessage("§c引数を確認してください");
                    return;
                }

                String a2 = args[2];
                String japosition;
                Player target;
                try {
                    target = Bukkit.getPlayerExact(a2);
                    UUID id = target.getUniqueId();
                    int getposition = position.getOrDefault(id,0);
                    japosition = plugin.numberposition(getposition);


                    sender.sendMessage("§6§l現在" + target.getName() + "の役職は§r" + japosition + "§§6です");


                }catch (Exception e){
                    sender.sendMessage("§cそのプレイヤーは現在オンラインではありません");
                    return;
                }

                return;




            }

            return;


        }else if (args[0].equals("give") && sender.hasPermission("minicashwerewolf.command.game.give")){
            if (args.length < 2){
                plugin.help(sender);
                return ;
            }
            String itemname = args[1];
            plugin.givecommanditem(player,itemname);


        } else if (args[0].equals("stop") && sender.hasPermission("minicashwerewolf.command.game.stop")) {

            wolfmain.gstop(player);

            return ;

        } else if (args[0].equals("villagerspawn") && player.hasPermission("minicashwerewolf.command.game.villagerspawn")) {

            if (commandSourceStack.getExecutor() instanceof Player player) {

                Location location = player.getLocation();

                World world = player.getWorld();
                cvillager.villagerspawn(player, world, location);


            }else {
                sender.sendMessage(
                        Component.text("このコマンドはプレイヤーのみ実行可能です").color(NamedTextColor.RED)
                );
            }
            return;

        } else {

            plugin.help(player);

            return;
        }


        return;
    }




    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] args) {
        return BasicCommand.super.suggest(commandSourceStack, args);
    }

    @Override
    public @Nullable String permission() {
        return "minicashwerewolf.command.game";
    }
}
