;
var detailId = 0;
var $contentForm = $("#contentForm");
var ajaxCtx = 'sku/';
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
                var tds = [obj.skuDesc, snname, storeName, obj.inventoryQuantity, obj.onthewayShipmentQuantity, obj.allPurchaseQuantity, obj.allShipmentQuantity];
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
                    toDetail(obj.skuId);
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

function createPlan() {
    $("#tableDiv").find("tr").hide();
    $("#tableDiv").find("tr.selected").show();
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
            //  inventoryChart
            var inventoryChartData = {};
            var dateArray = [];
            var inventoryArray = [];
            var allShipmentArray = [];
            var allPurchaseArray = [];
            var allPacketArray = [];
            var onthewayShipmentArray = [];
            var onthewayPurchaseArray = [];
            $.each(rs.inventoryArray, function (index, obj) {
                dateArray.push(obj.snapshotDate);
                inventoryArray.push(null2zero(obj.inventoryQuantity));
                allShipmentArray.push(null2zero(obj.allShipmentQuantity));
                allPurchaseArray.push(null2zero(obj.allPurchaseQuantity));
                allPacketArray.push(null2zero(obj.allPacketQuantity));
                onthewayShipmentArray.push(null2zero(obj.onthewayShipmentQuantity));
                onthewayPurchaseArray.push(null2zero(obj.onthewayPurchaseQuantity));
            });
            inventoryChartData.dateArray = dateArray;
            inventoryChartData.inventoryArray = inventoryArray;
            inventoryChartData.quantityArray = inventoryArray;
            inventoryChartData.allShipmentArray = allShipmentArray;
            inventoryChartData.allPurchaseArray = allPurchaseArray;
            inventoryChartData.allPacketArray = allPacketArray;
            inventoryChartData.onthewayShipmentArray = onthewayShipmentArray;
            inventoryChartData.onthewayPurchaseArray = onthewayPurchaseArray;
            createInventoryChart(inventoryChartData);
            //  shipmentChart
            var shipmentChartData = {};
            var dateArray = [];
            var quantityArray = [];
            var preDeliveryDate = undefined;
            var preShipmentId = undefined;
            $.each(rs.shipmentArray, function (index, obj) {
                var deliveryDate = obj.deliveryDate;
                var shipmentId = obj.shipmentId;
                if(deliveryDate == preDeliveryDate && shipmentId == preShipmentId){
                    var preQuantity = null2zero(quantityArray.pop());
                    quantityArray.push(null2zero(obj.quantity) + preQuantity);
                }else{
                    dateArray.push(obj.deliveryDate);
                    quantityArray.push(null2zero(obj.quantity));
                }
                preDeliveryDate = deliveryDate;
                preShipmentId = shipmentId;
            });
            shipmentChartData.dateArray = dateArray;
            shipmentChartData.quantityArray = quantityArray;
            var minusQuantityArray = [];
            $.each(quantityArray, function (index, obj) {
                minusQuantityArray.push(-1 * obj);
            })
            shipmentChartData.quantityArray = minusQuantityArray;
            //
            createShipmentChart(shipmentChartData);
            //  purchaseChart
            var purchaseChartData = {};
            var dateArray = [];
            var quantityArray = [];
            $.each(rs.purchaseArray, function (index, obj) {
                dateArray.push(obj.receivedDate);
                quantityArray.push(null2zero(obj.receivedQuantity));
            });
            purchaseChartData.dateArray = dateArray;
            purchaseChartData.quantityArray = quantityArray;
            createPurchaseChart(purchaseChartData);
            //  packetChart
            var packetChartData = {};
            var dateArray = [];
            var quantityArray = [];
            $.each(rs.packetArray, function (index, obj) {
                dateArray.push(obj.deliveryDate);
                quantityArray.push(null2zero(obj.quantity));
            });
            packetChartData.dateArray = dateArray;
            packetChartData.quantityArray = quantityArray;
            createPacketChart(packetChartData);

            var chartData = {};
            chartData.shipmentChartData = shipmentChartData;
            chartData.purchaseChartData = purchaseChartData;
            chartData.inventoryChartData = inventoryChartData;
            var snname = parent.$.cacheProducts["id" + productId] ? parent.$.cacheProducts["id" + productId].snname : "";
            createChart(snname, $.dateArray, chartData);
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

function createInventoryChart(chartData){
    console.log("createInventoryChart");
    console.log(chartData);
    $('#inventoryChart').highcharts({
        title: {
            text: '每日库存明细',
            x: -20 //center
        },
        subtitle: {
            text: '',
            x: -20
        },
        xAxis: {
            categories: chartData.dateArray
        },
        yAxis: {
            title: {
                text: '数量'
            },
            plotLines: [{
                value: 0,
                width: 1,
                color: '#808080'
            }]
        },
        tooltip: {
            valueSuffix: '个'
        },
        legend: {
            layout: 'vertical',
            align: 'right',
            verticalAlign: 'middle',
            backgroundColor:'#CCCCCC',
            borderWidth: 2
        },
        series: [{
            name: '库存',
            data: chartData.inventoryArray
        }, {
            name: '总FBA',
            data: chartData.allShipmentArray
        }, {
            name: '总采购',
            data: chartData.allPurchaseArray
        }, {
            name: '总小包',
            data: chartData.allPacketArray
        }, {
            name: 'FBA途中',
            data: chartData.onthewayShipmentArray
        }, {
            name: '采购途中',
            data: chartData.onthewayPurchaseArray
        }]
    });
}

function createShipmentChart(chartData){
    console.log("createShipmentChart");
    console.log(chartData);
    $('#shipmentChart').highcharts({
        title: {
            text: 'FBA明细',
            x: -20 //center
        },
        subtitle: {
            text: '',
            x: -20
        },
        xAxis: {
            categories: chartData.dateArray
        },
        yAxis: {
            title: {
                text: '数量'
            },
            plotLines: [{
                value: 0,
                width: 1,
                color: '#808080'
            }]
        },
        tooltip: {
            valueSuffix: '个'
        },
        legend: {
            layout: 'vertical',
            align: 'right',
            verticalAlign: 'middle',
            backgroundColor:'#CCCCCC',
            borderWidth: 2
        },
        series: [{
            name: '发货数量',
            data: chartData.quantityArray
        }]
    });
}

function createPurchaseChart(chartData){
    var today = new Date();
    var date = today;
    $.dateArray = [];
    for(var i = 1; i <= 45; i++){
        var dateString = getDateString(date);
        $.dateArray.push(dateString);
        date.setDate(date.getDate() - 1);
    }
    $.dateArray = $.dateArray.reverse();


    // $.shipmentArray = [];
    // $.shipmentArray.push(-01);
    // $.shipmentArray.push(-02);
    // $.shipmentArray.push(-03);
    // $.shipmentArray.push(-04);
    // $.shipmentArray.push(-05);
    // $.shipmentArray.push(-06);
    // $.shipmentArray.push(-07);
    // $.shipmentArray.push(-08);
    // $.shipmentArray.push(-09);
    // $.shipmentArray.push(-10);
    // $.shipmentArray.push(-11);
    // $.shipmentArray.push(-12);
    // $.shipmentArray.push(-13);
    // $.shipmentArray.push(-14);
    // $.shipmentArray.push(-15);
    // $.shipmentArray.push(-16);
    // $.shipmentArray.push(-17);
    // $.shipmentArray.push(-18);
    // $.shipmentArray.push(-19);
    // $.shipmentArray.push(-20);
    // $.shipmentArray.push(-21);
    // $.shipmentArray.push(-22);
    // $.shipmentArray.push(-23);
    // $.shipmentArray.push(-24);
    // $.shipmentArray.push(-25);
    // $.shipmentArray.push(-26);
    // $.shipmentArray.push(-27);
    // $.shipmentArray.push(-28);
    // $.shipmentArray.push(-29);
    // $.shipmentArray.push(-30);
    // $.shipmentArray.push(-31);
    // $.shipmentArray.push(-01);
    // $.shipmentArray.push(-02);
    // $.shipmentArray.push(-03);
    // $.shipmentArray.push(-04);
    // $.shipmentArray.push(-05);
    // $.shipmentArray.push(-06);
    // $.shipmentArray.push(-07);
    // $.shipmentArray.push(-08);
    // $.shipmentArray.push(-09);
    // $.shipmentArray.push(-10);
    // $.shipmentArray.push(-11);
    //
    // $.inventoryArray = [];
    // $.inventoryArray.push(1);
    // $.inventoryArray.push(12);
    // $.inventoryArray.push(3);
    // $.inventoryArray.push(24);
    // $.inventoryArray.push(15);
    // $.inventoryArray.push(36);
    // $.inventoryArray.push(17);
    // $.inventoryArray.push(18);
    // $.inventoryArray.push(9);
    // $.inventoryArray.push(10);
    // $.inventoryArray.push(21);
    // $.inventoryArray.push(12);
    // $.inventoryArray.push(33);
    // $.inventoryArray.push(14);
    // $.inventoryArray.push(25);
    // $.inventoryArray.push(-16);
    // $.inventoryArray.push(-17);
    // $.inventoryArray.push(-18);
    // $.inventoryArray.push(-19);
    // $.inventoryArray.push(-20);
    // $.inventoryArray.push(-21);
    // $.inventoryArray.push(-22);
    // $.inventoryArray.push(-23);
    // $.inventoryArray.push(-24);
    // $.inventoryArray.push(-25);
    // $.inventoryArray.push(-26);
    // $.inventoryArray.push(-27);
    // $.inventoryArray.push(-28);
    // $.inventoryArray.push(-29);
    // $.inventoryArray.push(-30);
    // $.inventoryArray.push(-31);
    // $.inventoryArray.push(-01);
    // $.inventoryArray.push(-02);
    // $.inventoryArray.push(-03);
    // $.inventoryArray.push(-04);
    // $.inventoryArray.push(-05);
    // $.inventoryArray.push(-06);
    // $.inventoryArray.push(-07);
    // $.inventoryArray.push(-08);
    // $.inventoryArray.push(-09);
    // $.inventoryArray.push(-10);
    // $.inventoryArray.push(-11);


    console.log("createPurchaseChart");
    console.log(chartData);
    // $('#purchaseChart').highcharts({
    //     chart: {
    //         type: 'column'
    //     },
    //     title: {
    //         text: '收货明细',
    //         x: -20 //center
    //     },
    //     subtitle: {
    //         text: '',
    //         x: -20
    //     },
    //     xAxis: {
    //         categories: $.dateArray,
    //     },
    //     yAxis: {
    //         title: {
    //             text: '数量'
    //         },
    //     },
    //     tooltip: {
    //         valueSuffix: '个'
    //     },
    //     series: [{
    //         type: 'column',
    //         name: '收货数量',
    //         data: setPurchaseChartData($.dateArray, chartData)
    //     },{
    //         type: 'column',
    //         name: '发货数量',
    //         data: $.shipmentArray
    //     },{
    //         type: 'spline',
    //         name: '库存数量',
    //         data: $.inventoryArray
    //     }
    //     ]
    // });
}

function createChart(snname, dateArray, chartData){
    console.log("createChart");
    console.log(chartData);
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
            data: setChartData($.dateArray, chartData.purchaseChartData)
        },{
            type: 'column',
            name: '发货',
            data: setChartData($.dateArray, chartData.shipmentChartData)
        },{
            type: 'spline',
            name: '库存',
            data: setChartData($.dateArray, chartData.inventoryChartData)
        }]
    });

}

