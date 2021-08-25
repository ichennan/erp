;
var ajaxCtx = 'month/';
var $chartForm = $("#chartForm");
var parentJs = parent;
$(document).ready(function(){
    $(".chartModeSelectedSpan").click(function(){
        $chartForm.find(".singleStore").hide();
        $chartForm.find(".multipleStore").hide();
        var $that = $(this);
        var chartMode = $that.attr("chartMode");
        $chartForm.find("." + chartMode).show();
    })
});

function drawChart(){
    console.log("drawChat()");
    var data = {};
    $chartForm.find("[pid]").each(function () {
        data[$(this).attr("pid")] = $(this).val();
    });
    data.chartMode = $chartForm.find(".chartModeSelectedSpan.selected").attr("chartMode");
    if(data.chartMode == "singleStore"){
        data.storeId = $chartForm.find(".singleStore.chartStoreSelectedSpan.selected").attr("storeId");
        var categoryList = [];
        $chartForm.find(".singleStore.chartCategorySelectedSpan.selected").each(function () {
            var $that = $(this);
            categoryList.push($that.attr("category"));
        });
        data.categoryList =  categoryList;
    }
    if(data.chartMode == "multipleStore"){
        data.category = $chartForm.find(".multipleStore.chartCategorySelectedSpan.selected").attr("category");
        var storeIdList = [];
        $chartForm.find(".multipleStore.chartStoreSelectedSpan.selected").each(function () {
            var $that = $(this);
            storeIdList.push($that.attr("storeId"));
        });
        data.storeIdList =  storeIdList;
    }
    console.log(data);
    createChart(data, $.findListRs);
}

function createChart(chartSearchData, rs) {
    console.log("createChart xyz");
    console.log(chartSearchData);
    console.log(rs);
    if(!chartSearchData.monthStart || !chartSearchData.monthEnd){
        $.showErrorModal("请输入起始月份");
        return;
    }
    var monthArray = [];
    $.each(rs.array, function (index, obj) {
        var month = obj.month;
        if (month < chartSearchData.monthStart || month > chartSearchData.monthEnd) {
            return true;
        }
        if ($.inArray(month, monthArray) < 0) {
            monthArray.push(month);
        }
    });
    monthArray = monthArray.sort();
    //
    if(chartSearchData.chartMode == "singleStore"){
        var storeId = chartSearchData.storeId;
        if(!storeId){
            $.showErrorModal("请选择店铺");
            return;
        }
        var categoryObj = {};

        $.each(chartSearchData.categoryList, function (index, obj) {
            categoryObj[obj] = [];
        });

        $.each(rs.array, function (index, obj) {
            if(obj.storeId != storeId){
                return true;
            }
            var month = obj.month;
            var monthIndex = $.inArray(month, monthArray);
            if (monthIndex < 0) {
                return true;
            }
            $.each(categoryObj, function(objKey, objValue) {
                var yValue = obj[objKey];
                if(objKey == "amazonServiceFeeAmountCNY"){
                    yValue = -1 * yValue;
                }
                if(objKey == "amazonOrderQuantity100"){
                    yValue = obj["amazonOrderQuantity"] * 100;
                }
                categoryObj[objKey][monthIndex] = yValue;
            });
        });

        var chartSeries = [];
        $.each(categoryObj, function(objKey, objValue) {
            var chartSeriesItem = {};
            chartSeriesItem.type = "spline";
            chartSeriesItem.name = $.category[objKey];
            chartSeriesItem.data = objValue;
            chartSeries.push(chartSeriesItem);
        });
        createChartFinal(parentJs.$.retrieveStoreName(storeId), monthArray, chartSeries);
    }else{
        var category = chartSearchData.category;
        var storeObj = {};
        $.each(chartSearchData.storeIdList, function (index, obj) {
            storeObj["storeId" + obj] = [];
        });
        if(!storeObj || storeObj.length == 0){
            $.showErrorModal("请选择店铺");
            return;
        }

        $.each(rs.array, function (index, obj) {
            var month = obj.month;
            var storeId = obj.storeId;
            var monthIndex = $.inArray(month, monthArray);
            if (monthIndex < 0) {
                return true;
            }
            if ($.inArray(storeId + "", chartSearchData.storeIdList) < 0) {
                return true;
            }
            var yValue = obj[category];
            if(category == "amazonServiceFeeAmountCNY"){
                yValue = -1 * yValue;
            }
            if(category == "amazonOrderQuantity100"){
                yValue = obj["amazonOrderQuantity"] * 100;
            }
            storeObj["storeId" + storeId][monthIndex] = yValue;
        });

        var chartSeries = [];
        $.each(storeObj, function(objKey, objValue) {
            var chartSeriesItem = {};
            chartSeriesItem.type = "spline";
            chartSeriesItem.name = parentJs.$.retrieveStoreName(objKey.replace("storeId",""));
            chartSeriesItem.data = objValue;
            chartSeries.push(chartSeriesItem);
        });

        createChartFinal($.category[category], monthArray, chartSeries);
    }
}

