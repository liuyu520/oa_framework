package oa.web.upload;

import org.springframework.web.multipart.MultipartFile;

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
	public String callback(MultipartFile file,HttpServletResponse response) throws  ParseException, IOException;
}
