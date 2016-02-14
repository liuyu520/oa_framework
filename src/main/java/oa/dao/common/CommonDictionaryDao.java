package oa.dao.common;

import java.util.List;

import oa.entity.common.CommonDictionary;

import org.hibernate.criterion.Order;
import org.springframework.stereotype.Component;

import com.common.dao.generic.GenericDao;

@Component
public class CommonDictionaryDao extends GenericDao<CommonDictionary> {
	public List<CommonDictionary> getList(){
		return (List<CommonDictionary>)super.getCurrentSession().createCriteria(CommonDictionary.class).addOrder(Order.asc("groupId"))
        .addOrder(Order.asc("key2")).list();
		
	}
	public CommonDictionary getDictionary(String groupId, String key){
    	return get("groupId", groupId,"key2", key);
    }
}
