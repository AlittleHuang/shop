var location_href = location.href;
sessionStorage.removeItem("sm.router")
$.config = {
    routerFilter: function ($link) {
        return !!(location_href !== location.href || !$link.hasClass("back"))
    },
    router: false
};


