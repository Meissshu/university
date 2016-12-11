package name.allayarovmarsel.app.dialogs;

import name.allayarovmarsel.app.entities.StudentObject;
import name.allayarovmarsel.app.tables.StudentTable;
import name.allayarovmarsel.app.tables.UniTable;
import name.allayarovmarsel.app.entities.UniversityObject;
import org.apache.logging.log4j.LogManager;
import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditStudentDialog extends AddStudentDialog {

    public EditStudentDialog(JFrame parent, String tittle, Dialog.ModalityType modal, StudentTable studentTable, UniTable uniTable){
        super(parent, tittle, modal, studentTable, uniTable);
        sT = studentTable;
        int selectedRow = sT.AbstractJTable.getSelectedRow();
        selectedRow = sT.AbstractJTable.convertRowIndexToModel(selectedRow);

        // fill text fields with data
        firstName.setText(sT.tModel.getValueAt(selectedRow, 0).toString());
        secondName.setText(sT.tModel.getValueAt(selectedRow, 1).toString());
        lastName.setText(sT.tModel.getValueAt(selectedRow, 2).toString());
        hull.setValue(Integer.parseInt(sT.tModel.getValueAt(selectedRow, 3).toString()));
        room.setValue(Integer.parseInt(sT.tModel.getValueAt(selectedRow, 4).toString()));
        String uniText = sT.tModel.getValueAt(selectedRow, 5).toString();

        // if uni table contains university then lets pick it in combobox
        // else create this element and add to combobox
        if(uniTable.containsUniList(uniText) && uniTable.uniList.get(uniTable.uniList.indexOf(new UniversityObject(uniText))).getIfShown()) {
            uni.setSelectedItem(uniText);
        }
        else{
            uniBoxModel.addElement(uniText);
            uni.setSelectedItem(uniText);
        }

        course.setValue(Integer.parseInt(sT.tModel.getValueAt(selectedRow, 6).toString()));

        try {
            datePayAgreement.setValue(new SimpleDateFormat("dd.MM.yy").parse(sT.tModel.getValueAt(selectedRow, 7).toString()));
        }
        catch (ParseException e) {
            LogManager.getLogger().info("Wrong data format found! Arrangement pay time");
            JOptionPane.showMessageDialog(parent, "Неверный формат даты срока оплаты! Дата изменена на текущее время", "Error", JOptionPane.ERROR_MESSAGE);
            datePayAgreement.setValue(new Date());
        }

        try {
            datePayActual.setValue(new SimpleDateFormat("dd.MM.yy").parse(sT.tModel.getValueAt(selectedRow, 8).toString()));
        }
        catch (ParseException e) {
            LogManager.getLogger().info("Wrong data format found! Actual pay time");
            JOptionPane.showMessageDialog(parent, "Неверный формат даты времени оплаты! Дата изменена на текущее время", "Error", JOptionPane.ERROR_MESSAGE);
            datePayActual.setValue(new Date());
        }
    }

    @Override
    protected void buttonsSetup(JFrame parent)
    {
        cancel.addActionListener(e -> dispose());
        accept.addActionListener(e -> {
            try {
                if (firstName.getText().equals("") || secondName.getText().equals("") || lastName.getText().equals(""))
                    throw new NullPointerException();

                int selectedRow = sT.AbstractJTable.getSelectedRow();
                selectedRow = sT.AbstractJTable.convertRowIndexToModel(selectedRow);

                StudentObject student = new StudentObject(
                        firstName.getText(),
                        secondName.getText(),
                        lastName.getText(),
                        ((Number) hull.getValue()).intValue(),
                        ((Number) room.getValue()).intValue(),
                        uniBoxModel.getSelectedItem().toString(),
                        ((Number) course.getValue()).intValue(),
                        ((Date) datePayAgreement.getValue()),
                        ((Date) datePayActual.getValue())); // preparing a record for table

                sT.tModel.removeRow(selectedRow); // delete row and add new instead of set method
                sT.tModel.insertRow(selectedRow, student.toData());

                dispose();
            }
            catch(NullPointerException npex)
            {
                LogManager.getLogger().info("User didn't fill all the fields");
                JOptionPane.showMessageDialog(parent, "Все поля обязательны для заполнения!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}

