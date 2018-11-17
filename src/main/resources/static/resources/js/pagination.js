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

function initTbody(table, data, i) {
    var ths = table.find("tr").first().children("th");
    var setting = [];
    ths.each(function () {
        setting.push({
            field: $(this).attr("field"),
            formarter: $(this).attr("formarter")
        })
    });

    if (table.find("tbody").length == 0) {
        table.append("<tbody></tbody>");
    }
    var tbody = table.children("tbody");
    var html = "";
    var rowDate = data.content;
    for (var row = 0; row < rowDate.length; row++) {
        html += "<tr>"
        for (var index = 0; index < setting.length; index++) {
            var field = setting[index].field;
            var formarter = setting[index].formarter;
            var value = null;
            if (field == undefined) {

            } else if (field == "_rownumber") {
                value = (data.number - 1) * data.size + row + 1;
            } else if (field == "_rowndata" && formarter != undefined) {
                value = rowDate[row];
                value._row = row;
                value._index = index;
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
                if (typeof value == "number" && (value.toString()).indexOf(".") != -1) {
                    value = value.toFixed(2);
                }
            }
            try {
                if (formarter != undefined) {
                    var cell = {
                        row: row,
                        index: index,
                        data: rowDate[row]
                    };
                    value = eval(formarter + "(value,cell)");
                }
            } catch (err) {
                console.log(err);
            }
            value = value == undefined ? '--' : value;
            html += "<td>" + value + "</td>";
        }
        html += "</tr>"
    }
    tbody.html(html)

    var pagerId = "pager_" + i;
    var pageDiv = $(table.attr("pagination"));
    var totalpage = Math.floor((data.total - 1) / data.size + 1);

    var html = "";
    html += '<div align="center" class="hjpage" style="white-space: pre;" id="' + pagerId
        + '">'
    html += '第' + data.number + '页  (每页' + data.size + '条，共' + totalpage + '页，'
        + data.total + '条)   '
    if (data.number > 1) {
        html += '<a id="pre_' + i + '" href="javascript:;">上一页</a>   '
    } else {
        html += '上一页   '
    }
    if (data.number < totalpage) {
        html += '<a id="nex_' + i + '" href="javascript:;">下一页</a>   '
    } else {
        html += '下一页   '
    }
    html += '第<input  id="NO_' + i
        + '" type="text">页  <a  id="GO_' + i
        + '"  href="javascript:;">跳转</a>   '
    html += '</div>'
    $("#" + pagerId).remove();
    table.after(html);

    $("#pre_" + i).click(function () {
        toPage(table, data.number - 1, i);
    });

    $("#nex_" + i).click(function () {
        toPage(table, data.number + 1, i);
    });

    $("#GO_" + i).click(function () {
        var number = $("#NO_" + i).val();
        number = number < 1 ? 1 : number;
        number = number > totalpage ? totalpage : number;
        toPage(table, number, i);
    });

}

function toPage(table, page, i) {
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

    var url = table.attr("url")// + "?page=" + page;
    var data = {url: url, qData: qData, i: i};
    table.data("data", data);
    $.get(url, qData, function (resp) {
        initTbody(table, resp, i);
    })
}

function reInit(table) {
    console.log(table.data("data"))
    i = table.data("data").i
    toPage(table, 1, i);
}

function reloadTbody(table) {
    var data = table.data("data");
    $.get(data.url, data.qData, function (resp) {
        initTbody(table, resp, data.i);
    })
}

function auditStatusFmt(value) {
    return ["待审核", "审核通过", "受理中"][value]
}

function operationBtn(id, btn0, btn1, btn2) {
    var html = '<a href="javascript:;" onclick="operation0(' + id + ')">' + btn0 + '</a>';
    if (btn1 != undefined) {
        html += ' | <a href="javascript:;" onclick="operation1(' + id + ')">' + btn1 + '</a>';
    }
    if (btn2 != undefined) {
        html += ' | <a href="javascript:;" onclick="operation2(' + id + ')">' + btn2 + '</a>';
    }
    return html;
}

function _operationFmt(value, rowdata) {
    var id = rowdata.data.id
    if (value == 0) {
        return operationBtn(id, "同意", "拒绝");
    }
    return "无";
}