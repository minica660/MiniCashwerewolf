package MiniCash.miniCashwerewolf;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import static MiniCash.miniCashwerewolf.Event.ItemTimer.tcheck;
import static MiniCash.miniCashwerewolf.Timer.nowtime;

public class MeetingTimer extends BukkitRunnable {

    //aaaaa
    private final MiniCashwerewolf plugin;

    public MeetingTimer(MiniCashwerewolf plugin){
        this.plugin = plugin;
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
            plugin.noon();

            //次のために夜状態に
            nowtime = false;


            //アイテム再配布
            plugin.giveitem();

            plugin.voteresult();


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
            plugin.vote();

        }



    }
}
