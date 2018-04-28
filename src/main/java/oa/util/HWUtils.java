package oa.util;

import com.common.bean.RequestSendChain;
import com.common.bean.ResponseResult;
import com.common.bean.StubRange;
import com.common.dict.Constant2;
import com.common.util.MapUtil;
import com.common.util.SystemHWUtil;
import com.common.util.WebServletUtil;
import com.file.hw.props.GenericReadPropsUtil;
import com.io.hw.file.util.FileUtils;
import com.io.hw.json.HWJacksonUtils;
import com.io.hw.json.XmlYunmaUtil;
import com.string.widget.util.RandomUtils;
import com.string.widget.util.RegexUtil;
import com.string.widget.util.ValueWidget;
import com.time.util.TimeHWUtil;
import net.sf.jxls.transformer.XLSTransformer;
import oa.bean.UploadResult;
import oa.bean.stub.ReadAndWriteResult;
import oa.bean.stub.StubUpdateOption;
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
import org.springframework.ui.Model;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLEncoder;
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
        resp.setHeader(Constant2.CONTENT_DISPOSITION, "attachment;filename="
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

    public static ReadAndWriteResult stub(HttpServletRequest request, String path, String charset) {
        return stub(request, path, charset, null);
    }

	/***
	 * 读取文件
	 *
	 * @param request
     * @param path : 有无后缀名都可以
     * @param charset
     * @param index : 从 1 开始
     * @return
	 */
    public static ReadAndWriteResult stub(HttpServletRequest request, String path, String charset, Integer index) {
        String content = null;
		ReadAndWriteResult readAndWriteResult = new ReadAndWriteResult();
		try {
            //path:/stub/v1/xxx
            String realPath2 = WebServletUtil.getRealPath(request, path);
            logger.warn("realPath2:"+realPath2);

            File file = getStubFile(realPath2);
            realPath2 = file.getAbsolutePath();//执行getStubFile 之后,路径可能变化
            logger.warn("realPath2:"+realPath2);
            readAndWriteResult.setAbsolutePath(WebServletUtil.escapePath(realPath2));
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
            if (null != index && index > 0) {//index 的优先级最高
                selectedIndex = index - 1;
            } else {
                selectedIndex = getSelectedIndex4Cache(path, selectedIndex);
                selectedIndex = getSelectedIndexByParameter(request, stubRange, selectedIndex);
            }


            if (selectedIndex >= stubRange.getStubs().size()) {
                content = "(请按下Command+S/X)";
            } else {
                content = stubRange.getStubs().get(selectedIndex);
            }

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

    private static int getSelectedIndexByParameter(HttpServletRequest request, StubRange stubRange, int selectedIndex) {
        String attributeName = stubRange.getAttributeName();
        if (!ValueWidget.isNullOrEmpty(attributeName)) {
            String parameterVal = request.getParameter(attributeName);
            if (!ValueWidget.isNullOrEmpty(parameterVal)) {
                if (stubRange.hasAttributeVal(parameterVal)) {
                    selectedIndex = stubRange.getAttributeValIndexMap().get(parameterVal);
                }
            }
        }
        return selectedIndex;
    }

    private static int getSelectedIndex4Cache(String path, int selectedIndex) {
        String sessionKey = deleteSuffix(path) + "selectedIndex";
        System.out.println("getSelectedIndex4Cache() get key:" + sessionKey);
        String selectedIndexStr = (String) SpringMVCUtil.resumeGlobalObject(sessionKey);
        if (ValueWidget.isNullOrEmpty(selectedIndexStr)) {
            System.out.println("使用工具请求时,请确认是否设置了JSESSIONID");
        } else {
            selectedIndex = Integer.parseInt(selectedIndexStr);
        }
        return selectedIndex;
    }

    public static File getStubFile(String realPath2) {
        String pathTmp;
        if (realPath2.endsWith(Constant2.STUB_FILE_SUFFIX)) {
            pathTmp = realPath2;
        } else {
            pathTmp = realPath2 + Constant2.STUB_FILE_SUFFIX;
        }
        File file = new File(pathTmp);
        if (!file.exists()) {
            String errorMessage = pathTmp + " does not exist";
            System.out.println(errorMessage);
            logger.error(errorMessage);
            //兼容appList.do.json 文件名
            file = new File(deleteSuffix(realPath2) + ".do" + Constant2.STUB_FILE_SUFFIX);
        }
        return file;
    }

    /***
     * 删除后缀名.json
     * @param realPath2
     * @return
     */
    public static String deleteSuffix(String realPath2) {
        return realPath2.replaceAll("\\" + Constant2.STUB_FILE_SUFFIX + "$", SystemHWUtil.EMPTY);
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
        String errorMessage = "文件" + WebServletUtil.escapePath(realPath2) + "不存在";
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
        String errorMessage = "文件" + WebServletUtil.escapePath(realPath2) + "已经存在";
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
            readAndWriteResult.setAbsolutePath(WebServletUtil.escapePath(realPath2));
            if (!file.exists()) {
                String errorMessage = "文件" + realPath2 + "不存在";
                logger.error(errorMessage);
                System.out.println(errorMessage);
                return fileNotExistReadAndWriteResult(readAndWriteResult, realPath2);
            }
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
        try {
            FileWriterWithEncoding fileW = new FileWriterWithEncoding(file, charset);
            fileW.write(content);
            fileW.close();
            readAndWriteResult.setResult(true);
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
            readAndWriteResult.setResult(false);
            //java.io.FileNotFoundException: /data/wwwroot/hweiWebsite/stub_test_svn/stub/api/b/c.xml (No such file or directory)
            readAndWriteResult.setErrorMessage("可能是文件目录没有权限," + e.getMessage());
        }
//        StubRange stubRange = XmlYunmaUtil.deAssembleStub(content);

        readAndWriteResult.setContent(content);
	}

    /***
     *
     * @param content             : content 中不能包含"</",可以有"/",比如图片地址
     * @param readAndWriteResult
     * @param path
     * @param index
     * @throws IOException
     */
    public static void writeStubFileOneOption2(String content, String attributeVal, /*String charset,*/  String path, StubUpdateOption stubUpdateOption) throws IOException {
        File file = getRealFile(path);
        stubUpdateOption.setFile(file);
        writeStubFileOneOption(content, attributeVal, stubUpdateOption);
    }

    public static void updateAttributeName(String attributeVal, /*String charset,*/ ReadAndWriteResult readAndWriteResult, String path) throws IOException {
        File file = getRealFile(path);
        updateAttributeName(attributeVal, readAndWriteResult, file);
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
     * @param content : 不是完整内容,只是一个选项(option)<br />
     *                content 中不能包含"</",可以有"/",比如图片地址
     * @param readAndWriteResult
     * @param file
     * @param index : 从0开始
     * @throws IOException
     */
    public static ReadAndWriteResult writeStubFileOneOption(String content, String attributeVal, /*String charset,*/ StubUpdateOption stubUpdateOption) throws IOException {
        ReadAndWriteResult readAndWriteResult = stubUpdateOption.getReadAndWriteResult();
        int index = stubUpdateOption.getIndex();
        StubRange stubRange = getStubRange(stubUpdateOption.getFile());
        String absolutePath = stubUpdateOption.getFile().getAbsolutePath();
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
        if (!stubUpdateOption.isLocked()) {
            stubRange.setSelectedIndex(index);
        }

        List<String> list = stubRange.getStubs();
        readAndWriteResult.setContent(content);
       /* if (list.get(index).equals(content)) {
            readAndWriteResult.setResult(false);
            readAndWriteResult.setErrorMessage("无变化");
            return;
        }*/
        //content 中不能包含"</",可以有"/",比如图片地址
//        String specialChar = "</";
//        content = content.replace(specialChar, XSSUtil.cleanXSS(specialChar));//注释掉,因为有时候就是需要测试xss 攻击
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
        updateAttributeVal(attributeVal, index, stubRange);
        stubRange.setStubs(list);
        writeStubRange(stubRange, stubUpdateOption.getFile());//写入文件
        readAndWriteResult.setResult(true);
        return readAndWriteResult;
    }

    public static ReadAndWriteResult updateAttributeName(String attributeName, ReadAndWriteResult readAndWriteResult, File file) throws IOException {
        if (ValueWidget.isNullOrEmpty(attributeName)) {
            return null;
        }
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

        stubRange.setAttributeName(attributeName);
        writeStubRange(stubRange, file);//写入文件
        readAndWriteResult.setResult(true);
        return readAndWriteResult;
    }

    private static void updateAttributeVal(String attributeVal, int index, StubRange stubRange) {
        if (!ValueWidget.isNullOrEmpty(attributeVal)) {
            Map<Object, Object> indexAttributeValMap = MapUtil.updateReverseMap(attributeVal, index, stubRange.getAttributeValIndexMap());
            stubRange.setAttributeValIndexMap(indexAttributeValMap);
        }
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
            readAndWriteResult.setErrorMessage("内容为空 (请点击 [添加option])");
            return true;
        }
        return false;
    }

	private static void setServletUrl(HttpServletRequest request, String path, ReadAndWriteResult readAndWriteResult) {
        boolean deleteProjectName = false;//为 true, 表示不包含项目名称
        try {
            Properties properties = GenericReadPropsUtil.getProperties(true, "config/common.properties");
            if (null != properties) {
                String sentinelIpTmp = properties.getProperty("deleteProjectName");
                if (!ValueWidget.isNullOrEmpty(sentinelIpTmp)) {
                    deleteProjectName = SystemHWUtil.parse33(sentinelIpTmp);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("deleteProjectName:"+deleteProjectName);
		String serverUrl = getServletUrl(request,!deleteProjectName);//http://10.1.253.44:81/tv_mobile
		logger.info("serverUrl:" + serverUrl);
        readAndWriteResult.setUrl(serverUrl + Constant2.SLASH + path.replaceAll("\\" + Constant2.STUB_FILE_SUFFIX + "$"/*需要转义，否则就是通配符*/, SystemHWUtil.EMPTY));
    }

	/***
	 * @param request
     * @param containsProjectName : 是否包含项目名,例如"http://stub.yhskyc.com/stub_test/stubEdit/search?servletAction=api/b/c"中的stub_test
     * @return : http://10.1.253.44:81/tv_mobile
	 */
	public static String getServletUrl(HttpServletRequest request,boolean containsProjectName) {
	    String url=request.getRequestURL().toString().replaceAll("(https?://[^/]+)/.*$", "$1");
		return containsProjectName? (url + request.getContextPath()):url;
	}

	public static ReadAndWriteResult stub(HttpServletRequest request, String path) {
        return HWUtils.stub(request, path, SystemHWUtil.CURR_ENCODING, null);
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
        List<File> files = FileUtils.getListFiles(rootPath, Constant2.STUB_FILE_SUFFIX.substring(1));//去掉.xml 中的.
        List<String> pathList = new ArrayList<String>();
		for (int i = 0; i < files.size(); i++) {
            String interface2 = files.get(i).getAbsolutePath().replace(rootPath, SystemHWUtil.EMPTY);
            interface2 = interface2.replace("\\", "/").replaceAll(Constant2.STUB_FILE_SUFFIX + "$", SystemHWUtil.EMPTY);
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

    /***
     * 设置上传文件的目标路径(保存路径)<br />
     * 参数"suffix"值为"true",则日期在后面,即"文件名_20170308201110_134.jpg"
     * @param request
     * @param fileName
     * @param uploadFolder
     * @return
     */
    public static UploadResult getSavedToFile(HttpServletRequest request, String fileName, String uploadFolder, boolean sameFileName) {
        fileName = RegexUtil.filterBlank(fileName);//IE中识别不了有空格的json "3C71E7EBAE9F48CEF1FE4A675E43F32B.jpg"
        // 保存到哪儿  prefix:"20170308201110_134_"
        String prefix = TimeHWUtil.formatDateByPattern(TimeHWUtil
                .getCurrentTimestamp(), TimeHWUtil.yyyyMMddHHmmss_NO_DELIMITER/*.replace(SystemHWUtil.MIDDLE_LINE, SystemHWUtil.EMPTY)
                .replace(SystemHWUtil.COLON, SystemHWUtil.EMPTY).replace(SystemHWUtil.BLANK, SystemHWUtil.EMPTY)*/) + SystemHWUtil.UNDERLINE + new Random().nextInt(1000);
        String finalFileName = null;
        if (sameFileName) {
            finalFileName = "upload_" + fileName;
        } else {
            String isSuffixStr = request.getParameter("suffix");
            if ("true".equals(isSuffixStr)) {
                finalFileName = fileName + SystemHWUtil.UNDERLINE + prefix;
            } else {
                finalFileName = prefix + SystemHWUtil.UNDERLINE + fileName;//"20170308201110_134_3C71E7EBAE9F48CEF1FE4A675E43F32B.jpg"
            }
        }
        String relativePath = null;
		if (ValueWidget.isNullOrEmpty(uploadFolder)) {
            relativePath = Constant2.UPLOAD_FOLDER_NAME + "/image";//"upload/image"
        } else {
			relativePath = uploadFolder;
		}
		UploadResult uploadResult = new UploadResult();
		File savedFile = WebServletUtil.getUploadedFilePath(request, relativePath
				, finalFileName,
                Constant2.SRC_MAIN_WEBAPP);//"/Users/whuanghkl/code/mygit/convention/src/main/webapp/upload/image/20170308201110_134_3C71E7EBAE9F48CEF1FE4A675E43F32B.jpg"
        uploadResult.setSavedFile(savedFile);
        try {//解决图片文件名中包含特殊字符,例如],(,` 导致访问不到的问题
            finalFileName = URLEncoder.encode(finalFileName, SystemHWUtil.CHARSET_UTF);//仅仅对多字节字符有影响
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String relativeUrl = WebServletUtil.getRelativePath(relativePath/*upload/image*/, finalFileName);
        if (!relativeUrl.startsWith(Constant2.SLASH)) {//uploadResult中的RelativePath 必须以斜杠开头  "upload/image/20170308201110_134_3C71E7EBAE9F48CEF1FE4A675E43F32B.jpg"
            relativeUrl = Constant2.SLASH + relativeUrl;//"/upload/image/20170308201110_134_3C71E7EBAE9F48CEF1FE4A675E43F32B.jpg"
        }
        uploadResult.setRelativePath(relativeUrl);//"/upload/image/20170308201110_134_3C71E7EBAE9F48CEF1FE4A675E43F32B.jpg"
        uploadResult.setFinalFileName(finalFileName);//"20170308201110_134_3C71E7EBAE9F48CEF1FE4A675E43F32B.jpg"
        return uploadResult;
	}

    public static Map getUploadResultMap(MultipartFile file, HttpServletRequest request/*, boolean isEscape*/) {
        return getUploadResultMap(file, request, (String) null/*specifiedFileName*//*, isEscape*/);
    }
    /***
     * sameFileName is false <br >
     *     deleteOldFile is false
     * @param file
     * @param request
     * @return
     */
    public static Map getUploadResultMap(MultipartFile file, HttpServletRequest request, String specifiedFileName/*, boolean isEscape*/) {
        return getUploadResultMap(file, request, false, false, specifiedFileName/*, isEscape*/);
    }

    /***
     * 真正上传文件<br />
     *
     * @param file
     * @param request
     * @param deleteOldFile : 是否删除原文件
     * @param sameFileName : 文件名是否动态改变(加上时间戳就会动态改变)
     * @return : {
     *     fileName:"",//文件名称<br />
     *     remoteAbsolutePath:"",//文件系统的绝对路径<br />
     *     url:"",//包含项目名的相对地址<br />
     *     fullUrl:"",//全路径<br />
     *     relativePath:"",//不包含项目名的相对地址<br />
     *     imgTag:"",//escape之后的img 标签<br />
     * }
     */
    public static Map getUploadResultMap(MultipartFile file, HttpServletRequest request, boolean sameFileName, boolean deleteOldFile, String specifiedFileName) {
        String fileName = file.getOriginalFilename();// 上传的文件名  "3C71E7EBAE9F48CEF1FE4A675E43F32B.jpg"
        if (ValueWidget.isNullOrEmpty(specifiedFileName)) {
            fileName = filterSpecialChar(fileName);//过滤掉特殊字符
        } else {
//            System.out.println(" :" + (int)fileName.charAt(22));;  fileName.replace((char)769,'_');
            fileName = specifiedFileName;
        }
//        System.out.println("fileName:"+fileName);

        UploadResult uploadResult = HWUtils.getSavedToFile(request, fileName, null, sameFileName);
        File savedFile = uploadResult.getSavedFile();//"/Users/whuanghkl/code/mygit/convention/src/main/webapp/upload/image/20170308201110_134_3C71E7EBAE9F48CEF1FE4A675E43F32B.jpg"
        File parentFolder = SystemHWUtil.createParentFolder(savedFile);
        FileUtils.makeWritable(parentFolder);//使...可写  "/Users/whuanghkl/code/mygit/convention/src/main/webapp/upload/image"
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
        String finalFileName = uploadResult.getFinalFileName();//"20170308201110_134_3C71E7EBAE9F48CEF1FE4A675E43F32B.jpg"
        String url2 = WebServletUtil.getRelativeUrl(request, uploadResult.getRelativePath(), finalFileName);
        String fullUrl = null;//http://localhost:8080/tv_mobile/upload/image/20150329170823_2122015-03-23_01-42-03.jpg
        /***
         * request.getRequestURL():http://localhost:8081/SSLServer/addUser.security<br>
         * request.getServletPath():/addUser.security<br>
         * prefixPath:http://localhost:8080/tv_mobile/
         */
        fullUrl = WebServletUtil.getFullUrl(request, uploadResult.getRelativePath(), finalFileName);//"http://localhost:8080/upload/image/20170308201110_134_3C71E7EBAE9F48CEF1FE4A675E43F32B.jpg"
        Map map = new HashMap();
        String relativePath = uploadResult.getRelativePath();//"/upload/image/20170308201110_134_3C71E7EBAE9F48CEF1FE4A675E43F32B.jpg"
        map.put("fileName", finalFileName);//"20170308201110_134_3C71E7EBAE9F48CEF1FE4A675E43F32B.jpg"
        map.put("remoteAbsolutePath", savedFile.getAbsolutePath());
        //包含项目名
        map.put("url", url2);//"//upload/image/20170308201110_134_3C71E7EBAE9F48CEF1FE4A675E43F32B.jpg"
        map.put("fullUrl", fullUrl);//"http://localhost:8080/upload/image/20170308201110_134_3C71E7EBAE9F48CEF1FE4A675E43F32B.jpg"
        //不包含项目名
        map.put("relativePath", relativePath);//"/upload/image/20170308201110_134_3C71E7EBAE9F48CEF1FE4A675E43F32B.jpg"
        map.put("imgTag", ValueWidget.escapeHTML(getHtmlImgTag(fullUrl)));//"&lt;img style=&quot;max-width: 99%&quot; src=&quot;http://localhost:8080/upload/image/20170308201110_134_3C71E7EBAE9F48CEF1FE4A675E43F32B.jpg&quot; alt=&quot;不是图片,无法显示&quot;&gt;"
        return map;
    }

    /**
     * 过滤掉特殊字符<br />
     * see ValueWidget.isBlank
     *
     * @param fileName
     * @return
     */
    public static String filterSpecialChar(String fileName) {
        //删除所有的空格
        fileName = RegexUtil.filterBlank(fileName).replace("?", SystemHWUtil.EMPTY);//IE中识别不了有空格的json
        logger.error("getUploadResultMap:" + fileName);
        fileName = fileName.replace((char) 769, '_').replace((char) 205, '_')
                .replace((char) 12288/* 全角空格, 参考:https://my.oschina.net/u/2312705/blog/832438 */, '_')
                .replace((char) 160/* 不间断空格, 参考:https://my.oschina.net/u/2312705/blog/832438 */, '_');
        logger.error("getUploadResultMap:" + fileName);
        return fileName.toLowerCase();//文件名全部修改为小写
    }

    public static String getHtmlImgTag(String fullUrl) {
        return "<img style=\"max-width: 99%\" src=\"" + fullUrl + "\" alt=\"不是图片,无法显示\">";
    }

    //    @Test
    /*public void test_writeStubFileOne() {
        try {
            ReadAndWriteResult readAndWriteResult = new ReadAndWriteResult();
            writeStubFileOneOption("ccc", 
                    readAndWriteResult, new File("/Users/whuanghkl/work/project/stub_test/src/main/webapp/stub/ab/test.json")
                    , 0);

           *//* addOneOptionStub("新增一个元素aaa",SystemHWUtil.CHARSET_UTF,
                    readAndWriteResult ,new File("/Users/whuanghkl/work/project/stub_test/src/main/webapp/stub/ab/test.json"));*//*
            System.out.println(HWUtils.getJsonP(readAndWriteResult));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }*/

    /**
     * 刷新数据字典
     *
     * @param request
     */
    public static void sendHttpRefreshDictionary(HttpServletRequest request) {
        System.out.println(" 发送请求");
        RequestSendChain requestInfoBeanOrderWIG = new RequestSendChain();
        String path = request.getContextPath();

        requestInfoBeanOrderWIG.setServerIp(request.getServerName());
        requestInfoBeanOrderWIG.setPort(String.valueOf(request.getServerPort()));
        requestInfoBeanOrderWIG.setSsl(false);
        requestInfoBeanOrderWIG.setActionPath(path + "/refresh/refresh");
        requestInfoBeanOrderWIG.setRequestMethod(Constant2.REQUEST_METHOD_GET);
        // requestInfoBeanOrderWIG.setDependentRequest(requestInfoBeanLogin);
        requestInfoBeanOrderWIG.setCurrRequestParameterName("");
        requestInfoBeanOrderWIG.setPreRequestParameterName("");

        ResponseResult responseResultOrderTDn = requestInfoBeanOrderWIG.request(); //new RequestPanel.ResponseResult(requestInfoBeanLogin).invoke();
        String responseOrderBcr = responseResultOrderTDn.getResponseJsonResult();
        System.out.println("responseText:" + responseOrderBcr);
    }

    /***
     *
     * @param model
     * @param file
     * @param request
     * @param sameFileName : 每次上传是否使用相同的文件名称
     */
    public static String uploadFileSameFileName(Model model, MultipartFile file, HttpServletRequest request/*, boolean sameFileName*/) {
        boolean sameFileName = HWUtils.isSameFileName(request);
        String deleteOldFileSt = request.getParameter("deleteOldFile");
        String isEscapeString = request.getParameter("escape");//是否转义
        boolean isEscape = SystemHWUtil.parse2Boolean(isEscapeString);
        System.out.println("ImageUploadCallback isEscapestr :" + isEscapeString);
        boolean deleteOldFile = SystemHWUtil.parse33(deleteOldFileSt);

        if (deleteOldFile) {
            System.out.println("上传时删除原文件");
        }
        String fileName = request.getParameter("fileName");
        Map map = HWUtils.getUploadResultMap(file, request, sameFileName, deleteOldFile, fileName/*, isEscape*/);
        model.addAllAttributes(map);
        String content = HWJacksonUtils.getJsonP(map);
        logger.info(content);
        return content;
    }

    public static boolean isSameFileName(HttpServletRequest request) {
        String sameFileNameStr = request.getParameter(Constant2.PARAMETER_SAME_FILE_NAME);
        return SystemHWUtil.parse33(sameFileNameStr);
    }

}