function setChartData(dateArray, chartData){
    console.log("dateArray");
    console.log(dateArray);
    console.log("chartData");
    console.log(chartData);
    var dataArray = [];
    $.each(dateArray, function (index, obj) {
        var targetDate = obj;
        var targetValue = null;
        $.each(chartData.dateArray, function(index_2, obj_2){
            if(obj_2 == targetDate){
                targetValue = chartData.quantityArray[index_2];
            }
        })
        dataArray.push(targetValue);
    })
    return dataArray;
}

// function setShipmentChartData(dateArray, chartData){
//     var dataArray = [];
//     $.each(dateArray, function (index, obj) {
//         var targetDate = obj;
//         var targetValue = null;
//         $.each(chartData.dateArray, function(index_2, obj_2){
//             if(obj_2 == targetDate){
//                 targetValue = chartData.quantityArray[index_2];
//             }
//         })
//         dataArray.push(targetValue);
//     })
//     return dataArray;
// }

function createPacketChart(chartData){
    console.log("createPacketChart");
    console.log(chartData);
    $('#packetChart').highcharts({
        title: {
            text: '小包明细',
            x: -20 //center
        },
        subtitle: {
            text: '',
            x: -20
        },
        xAxis: {
            categories: chartData.dateArray
        },
        yAxis: {
            title: {
                text: '数量'
            },
            plotLines: [{
                value: 0,
                width: 1,
                color: '#808080'
            }]
        },
        tooltip: {
            valueSuffix: '个'
        },
        legend: {
            layout: 'vertical',
            align: 'right',
            verticalAlign: 'middle',
            backgroundColor:'#CCCCCC',
            borderWidth: 2
        },
        series: [{
            name: '发货数量',
            data: chartData.quantityArray
        }]
    });
}

function null2zero(abc){
    if(abc){
        return abc * 1;
    }
    return 0;
}


// date function

function getDateString(date) {
    var month = date.getMonth() + 1;
    var day = date.getDate();
    return date.getFullYear() + (month < 10 ? "0" : "") + month + (day < 10 ? "0" : "") + day;
}
