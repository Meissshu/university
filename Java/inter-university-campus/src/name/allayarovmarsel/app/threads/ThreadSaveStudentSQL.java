package name.allayarovmarsel.app.threads;

import name.allayarovmarsel.app.tables.StudentTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThreadSaveStudentSQL implements Runnable{
    public Thread t;
    private Connection con;
    private StudentTable studentTable;
    private String tableName;
    private Logger log = LogManager.getLogger();

    public ThreadSaveStudentSQL(Connection con, StudentTable studentTable, String tableName){
        log.info("Saving students to SQL - starting");
        t = new Thread(this, "TSS");
        this.con = con;
        this.studentTable = studentTable;
        this.tableName = tableName;
        t.start();
    }

    public void run(){
        try {
            // turn off autocommit
            con.setAutoCommit(false);
            PreparedStatement st = con.prepareStatement("DELETE FROM " + tableName);
            st.executeUpdate();
            con.commit();
            // creating INSERT for students
            // form: idStudent, firstName, secondName, lastName, hull, room, uni, course, payTimeArr, payTimeAct, paymentStatus
            StringBuilder insertStudentQuery = new StringBuilder(17000); // ~ (default(7) + ~40 + ~25 + ~30 + 3 + 4 + 50 + 1 + 8 + 8 + 15) * 100 ~17k
            insertStudentQuery
                    .append("INSERT INTO ")
                    .append(tableName)
                    .append(" (idStudent, firstName, secondName, lastName, hull, room, uni, course, payTimeArr, payTimeAct, paymentStatus)")
                    .append(" VALUES ");
            int size = studentTable.tModel.getRowCount();
            int columns = studentTable.tModel.getColumnCount();
            int i;

            for (i = 0; i < size; ++i) {
                insertStudentQuery
                        .append("(default");
                for(int j = 0; j < columns; ++j)
                {
                    insertStudentQuery
                            .append(", \"")
                            .append(studentTable.tModel.getValueAt(i, j))
                            .append("\"");
                }
                insertStudentQuery.append(")");

                if (i % 100 == 0 && i > 0) {
                    // send data every 100 records
                    st = con.prepareStatement(insertStudentQuery.toString());
                    st.executeUpdate();
                    insertStudentQuery.delete(0, insertStudentQuery.length());
                    insertStudentQuery
                            .append("INSERT INTO ")
                            .append(tableName)
                            .append(" (idStudent, firstName, secondName, lastName, hull, room, uni, course, payTimeArr, payTimeAct, paymentStatus)")
                            .append(" VALUES ");
                } else {
                    insertStudentQuery
                            .append(", ");
                }
            }

            if ((i - 1) % 100 != 0 && i > 0) { // i - 1 cause of last cycle resource
                // in case we need to send more data when number of records is not divide by 100
                insertStudentQuery.delete(insertStudentQuery.length() - 2, insertStudentQuery.length()); // delete comma
                st = con.prepareStatement(insertStudentQuery.toString());
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
