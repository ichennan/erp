;
var detailId = 0;
var skuId = 0;
var $contentForm = $("#contentForm");
var $skuContentForm = $("#skuContentForm");
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

    $('input[pid=pcsPerBox]', $contentForm).on('keyup change', function () {
        var thatValue = $(this).val();
        if(thatValue){
            var supposedWeight = (20 / thatValue).toFixed(2);
            $contentForm.find("input[pid=weight]").val(supposedWeight);
        }
    } );
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
    var theadNames = ['id', '编号', '名称', '采购价', '每箱装/重量', '分类', '备注'];
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
                var pcsPerBoxAndWeight = obj.pcsPerBox + "/" + obj.weight;
                var tds = [obj.id, obj.sn, obj.snname, obj.purchasePrice, pcsPerBoxAndWeight, obj.subject, obj.remark];
                $.each(tds, function (index_2, obj_2) {
                    obj_2 = obj_2 ? obj_2 : "";
                    tr.append("<td>" + obj_2 + "</td>");
                })
                if(obj.skuCount <= 0){
                    tr.addClass("noSku");
                }
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
                "pageLength": 99999,
                "order": [[ 2, "asc" ]],
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
    $("#skuTable").remove();
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

    showSkuList();
}

function saveDetail(action){
    console.log("saveDetail: " + action);
    if(action != 'delete' && !validateForm()){
        return false;
    }
    var data = {};
    data.action = action;
    $contentForm.find("[pid]").each(function () {
        data[$(this).attr("pid")] = $(this).val();
    });
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
                $("[pid=storeId]").append(option);
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

//

// detailList

function showSkuList(){
    console.log("showDetailList()");
    //
    var skuTable = $("<table class='table table-bordered data-table' id='skuTable'></table>");
    var thead = $("<thead><tr></tr></thead>");
    var theadNames = ['店铺', 'SKU', 'FNSKU', 'ASIN', '优先级'];
    $.each(theadNames, function (index, obj) {
        thead.find("tr").append("<th>" + obj + "</th>");
    })
    var tbody = $("<tbody></tbody>");
    skuTable.append(thead).append(tbody);
    $("#skuTableDiv").append(skuTable);
    var data = {};
    data.productId = detailId;
    var ajaxUrl = 'findSkus'
    $.ajax({
        type: "POST",
        url: ajaxCtx + ajaxUrl,
        data: data,
        dataType: "json",
        success: function (rs) {
            console.log(rs);
            $.each(rs.array, function (index, obj) {
                var tr = $("<tr></tr>");
                var tds = [parent.$.retrieveStoreName(obj.storeId), obj.sku, obj.fnsku, obj.asin, obj.priority];
                $.each(tds, function (index_2, obj_2) {
                    obj_2 = obj_2 ? obj_2 : "";
                    tr.append("<td>" + obj_2 + "</td>");
                })
                tr.click(function () {
                    showSkuContentModal(obj.id);
                });
                tbody.append(tr);
            });
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            $.showErrorModal(XMLHttpRequest.responseText);
            console.log("error");
        },
        complete: function () {
        }
    });
}

function showSkuContentModal(id){
    $('#skuContentModal').modal('show');
    skuId = id;
    console.log("showSkuContentModal: " + skuId);
    //
    console.log($skuContentForm.find("[pid]").length);
    $skuContentForm.find("[pid]").each(function () {
        $(this).val("").trigger("change");
    });
    if(skuId - 0 == 0){
        $skuContentForm.find("button.update").hide();
        $skuContentForm.find("button.create").show();
        return;
    }
    $skuContentForm.find("button.update").show();
    $skuContentForm.find("button.create").hide();

    var data = {};
    data.skuId = skuId;
    var ajaxUrl = 'getSku';
    $.ajax({
        type: "POST",
        url: ajaxCtx + ajaxUrl,
        data: data,
        dataType: "json",
        success: function (rs) {
            console.log("getSku.success");
            console.log(rs);
            console.log($skuContentForm.find("[pid]").length);
            $skuContentForm.find("[pid]").each(function () {
                console.log($(this).attr("pid"));
                var jpa = rs.data[$(this).attr("pid")];
                $(this).val(jpa).trigger("change");
            });
        },
        error: function (XMLHttpRequest) {
            console.log("getSku.error");
            console.log(XMLHttpRequest);
            $.showErrorModal(XMLHttpRequest.responseText);
        },
        complete: function () {
            console.log("getSku.complete");
        }
    });

}

function deleteSku(){
    deleteConfirm(saveSku);
}


function saveSku(action){
    console.log("saveSku: " + action);
    if(action != 'delete' && !validateForm()){
        return false;
    }
    var data = {};
    data.action = action;
    $skuContentForm.find("[pid]").each(function () {
        data[$(this).attr("pid")] = $(this).val();
    });
    data.productId = detailId;
    console.log(data);
    var ajaxUrl = 'saveSku';
    $.ajax({
        type: "POST",
        url: ajaxCtx + ajaxUrl,
        data: data,
        dataType: "json",
        success: function (rs) {
            console.log("saveSku.success");
            console.log(rs);
            $('#skuContentModal').modal('hide');
            toDetail(detailId);
            $.showToastr();
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.log("saveSku.error");
            console.log(XMLHttpRequest);
            $.showErrorModal(XMLHttpRequest.responseText);
        },
        complete: function () {
            console.log("saveSku.complete");
        }
    });
}

function deleteConfirm(fc) {
    var randomNumber = parseInt(10*Math.random());
    $.confirm({
        title: '删除确认框',
        content: '' +
            '<div class="form-group">' +
            '<label>即将删除，确认请输入数字: ' + randomNumber + '</label>' +
            '<input type="text" placeholder="请输入数字以确认删除" class="randomNumber form-control" required />' +
            '</div>',
        buttons: {
            ok: {
                text: '确认',
                btnClass: 'btn-blue',
                action: function () {
                    var randomNumberInput = this.$content.find('.randomNumber').val();
                    if(!randomNumberInput || (randomNumber - randomNumberInput != 0)){
                        $.alert('请输入正确数字以确认删除');
                        return false;
                    }
                    fc("delete");
                }
            },
            cancel: {
                text: "取消",
                btnClass: 'btn',
                keys: ['esc'],
                action:function () {
                    return;
                }
            }
        }
    });
}