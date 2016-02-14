package oa.web.controller;

import com.common.dao.generic.GenericDao;
import com.common.util.SystemHWUtil;
import com.io.hw.json.HWJacksonUtils;
import com.io.hw.json.JSONHWUtil;
import com.string.widget.util.ValueWidget;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class DatabaseCustomizedController<T> {
	/***
	 * 需要登录
	 * @param model
	 * @param requeestBody
	 *            : {id:22,properties:["username","email"]} or
	 *            {id:1,properties:["username","email","password"]}
	 * @param session
	 * @param request
	 * @param response
	 * @param callback
	 * @return : map
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping(value = "/map", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
	public String map(Model model, @RequestBody String requeestBody,
			HttpSession session, HttpServletRequest request,
			HttpServletResponse response, String callback) throws IOException {
		String content = json(model, requeestBody, request, response, callback,true);
		return content;
	}
	/***
	 * 需要登录
	 * @param model
	 * @param requeestBody
	 *            : {id:22,properties:["username","email"]} or
	 *            {id:1,properties:["username","email","password"]}
	 * @param session
	 * @param request
	 * @param response
	 * @param callback
	 * @return : array
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping(value = "/arr", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
	public String arr(Model model, @RequestBody String requeestBody,
			HttpSession session, HttpServletRequest request,
			HttpServletResponse response, String callback) throws IOException {
		String content = json(model, requeestBody, request, response, callback,false);
		return content;
	}

	private String json(Model model, String requeestBody,
			HttpServletRequest request, HttpServletResponse response,
			String callback,boolean isMap) {
		JSONObject json = JSONObject.fromObject(requeestBody);
		Object idObj = json.get("id");
		int id = (Integer) idObj;
		JSONArray properties = json.getJSONArray("properties");
		String[] propertyNames = JSONHWUtil.parse(properties);
		Object[] objs = getDao().getPropertiesById(id, propertyNames);
		// System.out.println("id:"+id);
		Object result=null;
		if(isMap){
			Map map = new HashMap();
			result = ValueWidget.getMap(map, propertyNames, objs);
		}else{
			result=objs;
		}
		
		// System.out.println(requeestBody);
		String content = HWJacksonUtils.getJsonP(result, callback);
		return content;
	}

	public abstract GenericDao<T> getDao();
}
