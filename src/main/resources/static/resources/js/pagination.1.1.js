//huang!!!

$(document).ready(function () {
    var tables = $("table[url]");
    var i = 0;
    tables.each(function () {
        var data = initTable($(this), i);
        i++;
    })
})

function initTable(table, i) {
    toPage(table, 1, i);
}

function initTbody(table, data, tableIndex) {
    var ths = table.find("tr").first().children("th");
    var setting = [];
    ths.each(function () {
        setting.push({
            field: $(this).attr("field"),
            formarter: $(this).attr("formarter"),
            numfix: $(this).attr("numfix"),
            numsign: $(this).attr("numsign")
        })
    });

    if (table.find("tbody").length == 0) {
        table.append("<tbody></tbody>");
    }
    var tbody = table.children("tbody");
    tbody.html("");
    var rowDate = data.content;
    for (var row = 0; row < rowDate.length; row++) {
        var tr = $("<tr></tr>")
        tbody.append(tr);
        for (var index = 0; index < setting.length; index++) {
            var field = setting[index].field;
            var formarter = setting[index].formarter;
            var numfix = setting[index].numfix;
            var numsign = setting[index].numsign;
            var value = null;
            if (field == undefined) {
                if (formarter != undefined)
                    value = rowDate[row];
            } else if (field == "_rownumber") {
                value = (data.number - 1) * data.size + row + 1;
            } else if (field == "_row") {
                return row + 1;
            } else {
                field = field.split(".")
                value = rowDate[row];
                for (var x = 0; x < field.length; x++) {
                    if (value != undefined) {
                        value = value[field[x]]
                        if (value != undefined && value.$ref) {
                            value = value.$ref.replace(/\$/, "data")
                            value = eval(value)
                        }
                    }
                }
            }
            try {
                if (formarter != undefined && formarter != "") {
                    value = eval(formarter + "(value,row,index)");
                }
            } catch (err) {
                console.log(err);
            }
            value = value == undefined ? '--' : value;
            value = value.toString() == "[object Object]" ? "--" : value;
            if (typeof value == "number") {
                var num = value
                if (numfix == undefined) {
                    numfix = (value.toString()).indexOf(".") != -1 ? 2 : 0;
                }
                value = value.toFixed(numfix);
                if (numsign && num > 0) {
                    value = "+" + value;
                }
            }
            tr.append($("<td class='col-" + tableIndex + "-" + index + "'>" + value + "</td>"));
            tr.data("data", rowDate[row]);
        }
    }
    if (rowDate.length == 0) {
        tbody.html("<tr><td colspan='" + (setting.length + 1) + "' align='center'>暂无数据</td></tr>");
        $("#pager_" + tableIndex).html("");
    } else {
        var initpagintionFun = table.attr("initPagintion")
        if (initpagintionFun) {
            eval(initpagintionFun + "(data, table, tableIndex)");
        } else {
            initPagintion(data, table, tableIndex);
        }
    }
    var callback = table.attr("callback");
    if (callback != undefined) {
        callback = callback + "(data)"
        eval(callback)
    }
}

function initPagintion(data, table, tableIndex) {
    if (typeof layui != "undefined") {
        layui.use(['laypage', 'layer'], function () {
            var laypage = layui.laypage
            var pagerId = "pager_" + tableIndex;
            if (laypage.render && $("#" + pagerId).length == 0) {
                var html = '<div align="center"  id="' + pagerId + '"></div>'
                table.after(html);
                table.data("initedPagintion", false)
                laypage.render({
                    elem: pagerId
                    , count: data.totalElements
                    , limit: data.size
                    , layout: ['count', 'prev', 'page', 'next', 'skip']
                    , jump: function (obj) {
                        if (table.data("initedPagintion"))
                            toPage(table, obj.curr, tableIndex);
                        else table.data("initedPagintion", true)
                    }
                });

            }
        })
        return
    }

    var pagerId = "pager_" + tableIndex;
    var total = data.totalElements
    var totalpage = Math.floor((data.totalElements - 1) / data.size + 1);

    var html = "";
    html += '<div align="center" class="hjpage" style="margin-top: 20px;margin-bottom: 20px" id="' + pagerId + '">'
    html += '第' + data.number + '页  (每页' + data.size + '条，共' + totalpage + '页，' + total + '条)   '
    if (data.number > 1) {
        html += '<a id="pre_' + tableIndex + '" href="javascript:;">上一页</a>   '
    } else {
        html += '上一页   '
    }
    if (data.number < totalpage) {
        html += '<a id="nex_' + tableIndex + '" href="javascript:;">下一页</a>   '
    } else {
        html += '下一页   '
    }
    html += '第<input style="width: 20px" id="NO_' + tableIndex
        + '"  type="text">页  <a  id="GO_' + tableIndex
        + '"  href="javascript:;">跳转</a>   '
    html += '</div>'
    $("#" + pagerId).remove();
    table.after(html);

    $("#pre_" + tableIndex).click(function () {
        toPage(table, data.number - 1, tableIndex);
    });

    $("#nex_" + tableIndex).click(function () {
        toPage(table, data.number + 1, tableIndex);
    });

    $("#GO_" + tableIndex).click(function () {
        var number = $("#NO_" + tableIndex).val();
        number = number < 1 ? 1 : number;
        number = number > totalpage ? totalpage : number;
        toPage(table, number, tableIndex);
    });
}


function toPage(table, page, tableIndex) {
    var qData = "";
    var formId = table.attr("paramsForm");
    if (formId != undefined) {
        qData = $(formId).serialize();
    }

    if (qData == "") {
        qData += "page=" + page
    } else {
        qData += "&page=" + page;
    }

    var url = table.attr("url");
    var data = {url: url, qData: qData, i: tableIndex};
    table.data("data", data);
    $.get(url, qData, function (resp) {
        resp = $JSON(resp)
        initTbody(table, resp, tableIndex);
    })
}

function $JSON(json) {
    return recode(json, json);

    function recode($, data) {
        if (typeof data != "object") {
            return data;
        }
        for (var key in data) {
            var value = data[key];
            if (value.$ref != undefined) {
                value = eval(value.$ref);
            }
            data[key] = recode($, value);
        }
        return data;
    }
}

function reInit(tables) {
    if (typeof tables == "undefined")
        tables = $("table")
    tables.each(function () {
        if ($(this).data("data") != undefined) {
            var i = $(this).data("data").i
            $("#pager_" + i).remove();
            toPage($(this), 1, i);
        }
    })
}

function _statusFmt(value) {
    return ["受理中", "成功", "审核不通过"][value]
}

function _upcase(value) {
    return value.toUpperCase();
}

function _lowcase(value) {
    return value.toLowerCase();
}

function _date(value) {
    return value.substring(0, 10)
}

function _operationBtn(id, btn0, btn1, btn2) {
    var html = '<a href="javascript:;" onclick="operation0(' + id + ')">' + btn0 + '</a>';
    if (btn1 != undefined) {
        html += ' | <a href="javascript:;" onclick="operation1(' + id + ')">' + btn1 + '</a>';
    }
    if (btn2 != undefined) {
        html += ' | <a href="javascript:;" onclick="operation2(' + id + ')">' + btn2 + '</a>';
    }
    return html;
}
