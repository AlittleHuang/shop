var routes = [{//主页
    path: '/',
    component: componentLoader.load("components/index.html"),
    children: [//主页底部导航
        {path: '/main', component: componentLoader.load("components/main.html")},
        {path: '/cart', component: componentLoader.load("components/cart.html")},
    ]
}, {
    path: '/product/:id',
    props: true,
    component: componentLoader.load("components/product.html")
}, {
    path: '/order/confirm',
    props: true,
    component: componentLoader.load("components/order-confirm.html")
}, {
    path: '/address',
    props: true,
    component: componentLoader.load("components/address.html")
}];

let router = new VueRouter({routes});