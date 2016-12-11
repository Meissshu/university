package name.allayarovmarsel.app;

import name.allayarovmarsel.app.gui.StudentsMain;
import org.apache.logging.log4j.*;
import java.io.*;
import java.util.GregorianCalendar;

public class MainPoint {

    public static void main(String[] args){
        new MainPoint().run();
    }

    void run(){ // add info about new session
       try(BufferedWriter out = new BufferedWriter(new FileWriter(new File("src/name/allayarovmarsel/app/resources/logs/logInfo.log"), true))){
            out.write("\n=======================================================\n" + new GregorianCalendar().getTime().toString() + "  - NEW SESSION\n");
        }
        catch (IOException e){
            Logger log = LogManager.getLogger();
            log.info("Exception: " + e.toString());
        }
        StudentsMain app = new StudentsMain();
        app.show();
    }
}