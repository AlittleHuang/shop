function getUrlParam(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    if (r != null)
        return decodeURI(r[2]);
    return null;
}

function setCookie(c_name, value, expiredays) {
    var exdate = new Date()
    exdate.setDate(exdate.getDate() + expiredays)
    document.cookie = c_name
        + "="
        + escape(value)
        + ((expiredays == null) ? "" : ";expires="
            + exdate.toGMTString())
}

if (getUrlParam("sessionid")) {
    setCookie("JSESSIONID", getUrlParam("sessionid"), 1)
    sessionStorage.setItem("sessionid", getUrlParam("sessionid"))
    location.href = "fronts/index.html";
} else {
    $.ajax({
        type: "GET",
        url: "/islogin/front",
        success: function (resp) {
            if (resp.islogin) {
                window.location = isMobile() ? "fronts/index.html" : "fronts/index.html"
            } else {
                toLogin()
            }
        },
        error: function () {
            toLogin()
        }
    })
}

function toLogin() {
    window.location = isMobile() ? "fronts/login.html" : "fronts/login.html"
}

var browser = {
    versions: function () {
        var u = navigator.userAgent, app = navigator.appVersion;
        return {//移动终端浏览器版本信息
            trident: u.indexOf('Trident') > -1, //IE内核
            presto: u.indexOf('Presto') > -1, //opera内核
            webKit: u.indexOf('AppleWebKit') > -1, //苹果、谷歌内核
            gecko: u.indexOf('Gecko') > -1 && u.indexOf('KHTML') == -1, //火狐内核
            mobile: !!u.match(/AppleWebKit.*Mobile.*/), //是否为移动终端
            ios: !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/), //ios终端
            android: u.indexOf('Android') > -1 || u.indexOf('Linux') > -1, //android终端或者uc浏览器
            iPhone: u.indexOf('iPhone') > -1, //是否为iPhone或者QQHD浏览器
            iPad: u.indexOf('iPad') > -1, //是否iPad
            webApp: u.indexOf('Safari') == -1, //是否web应该程序，没有头部与底部
            weixin: u.indexOf('MicroMessenger') > -1, //是否微信
            qq: u.match(/\sQQ/i) == " qq" //是否QQ
        };
    }(),
    language: (navigator.browserLanguage || navigator.language).toLowerCase()
}

function isMobile() {
    return browser.versions.mobile || browser.versions.ios || browser.versions.android
        || browser.versions.iPhone || browser.versions.iPad
}