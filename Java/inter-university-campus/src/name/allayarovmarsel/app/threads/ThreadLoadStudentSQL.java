package name.allayarovmarsel.app.threads;

import name.allayarovmarsel.app.tables.StudentTable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ThreadLoadStudentSQL implements Runnable{
    public Thread t;
    private Connection con;
    private StudentTable studentTable;
    private String name;
    private Logger log = LogManager.getLogger();

    public ThreadLoadStudentSQL(Connection con, StudentTable studentTable, String name){
        log.info("Loading students from SQL - starting");
        t = new Thread(this, "TLS");
        this.con = con;
        this.studentTable = studentTable;
        this.name = name;
        t.start();
    }

    public void run(){
        try {
            // result set
            Statement st = con.createStatement();
            String queryDownloadStudentsFromSQL = "select * from " + name;
            ResultSet rs = st.executeQuery(queryDownloadStudentsFromSQL);

            // clear stuff
            int size = studentTable.tModel.getRowCount();
            for(int i = 0; i < size; ++i){
                studentTable.tModel.removeRow(0);
            }

            // actual adding
            while (rs.next()){
                String firstName = rs.getString(2);
                String secondName = rs.getString(3);
                String lastName = rs.getString(4);
                String hull = rs.getString(5);
                String room = rs.getString(6);
                String uni = rs.getString(7);
                String course = rs.getString(8);
                String payTimeArr = rs.getString(9);
                String payTimeAct = rs.getString(10);
                String status = rs.getString(11);
                studentTable.tModel.addRow(new Object[]{firstName, secondName, lastName, hull, room, uni, course, payTimeArr, payTimeAct, status});
            }

            log.info("Loading students from SQL - connection is out");
        }
        catch (SQLException e){
            log.info("Exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
