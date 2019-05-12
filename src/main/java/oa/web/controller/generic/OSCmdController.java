package oa.web.controller.generic;

import com.cmd.dos.hw.util.CMDUtil;
import com.common.util.SystemHWUtil;
import com.string.widget.util.ValueWidget;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 执行操作命令
 */
public class OSCmdController {
    protected final Logger logger = Logger.getLogger(this.getClass());

    protected void executeOsCmdAction(HttpServletResponse response, String folder2update, Boolean goBack, String[] commands) throws IOException {
        if (ValueWidget.isNullOrEmpty(folder2update)) {
            folder2update = "/var/www/ajl/";
        }
        logger.info("svn update:" + folder2update);

        String result = null;
        boolean beSuccess = true;
        try {
            result = CMDUtil.execute(commands, folder2update, SystemHWUtil.CURR_ENCODING);
        } catch (IOException e) {
            e.printStackTrace();
            result = e.getMessage();
            beSuccess = false;
        }
//        String result= CMDUtil.execute(commands, folder2update, SystemHWUtil.CURR_ENCODING);
        PrintWriter out = response.getWriter();
        out.println("<html>");
        if (beSuccess) {
            out.println(SystemHWUtil.formatArr(commands, " ") + folder2update + " successfully!<br>");
        }
        out.println(result);
        out.println("<script type=\"text/javascript\" charset=\"UTF-8\" >");
        if (null == goBack || goBack) {
            out.println("setTimeout(function(){history.go(-1);},3000);");
        }
        out.println("</script></html>");
    }
}
