package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

import log.Logger;

public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();

    public MainApplicationFrame() {
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset, screenSize.width - inset * 2, screenSize.height - inset * 2);
        setContentPane(desktopPane);

        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(400, 400);
        addWindow(gameWindow);

        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Добавляем слушатель событий для окна
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication(); // Вызываем метод exitApplication() при закрытии окна
            }
        });

    }

    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10, 10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    /**
     * Генерирует строку меню приложения.
     * Создает и настраивает пункты меню "Режим отображения", "Тесты" и "Меню",
     * добавляя к ним соответствующие подпункты и обработчики событий.
     *
     * @return Строка меню приложения с сгенерированными пунктами меню.
     */
    private JMenuBar generateMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        addFileMenu(menuBar);
        addLookAndFeelMenu(menuBar);
        addTestMenu(menuBar);
        return menuBar;
    }

    /**
     * Метод для добавления пункта "Меню" с пунктом "Выход".
     * Добавляет в главное меню приложения пункт "Меню" с пунктом "Выход",
     * который позволяет пользователю выйти из приложения с подтверждением.
     *
     * @return Строка меню приложения с добавленным пунктом "Файл".
     */
    private void addFileMenu(JMenuBar menuBar) {
        JMenu fileMenu = new JMenu("Меню");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenuItem exitItem = new JMenuItem("Выход", KeyEvent.VK_X);
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.ALT_DOWN_MASK));
        exitItem.addActionListener((event) -> exitApplication());

        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
    }

    private void addLookAndFeelMenu(JMenuBar menuBar) {
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");

        JMenuItem systemLookAndFeel = createLookAndFeelMenuItem("Системная схема", UIManager.getSystemLookAndFeelClassName());
        lookAndFeelMenu.add(systemLookAndFeel);

        JMenuItem crossplatformLookAndFeel = createLookAndFeelMenuItem("Универсальная схема", UIManager.getCrossPlatformLookAndFeelClassName());
        lookAndFeelMenu.add(crossplatformLookAndFeel);

        menuBar.add(lookAndFeelMenu);
    }

    private JMenuItem createLookAndFeelMenuItem(String label, String className) {
        JMenuItem menuItem = new JMenuItem(label);
        menuItem.addActionListener((event) -> {
            setLookAndFeel(className);
            this.invalidate();
        });
        return menuItem;
    }

    private void addTestMenu(JMenuBar menuBar) {
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");

        JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
        addLogMessageItem.addActionListener((event) -> {
            Logger.debug("Новая строка");
        });
        testMenu.add(addLogMessageItem);

        menuBar.add(testMenu);
    }

    /**
     * Метод для завершения работы приложения с подтверждением.
     * Отображает диалоговое окно с вопросом о подтверждении выхода.
     * Если пользователь подтверждает выход, приложение завершает свою работу.
     */
    private void exitApplication() {
        Object[] options = {"Да", "Нет"};
        int confirmed = JOptionPane.showOptionDialog(
                this,
                "Вы уверены, что хотите выйти?",
                "Подтверждение выхода",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);

        if (confirmed == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException
                 | IllegalAccessException | UnsupportedLookAndFeelException e) {
            // just ignore
        }
    }
}
