package com.common.util;

import com.common.bean.email.SendEmailInfo;
import com.common.bean.redis.RedisParaDto;
import com.common.dict.Constant2;
import com.io.hw.json.HWJacksonUtils;
import com.string.widget.util.ValueWidget;
import oa.bean.RequestInfoDto;

import java.util.*;

public class RedisCacheUtil {


    public static final String API_RESPONSE_CACHE = "apicache2";
    /***
     *  被com/common/web/filter/RequestbodyFilter.java 中 cacheWhenOvertime 调用
     */
    public static final String API_RESPONSE_CACHE_WHEN_OVERTIME = "apicache2overtime";
    public static final String API_USE_TIMES_CACHE_WHEN_OVERTIME = "apicacheusetimes2overtime";
    public static final String Redis_key_USE_STUB_MOCK = "use_stub_mock23";
    /***
     * 方法setSessionAttribute()<br />
     * see oa/web/responsibility/impl/custom/HttpSessionSyncShareFilter.java
     */
    public static String session_id = "hs";
    /***
     * 方法setSessionAttribute()<br />
     *      * see oa/web/responsibility/impl/custom/HttpSessionSyncShareFilter.java
     */
    public static String session_attr_class = "hsc";
    /***
     * List/Set 元素,HashMap value元素的对象类型
     */
    public static String session_attr_element_class = "hselementc";

    public static List<RequestInfoDto> getRequestInfoDtoList() {
        String redisKey = "convention_request_infoList2";
        String requestInfoJson = RedisHelper.getInstance().getCache(redisKey);
        List<RequestInfoDto> requestInfoList = null;
        if (ValueWidget.isNullOrEmpty(requestInfoJson)) {
            requestInfoList = new ArrayList();
        } else {
            requestInfoList = HWJacksonUtils.deSerializeList(requestInfoJson, RequestInfoDto.class);
        }
        return requestInfoList;
    }

    public static void cleanRequestInfoDtoList() {
        String redisKey = "convention_request_infoList2";
        RedisHelper.getInstance().saveCache(redisKey, "[]");
    }

    public static RedisParaDto parse(RedisParaDto redisParaDto) {
        if (null == redisParaDto) {
            return new RedisParaDto();
        }
        if (redisParaDto.isThree()) {
            redisParaDto.setValue(RedisHelper.getInstance().getKeyCache(redisParaDto.getId(), redisParaDto.getKey()));
        } else {
            String id = redisParaDto.getId();
            if (ValueWidget.isNullOrEmpty(id)) {
                id = redisParaDto.getKey();
            }
            String val = RedisHelper.getInstance().getCache(id);
            redisParaDto.setValue(val);
        }
        return redisParaDto;
    }

    /***
     * 获取邮箱,用于发生程序异常时接收异常信息
     * @return
     */
    public static String[] getEmail() {
        RedisParaDto redisParaDto = RedisParaDto.getInstance("exception_email");
        parse(redisParaDto);
        if (ValueWidget.isNullOrEmpty(redisParaDto.getValue())) {
            return new String[]{"1287789687@qq.com", "whuanghkl@gmail.com"};
        }
        return redisParaDto.getValue().split("[,;]");
    }

    public static SendEmailInfo getSendEmailInfo() {
        String sendEmailInfoJson = RedisHelper.getInstance().getCache("sendEmailInfo");
        if (ValueWidget.isNullOrEmpty(sendEmailInfoJson)) {
            return null;
        }
        return (SendEmailInfo) HWJacksonUtils.deSerialize(sendEmailInfoJson, SendEmailInfo.class);
    }

    public static String getNotExistRedisKey(Integer id, String simpleName) {
        return "notExistById" + Constant2.getDetailRedisKey(simpleName) + id;
    }

    public static void saveAliPayPrice(String orderNo, String strShouldPay) {
        String redisKey = orderNo + "strShouldPay";
        RedisHelper.getInstance().clearCache(redisKey);
        RedisHelper.getInstance().saveExpxKeyCache(redisKey, strShouldPay, 3600 * 25);
    }

