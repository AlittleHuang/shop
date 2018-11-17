$(document).ready(function () {
    autoheight();
    window.onresize = autoheight; //浏览器窗口发生变化时同时变化DIV高度
})

function autoheight() {
    var winHeight = 0;
    if (window.innerHeight)
        winHeight = window.innerHeight;
    else if ((document.body) && (document.body.clientHeight))
        winHeight = document.body.clientHeight;
    if (document.documentElement && document.documentElement.clientHeight)
        winHeight = document.documentElement.clientHeight;
    if ($(".phofooter").length > 0) {
        $(".page").css("margin-bottom", 75);
        winHeight -= 75;
    }
    $(".autoheight").css("min-height", winHeight + "px")
    $(".mui-content").css("min-height", winHeight + "px")
    $(".wytxls-content").css("min-height", winHeight + "px")
    $(".wytx-content").css("min-height", winHeight + "px")
    $(".email-content").css("min-height", winHeight + "px")
    $(".cwglind-content").css("min-height", winHeight + "px")
    $(".woderwm-content").css("min-height", winHeight + "px")
}
