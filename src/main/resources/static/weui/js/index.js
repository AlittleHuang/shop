axios.interceptors.response.use(function (resp) {
    if (typeof resp.data === "object") {
        rmCircularReference(resp.data);
    }
    return resp
});

Vue.filter("toFixed2", function (value) {
    return (1 * value).toFixed(2)
});

function limitNum(num, lo, hi) {
    return Math.max(lo, Math.min(hi, num))
}

const routes = [];

const index_main = [];
routes.push({//主页
    path: '/',
    component: vueLoader.load("components/index.html"),
    children: index_main
});

index_main.push(
    {path: '/main', component: {template: "<router-link to='product/1'>商品详情</router-link>"}},
);

index_main.push(//购物车
    {path: '/cart', component: vueLoader.load("components/cart.html")},
);

routes.push({ //商品详情
    path: '/product/:id',
    props: true,
    component: vueLoader.load("components/product.html")
});

routes.push({ //确认订单
    path: '/order/confirm',
    props: true,
    component: vueLoader.load("components/order-confirm.html")
});

const app = new Vue({
    data: {},
    router: new VueRouter({routes})
}).$mount('#app');