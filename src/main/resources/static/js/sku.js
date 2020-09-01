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
        $("#tableDiv").find(".theadSearch").find("th:nth-child(4)").find("input").val(storeName).trigger("change");
    })

    $('input:radio[name=statusSelected]').change(function () {
        console.log("statusSelected change");
        var statusSelected = $('input:radio[name=statusSelected]:checked').val();
        if(statusSelected == "plan"){
            $("#tableDiv tbody").find("tr:not(.plan)").hide();
        }else{
            $("#tableDiv tbody").find("tr:not(.plan)").show();
        }
    });

    $("#testButton").click(function(){
        $("#tableBox").hide();
        $("#contentBox").hide();
        $("#planBox").show();
        $("#planTableDiv").empty();
        showPlanDetail();
    })

});

function showList(){
    console.log("showList()");
    $("#tableBox").show();
    $("#contentBox").hide();
    $("#planBox").hide();
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
    var theadNames = ['','SKU', '产品', '店铺', '库存', 'FBA途中', '总采购', '总FBA'];

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
                //
                var tr = $("<tr></tr>");
                tr.attr("objJson", $.jsonToString(obj));
                var planTd = $("<td class='planTd'></td>");
                var planInput = $("<input />");
                planInput.keyup(function () {
                    var $this = $(this);
                    if($this.val()){
                        tr.addClass("plan");
                    }else{
                        tr.removeClass("plan");
                    }
                })
                planTd.append(planInput);
                tr.append(planTd);
                //
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
                tr.dblclick(function () {
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
    $("#planBox").hide();
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

function showPlanDetail(){
    console.log("showPlanDetail()");
    var planDetailArray = [];
    console.log($("#tableDiv tr").length);
    console.log($("#tableDiv tr.plan").length);
    $("#tableDiv tr.plan").each(function (index, obj) {
        var $this = $(this);
        console.log($this.attr("objJson"));
        var objJson = $.stringToJson($this.attr("objJson"));
        console.log(objJson);
        objJson["planQuantity"] = $this.find("td.planTd input").val();
        planDetailArray.push(objJson);
    })
    console.log("planDetailArray");
    console.log(planDetailArray);
    //
    var listTable = $("<table class='table table-bordered data-table'></table>");
    var thead = $("<thead><tr></tr></thead>");
    var theadNames = ['数量','SKU', 'FNSKU', '产品', '店铺'];
    $.each(theadNames, function (index, obj) {
        thead.find("tr").append("<th>" + obj + "</th>");
    })
    var tbody = $("<tbody></tbody>");
    listTable.append(thead).append(tbody);
    $("#planTableDiv").append(listTable);
    $.each(planDetailArray, function(index, obj){
        var tr = $("<tr></tr>");
        tr.attr("objJson", $.jsonToString(obj));
        var snname = parent.$.cacheProducts["id" + obj.productId] ? parent.$.cacheProducts["id" + obj.productId].snname : "";
        var storeName = parent.$.cacheStores["id" + obj.storeId] ? parent.$.cacheStores["id" + obj.storeId].name : "";
        //
        var tds = [obj.planQuantity, obj.skuDesc, obj.fnsku, snname, storeName];
        $.each(tds, function (index_2, obj_2) {
            obj_2 = obj_2 ? obj_2 : "";
            var td = $("<td>" + obj_2 + "</td>");
            tr.append(td);
        })
        tbody.append(tr);
    })
}

function saveDetail(){
    console.log("saveDetail()");
    var isError = false;
    var data = {};
    $contentForm.find("[pid]").each(function () {
        data[$(this).attr("pid")] = $(this).val();
    });
    var array = [];
    $("#planTableDiv").find("tbody tr").each(function(){
        var $this = $(this);
        var obj = $.stringToJson($this.attr("objJson"));
        obj.skuId = obj.id;
        array.push(obj);
        console.log("data.storeId: " + data.storeId + " obj.storeId: " + obj.storeId);
        if(data.storeId){
            if(data.storeId != obj.storeId){
                isError = true;
            }
        }else{
            data.storeId = obj.storeId;
        }

    })
    if(isError){
        $.showErrorModal("不能同时选择两个店铺的产品");
        showList();
        return;
    }
    data.array = array;
    console.log(data);
    if(isError){
        $.showErrorModal("错误");
        showList();
        return;
    }
    var ajaxUrl = 'createPlan';
    $.ajax({
        type: "POST",
        url: ajaxCtx + ajaxUrl,
        data: JSON.stringify(data),
        dataType: "json",
        contentType: "application/json",
        success: function (rs) {
            console.log("createPlan.success");
            console.log(rs);
            $.showToastr("创建计划成功，在计划管理里查看详情");
            $.isNoRefreshList = false;
            showList();
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.log("createPlan.error");
            console.log(XMLHttpRequest);
            $.showErrorModal(XMLHttpRequest.responseText);
        },
        complete: function () {
            console.log("createPlan.complete");
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