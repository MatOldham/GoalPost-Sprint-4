package utsa.cs3773goalpost;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConnectionClass {
    private static final ConnectionClass instance = new ConnectionClass();
    protected static String db = "goalpost";
    protected static String ip = "database-1.clekwicm03zh.us-east-2.rds.amazonaws.com";
    protected static String port = "3306";
    protected static String username = "admin";
    protected static String password = "goalpost4";
    protected Connection dbConn;
    protected String str;
    private ConnectionClass(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() ->{
            try {
                dbConn = this.CONN();
                if(dbConn == null){
                    str = "Could not connect to MySQL server";
                }else {
                    str = "Connected to MySQL server";
                }
            } catch(Exception e){
                throw new RuntimeException(e);
            }
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e){
            throw new RuntimeException(e);
        }
        Log.d("dbConnection", ""+str);

    }

    private Connection CONN(){
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String connectionString = "jdbc:mysql://"+ip+":"+port+"/"+db;
            conn = DriverManager.getConnection(connectionString, username, password);
        }catch (Exception e){
            Log.e("ERRO", Objects.requireNonNull(e.getMessage()));
        }
        return conn;

    }
    public static ConnectionClass getInstance() {
        return instance;
    }

    public Connection getDbConn(){
        return dbConn;
    }
}
