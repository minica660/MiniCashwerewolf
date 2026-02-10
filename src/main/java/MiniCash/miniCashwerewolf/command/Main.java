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
        if (args[0].equals("help") && player.hasPermission("minicashwerewolf.commands.help")) {
            plugin.help(player);
            return true;
        } else if (args[0].equals("reload")) {    
            plugin.mreload(player);
            return true;
        } else if (args[0].equals("start") && player.hasPermission("minicashwerewolf.commands.start")) { 
            plugin.roleset();  

            if (plugin.getConfig().getBoolean("gamePlaying")){       

                player.sendMessage("§c§l現在進行中の人狼ゲームがあります\nこのコマンドを実行させる場合は/mwgame stop\nと打ちゲームを一度終了させてください");

                return true;

            }
            if (plugin.playercheck()) {
                player.sendMessage("§c§l設定人数に役職人数が達していないためゲームが開始できません");
                return true;
            }

            plugin.player();
            plugin.gstart(player);
            plugin.giveitem();

            return true;
        }else if (args[0].equals("position") && player.hasPermission("minicashwerewolf.commands.position")) {     
            if (args.length < 3) {
                player.sendMessage("§c引数を確認してください");
                return true;
            }

            String check = args[1];
            if (check.equals("set")){
                if (args.length == 4) {
                    String positionch = args[2];
                    
                    String speople = args[3];
                    int people;
                    try {
                        people = Integer.parseInt(speople);

                    } catch (NumberFormatException e) {
                        
                        sender.sendMessage("§c§l" + speople + "§r§cは有効な数字ではありません");
                        return true;
                    }

                    if (plugin.check(positionch)) {
                        
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
                    String returnmessage = plugin.positionunset(positionch);

                    player.sendMessage(returnmessage);

                } else {
                    return true;
                }

            }


        }else if (args[0].equals("player") && player.hasPermission("minicashwerewolf.commands.player")) {      

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
