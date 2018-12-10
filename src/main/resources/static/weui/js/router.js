function getRoutes() {
    let component = componentLoader.load;
    return [
        {//主页
            path: '/',
            component: component("components/index.html"),
            children: [//主页底部导航
                {path: '/main', component: component("components/main.html")},
                {path: '/cart', component: component("components/cart.html")},
            ]
        },
        {
            path: '/product/:id',
            props: true,
            component: component("components/product.html")
        },
        {
            path: '/order/confirm',
            props: true,
            component: component("components/order-confirm.html")
        },
        {
            path: '/address',
            props: true,
            component: component("components/address.html")
        },
        {
            path: '/address/add',
            props: true,
            component: component("components/address-add.html")
        },
    ];
}
let router = new VueRouter({routes: getRoutes()});