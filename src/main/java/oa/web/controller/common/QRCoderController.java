package oa.web.controller.common;

import com.common.util.QRCodeUtil;
import com.common.util.SystemHWUtil;
import com.google.zxing.WriterException;
import com.string.widget.util.RandomUtils;
import com.string.widget.util.ValueWidget;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;

/***
 * 用于生成二维码
 * @author huangwei
 * @since 2014年9月7日
 */
@Controller
@Scope(value = "prototype")
@RequestMapping("/qrcode")
public class QRCoderController {
    /***
     * 生成二维码,返回二级制(图片)
     * @param info
     * @param response
     * @param size : 像素
     * @return
     * @throws IOException
     * @throws WriterException
     */
    @RequestMapping(value = "/create")
    public String createQR(String info, HttpServletRequest request, HttpServletResponse response, HttpSession session, Integer size) throws IOException, WriterException {
        if (ValueWidget.isNullOrEmpty(info)) {
            info = "请传递info参数";
        } else {
            info = (String) session.getAttribute(info);
        }

        if (ValueWidget.isNullOrEmpty(info)) {//非空判断
            return null;
        }
        createQRCodePicture(info, response, size);
        return null;

    }

    @RequestMapping(value = "/cre", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JPEG)
    public String createQRByMessage(String message, HttpServletRequest request, HttpServletResponse response, HttpSession session, Integer size) throws IOException, WriterException {
        if (ValueWidget.isNullOrEmpty(message)) {
            message = "请传递message参数";
        }

        createQRCodePicture(message, response, size);
        return null;

    }

    public static void createQRCodePicture(String info, HttpServletResponse response, Integer size) throws WriterException, IOException {
        if (size == null || size < 10) {
            size = 300;//像素
        }
        byte[] byrtes = QRCodeUtil.encode(info, size);
        response.setContentType(SystemHWUtil.RESPONSE_CONTENTTYPE_JPEG);
        response.addHeader("Content-Disposition", "inline;filename=\"qrcode" + RandomUtils.getTimeRandom2() + ".jpg\"");
        OutputStream out = response.getOutputStream();
//		byte[] reBytes = FileUtils.getByteArrayInputSreamFromByteArr(byrtes);
        out.write(byrtes);
        out.close();
    }

    @RequestMapping(value = "/show")
    public String showQR(String info, Model model, HttpSession session) {
        String infoKey = "info" + RandomUtils.getTimeRandom2();
        session.setAttribute(infoKey, info);
//		model.addAttribute("qrPicPath", "qrcode/create?info="+infoKey);
        model.addAttribute("info_key", infoKey);
        return "qrcode/qrcode";
    }
}
