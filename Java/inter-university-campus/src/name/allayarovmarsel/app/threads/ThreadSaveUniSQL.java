package name.allayarovmarsel.app.threads;

import name.allayarovmarsel.app.tables.StudentTable;
import name.allayarovmarsel.app.tables.UniTable;
import name.allayarovmarsel.app.entities.UniversityObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThreadSaveUniSQL implements Runnable {
    public Thread t;
    private Connection con;
    private UniTable uniTable;
    private StudentTable studentTable;
    private String tableName;
    private Logger log = LogManager.getLogger();

    public ThreadSaveUniSQL(Connection con, UniTable uniTable, StudentTable studentTable, String tableName){
        log.info("Saving universities to SQL - starting");
        t = new Thread(this, "TSU");
        this.con = con;
        this.uniTable = uniTable;
        this.studentTable = studentTable;
        this.tableName = tableName;
        t.start();
    }

    public void run(){
        try {
            // autocommit is turned off
            con.setAutoCommit(false);

            // clear the table
            PreparedStatement st = con.prepareStatement("DELETE FROM " + tableName);
            st.executeUpdate();
            con.commit();

            // form query for universities
            // add all the universities from unitable
            ArrayList<UniversityObject> fullUniGroup = new ArrayList<>();
            for (UniversityObject x : uniTable.uniList) {
                if (x.getIfShown())
                    fullUniGroup.add(x);
            }
            int rows = studentTable.tModel.getRowCount();

            // add all the universities that are used but not visible in unitable
            for (int i = 0; i < rows; ++i) {
                String uniName = (String) studentTable.tModel.getValueAt(i, 5);

                if (!fullUniGroup.contains(new UniversityObject(uniName)))
                    fullUniGroup.add(uniTable.uniList.get(uniTable.uniList.indexOf(new UniversityObject(uniName))));
            }
            fullUniGroup.remove(0); // delete default university

            // creating  INSERT
            StringBuilder insertUniQuery = new StringBuilder(10000); // ~ 50 * 100 + 1000 * 100
            insertUniQuery
                    .append("INSERT INTO ")
                    .append(tableName)
                    .append(" (shortName, longName)")
                    .append(" VALUES ");
            int size = fullUniGroup.size();
            int i;

            for (i = 0; i < size; ++i) {
                insertUniQuery
                        .append("(\"")
                        .append(String.join("\", \"", fullUniGroup.get(i).getShortName(), fullUniGroup.get(i).getLongName()))
                        .append("\")");

                if (i % 100 == 0 && i > 0) {
                    // send data every 100 records
                    st = con.prepareStatement(insertUniQuery.toString());
                    st.executeUpdate();
                    insertUniQuery.delete(0, insertUniQuery.length());
                    insertUniQuery
                            .append("INSERT INTO ")
                            .append(tableName)
                            .append(" (shortName, longName)")
                            .append(" VALUES ");
                } else {
                    insertUniQuery
                            .append(", ");
                }
            }

            if ((i - 1) % 100 != 0 && i > 0) { // i - 1 cause of last cycle resource
                // in case we need to send more data when number of records is not divide by 100
                insertUniQuery.delete(insertUniQuery.length() - 2, insertUniQuery.length()); // delete comma
                st = con.prepareStatement(insertUniQuery.toString());
                st.executeUpdate();
            }
            con.commit();
            log.info("Saving students to SQL - connection is out");
        }
        catch (SQLException e){
            log.info("Exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
