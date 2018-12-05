const Component_main = {
    template: `

<div class="weui-tab">
    <div class="weui-tab__panel">
        <router-view></router-view>
    </div>
    <div class="weui-tabbar">
        <router-link to="/main" append class="weui-tabbar__item" :class="{ 'weui-bar__item_on': isTabbarActive('/main') }">
            <img src="./images/icon_tabbar.png" alt="" class="weui-tabbar__icon"  v-on:click='tabbarActive(0)'>
            <p class="weui-tabbar__label">主页</p>
        </router-link>
        <router-link to="/bar" append class="weui-tabbar__item" :class="{ 'weui-bar__item_on': isTabbarActive('/bar') }">
            <img src="./images/icon_tabbar.png" alt="" class="weui-tabbar__icon" v-on:click='tabbarActive(1)'>
            <p class="weui-tabbar__label">通讯录</p>
        </router-link>
        <router-link to="/foo1" append class="weui-tabbar__item" :class="{ 'weui-bar__item_on': isTabbarActive('/foo1') }">
            <img src="./images/icon_tabbar.png" alt="" class="weui-tabbar__icon" v-on:click='tabbarActive(2)'>
            <p class="weui-tabbar__label">发现</p>
        </router-link>
        <router-link to="/bar1" append class="weui-tabbar__item" :class="{ 'weui-bar__item_on': isTabbarActive('/bar1') }">
            <img src="./images/icon_tabbar.png" alt="" class="weui-tabbar__icon" v-on:click='tabbarActive(3)'>
            <p class="weui-tabbar__label">我</p>
        </router-link>
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


const index_main = [];
index_main.push(
    {path: '/main', component: {template: `<router-link to='product'>商品详情</router-link>`}},
);

routes.push({
    path: '/',
    component: Component_main,
    children: index_main
});