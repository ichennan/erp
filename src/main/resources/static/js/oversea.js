;
var itemId = 0;
var detailId = 0;
var $itemForm = $("#itemForm");
var $detailForm = $("#detailForm");
var ajaxCtx = 'oversea/';
var parentJs = parent;
$(document).ready(function(){
    createItemForm();
    createDetailForm();
    $(window).on("hashchange",function () {
        var hash = location.hash ? location.hash : '';
        var hash4 = hash.substring(0, 5).toString();
        console.log('hashChange to ' + hash);
        if (hash4 == "#item") {
            //itemId=123456
            itemId = hash.substring(8);
            showItem();
        } else {
            itemId = 0;
            showList();
        }
    }).trigger("hashchange");

    //
    parentJs.$.refreshStoresSelect($itemForm.find("[pid=storeId]"));
    //
    parentJs.$.refreshSkusSelect($detailForm.find("[pid=skuId]"));

    $(".datetimepicker").datetimepicker({
        todayButton: true,
        timepicker:false,
        format:'Y-m-d'
    });

    // $("span.spanOption.searchSignedStatus").click(function () {
    //     var $this = $(this);
    //     $("span.searchSignedStatus").removeClass("selected");
    //     $this.addClass("selected");
    //     var searchSignedStatus = $this.attr("searchSignedStatus");
    //     console.log("searchSignedStatus: " + searchSignedStatus);
    //
    //     $("#listTable tbody").find("tr").each(function () {
    //         var $this = $(this);
    //         $this.show();
    //         var signedDate = $this.find("td[columnName = 签收日期]").text();
    //         console.log("signedDate: " + signedDate);
    //         if(signedDate && (searchSignedStatus == "OnTheWay")){
    //             console.log("hide1");
    //             $this.hide();
    //         }
    //         if(!signedDate && (searchSignedStatus == "Signed")){
    //             console.log("hide2");
    //             $this.hide();
    //         }
    //     })
    //
    // })
    //
    // resetSearch();
});



function toItem(id){
    console.log("toItem: " + id);
    location.hash == "#itemId=" + id ? $(window).trigger('hashchange') : location.hash = "#itemId=" + id;
}

// function resetSearch(){
//     $("[sid=dateTo]").val(moment().format("YYYY-MM-DD")).trigger("change");
//     $("span.defaultSelected").trigger("click");
//     $("[sid=productId]").val("").trigger("change");
// }

function showDetail(obj){
    $('#detailModal').modal('show');
    $detailForm.find("[pid]").each(function () {
        $(this).val("").trigger("change");
    });

    if(obj){
        $detailForm.find("[pid]").each(function () {
            var jpa = obj[$(this).attr("pid")];
            $(this).val(jpa).trigger("change");
        });
        var productId = obj["productId"];
        var storeId = obj["storeId"];
        console.log("---");
        console.log(productId);
        console.log(storeId);
        if(storeId){
            $detailForm.find("[pid=temp_store]").val(parentJs.$.cacheStores["id" + storeId].name);
        }
        if(productId){
            $detailForm.find("[pid=temp_product]").val(parentJs.$.cacheProducts["id" + productId].snname);
        }
    }
}

function showList(){
    console.log("showList()");
    $("#searchBox").show();
    $("#tableBox").show();
    $("#itemBox").hide();
    $("#detailBox").hide();
    // $("span.searchSignedStatus.defaultSelected").trigger("click");
    //
    var table = createListTableHead();
    var data = {};
    var ajaxUrl = 'findList'
    $.ajax({
        type: "POST",
        url: ajaxCtx + ajaxUrl,
        data: data,
        dataType: "json",
        success: function (rs) {
            console.log(rs);
            createListTableBody(table, rs);
            createDataTable(table, {"order": 2});
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            $.showErrorModal(XMLHttpRequest.responseText);
            console.log("error");
        },
        complete: function () {
        }
    });
}

function showItem(){
    console.log("showItem: " + itemId);
    $("#searchBox").hide();
    $("#tableBox").hide();
    $("#itemBox").show();
    $("#detailTable").remove();
    $('#detailModal').modal('hide');
    //
    $itemForm.find("[pid]").each(function () {
        $(this).val("").trigger("change");
    });
    if(itemId - 0 == 0){
        $itemForm.find("button.update").hide();
        $itemForm.find("button.create").show();
        $('.createHidden').hide();
        return;
    }
    $('.createHidden').show();
    $itemForm.find("button.update").show();
    $itemForm.find("button.create").hide();
    $("#detailBox").show();

    var data = {};
    data.id = itemId;
    var ajaxUrl = 'getItem';
    $.ajax({
        type: "POST",
        url: ajaxCtx + ajaxUrl,
        data: data,
        dataType: "json",
        success: function (rs) {
            console.log("getItem.success");
            console.log(rs);
            $itemForm.find("[pid]").each(function () {
                var jpa = rs.data[$(this).attr("pid")];
                $(this).val(jpa).trigger("change");
            });
            createDetailTable(rs);
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.log("getItem.error");
            console.log(XMLHttpRequest);
            $.showErrorModal(XMLHttpRequest.responseText);
        },
        complete: function () {
            console.log("getItem.complete");
        }
    });
}

