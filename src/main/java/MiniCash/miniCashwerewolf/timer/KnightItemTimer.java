package MiniCash.miniCashwerewolf.timer;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static MiniCash.miniCashwerewolf.Event.Item.knightProtectedPlayers;

public class KnightItemTimer extends BukkitRunnable {

    //particle
    double angle = 0;
    public static boolean check = false;
     static Player target = null;
     public static boolean tcheck = false; //ターン切り替わりチェック

    public static void setTarget(Player target) {

        KnightItemTimer.target = target;

    }

    @Override
    public void run() {


        //パーティクル！！
        if (target != null) {

            Location center = target.getLocation().add(0, 1, 0);

            double x = Math.cos(angle);
            double z = Math.sin(angle);

            Location playerLocation = center.clone().add(x, 0, z);

            // 対象プレイヤー「だけ」に見せる
            target.getWorld().spawnParticle(
                    Particle.END_ROD,
                    playerLocation,
                    1,
                    0, 0, 0, 0
            );

            angle += Math.PI / 8;



        }


        //ターンが変わったら守りも終了（例：昼から夜になったら..）
        if (tcheck) {
            cancel();
            angle = 0;
            target = null;
            tcheck = false;
            knightProtectedPlayers.clear();
        }


    }
}
