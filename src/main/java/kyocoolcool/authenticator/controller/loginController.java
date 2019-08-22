package kyocoolcool.authenticator.controller;

import com.google.zxing.WriterException;
import kyocoolcool.authenticator.GoogleAuthenticatorUtil;
import kyocoolcool.authenticator.QRCodeUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


/**
 * @ClassName loginController
 * @Description TODO
 * @Author Chris Chen
 * @Date 2019-08-12 16:52
 * @Version 1.0
 **/
@Controller
public class loginController {
    @RequestMapping("/hello")
    public String hello(){
        return "index.html";
    }
    @ResponseBody
    @RequestMapping("/getQRCode")
    public void getQRCode(HttpServletResponse response) throws IOException, WriterException {
        String securityKey = GoogleAuthenticatorUtil.genSecret();
        QRCodeUtil.writeToStream("otpauth://totp/chris@WebAPIS?secret=" + securityKey,response.getOutputStream() ,300 ,300 );
    }
}