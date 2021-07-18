;
$(document).ready(function(){
    createOverseaForm();
});

function showOversea(){
    console.log("showOversea");
    var batchArray = getBatchArray();
    if(!batchArray){
        alert("请输入箱子编号");
        return;
    }
    $("#tableBox").hide();
    $("#contentBox").hide();
    $("#overseaBox").show();
    $("#overseaTableDiv").empty();
    //
    var table = $("<table id='overseaTable' class='table table-bordered data-table'></table>");
    var thead = $("<thead><tr></tr></thead>");
    var theadNames = ['第几箱', '数量','产品识别码', '箱子识别码', '重量', '信息'];
    $.each(theadNames, function (index, obj) {
        thead.find("tr").append("<th>" + obj + "</th>");
    })
    var tbody = $("<tbody></tbody>");
    table.append(thead).append(tbody);
    var cccObjArray = [];
    $.each(batchArray, function(index, obj){
        var batchBoxArray = obj.batchQuantity.split(/\s+/);
        $.each(batchBoxArray, function (index_2, obj_2) {
            var box = obj_2 * 1;
            if(box && box != 0){
                var cccObj = $.extend({}, obj);
                cccObj.box = box;
                cccObjArray.push(cccObj);
            }
        })
    })

    cccObjArray.sort(function(a, b){
        return a.box - b.box;
    })

    console.log(cccObjArray);

    $.each(cccObjArray, function(index, obj){
        createOverseaRow(table, obj);
    })

    $("#overseaTableDiv").append(table);
}

function createOverseaRow(table, obj){
    var boxTd = $("<td tid='box'><input /></td>");
    boxTd.find("input").val(obj.box);
    var quantityTd = $("<td tid='quantity'><input /></td>");
    var productDescriptionTd = $("<td tid='productDescription'><input style='min-width: 120px' /></td>");
    var boxDescriptionTd = $("<td tid='boxDescription'><input style='min-width: 180px' /></td>");
    var weightTd = $("<td tid='weight'><input /></td>");
    var tr = $("<tr></tr>");
    tr.attr("objJson", $.jsonToString(obj));
    tr.attr("skuId", obj.skuId);
    tr.attr("productId", obj.productId);
    tr.attr("storeId", obj.storeId);
    tr.attr("sku", obj.sku);
    var snname = parent.$.cacheProducts["id" + obj.productId] ? parent.$.cacheProducts["id" + obj.productId].snname : "";
    var storeName = parent.$.cacheStores["id" + obj.storeId] ? parent.$.cacheStores["id" + obj.storeId].name : "";
    //
    var infoTd = $("<td></td>");
    infoTd.text(obj.skuDesc + " / " + obj.fnsku + " / " + snname);
    tr.append(boxTd).append(quantityTd).append(productDescriptionTd).append(boxDescriptionTd).append(weightTd).append(infoTd);
    table.find('tbody').append(tr);
}

function createOverseaForm() {
    console.log("createOverseaForm()");
    var itemArray = [];
    var i = -1;
    itemArray[++i] = {"label": "海外仓名字", "pid": "warehouseName", "required": true, "inputType": "text"};
    itemArray[++i] = {"label": "箱数", "pid": "boxCount", "required": true, "inputType": "text"};
    itemArray[++i] = {"label": "发货日期", "pid": "deliveryDate", "required": true, "inputType": "text"};
    itemArray[++i] = {"label": "发货编号", "pid": "deliveryNo", "inputType": "text"};
    itemArray[++i] = {"label": "货代", "pid": "carrier", "inputType": "text"};
    itemArray[++i] = {"label": "线路", "pid": "route", "inputType": "text"};
    itemArray[++i] = {"label": "单价", "pid": "unitPrice", "inputType": "text"};
    itemArray[++i] = {"label": "收费重量", "pid": "chargeWeight", "inputType": "text"};
    itemArray[++i] = {"label": "实付运费", "pid": "amount", "inputType": "text"};
    itemArray[++i] = {"label": "付款日期", "pid": "paymentDate", "inputType": "text"};
    itemArray[++i] = {"label": "签收日期", "pid": "signedDate", "inputType": "text"};
    itemArray[++i] = {"label": "备注", "pid": "remark", "inputType": "text"};
    itemArray[++i] = {"label": "发货状态", "pid": "status", "inputType": "select", "array": []};
    itemArray[i].array.push({"label": "--", "value": ""});
    itemArray[i].array.push({"label": "未发货", "value": "未发货"});
    itemArray[i].array.push({"label": "已发货", "value": "已发货"});
    itemArray[i].array.push({"label": "已签收", "value": "已签收"});
    itemArray[i].array.push({"label": "均转FBA", "value": "均转FBA"});
    $.drawContentForm($("#overseaForm"), itemArray);
}

function createOversea(){
    var data = {};
    var item = {};
    $("#overseaForm").find("[pid]").each(function () {
        item[$(this).attr("pid")] = $(this).val();
    });
    data.item = item;
    var array = [];
    $("#overseaTable tbody tr").each(function (index, obj) {
        var $this = $(this);
        var detailObj = {};
        detailObj.productId = $this.attr("productId");
        detailObj.skuId = $this.attr("skuId");
        detailObj.sku = $this.attr("sku");
        detailObj.storeId = $this.attr("storeId");
        detailObj.box = $this.find("td[tid=box]").find("input").val();
        detailObj.quantity = $this.find("td[tid=quantity]").find("input").val();
        detailObj.productDescription = $this.find("td[tid=productDescription]").find("input").val();
        detailObj.boxDescription = $this.find("td[tid=boxDescription]").find("input").val();
        detailObj.weight = $this.find("td[tid=weight]").find("input").val();
        array.push(detailObj);
    })
    data.array = array;
    console.log(data);
    $.ajax({
        type: "POST",
        url: "oversea/batchInsert",
        data: $.jsonToString(data),
        dataType: "json",
        contentType: 'application/json; charset=utf-8',
        success: function (rs) {
            console.log("createOversea.success");
            console.log(rs);
            $.isNoRefreshList = false;
            location.hash = "#";
            $(window).trigger('hashchange');
            $("#cleanBatch").trigger("click");
            $.showToastr();
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.log("createOversea.error");
            console.log(XMLHttpRequest);
            $.showErrorModal(XMLHttpRequest.responseText);
        },
        complete: function () {
            console.log("createOversea.complete");
        }
    });
}

function getBatchArray(){
    console.log("getBatchArray");
    var batchArray = [];
    $("#tableDiv tr.batch").each(function (index, obj) {
        var $this = $(this);
        var objJson = $.stringToJson($this.attr("objJson"));
        objJson["batchQuantity"] = $this.find("td.batchTd input").val();
        batchArray.push(objJson);
    })
    console.log(batchArray);
    return batchArray;
}