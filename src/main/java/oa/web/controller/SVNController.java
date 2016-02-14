package oa.web.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cmd.dos.hw.util.CMDUtil;
import com.common.util.SystemHWUtil;
import com.string.widget.util.ValueWidget;
/***
 * 执行本地命令 svn
 * @author Administrator
 * @date 2015年4月24日
 */
@Controller
@RequestMapping("/svn")
public class SVNController {
	@RequestMapping(value = "/update")
	public String update(HttpServletResponse response,String folder2update) throws IOException{
		if(ValueWidget.isNullOrEmpty(folder2update)){
			folder2update="/var/www/ajl/";
		}
		String result=CMDUtil.execute(new String[]{"svn","update" }, folder2update, SystemHWUtil.CURR_ENCODING);
		PrintWriter out=response.getWriter();
		out.println("<html>svn update successfully!<br>");
		out.println(result);
		out.println("<script type=\"text/javascript\" charset=\"UTF-8\" >");
		out.println("setTimeout(function(){history.go(-1);},3000);");
		out.println("</script></html>");
		return null;
	}
}
