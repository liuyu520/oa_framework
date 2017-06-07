package oa.web.controller.common;

import com.common.util.SystemHWUtil;
import oa.util.HWUtils;
import oa.web.upload.UploadCallback;
import org.apache.log4j.Logger;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;

/***
 * 用于上传restful 工具的配置文件<br />
 * see com/yunma/autotest/AutoTestPanel.java 中的uploadConfigFile2()方法<br />
 * 被 /frameworkLinkDB/src/main/java/oa/web/controller/common/UploadConfigController.java
 * 调用
 */
public class ConfigUploadCallback implements UploadCallback {
    protected final static Logger logger = Logger.getLogger(ConfigUploadCallback.class);


    @Override
    public String callback(Model model, MultipartFile file, HttpServletRequest request, HttpServletResponse response)
            throws ParseException, IOException {
        String content = HWUtils.uploadFileSameFileName(model, file, request/*, sameFileName*/);
         /*response.setCharacterEncoding(SystemHWUtil.CHARSET_UTF);//必不可少,要不然中文乱码
         PrintWriter writer=response.getWriter();
		 writer.write(content);
		 writer.close();*/
        response.setCharacterEncoding(SystemHWUtil.CHARSET_UTF);
        response.setContentType(SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF);
        PrintWriter out = response.getWriter();
        out.print(content);
        out.flush();
        return null;
    }


    @Override
    public String callback(Model model, MultipartFile[] files, HttpServletRequest request, HttpServletResponse response) throws ParseException, IOException {
        return null;
    }

}
