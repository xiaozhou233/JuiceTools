package cn.xiaozhou233.juicetools.gui;

import javax.swing.*;
import java.awt.*;

public class StartGui {
    private static JFrame frame;
    private static Bootstrap bootstrap = new Bootstrap();

    public static void start() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("JuiceTools Bootstrap");
            frame.setContentPane(bootstrap.panel1);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.pack();
            frame.setResizable(true);
            frame.setVisible(true);
        });
    }

    public static void showInfo(String info) {
        bootstrap.info.append(info + "\n");
    }

    public static void showControlPanel() {
        SwingUtilities.invokeLater(() -> {
            bootstrap.progressBar1.setIndeterminate(false);
            bootstrap.progressBar1.setValue(100);
            JFrame controlFrame = new JFrame("JuiceTools Control Panel");
            ControlPanel controlPanel = new ControlPanel();
            controlFrame.setContentPane(controlPanel.panel1);
            controlFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            controlFrame.pack();
            controlFrame.setResizable(true);
            controlFrame.setVisible(true);
        });
    }
}