    /***
     * 判断是否修改了订单金额
     * @param orderNo
     * @param strShouldPay
     * @return
     */
    public static boolean hasChangedPayPrice(String orderNo, String strShouldPay) {
        String strShouldPayTmp = RedisHelper.getInstance().getCache(orderNo + "strShouldPay");
        if (ValueWidget.isNullOrEmpty(strShouldPayTmp)) {
            return false;
        }
        return !strShouldPay.equals(strShouldPayTmp);
    }

    public static int getOrderNoIndex(String orderNo) {
        String strShouldPayTmp = RedisHelper.getInstance().getCache(orderNo + "orderNoIndex123");
        if (ValueWidget.isNullOrEmpty(strShouldPayTmp)) {
            return 1;
        }
        return Integer.parseInt(strShouldPayTmp);
    }

    public static String getOrderNoSuffix(String orderNo) {
        return getOrderNoSuffix(orderNo, false);
    }

    /***
     * 同时会更新 redis 中的序号<br />
     * 注意:手机端订单号不能包含"#",PC端订单号可以包含#,所以改为"__"
     * @param orderNo
     * @return
     */
    public static String getOrderNoSuffix(String orderNo, boolean addIndex) {
        int orderIndex = getOrderNoIndex(orderNo);
        if (addIndex) {
            orderIndex = orderIndex + 1;
            updateOrderNoIndex(orderNo, orderIndex);
        }
        return orderNo + "_" + ValueWidget.getIndexNoFormattd(orderIndex, 2);
    }

    public static void updateOrderNoIndex(String orderNo, int newIndex) {
        String redisKey = orderNo + "orderNoIndex123";
        RedisHelper.getInstance().clearCache(redisKey);
        RedisHelper.getInstance().saveExpxKeyCache(redisKey, String.valueOf(newIndex), 3600 * 25);
    }

    /**
     * 带看次数
     *
     * @param agentId
     * @return
     */
    public static String getVisitOrderCount(int agentId) {
        String redisKey = "house_agent2_visitCount" + agentId;
        return RedisHelper.getInstance().getCache(redisKey);
    }

    public static void saveVisitOrderCount(int agentId, Long visitOrderCount) {
        if (null == visitOrderCount) {
            return;
        }
        String redisKey = "house_agent2_visitCount" + agentId;
        RedisHelper.getInstance().saveExpxKeyCache(redisKey, String.valueOf(visitOrderCount), 120);
    }

    public static void clearOldStatusCache(int orderId) {
        String redisKey = "purchaseId" + orderId + "oldStatus";
        RedisHelper.getInstance().clearCache(redisKey);
    }

    /***
     *  RequestbodyFilter 指定只记录的servlet path<br />
     *  如果不为空,则不记录其他请求路径
     * @return
     */
    public static String getOnlyLoggerServlet() {
        RedisParaDto redisParaDto = RedisParaDto.getInstance("logger_requestinfo_onlyPath");
        parse(redisParaDto);
        return redisParaDto.getValue();
    }

    /***
     * zhi只记录指定 Conventionk 的日志
     * @return
     */
    public static String[] onlyRecordByConventionk() {
        String redisKey = "onlyShowByCookie";
        String json = RedisHelper.getInstance().getKeyCache("log", redisKey);
        if (ValueWidget.isNullOrEmpty(json)) {
            return null;
        }
        return json.split("[, ;]");
    }

    public static boolean recordRequestHeader() {
        return true;//TODO
    }

    public static String getAliNotifyUrl(String orderNo) {
        RedisParaDto redisParaDto = RedisParaDto.getInstance("notifyUrl_" + orderNo);
        parse(redisParaDto);
        return redisParaDto.getValue();
    }

    public static String getQueryByHouseInfoRedisKey(int inspectId) {
        String redisKey = "house_agent2_Inspect" + inspectId;
        return RedisHelper.getInstance().getCache(redisKey);
    }

    public static void clearQueryByHouseInfo(int inspectId) {
        String redisKey = getQueryByHouseInfoRedisKey(inspectId);
        if (ValueWidget.isNullOrEmpty(redisKey)) {
            return;
        }
        RedisHelper.getInstance().clearCache(redisKey);
    }

