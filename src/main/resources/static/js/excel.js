;
var detailId = 0;
var $contentForm = $("#contentForm");
var ajaxCtx = 'excel/';
var excelId = 0;
var theadNames1st = ['订单号', '订单状态', '应收合计', '货品总数', '实际结算','处理时间','发货时间','物流方式','货运单号','货运单批次'];
var theadNames2nd = ['订单号', '序号', '产品', '编号', '品名','规格','数量','单价','金额','备注'];
var theadNames3rd = ['店铺', 'sku', '产品', 'skuId', 'qty'];
var theadNamesTransaction = ['Type', 'Count', 'Total'];
var theadNamesCarrierBill = ['单号', '日期', 'FBA No.', '总费用', '线路', '单价', '重量', '运单号', '转单号'];
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

    $('#fileCategorySelect').change(function () {
        $("#uploading_div").hide();
        $("table").remove();
        $this = $(this);
        console.log("fileCategorySelect change");
        var fileCategory = $this.val();
        $("[fileCategory]").hide();
        if(fileCategory){
            if("fbatsv" == fileCategory){
                fileCategory = "fba";
            }
            $("[fileCategory=" + fileCategory + "]").show();
        }
    }).trigger("change");

    $('#file_upload_form').fileupload({
        url: ajaxCtx + "upload",
        dataType: 'json',
        previewSourceMaxFileSize: 0,
        process: null,
        fail: function (e, data) {
            $("#uploading_div").hide();
            console.log("upload errrrrrr:");
            console.log(e);
            console.log(data);
        },
        done: function (e, data) {
            console.log("fileUpload done");
            $("#uploading_div").hide();
            console.log(data);
            var fileCategory;
            if(data && data.result){
                excelId = data.result.excelId;
                fileCategory = data.result.fileCategory;
            }
            switch (fileCategory) {
                case "fba":
                    showFbaList();
                    break;
                case "fbatsv":
                    showFbaList();
                    break;
                case "supplierDelivery":
                    showList();
                    break;
                case "MonthlyUnifiedTransaction":
                    showTransactionList();
                    break;
                case "CarrierBillCainiao":
                    showCarrierBillList();
                    break;
            }
        },
        add: function (e, data) {
            var goUpload = true;
            var uploadFile = data.files[0];
            var fileCategory = $('#fileCategorySelect').val();
            if(!fileCategory){
                $.showErrorModal("请选择上传文件类型");
                goUpload = false;
                data.fileCategory = uploadFileData;
            }

            if (!(/\.(xls|xlsx|tsv|csv)$/i).test(uploadFile.name)) {
                $.showErrorModal("Only excel files (xls, xlsx) files allowed");
                goUpload = false;
            }
            if (uploadFile.size > 21000000) { // 10mb
                $.showErrorModal("Please upload a smaller excel, max size is 10 MB");
                goUpload = false;
            }
            if (goUpload == true) {
                var uploadFileData = {};
                uploadFileData.fileCategory = fileCategory;
                data.formData = {'uploadFileData': JSON.stringify(uploadFileData)};
                data.submit();
                console.log("excel uploaded: " + uploadFile.name);
            }
            $("#uploading_div").show();
        }
    });

});

function showFbaList(){
    console.log("showFbaList()");
    $("#tableBox").show();
    $("#contentBox").show();
    //
    var table3rd = createTable3rd($("#tableDiv3rd"));
    var data = {};
    data.excelId = excelId;
    var ajaxUrl = 'findFbaByExcelId'
    $.ajax({
        type: "POST",
        url: ajaxCtx + ajaxUrl,
        data: data,
        dataType: "json",
        success: function (rs) {
            console.log(rs);
            $contentForm.find("[pid]").each(function () {
                var jpa = rs.data[$(this).attr("pid")];
                $(this).val(jpa).trigger("change");
            });
            $contentForm.find("[pid=storeName]").val(parent.$.retrieveStoreName(rs.data["storeId"]));
            drawTable3rd(table3rd, rs.array, rs.data.boxCount);
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            $.showErrorModal(XMLHttpRequest.responseText);
            console.log("error");
        },
        complete: function () {
        }
    });
}

