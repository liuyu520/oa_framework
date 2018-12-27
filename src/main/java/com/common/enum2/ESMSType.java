package com.common.enum2;

import com.common.util.SystemHWUtil;

/**
 * Created by whuanghkl on 17/7/4. <br />
 * 需要同步设置 SMSService 中的 typeMap
 */
public enum ESMSType {
    SMS_TYPE_CHANGE_PWD("修改密码") {
        @Override
        public int value() {
            return 0;
        }
    }, SMS_TYPE_REGISTER("注册") {
        @Override
        public int value() {
            return 1;
        }
    }, SMS_TYPE_FIND_PWD("找回密码") {
        @Override
        public int value() {
            return 2;
        }
    }, SMS_TYPE_BIND_CARD("绑定银行卡") {
        @Override
        public int value() {
            return 3;
        }
    }, SMS_TYPE_agent_select_service_aera("经纪人修改服务区域") {
        @Override
        public int value() {
            return 5;
        }
    }, SMS_TYPE_change_bind_old_mobile("换绑手机号,向旧手机号发送的验证码") {
        @Override
        public int value() {
            return 6;
        }
    }, SMS_TYPE_change_bind_new_mobile("换绑手机号,向新手机号发送的验证码") {
        @Override
        public int value() {
            return 7;
        }
    }, SMS_TYPE_add_third_pay_method_weixin("添加第三方支付方式:微信支付") {
        @Override
        public int value() {
            return 11;
        }
    }, SMS_TYPE_add_third_pay_method_ali("添加第三方支付方式:支付宝") {
        @Override
        public int value() {
            return 12;
        }
    }, SMS_TYPE_22_bind_old_mobile2("备用") {
        @Override
        public int value() {
            return 13;
        }
    }, SMS_TYPE_22_change_houseInfo_mobile("房主修改房源联系方式") {
        @Override
        public int value() {
            return 15;
        }
    };
    private String typeLable;

    public int value() {
        return 0;
    }

    ESMSType(String typeLable) {
        this.typeLable = typeLable;
    }

    public boolean equals(ESMSType inspectionOrderStatus) {
        return this.compareTo(inspectionOrderStatus) == 0;
    }

    public static String getAllEnumDoc() {
        ESMSType[] esmsTypes = ESMSType.values();
        StringBuffer stringBuffer = new StringBuffer();

        for (ESMSType esmsType : esmsTypes) {
            stringBuffer.append(esmsType.value()).append("=").append(esmsType.typeLable).append(SystemHWUtil.CRLF);
        }
        return stringBuffer.toString();
    }
}
