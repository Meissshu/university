
package name.allayarovmarsel.app.gui;

import name.allayarovmarsel.app.dialogs.*;
import name.allayarovmarsel.app.entities.UniversityObject;
import name.allayarovmarsel.app.threads.ThreadLoadStudentSQL;
import name.allayarovmarsel.app.threads.ThreadLoadUniSQL;
import name.allayarovmarsel.app.threads.ThreadSaveStudentSQL;
import name.allayarovmarsel.app.threads.ThreadSaveUniSQL;
import name.allayarovmarsel.app.tables.StudentTable;
import name.allayarovmarsel.app.tables.UniTable;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.transform.*;
import java.io.File;
import javax.xml.parsers.*;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.regex.PatternSyntaxException;
import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.logging.log4j.*;

/**
 * This is the documentation about "MSG" application.<p>
 * MSG application is created for heads of the dormitory.
 * This application can save, load different databases, provides the ability to work with data.
 * @version 1.0
 */
public class StudentsMain {
    private static final String EMPTY_UNI = "<пусто>";
    private static final String SQL_UNI_TABLE = "universitiestable";
    private static final String SQL_STUDENT_TABLE = "studentstable";

    private static final String url = "jdbc:mysql://127.0.0.1:3306/msg_db?autoReconnect=true&useSSL=false";
    private static final String username = "root";
    private static final String pass = "root";

    private JFrame studentsMain;
    private JButton save;
    private JButton load;
    private JButton dbload;
    private JButton dbsave;
    private JButton add;
    private JButton edit;
    private JButton delete;
    private JButton help;
    private JButton pdf;
    private JToolBar toolBar;

    private JCheckBox wideSearchCheckBox;
    private JLabel[] Labels;
    private JTextField firstName;
    private JTextField secondName;
    private JTextField lastName;
    private JTextField hull;
    private JTextField room;
    private DefaultComboBoxModel<String> uniComboModel = new DefaultComboBoxModel<>(new String[]{EMPTY_UNI});
    private JComboBox<String> uni = new JComboBox<>(uniComboModel);
    private JTextField course;
    private JTextField datePayAgreement;
    private JTextField datePayActual;
    private JComboBox paymentStatus;

    private JComboBox searchCriterion;
    private JTextField searchObject;

    private ArrayList<RowFilter> rowFiltersArray;

    private JTabbedPane tabbedPane;

    static private StudentTable studentTable;
    static private UniTable uniTable;

    private Logger log = LogManager.getLogger();

    public StudentsMain(){
        log.info("Class was created");
    }

