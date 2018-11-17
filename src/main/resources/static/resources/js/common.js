var common = {

    _bankname: [
        '中国农业银行', '中国工商银行', '中国建设银行', '中国银行', '招商银行', '平安银行',
        '交通银行', '中信银行', '民生银行', '邮政储蓄银行', '广东发展银行', '万事达卡', '其他'
    ],

    bankName: function (index) {
        return common._bankname[index];
    },

    enptyFix: function (value, defvalue) {
        if (value == undefined || value == null) {
            return defvalue;
        }
        return value;
    }

}