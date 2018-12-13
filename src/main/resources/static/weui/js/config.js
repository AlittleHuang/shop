axios.interceptors.response.use(function (resp) {
    if (typeof resp.data === "object") {
        rmCircularReference(resp.data);
    }
    return resp
});

Vue.filter("toFixed2", function (value) {
    return (1 * value).toFixed(2)
});

var Event = new Vue();