    /***
     * 对应 InspectionOrderDao中的getByHouseInfo
     * @param houseInfoId
     * @param agentId
     */
    public static void clearQueryByHouseInfo(int houseInfoId, int agentId) {
        String redisKey = "houseId" + houseInfoId + "_agentId" + agentId + "detail";
        RedisHelper.getInstance().clearCache(redisKey);
    }

    public static void saveAPIStubResponse(String key, String json) {
        RedisHelper.getInstance().saveKeyCache("stub", key, json);

    }

    public static String getAPIStubResponse(String key) {
        RedisParaDto redisParaDto = new RedisParaDto();
        redisParaDto.setId("stub");
        redisParaDto.setKey(key);
        parse(redisParaDto);
        return redisParaDto.getValue();
    }

    /**
     * 会覆盖 config/stubMap.json
     *
     * @return
     */
    public static Map<String, String> getStubPathMap() {
        RedisParaDto redisParaDto = new RedisParaDto();
        redisParaDto.setKey("stub_path");//不能使用 setId

        parse(redisParaDto);
        String json = redisParaDto.getValue();
        if (ValueWidget.isNullOrEmpty(json)) {
            return null;
        }
        return HWJacksonUtils.deSerializeMap(json, String.class);
    }

    /***
     * see com/common/web/filter/RequestbodyFilter.java中的 handlerMethodPathMap<br>
     * <实际不存在的接口路径A,真实的接口路径B>
     * @return
     */
    public static Map<String, String> getServletPathMap() {
        RedisParaDto redisParaDto = new RedisParaDto();
        redisParaDto.setKey("servlet_path");//不能使用 setId

        parse(redisParaDto);
        String json = redisParaDto.getValue();
        if (ValueWidget.isNullOrEmpty(json)) {
            return null;
        }
        return HWJacksonUtils.deSerializeMap(json, String.class);
    }

    public static void setServletPathMap(String mapping) {
        RedisHelper.getInstance().saveCache("servlet_path", mapping);
    }

    public static void clearServletPathMap() {
        RedisHelper.getInstance().clearCache("servlet_path");//getServletPathMap()
    }

    /***
     * 删除一个接口,<br />
     * see http://i.yhskyc.com/static/html/stubMap/index.html
     * @param servletPath
     * @return
     */
    public static Map<String, String> deleteOneStubFromPathMap(String servletPath) {
        Map<String, String> redisMap = RedisCacheUtil.getStubPathMap();
        if (ValueWidget.isNullOrEmpty(redisMap)) {
            return redisMap;
        }
        if (redisMap.containsKey(servletPath)) {
            redisMap.remove(servletPath);
        } else {
            if (servletPath.startsWith("/")) {
                redisMap.remove(servletPath.substring(1));
            }
        }
        RedisHelper.getInstance().saveCache("stub_path", HWJacksonUtils.getJsonP(redisMap));
        return getStubPathMap();
    }

    /***
     * 与StubUtil.useAPIResponseStub 配套使用
     * @return
     */
    public static boolean useStubMockData() {
        RedisParaDto redisParaDto = new RedisParaDto();
        redisParaDto.setKey(Redis_key_USE_STUB_MOCK);//不能使用 setId

        parse(redisParaDto);
        String json = redisParaDto.getValue();
        if (ValueWidget.isNullOrEmpty(json)) {
            return false;
        }
        return SystemHWUtil.parse33(json);
    }

    /***
     * 和useStubMockData 配套使用
     * @param useStubMockData
     */
    public static void setIsUseStubMockData(Boolean useStubMockData) {
        if (null == useStubMockData) {
            useStubMockData = false;
        }
        RedisHelper.getInstance().saveCache(Redis_key_USE_STUB_MOCK, String.valueOf(useStubMockData));
    }

    /***
     * 被 com/house/ujiayigou/web/controller/intercept/CommonHandlerInterceptor.java 中 postHandle调用<br />
     * @param servletPath
     * @param conventionk
     * @return
     */
    public static String getAPIResponse(String servletPath, String conventionk) {
        RedisParaDto redisParaDto = new RedisParaDto();
        redisParaDto.setId(API_RESPONSE_CACHE);
        if (!servletPath.startsWith(Constant2.SLASH)) {
            servletPath = Constant2.SLASH + servletPath;
        }
        redisParaDto.setKey(SystemHWUtil.getMD5(servletPath + conventionk, SystemHWUtil.CHARSET_UTF));
        parse(redisParaDto);
        return redisParaDto.getValue();
    }

