;
var detailId = 0;
var $contentForm = $("#contentForm");
var ajaxCtx = 'product/'
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

    setStore();


    $('#file_upload_form').fileupload({
        url: ajaxCtx + "uploadProductImage",
        dataType: 'json',
        previewSourceMaxFileSize: 0,
        process: null,
        done: function (e, data) {
            renewProductImage(true);
        },
        add: function (e, data) {
            var goUpload = true;
            var uploadFile = data.files[0];
            if (!(/\.(jpg|jpeg|png)$/i).test(uploadFile.name)) {
                $.showErrorModal("Only image files (gif, jpg, jpeg, png) files allowed");
                goUpload = false;
            }
            if (uploadFile.size > 2100000) { // 1mb
                $.showErrorModal("Please upload a smaller image, max size is 1 MB");
                goUpload = false;
            }
            if (goUpload == true) {
                var uploadImageData = {};
                uploadImageData.productId = detailId;
                data.formData = {'uploadImageData': JSON.stringify(uploadImageData)};
                data.submit();
                console.log("image uploaded submit productId: " + uploadImageData.productId);
            }
        }
    });

    $("#productImage").click(function(){
        $('#file_upload_form').trigger("click");
    });
});

function showList(){
    console.log("showList()");
    $("#tableBox").show();
    $("#contentBox").hide();
    //
    $("#listTable").remove();
    $("#listTable_wrapper").remove();
    var listTable = $("<table class='table table-bordered data-table' id='listTable'></table>");
    var thead = $("<thead><tr></tr></thead>");
    var theadSearch = $("<thead class='theadSearch'><tr></tr></thead>");
    var theadNames = ['名称', '编号', '颜色', '尺寸','采购价','店铺','备注'];
    $.each(theadNames, function (index, obj) {
        thead.find("tr").append("<th>" + obj + "</th>");
        theadSearch.find("tr").append("<th><input style='width:1px'></th>");
    })
    var tbody = $("<tbody></tbody>");
    listTable.append(thead).append(theadSearch).append(tbody);
    $("#tableDiv").append(listTable);
    var data = {};
    var ajaxUrl = 'findAll'
    $.ajax({
        type: "POST",
        url: ajaxCtx + ajaxUrl,
        data: data,
        dataType: "json",
        success: function (rs) {
            console.log(rs);
            $.each(rs.array, function (index, obj) {
                var tr = $("<tr></tr>");
                var tds = [obj.name, obj.sn, obj.color, obj.size, obj.purchasePrice, obj.store, obj.remark];
                $.each(tds, function (index_2, obj_2) {
                    obj_2 = obj_2 ? obj_2 : "";
                    tr.append("<td>" + obj_2 + "</td>");
                })
                tr.click(function () {
                    toDetail(obj.id);
                });
                tbody.append(tr);
            });

            var datatable = $('#listTable').DataTable({
                "bJQueryUI": true,
                "sPaginationType": "full_numbers",
                "ordering": true,
                "bSort": true,
                "language": $.dataTablesLanguage,
                "pageLength": 100,
                "order": [[ 0, "desc" ]],
            });

            theadSearch.find('input').css("width", "100%");
            $("#listTable_filter, #listTable_length").css("display", "none");

            datatable.columns().eq( 0 ).each( function ( colIdx ) {
                $( 'input', datatable.column( colIdx ).header2() ).on( 'keyup change', function () {
                    datatable
                        .column( colIdx )
                        .search( this.value )
                        .draw();
                } );
            } );
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            $.showErrorModal(XMLHttpRequest.responseText);
            console.log("error");
        },
        complete: function () {
        }
    });
}

function toDetail(id){
    location.hash == "#detailId=" + id ? $(window).trigger('hashchange') : location.hash = "#detailId=" + id;
}

