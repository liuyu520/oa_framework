package oa.web.controller.common;

import com.common.dict.Constant2;
import com.common.util.SystemHWUtil;
import com.string.widget.util.ValueWidget;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;

@Controller
@RequestMapping("/bone")
public class ShellExeController {
    private StringBuffer buffer = new StringBuffer();
    public static final String session_key_workdir = "workdir";

    /***
     *
     * @param session
     * @param request
     * @param response
     * @param callback
     * @param cmd
     * @param encoding
     * @param intoDir :1:进入子目录
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/shell", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_PLAIN_UTF)
    public String execute(HttpSession session, HttpServletRequest request
            , HttpServletResponse response, String callback
            , String cmd, String encoding, String intoDir) {
        buffer.setLength(0);//清空结果
        if (ValueWidget.isNullOrEmpty(encoding)) {
            encoding = SystemHWUtil.CURR_ENCODING;
        }
        System.out.println("localCmd:" + cmd);
        if (cmd.equals("pwd") && SystemHWUtil.isWindows) {/*windows 没有“pwd” 命令*/
            cmd = "cd";
        }
        if (cmd.startsWith("cd") && cmd.split("[\\s]").length > 1) {//修改当前工作目录
            String[] strs = cmd.split("[\\s]");
            String workDirTmep = strs[1];
            File file = null;
            if (intoDir != null && intoDir.equals("1")) {
                file = new File(getWorkDir(session), workDirTmep);
            } else {
                file = new File(workDirTmep);
            }
            boolean result = false;
            if (file.exists() && file.isDirectory()) {
                setWorkDir(session, file.getAbsolutePath());
                System.out.println("new work dir:" + workDirTmep);
                result = true;
            }
            return workDirTmep + SystemHWUtil.COLON + result;
        }
        if (SystemHWUtil.isWindows) {
            cmd = Constant2.FIX_PREFIX_COMMAND + cmd;
        }
        System.out.println("执行的命令:" + cmd);
        String[] commands = cmd.split("[ \t]");
        ProcessBuilder pb = new ProcessBuilder();
        String workdir = getWorkDir(session);
        System.out.println("workdir:" + workdir);
        if (!ValueWidget.isNullOrEmpty(workdir)) {
            pb.directory(new File(workdir));
        }
        pb.command(commands);
        int tmp = 0;
        BufferedReader br_right = null;
        BufferedReader br_error = null;
        try {
            Process process = pb.start();
            InputStream input_right = process.getInputStream();
            InputStream input_error = process.getErrorStream();

            try {
                br_right = new BufferedReader(new InputStreamReader(
                        input_right, encoding), 4096);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                br_error = new BufferedReader(new InputStreamReader(
                        input_error, encoding), 4096);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            char word;
            while ((tmp = br_right.read()) != -1) {
                word = (char) tmp;
                publish(word);
            }
            while ((tmp = br_error.read()) != -1) {
                word = (char) tmp;
                publish(word);
            }
            if (!ValueWidget.isNullOrEmpty(br_right)) {
                try {
                    br_right.close();
                    br_error.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return buffer.toString();
    }

    private void publish(char word) {
        buffer.append(word);
    }

    /***
     * 获取工作目录
     * @param session
     * @return
     */
    private String getWorkDir(HttpSession session) {
        Object workdir = session.getAttribute(session_key_workdir);
        if (ValueWidget.isNullOrEmpty(workdir)) {
            return System.getProperty("user.dir");
        } else {
            return (String) workdir;
        }
    }

    /***
     * 设置工作目录
     * @param session
     * @param workDir
     */
    private void setWorkDir(HttpSession session, String workDir) {
        session.setAttribute(session_key_workdir, workDir);
    }

}