function showCarrierBillList(){
    console.log("showCarrierBillList()");
    $("#tableBox").show();
    $("#contentBox").show();
    //
    var tableCarrierBill = createTableCarrierBill($("#tableDivCarrierBill"));
    var data = {};
    data.excelId = excelId;
    var ajaxUrl = 'findCarrierBillByExcelId'
    $.ajax({
        type: "POST",
        url: ajaxCtx + ajaxUrl,
        data: data,
        dataType: "json",
        success: function (rs) {
            console.log(rs);
            $contentForm.find("[pid]").each(function () {
                var jpa = rs.data[$(this).attr("pid")];
                $(this).val(jpa).trigger("change");
            });
            drawTableCarrierBill(tableCarrierBill, rs.array);
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            $.showErrorModal(XMLHttpRequest.responseText);
            console.log("error");
        },
        complete: function () {
        }
    });
}

function resetAll(){
    $("#fileCategorySelect").val("").trigger("change");
}

function showTransactionList(){
    console.log("showTransactionList()");
    $("#tableBox").show();
    $("#contentBox").hide();
    //
    var tableTransaction = createTableTransaction($("#tableDivTransaction"));
    var data = {};
    data.excelId = excelId;
    var ajaxUrl = 'findTransactionByExcelId'
    $.ajax({
        type: "POST",
        url: ajaxCtx + ajaxUrl,
        data: data,
        dataType: "json",
        success: function (rs) {
            console.log(rs);
            drawTableTransaction(tableTransaction, rs);
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            $.showErrorModal(XMLHttpRequest.responseText);
            console.log("error");
        },
        complete: function () {
        }
    });
}

function showList(){
    console.log("showList()");
    $("#tableBox").show();
    $("#contentBox").hide();
    //
    var table1st = createTable1st($("#tableDiv1st"));
    var table2nd = createTable2nd($("#tableDiv2nd"));
    var data = {};
    data.excelId = excelId;
    var ajaxUrl = 'findByExcelId'
    $.ajax({
        type: "POST",
        url: ajaxCtx + ajaxUrl,
        data: data,
        dataType: "json",
        success: function (rs) {
            console.log(rs);
            drawTable1st(table1st, rs.orderArray);
            drawTable2nd(table2nd, rs.orderDetailArray);
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            $.showErrorModal(XMLHttpRequest.responseText);
            console.log("error");
        },
        complete: function () {
        }
    });
}

function createTableTransaction(tableDiv){
    console.log("createTableTransaction()");
    $("#listTableTransaction").remove();
    $("#listTableTransaction_wrapper").remove();
    var listTable = $("<table class='table table-bordered data-table' id='listTableTransaction'></table>");
    var thead = $("<thead><tr></tr></thead>");
    $.each(theadNamesTransaction, function (index, obj) {
        thead.find("tr").append("<th>" + obj + "</th>");
    })
    var tbody = $("<tbody></tbody>");
    listTable.append(thead).append(tbody);
    tableDiv.append(listTable);
    return listTable;
}

function createTableCarrierBill(tableDiv){
    console.log("createTableCarrierBill()");
    $("#listTableCarrierBill").remove();
    $("#listTableCarrierBill_wrapper").remove();
    var listTable = $("<table class='table table-bordered data-table' id='listTableCarrierBill'></table>");
    var thead = $("<thead><tr></tr></thead>");
    $.each(theadNamesCarrierBill, function (index, obj) {
        thead.find("tr").append("<th>" + obj + "</th>");
    })
    var tbody = $("<tbody></tbody>");
    listTable.append(thead).append(tbody);
    tableDiv.append(listTable);
    return listTable;
}

function createTable1st(tableDiv){
    console.log("createTable1st()");
    $("#listTable1st").remove();
    $("#listTable1st_wrapper").remove();
    var listTable = $("<table class='table table-bordered data-table' id='listTable1st'></table>");
    var thead = $("<thead><tr></tr></thead>");
    $.each(theadNames1st, function (index, obj) {
        thead.find("tr").append("<th>" + obj + "</th>");
    })
    var tbody = $("<tbody></tbody>");
    listTable.append(thead).append(tbody);
    tableDiv.append(listTable);
    return listTable;
}

function createTable2nd(tableDiv){
    console.log("createTable2nd()");
    $("#listTable2nd").remove();
    $("#listTable2nd_wrapper").remove();
    var listTable = $("<table class='table table-bordered data-table' id='listTable2nd'></table>");
    var thead = $("<thead><tr></tr></thead>");
    $.each(theadNames2nd, function (index, obj) {
        thead.find("tr").append("<th>" + obj + "</th>");
    })
    var tbody = $("<tbody></tbody>");
    listTable.append(thead).append(tbody);
    tableDiv.append(listTable);
    return listTable;
}

