document.write('<script src="https://cdn.bootcss.com/jquery/3.3.1/jquery.min.js"></script>')
var shop = new ajax("http://localhost:8031", "user-token");
var admin = new ajax("http://localhost:8031", "admin-token");

function ajax(host, tokenKey) {

    host = host
    tokenKey = tokenKey == undefined ? "sessionId" : tokenKey;

    function fixcallback(callback) {
        var callbackFix = function (data, textStatus, jqXHR) {
            if (data.code == -4003922) {
                alert("请先登录!")
                window.location.href = "redirection.html"
                return;
            }
            if (typeof data == "object")
                data = $JSON(data)
            callback(data, textStatus, jqXHR)
        }
        return callbackFix
    }

    function fixurl(url) {
        if (host && host != "" && url != undefined) {
            url = host + url
        }
        return url;
    }

    this.post = function (url, data, success, dataType) {

        return this.ajax({
            method: "POST",
            url: url,
            data: data,
            success: success,
            dataType: dataType
        })

    };

    this.get = function (url, data, success, dataType) {

        return this.ajax({
            method: "GET",
            url: url,
            data: data,
            success: success,
            dataType: dataType
        })

    };

    this.ajax = function (url, options) {
        var opt;
        if (typeof url === "object") {
            opt = url
            opt.url = fixurl(opt.url)
        } else {
            url = fixurl(url)
            opt = options
        }
        if (typeof opt.success == "function")
            opt.success = fixcallback(opt.success)
        var token = sessionStorage.getItem(tokenKey);
        if (token != undefined) {
            if (opt.headers == undefined) {
                opt.headers = {}
            }
            opt.headers[tokenKey] = token
        }
        return $.ajax(url, options);
    };

    this.fixurl = fixurl

    this.ajaxFileUpload = function (options) {
        if (options.url != undefined) {
            options.url = fixurl(options.url)
        }
        return $.ajaxFileUpload(options)
    }

    this.setToken = function (tokenValue) {
        sessionStorage.setItem(tokenKey, tokenValue)
    }
}

function $JSON(json) {
    return recode(json, json)

    function recode($, data) {
        if (typeof data != "object") {
            return data;
        }
        for (var key in data) {
            var value = data[key];
            if (value.$ref != undefined) {
                value = eval(value.$ref);
            }
            data[key] = recode($, value);
        }
        return data;
    }
}