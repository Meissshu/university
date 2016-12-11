package name.allayarovmarsel.app.tables;

import name.allayarovmarsel.app.entities.UniversityObject;

import javax.swing.table.TableColumnModel;
import java.util.ArrayList;

final public class UniTable extends AbstractTable{

    private String shortTextBeforeEdit;
    private String longTextBeforeEdit;
    public ArrayList<UniversityObject> uniList = new ArrayList<>(); // list of all universities

    public UniTable()
    {
        String[] columns = {"Сокращенное наименование вуза", "Полное наименование вуза"};
        initTableModel(columns);

        // размеры
        TableColumnModel TCM = AbstractJTable.getColumnModel();
        TCM.getColumn(0).setPreferredWidth(10); // Сокращенное
        TCM.getColumn(1).setPreferredWidth(300); // Полное
        // it makes auto resize as i wanted

        uniList.add(new UniversityObject("<пусто>", "<пусто>", true)); // default university
    }


   public boolean containsUniList(String object){
        for(UniversityObject test : uniList) {
            if(test.getShortName().equalsIgnoreCase(object))
                return true;
        }
        return false;
    }

    public void setStringSBeforeEdit(String shortObj, String longObj){
        shortTextBeforeEdit = shortObj;
        longTextBeforeEdit = longObj;
    }

    public String getShortTextBeforeEdit(){ return shortTextBeforeEdit;}

    public String getLongTextBeforeEdit() { return  longTextBeforeEdit;}
}