function createTable3rd(tableDiv){
    console.log("createTable3rd()");
    $("#listTable3rd").remove();
    $("#listTable3rd_wrapper").remove();
    var listTable = $("<table class='table table-bordered data-table' id='listTable3rd'></table>");
    var thead = $("<thead><tr></tr></thead>");
    $.each(theadNames3rd, function (index, obj) {
        thead.find("tr").append("<th>" + obj + "</th>");
    })
    var tbody = $("<tbody></tbody>");
    listTable.append(thead).append(tbody);
    tableDiv.append(listTable);
    return listTable;
}

function drawTableTransaction(table, rs){
    var array = rs.array;
    var arraySum = rs.arraySum;
    console.log("drawTableTransaction()");
    var tbody = table.find("tbody");
    $.each(array, function (index, obj) {
        var tr = $("<tr></tr>");
        tr.attr("objJson", $.jsonToString(obj));
        var tds = [obj.type, obj.count, obj.total];
        $.each(tds, function (index_2, obj_2) {
            obj_2 = obj_2 ? obj_2 : "";
            var tdContent = $("<span></span>");
            tdContent.text(obj_2);
            var td = $("<td></td>");
            td.attr("column", theadNames2nd[index_2]);
            td.append(tdContent);
            tr.append(td);
        })
        tbody.append(tr);
    });
    //
    $.each(arraySum, function (index, obj) {
        var tr = $("<tr></tr>");
        tr.attr("objJson", $.jsonToString(obj));
        tr.addClass("sum");
        var tds = [obj.type, obj.count, obj.total];
        $.each(tds, function (index_2, obj_2) {
            obj_2 = obj_2 ? obj_2 : "";
            var tdContent = $("<span></span>");
            tdContent.text(obj_2);
            var td = $("<td></td>");
            td.attr("column", theadNames2nd[index_2]);
            td.append(tdContent);
            tr.append(td);
        })
        tbody.append(tr);
    });
}

function drawTableCarrierBill(table, array){
    console.log("drawTableCarrierBill()");
    console.log(array);
    var tbody = table.find("tbody");
    $.each(array, function (index, obj) {
        var tr = $("<tr></tr>");
        tr.attr("objJson", $.jsonToString(obj));
        if(obj.relatedShipmentId){
            obj.related = "匹配FBA";
            tr.addClass("shipmentRow");
        }else if(obj.relatedOverseaId){
            obj.related = "匹配海外仓";
            tr.addClass("overseaRow");
        }else {
            obj.related = "无法匹配";
            tr.addClass("errorRow");
        }
        var tds = [obj.billNo, obj.billCreateDate, obj.fbaNo, obj.amount, obj.route, obj.unitPrice, obj.chargeWeight, obj.trackingNumber, obj.transferTrackingNumber1];
        $.each(tds, function (index_2, obj_2) {
            obj_2 = obj_2 ? obj_2 : "";
            var tdContent = $("<span></span>");
            tdContent.text(obj_2);
            var td = $("<td></td>");
            td.attr("column", theadNamesCarrierBill[index_2]);
            td.append(tdContent);
            tr.append(td);
        })
        tbody.append(tr);
    });
}

function drawTable1st(table, array){
    console.log("drawTable1st()");
    var tbody = table.find("tbody");
    $.each(array, function (index, obj) {
        var tr = $("<tr></tr>");
        tr.attr("objJson", $.jsonToString(obj));
        var tds = [obj.dingdanhao, obj.dingdanzhuangtai, obj.yingshouheji, obj.huopinzongshu, obj.shijijiesuan, obj.chulishijian, obj.fahuoshijian, obj.wuliufangshi, obj.huoyundanhao, obj.huoyundanpici];
        $.each(tds, function (index_2, obj_2) {
            obj_2 = obj_2 ? obj_2 : "";
            var tdContent = $("<span></span>");
            tdContent.text(obj_2);
            var td = $("<td></td>");
            td.attr("column", theadNames2nd[index_2]);
            td.append(tdContent);
            tr.append(td);
        })
        tbody.append(tr);
    });
}

