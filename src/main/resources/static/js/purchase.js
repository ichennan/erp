;
var detailId = 0;
var detailListDetailId = 0;
var $contentForm = $("#contentForm");
var $detailListContentForm = $("#detailListContentForm");
var ajaxCtx = 'purchase/'
var ajaxCtx_product = 'product/'
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

    parent.$.refreshProductsSelect($detailListContentForm.find("[pid=productId]"));

});

function showList(){
    console.log("showList()");
    $("#tableBox").show();
    $("#contentBox").hide();
    $("#detailListBox").hide();
    //
    $("#listTable").remove();
    $("#listTable_wrapper").remove();
    var listTable = $("<table class='table table-bordered data-table' id='listTable'></table>");
    var thead = $("<thead><tr></tr></thead>");
    var theadSearch = $("<thead class='theadSearch'><tr></tr></thead>");
    var theadNames = ['上传日期', '发货日期', '收货日期', '费用','供应商','采购产品'];
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
                var tds = [obj.excelDate, obj.deliveryDate, obj.receivedDate, obj.amount, obj.supplier, parent.$.showProductNameGroupByProductIdGroup(obj.productIdGroup)];
                $.each(tds, function (index_2, obj_2) {
                    obj_2 = obj_2 ? obj_2 : "";
                    tr.append("<td>" + obj_2 + "</td>");
                })
                // tr.attr("title", parent.$.showProductNameGroupByProductIdGroup(obj.productIdGroup));
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
    $("#detailListTable").remove();
    //
    $contentForm.find("[pid]").each(function () {
        $(this).val("").trigger("change");
    });
    $contentForm.find("[pid=freight]").val("0").trigger("change");
    if(detailId - 0 == 0){
        $contentForm.find("button.update").hide();
        $contentForm.find("button.create").show();
        $(".createHidden").hide();
        return;
    }
    $(".createHidden").show();
    $contentForm.find("button.update").show();
    $contentForm.find("button.create").hide();
    $("#detailListBox").show();

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

    showDetailList();
}

function deleteDetail(){
    deleteConfirm(saveDetail);
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
        data: data,
        dataType: "json",
        success: function (rs) {
            console.log("saveDetail.success");
            console.log(rs);
            if("create" == action){
                toDetail(rs.data.id);
            }else{
                showList();
            }
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

// detailList

function showDetailList(){
    console.log("showDetailList()");
    //
    var detailListTable = $("<table class='table table-bordered data-table' id='detailListTable'></table>");
    var thead = $("<thead><tr></tr></thead>");
    var theadNames = ['产品', '采购数量', '收货数量', '采购单价'];
    $.each(theadNames, function (index, obj) {
        thead.find("tr").append("<th>" + obj + "</th>");
    })
    var tbody = $("<tbody></tbody>");
    detailListTable.append(thead).append(tbody);
    $("#detailListTableDiv").append(detailListTable);
    var data = {};
    data.purchaseId = detailId;
    var ajaxUrl = 'findAllDetail'
    $.ajax({
        type: "POST",
        url: ajaxCtx + ajaxUrl,
        data: data,
        dataType: "json",
        success: function (rs) {
            console.log(rs);
            $.each(rs.array, function (index, obj) {
                var tr = $("<tr></tr>");
                var tds = [parent.$.cacheProducts["id" + obj.productId].snname, obj.bookQuantity, obj.receivedQuantity, obj.unitPrice];
                $.each(tds, function (index_2, obj_2) {
                    obj_2 = obj_2 ? obj_2 : "";
                    tr.append("<td>" + obj_2 + "</td>");
                })
                tr.click(function () {
                    showDetailListContentModal(obj.id);
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

function showDetailListContentModal(id){
    $('#detailListContentModal').modal('show');
    detailListDetailId = id;
    console.log("detailListContent: " + detailListDetailId);
    //
    $detailListContentForm.find("[pid]").each(function () {
        $(this).val("").trigger("change");
    });
    $detailListContentForm.find("[pid=bookQuantity]").val("0").trigger("change");
    $detailListContentForm.find("[pid=receivedQuantity]").val("0").trigger("change");
    $detailListContentForm.find("[pid=unitPrice]").val("0").trigger("change");
    if(detailListDetailId - 0 == 0){
        $detailListContentForm.find("button.update").hide();
        $detailListContentForm.find("button.create").show();
        return;
    }
    $detailListContentForm.find("button.update").show();
    $detailListContentForm.find("button.create").hide();

    var data = {};
    data.id = detailListDetailId;
    var ajaxUrl = 'getDetailListDetail';
    $.ajax({
        type: "POST",
        url: ajaxCtx + ajaxUrl,
        data: data,
        dataType: "json",
        success: function (rs) {
            console.log("getDetailListDetail.success");
            console.log(rs);
            $detailListContentForm.find("[pid]").each(function () {
                var jpa = rs.data[$(this).attr("pid")];
                $(this).val(jpa).trigger("change");
            });
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.log("getDetailListDetail.error");
            console.log(XMLHttpRequest);
            $.showErrorModal(XMLHttpRequest.responseText);
        },
        complete: function () {
            console.log("getDetailListDetail.complete");
        }
    });

}

function deleteDetailListDetail(){
    deleteConfirm(saveDetailListDetail);
}


function saveDetailListDetail(action){
    console.log("saveDetailListDetail: " + action);
    if(action != 'delete' && !validateForm()){
        return false;
    }
    var data = {};
    data.action = action;
    $detailListContentForm.find("[pid]").each(function () {
        data[$(this).attr("pid")] = $(this).val();
    });
    data.purchaseId = detailId;
    console.log(data);
    var ajaxUrl = 'saveDetailListDetail';
    $.ajax({
        type: "POST",
        url: ajaxCtx + ajaxUrl,
        data: data,
        dataType: "json",
        success: function (rs) {
            console.log("saveDetailListDetail.success");
            console.log(rs);
            $('#detailListContentModal').modal('hide');
            toDetail(detailId);
            $.showToastr();
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.log("saveDetailListDetail.error");
            console.log(XMLHttpRequest);
            $.showErrorModal(XMLHttpRequest.responseText);
        },
        complete: function () {
            console.log("saveDetailListDetail.complete");
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

function getPurchasePrice() {
    console.log("getPurchasePrice()");
    var productId = $detailListContentForm.find("[pid=productId]").val();
    if(productId){
        $detailListContentForm.find("[pid=unitPrice]").val(null2zero(parent.$.cacheProducts["id" + productId].purchasePrice));
    }
}

function updatePurchasePrice(){
    console.log("updatePurchasePrice");
    var data = {};
    data.purchasePrice = $detailListContentForm.find("[pid=unitPrice]").val();
    data.id = $detailListContentForm.find("[pid=productId]").val();
    if(!data.id){
        $.showErrorModal("请先选择产品");
        return;
    }
    console.log(data);
    var ajaxUrl = 'updatePurchasePrice';
    $.ajax({
        type: "POST",
        url: ajaxCtx_product + ajaxUrl,
        data: data,
        dataType: "json",
        success: function (rs) {
            console.log("updatePurchasePrice.success");
            console.log(rs);
            $.showToastr();
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.log("updatePurchasePrice.error");
            console.log(XMLHttpRequest);
            $.showErrorModal(XMLHttpRequest.responseText);
        },
        complete: function () {
            console.log("updatePurchasePrice.complete");
        }
    });
}

function null2zero(abc){
    if(abc){
        return abc * 1;
    }
    return 0;
}