package MiniCash.miniCashwerewolf.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Tab implements TabCompleter {
    List<String> list = new ArrayList<>();

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {


        if (args.length == 1 ){
            list.clear();
            list.add("help");
            list.add("start");
            list.add("stop");
            list.add("player");
            list.add("position");
            list.add("give");
            list.add("villagerspawn");
        }else if (args.length >= 2) {

            if (args[0].equals("position")) {
                list.clear();
                list.add("set");
                list.add("unset");
                list.add("check");
            }else if (args[0].equals("player")) {
                list.clear();
                list.add("set");
                list.add("check");
                if (args[1].equals("check")){
                    list.clear();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        list.add(player.getName());
                    }
                }
            }else if (args[0].equalsIgnoreCase("give")){
                list.clear();
                list.add("wolf");
                list.add("madman");
                list.add("knight");
                list.add("fortunecheck");
                list.add("mediumcheck");
                list.add("pcheck");
                list.add("coin");
                list.add("smoke");

            }
        }


        return list;
    }
}
