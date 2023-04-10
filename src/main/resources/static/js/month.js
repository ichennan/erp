;
var itemId = 0;
var detailId = 0;
var $generateForm = $("#generateForm");
var $itemForm = $("#itemForm");
var $itemForm2 = $("#itemForm2");
var $itemForm3 = $("#itemForm3");
var $detailForm = $("#detailForm");
var $fbaForm = $("#fbaForm");
var ajaxCtx = 'month/';
var parentJs = parent;
var refreshList = true;
$.category = {
    "maoli":"毛利",
    "liushui":"流水",
    "amazonOrderProductAmount":"进货价头程",
    "amazonServiceFeeAmount":"广告费",
    "amazonOrderAmount":"订单收入",
    "amazonPerOrderAmount":"平均订单收入",
    "amazonPerOrderSalesAmount":"平均订单销售额",
    "amazonOrderQuantity":"订单数",
    "amazonOrderQuantity100":"订单数*100",
    "amazonRefundQuantity":"退货数",
    "amazonProductSalesAmount":"销售额",
    "fbaProductQuantity":"FBA数量",
    "fbaShipmentAmount":"FBA运费",
    "overseaProductQuantity":"海外仓数量",
    "overseaShipmentAmount":"海外仓运费",
    "overseaWarehouseAmount":"海外仓费用",
    "purchaseQuantity":"采购数量",
    "purchaseAmount":"采购费用"
}
$.findListRs;
$(document).ready(function(){
    createItemForm();
    createItemForm2();
    createItemForm3();
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

    $(".multipleSelectedSpan").click(function(){
        var $that = $(this);
        $that.toggleClass("selected");
    })

    $(".singleSelectedSpan").click(function(){
        var $that = $(this);
        $that.siblings(".singleSelectedSpan").removeClass("selected");
        $that.addClass("selected");
    })

    $("#searchStoreId").select2({
        placeholder:'请选择',
        allowClear:true,
        width: "100%"
    })

    $("#searchStoreId").change(function () {
        var storeId = $(this).val();
        var storeName = parentJs.$.cacheStores["id" + storeId] ? parent.$.cacheStores["id" + storeId].name : "";
        $("#tableDiv").find(".theadSearch").find("th:nth-child(1)").find("input").val(storeName).trigger("change");
    })

    $("#chartBox").hide();
});

function toItem(id){
    console.log("toItem: " + id);
    location.hash == "#itemId=" + id ? $(window).trigger('hashchange') : location.hash = "#itemId=" + id;
}

