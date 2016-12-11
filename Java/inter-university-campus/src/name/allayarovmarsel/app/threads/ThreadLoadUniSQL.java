package name.allayarovmarsel.app.threads;

import name.allayarovmarsel.app.entities.UniversityObject;
import name.allayarovmarsel.app.tables.UniTable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ThreadLoadUniSQL implements Runnable {

    public Thread t;
    private Connection con;
    private UniTable uniTable;
    private String name;
    private Logger log = LogManager.getLogger();

    public ThreadLoadUniSQL(Connection con, UniTable uniTable, String name){
        log.info("Loading universities from SQL - starting");
        t = new Thread(this, "TLU");
        this.con = con;
        this.uniTable = uniTable;
        this.name = name;
        t.start();
    }

    public void run(){
        try {
            // result set
            Statement st = con.createStatement();
            String queryDownloadUniFromSQL = "select * from " + name;
            ResultSet rs = st.executeQuery(queryDownloadUniFromSQL);

            // clear uniTable & uniList
            int size = uniTable.tModel.getRowCount();
            for (int i = 0; i < size; ++i)
                uniTable.tModel.removeRow(0);

            size = uniTable.uniList.size();
            for(int i = 1; i < size; ++i)
                uniTable.uniList.remove(1); // clear everything except default uni

            // adding to and uniTable uniList
            while (rs.next()) {
                String shortName = rs.getString(1);
                String longName = rs.getString(2);
                uniTable.uniList.add(new UniversityObject(shortName, longName, true));
                uniTable.tModel.addRow(new Object[]{shortName, longName});
            }

            log.info("Loading universities from SQL - connection is out");
        }
        catch (SQLException e){
            log.info("Exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
