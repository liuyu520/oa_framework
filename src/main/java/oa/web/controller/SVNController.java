package oa.web.controller;

import com.cmd.dos.hw.util.CMDUtil;
import com.common.util.SystemHWUtil;
import com.string.widget.util.ValueWidget;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
/***
 * 执行本地命令 svn
 * @author Administrator
 * @date 2015年4月24日
 */
@Controller
@RequestMapping("/svn")
public class SVNController {
    protected static final Logger logger = Logger.getLogger(SVNController.class);

    @RequestMapping(value = "/update")
	public String update(HttpServletResponse response,String folder2update) throws IOException{
		if(ValueWidget.isNullOrEmpty(folder2update)){
			folder2update="/var/www/ajl/";
		}
		logger.info("svn update:" + folder2update);
		String result=CMDUtil.execute(new String[]{"svn","update" }, folder2update, SystemHWUtil.CURR_ENCODING);
		PrintWriter out=response.getWriter();
		out.println("<html>svn update " + folder2update + " successfully!<br>");
		out.println(result);
		out.println("<script type=\"text/javascript\" charset=\"UTF-8\" >");
		out.println("setTimeout(function(){history.go(-1);},3000);");
		out.println("</script></html>");
		return null;
	}
}