function showList(){
    console.log("showList()");
    $("#searchBox").show();
    $("#tableBox").show();
    $("#itemBox").hide();
    $("#detailBox").hide();
    $('#generateModal').modal('hide');
    $('#chartModal').modal('hide');

    if(!refreshList){
        console.log("refreshList: false");
        return;
    }
    console.log("refreshList");
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
            $.findListRs = rs;
            createListTableBody(table, rs);
            createDataTable(table, {"order": 1});
            refreshList = true;
            $("#searchStoreId").trigger("change");
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
            $itemForm2.find("[pid]").each(function () {
                var jpa = rs.data[$(this).attr("pid")];
                $(this).val(jpa).trigger("change");
            });
            $itemForm3.find("[pid]").each(function () {
                var jpa = rs.data[$(this).attr("pid")];
                $(this).val(jpa).trigger("change");
            });
            refreshList = false;
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

function createListTableHead(){
    console.log("createListTableHead");
    $("#listTable").remove();
    $("#listTable_wrapper").remove();
    var listTable = $("<table class='table table-bordered data-table' id='listTable'></table>");
    var thead = $("<thead><tr></tr></thead>");
    var theadSearch = $("<thead class='theadSearch'><tr></tr></thead>");
    var theadNames = ['店铺', '月份', '采购', 'fba货物', '海外仓货物','订单数','销售额','货物成本','结余','转账','毛利','流水'];
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
        if(obj.storeId == 999){
            obj.displayPurchaseAmount = obj.purchaseAmount;
        }
        obj.displayFba = obj.fbaProductAmount + "/" + obj.fbaShipmentAmount;
        obj.displayOversea = obj.overseaProductAmount + "/" + obj.overseaShipmentAmount + "/" + obj.overseaWarehouseAmount;

        var tds = [storeName, obj.month, obj.displayPurchaseAmount, obj.displayFba, obj.displayOversea, obj.amazonOrderQuantity, obj.amazonProductSalesAmountCNY, obj.amazonOrderProductAmount, obj.amazonAmountCNY, obj.amazonTransferAmountCNY, obj.maoli, obj.liushui ];
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
    itemArray[++i] = {"label": "店铺", "pid": "storeId", "inputType": "select", "readonly": true};
    itemArray[++i] = {"label": "月份", "pid": "month", "inputType": "text", "readonly": true};
    itemArray[++i] = {"label": "FBA运费", "pid": "fbaShipmentAmount", "inputType": "text", "readonly": true};
    itemArray[++i] = {"label": "FBA产品价值", "pid": "fbaProductAmount", "inputType": "text", "readonly": true};
    itemArray[++i] = {"label": "海外仓费用", "pid": "overseaWarehouseAmount", "inputType": "text", "readonly": true};
    itemArray[++i] = {"label": "海外仓运费", "pid": "overseaShipmentAmount", "inputType": "text", "readonly": true};
    itemArray[++i] = {"label": "海外仓产品价值", "pid": "overseaProductAmount", "inputType": "text", "readonly": true};
    itemArray[++i] = {"label": "亚马逊销售额(USD)", "pid": "amazonOrderAmount", "inputType": "text", "readonly": true};
    itemArray[++i] = {"label": "亚马逊结余(USD)", "pid": "amazonAmount", "inputType": "text", "readonly": true};
    itemArray[++i] = {"label": "亚马逊转账(USD)", "pid": "amazonTransferAmount", "inputType": "text", "readonly": true};
    itemArray[++i] = {"label": "毛利", "pid": "maoli", "inputType": "text", "readonly": true};
    itemArray[++i] = {"label": "流水", "pid": "liushui", "inputType": "text", "readonly": true};
    itemArray[++i] = {"label": "汇率", "pid": "rate", "inputType": "text", "readonly": true};
    $.drawContentForm($itemForm, itemArray);
}

function createItemForm2() {
    console.log("createItemForm2()");
    var itemArray = [];
    var i = -1;
    itemArray[++i] = {"pid": "amazonAdjustmentAmount", "inputType": "text", "readonly": true};
    itemArray[++i] = {"pid": "amazonAdjustmentQuantity", "inputType": "text", "readonly": true};
    itemArray[++i] = {"pid": "amazonFbaCustomerReturnFeeAmount", "inputType": "text", "readonly": true};
    itemArray[++i] = {"pid": "amazonFbaCustomerReturnFeeQuantity", "inputType": "text", "readonly": true};
    itemArray[++i] = {"pid": "amazonFbaInventoryFeeAmount", "inputType": "text", "readonly": true};
    itemArray[++i] = {"pid": "amazonFbaInventoryFeeQuantity", "inputType": "text", "readonly": true};
    itemArray[++i] = {"pid": "amazonFeeAdjustmentAmount", "inputType": "text", "readonly": true};
    itemArray[++i] = {"pid": "amazonFeeAdjustmentQuantity", "inputType": "text", "readonly": true};
    itemArray[++i] = {"pid": "amazonOrderAmount", "inputType": "text", "readonly": true};
    itemArray[++i] = {"pid": "amazonOrderQuantity", "inputType": "text", "readonly": true};
    itemArray[++i] = {"pid": "amazonOthersAmount", "inputType": "text", "readonly": true};
    itemArray[++i] = {"pid": "amazonOthersQuantity", "inputType": "text", "readonly": true};
    itemArray[++i] = {"pid": "amazonRefundAmount", "inputType": "text", "readonly": true};
    itemArray[++i] = {"pid": "amazonRefundQuantity", "inputType": "text", "readonly": true};
    itemArray[++i] = {"pid": "amazonRefundRetrochargeAmount", "inputType": "text", "readonly": true};
    itemArray[++i] = {"pid": "amazonRefundRetrochargeQuantity", "inputType": "text", "readonly": true};
    itemArray[++i] = {"pid": "amazonServiceFeeAmount", "inputType": "text", "readonly": true};
    itemArray[++i] = {"pid": "amazonServiceFeeQuantity", "inputType": "text", "readonly": true};
    itemArray[++i] = {"pid": "amazonAmount", "inputType": "text", "readonly": true};
    itemArray[++i] = {"pid": "amazonQuantity", "inputType": "text", "readonly": true};
    itemArray[++i] = {"pid": "amazonTransferAmount", "inputType": "text", "readonly": true};
    itemArray[++i] = {"pid": "amazonTransferQuantity", "inputType": "text", "readonly": true};
    itemArray[++i] = {"pid": "amazonOrderProductAmount", "inputType": "text", "readonly": true};
    itemArray[++i] = {"pid": "amazonRefundProductAmount", "inputType": "text", "readonly": true};
    $.drawContentForm($itemForm2, itemArray);
}

function createItemForm3() {
    console.log("createItemForm3()");
    var itemArray = [];
    var i = -1;
    itemArray[++i] = {"label": "总采购费用", "pid": "purchaseAmount", "inputType": "text", "readonly": true};
    itemArray[++i] = {"label": "总采购产品数量", "pid": "purchaseProductQuantity", "inputType": "text", "readonly": true};
    itemArray[++i] = {"label": "采购次数", "pid": "purchaseCount", "inputType": "text", "readonly": true};
    itemArray[++i] = {"label": "FBA运费", "pid": "fbaShipmentAmount", "inputType": "text", "readonly": true};
    itemArray[++i] = {"label": "FBA产品价值", "pid": "fbaProductAmount", "inputType": "text", "readonly": true};
    itemArray[++i] = {"label": "FBA产品数量", "pid": "fbaProductQuantity", "inputType": "text", "readonly": true};
    itemArray[++i] = {"label": "FBA次数", "pid": "fbaCount", "inputType": "text", "readonly": true};
    itemArray[++i] = {"label": "海外仓费用", "pid": "overseaWarehouseAmount", "inputType": "text", "readonly": true};
    itemArray[++i] = {"label": "海外仓运费", "pid": "overseaShipmentAmount", "inputType": "text", "readonly": true};
    itemArray[++i] = {"label": "海外仓产品价值", "pid": "overseaProductAmount", "inputType": "text", "readonly": true};
    itemArray[++i] = {"label": "海外仓产品数量", "pid": "overseaProductQuantity", "inputType": "text", "readonly": true};
    itemArray[++i] = {"label": "海外仓次数", "pid": "overseaCount", "inputType": "text", "readonly": true};
    itemArray[++i] = {"label": "订单数量", "pid": "amazonOrderQuantity", "inputType": "text", "readonly": true};
    itemArray[++i] = {"label": "退货数量", "pid": "amazonRefundQuantity", "inputType": "text", "readonly": true};
    itemArray[++i] = {"label": "亚马逊付款", "pid": "amazonAmount", "inputType": "text", "readonly": true};
    itemArray[++i] = {"label": "亚马逊转账", "pid": "amazonTransferAmount", "inputType": "text", "readonly": true};
    $.drawContentForm($itemForm3, itemArray);
}

function generate(){
    console.log("generate()");
    var data = {};
    $generateForm.find("[pid]").each(function () {
        data[$(this).attr("pid")] = $(this).val();
    });
    var storeIds = "";
    $generateForm.find(".generateStoreSelectedSpan.selected").each(function () {
        var $that = $(this);
        storeIds = storeIds + "," +$that.attr("storeId");
    });
    data.storeIds = storeIds;
    console.log(data);
    var ajaxUrl = 'generate';
    $.ajax({
        type: "POST",
        url: ajaxCtx + ajaxUrl,
        data: data,
        dataType: "json",
        success: function (rs) {
            console.log("generate.success");
            console.log(rs);
            $.showToastr();
            refreshList = true;
            showList();
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.log("generate.error");
            console.log(XMLHttpRequest);
            $.showErrorModal(XMLHttpRequest.responseText);
        },
        complete: function () {
            console.log("generate.complete");
        }
    });
}

function autoCreate(){
    console.log("autoCreate()");
    var data = {};
    console.log(data);
    var ajaxUrl = 'autoCreate';
    $.ajax({
        type: "POST",
        url: ajaxCtx + ajaxUrl,
        data: data,
        dataType: "json",
        success: function (rs) {
            console.log("autoCreate.success");
            console.log(rs);
            $.showToastr();
            refreshList = true;
            showList();
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.log("autoCreate.error");
            console.log(XMLHttpRequest);
            $.showErrorModal(XMLHttpRequest.responseText);
        },
        complete: function () {
            console.log("autoCreate.complete");
        }
    });
}

function showGenerateModal(){
    $("#generateForm").find("[pid]").val("");
    $(".generateStoreSelectedSpan").removeClass("storeSelected");
    $('#generateModal').modal('show');
}

function showTableBox(){
    $("#tableBox").show();
    $("#chartBox").hide();
}
//

function toNumber(x){
    if(x){
        return 1 * x;
    }
    return 0;
}