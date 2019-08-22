package kyocoolcool.authenticator;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * @ClassName QRCodeUtil
 * @Description QUCode工具類
 * @Author Chris Chen
 * @Date 2019-07-25 13:56
 * @Version 1.0
 **/
public class QRCodeUtil {
    private static final int width = 300;// 默認二維碼寬度
    private static final int height = 300;// 默認二維碼高度
    private static final String format = "png";// 默認二維碼文件格式
    private static final HashMap hints;// 二維碼參數

    static {
        hints = new HashMap();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");// 編碼
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);// 容錯等級 L、M、Q、H 其中 L 為最低, H 為最高
        hints.put(EncodeHintType.MARGIN, 2);// 二為碼圖片編距
    }

    /**
     * @param content 二維碼內容
     * @param width   寬
     * @param height  高
     * @description: 返回 BufferedImage 對象
     * @return: java.awt.image.BufferedImage
     * @author: Chris
     * @time: 2019/7/26 下午 02:53
     */
    public static BufferedImage toBufferedImage(String content, int width, int height) throws WriterException, IOException {
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }


    /**
     * @param content 二維碼內容
     * @param stream  輸出流
     * @param width   寬
     * @param height  高
     * @description: 將二維碼圖片輸出到stream中
     * @return: void
     * @author: Chris
     * @time: 2019/7/26 下午 02:54
     */
    public static void writeToStream(String content, OutputStream stream, int width, int height) throws WriterException, IOException {
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        MatrixToImageWriter.writeToStream(bitMatrix, format, stream);
    }


    /**
     * @description: 生成二維碼圖片文件
     * @param content 二維碼內容
     * @param path 文件保存路徑
     * @param width 寬
     * @param height 高
     * @return: void
     * @author: Chris
     * @time: 2019/7/26 下午 02:55
     */
    public static void createQRCode(String content, String path, int width, int height) throws WriterException, IOException {
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        MatrixToImageWriter.writeToPath(bitMatrix, format, new File(path).toPath());
    }

}