function saveItem(action){
    console.log("saveItem: " + action);
    if(action != 'delete' && !validateForm()){
        return false;
    }
    var data = {};
    data.action = action;
    $itemForm.find("[pid]").each(function () {
        data[$(this).attr("pid")] = $(this).val();
    });
    console.log(data);
    var ajaxUrl = 'saveItem';
    $.ajax({
        type: "POST",
        url: ajaxCtx + ajaxUrl,
        data: JSON.stringify(data),
        dataType: "json",
        contentType: "application/json",
        success: function (rs) {
            console.log("saveItem.success");
            console.log(rs);
            toItem(rs.data.id);
            $.showToastr();
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.log("saveItem.error");
            console.log(XMLHttpRequest);
            $.showErrorModal(XMLHttpRequest.responseText);
        },
        complete: function () {
            console.log("saveItem.complete");
        }
    });
}

function validateForm(form) {
    var form = form ? form : $("#itemForm");
    if (form.get(0).checkValidity()){
        return true;
    }else{
        form.find("[type='submit']").trigger("click");
        return false;
    }
}

function createListTableHead(){
    console.log("createListTableHead");
    $("#listTable").remove();
    $("#listTable_wrapper").remove();
    var listTable = $("<table class='table table-bordered data-table' id='listTable'></table>");
    var thead = $("<thead><tr></tr></thead>");
    var theadSearch = $("<thead class='theadSearch'><tr></tr></thead>");
    var theadNames = ['id', '店铺', '发货日期', '海外仓', '发货编号', '箱数', '发货产品'];
    $.each(theadNames, function (index, obj) {
        thead.find("tr").append("<th>" + obj + "</th>");
        theadSearch.find("tr").append("<th><input style='width:1px'></th>");
    })
    var tbody = $("<tbody></tbody>");
    listTable.append(thead).append(theadSearch).append(tbody);
    $("#tableDiv").append(listTable);
    return listTable;
}

function createListTableBody(table, rs){
    console.log("createListTableBody");
    $.each(rs.array, function (index, obj) {
        var tr = $("<tr></tr>");
        var storeName = parentJs.$.retrieveStoreName(obj.storeId);
        var tds = [obj.id, storeName, obj.deliveryDate, obj.warehouseName, obj.deliveryNo, obj.boxCount, parentJs.$.showProductNameGroupByProductIdGroupWithQuantity(obj.productIdGroup)];
        $.each(tds, function (index_2, obj_2) {
            obj_2 = obj_2 ? obj_2 : "";
            var td = $("<td>" + obj_2 + "</td>");
            tr.append(td);
        })
        tr.click(function () {
            toItem(obj.id);
        });
        table.find("tbody").append(tr);
    });
}

function createDetailTable(rs){
    console.log("createDetailTable()");
    var table = createDetailTableHead();
    createDetailTableBody(table, rs);
}

function createDetailTableHead(){
    $("#detailTableDiv").empty();
    var detailTable = $("<table class='table table-bordered data-table' id='detailTable'></table>");
    var thead = $("<thead><tr></tr></thead>");
    var theadNames = ['第几箱', '数量', 'SKU', '产品识别码'];
    $.each(theadNames, function (index, obj) {
        thead.find("tr").append("<th>" + obj + "</th>");
    })
    var tbody = $("<tbody></tbody>");
    detailTable.append(thead).append(tbody);
    $("#detailTableDiv").append(detailTable);
    return detailTable;
}

function createDetailTableBody(table, rs){
    console.log("createDetailTableBody");
    $.each(rs.array, function (index, obj) {
        var tr = $("<tr></tr>");
        var tds = [obj.box, obj.quantity, parentJs.$.cacheSkus["id" + obj.skuId].sku, obj.productDescription];
        $.each(tds, function (index_2, obj_2) {
            obj_2 = obj_2 ? obj_2 : "";
            var td = $("<td>" + obj_2 + "</td>");
            tr.append(td);
        })
        if(obj.fbaNo){
            tr.addClass("filledFba");
        }
        tr.click(function () {
            showDetail(obj);
        });
        table.find("tbody").append(tr);
    });
}

