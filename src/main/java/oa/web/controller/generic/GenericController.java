package oa.web.controller.generic;

import com.common.bean.BaseResponseDto;
import com.common.bean.ClientOsInfo;
import com.common.dao.generic.GenericDao;
import com.common.dict.Constant2;
import com.common.util.PageUtil;
import com.common.util.ReflectHWUtils;
import com.common.util.SystemHWUtil;
import com.common.util.WebServletUtil;
import com.common.web.view.PageView;
import com.io.hw.json.HWJacksonUtils;
import com.string.widget.util.ValueWidget;
import com.time.util.TimeHWUtil;
import oa.callback.RequestCallback;
import oa.util.HWUtils;
import oa.util.SpringMVCUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * session 中的key:
 * clientOsInfo520<br />
 * "return_url_after_login"<br />
 * "user":登录成功的标识<br />
 * "logined":登录成功的标识<br />
 * "keyword_columns"<br />
 * "alias_keyword233"
 * @param <T>
 */
public abstract class GenericController <T>{

    public static final String VIEW_LIST = "/list";
    protected static final Logger logger = Logger.getLogger(GenericController.class);
    public static final String VIEW_ADD = "/add";
    public static final String VIEW_DETAIL = "/detail";
    /***
     * 统一通过方法setEntityClz()来设置
     */
    private Class<T> clz = SystemHWUtil.getGenricClassType(getClass());
    protected GenericDao<T>dao;
    /***
     * 设备类型
     */
    protected int deviceType;
    /***
     * 类似aop
     */
    protected RequestCallback requestCallback = null;
    /***
     * 用于拦截器HandlerInterceptorAdapter中 保存处理的数据<br />
     * <code>
     public class LoggerInterceptor extends HandlerInterceptorAdapter {
     private static final Logger LOG = Logger.getLogger(LoggerInterceptor.class);

     public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
     if (handler instanceof HandlerMethod) {
     HandlerMethod handlerMethod = (HandlerMethod) handler;
     Object beanObj=handlerMethod.getBean();
     if(request.getRequestURI().endsWith("/test/list")) {
     Test2BoyController controller = (Test2BoyController) beanObj;
     HashMap<String,String> hashMap=new HashMap<>();
     hashMap.put("message","成功了,333");
     controller.setHandlerMethodCacheData(hashMap);
     }

     return true;
     } else {
     return super.preHandle(request, response, handler);
     }
     }
     }
     * </code>
     */
    protected ThreadLocal<HashMap<String, String>> handlerMethodCacheData = new ThreadLocal<HashMap<String, String>>();

    public void setHandlerMethodCacheData(HashMap<String, String> hashMap) {
        handlerMethodCacheData.set(hashMap);
    }

    public HashMap<String, String> getHandlerMethodCacheData() {
        return handlerMethodCacheData.get();
    }

    /***
     * 从WebApplicationContext 获取dao
     * @param request
     * @return
     */
    protected Object getDaoByWebApp(HttpServletRequest request, String entityName) {
        Object dao = null;
        WebApplicationContext webApp=RequestContextUtils.getWebApplicationContext(request	, request.getSession().getServletContext());
        try {
            dao = webApp.getBean(ValueWidget.title(SystemHWUtil.getFileSuffixName(entityName)));//返回的是同一个对象
        } catch (NoSuchBeanDefinitionException e) {
//				e.printStackTrace();
            dao = webApp.getBean(SystemHWUtil.getFileSuffixName(entityName));//返回的是同一个对象
        }
//			System.out.println("dao:"+dao);
        return dao;
    }

    protected GenericDao<T> getDaoByWebApp(HttpServletRequest request) {
        if (ValueWidget.isNullOrEmpty(dao)) {
            dao = (GenericDao<T>) getDaoByWebApp(request, ValueWidget.title(clz.getSimpleName()) + Constant2.DAO);
        }
        return dao;
    }


    /***
     * 获取dao
     * @param request
     */
    protected void init(HttpServletRequest request){
        getDaoByWebApp(request);
//		System.out.println(clz);//class oa.entity.RoleLevel
//		System.out.println(clz.getName());//oa.entity.RoleLevel
    }

    /***
     * 子类必须实现
     * @return
     */
//	protected abstract GenericDao<T> getDao();

    /***
     * 根据请求方式(GET,POST)区分 <br />
     * 不是保存,而是进入添加页面<br />
     * 此处的请求方式必须是GET
     * @param practiceWay : 区分手机端和PC端
     * @param model
     * @return
     */
    @RequestMapping(value = VIEW_ADD, method = RequestMethod.GET)
    public String addInput(String practiceWay, Model model, HttpServletRequest request, HttpServletResponse response, String targetView) {
        addCommonAction(model, request, response);
        model.addAttribute(getJspFolder(),getDao().createEmptyObj());//用于spring MVC 的sf标签
        if(!ValueWidget.isNullOrEmpty(targetView)){
            return targetView;
        }
        return getJspFolder2() + VIEW_ADD;
    }

    private void addCommonAction(Model model, HttpServletRequest request, HttpServletResponse response) {
        init(request);
        callback(model, request, response, Constant2.REQUEST_TYPE_ADD);
        commonAction(model);

        beforeAddInput(model, request);
        model.addAttribute(Constant2.KEY_MODEL_ATTRIBUTE, getJspFolder());//necessary and important
    }

    @RequestMapping(value = "/add/map",method=RequestMethod.GET)
    public String addInputMap(String practiceWay, Model model, HttpServletRequest request, HttpServletResponse response, String targetView) {
        addCommonAction(model, request, response);
        setId2Model(model, (T) getDao().createEmptyObj());
        if(!ValueWidget.isNullOrEmpty(targetView)){
            return targetView;
        }
        return getJspFolder2() + VIEW_ADD;
    }

