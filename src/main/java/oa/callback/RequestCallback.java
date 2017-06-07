package oa.callback;

import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;

/**
 * Created by whuanghkl on 17/4/8.<br />
 * 用于对控制器进行面向切面的编程
 */
public interface RequestCallback {
    String callback(Model model, HttpServletRequest request, HttpServletResponse response, int requestType) throws ParseException, IOException;

}
