import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {
    private static void createAndShowGUI() {

        String path="src/res/tiger.bmp";
        // 创建及设置窗口
        JFrame frame = new JFrame(path);
        JLabel infoLabel=new JLabel();
        JButton grayButton=new JButton("灰度");
        Font textFont=new Font("Dialog", 1, 18);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BmpImage bmpImage=new BmpImage(path);
        bmpImage.readInfoHead().readRGB24();
        infoLabel.setBounds(10,bmpImage.imageHeigh+20,400,20);
        infoLabel.setText(bmpImage.imageWidth+"x"+bmpImage.imageHeigh+" "+bmpImage.bitCount+"位"
                +"源图大小："+bmpImage.imageSize+"字节");
        frame.setBounds(0,0,bmpImage.imageWidth+500,bmpImage.imageHeigh+100);
        grayButton.setBounds(bmpImage.imageWidth+30,20,95,30);
        grayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bmpImage.transformGray().repaint();
                infoLabel.repaint();
            }
        });
        frame.add(infoLabel);
        frame.add(grayButton);
        frame.add(bmpImage);
        frame.setVisible(true);



    }
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
