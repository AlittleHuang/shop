const Component_product = {
    template: `
<div>    
    <div class="header">
        <a @click="$router.go(-1)" class="weui-btn weui-btn_mini weui-btn_default" style='margin-left: 0px;'>返回</a>
    </div>
    <div class='weui-tab__panel'>
    
    </div>
    <div class="weui-flex weui-tabbar">
        <div class="weui-flex__item">
            <div class="weui-btn weui-btn_warn">加入购物车</div>
        </div>
        <div class="weui-flex__item">
            <div class="weui-btn weui-btn_warn">购买</div>
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
    component: Component_product,
});