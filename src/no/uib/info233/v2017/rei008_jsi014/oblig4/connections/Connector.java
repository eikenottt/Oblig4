package no.uib.info233.v2017.rei008_jsi014.oblig4.connections;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by runeeikemo on 24.05.2017.
 */
public final class Connector {

    /**
     * Makes a connection to the database
     * @return Connection to the database
     * @throws Exception if connection is bad
     */
    public static Connection getConnection() throws Exception {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://wildboy.uib.no/oblig4?useSSL=false";
            String usr = "Syuty";
            String pwr = "(+DDq2sSyk(3P)}8";
            DriverManager.setLoginTimeout(5);
            Connection conn = DriverManager.getConnection(url, usr, pwr);


            return conn;
        } catch (Exception e) {
            System.out.println(e); //TODO print with Debugger class
        }
        return null;
    }
}