function drawTable2nd(table, array){
    console.log("drawTable2nd()");
    var tbody = table.find("tbody");
    $.each(array, function (index, obj) {
        var tr = $("<tr></tr>");
        tr.attr("objJson", $.jsonToString(obj));
        var tds = [obj.dingdanhao, obj.xu, '', obj.bianhao, obj.pinming, obj.guige, obj.shuliang, obj.danjia, obj.jine, obj.beizhu];
        $.each(tds, function (index_2, obj_2) {
            obj_2 = obj_2 ? obj_2 : "";
            var tdContent = $("<span></span>");
            tdContent.text(obj_2);
            if(index_2 == 2){
                tdContent = $("<select iid='productId' required class='required'></select>");
            }
            var td = $("<td></td>");
            td.attr("column", theadNames2nd[index_2]);
            td.append(tdContent);
            tr.append(td);
        })
        tbody.append(tr);
    });
    parent.$.refreshProductsSelect(tbody.find("[iid=productId]"));
    tbody.find("[iid=productId]").val("").trigger("change");
    $(".select2-container").click(function(){
        var $thisTd = $(this).parent("td");
        var bianhaoTd = $thisTd.siblings("[column=编号]");
        var bianhao = bianhaoTd.find("span").text().split("-")[0];
        $("input.select2-search__field").val(bianhao).keydown().keypress().keyup();
    });
}

function drawTable3rd(table, array, boxCount){
    console.log("drawTable3rd()");
    var tbody = table.find("tbody");
    var thead = table.find("thead");
    for(var i = 1; i <= boxCount; i++){
        var td = $("<td>" + i + "</td>");
        thead.find('tr').append(td);
    }

    var previousSku;
    $.each(array, function (index, obj) {
        var tr = $("<tr></tr>");
        var sku = obj.merchantSku;
        console.log("sku: " + sku);
        console.log("previousSku: " + previousSku);
        if(sku == previousSku){
            tr.addClass("combineSku");
        }
        previousSku = sku;
        tr.attr("objJson", $.jsonToString(obj));
        // var tds = [obj.merchantSku, obj.asin, obj.productId, obj.fnsku, obj.boxedQty];
        var snname = "";
        if(parent.$.cacheProducts["id" + obj.productId]){
            snname = parent.$.cacheProducts["id" + obj.productId].snname;
        }else{
            tr.addClass("errorRow");
        }
        var storeDesc = parent.$.cacheStores["id" + obj.storeId] ? parent.$.cacheStores["id" + obj.storeId].name + " " : "";
        var tds = [storeDesc, obj.merchantSku, snname, obj.skuId, obj.boxedQty];
        for(var i = 1; i <= boxCount; i++){
            var iString = i < 10 ? "0" + i : "" + i;
            var boxQty = "box" + iString + "Qty";
            tds.push(obj[boxQty]);
        }
        console.log("tds");
        console.log(tds);
        // var productId = "";
        $.each(tds, function (index_2, obj_2) {
            obj_2 = obj_2 ? obj_2 : "";
            var tdContent = $("<span></span>");
            tdContent.text(obj_2);
            // if(index_2 == 2){
            //     var tdContent = $("<select iid='productId'></select>");
            //     productId = obj_2;
            // }
            var td = $("<td></td>");
            td.attr("column", theadNames2nd[index_2]);
            td.append(tdContent);
            tr.append(td);
        })
        // parent.$.refreshProductsSelect(tr.find("select"));
        // tr.find("select").val(productId).trigger("change");
        tbody.append(tr);
    });
}

function showDetail(){
    console.log("showDetail: " + detailId);
}

function saveDetail(action){
    console.log("saveDetail: " + action);
}

function upload(){
    var fileCategory = $("#fileCategorySelect").val();
    console.log("upload: " + fileCategory);
    switch (fileCategory) {
        case "fba":
            uploadToShipment();
            break;
        case "fbatsv":
            uploadToShipment();
            break;
        case "supplierDelivery":
            uploadToPurchase();
            break;
        case "MonthlyUnifiedTransaction":
            uploadToTransaction();
            break;
        case "CarrierBillCainiao":
            uploadToCarrierBill();
            break;
        default:
            $("#uploading_div").hide();
            alert("请选择正确文件类型");
            return;
            break;
    }
}

