package kyocoolcool.authenticator;

/**
 * @ClassName QRCodeLogoUtil
 * @Description 產出代LogoQRCode
 * @Author Chris Chen
 * @Date 2019-08-13 14:11
 * @Version 1.0
 **/

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QRCodeLogoUtil {
    private static final int QRCOLOR = 0xFF000000;   //默認黑色
    private static final int BGWHITE = 0xFFFFFFFF;   //背影顏色


    public static void main(String[] args) throws WriterException {
        try {
            getLogoQRCode("https://kyocoolcool.nctu.me", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getLogoQRCode(String qrUrl, String productName) {
//      String filePath = (javax.servlet.http.HttpServletRequest)request.getSession().getServletContext().getRealPath("/") + "resources/images/logoImages/llhlogo.png";
        //filePath是二維碼logo的路徑，但是實際上中我們是放在項目的某個路徑下面，所以路徑用上面的，把下面註釋就好
        String filePath = "/opt/logo.PNG";
        String content = qrUrl;
        try {
            QRCodeLogoUtil zp = new QRCodeLogoUtil();
            BufferedImage bim = zp.getQR_CODEBufferedImage(content, BarcodeFormat.QR_CODE, 200, 200, zp.getDecodeHintType());
            return zp.addLogo_QRCode(bim, new File(filePath), new LogoConfig(), productName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String addLogo_QRCode(BufferedImage bim, File logoPic, LogoConfig logoConfig, String productName) {
        try {
            /**
             * 讀取二維碼圖片，並構建繪圖對象
             */
            BufferedImage image = bim;
            Graphics2D g = image.createGraphics();

            /**
             * 讀取Logo圖片
             */
            BufferedImage logo = ImageIO.read(logoPic);
            /**
             * 設置Logo大小,預設為二維碼圖片的20%,因為過大會蓋掉二維碼
             */
            int widthLogo = logo.getWidth(null) > image.getWidth() * 3 / 10 ? (image.getWidth() * 3 / 10) : logo.getWidth(null),
                    heightLogo = logo.getHeight(null) > image.getHeight() * 3 / 10 ? (image.getHeight() * 3 / 10) : logo.getWidth(null);

            /**
             * Logo放在中心
             */
            int x = (image.getWidth() - widthLogo) / 2;
            int y = (image.getHeight() - heightLogo) / 2;
            /**
             * Logo放在右下
             *  int x = (image.getWidth() - widthLogo);
             *  int y = (image.getHeight() - heightLogo);
             */

            //開始繪圖放Logo
            g.drawImage(logo, x, y, widthLogo, heightLogo, null);
//            g.drawRoundRect(x, y, widthLogo, heightLogo, 15, 15);
//            g.setStroke(new BasicStroke(logoConfig.getBorder()));
//            g.setColor(logoConfig.getBorderColor());
//            g.drawRect(x, y, widthLogo, heightLogo);
            g.dispose();

            //把項目名稱添加上去，預設支持兩行，太長會截斷
            if (productName != null && !productName.equals("")) {
                //新的圖片，把帶lgo的二維碼加上文字
                BufferedImage outImage = new BufferedImage(400, 445, BufferedImage.TYPE_4BYTE_ABGR);
                Graphics2D outg = outImage.createGraphics();
                //畫二維碼到新的面板
                outg.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
                //画文字到新的面板
                outg.setColor(Color.BLACK);
                outg.setFont(new Font("宋体", Font.BOLD, 30)); //字體、字型、字號
                int strWidth = outg.getFontMetrics().stringWidth(productName);
                if (strWidth > 399) {
//                  //長度過長就擷取前面
//                  outg.drawString(productName, 0, image.getHeight() + (outImage.getHeight() - image.getHeight())/2 + 5 ); //画文字
                    //長度過長就換行
                    String productName1 = productName.substring(0, productName.length() / 2);
                    String productName2 = productName.substring(productName.length() / 2, productName.length());
                    int strWidth1 = outg.getFontMetrics().stringWidth(productName1);
                    int strWidth2 = outg.getFontMetrics().stringWidth(productName2);
                    outg.drawString(productName1, 200 - strWidth1 / 2, image.getHeight() + (outImage.getHeight() - image.getHeight()) / 2 + 12);
                    BufferedImage outImage2 = new BufferedImage(400, 485, BufferedImage.TYPE_4BYTE_ABGR);
                    Graphics2D outg2 = outImage2.createGraphics();
                    outg2.drawImage(outImage, 0, 0, outImage.getWidth(), outImage.getHeight(), null);
                    outg2.setColor(Color.BLACK);
                    outg2.setFont(new Font("宋体", Font.BOLD, 30)); //字體、字型、字號
                    outg2.drawString(productName2, 200 - strWidth2 / 2, outImage.getHeight() + (outImage2.getHeight() - outImage.getHeight()) / 2 + 5);
                    outg2.dispose();
                    outImage2.flush();
                    outImage = outImage2;
                } else {
                    outg.drawString(productName, 200 - strWidth / 2, image.getHeight() + (outImage.getHeight() - image.getHeight()) / 2 + 12); //画文字
                }
                outg.dispose();
                outImage.flush();
                image = outImage;
            }
            logo.flush();
            image.flush();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.flush();
            ImageIO.write(image, "png", baos);

            //二維碼生成的路徑，但是實際項目中，我們是把這生成的二維碼顯示到介面上，因此下面的代碼可以註釋掉
            //可以看到這個方法最終返回的二維碼imageBase64字符串
            //前端用 <img src="data:image/png;base64,${imageBase64QRCode}"/>  其中${imageBase64QRCode}對應二維碼的imageBase64字串
            ImageIO.write(image, "png", new File("/opt/" + new Date().getTime() + "test.png")); //TODO

            String imageBase64QRCode = Base64.encodeBase64URLSafeString(baos.toByteArray());

            baos.close();
            return imageBase64QRCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 構建初始化二維碼
     *
     * @param bm
     * @return
     */
    public BufferedImage fileToBufferedImage(BitMatrix bm) {
        BufferedImage image = null;
        try {
            int w = bm.getWidth(), h = bm.getHeight();
            image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    image.setRGB(x, y, bm.get(x, y) ? 0xFF000000 : 0xFFCCDDEE);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * 生成二維碼bufferImage圖片
     *
     * @param content       編碼內容
     * @param barcodeFormat 編碼類型
     * @param width         生成圖片寬度
     * @param height        生成圖片高度
     * @param hints         設置參數
     * @return
     */
    public BufferedImage getQR_CODEBufferedImage(String content, BarcodeFormat barcodeFormat, int width, int height, Map<EncodeHintType, ?> hints) {
        MultiFormatWriter multiFormatWriter = null;
        BitMatrix bm = null;
        BufferedImage image = null;
        try {
            multiFormatWriter = new MultiFormatWriter();
            bm = multiFormatWriter.encode(content, barcodeFormat, width, height, hints);
            int w = bm.getWidth();
            int h = bm.getHeight();
            image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

            // 開始利用二維碼數據創建Bitmap圖片，分別設為黑（0xFFFFFFFF）白（0xFF000000）
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    image.setRGB(x, y, bm.get(x, y) ? QRCOLOR : BGWHITE);
                }
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * 設置二維碼的格式參數
     *
     * @return
     */
    public Map<EncodeHintType, Object> getDecodeHintType() {
        // 用於設置QR二維碼參數
        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
        // 設置QR二維碼的糾錯級別（H為最高級別）具體級別訊息
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        // 設置編碼方式
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN, 0);
        hints.put(EncodeHintType.MAX_SIZE, 350);
        hints.put(EncodeHintType.MIN_SIZE, 100);

        return hints;
    }
}

class LogoConfig {
    // logo默認編框顏色
    public static final Color DEFAULT_BORDERCOLOR = Color.WHITE;
    // logo默認編框寬度
    public static final int DEFAULT_BORDER = 2;
    // logo大小默認為照片的1/5
    public static final int DEFAULT_LOGOPART = 5;

    private final int border = DEFAULT_BORDER;
    private final Color borderColor;
    private final int logoPart;


    public LogoConfig() {
        this(DEFAULT_BORDERCOLOR, DEFAULT_LOGOPART);
    }

    public LogoConfig(Color borderColor, int logoPart) {
        this.borderColor = borderColor;
        this.logoPart = logoPart;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public int getBorder() {
        return border;
    }

    public int getLogoPart() {
        return logoPart;
    }
}