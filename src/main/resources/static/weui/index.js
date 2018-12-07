const routes = [];
var vueLoader = {
    fixfastJson: function (json) {
        var traversed = [];
        return recode(json, json);

        function recode($, data) {
            if (traversed.includes(data)) {
                return data
            }
            traversed.push(data)

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
    },

    load: function (url) {
        var this_ = this;
        return function (resolve) {
            axios.get(url).then(function (resp) {

                var dom = document.createElement("html");
                dom.innerHTML = this_.fixfastJson(resp.data);

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

Vue.filter("toFixed2", function (value) {
    return (1 * value).toFixed(2)
})

function limitNum(num, lo, hi) {
    return Math.max(lo, Math.min(hi, num))
}


const index_main = [];
index_main.push(
    {path: '/main', component: {template: "<router-link to='product/14'>商品详情</router-link>"}},
);

routes.push({
    path: '/',
    component: vueLoader.load("components/main.html"),
    children: index_main
});

routes.push({
    path: '/product/:id',
    props: true,
    component: vueLoader.load("components/product.html")
});