    /***
     * 可以覆写
     * @param model
     */
    protected abstract void beforeAddInput(Model model, HttpServletRequest request);//{
//		List<T> roles=this.getDao().getAll();
//		model.addAttribute(roles);//选择上级
//	}

    /***
     * 根据请求方式(GET,POST)区分<br>保存(新增一条记录)<br />
     * 会持久化数据
     * @param roleLevel
     * @param binding
     * @param model
     * @return
     */
    @RequestMapping(value = VIEW_ADD, method = RequestMethod.POST)
    public String save(@Valid T roleLevel, BindingResult binding, Model model, HttpServletRequest request, String targetView, HttpServletResponse response) {
        init(request);
        callback(model, request, response, Constant2.REQUEST_TYPE_ADD_SAVE);
        if (!saveValidate(roleLevel, binding, model)) {
            return getJspFolder2() + VIEW_ADD;
        }
        if (!beforeSave(roleLevel, model, response)) {
            return null;
        }

        saveCommon(roleLevel, model);
        commonAction(model);
        if(!ValueWidget.isNullOrEmpty(targetView)){
            return targetView;
        }
        return getRedirectViewAll();
    }

    /***
     * 若请求参数reqtype=json,则表示请求体就是json串,而不是普通的表单提交
     * @param roleLevel
     * @param binding
     * @param model
     * @param request
     * @param callback
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/add/json")
    public String saveJson(@Valid T roleLevel,
                           BindingResult binding,
                           Model model,
                           HttpServletRequest request,
                           String callback, HttpServletResponse response) {
        return addSaveJsonAction(roleLevel, binding, model, request, callback, response);
    }

    public String addSaveJsonAction(T roleLevel, BindingResult binding, Model model, HttpServletRequest request, String callback, HttpServletResponse response) {
        init(request);
        Map map = new HashMap();
        int login_result = 0;
        T roleLevelTmp = reqJson(request);
        if (null != roleLevelTmp) {
            roleLevel = roleLevelTmp;
        }

        if (saveValidate(roleLevel, binding, model)) {
            login_result = Constant2.LOGIN_RESULT_SUCCESS;
            beforeSave(roleLevel, model, response);
            saveCommon(roleLevel, model);
            commonAction(model);
        }
        map.put(Constant2.LOGIN_RESULT_KEY, login_result);
        String entityName = getJspFolder();//"test","convention"或"house",即实体类的名称(首字母小写)
        if (ValueWidget.isNullOrEmpty(entityName)) {
            entityName = getJspFolder2();
        }
        map.put(entityName, roleLevel);//此时id 已经注入进去
        map.put("id", ReflectHWUtils.getObjectValue(roleLevel, "id"));
        String content = HWJacksonUtils.getJsonP(map, callback, false);
        return content;
    }

    public T reqJson(HttpServletRequest request) {
        //判断请求参数reqtype=json
        String reqtype = request.getParameter("reqtype");
        if ("json".equals(reqtype)) {
            String requestBody = WebServletUtil.getRequestPostStr(request);
            T roleLevel = (T) HWJacksonUtils.deSerialize(requestBody, clz);//对请求体进行反序列化
            return roleLevel;
        }
        return null;
    }


    private boolean saveValidate(@Valid T roleLevel, BindingResult binding, Model model) {
        if (!binding.hasErrors()) {
            return true;
        }
        List<ObjectError> errors = binding.getAllErrors();
        for (int i = 0; i < errors.size(); i++) {
            ObjectError error = errors.get(i);
            System.out.println(error.getCode() + "\t" + error.getDefaultMessage());
        }
        model.addAttribute(getJspFolder(), roleLevel);
        errorDeal(model);
//			System.out.println("error path:"+binding.getObjectName());
        model.addAttribute(Constant2.KEY_MODEL_ATTRIBUTE, binding.getObjectName());//提示错误信息时,jsp页面中modelAttribute 的值必须是实体类名称首字母小写
        return false;
    }

    /***
     * 使用bean validator 机制出错时自定义的处理方式
     * @param model
     */
    protected abstract void errorDeal(Model model) ;

    /***
     * 一定要在saveCommon 之前调用
     * @param roleLevel
     */
    protected boolean beforeSave(T roleLevel, Model model, HttpServletResponse response) {
        if (ValueWidget.isNullOrEmpty(roleLevel)) {
            return true;
        }
        ReflectHWUtils.fillTimeForObj(roleLevel);
        return true;
    }

    /***
     * 兼容ID的类型:long,int
     * @param id
     * @return
     */
    private T getById(int id){
        T roleLevel=null;
        Field f = ReflectHWUtils.getSpecifiedField(clz, Constant2.DB_ID);
        String propertyType=f.getType().getSimpleName();//成员变量的类型,例如long
        if(propertyType.equalsIgnoreCase("Long")){
            roleLevel=(T) this.getDao().get((long)id);
        }else{
            roleLevel=(T) this.getDao().get(id);
        }
        return roleLevel;
    }

