package name.allayarovmarsel.app.dialogs;

import name.allayarovmarsel.app.DuplicateInUniList;
import name.allayarovmarsel.app.tables.StudentTable;
import name.allayarovmarsel.app.tables.UniTable;
import name.allayarovmarsel.app.entities.UniversityObject;
import org.apache.logging.log4j.LogManager;

import javax.swing.*;
import java.awt.*;

public class EditUniDialog extends AddUniDialog {

    private StudentTable sT;

    public EditUniDialog(JFrame parent, String tittle, Dialog.ModalityType modal, UniTable uniTable, StudentTable studentTable)
    {
        super(parent, tittle, modal, uniTable);
        uT = uniTable;
        sT = studentTable;
        int selectedRow = uT.AbstractJTable.getSelectedRow();
        selectedRow = uT.AbstractJTable.convertRowIndexToModel(selectedRow);

        // fill textfields with data
        shortUniName.setText(uT.tModel.getValueAt(selectedRow, 0).toString());
        longUniName.setText(uT.tModel.getValueAt(selectedRow, 1).toString());
        uT.setStringSBeforeEdit(shortUniName.getText(), longUniName.getText());
        uT.uniList.remove(new UniversityObject(shortUniName.getText()));
    }

    @Override
    protected void buttonsSetup(JFrame parent)
    {
        cancel.addActionListener(e -> {
            uT.uniList.add(new UniversityObject(uT.getShortTextBeforeEdit(), uT.getLongTextBeforeEdit(), true));
            dispose();
        });
        accept.addActionListener(e -> {
            try {
                if (shortUniName.getText().equals("") || longUniName.getText().equals("")) {
                    throw new NullPointerException();
                }

                if(uT.containsUniList(shortUniName.getText())) {
                    if(uT.uniList.get(uT.uniList.indexOf(new UniversityObject(shortUniName.getText()))).getIfShown()) // seek for duplicates
                        throw new DuplicateInUniList();
                }
                uT.uniList.add(new UniversityObject(shortUniName.getText(), longUniName.getText(), true));

                int selectedRow = uT.AbstractJTable.getSelectedRow();
                selectedRow =  uT.AbstractJTable.convertRowIndexToModel(selectedRow);

                uT.tModel.removeRow(selectedRow); // remove row and add new one instead of set methods
                uT.tModel.insertRow(selectedRow, new Object[]{shortUniName.getText(), longUniName.getText()});

                // update information in student table
                updateStudentTable(uT.getShortTextBeforeEdit(), shortUniName.getText());
                dispose();
            }
            catch(NullPointerException npex)
            {
                LogManager.getLogger().info("User didn't fill all the fields");
                JOptionPane.showMessageDialog(parent, "Все поля обязательны для заполнения!", "Error", JOptionPane.ERROR_MESSAGE);
            }
            catch(DuplicateInUniList diul)
            {
                LogManager.getLogger().info("User tried to create a duplicate");
                JOptionPane.showMessageDialog(parent, "Такое сокращенное имя уже существует!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void updateStudentTable(String oldUni, String newUni){
        for(int i = 0; i < sT.tModel.getRowCount(); ++i){
            if(sT.tModel.getValueAt(i, 5).equals(oldUni)){
                sT.tModel.setValueAt(newUni, i, 5);
            }
        }
    }
}
