;
(function ($) {
    var x = "x";
    jQuery(document).ready(function () {
        toastr.options.positionClass = 'toast-bottom-full-width';
    });

    $.showErrorModal = function (text) {
        if($("#errorModal").length <= 0){
            alert(text);
            return;
        }
        $("#errorModal .modal-body p").text(text);
        $('#errorModal').modal('show');
    }

    $.showMessageModal = function (text) {
        if($("#messageModal").length <= 0){
            alert(text);
            return;
        }
        $("#messageModal .modal-body p").text(text);
        $('#messageModal').modal('show');
    }

    $.jsonToString = function (json) {
        return (json ? JSON.stringify(json) : "");
    }

    $.stringToJson = function (str) {
        return (str ? $.parseJSON(str) : {});
    }

    $.dataTablesLanguage = {"paginate": {
            "previous": "前一页",
            "next": "后一页",
            "first": "首页",
            "last": "尾页"}};

    $.showToastr = function(m){
        var message = m ? m : "操作成功";
        if(!toastr){
            alert(message);
            return;
        }
        toastr.success(message);
    }

    var datePattern = "^\\d{8}$";
    $(".datePattern").attr("title", "例如: 20191231").attr("pattern", datePattern);

})(jQuery);