function saveDetail(action){
    console.log("saveDetail: " + action);
    if(action != 'delete' && !validateForm($detailForm)){
        return false;
    }
    var data = {};
    data.action = action;
    $detailForm.find("[pid]").each(function () {
        data[$(this).attr("pid")] = $(this).val();
    });
    data.overseaId = itemId;
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
            $('#detailModal').modal('hide');
            toItem(itemId);
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

function createDataTable(table, opt){
    console.log("createDataTable");
    var order = (opt && opt.order) ? opt.order : 0;
    var tableId = table.attr("id");
    console.log("tableId: " + tableId);
    var datatable = table.DataTable({
        "bJQueryUI": true,
        "sPaginationType": "full_numbers",
        "ordering": true,
        "bSort": true,
        "language": $.dataTablesLanguage,
        "pageLength": 1000000,
        "order": [[ order, "desc" ]],
    });
    if(table.find('.theadSearch')){
        table.find('.theadSearch').find('input').css("width", "100%");
        $("#" + tableId + "_filter").css("display", "none");
        $("#" + tableId + "_length").css("display", "none");

        datatable.columns().eq( 0 ).each( function ( colIdx ) {
            $( 'input', datatable.column( colIdx ).header2() ).on( 'keyup change', function () {
                datatable
                    .column( colIdx )
                    .search( this.value )
                    .draw();
            } );
        } );
    }
}

function createItemForm() {
    console.log("createItemForm()");
    var itemArray = [];
    var i = -1;
    itemArray[++i] = {"label": "店铺", "pid": "storeId", "required": true, "inputType": "select"};
    itemArray[++i] = {"label": "海外仓名字", "pid": "warehouseName", "required": true, "inputType": "text"};
    itemArray[++i] = {"label": "箱数", "pid": "boxCount", "required": true, "inputType": "text"};
    itemArray[++i] = {"label": "发货日期", "pid": "deliveryDate", "required": true, "inputType": "text"};
    itemArray[++i] = {"label": "发货编号", "pid": "deliveryNo", "inputType": "text"};
    itemArray[++i] = {"label": "发货状态", "pid": "status", "required": true, "inputType": "select", "array": []};
    itemArray[i].array.push({"label": "--", "value": ""});
    itemArray[i].array.push({"label": "未发货", "value": "未发货"});
    itemArray[i].array.push({"label": "已发货", "value": "已发货"});
    itemArray[i].array.push({"label": "已签收", "value": "已签收"});
    itemArray[i].array.push({"label": "均转FBA", "value": "均转FBA"});
    itemArray[++i] = {"label": "货代", "pid": "carrier", "inputType": "text"};
    itemArray[++i] = {"label": "线路", "pid": "route", "inputType": "text"};
    itemArray[++i] = {"label": "单价", "pid": "unitPrice", "inputType": "text"};
    itemArray[++i] = {"label": "收费重量", "pid": "chargeWeight", "inputType": "text"};
    itemArray[++i] = {"label": "实付运费", "pid": "amount", "inputType": "text"};
    itemArray[++i] = {"label": "付款日期", "pid": "paymentDate", "inputType": "text"};
    itemArray[++i] = {"label": "签收日期", "pid": "signedDate", "inputType": "text"};
    itemArray[++i] = {"label": "备注", "pid": "remark", "inputType": "text"};
    $.drawContentForm($itemForm, itemArray);
}
function createDetailForm() {
    console.log("createDetailForm()");
    var itemArray = [];
    var i = -1;
    itemArray[++i] = {"label": "店铺", "pid": "temp_store", "required": true, "readonly":true, "inputType": "text"};
    itemArray[++i] = {"label": "产品", "pid": "temp_product", "required": true, "readonly":true, "inputType": "text"};
    itemArray[++i] = {"label": "SKU", "pid": "skuId", "required": true, "inputType": "select"};
    itemArray[++i] = {"label": "第几箱", "pid": "box", "required": true, "inputType": "text"};
    itemArray[++i] = {"label": "数量", "pid": "quantity", "required": true, "inputType": "text"};
    itemArray[++i] = {"label": "重量", "pid": "weight", "inputType": "text"};
    itemArray[++i] = {"label": "箱子识别码", "pid": "boxDescription", "inputType": "text"};
    itemArray[++i] = {"label": "产品识别码", "pid": "productDescription", "inputType": "text"};
    itemArray[++i] = {"label": "FBA单号", "pid": "fbaNo", "inputType": "text"};
    $.drawContentForm($detailForm, itemArray);
}


//

function toNumber(x){
    if(x){
        return 1 * x;
    }
    return 0;
}