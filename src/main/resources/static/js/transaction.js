;
var itemId = 0;
var detailId = 0;
var $itemForm = $("#itemForm");
var $detailForm = $("#detailForm");
var $searchForm = $("#searchForm");
var $listTable = $("#listTable");
var ajaxCtx = 'transaction/';
var parentJs = parent;
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
    parentJs.$.refreshStoresSelect($itemForm.find("[pid=storeId]"));
    //
    parentJs.$.refreshSkusSelect($detailForm.find("[pid=skuId]"));
    // parentJs.$.refreshSkusSelect($fbaForm.find("[pid=skuId]"));

    $(".datetimepicker").datetimepicker({
        todayButton: true,
        timepicker:false,
        format:'Y-m-d'
    });

    $("#search").click(function () {
        showList();
    })

    $("#searchShowMoreDiv").hide();
    $("#search_show_more").click(function () {
        $("#searchShowMoreDiv").toggle();
    })

    $(".dtPicker.dtPickerFrom").datetimepicker({
        format: 'Y-m-d H:i:s',
        defaultTime: '00:00:00'
    });

    $(".dtPicker.dtPickerTo").datetimepicker({
        format: 'Y-m-d H:i:s',
        defaultTime: '23:59:59'
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

    var queryData = searchFormCollect();
    var ajaxUrl = 'search';
    var tableColumns = [{title: 'Id', field: 'id', sortable:true
    },{title: 'Store', field: 'storeId', sortable:true
        , formatter: function(value){ return parentJs.$.retrieveStoreName(value);}
    },{title: 'Type', field: 'type', sortable:true
        , formatter: function(value){ return hideLongStr(value, 10);}
    },{title: 'Time', field: 'transactionTime', sortable:true
    },{title: 'Order', field: 'orderId', sortable:true
        , formatter: function(value){ return hideLongStr(value, 20);}
    },{title: 'SKU', field: 'sku', sortable:true
    },{title: 'Description', field: 'description', sortable:true
        , formatter: function(value){ return hideLongStr(value, 20);}
    },{title: 'Q', field: 'quantity', sortable:true
    },{title: 'Total', field: 'total', sortable:true
    }];

    $listTable.bootstrapTable("destroy");
    $listTable.bootstrapTable($.extend($.bootstrapTableOptions, {
        pageList: [10, 100, 500, 1000, 10000],
        columns: tableColumns,
        sortName: "transactionTime",
        onPostBody:function() {
            $('.search .search-input').attr('placeholder','Input Order or Sku');
        },
        url: ajaxCtx + ajaxUrl,
        onClickRow: function onClickRow(item, $element) {
            // toDetail(item.id);
            alert(item.description);
        },
        queryParams: function queryParams(params){
            return $.extend(params, queryData);
        },
        responseHandler: function responseHandler(res) {
            console.log("responseHandler");
            console.log(res);
            return res;
        }
    }));
}

function searchFormCollect(){
    console.log("searchFormCollect()");
    var data = {};
    $searchForm.find("[sid]").each(function () {
        data[$(this).attr("sid")] = $(this).val();
    });
    console.log(data);
    return data;
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

// function saveItem(action){
//     console.log("saveItem: " + action);
//     if(action != 'delete' && !validateForm()){
//         return false;
//     }
//     var data = {};
//     data.action = action;
//     $itemForm.find("[pid]").each(function () {
//         data[$(this).attr("pid")] = $(this).val();
//     });
//     console.log(data);
//     var ajaxUrl = 'saveItem';
//     $.ajax({
//         type: "POST",
//         url: ajaxCtx + ajaxUrl,
//         data: JSON.stringify(data),
//         dataType: "json",
//         contentType: "application/json",
//         success: function (rs) {
//             console.log("saveItem.success");
//             console.log(rs);
//             toItem(rs.data.id);
//             $.showToastr();
//         },
//         error: function (XMLHttpRequest, textStatus, errorThrown) {
//             console.log("saveItem.error");
//             console.log(XMLHttpRequest);
//             $.showErrorModal(XMLHttpRequest.responseText);
//         },
//         complete: function () {
//             console.log("saveItem.complete");
//         }
//     });
// }

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
        var tds = [obj.id, storeName, obj.transactionTime, obj.description, obj.deliveryNo, obj.boxCount, parentJs.$.showProductNameGroupByProductIdGroupWithQuantity(obj.productIdGroup)];
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
    var theadNames = ['', '第几箱', '数量', 'SKU', '产品识别码'];
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
        //
        var fbaIconTd = $("<td>" + "" + "</td>");
        fbaIconTd.append("<i class='fa fa-amazon'></i>");
        fbaIconTd.click(function(){
            showFba(obj);
            return false;
        })
        tr.append(fbaIconTd);
        //
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
    itemArray[++i] = {"label": "海外仓费用", "pid": "overseaAmount", "inputType": "text"};
    itemArray[++i] = {"label": "付款日期", "pid": "paymentDate", "inputType": "text"};
    itemArray[++i] = {"label": "签收日期", "pid": "signedDate", "inputType": "text"};
    itemArray[++i] = {"label": "备注", "pid": "remark", "inputType": "text"};
    $.drawContentForm($itemForm, itemArray);
}

//

function toNumber(x){
    if(x){
        return 1 * x;
    }
    return 0;
}

function hideLongStr(str, length){
    if(!str){
        return "";
    }
    if(str.length <= length){
        return str;
    }
    return str.substr(0, length) + "...";
}