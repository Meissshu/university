package name.allayarovmarsel.app.tables;

import javax.swing.table.TableColumnModel;

final public class StudentTable extends AbstractTable{

    public StudentTable()
    {
        String[] columns = {"Фамилия", "Имя", "Отчество", "Корпус", "Комната", "Университет", "Курс", "Срок оплаты", "Время оплаты", "Статус"};
        initTableModel(columns);

        // размеры
        TableColumnModel TCM = AbstractJTable.getColumnModel();
        TCM.getColumn(0).setPreferredWidth(90); // Фамилия
        TCM.getColumn(1).setPreferredWidth(90); // Имя
        TCM.getColumn(2).setPreferredWidth(90); // Отчество
        TCM.getColumn(3).setPreferredWidth(50); // Корпус
        TCM.getColumn(4).setPreferredWidth(55); // Комната
        TCM.getColumn(5).setPreferredWidth(90); // Универ
        TCM.getColumn(6).setPreferredWidth(40); // Курс
        TCM.getColumn(7).setPreferredWidth(85); // Срок оплаты
        TCM.getColumn(8).setPreferredWidth(85); // Последняя оплата
        //TCM.getColumn(9).setPreferredWidth(70); // Статус - автоматически
    }
}
