package oa.util;

import com.common.util.SystemHWUtil;
import com.common.util.WebServletUtil;
import com.io.hw.file.util.FileUtils;
import com.string.widget.util.RandomUtils;
import com.string.widget.util.ValueWidget;
import net.sf.jxls.transformer.XLSTransformer;
import oa.bean.stub.ReadAndWriteResult;
import org.apache.commons.compress.archivers.dump.InvalidFormatException;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.ser.FilterProvider;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class HWUtils {
	protected static Logger logger=Logger.getLogger(HWUtils.class);
	private static ObjectMapper mapper = null;

	public static ObjectMapper getObjectMapper(){
		if(mapper==null){
			mapper = new ObjectMapper();
		}
		return mapper;
	}
	/***
	 * 用于jsonp调用
	 * @param map : 用于构造json数据
	 * @param callback : 回调的javascript方法名
	 * @param filters : <code>SimpleBeanPropertyFilter theFilter = SimpleBeanPropertyFilter
				.serializeAllExcept("content");
		FilterProvider filters = new SimpleFilterProvider().addFilter(
				Constant2.SIMPLEFILTER_JACKSON_PAPERNEWS, theFilter);</code>
	 * @return : js函数名(json字符串)
	 */
	public static String getJsonP(Object map,String callback,FilterProvider filters)
	{
		if(ValueWidget.isNullOrEmpty(map)){
			return null;
		}
		String content = null;
		if(map instanceof String){
			content=(String)map;
		}else{
		ObjectMapper mapper = getObjectMapper();
		
		ObjectWriter writer=null;
		try {
			if(filters!=null){
				writer=mapper.writer(filters);
			}else{
				writer=mapper.writer();
			}
			content=writer.writeValueAsString(map);
			logger.info(content);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		}
		if(ValueWidget.isNullOrEmpty(callback)){
			return content;
		}
		return callback+"("+content+");";
	}
	/***
	 * jackson没有过滤
	 * @param map
	 * @param callback
	 * @return
	 */
	public static String getJsonP(Object map,String callback)
	{
		return getJsonP(map, callback, null);
	}
	public static String getJsonP(Object map)
	{
		return getJsonP(map, null, null);
	}
	/***
	 * 
	 * @param key
	 * @param value2
	 * @param callback
	 * @return : js函数名(json字符串)
	 */
	public static String getJsonP(String key ,Object value2,String callback){
		Map map = new HashMap();
		map.put(key, value2);
		return getJsonP(map, callback);
	}

	public static void buildExcel(Map<String, Object> model,
			HttpServletResponse resp, String templatePath) throws IOException,
			InvalidFormatException, org.apache.poi.openxml4j.exceptions.InvalidFormatException {
		org.apache.poi.ss.usermodel.Workbook workbook = getTemplateSource(templatePath);
		XLSTransformer transformer = new XLSTransformer();
		transformer.transformWorkbook(workbook, model);
		resp.setContentType("application/vnd.ms-excel");
		// 设置下载文件生成的文件名称
		resp.setHeader("Content-Disposition", "attachment;filename="
				+ RandomUtils.getTimeRandom2()+".xls");
		// Flush byte array to servlet output stream.
		ServletOutputStream out = resp.getOutputStream();
		workbook.write(out);
		out.flush();
	}
	/**
	 *
	 * @param path
	 * @return
	 * @author lianrao
	 * @throws IOException
	 * @throws InvalidFormatException
	 * @throws org.apache.poi.openxml4j.exceptions.InvalidFormatException 
	 */
	public static Workbook getTemplateSource(String path) throws IOException,
			InvalidFormatException, org.apache.poi.openxml4j.exceptions.InvalidFormatException {
		DefaultResourceLoader dl = new DefaultResourceLoader();
		Resource resource = dl.getResource(path);
		InputStream inputStream = resource.getInputStream();
		Workbook workbook = WorkbookFactory.create(inputStream);
		return workbook;
	}

	/***
	 * 读取文件
	 *
	 * @param request
	 * @param path
	 * @param charset
	 * @return
	 */
	public static ReadAndWriteResult stub(HttpServletRequest request, String path, String charset) {
		String content = null;
		ReadAndWriteResult readAndWriteResult = new ReadAndWriteResult();
		try {
			String realPath2 = WebServletUtil.getRealPath(request, path);
			File file = new File(realPath2);
			if (!file.exists()) {
				return fileNotExistReadAndWriteResult(readAndWriteResult, realPath2);
			}
			java.io.InputStream input = new FileInputStream(realPath2);
			if (null == input) {
				return fileNotExistReadAndWriteResult(readAndWriteResult, realPath2);
			}
			content = FileUtils.getFullContent2(input, charset, true);
			readAndWriteResult.setContent(content);
			readAndWriteResult.setSuccess(true);
		} catch (java.io.FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return readAndWriteResult;
	}

	private static ReadAndWriteResult fileNotExistReadAndWriteResult(ReadAndWriteResult readAndWriteResult, String realPath2) {
		readAndWriteResult.setSuccess(false);
		readAndWriteResult.setErrorMessage("文件" + realPath2 + "不存在");
		readAndWriteResult.setContent(SystemHWUtil.EMPTY);
		return readAndWriteResult;
	}

	public static ReadAndWriteResult saveStub(HttpServletRequest request, String path, String content, String charset) {
		ReadAndWriteResult readAndWriteResult = new ReadAndWriteResult();
		if (ValueWidget.isNullOrEmpty(content)) {
			readAndWriteResult.setErrorMessage("内容为空");
			return readAndWriteResult;
		}
		try {
			String realPath2 = WebServletUtil.getRealPath(request, path);
			File file = new File(realPath2);
			if (file.exists()) {
				FileWriterWithEncoding fileW = new FileWriterWithEncoding(file, charset);
				fileW.write(content);
				fileW.close();
				readAndWriteResult.setSuccess(true);
				readAndWriteResult.setContent(content);
			} else {
				logger.error("文件" + realPath2 + "不存在");
				return fileNotExistReadAndWriteResult(readAndWriteResult, realPath2);
			}

		} catch (java.io.FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return readAndWriteResult;
	}

	public static ReadAndWriteResult stub(HttpServletRequest request, String path) {
		return HWUtils.stub(request, path, SystemHWUtil.CURR_ENCODING);
	}

}
