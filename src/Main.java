import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

public class Main {
    public static String path="src/res/tiger.bmp";
    // 创建及设置窗口
    public static JFrame frame = new JFrame(path);
    public static JLabel infoLabel=new JLabel();
    public static JLabel scaleLabel= new JLabel("缩放：");
    public static JButton grayButton=new JButton("灰度");
    public static JSlider scaleSlider=new JSlider();
    public static BmpImage bmpImage=new BmpImage(path);
    private static void createAndShowGUI() {
        FontUIResource fontUIResource = new FontUIResource(new Font("宋体",Font.PLAIN, 15));
        for (Enumeration keys = UIManager.getDefaults().keys(); keys.hasMoreElements();) {
            Object key = keys.nextElement();
            Object value= UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, fontUIResource);
            }
        }

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        bmpImage.readInfoHead().readRGB24();
        infoLabel.setBounds(10,bmpImage.imageHeigh+20,400,20);
        infoLabel.setText(bmpImage.imageWidth+"x"+bmpImage.imageHeigh+" "+bmpImage.bitCount+"位"
                +"源图大小："+bmpImage.imageSize+"字节");
        frame.setBounds(0,0,bmpImage.imageWidth+500,bmpImage.imageHeigh+100);
        grayButton.setBounds(bmpImage.imageWidth+30,20,95,30);
        grayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               repaint();
            }
        });
        scaleLabel.setBounds(bmpImage.imageWidth+30,60,50,30);
        scaleSlider.setBounds(bmpImage.imageWidth+60,60,300,30);
        frame.add(infoLabel);
        frame.add(grayButton);
        frame.add(scaleLabel);
        frame.add(scaleSlider);
        frame.add(new JScrollPane(bmpImage));
        frame.setVisible(true);

    }
    public static void repaint(){
        bmpImage.transformGray().repaint();
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
