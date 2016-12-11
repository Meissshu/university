import name.allayarovmarsel.app.entities.StudentObject;
import name.allayarovmarsel.app.tables.UniTable;
import name.allayarovmarsel.app.entities.UniversityObject;
import org.junit.*;

import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

public class TestMethods {

    @Test
    public void testIfShown(){
        UniversityObject test = new UniversityObject("1", "2", true);
        assertTrue(!test.getIfShown());

        test.setIfShown(false);
        assertFalse(test.getIfShown());
    }

    @Test
    public void testEquals(){
        UniversityObject test = new UniversityObject("1", "2", true);
        assertEquals(test, new UniversityObject("1", "2", false));
        assertEquals(test, new UniversityObject("1", "2", true));
        assertEquals(test, new UniversityObject("1", "1", false));
        assertEquals(test, test);
        assertNotEquals(test, new UniversityObject("3", "2", false));
    }

    @Test
    public void testUniTableContains(){
        UniTable test = new UniTable();
        assertTrue(test.containsUniList("<пусто>"));
        assertFalse(test.containsUniList("some university"));
    }

    @Test
    public void testFormPaidStatus(){

        StudentObject test = new StudentObject("1", "2", "3", 4, 5, "6", 7, new Date(), new Date());
        // agreement = actual;
        assertEquals("Оплачено", test.formPaidStatus(new GregorianCalendar(0, 0, 0), new GregorianCalendar(0, 0, 0)));

        // agreement - actual >= 30 days
        assertEquals("Не оплачено", test.formPaidStatus(new GregorianCalendar(0, 0, 30), new GregorianCalendar(0, 0, 0)));

        // agreement - actual < 0
        assertEquals("Просрочено", test.formPaidStatus(new GregorianCalendar(0, 0, 0), new GregorianCalendar(1, 0, 0)));
    }
}