function showDetail(){
    console.log("showDetail: " + detailId);
    $("#tableBox").hide();
    $("#contentBox").show();
    //
    $contentForm.find("[pid]").each(function () {
        $(this).val("").trigger("change");
    });
    $contentForm.find("[sid]").each(function () {
        $(this).val("").trigger("change");
    });
    if(detailId - 0 == 0){
        $contentForm.find("button.update").hide();
        $contentForm.find("button.create").show();
        $(".createHidden").hide();
        return;
    }
    $(".createHidden").show();
    $contentForm.find("button.update").show();
    $contentForm.find("button.create").hide();
    renewProductImage();

    var data = {};
    data.id = detailId;
    var ajaxUrl = 'getDetail';
    $.ajax({
        type: "POST",
        url: ajaxCtx + ajaxUrl,
        data: data,
        dataType: "json",
        success: function (rs) {
            console.log("getDetail.success");
            console.log(rs);
            $contentForm.find("[pid]").each(function () {
                var jpa = rs.data[$(this).attr("pid")];
                $(this).val(jpa).trigger("change");
            });
            if(rs.skuArray){
                $.each(rs.skuArray, function(index, obj){
                    var skuDivNo = index + 1;
                    var skuDiv = $contentForm.find("[skuDiv=" + skuDivNo + "]");
                    skuDiv.find("[sid]").each(function () {
                        var jpa = obj[$(this).attr("sid")];
                        $(this).val(jpa).trigger("change");
                    });
                })
            }
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.log("getDetail.error");
            console.log(XMLHttpRequest);
            $.showErrorModal(XMLHttpRequest.responseText);
        },
        complete: function () {
            console.log("getDetail.complete");
        }
    });
}

function saveDetail(action){
    console.log("saveDetail: " + action);
    if(action != 'delete' && !validateForm()){
        return false;
    }
    var data = {};
    data.action = action;
    var skuArray = [];
    $contentForm.find("[pid]").each(function () {
        data[$(this).attr("pid")] = $(this).val();
    });
    $contentForm.find("[skuDiv]").each(function () {
        var $this = $(this);
        var skuJson = {};
        $this.find("[sid]").each(function () {
            skuJson[$(this).attr("sid")] = $(this).val();
        });
        if(skuJson.sku && skuJson.fnsku && skuJson.storeId){
            skuArray.push(skuJson);
        }
    });
    data.skuArray = skuArray;
    console.log(data);
    var ajaxUrl = 'saveDetail';
    $.ajax({
        type: "POST",
        url: ajaxCtx + ajaxUrl,
        data: JSON.stringify(data),
        dataType: "json",
        contentType: "application/json",
        success: function (rs) {
            console.log("saveDetail.success");
            console.log(rs);
            toDetail(rs.data.id);
            $.showToastr();
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.log("saveDetail.error");
            console.log(XMLHttpRequest);
            $.showErrorModal(XMLHttpRequest.responseText);
        },
        complete: function () {
            console.log("saveDetail.complete");
        }
    });
}

function validateForm(contentForm) {
    var contentForm = contentForm ? contentForm : $("#contentForm");
    if (contentForm.get(0).checkValidity()){
        return true;
    }else{
        contentForm.find("[type='submit']").trigger("click");
        return false;
    }
}

function renewProductImage(noCache){
    $("#productImage").empty().append("<img src=" + "product/getProductImage/empty.jpg " + "/>");
    var uploadFileName = "id" + detailId +".jpg";
    console.log("image uploaded done : " + uploadFileName);
    if(noCache){
        $("#productImage img").attr("src", "product/getProductImage/" + uploadFileName + "?noCache=" + new Date().getTime());
    }else{
        $("#productImage img").attr("src", "product/getProductImage/" + uploadFileName + "");
    }
    console.log("get image done : " + uploadFileName);
}

function setStore(){
    var ajaxUrl = 'findAllStore';
    var data = {};
    $.ajax({
        type: "POST",
        url: ajaxCtx + ajaxUrl,
        data: data,
        dataType: "json",
        success: function (rs) {
            console.log("findAllStore.success");
            console.log(rs);
            $.each(rs.array, function (index, obj) {
                var option = $("<option></option>");
                option.val(obj.id);
                option.text(obj.name);
                $("[sid=storeId]").append(option);
            })
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.log("findAllStore.error");
            console.log(XMLHttpRequest);
            $.showErrorModal(XMLHttpRequest.responseText);
        },
        complete: function () {
            console.log("findAllStore.complete");
        }
    });

}