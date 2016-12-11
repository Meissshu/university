package name.allayarovmarsel.app.entities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

    final public class StudentObject {

        private final String firstName;
        private final String secondName;
        private final String lastName;
        private final int hullNumber;
        private final int roomNumber;
        private final String uni;
        private final int courseNumber;
        private final GregorianCalendar datePayAgreement;
        private final GregorianCalendar datePayActual;
        private final String paymentStatus;

        public StudentObject(String fN, String sN, String lN, int hN, int rN, String u, int cN, Date dPAg, Date dPAc) {
            firstName = fN;
            secondName = sN;
            lastName = lN;
            hullNumber = hN;
            roomNumber = rN;
            uni = u;
            courseNumber = cN;
            datePayAgreement = new GregorianCalendar();
            datePayAgreement.setTime(dPAg);
            datePayActual = new GregorianCalendar();
            datePayActual.setTime(dPAc);
            paymentStatus = formPaidStatus(datePayAgreement, datePayActual);
        }

        public String[] toData() {
            SimpleDateFormat SDF = new SimpleDateFormat("dd.MM.yy");
            return new String[]{
                    firstName,
                    secondName,
                    lastName,
                    Integer.toString(hullNumber),
                    Integer.toString(roomNumber),
                    uni,
                    Integer.toString(courseNumber),
                    SDF.format(datePayAgreement.getTime()),
                    SDF.format(datePayActual.getTime()), paymentStatus
            }; // returns data for row
        }

        public String formPaidStatus(Calendar dpAG, Calendar dPAc){
            String result;
            long time = (dpAG.getTimeInMillis() - dPAc.getTimeInMillis())/(1000*60*60*24); // ms -> days
            if(time >= 30)
                result = "Не оплачено";
            else if(time >= 0)
                result = "Оплачено";
            else
                result = "Просрочено";
            return result;
        }
    }
