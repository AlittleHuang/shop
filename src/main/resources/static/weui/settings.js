var vueLoader = {
    load: function (url) {
        return function (resolve) {
            axios.get(url).then(function (resp) {
                let template = resp.data;
                var dom = document.createElement('html');
                dom.innerHTML = template;

                var component = {};

                function register(data) {
                    component = data;
                }

                let list = dom.querySelectorAll("script");
                for (let i = 0; i < list.length; i++) {
                    eval(list[i].innerHTML);
                }

                component.template = dom.querySelectorAll("template")[0].innerHTML;
                resolve(component);
            })
        }
    }
}

const routes = [];
routes.push({
    path: '/product',
    component: vueLoader.load("product.vue")
});

const index_main = [];
index_main.push(
    {path: '/main', component: {template: `<router-link to='product'>商品详情</router-link>`}},
);

routes.push({
    path: '/',
    component: vueLoader.load("main.vue"),
    children: index_main
});