    /**
     * 不需要覆写
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(value = "/{id}")
    public String detail(@PathVariable int id, Model model, HttpServletRequest request, HttpServletResponse response, String targetView) {
        init(request);
        return detailAction(id, model, request, response, targetView);
    }

    protected String detailAction(int id, Model model, HttpServletRequest request, HttpServletResponse response, String targetView) {
        T t = detailTODO(id, model, request, response);
        if (null != t) {
            model.addAttribute(getJspFolder(), t);
        }
        commonAction(model);
        callback(model, request, response, Constant2.REQUEST_TYPE_DETAIL);
        if(!ValueWidget.isNullOrEmpty(targetView)){
            return targetView;
        }
        String view = getJspFolder2() + VIEW_DETAIL;
        return view;
    }

    @RequestMapping(value = "/json/{id}", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    @ResponseBody
    @Deprecated
    public String detailJson(@PathVariable int id, Model model, HttpServletRequest request, HttpServletResponse response, String filterColumn) {
        logger.warn("建议使用接口:/" + id + "/json");
        return detailJsonAction(id, model, request, response, filterColumn);
    }

    @RequestMapping(value = "/{id}/json", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    @ResponseBody
    public String detailJson2(@PathVariable int id, Model model, HttpServletRequest request, HttpServletResponse response, String filterColumn) {
        return detailJsonAction(id, model, request, response, filterColumn);
    }

    /***
     * 获取单个成员变量(属性)的值<br />
     * /test/753/json/updateTime<br />
     * /test/753/json/testcase
     * @param id
     * @param column
     * @param model
     * @param request
     * @param response
     * @param filterColumn
     * @return
     */
    @RequestMapping(value = "/{id}/json/{column}", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    @ResponseBody
    public String specialJson2(@PathVariable int id, @PathVariable String column, Model model, HttpServletRequest request, HttpServletResponse response, String filterColumn) {
        init(request);
        Object objectVal = this.getDao().getOnePropertyById2(id, column);
        Map map = new HashMap();
        map.put(column, objectVal);
        return HWJacksonUtils.getJsonP(map);
    }

    public String detailJsonAction(int id, Model model, HttpServletRequest request, HttpServletResponse response, String filterColumn) {
        init(request);
        T t = detailTODO(id, model, request, response);
        if (null != t) {
            model.addAttribute(getJspFolder(), t);
        }
        commonAction(model);
        callback(model, request, response, Constant2.REQUEST_TYPE_DETAIL);
        if (!ValueWidget.isNullOrEmpty(filterColumn)) {
            ReflectHWUtils.setObjectValue(t, filterColumn, null, false);
        }
        return HWJacksonUtils.getJsonP(t);
    }

    @RequestMapping(value = "/{id}/map")
    public String detailMap(@PathVariable int id, Model model, HttpServletRequest request, HttpServletResponse response, String targetView) {
        init(request);
        callback(model, request, response, Constant2.REQUEST_TYPE_DETAIL);
        T roleLevel = detailTODO(id, model, request, response);
        setId2Model(model, roleLevel);
        commonAction(model);
        if(!ValueWidget.isNullOrEmpty(targetView)){
            return targetView;
        }
        return getJspFolder2() + VIEW_DETAIL;
    }

    private void setId2Model(Model model, T roleLevel) {
        model.addAttribute(getJspFolder(), ReflectHWUtils.parseObject(roleLevel, Constant2.DB_ID));
        //用于spring MVC 的sf标签
    }

    /***
     * 可以被覆写(override)
     * @param id
     * @param model
     * @param request
     * @return
     */
    protected T detailTODO(int id, Model model, HttpServletRequest request, HttpServletResponse response) {
        T roleLevel=getById(id);
        return roleLevel;
    }

    /***
     * 在实现的方法中,先执行自定义操作,在调用super.deleteTODO
     * @param id
     * @param roleLevel
     * @param model
     * @param request
     */
    protected void deleteTODO(int id, T roleLevel, Model model, HttpServletRequest request){
        if (roleLevel != null) {
            this.getDao().delete(roleLevel);
        }
    }

    /***
     * 不需要覆写
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(value = "/{id}/delete")
    public String deleteOne(@PathVariable int id, Model model, HttpServletRequest request, HttpServletResponse response, String targetView) {
        return deleteAction(id, model, request, response, targetView);
    }

    protected String deleteAction(int id, Model model, HttpServletRequest request, HttpServletResponse response, String targetView) {
        boolean success = deleteCommon(id, model, request, response);
        if (!success) {
            logger.error("delete failed id:" + id);
        }
        String resultUrl=getRedirectViewAll() + "?fsdf=" + new Date().getTime();
        if(!ValueWidget.isNullOrEmpty(targetView)){
            resultUrl=resultUrl+"&targetView="+targetView;///api_group/list?fsdf=1434898280423&targetView=api_group/list_common
        }
        logger.info(resultUrl);
        return resultUrl;
    }

    /***
     * 删除一条记录,返回json
     * @param id
     * @param model
     * @param request
     * @param targetView
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/{id}/delete/json", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    public String jsonDeleteOne(@PathVariable int id, Model model, HttpServletRequest request, HttpServletResponse response, String targetView) {
        return jsonDeleteAction(id, model, request, response);

    }

    @ResponseBody
    @RequestMapping(value = "/{id}/json/delete", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    @Deprecated
    public String jsonDeleteOne2(@PathVariable int id, Model model, HttpServletRequest request, HttpServletResponse response, String targetView) {
        logger.warn("建议使用接口:/" + id + "/delete/json");
        return jsonDeleteAction(id, model, request, response);

    }

    public String jsonDeleteAction(@PathVariable int id, Model model, HttpServletRequest request, HttpServletResponse response) {
        boolean success = deleteCommon(id, model, request, response);
        Map map=new HashMap();
        if (success) {
            return Constant2.RESPONSE_RIGHT_RESULT;
        }
        map.put(Constant2.LOGIN_RESULT_KEY, false);
        map.put(Constant2.RESPONSE_KEY_ERROR_MESSAGE, "未找到记录,id:"+id);
        return HWJacksonUtils.getJsonP(map);
    }

    private boolean deleteCommon(int id, Model model, HttpServletRequest request, HttpServletResponse response) {
        init(request);
//		System.out.println("delete a os type :"+id);
//		T newsToDel = this.getDao().get(id);
        callback(model, request, response, Constant2.REQUEST_TYPE_DELETE);
        if (deleteTODO(id, model, request)) return false;
        commonAction(model);
        return true;
    }

    private boolean deleteTODO(int id, Model model, HttpServletRequest request) {
        T newsToDel=getById(id);
        if(ValueWidget.isNullOrEmpty(newsToDel)){
            return true;
        }
        deleteTODO(id, newsToDel, model, request);
        return false;
    }

    /***
     * 需要覆写
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(value = "/{id}/edit"/*,method=RequestMethod.GET*/)
    public String edit(@PathVariable int id, Model model, HttpServletRequest request, HttpServletResponse response, String targetView) {
        init(request);
        callback(model, request, response, Constant2.REQUEST_TYPE_EDIT);
//		T currentBlog = this.getDao().get(id);
        T currentBlog=getById(id);
        model.addAttribute( getJspFolder(),currentBlog);
        model.addAttribute(Constant2.EDIT_FLAG, Constant2.YES);
        beforeEditInput(id,currentBlog,model);
        model.addAttribute(Constant2.KEY_MODEL_ATTRIBUTE, getJspFolder());//necessary and important
        commonAction(model);
        if(!ValueWidget.isNullOrEmpty(targetView)){
            return targetView;
        }
        return getJspFolder2() + VIEW_ADD;

    }

