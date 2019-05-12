package oa.web.controller;

import com.string.widget.util.ValueWidget;
import oa.web.controller.generic.OSCmdController;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/***
 * 执行本地命令 svn
 * @author Administrator
 * @date 2015年4月24日
 */
@Controller
@RequestMapping("/svn")
public class SVNController extends OSCmdController {
    protected static final Logger logger = Logger.getLogger(SVNController.class);

    @RequestMapping(value = "/update")
    public String update(HttpServletResponse response, String folder2update, Boolean goBack) throws IOException {
        if (ValueWidget.isNullOrEmpty(folder2update)) {
			folder2update="/var/www/ajl/";
		}
        String commands[] = new String[]{"ps", "-ef", "|grep ", "tomcat"};
        executeOsCmdAction(response, folder2update, goBack, commands);
		return null;
	}
}
