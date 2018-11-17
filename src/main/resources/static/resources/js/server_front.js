var server = {

    PRE: "resources/",

    insuranceApplication: {
        // 根据ID获取保险订单
        findByid: function (id, callback) {
            $.get(server.PRE + "insurance-application/one", {id: id}, callback);
        }
    },

    user: {
        /**
         * 删除未激活用户
         * @param id 用户id
         * @param callback 回调函数
         */
        deleteById: function (id, callback) {
            $.post(server.PRE + "user/delete", {id: id}, callback);
        },
        /**
         * 获取用户信息
         * @param callback
         */
        info: function (callback) {
            $.get(server.PRE + "user/info", callback)
        },
        get: function (callback) {
            $.post(server.PRE + "user", {}, callback);
        }
    },

    sp: {
        info: function (callback) {
            $.get(server.PRE + "sp/statistics", callback)
        },
        priceHistory: function (callback) {
            $.get(server.PRE + "sp/price/history", callback)
        }
    },

    balance: {
        CP: 0, RP: 1, DP: 2, IP: 3, SP: 4, WP: 5, BP: 6, IA: 7, MP: 8, L: 9, R: 10,
        amount: function (type, callback) {
            $.get(server.PRE + "balance/amount", {type: type}, callback)
        },
        all: function (callback) {
            $.get(server.PRE + "balance/all", {}, callback)
        },
    }


}


function getUrlParam(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return decodeURI(r[2]);
    return null;
}
