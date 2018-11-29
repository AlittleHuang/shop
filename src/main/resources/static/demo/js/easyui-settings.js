$.fn.pagination.defaults.displayMsg = "第 {from} 至 {to} 条，共 {total} 条记录";
$.fn.pagination.defaults.pageList = [20, 30, 50, 100];
$.fn.pagination.defaults.pageSize = 20;
$.fn.datagrid.defaults.pageList = [20, 30, 50, 100];
$.fn.datagrid.defaults.pageSize = 20;
$.fn.datagrid.defaults.fitColumns = true;
$.fn.datagrid.defaults.pagination = true;
$.fn.datagrid.defaults.pagePosition = "top";
$.fn.datagrid.defaults.singleSelect = true;
$.fn.datagrid.defaults.scrollbarSize = 0;
$.fn.datagrid.defaults.method = "get";

$.fixfastJson = function (json) {
    var traversed = [];
    return recode(json, json);

    function recode($, data) {
        if (traversed.includes(data)) {
            return data
        }
        traversed.push(data)

        if (typeof data !== "object") {
            return data;
        }
        for (var key in data) {
            var value = data[key];
            if (value.$ref !== undefined) {
                value = eval(value.$ref);
                data[key] = value
            } else {
                data[key] = recode($, value);
            }
        }
        return data;
    }
}

$.fn.datagrid.methods.options = function (jq) {
    var _1de = $.data(jq[0], "datagrid").options;
    var _1df = $.data(jq[0], "datagrid").panel.panel("options");
    var opts = $.extend(_1de, {
        width: _1df.width,
        height: _1df.height,
        closed: _1df.closed,
        collapsed: _1df.collapsed,
        minimized: _1df.minimized,
        maximized: _1df.maximized
    });

    var columns = opts.columns;
    for (var k = 0; k < columns.length; k++) {
        for (var i = 0; i < columns[k].length; i++) {
            var column = columns[k][i];
            var field = column.field;
            if (field && !column.formatter && (field.includes(".") || field.includes("["))) {
                column.field_fix = field;
                column.field = i + "-" + k;
                column.formatter = function (value, row) {
                    try {
                        return eval("row." + this.field_fix)
                    } catch (e) {
                        console.error("row." + value);
                        console.error(e);
                        return null
                    }
                }
            }
        }
    }

    return opts;
};

$.fn.datagrid.defaults.loader = function (param, success, error) {
    var opts = $(this).datagrid("options");
    if (!opts.url) {
        return false;
    }
    $.ajax({
        type: opts.method, url: opts.url, data: param, dataType: "json", success: function (data) {
            success({rows: $.fixfastJson(data).rows || data.content, total: data.total || data.totalElements});
        }, error: function () {
            error.apply(this, arguments);
        }
    });
};

