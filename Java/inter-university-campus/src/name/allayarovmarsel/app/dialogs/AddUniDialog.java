package name.allayarovmarsel.app.dialogs;

import name.allayarovmarsel.app.DuplicateInUniList;
import name.allayarovmarsel.app.tables.UniTable;
import name.allayarovmarsel.app.entities.UniversityObject;
import org.apache.logging.log4j.LogManager;
import javax.swing.*;
import java.awt.*;

public class AddUniDialog extends AbstractPropertiesDialog {

    protected JTextField shortUniName;
    protected JTextField longUniName;
    protected UniTable uT;

    public AddUniDialog(JFrame parent, String tittle, Dialog.ModalityType modal, UniTable uniTable)
    {
        super(parent, tittle, modal);
        //  === КНОПКИ, ПАНЕЛИ, и т.д. СВОЙСТВ ===
        uT = uniTable;
        accept = new JButton("Принять");
        cancel = new JButton("Отмена");
        shortUniName = new JTextField();
        longUniName = new JTextField();

        Labels = new JLabel[]{new JLabel("Сокращенное наименование"), new JLabel("Полное наименование")};

        //  === ДОБАВЛЕНИЕ И РАЗМЕЩЕНИЕ ===

        Container contentPane = getContentPane();
        GroupLayout layout = new GroupLayout(contentPane);
        contentPane.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        final int textFieldSize = 250;

        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(Labels[0])
                                .addComponent(Labels[1])
                                .addComponent(accept)
                        )
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(shortUniName, GroupLayout.PREFERRED_SIZE, textFieldSize, GroupLayout.PREFERRED_SIZE)
                                .addComponent(longUniName, GroupLayout.PREFERRED_SIZE, textFieldSize, GroupLayout.PREFERRED_SIZE)
                                .addComponent(cancel)
                        )
        );

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(Labels[0])
                                .addComponent(shortUniName)
                        )
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(Labels[1])
                                .addComponent(longUniName)
                        )
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(accept)
                                .addComponent(cancel)
                        )
        );
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
        setSize(450, 130);
        setLocation(parent.getX() + (parent.getWidth() - getWidth())/2 , parent.getY() + (parent.getHeight() - getHeight())/2);
        setResizable(false);
    }

    @Override
    protected void buttonsSetup(JFrame parent){
        cancel.addActionListener(e -> dispose());
        accept.addActionListener(e -> {
            try {
                if (shortUniName.getText().equals("") || longUniName.getText().equals("")) {
                    throw new NullPointerException();
                }
                if(uT.containsUniList((shortUniName.getText()))) {
                    if(uT.uniList.get(uT.uniList.indexOf(new UniversityObject(shortUniName.getText()))).getIfShown()) // seek for duplicates by short name
                        throw new DuplicateInUniList();
                }
                uT.uniList.add(new UniversityObject(shortUniName.getText(), longUniName.getText(), true)); // add uni to the list of using universities
                uT.tModel.addRow(new Object[]{shortUniName.getText(), longUniName.getText()});

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
        }
        );
    }
}
