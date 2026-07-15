package MiniCash.miniCashwerewolf.timer;

import MiniCash.miniCashwerewolf.MiniCashWereWolf;
import MiniCash.miniCashwerewolf.GameManager;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

import static MiniCash.miniCashwerewolf.Event.ItemTimer.tcheck;


public class Timer extends BukkitRunnable {
    //この場合300秒ごとに昼と夜が入れ替わる
    private int daytime = 60;
    //何日目か
    public static int day = 0;
    //昼ならtrue、夜ならfalse
    public static boolean nowtime;
    //何回昼、夜が切り替わったかの確認用
    public static int mcheck = 0;

    int cointimer = 40;
    int SPAWNPOINTCOUNT = 5;  //コインをスポーンさせる箇所

    public static boolean tstop = false;        //stop用



    //aaaaa
    private final MiniCashWereWolf plugin;
    private final GameManager gameManager;

    public Timer(MiniCashWereWolf plugin, GameManager wolfmain){
        this.plugin = plugin;
        this.gameManager = wolfmain;
    }

    @Override
    public void run() {

        //check
        if (tstop){
            cancel(); //停止
            tstop= false;

            //何日目かをリセット
            day = 0;

            //騎士の守りも停止させる
            tcheck = true;
        }

        //時間計測が0じゃなかったら値を減らし、0以下だったら停止
        if (daytime >= 0) {


            //時間計測変数内の数値を１減らす（1秒）
            daytime = daytime - 1;


            Bukkit.getOnlinePlayers().forEach(player -> {
                String actionbarm = "残り時間：" + daytime;

                //player.sendTitle("残り時間：", actionbar, 1, 1, 1);
                player.sendActionBar(actionbarm);
            });
        }


        if (cointimer == 0) {
            //コインスポーン
            for (int i = 1; i <= SPAWNPOINTCOUNT; i++) {
                //String cx = "cspawn" + i + "X";


                Random random = new Random();
                int type = random.nextInt(2) + 1; // 1 か 2 を生成

                World world = Bukkit.getWorld(plugin.startpointworld);
                int csX = plugin.startpointX + (random.nextInt(plugin.range * 2 + 1) - plugin.range);
                int csZ = plugin.meetingpointZ + (random.nextInt(plugin.range * 2 + 1) - plugin.range);

/*                if (type == 1){
                    csX = -csX;
                }else {
                    csZ = -csZ;
                }
*/
                int csY = world.getHighestBlockYAt(csX, csZ) + 1;

                Location scl = new Location(world, csX, csY, csZ);

                //ItemStack item = new ItemStack(Material.GOLD_INGOT);
                //world.dropItem(scl, item);

                NamespacedKey namekey = new NamespacedKey(plugin,"wolfitem");


                Skeleton skelton = (Skeleton) world.spawn(scl, Skeleton.class);
                skelton.getEquipment().clear();
                skelton.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(3.0);
                skelton.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(1.0);
                skelton.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
                skelton.getPersistentDataContainer().set(namekey, PersistentDataType.STRING,"spawncoinskelton");

            }



            //タイマーを再設定
            cointimer = 40;


        }

        cointimer--;


        //会議関連メソッド呼び出し（1日の終わり　昼と夜）
        if (daytime == 0 && nowtime && mcheck == 0) {    //タイマーが終了したとき、昼で１日目ならここの処理を実行
            //夜に変更
            gameManager.noon();

            mcheck++;


            //次のために夜状態に
            nowtime = false;

            //開発時の確認用
            Bukkit.broadcastMessage("１日目の昼が終了");


            //一度タイマー停止
            cancel();

            //騎士守り
            tcheck = true;


        }else if (daytime == 0 && nowtime && mcheck >= 1){  //タイマーが終了したとき、昼で２日目以降なら会議に移動させるここの処理を実行
            gameManager.meeting(); //meeting呼び出し

            mcheck++;


            //開発時の確認用
            Bukkit.broadcastMessage("会議へ移行");


            //一度タイマー停止
            cancel();

            //一度スケルトンをキル


        }else if (daytime == 0 && !nowtime && mcheck >= 1){
            //夜ならnoonメソッド呼び出し
            gameManager.day();

            mcheck++;
            
            //次のために昼状態に
            nowtime = true;

            //開発時の確認用
            Bukkit.broadcastMessage("昼へ移行");

            //一度タイマー停止
            cancel();

            //日付変更
            day++;

            Bukkit.broadcastMessage("§6" + day + "§r日目になりました");

            //騎士守り
            tcheck = true;


        }



        }




    }




