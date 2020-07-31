;
var detailId = 0;
var $contentForm = $("#contentForm");
var ajaxCtx = 'excel/';
var excelId = 0;
var theadNames1st = ['订单号', '订单状态', '应收合计', '货品总数', '实际结算','处理时间','发货时间','物流方式','货运单号','货运单批次'];
var theadNames2nd = ['订单号', '序号', '产品', '编号', '品名','规格','数量','单价','金额','备注'];
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


    $('#file_upload_form').fileupload({
        // url: ajaxCtx + "uploadSupplierDelivery",
        url: ajaxCtx + "uploadFba",
        dataType: 'json',
        previewSourceMaxFileSize: 0,
        process: null,
        done: function (e, data) {
            console.log("fileUpload done");
            console.log(data);
            if(data && data.result){
                excelId = data.result.excelId;
            }
            showList();
        },
        add: function (e, data) {
            var goUpload = true;
            var uploadFile = data.files[0];
            if (!(/\.(xls|xlsx)$/i).test(uploadFile.name)) {
                $.showErrorModal("Only excel files (xls, xlsx) files allowed");
                goUpload = false;
            }
            if (uploadFile.size > 21000000) { // 10mb
                $.showErrorModal("Please upload a smaller excel, max size is 10 MB");
                goUpload = false;
            }
            if (goUpload == true) {
                // var uploadImageData = {};
                // uploadImageData.productId = detailId;
                // data.formData = {'uploadImageData': JSON.stringify(uploadImageData)};
                data.submit();
                console.log("excel uploaded: " + uploadFile.name);
            }
        }
    });

    $("#productImage").click(function(){
        $('#file_upload_form').trigger("click");
    });

});

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
                tdContent = $("<select iid='productId'></select>");
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

function showDetail(){
    console.log("showDetail: " + detailId);
}

function saveDetail(action){
    console.log("saveDetail: " + action);
}

function uploadToPurchase(){
    console.log("uploadToPurchase");
    if(!validateForm()){
        return false;
    }
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
        orderDetailArray.push(obj);
    })
    data.orderArray = orderArray;
    data.orderDetailArray = orderDetailArray;

    console.log(data);
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