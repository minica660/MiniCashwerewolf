package MiniCash.miniCashwerewolf.timer;

import MiniCash.miniCashwerewolf.MiniCashWereWolf;
import MiniCash.miniCashwerewolf.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import static MiniCash.miniCashwerewolf.Event.ItemTimer.tcheck;
import static MiniCash.miniCashwerewolf.timer.Timer.nowtime;

public class MeetingTimer extends BukkitRunnable {

    //aaaaa
    private final MiniCashWereWolf plugin;
    private final GameManager wolfmain;

    public MeetingTimer(MiniCashWereWolf plugin, GameManager wolfmain) {
        this.plugin = plugin;
        this.wolfmain = wolfmain;
    }

    int timer = 40;


    public static boolean mstop = false;        //stop用


    @Override
    public void run() {

        //停止
        if (mstop){
            cancel();
            mstop = false;
        }

        if (timer > 0) {
            timer--;


            Bukkit.getOnlinePlayers().forEach(player -> {
                String actionbarm = "残り時間：" + timer;

                //player.sendTitle("残り時間：", actionbar, 1, 1, 1);
                player.sendActionBar(actionbarm);
            });

        }else{
            //タイマーの残り時間が0秒になったら夜に移行

            //夜に移動
            wolfmain.noon();

            //次のために夜状態に
            nowtime = false;


            //アイテム再配布
            wolfmain.distributionItem();

            wolfmain.voteResult();


            //キャンセル
            cancel();

            //騎士守り
            tcheck = true;
        }


        if (timer == 30){
            Bukkit.broadcastMessage("§5まもなく投票GUIが開かれます");
            Bukkit.broadcastMessage("操作に注意してください");
        }

        if (timer == 23){
            Bukkit.broadcastMessage("       3");
        }

        if (timer == 22){
            Bukkit.broadcastMessage("       2");
        }

        if (timer == 21){
            Bukkit.broadcastMessage("       1");
        }

        if (timer == 20){
            //投票GUI
            wolfmain.vote();

        }



    }
}
