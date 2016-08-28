package oa.util;

import com.common.bean.StubRange;
import com.common.dict.Constant2;
import com.common.util.SystemHWUtil;
import com.common.util.WebServletUtil;
import com.io.hw.file.util.FileUtils;
import com.io.hw.json.XmlYunmaUtil;
import com.string.widget.util.RandomUtils;
import com.string.widget.util.RegexUtil;
import com.string.widget.util.ValueWidget;
import com.time.util.TimeHWUtil;
import net.sf.jxls.transformer.XLSTransformer;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static com.common.util.WebServletUtil.escapePath;

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

            ObjectWriter writer = null;
            try {
                if (filters != null) {
                    writer = mapper.writer(filters);
                } else {
                    writer = mapper.writer();
                }
                content = writer.writeValueAsString(map);
//                logger.info(content);
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


	public static void buildExcel(Map<String, Object> model,
			HttpServletResponse resp, String templatePath) throws IOException,
			InvalidFormatException, org.apache.poi.openxml4j.exceptions.InvalidFormatException {
		org.apache.poi.ss.usermodel.Workbook workbook = getTemplateSource(templatePath);
		XLSTransformer transformer = new XLSTransformer();
		transformer.transformWorkbook(workbook, model);
        resp.setContentType(SystemHWUtil.RESPONSE_CONTENTTYPE_MS_EXCEL);
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
     * @author 黄威
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
     * @param path : 有无后缀名都可以
     * @param charset
	 * @return
	 */
	public static ReadAndWriteResult stub(HttpServletRequest request, String path, String charset) {
		String content = null;
		ReadAndWriteResult readAndWriteResult = new ReadAndWriteResult();
		try {
            //path:/stub/v1/xxx
            String realPath2 = WebServletUtil.getRealPath(request, path);

            File file = getStubFile(realPath2);
            realPath2 = file.getAbsolutePath();//执行getStubFile 之后,路径可能变化
            readAndWriteResult.setAbsolutePath(escapePath(realPath2));
            if (!file.exists()) {
                String errorMessage = realPath2 + " does not exist";
                logger.error(errorMessage);
                System.out.println(errorMessage);
                return fileNotExistReadAndWriteResult(readAndWriteResult, realPath2);
            }
			java.io.InputStream input = new FileInputStream(file);
            /*if (null == input) {
                logger.error("input is null");
                return fileNotExistReadAndWriteResult(readAndWriteResult, realPath2);
			}*/
            content = FileUtils.getFullContent2(input);
            //反序列化
            StubRange stubRange = XmlYunmaUtil.deAssembleStub(content);
            int selectedIndex = stubRange.getSelectedIndex();
            String sessionKey = deleteSuffix(path) + "selectedIndex";
            System.out.println("stub() get key:" + sessionKey);
            String selectedIndexStr = (String) SpringMVCUtil.resumeObject(sessionKey);
            if (!ValueWidget.isNullOrEmpty(selectedIndexStr)) {
                selectedIndex = Integer.parseInt(selectedIndexStr);
            }
            content = stubRange.getStubs().get(selectedIndex);
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

    public static File getStubFile(String realPath2) {
        String pathTmp;
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
            file = new File(deleteSuffix(realPath2) + ".do" + Constant2.stub_file_Suffix);
        }
        return file;
    }

    /***
     * 删除后缀名.json
     * @param realPath2
     * @return
     */
    public static String deleteSuffix(String realPath2) {
        return realPath2.replaceAll("\\" + Constant2.stub_file_Suffix + "$", SystemHWUtil.EMPTY);
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
        if (contentNull(content, readAndWriteResult)) return readAndWriteResult;
        try {
            String realPath2 = WebServletUtil.getRealPath(request, path);
            File file = getStubFile(realPath2);
            realPath2 = file.getAbsolutePath();
            readAndWriteResult.setAbsolutePath(escapePath(realPath2));
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

    public static void writeStubFileOneOption(String content, /*String charset,*/ ReadAndWriteResult readAndWriteResult, String path, int index) throws IOException {
        File file = getRealFile(path);
        writeStubFileOneOption(content, readAndWriteResult, file, index);
    }

    public static File getRealFile(String path) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String realPath2 = WebServletUtil.getRealPath(request, path);//父目录可能不存在
        return getStubFile(realPath2);
    }

    /***
     *  新增一个option
     * @param content
     * @param readAndWriteResult
     * @param path
     * @throws IOException
     */
    public static void addOneOptionStub(String content, /*String charset, */ReadAndWriteResult readAndWriteResult, String path) throws IOException {
        File file = getRealFile(path);
        addOneOptionStub(content,/*charset,*/readAndWriteResult, file);
    }
    /***
     * 第一个元素从0开始
     * @param content : 不是完整内容,只是一个选项(option)
     * @param readAndWriteResult
     * @param file
     * @param index : 从0开始
     * @throws IOException
     */
    public static ReadAndWriteResult writeStubFileOneOption(String content, /*String charset,*/ ReadAndWriteResult readAndWriteResult, File file, int index) throws IOException {
        StubRange stubRange = getStubRange(file);
        String absolutePath = file.getAbsolutePath();
        if (ValueWidget.isNullOrEmpty(readAndWriteResult.getAbsolutePath())) {
            readAndWriteResult.setAbsolutePath(absolutePath);
        }
        if (stubRange == null) {
            readAndWriteResult.setResult(false);
            readAndWriteResult.setErrorMessage(absolutePath + " 的内容为空");
            return readAndWriteResult;
        }

        if (index < 0) {
            index = 0;
        }
        stubRange.setSelectedIndex(index);
        List<String> list = stubRange.getStubs();
        readAndWriteResult.setContent(content);
       /* if (list.get(index).equals(content)) {
            readAndWriteResult.setResult(false);
            readAndWriteResult.setErrorMessage("无变化");
            return;
        }*/
        System.out.println("content:" + content + " , index:" + index);
        int length = list.size();
        if (index == length) {//新增一个option
            list.add(content);
        } else if (index > length) {//添加了多个textarea
            for (int i = 0; i < (index - length + 1); i++) {
                list.add(content);
            }
        } else {//修改已存在的option
            replaceElement(content, index, list);
        }
        stubRange.setStubs(list);
        writeStubRange(stubRange, file);//写入文件
        readAndWriteResult.setResult(true);
        return readAndWriteResult;
    }

    /***
     * 写入文件<br >io操作
     * @param stubRange
     * @param file
     * @throws IOException
     */
    public static void writeStubRange(StubRange stubRange, File file) throws IOException {
        FileWriterWithEncoding fileW = new FileWriterWithEncoding(file, SystemHWUtil.CHARSET_UTF);
        fileW.write(XmlYunmaUtil.assembleStub(stubRange));
        fileW.close();
    }

    public static StubRange getStubRange(File file) throws IOException {
        String oldContent = FileUtils.getFullContent2(file);//必须放在new FileWriterWithEncoding()之前,
        //为什么呢?因为new FileWriterWithEncoding()会把文件先清空.
        StubRange stubRange = XmlYunmaUtil.deAssembleStub(oldContent);
        if (null == stubRange || ValueWidget.isNullOrEmpty(stubRange.getStubs())) {
            return null;
        }
        return stubRange;
    }

    /***
     * 对已经存在的stub,增加一个选项(option)
     * @param content
     * @param readAndWriteResult
     * @param file
     * @throws IOException
     */
    public static void addOneOptionStub(String content, /*String charset, */ReadAndWriteResult readAndWriteResult, File file) throws IOException {
        StubRange stubRange = getStubRange(file);
        String absolutePath = file.getAbsolutePath();
        if (ValueWidget.isNullOrEmpty(readAndWriteResult.getAbsolutePath())) {
            readAndWriteResult.setAbsolutePath(absolutePath);
        }
        if (stubRange == null) {
            readAndWriteResult.setResult(false);
            readAndWriteResult.setErrorMessage(absolutePath + " 的内容为空");
            return;
        }
        List<String> list = stubRange.getStubs();
        list.add(content);
        stubRange.setStubs(list);
//        FileWriterWithEncoding fileW = new FileWriterWithEncoding(file, charset);
        writeStubRange(stubRange, file);
        readAndWriteResult.setResult(true);
        readAndWriteResult.setContent(content);
    }

    public static void replaceElement(String content, int index, List<String> list) {
        ValueWidget.replaceElement(content, index, list);
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
        if (contentNull(content, readAndWriteResult)) return readAndWriteResult;
        try {
            String realPath2 = WebServletUtil.getRealPath(request, path);//父目录可能不存在
			String parent = SystemHWUtil.getParentDir(realPath2);
			File parentFile = new File(parent);
			if (!parentFile.exists()) {
				parentFile.mkdirs();
			}
            readAndWriteResult.setAbsolutePath(WebServletUtil.escapePath(realPath2));
            File file = new File(realPath2);
			if (file.exists()) {
				String errorMessage = "文件" + realPath2 + "已经存在";
				logger.error(errorMessage);
				System.out.println(errorMessage);
				return fileHasExistReadAndWriteResult(readAndWriteResult, realPath2);
			}
            setServletUrl(request, path, readAndWriteResult);
            writeStubFile(content, charset, readAndWriteResult, file, index);
            readAndWriteResult.setTips("添加成功");

		} catch (java.io.FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return readAndWriteResult;
	}

    public static boolean contentNull(String content, ReadAndWriteResult readAndWriteResult) {
        if (ValueWidget.isNullOrEmpty(content)) {
            readAndWriteResult.setResult(false);
            readAndWriteResult.setErrorMessage("内容为空");
            return true;
        }
        return false;
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
            String interface2 = files.get(i).getAbsolutePath().replace(rootPath, SystemHWUtil.EMPTY);
            interface2 = interface2.replace("\\", "/").replaceAll(Constant2.stub_file_Suffix + "$", SystemHWUtil.EMPTY);
//			System.out.println(interface2);
			if (null == keyWord || interface2.contains(keyWord)) {
				pathList.add(interface2);
			}
		}
		return pathList;
	}

    /***
     * sameFileName is false
     * @param request
     * @param fileName
     * @param uploadFolder
     * @return
     */
    public static UploadResult getSavedToFile(HttpServletRequest request, String fileName, String uploadFolder) {
        return getSavedToFile(request, fileName, uploadFolder, false);
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
        String relativeUrl = WebServletUtil.getRelativePath(relativePath, finalFileName);
        if (!relativeUrl.startsWith("/")) {//uploadResult中的RelativePath 必须以斜杠开头
            relativeUrl = "/" + relativeUrl;
        }
        uploadResult.setRelativePath(relativeUrl);
        uploadResult.setFinalFileName(finalFileName);
		return uploadResult;
	}

    public static Map getUploadResultMap(MultipartFile file, HttpServletRequest request) {
        return getUploadResultMap(file, request, (String) null/*specifiedFileName*/);
    }
    /***
     * sameFileName is false <br >
     *     deleteOldFile is false
     * @param file
     * @param request
     * @return
     */
    public static Map getUploadResultMap(MultipartFile file, HttpServletRequest request, String specifiedFileName) {
        return getUploadResultMap(file, request, false, false, specifiedFileName);
    }

    /***
     *
     * @param file
     * @param request
     * @param deleteOldFile : 是否删除原文件
     * @param sameFileName : 文件名是否动态改变(加上时间戳就会动态改变)
     * @return
     */
    public static Map getUploadResultMap(MultipartFile file, HttpServletRequest request, boolean sameFileName, boolean deleteOldFile, String specifiedFileName) {
        String fileName = file.getOriginalFilename();// 上传的文件名
        if (ValueWidget.isNullOrEmpty(specifiedFileName)) {
            //删除所有的空格
            fileName = RegexUtil.filterBlank(fileName).replace("?", SystemHWUtil.EMPTY);//IE中识别不了有空格的json
        } else {
            fileName = specifiedFileName;
        }
//        System.out.println("fileName:"+fileName);

        UploadResult uploadResult = HWUtils.getSavedToFile(request, fileName, null, sameFileName);
        File savedFile = uploadResult.getSavedFile();
        File parentFolder = SystemHWUtil.createParentFolder(savedFile);
        FileUtils.makeWritable(parentFolder);//使...可写
        System.out.println("[upload]savedFile:"
                + savedFile.getAbsolutePath());
        //如果文件已经存在,则先删除
        if (deleteOldFile && savedFile.exists()) {
            System.out.println("删除 " + savedFile.getAbsolutePath());
            boolean deleteResult = savedFile.delete();
            if (!deleteResult) {
                logger.error("删除" + savedFile.getAbsolutePath() + "失败");
            }
        }
        // 保存
        try {
            file.transferTo(savedFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String url2 = WebServletUtil.getRelativeUrl(request, uploadResult.getRelativePath(), uploadResult.getFinalFileName());
        String fullUrl = null;//http://localhost:8080/tv_mobile/upload/image/20150329170823_2122015-03-23_01-42-03.jpg
        /***
         * request.getRequestURL():http://localhost:8081/SSLServer/addUser.security<br>
         * request.getServletPath():/addUser.security<br>
         * prefixPath:http://localhost:8080/tv_mobile/
         */
        fullUrl = WebServletUtil.getFullUrl(request, uploadResult.getRelativePath(), uploadResult.getFinalFileName());
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
        return "<img style=\"max-width: 99%\" src=\"" + fullUrl + "\" alt=\"不是图片,无法显示\">";
    }

    //    @Test
    public void test_writeStubFileOne() {
        try {
            ReadAndWriteResult readAndWriteResult = new ReadAndWriteResult();
            writeStubFileOneOption("ccc", 
                    readAndWriteResult, new File("/Users/whuanghkl/work/project/stub_test/src/main/webapp/stub/ab/test.json")
                    , 0);

           /* addOneOptionStub("新增一个元素aaa",SystemHWUtil.CHARSET_UTF,
                    readAndWriteResult ,new File("/Users/whuanghkl/work/project/stub_test/src/main/webapp/stub/ab/test.json"));*/
            System.out.println(HWUtils.getJsonP(readAndWriteResult));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
