package oa.web.controller.generic;

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
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GenericController <T>{

	public static final String VIEW_LIST = "/list";
    protected static final Logger logger = Logger.getLogger(GenericController.class);
    protected final Class<T> clz = SystemHWUtil.getGenricClassType(getClass());
	protected GenericDao<T>dao;
	/***
	 * 设备类型
	 */
	protected int deviceType;

	/***
	 * 从WebApplicationContext 获取dao
	 * @param request
	 * @return
	 */
	private  GenericDao<T> getDaoByWebApp(HttpServletRequest request){
		if(ValueWidget.isNullOrEmpty(dao)){
			WebApplicationContext webApp=RequestContextUtils.getWebApplicationContext(request	, request.getSession().getServletContext());
			try {
				dao = (GenericDao<T>) webApp.getBean(ValueWidget.title(SystemHWUtil.getFileSuffixName(clz.getName() + Constant2.DAO)));//返回的是同一个对象
			} catch (NoSuchBeanDefinitionException e) {
//				e.printStackTrace();
				dao = (GenericDao<T>) webApp.getBean(SystemHWUtil.getFileSuffixName(clz.getName() + Constant2.DAO));//返回的是同一个对象
			}
//			System.out.println("dao:"+dao);
		}
		return dao;
	}
	/***
	 * 
	 * @param request
	 * @param beanName : 例如 userDao ,adminDao
	 * @return
	 */
	public Object getBean(HttpServletRequest request,String beanName){
		WebApplicationContext webApp=RequestContextUtils.getWebApplicationContext(request, request.getSession().getServletContext());
		return webApp.getBean(beanName);
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
	 * 不是保存,而是进入添加页面
	 * @param practiceWay : 区分手机端和PC端
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/add",method=RequestMethod.GET)
	public String addInput(String practiceWay, Model model,HttpServletRequest request,String targetView) {
		addCommonAction(model, request);
		model.addAttribute(getJspFolder(),getDao().createEmptyObj());//用于spring MVC 的sf标签
		if(!ValueWidget.isNullOrEmpty(targetView)){
			return targetView;
		}
		return getJspFolder2()+"/add";
	}

	private void addCommonAction(Model model, HttpServletRequest request) {
		init(request);
		commonAction(model);
		beforeAddInput(model);
		model.addAttribute(Constant2.KEY_MODEL_ATTRIBUTE, getJspFolder());//necessary and important
	}

	@RequestMapping(value = "/add/map",method=RequestMethod.GET)
	public String addInputMap(String practiceWay, Model model,HttpServletRequest request,String targetView) {
		addCommonAction(model, request);
		setId2Model(model, (T) getDao().createEmptyObj());
		if(!ValueWidget.isNullOrEmpty(targetView)){
			return targetView;
		}
		return getJspFolder2()+"/add";
	}
	/***
	 * 可以覆写
	 * @param model
	 */
	protected abstract void beforeAddInput(Model model);//{
//		List<T> roles=this.getDao().getAll();
//		model.addAttribute(roles);//选择上级
//	}
	/***
	 * 根据请求方式(GET,POST)区分<br>保存
	 * @param roleLevel
	 * @param binding
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/add",method=RequestMethod.POST)
	public String save(@Valid T roleLevel, BindingResult binding, Model model,HttpServletRequest request,String targetView) {
		init(request);

		if (!saveValidate(roleLevel, binding, model)) {
			return getJspFolder2() + "/add";
		}
		beforeSave(roleLevel,model);
		
		saveCommon(roleLevel, model);
		commonAction(model);
		if(!ValueWidget.isNullOrEmpty(targetView)){
			return targetView;
		}
		return getRedirectViewAll();
	}

	@ResponseBody
	@RequestMapping(value = "/add_json", method = RequestMethod.POST)
	public String save_json(@Valid T roleLevel,
							BindingResult binding,
							Model model,
							HttpServletRequest request,
							String callback) {
		init(request);
		Map map = new HashMap();
		int login_result = 0;
		if (saveValidate(roleLevel, binding, model)) {
			login_result = Constant2.LOGIN_RESULT_SUCCESS;
			beforeSave(roleLevel, model);
			saveCommon(roleLevel, model);
			commonAction(model);
		}
		map.put(Constant2.LOGIN_RESULT_KEY, login_result);
		String content = HWJacksonUtils.getJsonP(map, callback);
		return content;
	}


	private boolean saveValidate(@Valid T roleLevel, BindingResult binding, Model model) {
		if (binding.hasErrors()) {
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
		return true;
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
	protected  void beforeSave(T roleLevel,Model model){
		if(!ValueWidget.isNullOrEmpty(roleLevel)){
			try {
				ReflectHWUtils.fillTimeForObj(roleLevel);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
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
	public String detail(@PathVariable int id, Model model,HttpServletRequest request,String targetView) {
		init(request);
		
		T t=detailTODO(id,model, request);
		model.addAttribute(getJspFolder(), t);
		commonAction(model);
		if(!ValueWidget.isNullOrEmpty(targetView)){
			return targetView;
		}
		return getJspFolder2()+"/detail";
	}
	
	@RequestMapping(value = "/{id}/map")
	public String detailMap(@PathVariable int id, Model model,HttpServletRequest request,String targetView) {
		init(request);
		
		T roleLevel=detailTODO(id,model, request);
		setId2Model(model, roleLevel);
		commonAction(model);
		if(!ValueWidget.isNullOrEmpty(targetView)){
			return targetView;
		}
		return getJspFolder2()+"/detail";
	}

	private void setId2Model(Model model, T roleLevel) {
		try {
			model.addAttribute(getJspFolder(), ReflectHWUtils.parseObject(roleLevel, Constant2.DB_ID));
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}//用于spring MVC 的sf标签
	}

	/***
	 * 可以被覆写(override)
	 * @param id
	 * @param model
	 * @param request
     * @return
     */
	protected T detailTODO(int id,Model model,HttpServletRequest request){
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
	protected void deleteTODO(int id,T roleLevel,Model model,HttpServletRequest request){
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
	public String deleteOne(@PathVariable int id, Model model,HttpServletRequest request,String targetView) {
		boolean success=deleteCommon(id, model, request);
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
	@RequestMapping(value = "/{id}/delete/json")
	public String json_deleteOne(@PathVariable int id, Model model,HttpServletRequest request,String targetView) {
		boolean success=deleteCommon(id, model, request);
		Map map=new HashMap();
		if(!success){
			map.put(Constant2.LOGIN_RESULT_KEY, false);
			map.put(Constant2.RESPONSE_KEY_ERROR_MESSAGE, "未找到记录,id:"+id);
			return HWJacksonUtils.getJsonP(map);
		}
		return Constant2.RESPONSE_RIGHT_RESULT;
	}
	
	private boolean deleteCommon(int id, Model model,HttpServletRequest request){
		init(request);
//		System.out.println("delete a os type :"+id);
//		T newsToDel = this.getDao().get(id);
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
	public String edit(@PathVariable int id, Model model,HttpServletRequest request,String targetView) {
		init(request);
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
		return getJspFolder2()+"/add";

	}
	@RequestMapping(value = "/{id}/edit/map"/*,method=RequestMethod.GET*/) 
	public String editMap(@PathVariable int id, Model model,HttpServletRequest request,String targetView) {
		init(request);
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
		return getJspFolder2()+"/add";

	}
	@ResponseBody
	@RequestMapping(value = "/jsonStub", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
	public String json(Model model, HttpSession session,@RequestBody byte[]bytes,
			HttpServletRequest request, String callback) throws IOException {
		Map map=WebServletUtil.parseRequest(request, null);
		if(ValueWidget.isNullOrEmpty(map)){
			String postStr=new String(bytes,SystemHWUtil.CURR_ENCODING);//username=huangwei&password=123
			System.out.println("postStr:"+postStr);//username=%E9%BB%84%E5%A8%81&password=123
			postStr=URLDecoder.decode(postStr,SystemHWUtil.CURR_ENCODING);//{"username":"黄威","password":"123"}
			map=WebServletUtil.parseRequestStr(postStr, true);
		}
		String content = HWJacksonUtils.getJsonP(map, callback);
		return content;
	}
	/***
	 * 可以被覆写
	 */
	protected  void beforeEditInput(int id,T currentBlog, Model model){
	}
	/***
	 * 需要覆写
	 * @param roleLevel
	 * @param model
	 * @param targetView
	 * @param targetView2 : 优先级比targetView 高
	 * @return
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws NoSuchFieldException 
	 * @throws SecurityException 
	 */
	@RequestMapping(value = "/{id}/update",method=RequestMethod.POST) 
	public String update(@PathVariable int id,T roleLevel, Model model,HttpServletRequest request,String targetView, String targetView2) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		updateCommon(id, roleLevel, model, request);
		String resultUrl=getRedirectViewAll() + "?fsdf=" + new Date().getTime();
		if(!ValueWidget.isNullOrEmpty(targetView2)){
			return targetView2;
		}
		if(!ValueWidget.isNullOrEmpty(targetView)){
			resultUrl=resultUrl+"&targetView="+targetView;//先调用list刷新数据,在导向targetView
		}
		return resultUrl;
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
	@RequestMapping(value = "/{id}/update/json",method=RequestMethod.POST) 
	public String json_update(@PathVariable int id,T roleLevel, Model model,HttpServletRequest request,String targetView) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		boolean success= updateCommon(id, roleLevel, model, request);
		Map map=new HashMap();
		if(!success){
			map.put(Constant2.LOGIN_RESULT_KEY, false);
			map.put(Constant2.RESPONSE_KEY_ERROR_MESSAGE, "未找到记录,id:"+id);
			return HWJacksonUtils.getJsonP(map);
		}
		return Constant2.RESPONSE_RIGHT_RESULT;
		
	}
	protected boolean updateCommon(int id,T roleLevel, Model model,HttpServletRequest request) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
		init(request);
		beforeUpdate(roleLevel);
		T t=getById(id);
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
	protected  void beforeUpdate(T roleLevel){
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
		String path=deviceTypePathMap().get(getDeviceType());
		return Constant2.SPRINGMVC_REDIRECT_PREFIX
				+ getJspFolder2() + (path == null ? SystemHWUtil.EMPTY : path) + VIEW_LIST;
	}
	protected void saveCommon(T roleLevel, Model model) {
		// 查询时可以使用 isNotNull
		if (!ValueWidget.isNullOrEmpty(roleLevel)) {
			try {
				// 把对象中空字符串改为null
				ReflectHWUtils.convertEmpty2Null(roleLevel);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
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

	@RequestMapping(value = "/list/{type}")
	public String listByType(Model model,T roleLevel,@PathVariable int type,PageView view,HttpSession session,HttpServletRequest request,String targetView) {
		if(roleLevel==null){
			roleLevel=(T) getDao().createEmptyObj();
			try {
				ReflectHWUtils.setObjectValue(roleLevel, "type", type);
			} catch (SecurityException e) {
				e.printStackTrace();
				logger.error(e);
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
				logger.error(e);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				logger.error(e);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				logger.error(e);
			}
		}
		init(request);
		return listCommon(model, roleLevel, view, session, request, targetView);
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
    @RequestMapping(value = VIEW_LIST)
    public String list(Model model, T roleLevel, PageView view, HttpSession session, HttpServletRequest request, String targetView, String columnsArr, String keyword) {
        init(request);
        if (ValueWidget.isNullOrEmpty(columnsArr)) {
            return listCommon(model, roleLevel, view, session, request, targetView);
        } else {
            String[] columns = columnsArr.split(",");
            return listCommon(model, roleLevel, view, session, request, targetView, columns, keyword);
        }

    }

    private String listCommon(Model model, T roleLevel, PageView view, HttpSession session, HttpServletRequest request, String targetView) {
        return listCommon(model, roleLevel, view, session, request, targetView, (String[]) null, null);
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
     * @return
     */
    private String listCommon(Model model, T roleLevel, PageView view, HttpSession session, HttpServletRequest request, String targetView
            , String[] columns, String keyword) {
        String sessionKey=getJspFolder();
		if(!ValueWidget.isNullOrEmpty(view.getPageFlag())&&view.getPageFlag().equals(Constant2.PAGEFLAG_NOT_QUERY)){
			System.out.println("不是查询");
			roleLevel=(T)session.getAttribute(sessionKey);
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
			if(!ValueWidget.isNullOrEmpty(roleLevel)){
				session.setAttribute(sessionKey, roleLevel);
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
            Map condition = null;
            try {
                condition = ReflectHWUtils.convertObj2Map(roleLevel, columns, true);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
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
            PageUtil.paging(condition, columns, keyword, view, getDao(), getListOrderBy());
        }

		listTODO(model,view,request);

		model.addAttribute("view", view);
		commonAction(model);
		model.addAttribute(Constant2.KEY_CURRENT_TIME, TimeHWUtil.getCurrentTimestamp().getTime());

		if(!ValueWidget.isNullOrEmpty(targetView)){
			return targetView;
		}
		return getJspFolder2()+getListView();
	}

	/***
	 * 设置每页显示的条数
	 * @param view
	 */
	protected void setRecordsPerPageBeforeQuery(PageView view) {
	}
	@ResponseBody
	@RequestMapping(value = "/json_save", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
	public String jsonSave(Model model,
			T t, HttpSession session,
			HttpServletRequest request, String callback) throws IOException {
		init(request);
		Map map=new HashMap();
		if(ValueWidget.isNullOrEmpty(t)){
			map.put(Constant2.LOGIN_RESULT_KEY, false);
			map.put(Constant2.RESPONSE_KEY_ERROR_MESSAGE, t.getClass().getSimpleName()+" is null");
			return HWJacksonUtils.getJsonP(map);
		}
		getDao().add(t);
		return Constant2.RESPONSE_RIGHT_RESULT;
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
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping(value = "/json_list", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
	public String json_list(Model model, T t, HttpSession session,
							String orderBy, String orderMode,
							HttpServletRequest request, PageView view, String callback) throws IOException {
		init(request);
		if (ValueWidget.isNullOrEmpty(orderMode)) {
			orderMode = "asc";
		}
		String content;
		if(view.getRecordsPerPage()==SystemHWUtil.NEGATIVE_ONE){
			content = HWJacksonUtils.getJsonP(getDao().find(t, false, orderBy, orderMode, false, false), callback);
		}else{
			Map map = new HashMap();
			PageUtil.paging(t, false, view, getDao(), orderMode, orderBy, null,
					null, null);
			setJsonPaging(map, view);
			content = HWJacksonUtils.getJsonP(map, callback);
		}
		
		return content;
	}
		
	protected void beforeList(T roleLevel) {
	}
	/***
	 * 子类可以对view中的recordList进行操作
	 * @param view
	 */
	protected void listTODO(Model model,PageView view,HttpServletRequest request){
	}
	/***
	 * 如果想对指定列排序,可以覆写
	 * @param roleLevel
	 * @param view
	 */
	protected void listPaging(T roleLevel,PageView view,ListOrderedMap orderColumnModeMap){
		if(ValueWidget.isNullOrEmpty(dao)){
			System.out.println("[listPaging]dao is null");
			logger.error("dao is null");
			return;
		}
		PageUtil.paging(roleLevel,true,view, getDao(),null,orderColumnModeMap);
	}
	protected GenericDao getDao() {
		if(this.dao==null){
			String errorMessage = "请先执行init(request)";
			System.out.println(errorMessage);
			logger.error(errorMessage);
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			init(request);
		}
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
	protected void setJsonPaging(Map map,PageView view){
		List data = view.getRecordList();
		map.put(Constant2.JSON_RETURN_CURRENTPAGE, view.getCurrentPage());
		map.put(Constant2.JSON_RETURN_LENGTH, data.size());
		map.put(Constant2.JSON_RETURN_SUM, view.getTotalRecords());
		map.put(Constant2.JSON_RETURN_OVER,
				view.getCurrentPage() >= view.getTotalPages());
		map.put(Constant2.JSON_RETURN_RECORDSPERPAGE, view.getRecordsPerPage());//每页最多显示多少条
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
}
