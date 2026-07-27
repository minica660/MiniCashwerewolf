package MiniCash.miniCashwerewolf.command;

import MiniCash.miniCashwerewolf.*;
import MiniCash.miniCashwerewolf.model.Role;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static MiniCash.miniCashwerewolf.MiniCashWereWolf.gamePlaying;
import static MiniCash.miniCashwerewolf.GameManager.distributionItem;

public class GameCommand implements BasicCommand {
    private MiniCashWereWolf plugin;

    private final GameManager gameManager;


    public GameCommand(MiniCashWereWolf plugin , GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] args) {

        if (args.length >= 1) {

            String sub = args[0];

            CommandSender sender = commandSourceStack.getSender();

            if (sub.equals("help")) {

                if (sender.hasPermission("minicashwerewolf.command.game.help")) {
                    plugin.help(sender);
                    return;
                }


            } else if (sub.equals("start")) {

                RoleManager.randomPlayerRoleSet();   //ランダム役職設定メソッド呼び出し

                //ゲームがすでにスタートしていたら処理を停止
                if (gamePlaying) {       //ゲーム実行中だったら処理を終了する（エラー防止）

                    sender.sendMessage("§c§l現在進行中の人狼ゲームがあります\nこのコマンドを実行させる場合は/mwgame stop\nと打ちゲームを一度終了させてください");

                    return;

                }

                //人数が等しくなかったら処理を止める
                if (!RoleManager.playerCheck()) {
                    sender.sendMessage("§c§l設定人数に役職人数が達していないためゲームが開始できません");
                    return;
                }




                gameManager.gameStart(sender);
                GameManager.distributionItem();


            } else if (args[0].equals("role") && sender.hasPermission("minicashwerewolf.command.game.role")) {     //役職人数設定
                if (args.length < 3) {
                    sender.sendMessage(
                            MiniCashWereWolf.getMessage(
                                    Component.text("引数が足りません").color(NamedTextColor.RED)
                            )
                    );
                    return;
                }

                //何をしたいかチェック
                String check = args[1];

                if (check.equals("set")) {
                    if (args.length == 4) {
                        String roleName = args[2];
                        //役職人数を設定
                        String speople = args[3];
                        int people;

                        try {
                            people = Integer.parseInt(speople);

                        } catch (NumberFormatException e) {

                            sender.sendMessage("§c§l" + speople + "§r§cは有効な数字ではありません");

                            return;
                        }


                        for (RoleManager.RoleType roleType : RoleManager.RoleType.values()) {

                            if (roleType.name().equalsIgnoreCase(roleName)) {

                                RoleManager.setRole(roleType, people);

                                sender.sendMessage(MiniCashWereWolf.getMessage(roleType.getJapaneseName() + "の最大人数を " + people + "人に設定しました"));

                                return;
                            }

                        }


                    } else {

                        sender.sendMessage("§c役職の設定人数コマンドの入力方法を確認してください");
                        return;

                    }

                } else if (check.equals("check")) {
                    String roleName = args[2];

                    try {

                        RoleManager.RoleType roleType = RoleManager.RoleType.valueOf(roleName);

                        Role role = RoleManager.getRoles().get(roleType);


                        sender.sendMessage(MiniCashWereWolf.getMessage("========  §b現在の" + roleType.getJapaneseName() + "の情報  §r========"));
                        sender.sendMessage(MiniCashWereWolf.getMessage("            役職有無 : " + role.isActive()));
                        sender.sendMessage(MiniCashWereWolf.getMessage("         役職設定人数 : " + role.getTotal()));
                        sender.sendMessage(MiniCashWereWolf.getMessage("=============================="));


                    } catch (IllegalArgumentException e) {
                        sender.sendMessage(MiniCashWereWolf.getMessage(Component.text("有効な役職名を入力してください").color(NamedTextColor.RED)));
                        return;
                    }


                } else if (check.equals("unset")) {

                    String roleName = args[2];

                    try {

                        RoleManager.RoleType roleType = RoleManager.RoleType.valueOf(roleName);


                        RoleManager.unsetRole(roleType);

                        sender.sendMessage(
                                MiniCashWereWolf.getMessage(Component.text("役職:" + roleType.getJapaneseName() + "を無効化しました").color(NamedTextColor.GREEN)
                                ));


                    } catch (IllegalArgumentException e) {
                        sender.sendMessage(MiniCashWereWolf.getMessage(Component.text("有効な役職名を入力してください").color(NamedTextColor.RED)));
                        return;
                    }


                }


            } else if (args[0].equals("player") && sender.hasPermission("minicashwerewolf.command.game.player")) {      //手動で自分の役職決定するよう(管理者向け)



                String playerName = args[1];

                Player player;

                try {

                        player = Bukkit.getPlayer(playerName);

                        if (player == null) {
                            sender.sendMessage(
                                    MiniCashWereWolf.getMessage(
                                            Component.text(playerName + "という名前のプレイヤーは不明です").color(NamedTextColor.RED)
                                    )
                            );

                            return;
                        }


                } catch (Exception e) {
                    sender.sendMessage(
                            MiniCashWereWolf.getMessage(
                                    Component.text(playerName + "という名前のプレイヤーは不明です").color(NamedTextColor.RED)
                            )
                    );

                    return;

                }

                if (args[2].equals("set")) {

                    if (args.length > 3) {


                        String roleName = args[3];


                        try {

                            RoleManager.RoleType roleType = RoleManager.RoleType.valueOf(roleName);


                            RoleManager.setPlayerRole(player, roleType);

                            sender.sendMessage(
                                    MiniCashWereWolf.getMessage(Component.text(player.getName() + "の役職を " + roleType.getJapaneseName() + "に変更しました").color(NamedTextColor.GREEN)
                                    ));

                            return;


                        } catch (IllegalArgumentException e) {
                            sender.sendMessage(MiniCashWereWolf.getMessage(Component.text("有効な役職名を入力してください").color(NamedTextColor.RED)));
                            return;
                        }

                    }else {

                        sender.sendMessage("§4§l引数が足りません");
                        return;

                    }

                }else if (args[2].equals("check")) {


                    UUID uuid = player.getUniqueId();

                    RoleManager.RoleType roleType = RoleManager.getPlayerRole().getOrDefault(uuid, RoleManager.RoleType.NO);

                    if (roleType == RoleManager.RoleType.NO) {
                        sender.sendMessage(
                                MiniCashWereWolf.getMessage(
                                        Component.text("§6§l現在" + player.getName() + "の役職はありません").color(NamedTextColor.RED)
                                )
                        );

                        return;
                    }

                    sender.sendMessage(
                            MiniCashWereWolf.getMessage(
                                    Component.text("§6§l現在" + player.getName() + "の役職は§r" + roleType.getJapaneseName() + "§6です").color(NamedTextColor.AQUA)
                            )
                    );

                    return;


                }


            } else if (args[0].equals("give") && sender.hasPermission("minicashwerewolf.command.game.give")) {

                if (sender instanceof Player player) {

                    if (args.length < 2) {
                        plugin.help(player);
                        return;
                    }
                    String itemNam = args[1];


                    ItemStack item = GameItem.createItem(itemNam, 1);

                    if (item == null) {
                        player.sendMessage(
                                MiniCashWereWolf.getMessage(
                                        Component.text("アイテム名が不明です").color(NamedTextColor.RED)
                                )
                        );

                        return;
                    }

                    player.getInventory().addItem(item);

                    player.sendMessage(
                            MiniCashWereWolf.getMessage(
                                    Component.text(
                                            itemNam + "を与えました"
                                    )
                            )
                    );


                } else {
                    sender.sendMessage(
                            Component.text("このコマンドはプレイヤーのみ実行可能です").color(NamedTextColor.RED)
                    );
                }

                return;

            } else if (args[0].equals("stop") && sender.hasPermission("minicashwerewolf.command.game.stop")) {

                gameManager.commandGameStop(sender);

                return;

            } else if (args[0].equals("villager") && sender.hasPermission("minicashwerewolf.command.game.villagerspawn")) {

                if (commandSourceStack.getExecutor() instanceof Player player) {

                    Location location = player.getLocation();

                    Villager.villagerSpawn(player, location);


                } else {
                    sender.sendMessage(
                            Component.text("このコマンドはプレイヤーのみ実行可能です").color(NamedTextColor.RED)
                    );
                }

                return;

            } else {

                plugin.help(sender);

                return;
            }

        }else {
            plugin.help(commandSourceStack.getSender());
        }

        return;
    }




    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] args) {
        List<String> suggestions = new ArrayList<>();
        CommandSender sender = commandSourceStack.getSender();

        if (args.length == 0) {
            return filterSuggest(getFirstArgs(sender), "");
        }


        if (args.length == 1) {

            return filterSuggest(getFirstArgs(sender), args[0]);

        }

        String sub = args[0].toLowerCase();


        if (args.length == 2) {
            if (sub.equals("role")) {
                suggestions.addAll(List.of("set", "check", "unset"));
            } else if (sub.equals("player")) {

                for (Player player : Bukkit.getOnlinePlayers()) {
                    suggestions.add(player.getName());
                }

            } else if (sub.equals("give")) {

                suggestions.addAll(List.of("wolf", "madman","knight","fortunecheck",
                        "mediumcheck","pcheck","coin","glowing","speed","invisibility","smoke"));

            }
            return filterSuggest(suggestions, args[1]);
        }

        if (args.length == 3) {
            if (sub.equals("role")) {

                for (RoleManager.RoleType roleType : RoleManager.RoleType.values()) {
                    suggestions.add(roleType.name());
                }

            } else if (sub.equals("player")) {

                suggestions.addAll(List.of("set", "check"));

            }
            return filterSuggest(suggestions, args[2]);
        }

        if (args.length == 4) {
            if (sub.equals("role") && args[1].equalsIgnoreCase("set")) {
                suggestions.addAll(List.of("1", "2", "3", "4", "5"));
            } else if (sub.equals("player") && args[1].equalsIgnoreCase("set")) {

                for (RoleManager.RoleType roleType : RoleManager.RoleType.values()) {

                    suggestions.add(roleType.name());
                }
            }
            return filterSuggest(suggestions, args[3]);
        }

        return Collections.emptyList();
    }

    private List<String> getFirstArgs(CommandSender sender) {

        List<String> list = new ArrayList<>();

        if (sender.hasPermission("minicashwerewolf.command.game.help")){
            list.add("help");
        }
        list.add("start");
        if (sender.hasPermission("minicashwerewolf.command.game.role")){
            list.add("role");
        }
        if (sender.hasPermission("minicashwerewolf.command.game.player")){
            list.add("player");
        }
        if (sender.hasPermission("minicashwerewolf.command.game.give")){
            list.add("give");
        }
        if (sender.hasPermission("minicashwerewolf.command.game.stop")) {
            list.add("stop");
        }
        if (sender.hasPermission("minicashwerewolf.command.game.villagerspawn")){
            list.add("villager");
        }

        return list;
    }


    private Collection<String> filterSuggest(Collection<String> choices, String current) {

        String lowerCurrent = current.toLowerCase();
        List<String> result = new ArrayList<>();

        for (String choice : choices) {

            if (choice.toLowerCase().startsWith(lowerCurrent)) {

                result.add(choice);

            }

        }
        return result;
    }

    @Override
    public @Nullable String permission() {
        return "minicashwerewolf.command.game";
    }
}
