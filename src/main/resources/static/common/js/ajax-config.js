function $JSON(json) {
    return recode(json, json)

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