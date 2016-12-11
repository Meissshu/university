package name.allayarovmarsel.app.dialogs;

import name.allayarovmarsel.app.entities.StudentObject;
import name.allayarovmarsel.app.tables.StudentTable;
import name.allayarovmarsel.app.tables.UniTable;
import org.apache.logging.log4j.LogManager;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;

public class AddStudentDialog extends AbstractPropertiesDialog {

    protected JTextField firstName;
    protected JTextField secondName;
    protected JTextField lastName;
    protected JFormattedTextField hull;
    protected JFormattedTextField room;
    protected DefaultComboBoxModel<String> uniBoxModel = new DefaultComboBoxModel<>();
    protected JComboBox<String> uni = new JComboBox<>(uniBoxModel);
    protected JFormattedTextField course;
    protected JFormattedTextField datePayAgreement;
    protected JFormattedTextField datePayActual;

    protected StudentTable sT;

    public AddStudentDialog(JFrame parent, String tittle, Dialog.ModalityType modal, StudentTable studentTable, UniTable uniTableForBox)
    {
        super(parent, tittle, modal);
        //  === КНОПКИ, ПАНЕЛИ, и т.д. СВОЙСТВ ===
        sT = studentTable;
        accept = new JButton("Принять");
        cancel = new JButton("Отмена");
        firstName = new JTextField();
        secondName = new JTextField();
        lastName  = new JTextField();
        hull = new JFormattedTextField(NumberFormat.getIntegerInstance()); // only integers
        room = new JFormattedTextField(NumberFormat.getIntegerInstance());
        course = new JFormattedTextField(NumberFormat.getIntegerInstance());
        //ifPaid = new JComboBox<>(new Boolean[]{true, false});
        datePayAgreement = new JFormattedTextField(DateFormat.getDateInstance(DateFormat.SHORT)); // only date
        datePayActual = new JFormattedTextField(DateFormat.getDateInstance(DateFormat.SHORT));

        int size = uniTableForBox.uniList.size(); // forms combobox inside of dialog from uniList
        for(int i = 0; i < size; ++i) {
            if(uniTableForBox.uniList.get(i).getIfShown())
                uniBoxModel.addElement(uniTableForBox.uniList.get(i).getShortName());
        }

        Component[] textFields = new Component[]{firstName, secondName,lastName, hull, room, uni, course, datePayAgreement, datePayActual};
        Labels = new JLabel[]{new JLabel("Фамилия"), new JLabel("Имя"), new JLabel("Отчество"), new JLabel("Корпус"), new JLabel("Комната"), new JLabel("Университет"), new JLabel("Курс"), new JLabel("Срок оплаты"), new JLabel("Время оплаты")};

        //  === ДОБАВЛЕНИЕ И РАЗМЕЩЕНИЕ ===

        // добавление
        Container contentPane = getContentPane();
        SpringLayout layout = new SpringLayout();
        contentPane.setLayout(layout);

        for(JLabel x : Labels)
            contentPane.add(x);

        for(Component x : textFields) {
            x.setPreferredSize(new Dimension(150, 24));
            contentPane.add(x);
        }

        contentPane.add(accept);
        contentPane.add(cancel);

        // размещение

        layout.putConstraint(SpringLayout.NORTH, Labels[0], 5, SpringLayout.NORTH, contentPane);
        layout.putConstraint(SpringLayout.WEST, Labels[0], 5, SpringLayout.WEST, contentPane);
        layout.putConstraint(SpringLayout.NORTH, textFields[0], 5, SpringLayout.NORTH, contentPane);
        layout.putConstraint(SpringLayout.WEST, textFields[0], 95, SpringLayout.WEST, contentPane);

        for(int i = 1; i < Labels.length; ++i)
        {
            layout.putConstraint(SpringLayout.WEST, Labels[i], 5, SpringLayout.WEST, contentPane);
            layout.putConstraint(SpringLayout.NORTH, Labels[i], 29, SpringLayout.NORTH, Labels[i-1]); // 24 (height of text) + 5
            layout.putConstraint(SpringLayout.NORTH, textFields[i], 29, SpringLayout.NORTH, textFields[i-1]);
            layout.putConstraint(SpringLayout.WEST, textFields[i], 95, SpringLayout.WEST, contentPane);
        }

        layout.putConstraint(SpringLayout.WEST, accept, 30, SpringLayout.WEST, contentPane);
        layout.putConstraint(SpringLayout.SOUTH, accept, -10, SpringLayout.SOUTH, contentPane);
        layout.putConstraint(SpringLayout.SOUTH, cancel, -10, SpringLayout.SOUTH, contentPane);
        layout.putConstraint(SpringLayout.EAST, cancel, -30, SpringLayout.EAST, contentPane);

        // === ЗАВЕРШАЮЩИЕ МЕТОДЫ ===
        // вызов метода настройки окна
        mainDialogSetup(parent);

        // вызодв метода добавления слушателей к кнопкам

        buttonsSetup(parent);
    }

    @Override
    protected void mainDialogSetup(JFrame parent){
        setVisible(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(260, 350);
        setLocation(parent.getX() + (parent.getWidth() - getWidth())/2 , parent.getY() + (parent.getHeight() - getHeight())/2);
        setResizable(false);
    }

    @Override
    protected void buttonsSetup(JFrame parent){
        cancel.addActionListener(e -> dispose());
        accept.addActionListener(e -> {
                    try {
                        if (firstName.getText().equals("") || secondName.getText().equals("") || lastName.getText().equals("")) {
                            throw new NullPointerException();
                        }
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

                        sT.tModel.addRow(student.toData());
                        dispose();
                    }
                    catch(NullPointerException npex)
                    {
                        LogManager.getLogger().info("User didn't fill all the fields");
                        JOptionPane.showMessageDialog(parent, "Все поля обязательны для заполнения!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
        );
    }
}
