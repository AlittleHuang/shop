axios.interceptors.response.use(function (resp) {
    if (typeof resp.data === "object") {
        rmCircularReference(resp.data);
    }
    return resp
});

Vue.filter("toFixed2", function (value) {
    return (1 * value).toFixed(2)
});

history.pushState(null, null, document.URL);

window.addEventListener('popstate', function (data) {
    console.log(data)
    //history.pushState(null, null, document.URL);
});

window.addEventListener('pushstate', function (data) {
    console.log(data)
    //history.pushState(null, null, document.URL);
});

var Event = new Vue();