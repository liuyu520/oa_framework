package oa.web.upload;

import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;

public interface UploadCallback {
	/***
	 * 
	 * @param file : 上传的文件
	 * @param response
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public String callback(Model model, MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws ParseException, IOException;

    public String callback(Model model, MultipartFile[] files, HttpServletRequest request, HttpServletResponse response) throws ParseException, IOException;
}
