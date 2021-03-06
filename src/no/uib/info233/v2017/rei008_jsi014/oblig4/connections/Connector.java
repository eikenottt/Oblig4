package no.uib.info233.v2017.rei008_jsi014.oblig4.connections;

import no.uib.info233.v2017.rei008_jsi014.oblig4.Debugger;

import java.sql.Connection;
import java.sql.DriverManager;


public final class Connector {

    private static boolean hasConnection;

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
            DriverManager.setLoginTimeout(1);
            Connection conn = DriverManager.getConnection(url, usr, pwr);
            hasConnection = true;

            return conn;
        }catch (NullPointerException n){
            Debugger.printException(n.getMessage());
            Debugger.printError("Can't connect to the server");

        }catch (Exception e) {
            //System.out.println(e);
            hasConnection = false;
            Debugger.print("EXCEPTION: " + e);
        }
        return null;
    }

    public static boolean hasConnection() {
        return hasConnection;
    }


}
