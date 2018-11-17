$(document).ready(function () {
    pullRefresh();
    window.onresize = pullRefresh; //浏览器窗口发生变化时同时变化DIV高度
})

function pullRefresh() {
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
    $(".pullRefresh").css("min-height", winHeight + "px")
    $(".mui-content").css("min-height", winHeight + "px")
    $(".index-content").css("min-height", winHeight + "px")
    $(".wodeerwm").css("min-height", winHeight + "px")
}

var pageNumber = 1;
var size = 5

function resetPageInfo() {
    pageNumber = 1;
    size = 5
}

function initList(url, formarter) {
    $.get(url, {page: pageNumber++, size: size}, function (resp) {
        $JSON(resp)
        if (resp.content[0] == undefined) {
            $("#data-list").html("<div class='zanwushuju'>暂无数据</div>")
            return;
        }
        $("#data-list").html(toHtml(resp, formarter) + "<page></page>")
        mui.init({
            pullRefresh: {
                container: "#page-content",//待刷新区域标识，querySelector能定位的css选择器均可，比如：id、.class等
                up: {
                    height: 245,//可选.默认50.触发上拉加载拖动距离
                    auto: false,//可选,默认false.自动上拉加载一次
                    contentrefresh: "正在加载...",//可选，正在加载状态时，上拉加载控件上显示的标题内容
                    contentnomore: '没有更多数据了',//可选，请求完毕若没有更多数据时显示的提醒内容；
                    callback: function () {
                        var e = this;
                        $.get(url, {page: pageNumber++, size: size}, function (pagedata) {
                            var html = toHtml(pagedata, formarter) + "<page></page>"
                            $("page:last").after(html);
                            e.endPullupToRefresh(pagedata.number >= pagedata.totalPages);
                        }).error(function () {
                            e.endPullupToRefresh(false);
                        })
                    }
                },
                down: {
                    callback: function () {
                        var e = this;
                        reInit();
                        e.endPulldownToRefresh(true)
                        e.endPullupToRefresh(false);
                    }
                }
            }
        });
    })


    function toHtml(pagedata, formarter) {
        var list = pagedata.content
        var html = ""
        for (var i = 0; i < list.length; i++) {
            var row = list[i]
            try {
                html += formarter(row, pagedata, i)
            } catch (e) {
                console.log(e);
            }
        }
        return html;
    }


}


function $JSON(json) {

    function Obj($, date, count) {
        if (count++ > 5000) return;
        if (typeof date != "object")
            return date;
        for (var key in date) {
            var value = date[key];
            if (value.$ref != undefined) {
                value = eval(value.$ref);
            }
            if (typeof value != "object")
                this[key] = value;
            else
                this[key] = new Obj($, value, count);
        }
    }

    return new Obj(json, json, 0)
}
