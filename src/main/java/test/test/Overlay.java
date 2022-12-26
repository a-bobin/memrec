package test.test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Overlay {

    private final java.util.List<JLabel> labels;

    public Overlay(String processName, boolean showTimer, int opacity, int fontSize, int colorR, int colorG, int colorB) {
        labels = java.util.List.of(new JLabel(""), new JLabel(""), new JLabel(""));
        labels.forEach(l -> l.setFont(new Font("Verdana", Font.BOLD, fontSize)));
        labels.forEach(l -> l.setForeground(new Color(colorR, colorG, colorB)));
        labels.forEach(l -> l.setAlignmentX(Component.RIGHT_ALIGNMENT));

        Box box = Box.createVerticalBox();
        labels.forEach(box::add);

        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.add(box);

        JFrame frame = new DraggableFramelessWindow("Overlay");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        frame.setPreferredSize(new Dimension((
                processName.length() < 8 ? fontSize*6 : (fontSize-9)*(processName.length()+4)),
                (fontSize+3)*(showTimer? 4 : 3)
        ));
        frame.setBackground(new Color(0, 0, 0, opacity));
        frame.setLocation(100, 100);
        frame.add(panel);
        frame.setVisible(true);
        frame.pack();
    }

    public void setText1(String text) {
        labels.get(0).setText(text);
    }

    public void setText2(String text) {
        labels.get(1).setText(text);
    }

    public void setText3(String text) {
        labels.get(2).setText(text);
    }

    static class DraggableFramelessWindow extends JFrame {

        int posX = 0, posY = 0;

        public DraggableFramelessWindow(String title) {
            super(title);
            this.setUndecorated(true);
            this.addMouseListener(new MouseAdapter()
            {
                public void mousePressed(MouseEvent e)
                {
                    posX=e.getX();
                    posY=e.getY();
                }
            });
            this.addMouseMotionListener(new MouseAdapter() {
                public void mouseDragged(MouseEvent evt) {
                    setLocation (evt.getXOnScreen()-posX,evt.getYOnScreen()-posY);
                }
            });
        }
    }
}
