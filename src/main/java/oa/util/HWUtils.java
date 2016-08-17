package oa.util;

import com.common.dict.Constant2;
import com.common.util.SystemHWUtil;
import com.common.util.WebServletUtil;
import com.io.hw.file.util.FileUtils;
import com.string.widget.util.RandomUtils;
import com.string.widget.util.RegexUtil;
import com.string.widget.util.ValueWidget;
import com.time.util.TimeHWUtil;
import net.sf.jxls.transformer.XLSTransformer;
import oa.bean.StubRange;
import oa.bean.UploadResult;
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
import org.springframework.web.multipart.MultipartFile;
import yunma.oa.bean.xml.XmlYunmaUtil;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class HWUtils {
	public static final String SESSION_KEY_STUB_OLD_CONTENT = "stub_old_content";
    protected static final Logger logger = Logger.getLogger(HWUtils.class);
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
			readAndWriteResult.setAbsolutePath(escapePath(realPath2));
			String pathTmp = null;
			if (realPath2.endsWith(Constant2.stub_file_Suffix)) {
				pathTmp = realPath2;
			} else {
				pathTmp = realPath2 + Constant2.stub_file_Suffix;
			}
			File file = new File(pathTmp);
			if (!file.exists()) {
				String errorMessage = pathTmp + " does not exist";
				System.out.println(errorMessage);
				logger.error(errorMessage);
				//兼容appList.do.json 文件名
				file = new File(realPath2 + ".do" + Constant2.stub_file_Suffix);
			}
			realPath2 = file.getAbsolutePath();
			if (!file.exists()) {
				return fileNotExistReadAndWriteResult(readAndWriteResult, realPath2);
			}
			java.io.InputStream input = new FileInputStream(file);
			if (null == input) {
				return fileNotExistReadAndWriteResult(readAndWriteResult, realPath2);
			}
			content = FileUtils.getFullContent2(input, charset, true);
            //反序列化
            StubRange stubRange = XmlYunmaUtil.deAssembleStub(content);
            content = stubRange.getStubs().get(stubRange.getSelectedIndex());
            setServletUrl(request, path, readAndWriteResult);
			readAndWriteResult.setContent(content);
            readAndWriteResult.setStubRange(stubRange);
            readAndWriteResult.setResult(true);
		} catch (java.io.FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return readAndWriteResult;
	}

	/***
	 * 文件不存在
	 *
	 * @param readAndWriteResult
	 * @param realPath2
	 * @return
	 */
	private static ReadAndWriteResult fileNotExistReadAndWriteResult(ReadAndWriteResult readAndWriteResult, String realPath2) {
		readAndWriteResult.setResult(false);
		String errorMessage = "文件" + escapePath(realPath2) + "不存在";
		readAndWriteResult.setErrorMessage(errorMessage);
		System.out.println(errorMessage);
		readAndWriteResult.setContent(SystemHWUtil.EMPTY);
		return readAndWriteResult;
	}

	private static String escapePath(String realPath2) {
		return realPath2.replace("\\", "\\\\");
	}

	/***
	 * 文件已经存在
	 * @param readAndWriteResult
	 * @param realPath2
	 * @return
	 */
	private static ReadAndWriteResult fileHasExistReadAndWriteResult(ReadAndWriteResult readAndWriteResult, String realPath2) {
		readAndWriteResult.setResult(false);
		String errorMessage = "文件" + escapePath(realPath2) + "已经存在";
		readAndWriteResult.setErrorMessage(errorMessage);
		System.out.println(errorMessage);
		readAndWriteResult.setContent(SystemHWUtil.EMPTY);
		return readAndWriteResult;
	}

	/***
	 * 更新stub json文件<br />
	 * 若文件不存在,则不更新
	 *
	 * @param request
	 * @param path
	 * @param content
	 * @param charset
	 * @return
	 */
    public static ReadAndWriteResult updateStub(HttpServletRequest request, String path, String content, String charset, int index) {
        ReadAndWriteResult readAndWriteResult = new ReadAndWriteResult();
		if (ValueWidget.isNullOrEmpty(content)) {
			readAndWriteResult.setErrorMessage("内容为空");
			return readAndWriteResult;
		}
		try {
			String realPath2 = WebServletUtil.getRealPath(request, path);
			readAndWriteResult.setAbsolutePath(escapePath(realPath2));
			File file = new File(realPath2);
			if (file.exists()) {
				String contentOld = FileUtils.getFullContent2(file, charset, true/*isCloseStream*/);
				if (content.equals(contentOld)) {
					readAndWriteResult.setResult(false);
					readAndWriteResult.setErrorMessage("无变化");
					readAndWriteResult.setContent(contentOld);
					return readAndWriteResult;
				}
				setServletUrl(request, path, readAndWriteResult);
                writeStubFile(content, charset, readAndWriteResult, file, index);
                HttpSession session = request.getSession(true);
				logger.debug("update sessionId:" + session.getId());
				session.setAttribute(SESSION_KEY_STUB_OLD_CONTENT, contentOld);
				readAndWriteResult.setTips("更新成功");
			} else {
				String errorMessage = "文件" + realPath2 + "不存在";
				logger.error(errorMessage);
				System.out.println(errorMessage);
				return fileNotExistReadAndWriteResult(readAndWriteResult, realPath2);
			}

		} catch (java.io.FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return readAndWriteResult;
	}

	/***
	 * 写入文件<br />
	 *	已关闭输出流
	 * @param content
	 * @param charset
	 * @param readAndWriteResult
	 * @param file
	 * @throws IOException
	 */
    private static void writeStubFile(String content, String charset, ReadAndWriteResult readAndWriteResult, File file, int index) throws IOException {
        FileWriterWithEncoding fileW = new FileWriterWithEncoding(file, charset);
//        StubRange stubRange = XmlYunmaUtil.deAssembleStub(content);
        fileW.write(content);
		fileW.close();
		readAndWriteResult.setResult(true);
		readAndWriteResult.setContent(content);
	}

	/***
	 * 新增一个新的接口
	 * 若文件已经存在则报错
	 * @param request
	 * @param path
	 * @param content
	 * @param charset
	 * @return
	 */
    public static ReadAndWriteResult saveStub(HttpServletRequest request, String path, String content, String charset, int index) {
        ReadAndWriteResult readAndWriteResult = new ReadAndWriteResult();
		if (ValueWidget.isNullOrEmpty(content)) {
			readAndWriteResult.setErrorMessage("内容为空");
			return readAndWriteResult;
		}
		try {
			String realPath2 = WebServletUtil.getRealPath(request, path);//父目录可能不存在
			String parent = SystemHWUtil.getParentDir(realPath2);
			File parentFile = new File(parent);
			if (!parentFile.exists()) {
				parentFile.mkdirs();
			}
			readAndWriteResult.setAbsolutePath(escapePath(realPath2));
			File file = new File(realPath2);
			if (file.exists()) {
				String errorMessage = "文件" + realPath2 + "已经存在";
				logger.error(errorMessage);
				System.out.println(errorMessage);
				return fileHasExistReadAndWriteResult(readAndWriteResult, realPath2);
			} else {
				setServletUrl(request, path, readAndWriteResult);
                writeStubFile(content, charset, readAndWriteResult, file, index);
                readAndWriteResult.setTips("添加成功");
			}

		} catch (java.io.FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return readAndWriteResult;
	}

	private static void setServletUrl(HttpServletRequest request, String path, ReadAndWriteResult readAndWriteResult) {
		String serverUrl = getServletUrl(request);//http://10.1.253.44:81/tv_mobile
		logger.info("serverUrl:" + serverUrl);
		readAndWriteResult.setUrl(serverUrl + Constant2.Slash + path.replaceAll("\\.json$"/*需要转义，否则就是通配符*/, SystemHWUtil.EMPTY));
	}

	/***
	 * @param request
	 * @return : http://10.1.253.44:81/tv_mobile
	 */
	public static String getServletUrl(HttpServletRequest request) {
		return request.getRequestURL().toString().replaceAll("(https?://[^/]+)/.*$", "$1") + request.getContextPath();
	}

	public static ReadAndWriteResult stub(HttpServletRequest request, String path) {
		return HWUtils.stub(request, path, SystemHWUtil.CURR_ENCODING);
	}

	public static List<String> listStubServletPath(String rootPath) {
		return listStubServletPath(rootPath, null);
	}
	/***
	 * 列出所有的stub 接口
	 *
	 * @param rootPath
	 * @return
	 */
	public static List<String> listStubServletPath(String rootPath, String keyWord) {
		List<File> files = FileUtils.getListFiles(rootPath, "json");
		List<String> pathList = new ArrayList<String>();
		for (int i = 0; i < files.size(); i++) {
			String interface2 = files.get(i).getAbsolutePath().replace(rootPath, "");
			interface2 = interface2.replace("\\", "/").replaceAll(Constant2.stub_file_Suffix+"$", "");
//			System.out.println(interface2);
			if (null == keyWord || interface2.contains(keyWord)) {
				pathList.add(interface2);
			}
		}
		return pathList;
	}

	/***
	 * @param request
	 * @param relativePath
	 * @param finalFileName
	 * @return
	 */
	public static String getRelativeUrl(HttpServletRequest request, String relativePath, String finalFileName) {
		String rootPath = request.getContextPath();
		if (!rootPath.endsWith("/")) {
			rootPath = rootPath + "/";
		}
		if (relativePath.endsWith("/")) {
			relativePath = getRelativePath(relativePath, finalFileName);
		}
		return rootPath + relativePath;
	}

	public static String getRelativePath(String relativePath, String finalFileName) {
		if (!relativePath.endsWith("/")) {
			relativePath = relativePath + "/";
		}
		if (relativePath.endsWith("/")) {
			relativePath = relativePath + finalFileName;//upload/image/20150329170823_2122015-03-23_01-42-03.jpg
		}
		return relativePath;
	}

	public static String getFullUrl(HttpServletRequest request, String relativePath, String finalFileName) {
		String fullUrl;
		String prefixPath = request.getRequestURL().toString().replaceAll(request.getServletPath(), "");
		if (!prefixPath.endsWith("/") && (!relativePath.startsWith("/"))) {
			prefixPath = prefixPath + "/";
		}
        if (relativePath.endsWith("/")) {
            relativePath = relativePath + finalFileName;//upload/image/20150329170823_2122015-03-23_01-42-03.jpg
        }
        fullUrl = prefixPath + relativePath;
		return fullUrl;
	}

    public static UploadResult getSavedToFile(HttpServletRequest request, String fileName, String uploadFolder, boolean sameFileName) {
        fileName = RegexUtil.filterBlank(fileName);//IE中识别不了有空格的json
        // 保存到哪儿
        String prefix = TimeHWUtil.formatDateByPattern(TimeHWUtil
                .getCurrentTimestamp(), TimeHWUtil.yyyyMMddHHmmss.replace("-", SystemHWUtil.EMPTY)
                .replace(":", SystemHWUtil.EMPTY).replace(SystemHWUtil.BLANK, SystemHWUtil.EMPTY)) + "_" + new Random().nextInt(1000) + "_";
        String finalFileName = null;
        if (sameFileName) {
            finalFileName = "upload_";
        } else {
            finalFileName = prefix;
        }
        finalFileName = finalFileName + fileName;
        String relativePath = null;
		if (ValueWidget.isNullOrEmpty(uploadFolder)) {
			relativePath = Constant2.UPLOAD_FOLDER_NAME + "/image";
		} else {
			relativePath = uploadFolder;
		}
		UploadResult uploadResult = new UploadResult();
		File savedFile = WebServletUtil.getUploadedFilePath(request, relativePath
				, finalFileName,
				Constant2.SRC_MAIN_WEBAPP);
		uploadResult.setSavedFile(savedFile);
        String relativeUrl = HWUtils.getRelativePath(relativePath, finalFileName);
        if (!relativeUrl.startsWith("/")) {//uploadResult中的RelativePath 必须以斜杠开头
            relativeUrl = "/" + relativeUrl;
        }
        uploadResult.setRelativePath(relativeUrl);
        uploadResult.setFinalFileName(finalFileName);
		return uploadResult;
	}

    /***
     *
     * @param file
     * @param request
     * @param deleteOldFile : 是否删除原文件
     * @param sameFileName : 文件名是否动态改变(加上时间戳就会动态改变)
     * @return
     */
    public static Map getUploadResultMap(MultipartFile file, HttpServletRequest request, boolean sameFileName, boolean deleteOldFile) {
        String fileName = file.getOriginalFilename();// 上传的文件名
        fileName = RegexUtil.filterBlank(fileName);//IE中识别不了有空格的json

        UploadResult uploadResult = HWUtils.getSavedToFile(request, fileName, null, sameFileName);
        File savedFile = uploadResult.getSavedFile();
        File parentFolder = SystemHWUtil.createParentFolder(savedFile);
        FileUtils.makeWritable(parentFolder);//使...可写
        System.out.println("[upload]savedFile:"
                + savedFile.getAbsolutePath());
        //如果文件已经存在,则先删除
        if (deleteOldFile && savedFile.exists()) {
            System.out.println("删除 " + savedFile.getAbsolutePath());
            savedFile.delete();
        }
        // 保存
        try {
            file.transferTo(savedFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
        String url2 = HWUtils.getRelativeUrl(request, uploadResult.getRelativePath(), uploadResult.getFinalFileName());
        String fullUrl = null;//http://localhost:8080/tv_mobile/upload/image/20150329170823_2122015-03-23_01-42-03.jpg
        /***
         * request.getRequestURL():http://localhost:8081/SSLServer/addUser.security<br>
         * request.getServletPath():/addUser.security<br>
         * prefixPath:http://localhost:8080/tv_mobile/
         */
        fullUrl = HWUtils.getFullUrl(request, uploadResult.getRelativePath(), uploadResult.getFinalFileName());
        Map map = new HashMap();

        map.put("fileName", uploadResult.getFinalFileName());
        map.put("remoteAbsolutePath", savedFile.getAbsolutePath());
        map.put("url", url2);
        map.put("fullUrl", fullUrl);
        map.put("relativePath", uploadResult.getRelativePath());
        map.put("imgTag", ValueWidget.escapeHTML(getHtmlImgTag(fullUrl)));
        return map;
    }

    public static String getHtmlImgTag(String fullUrl) {
        return "<img style=\"max-width: 99%\" src=\"" + fullUrl + "\" alt=\"\">";
    }

}
