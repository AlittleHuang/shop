// 依赖 ajaxfileupload.js

$(document).ready(function () {
    $(".img-upload").change(function () {
        // console.log("img-upload")
        var uploadUrl = $(this).attr("uploadUrl");
        var id = $(this).attr("id");
        var targetId = $(this).attr("targetId");
        var previewId = $(this).attr("previewId");
        uploadImg(uploadUrl, id, targetId, previewId);
    })
})

function uploadImg(url, fileElementId, target, preview) {
    $.ajaxFileUpload({
        url: url,
        secureuri: false,
        fileElementId: fileElementId,
        dataType: "application/json",
        type: "POST",
        success: function (resp, status) {
            resp = JSON.parse($(resp).text())
            console.log(resp)
            if (resp.success) {
                var data = resp.data
                console.log(data);
                $("#" + target).val(data);
                if (preview != undefined)
                    $("#" + preview).attr("src", data);
            } else {
                alert(resp.message)
            }
        },
        error: function (data, status, e) {
            console.log(e);
        }
    })
    return false;
};