package MiniCash.miniCashwerewolf;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 役職振り分け前の参加登録用
 */
public class RegisterManager {

    // ゲームに参加登録をしているプレイヤー(参加ではなくあくまで登録状態)
    private static final List<UUID> registerPlayers = new ArrayList<>();


    public static void registerPlayer(UUID uuid) {
        registerPlayers.add(uuid);
    }

    public static void unregisterPlayer(UUID uuid) {
        registerPlayers.remove(uuid);
    }

    public static boolean isPlayerRegistered(UUID uuid) {
        return registerPlayers.contains(uuid);
    }

    public static List<UUID> getRegisterPlayers() {
        return registerPlayers;
    }






}
