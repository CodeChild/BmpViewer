import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

public class Main {
    public static String path = "src/res/tiger.bmp";
    // 创建及设置窗口
    public static JFrame frame = new JFrame(path);
    public static JLabel infoLabel = new JLabel();
    public static JLabel scaleLabel = new JLabel("缩放：");
    public static JButton grayButton = new JButton("灰度");
    public static JButton rotateRightButton = new JButton("右转90度");
    public static JButton rotateLeftButton = new JButton("左转90度");
    public static JCheckBox redCheckBox = new JCheckBox("红色通道");
    public static JCheckBox greenCheckBox = new JCheckBox("绿色通道");
    public static JCheckBox blueCheckBox = new JCheckBox("蓝色通道");
    public static JSlider scaleSlider = new JSlider();
    public static BmpImage bmpImage = new BmpImage(path);
    public static BmpImage zoomedImage;
    public static boolean[] rgbChannel = {true, true, true};

    private static void createAndShowGUI() {
        FontUIResource fontUIResource = new FontUIResource(new Font("宋体", Font.PLAIN, 15));
        for (Enumeration keys = UIManager.getDefaults().keys(); keys.hasMoreElements(); ) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, fontUIResource);
            }
        }

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        bmpImage.readInfoHead().readRGB24();
        infoLabel.setBounds(10, bmpImage.imageHeigh + 20, 400, 20);
        infoLabel.setText(bmpImage.imageWidth + "x" + bmpImage.imageHeigh + " " + bmpImage.bitCount + "位"
                + " 源图大小：" + bmpImage.imageSize + "字节");
        frame.setBounds(0, 0, bmpImage.imageWidth + 500, bmpImage.imageHeigh + 100);
        grayButton.setBounds(bmpImage.imageWidth + 30, 20, 95, 30);
        grayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bmpImage.transformGray();
                if (zoomedImage != null)
                    zoomedImage.transformGray();
                frame.remove(redCheckBox);
                frame.remove(greenCheckBox);
                frame.remove(blueCheckBox);
                repaint();
            }
        });
        scaleLabel.setBounds(bmpImage.imageWidth + 30, 60, 50, 30);
        redCheckBox.setSelected(true);
        greenCheckBox.setSelected(true);
        blueCheckBox.setSelected(true);
        redCheckBox.setBounds(bmpImage.imageWidth + 30, 110, 160, 30);
        greenCheckBox.setBounds(bmpImage.imageWidth + 190, 110, 160, 30);
        blueCheckBox.setBounds(bmpImage.imageWidth + 350, 110, 160, 30);
        rotateRightButton.setBounds(bmpImage.imageWidth + 30, 150, 100, 30);
        rotateLeftButton.setBounds(bmpImage.imageWidth + 150, 150, 100, 30);
        rotateRightButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (zoomedImage != null)
                    zoomedImage.rotate90(true);
                else bmpImage.rotate90(true);
                frame.setBounds(0, 0, bmpImage.imageWidth + 500, bmpImage.imageHeigh + 100);
                repaint();
            }
        });
        rotateLeftButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (zoomedImage != null)
                    zoomedImage.rotate90(false);
                else bmpImage.rotate90(false);
                frame.setBounds(0, 0, bmpImage.imageWidth + 500, bmpImage.imageHeigh + 100);
                repaint();
            }
        });
        scaleSlider.setBounds(bmpImage.imageWidth + 60, 60, 300, 30);
        scaleSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (e.getSource() instanceof JSlider) {
                    JSlider source = (JSlider) e.getSource();
                    float scaleRatio = source.getValue() / 100f * 2f;
                    zoomedImage = bmpImage.scale(scaleRatio);
                    frame.getContentPane().removeAll();
                    frame.add(infoLabel);
                    frame.add(grayButton);
                    frame.add(scaleLabel);
                    frame.add(scaleSlider);
                    frame.add(redCheckBox);
                    frame.add(greenCheckBox);
                    frame.add(blueCheckBox);
                    frame.add(rotateRightButton);
                    frame.add(rotateLeftButton);
                    frame.add(zoomedImage);
                    frame.revalidate();
                    frame.repaint();

                }
            }
        });
        ActionListener checkBoxActionListenner = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (redCheckBox.isSelected())
                    rgbChannel[0] = true;
                else
                    rgbChannel[0] = false;
                if (greenCheckBox.isSelected())
                    rgbChannel[1] = true;
                else
                    rgbChannel[1] = false;
                if (blueCheckBox.isSelected())
                    rgbChannel[2] = true;
                else
                    rgbChannel[2] = false;
                scaleSlider.setValue(50);
                updateRGBChannel();
            }
        };

        redCheckBox.addActionListener(checkBoxActionListenner);
        greenCheckBox.addActionListener(checkBoxActionListenner);
        blueCheckBox.addActionListener(checkBoxActionListenner);
        frame.add(infoLabel);
        frame.add(grayButton);
        frame.add(scaleLabel);
        frame.add(scaleSlider);
        frame.add(redCheckBox);
        frame.add(greenCheckBox);
        frame.add(blueCheckBox);
        frame.add(rotateRightButton);
        frame.add(rotateLeftButton);
        Dimension dimension = new Dimension(600, 400);
        JScrollPane imgScrollPane = new JScrollPane(bmpImage);
        imgScrollPane.setPreferredSize(dimension);
        frame.add(imgScrollPane);
        frame.setVisible(true);

    }

    private static void updateRGBChannel() {
        BmpImage rgbChannelImage = bmpImage.rgbChannel(rgbChannel[0], rgbChannel[1], rgbChannel[2]);
        frame.getContentPane().removeAll();
        frame.add(infoLabel);
        frame.add(grayButton);
        frame.add(scaleLabel);
        frame.add(scaleSlider);
        frame.add(redCheckBox);
        frame.add(greenCheckBox);
        frame.add(blueCheckBox);
        frame.add(rgbChannelImage);
        frame.revalidate();
        frame.repaint();

    }

    public static void repaint() {
        bmpImage.repaint();
        frame.repaint();
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
