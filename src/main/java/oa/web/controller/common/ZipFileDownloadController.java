package oa.web.controller.common;

import com.common.util.ZipUtil;
import oa.web.controller.generic.GenericController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * Created by 黄威 on 11/01/2017.<br >
 * localhost:8082/convention/download/zip?folderWillZipped=/Users/whuanghkl/work/mygit/xxx/release/android
 */
@Controller
@RequestMapping("/download")
public class ZipFileDownloadController extends GenericController {
    /***
     * 下载zip包
     * @param response
     * @param folderWillZipped
     * @return
     */
    @RequestMapping("/zip")
    public String downloadZip(/*HttpServletRequest request, */HttpServletResponse response, @RequestParam(value = "folderWillZipped", required = true) String folderWillZipped) {
        File folder = null;
        try {
            folder = ZipUtil.compress2response(response, folderWillZipped);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void beforeAddInput(Model model, HttpServletRequest request) {
    }

    @Override
    protected void errorDeal(Model model) {
    }

    @Override
    public String getJspFolder() {
        return null;
    }
}
