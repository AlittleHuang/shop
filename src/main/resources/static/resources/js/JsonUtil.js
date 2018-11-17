function $JSON(json) {
    return new $refGet(json, json)
}

function $refGet(root, date) {
    if (typeof date == "object") {
        for (var key in date) {
            var value = date[key]
            var $ = root
            if (value.$ref != undefined) {
                value = eval(value);
            }
            if (typeof value == "object") {
                value = new $refGet(root, value)
            }
            this[key] = value;
        }
    } else {
        return date;
    }
}