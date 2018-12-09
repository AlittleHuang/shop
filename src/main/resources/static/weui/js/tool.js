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

var vueLoader = {
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
                    eval(scripts[i].innerHTML)
                }

                component.template = dom.querySelector("template").innerHTML;
                return resolve(component);
            });
        }
    }
};

//