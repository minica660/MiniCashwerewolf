package MiniCash.miniCashwerewolf.Event;

import MiniCash.miniCashwerewolf.GameManager;
import MiniCash.miniCashwerewolf.MiniCashWereWolf;
import MiniCash.miniCashwerewolf.RegisterManager;
import MiniCash.miniCashwerewolf.RoleManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class GameJoinAndLeaveEvent implements Listener {

    private final Component TITLE = Component.text("[MWereWolf]", NamedTextColor.GOLD);

    private final String JOIN_MESSAGE = "参加登録";
    private final String LEAVE_MESSAGE = "登録を解除";
    private final String SPECTATOR_JOIN_MESSAGE = "観戦者登録";


    @EventHandler
    public void SignChange(SignChangeEvent event) {

        if (event.getBlock().getState() instanceof Sign sign) {

            String line1 = PlainTextComponentSerializer.plainText().serialize(event.line(0));
            String line2 = PlainTextComponentSerializer.plainText().serialize(event.line(1));

            if (line1.equals("mwgame")) {

                if (line2.equals("join")) {

                    event.line(0, TITLE);
                    event.line(1, Component.text(JOIN_MESSAGE , NamedTextColor.GREEN));


                    sign.setWaxed(true);
                    sign.update();


                } else if (line2.equals("spectator")) {
                    //観戦者用

                    event.line(0, TITLE);
                    event.line(1, Component.text(SPECTATOR_JOIN_MESSAGE , NamedTextColor.GREEN));

                    sign.setWaxed(true);
                    sign.update();

                } else if (line2.equals("cancel")) {
                    //登録キャンセル
                    event.line(0, TITLE);
                    event.line(1, Component.text(LEAVE_MESSAGE , NamedTextColor.GREEN));

                    sign.setWaxed(true);
                    sign.update();

                }

            }


        }


    }


    @EventHandler
    public void signClickEvent(PlayerInteractEvent event) {

        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {

            Block block = event.getClickedBlock();

            if (block != null && block.getState() instanceof Sign sign) {

                List<Component> allLines = sign.getSide(Side.FRONT).lines();

                String line2 = PlainTextComponentSerializer.plainText().serialize(allLines.get(1));


                if(!allLines.get(0).equals(TITLE)) {
                    return;
                }

                if (line2.equals(JOIN_MESSAGE)) {

                    //すでに登録していないかのチェック
                    if (RegisterManager.isPlayerRegistered(player.getUniqueId())) {
                        player.sendMessage(MiniCashWereWolf.getMessage(
                                Component.text("すでに人狼ゲームに登録しています!").color(NamedTextColor.RED)
                        ));
                        return;

                    } else {

                        //ここからゲームに参加準備をした後の処理記述！

                        //参加したいプレイヤーをplayersListに入れる
                        player.sendMessage(MiniCashWereWolf.getMessage(
                                Component.text("人狼ゲームに登録しました").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD)
                        ));

                        RegisterManager.registerPlayer(player.getUniqueId());

                    }

                } else if (line2.equals(SPECTATOR_JOIN_MESSAGE)) {
                    //先に普通の登録をしていないかのチェック
                    if (RegisterManager.isPlayerRegistered(player.getUniqueId()) || RoleManager.playerHasGameRole(player.getUniqueId())) {

                        player.sendMessage(MiniCashWereWolf.getMessage(
                                Component.text("すでに人狼ゲームに登録しています!").color(NamedTextColor.RED)
                        ));

                    }else {

                        //プレイヤーを観戦者として登録
                        RoleManager.playerRoleSet(player, RoleManager.RoleType.SPECTATOR);
                        player.sendMessage(MiniCashWereWolf.getMessage(
                                Component.text("人狼ゲームに").color(NamedTextColor.GREEN)
                                        .append(Component.text("観戦者").color(NamedTextColor.GOLD))
                                        .append(Component.text("として登録しました").color(NamedTextColor.GREEN))
                                        .decorate(TextDecoration.BOLD)
                        ));

                    }

                } else if (line2.equals(LEAVE_MESSAGE)) {
                    //登録キャンセル
                    //ゲームに登録又は、役職を設定しているのかをチェック

                    if (RegisterManager.isPlayerRegistered(player.getUniqueId()) || RoleManager.playerHasGameRole(player.getUniqueId())) {

                        RegisterManager.unregisterPlayer(player.getUniqueId());
                        RoleManager.getPlayerRole().remove(player.getUniqueId());

                        player.sendMessage("§b人狼ゲームの登録を解除しました");

                    } else {


                        //まだ登録していなかったら
                        player.sendMessage(MiniCashWereWolf.getMessage(
                                Component.text("まだ人狼ゲームに登録していません！").color(NamedTextColor.RED)
                        ));

                    }

                }

            }


        }
    }


}