    /***
     * 被 com/house/ujiayigou/web/controller/intercept/CommonHandlerInterceptor.java 中 postHandle 调用<br />
     * 干吗用的 ??? TODO
     * @param servletPath
     * @param conventionk
     * @param json
     */
    public static void saveAPIResponse(String servletPath, String conventionk, String json, Integer hours) {
        String key = SystemHWUtil.getMD5(servletPath + conventionk, SystemHWUtil.CHARSET_UTF);
        if (null == hours) {
            RedisHelper.getInstance().saveKeyCache(API_RESPONSE_CACHE, key, json);
        } else {
            RedisHelper.getInstance().saveKeyCacheAndExpire(API_RESPONSE_CACHE, key, json, hours);
        }
    }

    /***
     * 如果请求特别耗时,请求响应时间超过4秒,则设置缓存<br />
     * 缓存使用次数 见 RequestbodyFilter.timesCanUseCacheWhenOvertime
     * @param md5
     * @param response
     */
    public static void saveAPIResponseBackUpWhenOvertime(String md5, String response, String timesCanUseCacheWhenOvertime) {
        if (ValueWidget.isNullOrEmpty(response)) {
            return;
        }
        RedisHelper.getInstance().clearKeyCache(API_RESPONSE_CACHE_WHEN_OVERTIME, md5);
        RedisHelper.getInstance().clearKeyCache(API_USE_TIMES_CACHE_WHEN_OVERTIME, md5);
        RedisHelper.getInstance().saveKeyCacheExpire1day(API_RESPONSE_CACHE_WHEN_OVERTIME, md5, response);
        if (ValueWidget.isNullOrEmpty(timesCanUseCacheWhenOvertime)) {
            timesCanUseCacheWhenOvertime = "2";
        }
        RedisHelper.getInstance().saveKeyCacheExpire1day(API_USE_TIMES_CACHE_WHEN_OVERTIME, md5, timesCanUseCacheWhenOvertime);
    }

    /***
     * 缓存使用次数 见 RequestbodyFilter.timesCanUseCacheWhenOvertime
     * @param md5
     * @param response
     */
    public static void saveAPIResponseBackUpWhenOvertime(String md5, String response) {
        saveAPIResponseBackUpWhenOvertime(md5, response, null);
    }

    /***
     * 如果请求特别耗时,请求响应时间超过4秒,则设置缓存<br />
     * 保证不了 原子性
     * @param md5
     * @return
     */
    public static String getAPIResponseBackUpWhenOvertime(String md5) {
        String response3 = RedisHelper.getInstance().getKeyCache(API_RESPONSE_CACHE_WHEN_OVERTIME, md5);
        if (ValueWidget.isNullOrEmpty(response3)) {
            return response3;
        }
        String countRemainingStr = RedisHelper.getInstance().getKeyCache(API_USE_TIMES_CACHE_WHEN_OVERTIME, md5);
        int count = 0;
        if (!ValueWidget.isNullOrEmpty(countRemainingStr)) {
            count = Integer.parseInt(countRemainingStr);
        }
        if (0 == count) {
            RedisHelper.getInstance().clearKeyCache(API_RESPONSE_CACHE_WHEN_OVERTIME, md5);
            RedisHelper.getInstance().clearKeyCache(API_USE_TIMES_CACHE_WHEN_OVERTIME, md5);
            return null;
        }
        count--;
        RedisHelper.getInstance().saveKeyCacheExpire1day(API_USE_TIMES_CACHE_WHEN_OVERTIME, md5, String.valueOf(count));
        return response3;
    }

    /***
     * 获取App选择的行政区域
     * @param cid
     * @return
     */
    public static String getAppDistrictCity(String cid) {
        return RedisHelper.getInstance().getKeyCache(cid, "appDistrictCity");
    }

    /***
     * 解决 stub 乱码的问题<br />
     * see  com/house/ujiayigou/web/controller/agent/WorkScheduleController.java
     * @return
     */
    public static boolean convertToISO88591() {
        String stubRespIso = RedisHelper.getInstance().getKeyCache("encoding", "stubResp");
        return SystemHWUtil.parse33(stubRespIso);
    }

