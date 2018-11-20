import javax.swing.*;
import java.awt.*;
import java.io.*;

public class BmpImage extends JPanel {
    FileInputStream fis;
    DataInputStream dis;
    private String path;
    public int imageHeigh = 0;
    public int imageWidth = 0;
    public int bitCount = 0;
    public int imageSize = 0;
    public int[][] imageR;
    public int[][] imageG;
    public int[][] imageB;

    BmpImage(String path) {
        this.path = path;
        try {
            int bflen = 14;
            byte bf[] = new byte[bflen];
            fis = new FileInputStream(path);
            dis = new DataInputStream(fis);
            dis.read(bf, 0, bflen);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public BmpImage readInfoHead() {
        int bilen = 40;
        byte bi[] = new byte[bilen];
        try {
            dis.read(bi, 0, bilen);//读取40字节BMP信息头
            // 获取一些重要数据
            imageWidth = ChangeInt(bi, 7);                //源图宽度

            System.out.println("宽:" + imageWidth);

            imageHeigh = ChangeInt(bi, 11);        //源图高度
            System.out.println("高:" + imageHeigh);
            //位数
            bitCount = (((int) bi[15] & 0xff) << 8) | (int) bi[14] & 0xff;
            System.out.println("位数:" + bitCount);
            //源图大小
            imageSize = ChangeInt(bi, 23);
            System.out.println("源图大小:" + imageSize);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return this;
    }

    //转成int
    public int ChangeInt(byte[] bi, int start) {
        return (((int) bi[start] & 0xff) << 24)
                | (((int) bi[start - 1] & 0xff) << 16)
                | (((int) bi[start - 2] & 0xff) << 8)
                | (int) bi[start - 3] & 0xff;
    }

    public BmpImage readRGB24() {
        int skipWidth = 0;

        if (!(imageWidth * 3 % 4 == 0)) {//图片的宽度不为0
            skipWidth = 4 - imageWidth * 3 % 4;
        }//判断是否后面有补0 的情况
        //装载RGB颜色的数据数组
        imageR = new int[imageHeigh][imageWidth];
        imageG = new int[imageHeigh][imageWidth];
        imageB = new int[imageHeigh][imageWidth];


        //按行读取 如果H,W为正则倒着来
        for (int h = 0; h < imageHeigh; h++) {
            for (int w = imageWidth - 1; w >= 0; w--) {
                //  读入三原色
                try {
                    if (w == 0) {//跳过补0项
                        dis.skipBytes(skipWidth);

                    }

                    int blue = dis.read();
                    int green = dis.read();
                    int red = dis.read();
                    imageB[h][w] = blue;
                    imageG[h][w] = green;
                    imageR[h][w] = red;
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        this.repaint();
        return this;
    }

    private static int colorToRGB(int alpha, int red, int green, int blue) {

        int newPixel = 0;
        newPixel += alpha;
        newPixel = newPixel << 8;
        newPixel += red;
        newPixel = newPixel << 8;
        newPixel += green;
        newPixel = newPixel << 8;
        newPixel += blue;

        return newPixel;

    }

    public BmpImage transformGray() {
        for (int h = 0; h < imageHeigh; h++) {
            for (int w = imageWidth - 1; w >= 0; w--) {
                int color = new Color(imageR[h][w], imageG[h][w], imageB[h][w]).getRGB();
                int r = (color >> 16) & 0xff;
                int g = (color >> 8) & 0xff;
                int b = color & 0xff;
                int gray = (int) (0.3 * r + 0.59 * g + 0.11 * b);
                imageR[h][w] = gray;
                imageG[h][w] = gray;
                imageB[h][w] = gray;
            }
        }
        return this;
    }
    //Nearest Interpolation
    public BmpImage scale(float scaleRatio) {
        int desWidth = (int) (imageWidth * scaleRatio);
        int desHeigh = (int) (imageHeigh * scaleRatio);
        int[][] imageR = new int[desHeigh][desWidth];
        int[][] imageG = new int[desHeigh][desWidth];
        int[][] imageB = new int[desHeigh][desWidth];
        for (int i = 0; i < desHeigh; i++) {
            int tSrcH = (int) ((int) i/scaleRatio + 0.5);
            for (int j = 0; j < desWidth; j++) {
                int tSrcW = (int) ((int) j/scaleRatio + 0.5);
                if (tSrcW >= 0 && tSrcW < imageWidth && tSrcH >= 0 && tSrcH < imageHeigh) {
                    imageR[i][j] = this.imageR[tSrcH][tSrcW];
                    imageG[i][j] = this.imageG[tSrcH][tSrcW];
                    imageB[i][j] = this.imageB[tSrcH][tSrcW];
                } else {
                    imageR[i][j] = 255;
                    imageG[i][j] = 255;
                    imageB[i][j] = 255;
                }
            }
        }
        this.imageR = imageR;
        this.imageG = imageG;
        this.imageB=imageB;
        this.imageHeigh = desHeigh;
        this.imageWidth = desWidth;
        return this;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        for (int h = 0; h < imageHeigh; h++) {
            for (int w = 0; w < imageWidth; w++) {
                g.setColor(new Color(imageR[imageHeigh - h - 1][imageWidth - w - 1]
                        , imageG[imageHeigh - h - 1][imageWidth - w - 1]
                        , imageB[imageHeigh - h - 1][imageWidth - w - 1]));
                g.fillRect(w, h, 1, 1);
            }
        }
    }
}
