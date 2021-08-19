;
var itemId = 0;
var detailId = 0;
var $itemForm = $("#itemForm");
var $itemForm2 = $("#itemForm2");
var $detailForm = $("#detailForm");
var $fbaForm = $("#fbaForm");
var ajaxCtx = 'month/';
var parentJs = parent;
$(document).ready(function(){
    createItemForm();
    createItemForm2();
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
    // parentJs.$.refreshSkusSelect($fbaForm.find("[pid=skuId]"));

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
            $itemForm2.find("[pid]").each(function () {
                var jpa = rs.data[$(this).attr("pid")];
                $(this).val(jpa).trigger("change");
            });
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
    var theadNames = ['店铺', '月份', '采购', 'fba运费', 'fba货物', '海外仓运费', '海外仓货物','订单数','订单金额','货物成本','结余','转账'];
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
        var tds = [storeName, obj.month, obj.purchaseAmount, obj.fbaShipmentAmount, obj.fbaProductAmount, obj.overseaShipmentAmount, obj.overseaProductAmount, obj.amazonOrderQuantity, obj.amazonOrderAmount, obj.amazonOrderProductAmount, obj.amazonAmount, obj.amazonTransferAmount ];
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
    itemArray[++i] = {"label": "店铺", "pid": "storeId", "inputType": "select"};
    itemArray[++i] = {"label": "月份", "pid": "month", "inputType": "text"};
    itemArray[++i] = {"pid": "purchaseAmount", "inputType": "text"};
    itemArray[++i] = {"pid": "purchaseProductQuantity", "inputType": "text"};
    itemArray[++i] = {"pid": "purchaseCount", "inputType": "text"};
    itemArray[++i] = {"pid": "fbaShipmentAmount", "inputType": "text"};
    itemArray[++i] = {"pid": "fbaProductAmount", "inputType": "text"};
    itemArray[++i] = {"pid": "fbaProductQuantity", "inputType": "text"};
    itemArray[++i] = {"pid": "fbaCount", "inputType": "text"};
    itemArray[++i] = {"pid": "overseaWarehouseAmount", "inputType": "text"};
    itemArray[++i] = {"pid": "overseaShipmentAmount", "inputType": "text"};
    itemArray[++i] = {"pid": "overseaProductAmount", "inputType": "text"};
    itemArray[++i] = {"pid": "overseaProductQuantity", "inputType": "text"};
    itemArray[++i] = {"pid": "overseaCount", "inputType": "text"};
    itemArray[++i] = {"pid": "amazonOrderQuantity", "inputType": "text"};
    itemArray[++i] = {"pid": "amazonAmount", "inputType": "text"};
    itemArray[++i] = {"pid": "amazonTransferAmount", "inputType": "text"};
    $.drawContentForm($itemForm, itemArray);
}

function createItemForm2() {
    console.log("createItemForm2()");
    var itemArray = [];
    var i = -1;
    itemArray[++i] = {"pid": "amazonAdjustmentAmount", "inputType": "text"};
    itemArray[++i] = {"pid": "amazonAdjustmentQuantity", "inputType": "text"};
    itemArray[++i] = {"pid": "amazonFbaCustomerReturnFeeAmount", "inputType": "text"};
    itemArray[++i] = {"pid": "amazonFbaCustomerReturnFeeQuantity", "inputType": "text"};
    itemArray[++i] = {"pid": "amazonFbaInventoryFeeAmount", "inputType": "text"};
    itemArray[++i] = {"pid": "amazonFbaInventoryFeeQuantity", "inputType": "text"};
    itemArray[++i] = {"pid": "amazonFeeAdjustmentAmount", "inputType": "text"};
    itemArray[++i] = {"pid": "amazonFeeAdjustmentQuantity", "inputType": "text"};
    itemArray[++i] = {"pid": "amazonOrderAmount", "inputType": "text"};
    itemArray[++i] = {"pid": "amazonOrderQuantity", "inputType": "text"};
    itemArray[++i] = {"pid": "amazonOthersAmount", "inputType": "text"};
    itemArray[++i] = {"pid": "amazonOthersQuantity", "inputType": "text"};
    itemArray[++i] = {"pid": "amazonRefundAmount", "inputType": "text"};
    itemArray[++i] = {"pid": "amazonRefundQuantity", "inputType": "text"};
    itemArray[++i] = {"pid": "amazonRefundRetrochargeAmount", "inputType": "text"};
    itemArray[++i] = {"pid": "amazonRefundRetrochargeQuantity", "inputType": "text"};
    itemArray[++i] = {"pid": "amazonServiceFeeAmount", "inputType": "text"};
    itemArray[++i] = {"pid": "amazonServiceFeeQuantity", "inputType": "text"};
    itemArray[++i] = {"pid": "amazonAmount", "inputType": "text"};
    itemArray[++i] = {"pid": "amazonQuantity", "inputType": "text"};
    itemArray[++i] = {"pid": "amazonTransferAmount", "inputType": "text"};
    itemArray[++i] = {"pid": "amazonTransferQuantity", "inputType": "text"};
    itemArray[++i] = {"pid": "amazonOrderProductAmount", "inputType": "text"};
    itemArray[++i] = {"pid": "amazonRefundProductAmount", "inputType": "text"};
    $.drawContentForm($itemForm2, itemArray);
}

function generate(){
    console.log("generate()");
    var data = {};
    data.id = itemId;
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
            toItem(rs.data.id);
            $.showToastr();
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

//

function toNumber(x){
    if(x){
        return 1 * x;
    }
    return 0;
}