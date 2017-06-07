package oa.web.controller.generic;

import com.common.dao.generic.GenericDao;
import com.common.dict.Constant2;
import com.common.web.view.PageView;
import com.string.widget.util.ValueWidget;
import org.apache.commons.collections.map.ListOrderedMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by 黄威 on 11/01/2017.<br >
 * 可以查询任何实体
 */
public class GenericSuperHWController extends GenericController {
    /***
     * 实体类名称:例如Test2Boy,User,Convention等.
     */
    private String entityName;
    private Class entityClass;
    private String jspFolder;
    private Integer id;

    /***
     *
     * @param request
     * @param entity : 实体类名称:例如Test2Boy,User,Convention等.
     * @param cmd : 动作,例如detail,delete,id,list等
     * @return
     */
    @RequestMapping(value = "/entity/{entity}/{cmd}")
//    @ResponseBody
    public String entity(Model model, HttpServletRequest request, HttpServletResponse response, PageView view, HttpSession session,
                         @PathVariable String entity, @PathVariable String cmd
            , String targetView, String jspFolder, Boolean returnJson) {
        init(request, entity, jspFolder);
        System.out.println("cmd:" + cmd);
        if ("list".equals(cmd)) {
            return listCommon(model, null, view, session, request, response, targetView, returnJson);
        }
        if (ValueWidget.isNumeric(cmd)) {
            this.id = Integer.parseInt(cmd);
            return detailAction(this.id, model, request, response, targetView);
        }
        return entity;
    }

    /***
     *
     * @param request
     * @param entity : 实体类名称:例如Test2Boy,User,Convention等.
     * @param cmd : 动作,例如detail,delete,id,list等
     * @return
     */
    @RequestMapping(value = "/entity/{entity}/{id}/{cmd}")
//    @ResponseBody
    public String entityById(Model model, HttpServletRequest request, HttpServletResponse response, PageView view, HttpSession session,
                             @PathVariable String entity, @PathVariable String cmd, @PathVariable Integer id
            , String targetView, String jspFolder) {
        init(request, entity, jspFolder);
        System.out.println("cmd:" + cmd);
        if ("delete".equals(cmd)) {
            return deleteAction(id, model, request, response, targetView);
        }
        if ("edit".equals(cmd)) {
        }
        return entity;
    }

    private void init(HttpServletRequest request, String entity, String jspFolder) {
        setEntityName(entity);
        this.jspFolder = jspFolder;
        if (null == this.jspFolder) {
            this.jspFolder = ValueWidget.title(entity);
        }
        super.dao = (GenericDao) getDaoByWebApp(request, entity + Constant2.DAO);
        this.entityClass = super.dao.getEntityClass();
        super.setEntityClz(this.entityClass);
        System.out.println(dao);
        System.out.println(entityClass);
    }

    @Override
    protected void beforeAddInput(Model model, HttpServletRequest request) {
    }

    @Override
    protected void errorDeal(Model model) {
    }

    @Override
    public String getJspFolder() {
        return this.jspFolder;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }


    @Override
    public ListOrderedMap getListOrderBy() {
        ListOrderedMap orderColumnModeMap = new ListOrderedMap();
        orderColumnModeMap.put("updateTime", "desc");
//        orderColumnModeMap.put("stars", "desc");
        return orderColumnModeMap;
    }

    @Override
    protected Object detailTODO(int id, Model model,
                                HttpServletRequest request, HttpServletResponse response) {
        return super.detailTODO(id, model, request, response);
    }
}
