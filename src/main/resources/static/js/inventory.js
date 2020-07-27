;
var detailId = 0;
var $contentForm = $("#contentForm");
var ajaxCtx = 'inventory/'
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
    var theadNames = ['名称', '编号','库存','FBA途中','采购途中', '总采购', '总小包','总FBA'];
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
                var tds = [obj.name, obj.sn, obj.inventoryQuantity, obj.onthewayShipmentQuantity, obj.onthewayPurchaseQuantity, obj.allPurchaseQuantity, obj.allPacketQuantity, obj.allShipmentQuantity];
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
    console.log("createPurchaseChart");
    console.log(chartData);
    $('#purchaseChart').highcharts({
        title: {
            text: '收货明细',
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
            name: '收货数量',
            data: chartData.quantityArray
        }]
    });
}

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