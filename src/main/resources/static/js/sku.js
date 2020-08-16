;
var detailId = 0;
var $contentForm = $("#contentForm");
var ajaxCtx = 'sku/';
$.isNoRefreshList = false;
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

    $("#fbaStoreId").change(function () {
        $("#tableDiv").find("tr").removeClass("selected");
        var storeId = $(this).val();
        var storeName = parent.$.cacheStores["id" + storeId] ? parent.$.cacheStores["id" + storeId].name : "";
        $("#tableDiv").find(".theadSearch").find("th:nth-child(3)").find("input").val(storeName).trigger("change");
    })
});

function showList(){
    console.log("showList()");
    $("#tableBox").show();
    $("#contentBox").hide();
    if($.isNoRefreshList){
        console.log("NoRefreshList");
        return;
    }
    console.log("RefreshList");
    //
    $("#listTable").remove();
    $("#listTable_wrapper").remove();
    var listTable = $("<table class='table table-bordered data-table' id='listTable'></table>");
    var thead = $("<thead><tr></tr></thead>");
    var theadSearch = $("<thead class='theadSearch'><tr></tr></thead>");
    var theadNames = ['SKU', '产品', '店铺', '库存', 'FBA途中', '总采购', '总FBA'];

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
                var snname = parent.$.cacheProducts["id" + obj.productId] ? parent.$.cacheProducts["id" + obj.productId].snname : "";
                var storeName = parent.$.cacheStores["id" + obj.storeId] ? parent.$.cacheStores["id" + obj.storeId].name : "";

                var tr = $("<tr></tr>");
                var tds = [obj.skuDesc, snname, storeName, obj.productInventoryQuantity, obj.sumSkuShipmentOnthewayQuantity, obj.sumProductPurchaseQuantity, obj.sumSkuShipmentQuantity];
                $.each(tds, function (index_2, obj_2) {
                    obj_2 = obj_2 ? obj_2 : "";
                    var td = $("<td>" + obj_2 + "</td>");
                    if(index_2 == 3){
                        td.addClass("inventoryQuantity");
                        if(!obj_2){
                            td.text(0);
                            td.addClass("inventoryLack");
                        }else if(obj_2 < 10){
                            td.addClass("inventoryLack");
                        }
                    }
                    tr.append(td);
                })
                tr.click(function () {
                    // $(this).toggleClass("selected");
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
                "pageLength": 1000,
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
            $.isNoRefreshList = true;
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

function validateForm(contentForm) {
    var contentForm = contentForm ? contentForm : $("#contentForm");
    if (contentForm.get(0).checkValidity()){
        return true;
    }else{
        contentForm.find("[type='submit']").trigger("click");
        return false;
    }
}

function showDetail(){
    console.log("showDetail: " + detailId);
    $("#tableBox").hide();
    $("#contentBox").show();
    $("#contentBox div.chartDiv").empty();
    //

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
            var productId = rs.productId;
            var skuId = rs.skuId;
            var snname = parent.$.cacheProducts["id" + productId] ? parent.$.cacheProducts["id" + productId].snname : "";
            createChart(skuId, snname, rs);
            //

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

function setChartDataX(){
    console.log("setChartDataX");
    var today = new Date();
    var date = today;
    $.dateArray = [];
    for(var i = 1; i <= 45; i++){
        var dateString = getDateString(date);
        $.dateArray.push(dateString);
        date.setDate(date.getDate() - 1);
    }
    $.dateArray = $.dateArray.reverse();
    console.log($.dateArray);
}

function createChart(skuId, snname, rs){
    console.log("createChart");
    setChartDataX();
    $('#myChart').highcharts({
        title: {
            text: '近45天库存明细: ' + snname,
            x: -20 //center
        },
        subtitle: {
            text: '',
            x: -20
        },
        xAxis: {
            categories: $.dateArray,
        },
        yAxis: {
            title: {
                text: ''
            },
        },
        plotOptions: {
            column: {
                pointWidth: 1,
                dataLabels:{
                    enabled: true
                }
            }
        },
        tooltip: {
            valueSuffix: '个'
        },
        series: [{
            type: 'column',
            name: '收货',
            color: 'green',
            data: setChartDataY(rs.productPurchaseJson)
        },{
            type: 'column',
            name: '发货',
            color: 'red',
            data: setChartDataY(rs.skuShipmentJson)
        },{
            type: 'column',
            name: '其它sku发货',
            color: '#eeeeee',
            data: setChartDataY(rs.skuElseShipmentJson)
        },{
            type: 'spline',
            name: '库存',
            color: 'black',
            data: setChartDataY(rs.productInventoryJson)
        }]
    });

}

function setChartDataY(dataJson){
    console.log("setChartDataY");
    console.log(dataJson);
    var dataArray = [];
    $.each($.dateArray, function (index, obj) {
        dataArray.push(dataJson[obj] ? dataJson[obj] : "");
    })
    console.log("dataArray");
    console.log(dataArray);
    return dataArray;
}

// date function

function getDateString(date) {
    var month = date.getMonth() + 1;
    var day = date.getDate();
    return date.getFullYear() + (month < 10 ? "0" : "") + month + (day < 10 ? "0" : "") + day;
}