    @RequestMapping(value = "/{id}/edit/map"/*,method=RequestMethod.GET*/)
    public String editMap(@PathVariable int id, Model model, HttpServletRequest request, HttpServletResponse response, String targetView) {
        init(request);
        callback(model, request, response, Constant2.REQUEST_TYPE_EDIT);
        T currentBlog=getById(id);
        setId2Model(model, currentBlog);
        model.addAttribute(getJspFolder()+"_id",String.valueOf(id));
        model.addAttribute(Constant2.EDIT_FLAG, Constant2.YES);
        beforeEditInput(id,currentBlog,model);
        model.addAttribute(Constant2.KEY_MODEL_ATTRIBUTE, getJspFolder());//necessary and important
        commonAction(model);
        if(!ValueWidget.isNullOrEmpty(targetView)){
            return targetView;
        }
        return getJspFolder2() + VIEW_ADD;

    }

    @ResponseBody
    @RequestMapping(value = "/jsonStub", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    public String json(Model model, HttpSession session, @RequestBody byte[]bytes,
                       HttpServletRequest request, String callback) throws IOException {
        Map map=WebServletUtil.parseRequest(request, null);
        if(ValueWidget.isNullOrEmpty(map)){
            String postStr=new String(bytes,SystemHWUtil.CURR_ENCODING);//username=huangwei&password=123
            System.out.println("postStr:"+postStr);//username=%E9%BB%84%E5%A8%81&password=123
            postStr=URLDecoder.decode(postStr,SystemHWUtil.CURR_ENCODING);//{"username":"黄威","password":"123"}
            map=WebServletUtil.parseRequestStr(postStr, true);
        }
        String content = HWJacksonUtils.getJsonP(map, callback, false);
        return content;
    }

    /***
     * 可以被覆写
     */
    protected  void beforeEditInput(int id, T currentBlog, Model model){
    }

    /***
     * 需要覆写<br >
     *     子类有可能有相同的方法名,所以修改方法名称"update"为"updateByIdliuyu"
     * @param roleLevel
     * @param model
     * @param targetView
     * @param targetView2 : 优先级比targetView 高
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws NoSuchFieldException
     */
    @RequestMapping(value = "/{id}/update",method=RequestMethod.POST)
    public String updateByIdliuyu(@PathVariable int id, T roleLevel, Model model, HttpServletRequest request, HttpServletResponse response, String targetView, String targetView2) {
        updateCommon(id, roleLevel, model, request, response);
        String resultUrl=getRedirectViewAll() + "?fsdf=" + new Date().getTime();
        if(!ValueWidget.isNullOrEmpty(targetView2)){
            return targetView2;
        }
        if (ValueWidget.isNullOrEmpty(targetView)) {
            targetView = request.getParameter("targetView");
        }
        if(!ValueWidget.isNullOrEmpty(targetView)){
            resultUrl=resultUrl+"&targetView="+targetView;//先调用list刷新数据,在导向targetView
        }
        return resultUrl;
    }

    @ResponseBody
    @RequestMapping(value = "/{id}/update/json/{column}"/*,method=RequestMethod.GET*/, produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    public String update(@PathVariable int id, @PathVariable String column, String val, Model model, HttpServletRequest request, HttpServletResponse response, String targetView) {
        return updateSpecialAction(id, column, val, request);

    }

    /***
     * url :/test/753/update/json/status/val/1
     * @param id
     * @param column
     * @param val
     * @param model
     * @param request
     * @param response
     * @param targetView
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/{id}/update/json/{column}/val/{val}"/*,method=RequestMethod.GET*/, produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    public String updateSpecail(@PathVariable int id, @PathVariable String column, @PathVariable String val, Model model, HttpServletRequest request, HttpServletResponse response, String targetView) {
        return updateSpecialAction(id, column, val, request);
    }

    /***
     * 更新单个字段(属性)
     * @param id
     * @param column
     * @param val
     * @param request
     * @return
     */
    public String updateSpecialAction(int id, String column, String val, HttpServletRequest request) {
        if (ValueWidget.isNullOrEmpty(column)) {
            logger.error("column is null");
            return Constant2.RESPONSE_WRONG_RESULT;
        }
        init(request);
        Object oldVal = this.getDao().getOnePropertyById2(id, column);
//        this.getDao().updateSpecial(id, column, val); TODO
        BaseResponseDto baseResponseDto = new BaseResponseDto();
        baseResponseDto.result = true;

        Map map = new HashMap();
        map.put("oldVal", oldVal);
        map.put("newVal", val);
        baseResponseDto.setValue(map);
        return baseResponseDto.toJson();
    }

    /***
     * 返回json
     * @param id
     * @param roleLevel
     * @param model
     * @param request
     * @param targetView
     * @return
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    @ResponseBody
    @RequestMapping(value = "/{id}/update/json", method = RequestMethod.POST, produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    public String jsonUpdate(@PathVariable int id, T roleLevel, Model model, HttpServletRequest request, HttpServletResponse response, String targetView) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        return jsonUpdateAction(id, roleLevel, model, request, response);
    }

    @ResponseBody
    @RequestMapping(value = "/{id}/json/update", method = RequestMethod.POST, produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    @Deprecated
    public String jsonUpdate2(@PathVariable int id, T roleLevel, Model model, HttpServletRequest request, HttpServletResponse response, String targetView) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        logger.warn("建议使用接口:/" + id + "/update/json");
        return jsonUpdateAction(id, roleLevel, model, request, response);
    }

    public String jsonUpdateAction(@PathVariable int id, T roleLevel, Model model, HttpServletRequest request, HttpServletResponse response) throws NoSuchFieldException, IllegalAccessException {
        boolean success = updateCommon(id, roleLevel, model, request, response);
//        Map map=new HashMap();
        BaseResponseDto baseResponseDto = new BaseResponseDto();
        if (success) {
//            return Constant2.RESPONSE_RIGHT_RESULT;
            baseResponseDto.result = true;
//            map.put(Constant2.LOGIN_RESULT_KEY, true);
            baseResponseDto.setValue(roleLevel);
//            map.put("value", );
            return HWJacksonUtils.getJsonP(baseResponseDto);
        }
//        map.put(Constant2.LOGIN_RESULT_KEY, false);
        baseResponseDto.setErrorMessage("未找到记录,id:" + id);
//        map.put(Constant2.RESPONSE_KEY_ERROR_MESSAGE, "未找到记录,id:" + id);
        return HWJacksonUtils.getJsonP(baseResponseDto);
    }

    protected boolean updateCommon(int id, T roleLevel, Model model, HttpServletRequest request, HttpServletResponse response) {
        init(request);
        if (null == roleLevel) {
            String msg = "java bean is null,id:" + id;
            System.out.println(msg);
            logger.error(msg);
            roleLevel = (T) getDao().createEmptyObj();
            ReflectHWUtils.setObjectValue(roleLevel, "id", id);
        }
        T t=getById(id);
        beforeUpdate(roleLevel, t);
        callback(model, request, response, Constant2.REQUEST_TYPE_UPDATE);
        if(ValueWidget.isNullOrEmpty(t)){//要更新的对象不存在
            return false;
        }
        ReflectHWUtils.fillTimeForEditedObj(roleLevel, t);
        this.getDao().update(roleLevel);
        commonAction(model);
        return true;
    }

    /***
     * 应该被覆写
     * @param roleLevel
     */
    protected void beforeUpdate(T roleLevel, T justQuery) {
//		try {
//			ReflectHWUtils.fillTimeForObj(roleLevel);
//		} catch (SecurityException e) {
//			e.printStackTrace();
//		} catch (NoSuchFieldException e) {
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		}

    }

    /***
     * 获取jsp所在目录,用于兼容移动端
     * @return
     */
    protected  String getJspFolder2(){
        if (ValueWidget.isNullOrEmpty(deviceTypePathMap())) {
            return getJspFolder();
        }
        String path=deviceTypePathMap().get(getDeviceType());
        return (path == null ? SystemHWUtil.EMPTY : path) + getJspFolder();
    }

    public abstract String getJspFolder();
    /***
     * 可以自定义,但是最好和getJspFolder()一致
     * @return
     */
//	protected abstract String getRequestUri();

    /***
     * 浏览器端跳转
     * @return
     */
    public String getRedirectViewAll() {
        String path = null;
        if (ValueWidget.isNullOrEmpty(deviceTypePathMap())) {
            path = SystemHWUtil.EMPTY;
        } else {
            path = deviceTypePathMap().get(getDeviceType());
        }
        return Constant2.SPRINGMVC_REDIRECT_PREFIX
                + getJspFolder2() + (path == null ? SystemHWUtil.EMPTY : path) + VIEW_LIST;
    }

    protected void saveCommon(T roleLevel, Model model) {
        // 查询时可以使用 isNotNull
        PageUtil.convertEmpty2Null(roleLevel);
        this.getDao().add(roleLevel);
        afterSave(roleLevel, model);
//		commonAction(model);
    }

    /***
     * 可以被子类覆写
     *
     * @param roleLevel
     * @param model
     */
    protected void afterSave(T roleLevel, Model model) {
        model.addAttribute(getJspFolder(), roleLevel);
    }

    /***
     * jsp页面中会使用label
     * @param model
     */
    protected void commonAction(Model model){
        model.addAttribute(Constant2.KEY_LABEL, getJspFolder());
    }

    /***
     * 该接口不是通用接口,当时是供电视台使用的
     * @param model
     * @param roleLevel
     * @param type
     * @param view
     * @param session
     * @param request
     * @param targetView
     * @return
     */
    @RequestMapping(value = "/list/{type}")
    public String listByType(Model model, T roleLevel, @PathVariable int type, PageView view, HttpSession session, HttpServletRequest request, HttpServletResponse response, String targetView
            , Boolean returnJson) {
        if(roleLevel==null){
            roleLevel=(T) getDao().createEmptyObj();
            ReflectHWUtils.setObjectValue(roleLevel, "type", type);
        }
        init(request);
        return listCommon(model, roleLevel, view, session, request, response, targetView, returnJson);
    }

    /***
     * select
     count(*) as y0_
     from
     t_test_to_boy this_
     where
     this_.status=?
     and (
     (
     this_.testcase like ?
     or this_.alias like ?
     )
     or this_.alias2 like ?
     )<br >
     * @param model
     * @param roleLevel
     * @param view
     * @param session
     * @param request
     * @param targetView
     * @param columnsArr : 字符串,以逗号(,)分隔
     * @param keyword : 如果columnsArr 为空,则直接忽略参数keyword
     * @return
     */
    @RequestMapping(value = VIEW_LIST + "/page/{currentPage}")
    public String listPage(Model model, T roleLevel, PageView view, @PathVariable Integer currentPage, HttpSession session, HttpServletRequest request, HttpServletResponse response, String targetView, String columnsArr, String keyword, String isAccurate
            , Boolean returnJson) {
        init(request);
        if (null != currentPage) {
            view.setCurrentPage(currentPage);
        }
        return listAction(model, roleLevel, view, session, request, response, targetView, columnsArr, keyword, isAccurate, returnJson);
    }

    /***
     * 查询列表
     * @param model
     * @param roleLevel
     * @param view
     * @param session
     * @param request
     * @param response
     * @param targetView
     * @param columnsArr
     * @param keyword
     * @param isAccurate
     * @return
     */
    @RequestMapping(value = VIEW_LIST)
    public String list(Model model, T roleLevel, PageView view, HttpSession session, HttpServletRequest request, HttpServletResponse response, String targetView, String columnsArr, String keyword, String isAccurate
            , Boolean returnJson) {
        init(request);
        return listAction(model, roleLevel, view, session, request, response, targetView, columnsArr, keyword, isAccurate, returnJson);
    }

    public String listAction(Model model, T roleLevel, PageView view, HttpSession session, HttpServletRequest request, HttpServletResponse response, String targetView, String columnsArr, String keyword, String isAccurate
            , Boolean returnJson) {
        if (ValueWidget.isNullOrEmpty(columnsArr)) {
            return listCommon(model, roleLevel, view, session, request, response, targetView, returnJson);
        }
        columnsArr = columnsArr.replace("%", SystemHWUtil.EMPTY).replace("=", SystemHWUtil.EMPTY)
                .replace(SystemHWUtil.BLANK, SystemHWUtil.EMPTY);
        String[] columns = columnsArr.split(",");
        return listCommon(model, roleLevel, view, session, request, response, targetView, columns, keyword, "1".equals(isAccurate), returnJson);

    }

    protected String listCommon(Model model, T roleLevel, PageView view, HttpSession session, HttpServletRequest request, HttpServletResponse response, String targetView, Boolean returnJson) {
        return listCommon(model, roleLevel, view, session, request, response, targetView, (String[]) null, null, false, returnJson);
    }

    private String listCommon(Model model, T roleLevel, PageView view, HttpSession session, HttpServletRequest request, HttpServletResponse response, String targetView
            , String[] columns, String keyword, Boolean returnJson) {
        return listCommon(model, roleLevel, view, session, request, response, targetView, columns, keyword, false, returnJson);
    }

    /***
     *
     * @param model
     * @param roleLevel
     * @param view
     * @param session
     * @param request
     * @param targetView
     * @param columns : 如果columns 为空,则直接忽略参数keyword
     * @param keyword : 如果columns 为空,则直接忽略参数keyword
     * @param returnJson : 是返回json 还是页面(例如jsp 或者 freeMark)
     * @return
     */
    private String listCommon(Model model, T roleLevel, PageView view, HttpSession session, HttpServletRequest request, HttpServletResponse response, String targetView
            , String[] columns, String keyword, boolean isAccurate, Boolean returnJson) {
        String sessionKey=getJspFolder();
        String session_keyword_columns = "keyword_columns";
        String session_alias_keyword233 = "alias_keyword233";
        //兼容小写的"currentPage"
        String currentPageTmp = request.getParameter("currentpage");
        if (!ValueWidget.isNullOrEmpty(currentPageTmp)) {
            view.setCurrentPage(Integer.parseInt(currentPageTmp));
        }
        if(!ValueWidget.isNullOrEmpty(view.getPageFlag())&&view.getPageFlag().equals(Constant2.PAGEFLAG_NOT_QUERY)){
            System.out.println("不是查询");
            roleLevel=(T)session.getAttribute(sessionKey);
            columns = (String[]) session.getAttribute(session_keyword_columns);
            if (!ValueWidget.isNullOrEmpty(columns)) {
                keyword = (String) session.getAttribute(session_alias_keyword233);
            }
            if(!ValueWidget.isNullOrEmpty(roleLevel)){
                try {
                    BeanUtils.copyProperties(view, roleLevel);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }else{//查询
            System.out.println("是查询");
            if (ValueWidget.isNullOrEmpty(roleLevel) && !ValueWidget.isNullOrEmpty(sessionKey)) {
                session.removeAttribute(sessionKey);
            } else {
                session.setAttribute(sessionKey, roleLevel);//恢复现场
            }
            if (!ValueWidget.isNullOrEmpty(columns) && !ValueWidget.isNullOrEmpty(keyword)) {
                session.setAttribute(session_keyword_columns, columns);
                session.setAttribute(session_alias_keyword233, keyword);
            } else {
                session.removeAttribute(session_keyword_columns);//恢复现场
                session.removeAttribute(session_alias_keyword233);
            }
        }
        if(ValueWidget.isNullOrEmpty(roleLevel)){
            roleLevel=(T) getDao().createEmptyObj();
        }
        beforeList(roleLevel);
        setRecordsPerPageBeforeQuery(view);
        if (ValueWidget.isNullOrEmpty(columns) || ValueWidget.isNullOrEmpty(keyword)) {//如果columns 为空,则直接忽略最后一个参数keyword
            listPaging(roleLevel, view, getListOrderBy());
        } else {//见方法 com/common/dao/generic/UniversalDao.java 218行 getCriteria()
            view = searchAction(roleLevel, view, columns, keyword, isAccurate);
        }
        long startMillisecond = System.currentTimeMillis();
        System.out.println("startMillisecond :" + startMillisecond);
        listTODO(model, view, request, response);
        long endMillisecond = System.currentTimeMillis();
        System.out.println("endMillisecond :" + endMillisecond);
        long delta = endMillisecond - startMillisecond;
        if (delta > 500) {
            logger.error("listCommon5 delta time:\t" + delta + " millisecond from " + SpringMVCUtil.getRequest().getRequestURI());
        }
        if (delta > 2000) {
            HWUtils.sendHttpRefreshDictionary(request);
        }
        callback(model, request, response, Constant2.REQUEST_TYPE_LIST);
        if (null != returnJson && returnJson) {
            if (responseJson(view, response)) return null;
        }
        model.addAttribute("view", view);
        commonAction(model);
        model.addAttribute(Constant2.KEY_CURRENT_TIME, TimeHWUtil.getCurrentTimestamp().getTime());

        if (!ValueWidget.isNullOrEmpty(targetView)) {
            return targetView;
        }
        return getJspFolder2() + getListView();
    }

    /***
     * response 返回 json
     * @param view
     * @param response
     * @return
     */
    public static boolean responseJson(PageView view, HttpServletResponse response) {
        PrintWriter printWriter = null;
        response.setCharacterEncoding(SystemHWUtil.CHARSET_UTF);
        response.setContentType(SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF);
        try {
            printWriter = response.getWriter();
            int length = view.getRecordList().size();
            for (int i = 0; i < length; i++) {
                ReflectHWUtils.skipHibernatePersistentBag(view.getRecordList().get(i));
            }

            printWriter.write(HWJacksonUtils.getJsonP(view));
            printWriter.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /***
     * 子类 可以重写<br />
     * 搜索
     * @param roleLevel
     * @param view
     * @param columns
     * @param keyword : 搜索的关键字
     * @param isAccurate
     * @return
     */
    protected PageView searchAction(T roleLevel, PageView view, String[] columns, String keyword, boolean isAccurate) {
        Map condition = null;
        condition = ReflectHWUtils.convertObj2Map(roleLevel, columns, true);
            /*
            * select
        count(*) as y0_
    from
        t_test_to_boy this_
    where
        this_.status=?
        and (
            (
                this_.testcase like ?
                or this_.alias like ?
            )
            or this_.alias2 like ?
        )
            * */
        PageUtil.paging(condition, columns, keyword, view, getDao(), getListOrderBy(), isAccurate);
        return view;
    }

    /***
     * 设置每页显示的条数
     * @param view
     */
    protected void setRecordsPerPageBeforeQuery(PageView view) {
    }


    @ResponseBody
    @RequestMapping(value = "/json_save"/** 接口名称不规范 */, produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    @Deprecated
    public String jsonSave(Model model,
                           T t, HttpSession session,
                           HttpServletRequest request, String callback) throws IOException {
        logger.warn("建议使用接口:/save/json");
        return jsonSaveAction(t, request);
    }

    @ResponseBody
    @RequestMapping(value = "/save/json", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    public String jsonSave2(Model model,
                            T t, HttpSession session,
                            HttpServletRequest request, String callback) throws IOException {
        return jsonSaveAction(t, request);
    }

    public String jsonSaveAction(T t, HttpServletRequest request) {
        T roleLevelTmp = reqJson(request);
        if (null != roleLevelTmp) {
            t = roleLevelTmp;
        }
        init(request);
//        Map map=new HashMap();
        if(ValueWidget.isNullOrEmpty(t)){
//            map.put(Constant2.LOGIN_RESULT_KEY, false);
//            map.put(Constant2.RESPONSE_KEY_ERROR_MESSAGE, );
            return HWJacksonUtils.getJsonP(new BaseResponseDto("", t.getClass().getSimpleName() + " is null"));
        }
        getDao().add(t);
        BaseResponseDto baseResponseDto = new BaseResponseDto();
        baseResponseDto.result = true;
//        map.put(Constant2.LOGIN_RESULT_KEY, true);
        baseResponseDto.setValue(t);
//        map.put("value", t);
        return HWJacksonUtils.getJsonP(baseResponseDto);
//        return Constant2.RESPONSE_RIGHT_RESULT;
    }

    /***
     * http://127.0.0.1:8080/tv_mobile/dictionary/json_list?recordsPerPage=-1&groupId=broadcast_group
     * <br>http://127.0.0.1:8080/tv_mobile/house/json_list?recordsPerPage=-1
     * @param model
     * @param t
     * @param session
     * @param request
     * @param view
     * @param callback
     * @param filterColumn : 要过滤的成员变量,即会设置该成员变量为null<br />
     *                     为什么呢?因为json序列化会报错:<br />
    org.codehaus.jackson.map.JsonMappingException: failed to lazily initialize a collection of role: com.girltest.entity.Test2Boy.conventions, could not initialize proxy - no Session (through reference chain: java.util.HashMap["recordList"]->java.util.ArrayList[0]->com.girltest.entity.Test2Boy["conventions"])
    at org.codehaus.jackson.map.JsonMappingException.wrapWithPath(JsonMappingException.java:218)

     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(value = "/json_list"/** 接口名称不规范 */, produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    @Deprecated
    public String jsonList(Model model, T t, HttpSession session,
                           String orderBy, String orderMode,
                           HttpServletRequest request, PageView view, String callback, String filterColumn) throws IOException {
        logger.warn("建议使用接口:/list/json");
        return jsonListAction(t, orderBy, orderMode, request, view, callback, filterColumn);
    }

    @ResponseBody
    @RequestMapping(value = "/list/json", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    public String jsonList2(Model model, T t, HttpSession session,
                            String orderBy, String orderMode,
                            HttpServletRequest request, PageView view, String callback, String filterColumn) throws IOException {
        return jsonListAction(t, orderBy, orderMode, request, view, callback, filterColumn);
    }

    public String jsonListAction(T t, String orderBy, String orderMode, HttpServletRequest request, PageView view, String callback, String filterColumn) {
        init(request);
        if (ValueWidget.isNullOrEmpty(orderMode)) {
            orderMode = "asc";
        }
        String content;
        if(view.getRecordsPerPage()==SystemHWUtil.NEGATIVE_ONE){
            content = HWJacksonUtils.getJsonP(getDao().find(t, false, orderBy, orderMode, false, false), callback, false);
        }else{
            Map map = new HashMap();
            PageUtil.paging(t, false, view, getDao(), orderMode, orderBy, null,
                    null, null);
            setJsonPaging(map, view, filterColumn);
            content = HWJacksonUtils.getJsonP(map, callback, false);
        }

        return content;
    }

    protected void beforeList(T roleLevel) {
    }

    /***
     * 子类可以对view中的recordList进行操作
     * @param view
     */
    protected void listTODO(Model model, PageView view, HttpServletRequest request, HttpServletResponse response) {
    }

    /***
     * 如果想对指定列排序,可以覆写
     * @param roleLevel
     * @param view
     */
    protected void listPaging(T roleLevel, PageView view, ListOrderedMap orderColumnModeMap){
        if(ValueWidget.isNullOrEmpty(dao)){
            System.out.println("[listPaging]dao is null");
            logger.error("dao is null");
            return;
        }
        PageUtil.paging(roleLevel,true,view, getDao(),null,orderColumnModeMap);
    }

    protected GenericDao getDao() {
        if (this.dao != null) {
            return this.dao;
        }
        String errorMessage = "请先执行init(request)";
        System.out.println(errorMessage);
        logger.error(errorMessage);
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        init(request);
        return this.dao;
    }

    public int getDeviceType() {
        return this.deviceType;
    }

    /***
     * 设备类型和路径映射map<br>
     * TODO 需要子类重写
     * <br>
     * @return
     */
    protected  Map<Integer,String> deviceTypePathMap(){
//		return DictionaryParam.getMap("device_type_path");
        return null;
    }

    protected void setJsonPaging(Map map, PageView view){
        setJsonPaging(map, view, null);
    }

    /***
     *
     * @param map
     * @param view
     * @param filterColumn : 要过滤的成员变量,即会设置该成员变量为null<br />
     *                     为什么呢?因为json 序列化会报错:<br />
     */
    protected void setJsonPaging(Map map, PageView view, String filterColumn) {
        List data = view.getRecordList();
        map.put(Constant2.JSON_RETURN_CURRENTPAGE, view.getCurrentPage());
        map.put(Constant2.JSON_RETURN_LENGTH, data.size());
        map.put(Constant2.JSON_RETURN_SUM, view.getTotalRecords());
        map.put(Constant2.JSON_RETURN_OVER,
                view.getCurrentPage() >= view.getTotalPages());
        map.put(Constant2.JSON_RETURN_RECORDSPERPAGE, view.getRecordsPerPage());//每页最多显示多少条
        for (Object object : data) {
            if (ValueWidget.isNullOrEmpty(filterColumn)) {
                ReflectHWUtils.skipHibernatePersistentBag(object);
            } else {
                ReflectHWUtils.setObjectValue(object, filterColumn, null, false);
            }
        }
        map.put("recordList", data);
    }

    public ListOrderedMap getListOrderBy(){
        return null;
    }

    /***
     * 子类可以覆写<br>
     * 默认为"/list"
     * @return
     */
    protected String getListView(){
        return VIEW_LIST;
    }

    protected void setEntityClz(Class clz) {
        this.clz = clz;
    }


    /***
     * 执行回调<br />
     * 类似于spring MVC的aop,可以:
     * (1)获取设备类型,Pc端,wap端;
     * (2)可以对数据进行二次处理;
     * (3)可以记录日志
     * (4)可以增加一些特殊逻辑
     * @param model
     * @param request
     * @param response
     * @return
     */
    protected String callback(Model model, HttpServletRequest request, HttpServletResponse response, int requestType) {
        if (null == this.getRequestCallback()) {
            //后面需要优化:只有登录时判断一次,就够了
            //获取操作系统类型和设备类型
            ClientOsInfo info = WebServletUtil.getMobileOsInfo(request);
            request.getSession(true).setAttribute(Constant2.SESSION_KEY_CLIENT_OS_INFO, info);
            return null;
        } else {
            try {
                return this.getRequestCallback().callback(model, request, response, requestType);
            } catch (ParseException e) {
                e.printStackTrace();
                logger.error(e.getMessage(), e);
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(e.getMessage(), e);
            }
            return null;
        }
    }

    public RequestCallback getRequestCallback() {
        return requestCallback;
    }

    public void setRequestCallback(RequestCallback requestCallback) {
        this.requestCallback = requestCallback;
    }
}