    /***
     * 调用接口 /inspectionOrder/inspectionTime/update2/json 时,是否严格校验时间
     * @return
     */
    public static boolean strictAgentInspectionTime() {
        return false;
    }

    public static boolean allowRepeatOperate() {
        return true;
    }

    public static boolean strictCountyWhenQueryGrab() {
        return true;
    }

    /***
     * 经纪人主页接口 调用
     * /customer/agent/home/json
     * @param cid
     * @param agentId
     */
    public static void saveAgentHomePage(String cid, int agentId) {
        RedisHelper.getInstance().saveKeyCacheExpire15days(cid, Constant2.GET_AGENTID_BY_AGENT_HOME, String.valueOf(agentId));
    }

    public static int getAgentIdByHome(String cid) {
        String agentIdStr = RedisHelper.getInstance().getKeyCache(cid, Constant2.GET_AGENTID_BY_AGENT_HOME);
        if (ValueWidget.isNullOrEmpty(agentIdStr)) {
            return SystemHWUtil.NEGATIVE_ONE;
        }
        return Integer.parseInt(agentIdStr);
    }

    public static void savePassbackParams(String hook, Map passbackParamsDetail) {
        String key = "passback" + hook;
        RedisHelper.getInstance().clearCache(key);
        //有效期:2小时
        RedisHelper.getInstance().saveExpxKeyCache(key, HWJacksonUtils.getJsonP(passbackParamsDetail), 3600 * 2);
    }

    public static Map getPassbackParams(String hook) {
        String key = "passback" + hook;
        String passbackParamsDetail = RedisHelper.getInstance().getCache(key);
        if (ValueWidget.isNullOrEmpty(passbackParamsDetail)) {
            return new HashMap();
        }
        return (HashMap) HWJacksonUtils.deSerialize(passbackParamsDetail, HashMap.class);
    }

    /***
     * 有效期:15天
     * @param s
     * @param o
     */
    public static void setSessionAttribute(String s, Object o) {
        RedisHelper.getInstance().clearKeyCache(session_id, s);
        RedisHelper.getInstance().clearKeyCache(session_attr_class, s);
        if (o == null) {
            return;
        }
        String val = getStringOf(s, o);
        RedisHelper.getInstance().saveKeyCacheExpire15days(session_id, s, val);
        RedisHelper.getInstance().saveKeyCacheExpire15days(session_attr_class, s, o.getClass().getName());

        complexType(s, o);
    }

    public static String getStringOf(String s, Object o) {
        String val = null;
        if (o instanceof String) {
            val = (String) o;
        } else if (o instanceof Number) {
            val = String.valueOf(o);
        } else {
            val = HWJacksonUtils.getJsonP(o);
        }
        RedisHelper.getInstance().saveKeyCacheExpire15days(session_id, s, val);
        RedisHelper.getInstance().saveKeyCacheExpire15days(session_attr_class, s, o.getClass().getName());

        complexType(s, o);
        return val;//TODO
    }

    /***
     * 针对集合类
     * @param s
     * @param o
     */
    public static void complexType(String s, Object o) {
        if (o instanceof List) {
            List list = (List) o;
            if (ValueWidget.isNullOrEmpty(list)) {
                return;
            }
            Object element = list.get(0);
            Class elementClazz = element.getClass();
            cacheElementClassName(s, elementClazz);
        } else if (o instanceof Set) {
            Set set = (Set) o;
            if (ValueWidget.isNullOrEmpty(set)) {
                return;
            }
            Object element = set.iterator().next();
            cacheElementClassName(s, element.getClass());
        } else if (o instanceof Map) {
            Map map = (Map) o;
            if (ValueWidget.isNullOrEmpty(map)) {
                return;
            }
            Object element = map.values().iterator().next();
            cacheElementClassName(s, element.getClass());
        }
    }

    public static void cacheElementClassName(String s, Class elementClazz) {
        RedisHelper.getInstance().saveKeyCacheExpire15days(session_attr_element_class, s, elementClazz.getName());
    }

