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
