package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.*;

import State.AbstractWindow;
import log.Logger;

import model.RobotsLogic;
import gui.GameWindow;

/**
 * Главное окно приложения, содержащее панель рабочего стола и меню.
 */
public class MainApplicationFrame extends JFrame {

    /**
     * Текущая локаль для локализации сообщений.
     */
    private Locale currentLocale = new Locale("ru", "RU");

    /**
     * Ресурсы для локализации сообщений.
     */
    private ResourceBundle messages = ResourceBundle.getBundle("resources", currentLocale);

    /**
     * Панель рабочего стола, на которой отображаются внутренние окна.
     */
    private JDesktopPane desktopPane;

    /**
     * Конструктор главного окна приложения.
     */
    public MainApplicationFrame() {
        // Определяем отступы для размещения окна
        Integer indent = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final int indentedWidth = screenSize.width - indent * 2;
        final int indentedHeight = screenSize.height - indent * 2;

        // Устанавливаем размеры и расположение окна
        setBounds(indent, indent, indentedWidth, indentedHeight);

        // Создаем и устанавливаем панель рабочего стола
        setContentPane(createDesktopPane());

        // Создаем и устанавливаем меню
        setJMenuBar(generateMenuBar());

        // Устанавливаем операцию по умолчанию при закрытии окна
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Добавляем слушатель оконного события для закрытия
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });
    }

    /**
     * Создает панель рабочего стола, где отображаются внутренние окна.
     * @return Созданная панель рабочего стола.
     */
    private JDesktopPane createDesktopPane() {
        desktopPane = new JDesktopPane();
        var logic = new RobotsLogic();

        // Добавляем окна на панель рабочего стола
        addWindow(createLogWindow(), 150, 350);
        addWindow(new GameWindow(logic), 400, 400);
        addWindow(new RobotInfo(logic), 150, 350);



        // Загружаем состояние каждого окна
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            AbstractWindow abstractWindow = (AbstractWindow) frame;
            abstractWindow.loadWindow();
        }

        return desktopPane;
    }

    /**
     * Создает окно для отображения логов.
     * @return Созданное окно для отображения логов.
     */
    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        Logger.debug(messages.getString("ProtocolIsWorking"));
        return logWindow;
    }

    /**
     * Добавляет окно на панель рабочего стола.
     * @param frame Окно для добавления.
     * @param width Ширина окна.
     * @param height Высота окна.
     */
    protected void addWindow(JInternalFrame frame, int width, int height) {
        desktopPane.add(frame);
        frame.setSize(width, height);
        frame.setVisible(true);
    }

    /**
     * Генерирует строку меню приложения.
     * @return Сгенерированная строка меню.
     */
    private JMenuBar generateMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        menuBar.add(createFileMenu());
        menuBar.add(createLookAndFeelMenu());
        menuBar.add(createTestMenu());

        return menuBar;
    }



    /**
     * Создает меню файлов.
     * @return Меню файлов.
     */
    private JMenu createFileMenu() {
        var logic = new RobotsLogic();
        JMenu menu = new JMenu(messages.getString("Menu"));
        menu.setMnemonic(KeyEvent.VK_D);

        menu.add(createMenuItem(messages.getString("NewGameWindow"), KeyEvent.VK_N, KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.ALT_MASK), (event) -> {
            GameWindow window = new GameWindow(logic);
            addWindow(window, 400, 400);
        }));

        menu.add(createMenuItem(messages.getString("LogsWindow"), KeyEvent.VK_L, KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.ALT_MASK), (event) -> {
            LogWindow window = new LogWindow(Logger.getDefaultLogSource());
            addWindow(window, 150, 350);
        }));


        menu.add(createMenuItem(messages.getString("Coordinates"), KeyEvent.VK_L, KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.ALT_MASK), (event) -> {
            RobotInfo window = new RobotInfo(logic);
            addWindow(window, 300, 200);
        }));


        menu.add(exit());



        return menu;
    }

    /**
     * Создает элемент меню.
     * @param text Текст элемента меню.
     * @param mnemonic Мнемоника элемента меню.
     * @param accelerator Горячая клавиша элемента меню.
     * @param action Действие элемента меню.
     * @return Созданный элемент меню.
     */
    private JMenuItem createMenuItem(String text, int mnemonic, KeyStroke accelerator, ActionListener action) {
        JMenuItem item = new JMenuItem(text);
        item.setMnemonic(mnemonic);
        item.setAccelerator(accelerator);
        item.addActionListener(action);
        return item;
    }

    /**
     * Создает меню оформления.
     * @return Меню оформления.
     */
    private JMenu createLookAndFeelMenu() {
        JMenu lookAndFeelMenu = new JMenu(messages.getString("DisplayMode"));
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(messages.getString("ModeControl"));

        lookAndFeelMenu.add(createMenuItem(messages.getString("SystemDiagram"), KeyEvent.VK_S, null, (event) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        }));

        lookAndFeelMenu.add(createMenuItem(messages.getString("UniversalScheme"), KeyEvent.VK_U, null, (event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        }));

        return lookAndFeelMenu;
    }

    /**
     * Создает меню тестирования.
     * @return Меню тестирования.
     */
    private JMenu createTestMenu() {
        JMenu testMenu = new JMenu(messages.getString("Tests"));
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(messages.getString("TestsCommands"));

        JMenuItem addLogMessageItem = new JMenuItem(messages.getString("MessageLog"), KeyEvent.VK_S);
        addLogMessageItem.addActionListener((event) -> {
            Logger.debug(messages.getString("NewString"));
        });

        testMenu.add(addLogMessageItem);

        return testMenu;
    }

    /**
     * Устанавливает внешний вид.
     * @param className Название класса внешнего вида.
     */
    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException
                 | IllegalAccessException | UnsupportedLookAndFeelException e) {
        }
    }

    /**
     * Вызывает диалоговое окно закрытия для каждого окна на панели рабочего стола.
     */
    private void callCloseDialog(){
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            AbstractWindow abstractWindow = (AbstractWindow) frame;
            abstractWindow.saveWindow();
        }
    }

    /**
     * Завершает работу приложения.
     */
    private void exitApplication() {
        UIManager.put("OptionPane.yesButtonText", messages.getString("Yes"));
        UIManager.put("OptionPane.noButtonText", messages.getString("No"));

        int confirmation = JOptionPane.showConfirmDialog(this, messages.getString("ConfirmationExitQuestion"),
                messages.getString("ConfirmationExit"), JOptionPane.YES_NO_OPTION);
        callCloseDialog();
    }



    /**
     * Создает элемент меню "Выход".
     * @return Элемент меню "Выход".
     */
    private JMenuItem exit() {
        JMenuItem exitMenuItem = new JMenuItem(messages.getString("Exit"));
        exitMenuItem.setMnemonic(KeyEvent.VK_Q);
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.ALT_MASK));
        exitMenuItem.addActionListener((event) -> {
            exitApplication();
        });
        return exitMenuItem;
    }

}
