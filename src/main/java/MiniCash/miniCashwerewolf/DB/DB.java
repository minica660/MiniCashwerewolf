package MiniCash.miniCashwerewolf;



import java.sql.*;
import java.util.logging.Level;

import static java.sql.DriverManager.getConnection;

public class DB {
    private final MiniCashwerewolf plugin;

    public DB(MiniCashwerewolf plugin){
        this.plugin = plugin;
    }

    private Connection connect = null;
//    private Statement stmt = null;

    //プラグイン起動時に一度だけ呼び出す
    public void connect() throws SQLException {
        // すでに接続が生きていれば何もしない (3秒以内に応答があるか確認)
        if (connect != null && !connect.isClosed() && connect.isValid(3)) {
            return;
        }
        final String URL = "jdbc:mysql://" + plugin.getConfig().getString("mysql.host") + "/" + plugin.getConfig().getString("mysql.database") + "?useSSL=false&autoReconnect=true&serverTimezone=Asia/Tokyo";
        final String USER = plugin.getConfig().getString("mysql.user");
        final String PASS = plugin.getConfig().getString("mysql.password");

        try{
            //db接続
            connect = getConnection(URL, USER, PASS);
            plugin.getLogger().info("データベースへの接続が完了しました");
//            //ステートメント生成
//            stmt = connect.createStatement();

        }catch(Exception e){

            plugin.getLogger().severe("データベースへの接続に失敗しました: " + e.getMessage());

        }


    }

//    public Connection getconnect() throws SQLException {
//
//
//        return connect;
//    }

    public void closeConnection(){
        try {
            if (connect != null) {
                connect.close();
                plugin.getLogger().info("データベースの停止を完了しました。");
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "データベースの接続を閉じる際にエラーが発生しました", e.getMessage());
        }
    }

    public void setupTable() {
        if (connect == null) {
            return;
        }
        Statement stmt = null;
        try{
            //ステートメント生成
            stmt = connect.createStatement();


            // プレイヤーの行動ログを保存するテーブル
            String sql = "CREATE TABLE IF NOT EXISTS mlog ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "time TIMESTAMP DEFAULT CURRENT_TIMESTAMP," // 現在時刻を自動挿入
                    + "player_name VARCHAR(16) NOT NULL,"
                    + "uuid VARCHAR(36) NOT NULL,"
                    + "content TEXT"
                    + ");";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            plugin.getLogger().severe("データベース操作中にエラーが発生しました: " + e.getMessage());
        }finally{
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("データベース ステートメントを閉じる際にエラーが発生しました: " + e.getMessage());
            }
        }
    }


    public void addlog(String playername, String uuid, String message){

        String sql = "INSERT INTO mlog (player_name, uuid, content) VALUES (?, ?, ?);";

        // 非同期で実行
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                // 接続状態のチェックと復旧
                synchronized (this) {
                    if (connect == null || connect.isClosed() || !connect.isValid(3)) {
                        connect();
                    }
                }

                // try(...) を使うことで、このブロックを抜けた瞬間に自動で Statement が close される
                try (PreparedStatement pstmt = connect.prepareStatement(sql)) {
                    pstmt.setString(1, playername);
                    pstmt.setString(2, uuid);
                    pstmt.setString(3, message);
                    pstmt.executeUpdate();
                }
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "データの追加に失敗しました: " + e.getMessage());
            }
        });

    }




}
