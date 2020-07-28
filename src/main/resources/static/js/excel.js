;
var detailId = 0;
var $contentForm = $("#contentForm");
var ajaxCtx = 'excel/'
$(document).ready(function(){
    $(window).on("hashchange",function () {
        var hash = location.hash ? location.hash : '';
        var hash6 = hash.substring(0, 7).toString();
        console.log('hashChange to ' + hash);
        if (hash6 == "#detail") {
            //detailId=123456
            detailId = hash.substring(10);
            showDetail();
        } else {
            detailId = 0;
            showList();
        }
    }).trigger("hashchange");


    $('#file_upload_form').fileupload({
        url: ajaxCtx + "uploadSupplierDelivery",
        dataType: 'json',
        previewSourceMaxFileSize: 0,
        process: null,
        done: function (e, data) {
            renewProductImage(true);
        },
        add: function (e, data) {
            var goUpload = true;
            var uploadFile = data.files[0];
            if (!(/\.(xls|xlsx)$/i).test(uploadFile.name)) {
                $.showErrorModal("Only excel files (xls, xlsx) files allowed");
                goUpload = false;
            }
            if (uploadFile.size > 21000000) { // 1mb
                $.showErrorModal("Please upload a smaller excel, max size is 10 MB");
                goUpload = false;
            }
            if (goUpload == true) {
                // var uploadImageData = {};
                // uploadImageData.productId = detailId;
                // data.formData = {'uploadImageData': JSON.stringify(uploadImageData)};
                data.submit();
                console.log("excel uploaded: " + uploadFile.name);
            }
        }
    });

    $("#productImage").click(function(){
        $('#file_upload_form').trigger("click");
    });
});

function showList(){
    console.log("showList()");
}

function showDetail(){
    console.log("showDetail: " + detailId);
}

function saveDetail(action){
    console.log("saveDetail: " + action);
}