    /***
     * 目前仅支持 值为字符串 或者实体类的情况,<br />
     * 不支持 值为数字,List,Set 等
     * @param key
     * @return
     */
    public static Object getSessionAttribute(String key) {
        String val = RedisHelper.getInstance().getKeyCache(session_id, key);
        if (null == val) {
            return val;
        }
        String attrClass = RedisHelper.getInstance().getKeyCache(session_attr_class, key);
        if (null == attrClass) {
            return val;
        }
        String elementClass = RedisHelper.getInstance().getKeyCache(session_attr_element_class, key);
        return getObjectOfSession(val, attrClass, elementClass);
    }

    public static Object getObjectOfSession(String val, String attrClass, String elementClass) {
        Object o = null;
        if (attrClass.contains("entity")
                || attrClass.contains("domain")
                || attrClass.contains("dto.")
                || attrClass.contains("vo.")
                || attrClass.contains("vm.")
                || attrClass.endsWith("DTO")
                || attrClass.endsWith("Dto")
                || attrClass.endsWith("Vo")) {
            try {
                o = HWJacksonUtils.deSerialize(val, Class.forName(attrClass));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if (attrClass.endsWith("short")
                || attrClass.endsWith("Short")) {
            return Short.parseShort(val);
        } else if (attrClass.endsWith("int")
                || attrClass.endsWith("Integer")) {
            return Integer.parseInt(val);
        } else if (attrClass.endsWith("long")
                || attrClass.endsWith("Long")) {
            return Long.parseLong(val);
        } else if (attrClass.endsWith("float")
                || attrClass.endsWith("Float")) {
            return Float.parseFloat(val);
        } else if (attrClass.endsWith("double")
                || attrClass.endsWith("Double")) {
            return Double.parseDouble(val);
        } else if (attrClass.endsWith("List")) {
            return parseSessionValList(val, elementClass);
        } else if (attrClass.endsWith("Set")) {
            return parseSessionValSet(val, elementClass);
        } else if (attrClass.endsWith("Map")) {
            return parseSessionValMap(val, elementClass);
        } else if (attrClass.endsWith("String;")) {
            return HWJacksonUtils.deSerialize(val, String[].class);
        } else if (attrClass.endsWith("String")) {
            return val;
        } else {
            o = val;
        }
        return o;
    }

    public static Object parseSessionValList(String val, String elementClass) {
        if (ValueWidget.isNullOrEmpty(elementClass)) {
            return HWJacksonUtils.deSerialize(val, ArrayList.class);
        }
        try {
            return HWJacksonUtils.deSerializeList(val, Class.forName(elementClass));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return val;
    }

    public static Object parseSessionValSet(String val, String elementClass) {
        if (ValueWidget.isNullOrEmpty(elementClass)) {
            return HWJacksonUtils.deSerialize(val, HashSet.class);
        }
        try {
            return HWJacksonUtils.deSerializeSet(val, Class.forName(elementClass));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return val;
    }

    public static Object parseSessionValMap(String val, String elementClass) {
        if (ValueWidget.isNullOrEmpty(elementClass)) {
            return HWJacksonUtils.deSerialize(val, HashSet.class);
        }
        try {
            return HWJacksonUtils.deSerializeMap(val, Class.forName(elementClass));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return val;
    }

    /***
     * 经纪人发布二手房<br />
     * 设置video 时,是否上架
     * @return
     */
    public static boolean onSaleHouseWhenAgentCreate() {
        String stubRespIso = RedisHelper.getInstance().getKeyCache("houseTypeAgentCreate", "onsale");
        return SystemHWUtil.parse33(stubRespIso);
    }

    /***
     * 用于debug,故意使接口 变慢
     *         单位:秒 <br />
     *         see RecordAPIExecuteTimeFilter
     * @return
     */
    public static Integer debugSleepAfterDealRequest() {
        String stubRespIso = RedisHelper.getInstance().getKeyCache("debug_request", "sleepSecond");
        if (ValueWidget.isNullOrEmpty(stubRespIso)) {
            return null;
        }
        return Integer.parseInt(stubRespIso);
    }

    /***
     * 极光推送
     * @return
     */
    public static String getPushMessageValueForDebug() {
        return RedisHelper.getInstance().getKeyCache("jpush_push", "message_val");
    }
}