function uploadToPurchase(){
    console.log("uploadToPurchase");
    var isError = false;
    var data = {};
    var orderArray = [];
    var orderDetailArray = [];
    $("#tableDiv1st").find("tbody tr").each(function(){
        var $this = $(this);
        var obj = $.stringToJson($this.attr("objJson"));
        orderArray.push(obj);
    })
    $("#tableDiv2nd").find("tbody tr").each(function(){
        var $this = $(this);
        var obj = $.stringToJson($this.attr("objJson"));
        obj.productId = $this.find("[iid=productId]").val();
        if(!obj.productId){
            isError = true;
            return false;
        }
        orderDetailArray.push(obj);
    })
    data.orderArray = orderArray;
    data.orderDetailArray = orderDetailArray;
    console.log(data);
    if(isError){
        $.showErrorModal("请确认产品后上传");
        return;
    }
    var ajaxUrl = 'uploadToPurchase';
    $.ajax({
        type: "POST",
        url: ajaxCtx + ajaxUrl,
        data: JSON.stringify(data),
        dataType: "json",
        contentType: "application/json",
        success: function (rs) {
            console.log("uploadToPurchase.success");
            console.log(rs);
            $.showToastr();
            resetAll();
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.log("uploadToPurchase.error");
            console.log(XMLHttpRequest);
            $.showErrorModal(XMLHttpRequest.responseText);
        },
        complete: function () {
            console.log("uploadToPurchase.complete");
        }
    });
}

function uploadToShipment(){
    console.log("uploadToShipment");
    var isError = false;
    var data = {};
    $contentForm.find("[pid]").each(function () {
        data[$(this).attr("pid")] = $(this).val();
    });
    var array = [];
    $("#tableDiv3rd").find("tbody tr").each(function(){
        var $this = $(this);
        var obj = $.stringToJson($this.attr("objJson"));
        if(!obj.productId){
            isError = true;
            return false;
        }
        array.push(obj);
    })
    data.array = array;
    console.log(data);
    if(isError){
        $.showErrorModal("请完善产品信息后重新上传");
        return;
    }
    var ajaxUrl = 'uploadToShipment';
    $.ajax({
        type: "POST",
        url: ajaxCtx + ajaxUrl,
        data: JSON.stringify(data),
        dataType: "json",
        contentType: "application/json",
        success: function (rs) {
            console.log("uploadToShipment.success");
            console.log(rs);
            $.showToastr();
            resetAll();
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.log("uploadToShipment.error");
            console.log(XMLHttpRequest);
            $.showErrorModal(XMLHttpRequest.responseText);
        },
        complete: function () {
            console.log("uploadToShipment.complete");
        }
    });
}

function uploadToTransaction(){
    console.log("uploadToTransaction");
    var isError = false;
    var data = {};
    data.excelId = excelId;
    console.log(data);
    if(isError){
        $.showErrorModal("Error");
        return;
    }
    var ajaxUrl = 'uploadToTransaction';
    $.ajax({
        type: "POST",
        url: ajaxCtx + ajaxUrl,
        data: data,
        dataType: "json",
        // contentType: "application/json",
        success: function (rs) {
            console.log("uploadToTransaction.success");
            console.log(rs);
            $.showToastr();
            resetAll();
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.log("uploadToTransaction.error");
            console.log(XMLHttpRequest);
            $.showErrorModal(XMLHttpRequest.responseText);
        },
        complete: function () {
            console.log("uploadToTransaction.complete");
        }
    });
}

function uploadToCarrierBill(){
    console.log("uploadToCarrierBill()");
    var isError = false;
    var data = {};
    data.excelId = excelId;
    console.log(data);
    if($("#tableDivCarrierBill").find("tbody tr.errorRow").length){
        $.showErrorModal("请在Excel处理了无法匹配的数据(红色)后再上传");
        return;
    }
    if(isError){
        $.showErrorModal("Error");
        return;
    }
    var ajaxUrl = 'uploadToCarrierBill';
    $.ajax({
        type: "POST",
        url: ajaxCtx + ajaxUrl,
        data: data,
        dataType: "json",
        // contentType: "application/json",
        success: function (rs) {
            console.log("uploadToCarrierBill.success");
            console.log(rs);
            $.showToastr();
            resetAll();
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.log("uploadToCarrierBill.error");
            console.log(XMLHttpRequest);
            $.showErrorModal(XMLHttpRequest.responseText);
        },
        complete: function () {
            console.log("uploadToCarrierBill.complete");
        }
    });
}

function validateForm(contentForm) {
    return true;
    // var contentForm = contentForm ? contentForm : $("#contentForm");
    // if (contentForm.get(0).checkValidity()){
    //     return true;
    // }else{
    //     contentForm.find("[type='submit']").trigger("click");
    //     return false;
    // }
}