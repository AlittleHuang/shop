function rmCircularReference(json) {
    var traversed = [];
    return recode(json, json);

    function recode($, data) {
        if (traversed.includes(data)) {
            return data
        }
        traversed.push(data);

        if (typeof data !== "object") {
            return data;
        }
        for (var key in data) {
            var value = data[key];
            if (value.$ref !== undefined) {
                value = eval(value.$ref);
                data[key] = value
            } else {
                data[key] = recode($, value);
            }
        }
        return data;
    }
}

var componentLoader = {
    load: function (url) {
        return function (resolve) {
            axios.get(url).then(function (resp) {

                var dom = document.createElement("html");
                dom.innerHTML = rmCircularReference(resp.data);

                var component = {};

                function exports(data) {
                    component = data;
                }

                var scripts = dom.querySelectorAll("script");
                for (var i = 0; i < scripts.length; i++) {
                    eval(scripts[i].innerHTML);
                }

                component.template = dom.querySelector("template").innerHTML;
                setTimeout(resolve(component), 100);
            });
        }
    }
};
var __JAVA_SCRIPS_RCS_LOADED__ = [];

function loadJavaScript(src, callback) {
    if (!__JAVA_SCRIPS_RCS_LOADED__.includes(src)) {
        var newScript = document.createElement('script');
        newScript.setAttribute('src', src);
        var head = document.getElementsByTagName('head')[0];
        newScript.onload = callback;
        head.appendChild(newScript);
        __JAVA_SCRIPS_RCS_LOADED__.push(src)
    } else {
        callback()
    }
}

var ___map_js_source___ = {};

function execute(src, callback) {
    let code = ___map_js_source___[src];
    if (!code) {
        axios.get(src).then(function (resp) {
            if (resp.data) {
                eval(resp.data);
                callback();
            }
        })
    } else {
        eval(code);
        callback();
    }
}

function limitNum(num, min, max) {
    return Math.max(min, Math.min(max, num))
}

String.prototype.replaceAll = function (FindText, RepText) {
    var regExp = new RegExp(FindText, "g");
    return this.replace(regExp, RepText);
};

String.prototype.toJSON = function () {
    return JSON.parse(this);
};

var provinceCitArea;

function getPca(resp) {
    provinceCitArea = provinceCitArea || new ProvinceCitArea(resp);
    return provinceCitArea;
}

function ProvinceCitArea(resp) {
    var pca = resp.data;
    var weuiPicker = resp.request.responseText
        .replaceAll("code", "value")
        .replaceAll("name", "label")
        .toJSON();
    var pcaMaps;


    function addToMap(node) {
        pcaMaps[node.code] = node;
        let children = node.children;
        for (let i in children) {
            addToMap(children[i])
        }
    }

    function pcaMap() {
        if (pcaMaps) {
            return pcaMaps;
        }
        pcaMaps = {};
        for (var i in pca) {
            addToMap(pca[i]);
        }
        return pcaMaps;
    }


    this.areaIdToArr = function (areaId) {
        if (areaId) {
            return [
                (areaId - areaId % 10000) / 10000,
                (areaId - areaId % 100) / 100,
                areaId
            ];
        }
        return [11, 1101, 110101]
    };

    this.toWeuiPicker = function () {
        weuiPicker = weuiPicker || resp.request.responseText
            .replaceAll("code", "value")
            .replaceAll("name", "label")
            .toJSON();
        return weuiPicker
    };

    this.toString = function (areaId) {
        let pcaIds = this.areaIdToArr(areaId);
        return pcaMap()[pcaIds[0]].name + pcaMap()[pcaIds[1]].name + pcaMap()[areaId].name
    }
}