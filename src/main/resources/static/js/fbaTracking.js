;
var itemId = 0;
var detailId = 0;
var $itemForm = $("#itemForm");
var $detailForm = $("#detailForm");
var $searchForm = $("#searchForm");
var ajaxCtx = 'fbaTracking/';
var parentJs = parent;
var theadNames = ['id', '创建日期', '店铺', 'fba箱标', '货代', '渠道', '价格', '追踪号', '发货日期'];
$(document).ready(function(){
    createItemForm();
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
    parentJs.$.refreshStoresSelect($searchForm.find("[sid=storeId]"));
    parentJs.$.refreshStoresSelect($itemForm.find("[pid=storeId]"));
    //
    parentJs.$.refreshSkusSelect($detailForm.find("[pid=skuId]"));
    // parentJs.$.refreshSkusSelect($fbaForm.find("[pid=skuId]"));

    $(".datetimepicker").datetimepicker({
        todayButton: true,
        timepicker:false,
        format:'Y-m-d'
    });

});

function toItem(id){
    console.log("toItem: " + id);
    location.hash == "#itemId=" + id ? $(window).trigger('hashchange') : location.hash = "#itemId=" + id;
}

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
            createDataTable(table, {"order": 0});
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
    var multipleSelectedItemList = collectMultipleSelectedItem();
    if(multipleSelectedItemList.array.length <= 0){
        $.showErrorModal("请至少选择一条记录");
        return;
    }

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
    createDetailTable(multipleSelectedItemList);
}

function collectMultipleSelectedItem(){
    var multipleSelectedItemList = {};
    var array = [];
    $.each($("#listTable").find("tr.multipleSelectedItem"), function(index, obj){
        var $that = $(this);
        var multipleSelectedItem = {};
        multipleSelectedItem.id = $that.find("td[columnName='id']").text();
        multipleSelectedItem.storeName = $that.find("td[columnName='店铺']").text();
        multipleSelectedItem.fbaBoxLabel = $that.find("td[columnName='fba箱标']").text();
        multipleSelectedItem.dateSent = $that.find("td[columnName='发货日期']").text();
        multipleSelectedItem.shipper = $that.find("td[columnName='货代']").text();
        array.push(multipleSelectedItem);
    });
    multipleSelectedItemList.array = array;
    return multipleSelectedItemList;
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
        var storeName = parentJs.$.cacheStores["id" + obj.storeId].name;
        var tds = [obj.id, obj.dateCreate, storeName, obj.fbaBoxLabel, obj.shipper, obj.route, obj.unitPrice, obj.trackingNo, obj.dateSent];
        $.each(tds, function (index_2, obj_2) {
            obj_2 = obj_2 ? obj_2 : "";
            var td = $("<td>" + obj_2 + "</td>");
            td.attr("columnName", theadNames[index_2]);
            tr.append(td);
        })
        if(obj.shipper == "-"){
            tr.addClass("noShipper");
        }
        if(obj.trackingNo == "-"){
            tr.addClass("noTrackingNo");
        }
        tr.click(function () {
            $(this).toggleClass("multipleSelectedItem");
            // toItem(obj.id);
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
    itemArray[++i] = {"label": "货代", "pid": "shipper", "required": true, "inputType": "select"};
    itemArray[++i] = {"label": "渠道", "pid": "route", "required": true, "inputType": "select"};
    itemArray[++i] = {"label": "发货日期", "pid": "dateSent", "required": true, "inputType": "text"};
    itemArray[++i] = {"label": "价格", "pid": "unitPrice", "required": true, "inputType": "text"};
    itemArray[++i] = {"label": "追踪号", "pid": "trackingNo", "inputType": "text"};
    itemArray[++i] = {"label": "备注", "pid": "remark", "inputType": "text"};
    $.drawContentForm($itemForm, itemArray);
    parentJs.$.setSelectByParamConfig("货代", $("[pid=shipper]"));
    parentJs.$.setSelectByParamConfig("线路", $("[pid=route]"));
}

function batchCreate(){
    console.log("batchCreate()");
    var data = {};
    $searchForm.find("[sid]").each(function () {
        data[$(this).attr("sid")] = $(this).val();
    });
    console.log(data);
    var ajaxUrl = 'batchCreate';
    $.ajax({
        type: "POST",
        url: ajaxCtx + ajaxUrl,
        data: JSON.stringify(data),
        dataType: "json",
        contentType: "application/json",
        success: function (rs) {
            console.log("batchCreate.success");
            console.log(rs);
            showList();
            $.showToastr();
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.log("batchCreate.error");
            console.log(XMLHttpRequest);
            $.showErrorModal(XMLHttpRequest.responseText);
        },
        complete: function () {
            console.log("batchCreate.complete");
        }
    });
}

function batchUpdate(action){
    console.log("batchUpdate()");
    var data = {};
    $itemForm.find("[pid]").each(function () {
        data[$(this).attr("pid")] = $(this).val();
    });
    //
    data.action = action;
    var ids = [];
    $.each($("#detailTableDiv").find("tbody tr"), function(index, obj){
        var $that = $(this);
        ids.push($that.find("td:first-child").text());
    });
    data.ids = ids;
    //
    console.log(data);
    var ajaxUrl = 'batchUpdate';
    $.ajax({
        type: "POST",
        url: ajaxCtx + ajaxUrl,
        data: JSON.stringify(data),
        dataType: "json",
        contentType: "application/json",
        success: function (rs) {
            console.log("batchUpdate.success");
            console.log(rs);
            showList();
            $.showToastr();
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.log("batchUpdate.error");
            console.log(XMLHttpRequest);
            $.showErrorModal(XMLHttpRequest.responseText);
        },
        complete: function () {
            console.log("batchUpdate.complete");
        }
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
    var theadNames = ['id', '店铺', 'FBA箱标', '发货日期', '货代'];
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
    console.log(rs);
    $.each(rs.array, function (index, obj) {
        var tr = $("<tr></tr>");
        var tds = [obj.id, obj.storeName, obj.fbaBoxLabel, obj.dateSent, obj.shipper];
        $.each(tds, function (index_2, obj_2) {
            obj_2 = obj_2 ? obj_2 : "";
            var td = $("<td>" + obj_2 + "</td>");
            tr.append(td);
        })
        table.find("tbody").append(tr);
    });
}

//

function toNumber(x){
    if(x){
        return 1 * x;
    }
    return 0;
}