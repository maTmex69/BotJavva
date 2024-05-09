package gui;

import java.awt.BorderLayout;
import javax.swing.JPanel;


public class GameWindow extends AbstractWindow
{
    private final GameVisualizer m_visualizer;

    public GameWindow() {
        super();

        setTitle("Игровое окно");
        setResizable(true);
        setClosable(true);
        setMaximizable(true);
        setIconifiable(true);

        m_visualizer = new GameVisualizer();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }

    @Override
    public void dispose() {
//        m_visualizer.stopTimer();

        super.dispose();
    }
}