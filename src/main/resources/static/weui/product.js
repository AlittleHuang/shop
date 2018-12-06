const Component_product = {
    template: `

<div id='component_product'>
        <div class="back-round-but">
            <span @click="$router.go(-1)" class="icon icon-left"></span>
        </div>
        <div class='weui-tab__panel'>

            <div class='product-content'>

                <div class='product-preview'>
                    <img width='100%' height='100%' src='images/tmp/fm.jpg'>
                </div>

                <div class="weui-panel">
                    <div class="weui-panel__hd">
                        <h3 style='color: black'>
                            苹果X手机壳iPhoneXS MAX手机壳男苹果xr手机壳7/8plus防摔潮牌女苹果X透明套xs全包硬壳max镜头保护圈手机壳
                        </h3>
                    </div>
                    <div class="weui-panel__bd">
                        <div class="weui-media-box weui-media-box_text">
                            <h4 class="weui-media-box__title real-price">￥99.99</h4>
                            <ul class="weui-media-box__info weui-flex">
                                <li class="weui-media-box__info__meta weui-flex__item text-left">
                                    <span>价格</span>
                                    <del>￥10.00</del>
                                </li>
                                <li class="weui-media-box__info__meta weui-flex__item text-left">快递￥0.00</li>
                                <li class="weui-media-box__info__meta weui-flex__item text-center">月销量9999</li>
                            </ul>
                        </div>
                    </div>
                </div>

                <div class="weui-loadmore weui-loadmore_line info-title">
                    <span class="weui-loadmore__tips bg-page">商品详情</span>
                </div>

                <div class="weui-panel">
                    <div class="weui-panel__hd">
                        <div class='weui-media-box weui-media-box_text'>
                            <img src='images/tmp/fm.jpg'>
                            <img src='images/tmp/fm.jpg'>
                            <img src='images/tmp/fm.jpg'>
                            <img src='images/tmp/fm.jpg'>
                            <img src='images/tmp/fm.jpg'>
                            <img src='images/tmp/fm.jpg'>
                            <img src='images/tmp/fm.jpg'>
                        </div>
                    </div>

                </div>

            </div>

        </div>
        <div class="weui-flex weui-tabbar botton-bar">
            <div class="weui-flex__item text-center cart btn">
                加入购物车
            </div>
            <div class="weui-flex__item text-center buy btn">
                立即购买
            </div>
        </div>
    </div>
    
`,

    data: function () {
        return {
            tabbar_active_index: this.$router.path
        }
    },

    methods: {
        tabbarActive: function (index) {
            this.$data.tabbar_active_index = index;
        },
        isTabbarActive: function (path) {
            return this.$route.path === path;
        }
    }
};

routes.push({
    path: '/product',
    component: function (resolve) {

        // 异步加载数据
        // $.get("xxx",function (data) {
        //     return resolve(Component_product)
        // })

        return resolve(Component_product) //
    },
});