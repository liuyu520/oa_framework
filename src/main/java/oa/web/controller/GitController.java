package oa.web.controller;

import oa.web.controller.generic.OSCmdController;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/***
 * 执行本地命令 git pull
 * @author huangwei
 * @date 2016年6月7日
 */
@Controller
@RequestMapping("/git")
public class GitController extends OSCmdController {
    protected static final Logger logger = Logger.getLogger(GitController.class);

    @RequestMapping(value = "/update")
    public String update(HttpServletResponse response, String folder2update, Boolean goBack) throws IOException {
        String commands[] = new String[]{"git", "pull"};
        executeOsCmdAction(response, folder2update, goBack, commands);
        return null;
    }
}