    public void show() {

        log.info("Starting the app");
        // ***************** ООЗДАНИЕ И НАСТРОЙКА *****************
        log.info("Main window initialization");
        initMainWindow(); // Главное окно
        log.info("Buttons initialization");
        initButtons();    // Кнопки
        log.info("Toolbar initialization");
        initToolBar();    // Панель инструментов
        log.info("Tabs initialization");
        initTabbedPane(); // Вкладки
        log.info("Tables initialization");
        initDataTable();  // Таблицы с данными (students + uni)
        log.info("Search initialization");
        initSearch();     // Поиск

        // ***************** СОБЫТИЯ И СЛУШАТЕЛИ ****************
        log.info("Action and listeners initialization");
        initActionsAndListeners();

        // we need a registration
        try {
            log.info("Loading and registering JDBC driver for mySQL ");
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch (ClassNotFoundException e){
            log.info("Exception: " + e);
            e.printStackTrace();
        }

        studentsMain.setVisible(true);
    }

    // ***************** ООЗДАНИЕ И НАСТРОЙКА *****************
    // ======== Главное окно ========
    private void initMainWindow() {
        studentsMain = new JFrame("Мониторинг студентов МСГ");
        studentsMain.setSize(800, 500);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        studentsMain.setLocation(((int) screenSize.getWidth() - studentsMain.getWidth()) / 2, ((int) screenSize.getHeight() - studentsMain.getHeight()) / 2); // (screenwidght-appwidght)/2;
        studentsMain.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        studentsMain.setResizable(false);
    }


    // ======== Кнопки ========
    private void initButtons() {
        load = new JButton(new ImageIcon(this.getClass().getResource("/icons/load.png")));
        save = new JButton(new ImageIcon(this.getClass().getResource("/icons/save.png")));
        dbload = new JButton(new ImageIcon(this.getClass().getResource("/icons/db_load.png")));
        dbsave = new JButton(new ImageIcon(this.getClass().getResource("/icons/db_save.png")));
        add = new JButton(new ImageIcon(this.getClass().getResource("/icons/add.png")));
        edit = new JButton(new ImageIcon(this.getClass().getResource("/icons/edit.png")));
        delete = new JButton(new ImageIcon(this.getClass().getResource("/icons/delete.png")));
        pdf = new JButton(new ImageIcon(this.getClass().getResource("/icons/pdf.png")));
        help = new JButton(new ImageIcon(this.getClass().getResource("/icons/help.png")));
        setToolTipTextButtons(); // подсказки для иконок
    }

    private void setToolTipTextButtons() {
        save.setToolTipText("Сохранить данные в XML файл");
        load.setToolTipText("Загрузить данные из XML файла");
        dbload.setToolTipText("Загрузить данные из базы данных mySQL");
        dbsave.setToolTipText("Сохранить данные в базу данных mySQL");
        add.setToolTipText("Добавить новую запись");
        edit.setToolTipText("Изменить выбранную запись");
        delete.setToolTipText("Удалить выбранную запись");
        pdf.setToolTipText("Сохранить отчеты в PDF формате");
        help.setToolTipText("Что-то не выходит?");
    }
    //========================================


    // ======== Панель инструментов ========
    private void initToolBar() {
        toolBar = new JToolBar("Панель инструментов");
        toolBar.setFloatable(false);

        addButtonsToToolbar();  // иконки на панель инструментов

        toolBarLayout(); // размещение панели инструментов
    }

    private void addButtonsToToolbar() {
        toolBar.add(load);
        toolBar.add(save);
        toolBar.add(dbload);
        toolBar.add(dbsave);
        toolBar.add(add);
        toolBar.add(edit);
        toolBar.add(delete);
        toolBar.add(Box.createHorizontalGlue()); // Right align component in a JToolBar @ helpdesk
        toolBar.add(pdf);
        toolBar.add(help);
    }

    private void toolBarLayout() {
        studentsMain.setLayout(new BorderLayout());
        studentsMain.add(toolBar, BorderLayout.NORTH);
    }
    //========================================


    // ======== Вкладки ==========
    private void initTabbedPane() {
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        studentsMain.add(tabbedPane);
    }

    // ============================


    // ======== Таблицы с данными ========
    private void initDataTable() {
        studentTable = new StudentTable();
        uniTable = new UniTable();
        tabbedPane.addTab("Студенты", new ImageIcon("./src/doc-files/students.png"), studentTable.scroll, "Список студентов");
        tabbedPane.addTab("Университеты", new ImageIcon("./src/doc-files/university.png"), uniTable.scroll, "Список университетов");
    }
    //========================================


    // ======== Поиск ========
    private void initSearch() {
        initSingleCriteriaSearch(); // инициализация поиска по одному критерию

        initMultipleCriteriaSearch(); // инициализация поиска по нескольким критериям

        searchFieldsLayout(); // добавление компонентов на панель и их размещение

        initRowFiltersArray(); // инициализация массива для хранения фильтров
    }


    private void initSingleCriteriaSearch() {
        searchCriterion = new JComboBox<>(new String[]{"Фамилия", "Имя", "Отчество", "Корпус", "Комната", "Университет", "Курс", "Срок оплаты", "Время оплаты", "Статус"});
        searchObject = new JTextField();
        searchObject.setPreferredSize(new Dimension(100, 24));
    }

    private void initMultipleCriteriaSearch() {
        wideSearchCheckBox = new JCheckBox("Wide search", false);
        firstName = new JTextField();
        secondName = new JTextField();
        lastName = new JTextField();
        hull = new JTextField();
        room = new JTextField();
        course = new JTextField();
        datePayAgreement = new JTextField();
        datePayActual = new JTextField();
        paymentStatus = new JComboBox<>(new String[]{"Не важно", "Не оплачено", "Просрочено", "Оплачено"});
        Labels = new JLabel[]{
                new JLabel("Фамилия"),
                new JLabel("Имя"),
                new JLabel("Отчество"),
                new JLabel("Корпус"),
                new JLabel("Комната"),
                new JLabel("Университет"),
                new JLabel("Курс"),
                new JLabel("Срок оплаты"),
                new JLabel("Время оплаты"),
                new JLabel("Статус")
        };

        updateWideSearchComboBox();
    }

    // расстановка компонентов для поиска по нескольким критериям
    private void searchFieldsLayout() {

        JPanel wideSearchPanel = new JPanel();
        GroupLayout wideSearchLayout = new GroupLayout(wideSearchPanel);
        wideSearchPanel.setLayout(wideSearchLayout);
        wideSearchLayout.setAutoCreateGaps(true);
        wideSearchLayout.setAutoCreateContainerGaps(true);

        final int textFieldSize = 180;
        wideSearchLayout.setHorizontalGroup(
                wideSearchLayout.createSequentialGroup()
                        .addGroup(wideSearchLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(wideSearchCheckBox)
                                .addComponent(searchCriterion, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                                .addComponent(Labels[0])
                                .addComponent(Labels[1])
                                .addComponent(Labels[2])
                                .addComponent(Labels[3])
                                .addComponent(Labels[4])
                        )
                        .addGroup(wideSearchLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(searchObject, GroupLayout.PREFERRED_SIZE, textFieldSize, GroupLayout.PREFERRED_SIZE)
                                .addComponent(firstName, GroupLayout.PREFERRED_SIZE, textFieldSize, GroupLayout.PREFERRED_SIZE)
                                .addComponent(secondName, GroupLayout.PREFERRED_SIZE, textFieldSize, GroupLayout.PREFERRED_SIZE)
                                .addComponent(lastName, GroupLayout.PREFERRED_SIZE, textFieldSize, GroupLayout.PREFERRED_SIZE)
                                .addComponent(hull, GroupLayout.PREFERRED_SIZE, textFieldSize, GroupLayout.PREFERRED_SIZE)
                                .addComponent(room, GroupLayout.PREFERRED_SIZE, textFieldSize, GroupLayout.PREFERRED_SIZE)
                        )
                        .addGroup(wideSearchLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(Labels[5])
                                .addComponent(Labels[6])
                                .addComponent(Labels[7])
                                .addComponent(Labels[8])
                                .addComponent(Labels[9])
                        )
                        .addGroup(wideSearchLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(uni, GroupLayout.PREFERRED_SIZE, textFieldSize, GroupLayout.PREFERRED_SIZE)
                                .addComponent(course, GroupLayout.PREFERRED_SIZE, textFieldSize, GroupLayout.PREFERRED_SIZE)
                                .addComponent(datePayAgreement, GroupLayout.PREFERRED_SIZE, textFieldSize, GroupLayout.PREFERRED_SIZE)
                                .addComponent(datePayActual, GroupLayout.PREFERRED_SIZE, textFieldSize, GroupLayout.PREFERRED_SIZE)
                                .addComponent(paymentStatus, GroupLayout.PREFERRED_SIZE, textFieldSize, GroupLayout.PREFERRED_SIZE)
                        )
        );

        wideSearchLayout.setVerticalGroup(
                wideSearchLayout.createSequentialGroup()
                        .addGroup(wideSearchLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(wideSearchCheckBox)
                        )
                        .addGroup(wideSearchLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(searchCriterion)
                                .addComponent(searchObject)
                        )
                        .addGroup(wideSearchLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(Labels[0])
                                .addComponent(firstName)
                                .addComponent(Labels[5])
                                .addComponent(uni)
                        )
                        .addGroup(wideSearchLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(Labels[1])
                                .addComponent(secondName)
                                .addComponent(Labels[6])
                                .addComponent(course)
                        )
                        .addGroup(wideSearchLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(Labels[2])
                                .addComponent(lastName)
                                .addComponent(Labels[7])
                                .addComponent(datePayAgreement)
                        )
                        .addGroup(wideSearchLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(Labels[3])
                                .addComponent(hull)
                                .addComponent(Labels[8])
                                .addComponent(datePayActual)
                        )
                        .addGroup(wideSearchLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(Labels[4])
                                .addComponent(room)
                                .addComponent(Labels[9])
                                .addComponent(paymentStatus)
                        )
        );
        studentsMain.add(wideSearchPanel, BorderLayout.SOUTH);
    }

    private void initRowFiltersArray() {
        rowFiltersArray = new ArrayList<>();
        rowFiltersArray.add(0, null);
        rowFiltersArray.add(1, null);
        actionSwitch(studentTable.sorter, rowFiltersArray);
    }
    //========================================


    // ***************** СОБЫТИЯ И СЛУШАТЕЛИ ****************
    private void initActionsAndListeners() {

        // ======== События ========
        save.addActionListener(e -> actionSave());  // сохранение xml
        load.addActionListener(e -> actionLoad());  // загрузка xml

        dbsave.addActionListener(e -> actionSaveDB()); // save to DB
        dbload.addActionListener(e -> actionLoadDB()); // load from DB
        add.addActionListener(e -> actionAdd());   // добавление

        // изменение
        edit.addActionListener(e ->
        {
            try {
                actionEdit();
            } catch (ArrayIndexOutOfBoundsException ex) {
                log.info("User didn't choose any line to edit");
                JOptionPane.showMessageDialog(studentsMain, "Выберите строку для изменения", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // удаление
        delete.addActionListener(e ->
        {
            try {
                actionDelete();
            } catch (ArrayIndexOutOfBoundsException ex) {
                log.info("User didn't choose any line to delete");
                JOptionPane.showMessageDialog(studentsMain, "Выберите строку для удаления", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        pdf.addActionListener(e -> actionReport()); // сохранение отчетов в pdf
        help.addActionListener(e -> actionHelp()); // справка

        searchObject.addCaretListener(e -> actionSingleSearch(studentTable.sorter, rowFiltersArray));  // поиск по одному критерию студенты
        searchObject.addCaretListener(e -> actionSingleSearch(uniTable.sorter, rowFiltersArray));  // поиск по одному критерию университеты

        wideSearchCheckBox.addActionListener(e -> actionSwitch(studentTable.sorter, rowFiltersArray));  // переключение типа поиска

        // поиск по нескольким критериям
        JTextField[] textFields = new JTextField[]{firstName, secondName, lastName, hull, room, course, datePayAgreement, datePayActual}; //кроме paymentstatus
        for (JTextField x : textFields)
            x.addCaretListener(e -> actionWideSearch(studentTable.sorter, rowFiltersArray));
        uni.addActionListener(e -> actionWideSearch(studentTable.sorter, rowFiltersArray)); // combobox
        paymentStatus.addActionListener(e -> actionWideSearch(studentTable.sorter, rowFiltersArray)); // paymentstatus

        tabbedPane.addChangeListener(e -> actionChangedCurrentTab()); // переключение вкладок
        Runtime.getRuntime().addShutdownHook(new Thread(() -> log.info("The app is closed")));
    }

    // ======== Методы обработки событий ========

    // сохранение
    private void actionSave() {
        log.info("Saving as xml - button pressed");
        FileDialog save = new FileDialog(studentsMain, "Save", FileDialog.SAVE);
        save.setFile("*.xml");
        save.setVisible(true);
        String fileName = save.getFile();
        if (fileName != null) {
            try {
                log.info("Saving as xml - file is opened");
                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document doc = builder.newDocument();
                Node appData = doc.createElement("appData");
                doc.appendChild(appData);

                Node listOfStudents = doc.createElement("listOfStudents");
                appData.appendChild(listOfStudents);


                Node listOfUniversities = doc.createElement("listOfUniversities");
                appData.appendChild(listOfUniversities);

                // формируем группу используемых университетов перед записью
                // stream example -> uniTable.uniList.stream().filter(UniversityObject::getIfShown).collect(Collectors.toCollection(ArrayList::new));
                // but using stream here is not effective
                ArrayList<UniversityObject> fullUniGroup = new ArrayList<>();

                for (UniversityObject x: uniTable.uniList) {
                    if(x.getIfShown())
                        fullUniGroup.add(x);
                }
                int rows = studentTable.tModel.getRowCount();

                for (int i = 0; i < rows; ++i) {
                    Element student = doc.createElement("student");
                    String uniName = (String) studentTable.tModel.getValueAt(i, 5);

                    student.setAttribute("firstName", (String) studentTable.tModel.getValueAt(i, 0));
                    student.setAttribute("secondName", (String) studentTable.tModel.getValueAt(i, 1));
                    student.setAttribute("lastName", (String) studentTable.tModel.getValueAt(i, 2));
                    student.setAttribute("hull", (String) studentTable.tModel.getValueAt(i, 3));
                    student.setAttribute("room", (String) studentTable.tModel.getValueAt(i, 4));
                    student.setAttribute("uni", uniName);
                    student.setAttribute("course", (String) studentTable.tModel.getValueAt(i, 6));
                    student.setAttribute("payTimeArrangement", (String) studentTable.tModel.getValueAt(i, 7));
                    student.setAttribute("payTimeActual", (String) studentTable.tModel.getValueAt(i, 8));
                    student.setAttribute("paymentStatus", (String) studentTable.tModel.getValueAt(i, 9));

                    if (!fullUniGroup.contains(new UniversityObject(uniName)))
                        fullUniGroup.add(uniTable.uniList.get(uniTable.uniList.indexOf(new UniversityObject(uniName))));
                    listOfStudents.appendChild(student);
                }

                fullUniGroup.remove(0);
                rows = fullUniGroup.size();
                for (int i = 0; i < rows; ++i) {
                    Element university = doc.createElement("uni");
                    university.setAttribute("shortName", fullUniGroup.get(i).getShortName());
                    university.setAttribute("longName", fullUniGroup.get(i).getLongName());
                    listOfUniversities.appendChild(university);
                }
                try (FileWriter fw = new FileWriter(save.getDirectory() + fileName)) {
                    Transformer trans = TransformerFactory.newInstance().newTransformer();
                    trans.transform(new DOMSource(doc), new StreamResult(fw));

                    log.info("Saving as xml - file is closed");
                    JOptionPane.showMessageDialog(studentsMain, "Сохранение произведено успешно!", "Сохранение: успех", JOptionPane.INFORMATION_MESSAGE);

                } catch (TransformerException | IOException tce) {
                    log.info("Exception: " + tce.toString());
                    tce.printStackTrace();
                    JOptionPane.showMessageDialog(studentsMain, "Ошибка при сохранении!", "Error", JOptionPane.ERROR_MESSAGE);
                }


            } catch (ParserConfigurationException e) {
                log.info("Exception: " + e.toString());
                e.printStackTrace();
                JOptionPane.showMessageDialog(studentsMain, "Серьезная ошибка конфигурации!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

    }

    // загрузка
    private void actionLoad() {
        log.info("Loading as xml - button pressed");
        FileDialog save = new FileDialog(studentsMain, "Load", FileDialog.LOAD);
        save.setFile("*.xml");
        save.setVisible(true);
        String fileName = save.getFile();
        if (fileName != null) {
            try {
                log.info("Loading as xml - file is opened");
                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document doc = builder.parse(new File(save.getDirectory() + fileName));
                doc.getDocumentElement().normalize();

                NodeList currentList = doc.getElementsByTagName("student");
                int length = studentTable.tModel.getRowCount();

                for (int i = 0; i < length; ++i) {
                    studentTable.tModel.removeRow(0);
                }

                length = currentList.getLength();
                for (int i = 0; i < length; ++i) {
                    Node element = currentList.item(i);
                    NamedNodeMap attrs = element.getAttributes();

                    String firstName = attrs.getNamedItem("firstName").getNodeValue();
                    String secondName = attrs.getNamedItem("secondName").getNodeValue();
                    String lastName = attrs.getNamedItem("lastName").getNodeValue();
                    String hull = attrs.getNamedItem("hull").getNodeValue();
                    String room = attrs.getNamedItem("room").getNodeValue();
                    String uni = attrs.getNamedItem("uni").getNodeValue();
                    String course = attrs.getNamedItem("course").getNodeValue();
                    String payTimeArrangement = attrs.getNamedItem("payTimeArrangement").getNodeValue();
                    String payTimeActual = attrs.getNamedItem("payTimeActual").getNodeValue();
                    String paymentStatus = attrs.getNamedItem("paymentStatus").getNodeValue();

                    studentTable.tModel.addRow(new String[]{firstName, secondName, lastName, hull, room, uni, course, payTimeArrangement, payTimeActual, paymentStatus});

                }

                length = uniTable.tModel.getRowCount();
                for (int i = 0; i < length; ++i) {
                    uniTable.tModel.removeRow(0);
                }

                // все, кроме пусто пусто
                length = uniTable.uniList.size();
                for (int i = 1; i < length; ++i)
                    uniTable.uniList.remove(1);

                currentList = doc.getElementsByTagName("uni");
                length = currentList.getLength();
                for (int i = 0; i < length; ++i) {
                    Node element = currentList.item(i);
                    NamedNodeMap attrs = element.getAttributes();

                    String shortName = attrs.getNamedItem("shortName").getNodeValue();
                    String longName = attrs.getNamedItem("longName").getNodeValue();

                    uniTable.tModel.addRow(new String[]{shortName, longName});
                    uniTable.uniList.add(new UniversityObject(shortName, longName, true));
                }
                updateWideSearchComboBox();
                log.info("Loading as xml - file is closed");
                JOptionPane.showMessageDialog(studentsMain, "Загрузка произведена успешно!", "Загрузка: успех", JOptionPane.INFORMATION_MESSAGE);

            } catch (ParserConfigurationException | SAXException | IOException pce) {
                log.info("Exception: " + pce.toString());
                pce.printStackTrace();
                JOptionPane.showMessageDialog(studentsMain, "Ошибка при загрузке!", "Error", JOptionPane.ERROR_MESSAGE);
            }

        }
    }

    // save to DB
    private void actionSaveDB() {
        log.info("Saving database - button pressed");
        if (JOptionPane.showConfirmDialog(studentsMain, "Вы уверены, что хотите сохранить все записи в базу данных?", "Подвертите запись", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

            try (Connection con = DriverManager.getConnection(url, username, pass)) {
                log.info("Saving database - confirmed; connection established");
                ThreadSaveUniSQL threadUni = new ThreadSaveUniSQL(con, uniTable, studentTable, SQL_UNI_TABLE);
                ThreadSaveStudentSQL threadStudent = new ThreadSaveStudentSQL(con, studentTable, SQL_STUDENT_TABLE);

                try {
                    threadUni.t.join();
                    threadStudent.t.join();
                } catch (InterruptedException e) {
                    log.info("Exception: " + e.toString());
                    e.printStackTrace();
                }

                log.info("Saving database - connection is out");
                JOptionPane.showMessageDialog(studentsMain, "Запись произведена успешно!", "", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                log.info("Exception: " + e.toString());
                e.printStackTrace();
                JOptionPane.showMessageDialog(studentsMain, "Ошибка записи в базу данных", "Error", JOptionPane.ERROR_MESSAGE);

            }
        }
    }

    // load from DB
    private void actionLoadDB() {
        log.info("Loading database - button pressed");
        if (JOptionPane.showConfirmDialog(studentsMain, "Вы действительно хотите загрузить записи из базы данных?", "Подтвердите загрузку", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try (Connection con = DriverManager.getConnection(url, username, pass)) {
                log.info("Loading database - confirmed; connection established");
                ThreadLoadUniSQL threadUni = new ThreadLoadUniSQL(con, uniTable, SQL_UNI_TABLE);
                ThreadLoadStudentSQL threadStu = new ThreadLoadStudentSQL(con, studentTable, SQL_STUDENT_TABLE);

                try {
                    threadUni.t.join();
                    threadStu.t.join();
                } catch (InterruptedException e) {
                    log.info("Exception: " + e.toString());
                    e.printStackTrace();
                }

                updateWideSearchComboBox();
                log.info("Loading database - connection is out");
                JOptionPane.showMessageDialog(studentsMain, "Загрузка произведена успешно!", "", JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException e) {
                log.info("Exception: " + e.toString());
                e.printStackTrace();
                JOptionPane.showMessageDialog(studentsMain, "Ошибка загрузки записей из базы данных", "Error", JOptionPane.ERROR_MESSAGE);

            }
        }
    }

    // добавление
    private void actionAdd() {
        AbstractPropertiesDialog dlg;
        if (tabbedPane.getSelectedIndex() == 0) {
            log.info("Adding a student");
            dlg = new AddStudentDialog(studentsMain, "Добавление: свойства студента", Dialog.ModalityType.DOCUMENT_MODAL, studentTable, uniTable);
        } else {
            log.info("Adding an university");
            dlg = new AddUniDialog(studentsMain, "Добавление: высшее учебное заведение", Dialog.ModalityType.DOCUMENT_MODAL, uniTable);
        }
        dlg.actionPerformed();
    }

    // изменение
    private void actionEdit() {
        AbstractPropertiesDialog dlg;
        if (tabbedPane.getSelectedIndex() == 0) {
            log.info("Editing the student");
            dlg = new EditStudentDialog(studentsMain, "Изменение: свойства студента", Dialog.ModalityType.DOCUMENT_MODAL, studentTable, uniTable);
        } else {
            log.info("Editing the university");
            dlg = new EditUniDialog(studentsMain, "Изменение: высшее учебное заведение", Dialog.ModalityType.DOCUMENT_MODAL, uniTable, studentTable);
        }
        dlg.actionPerformed();
    }

    // удаление
    private void actionDelete() {
        int selectedRow;
        if (tabbedPane.getSelectedIndex() == 0) {
            log.info("Deleting the student - button pressed");
            selectedRow = studentTable.AbstractJTable.getSelectedRow();
            selectedRow = studentTable.AbstractJTable.convertRowIndexToModel(selectedRow);
            int action = JOptionPane.showConfirmDialog(studentsMain, "Вы действительно хотите удалить следующего студента: " + studentTable.tModel.getValueAt(selectedRow, 0) + " " + studentTable.tModel.getValueAt(selectedRow, 1) + "?", "Удаление записи", JOptionPane.YES_NO_OPTION);
            if (action == JOptionPane.YES_OPTION) {
                studentTable.tModel.removeRow(selectedRow);
                log.info("Deleting the student - confirmed");
            }
        } else {
            log.info("Deleting the university - button pressed");
            selectedRow = uniTable.AbstractJTable.getSelectedRow();
            selectedRow = uniTable.AbstractJTable.convertRowIndexToModel(selectedRow);
            int action = JOptionPane.showConfirmDialog(studentsMain, "Вы действительно хотите удалить запись о вузе" + "\n\"" + uniTable.tModel.getValueAt(selectedRow, 1) + "\"?", "Удаление записи", JOptionPane.YES_NO_OPTION);
            if (action == JOptionPane.YES_OPTION) {
                uniTable.uniList.get(
                        uniTable.uniList.indexOf(
                                new UniversityObject(uniTable.tModel.getValueAt(selectedRow, 0).toString()))
                ).setIfShown(false); // мы не удаляем университет из таблицы, а просто скрываем
                uniTable.tModel.removeRow(selectedRow);

                log.info("Deleting the university - confirmed; \nCurrent list: " + Arrays.toString(uniTable.uniList.toArray()) );
            }
        }
    }

    // создание PDF отчетов
    private void actionReport() {
        final String recordPathUni = "/appData/listOfUniversities/uni"; // XPath (тип источника данных – XML-файл);
        final String recordPathStud = "/appData/listOfStudents/student";
        final String reportFileNameUni = "src/name/allayarovmarsel/app/resources/reports/report3.jasper"; // имя jrxml-файла шаблона;
        final String reportFileNameStud = "src/name/allayarovmarsel/app/resources/reports/report5.jasper";

        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("XML-files", "xml"));
        if(fc.showOpenDialog(studentsMain) == JFileChooser.APPROVE_OPTION) {
            final String xmlFileName = fc.getSelectedFile().getAbsolutePath();
            log.info("Creating pdf files - starting");
            try {
                final String outFileNameUni = fc.getSelectedFile().getParent() + "\\UniReport.pdf"; // имя файла отчета
                final String outFileNameStud =  fc.getSelectedFile().getParent() + "\\StudReport.pdf";
                JReport(xmlFileName, recordPathUni, reportFileNameUni, outFileNameUni); // uni
                JReport(xmlFileName, recordPathStud, reportFileNameStud, outFileNameStud);
            } catch (JRException e) {
                log.info("Exception: " + e.toString());
                e.printStackTrace();
                JOptionPane.showMessageDialog(studentsMain, "Ошибка формирования отчета!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        log.info("Creating pdf files - done");
        JOptionPane.showMessageDialog(studentsMain, "Сохранение отчетов завершено!", "Success!", JOptionPane.INFORMATION_MESSAGE);
    }

    private void JReport(String xmlFileName, String recordPath, String reportFileName, String outFileName) throws  JRException{
        HashMap<String, Object> hm = new HashMap<>();
        JRDataSource ds = new JRXmlDataSource(xmlFileName, recordPath); // Указание  источника XML-данных
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new File(reportFileName)); //(JasperReport) JRLoader.loadObject(new File(reportFileName)); //JasperCompileManager.compileReport(reportFileName); // Создание отчета на базе шаблона
        JasperPrint print = JasperFillManager.fillReport(jasperReport, hm, ds); // Заполнение отчета данными
        JRPdfExporter exporter = new JRPdfExporter(); // Генерация отчета в формате PDF
        exporter.setExporterInput(new SimpleExporterInput(print));  // Подключение данных к отчету
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outFileName)); // Задание имени файла для выгрузки отчета
        SimplePdfExporterConfiguration config = new SimplePdfExporterConfiguration();
        exporter.setConfiguration(config);
        exporter.exportReport();
        log.info("Exporting " + outFileName + " - success");
    }

    // помощь
    private void actionHelp() {
        log.info("Helping :)");
        JOptionPane.showMessageDialog(studentsMain, "\nMSG App (c)\nCreated by Marsel Allayarov\nGroup: 4305\nUniversityObject: LETI\nYear: 2016", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    // переключение режима поиска
    private void actionSwitch(TableRowSorter<TableModel> sorter, ArrayList<RowFilter> filters) {
        boolean isWideSearchEnabled = wideSearchCheckBox.isSelected();

        for (JLabel x : Labels)
            x.setEnabled(isWideSearchEnabled);

        Component[] textFields = new Component[]{firstName, secondName, lastName, hull, room, uni, course, datePayAgreement, datePayActual, paymentStatus};
        for (Component x : textFields)
            x.setEnabled(isWideSearchEnabled);

        searchCriterion.setEnabled(!isWideSearchEnabled);
        searchObject.setEnabled(!isWideSearchEnabled);

        sorter.setRowFilter(filters.get(isWideSearchEnabled ? 1 : 0));
    }

    // поиск
    private void actionSingleSearch(TableRowSorter<TableModel> sorter, ArrayList<RowFilter> filters) {
        String text = searchObject.getText();
        if (text.length() == 0) {
            filters.set(0, null);
        } else {
            try {
                if (tabbedPane.getSelectedIndex() == 0) {
                    filters.set(0, RowFilter.regexFilter("(?u)(?i)^" + text, searchCriterion.getSelectedIndex()));
                    // http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html#CASE_INSENSITIVE
                    // http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html#UNICODE_CASE - теперь кириллица тоже работает
                    // ^ - matches the beggining
                } else {
                    java.util.List<RowFilter<TableModel, Object>> uniFilters = new ArrayList<>();
                    uniFilters.add(RowFilter.regexFilter("(?u)(?i)^" + text, 0));
                    uniFilters.add(RowFilter.regexFilter("(?u)(?i)" + text, 1));
                    filters.set(0, RowFilter.orFilter(uniFilters));
                }
            } catch (PatternSyntaxException e) {
                Toolkit.getDefaultToolkit().beep();
                filters.set(0, null);
            }
        }
        sorter.setRowFilter(filters.get(0));
    }

    // поиск по нескольким критериям
    private void actionWideSearch(TableRowSorter<TableModel> sorter, ArrayList<RowFilter> parentFilters) {
        java.util.List<RowFilter<TableModel, Object>> filters = new ArrayList<>();
        JTextField[] textFields = new JTextField[]{firstName, secondName, lastName, hull, room, course, datePayAgreement, datePayActual};
        for (int i = 0; i < textFields.length; ++i)
            try {
                filters.add(RowFilter.regexFilter("(?u)(?i)^" + textFields[i].getText(), i + i/5)); // ибо не совпадают индексы text fields с индексами столбцом
            } catch (PatternSyntaxException e) {
                filters.add(RowFilter.regexFilter("(?u)(?i)^", i));
            }
        if (uni.getSelectedItem() != EMPTY_UNI)
            filters.add(RowFilter.regexFilter("(?u)(?i)^" + uni.getSelectedItem().toString(), 5));
        if (!paymentStatus.getSelectedItem().equals("Не важно"))
            filters.add(RowFilter.regexFilter("(?u)(?i)^" + paymentStatus.getSelectedItem().toString(), 9));
        parentFilters.set(1, RowFilter.andFilter(filters));
        sorter.setRowFilter(parentFilters.get(1));
    }

    // смена текущей вкладки
    private void actionChangedCurrentTab() {
        log.info("Switching to " + tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()));
        if (tabbedPane.getSelectedIndex() == 1) {
            if (wideSearchCheckBox.isSelected()) {
                wideSearchCheckBox.setSelected(false);
                actionSwitch(studentTable.sorter, rowFiltersArray);
            }
            searchCriterion.setSelectedItem("Университет"); // how to fix?
            searchCriterion.setEnabled(false);
            wideSearchCheckBox.setEnabled(false);
        } else {
            updateWideSearchComboBox();
            wideSearchCheckBox.setEnabled(!wideSearchCheckBox.isEnabled());
            searchCriterion.setEnabled(!searchCriterion.isEnabled());
            searchCriterion.setSelectedIndex(0);
        }
    }

    private void updateWideSearchComboBox() {
        log.info("Wide search was updated!");
        int size = uniComboModel.getSize();

        for (int i = 1; i < size; ++i)
            uniComboModel.removeElementAt(1);
        size = uniTable.uniList.size();

        for (int i = 0; i < size; ++i) {
            if (!(uniTable.uniList.get(i).getShortName()).equals(EMPTY_UNI) && (uniTable.uniList.get(i).getIfShown())) {
                uniComboModel.addElement(uniTable.uniList.get(i).getShortName());
            }
        }
    }
}