function createChartFinal(chartTile, chartMonthArray, chartSeries){
    console.log("createChartSingleStore")
    console.log(chartTile);
    console.log(chartMonthArray);
    console.log(chartSeries);
    if(!chartSeries || chartSeries.length == 0){
        $.showErrorModal("请选择数据类型");
        return;
    }
    var chartDom = $("<div class='chartDiv row'></div>");
    $("#chartDiv").prepend(chartDom);
    chartDom.highcharts({
        // chart: {margin: [10,10,10,10], shadow: true, borderWidth: 2, backgroundColor: "#666"},
        title: {text: chartTile, x: -20},
        subtitle: {text: '', x: -20},
        xAxis: {categories: chartMonthArray},
        yAxis: {title: {text: ''}},
        plotOptions: {series: {pointWidth: 1,dataLabels:{enabled: true}}},
        tooltip: {valueSuffix: ''},
        series: chartSeries
    });
}

function showChartBox(){
    $chartForm.find(".chartModeSelectedSpan.selected").trigger("click");
    $("#tableBox").hide();
    $("#chartBox").show();
}

function clearChart(){
    $("#chartDiv").empty();
}

// function createChart(){
//     console.log("createChart");
//     $('#myChart').highcharts({
//         title: {
//             text: '近45天库存明细: ',
//             x: -20 //center
//         },
//         subtitle: {
//             text: '',
//             x: -20
//         },
//         xAxis: {
//             categories: ["2021-01", "2021-02", "2021-03", "2021-04", "2021-05", "2021-06"],
//         },
//         yAxis: {
//             title: {
//                 text: ''
//             },
//         },
//         plotOptions: {
//             column: {
//                 pointWidth: 1,
//                 dataLabels:{
//                     enabled: true
//                 }
//             }
//         },
//         tooltip: {
//             valueSuffix: '个'
//         },
//         series: [{
//             type: 'spline',
//             name: '拾子店铺',
//             data: [1235,1234,222,666,7777, 3420]
//         },{
//             type: 'spline',
//             name: '老庙',
//             data: [3331,255,666,7777, 3420,1428]
//         },{
//             type: 'spline',
//             name: '燕子',
//             data: [333,2550,66,7711, 1428,1428]
//         },{
//             type: 'spline',
//             name: '聪美',
//             data: [3331,255, 3420,1428,666,7777]
//         },{
//             type: 'pie',
//             name: 'testPie',
//             center: [0, 0],
//             size: 100,
//             data: [{
//                 name: '老庙',
//                 y: 333
//             },{
//                 name: '老庙2',
//                 y: 11
//             },{
//                 name: '老庙3',
//                 y: 3233
//             },{
//                 name: '老庙4',
//                 y: 999
//             }]
//         }]
//     });
//
// }