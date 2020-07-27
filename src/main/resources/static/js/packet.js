;
var detailId = 0;
var $contentForm = $("#contentForm");
var ajaxCtx = 'packet/';
$.cacheEnablePacketProducts = {};
$(document).ready(function(){
    $.refreshCacheEnablePacketProducts();
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
    $("#detailListBox").hide();
    //
    $("#listTable").remove();
    $("#listTable_wrapper").remove();
    var listTable = $("<table class='table table-bordered data-table' id='listTable'></table>");
    var thead = $("<thead><tr></tr></thead>");
    var theadSearch = $("<thead class='theadSearch'><tr></tr></thead>");
    var theadNames = ['发货日期'];
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
                var tds = [obj.deliveryDate];
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
    $contentForm.find(".packetProductDiv").remove();
    //
    $contentForm.find("[pid]").each(function () {
        $(this).val("").trigger("change");
    });
    for(key in $.cacheEnablePacketProducts){
        var productId = key.substr(2);
        var productName = $.cacheEnablePacketProducts[key];
        //
        var formGroupDiv = $('<div class="form-group packetProductDiv">');
        var label = $('<label class="col-xs-3 control-label">' + productName + '<span class="required hide">*</span></label>');
        var inputDiv = $('<div class="col-xs-9">');
        var input = $('<input class="form-control required" productId="' + productId + '"/>');
        input.val(0);
        inputDiv.append(input);
        formGroupDiv.append(label).append(inputDiv);
        console.log($contentForm.find(".box-body").length);
        $contentForm.find(".box-body").append(formGroupDiv);
    }
    //
    if(detailId - 0 == 0){
        $contentForm.find("button.update").hide();
        $contentForm.find("button.create").show();
        $(".createHidden").hide();
        return;
    }
    $(".createHidden").show();
    $contentForm.find("button.update").show();
    $contentForm.find("button.create").hide();
    $("#detailListBox").show();

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
            $contentForm.find("[pid]").each(function () {
                var jpa = rs.data[$(this).attr("pid")];
                $(this).val(jpa).trigger("change");
            });

            $.each(rs.data.array, function (index, obj) {
                var productId = obj.productId;
                var productName = $.cacheEnablePacketProducts["id" + productId];
                var quantity = obj.quantity;
                var existingInput = $contentForm.find(".packetProductDiv input[productId=" + productId + "]");
                if(existingInput.length > 0){
                    existingInput.val(existingInput.val() * 1 + quantity);
                }else{
                    var formGroupDiv = $('<div class="form-group packetProductDiv">');
                    var label = $('<label class="col-xs-3 control-label">' + productName + '<span class="required hide">*</span></label>');
                    var inputDiv = $('<div class="col-xs-9">');
                    var input = $('<input class="form-control required" productId="' + productId + '"/>');
                    input.val(quantity);
                    inputDiv.append(input);
                    formGroupDiv.append(label).append(inputDiv);
                    console.log($contentForm.find(".box-body").length);
                    $contentForm.find(".box-body").append(formGroupDiv);
                }
            })
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

function saveDetail(action){
    console.log("saveDetail: " + action);
    if(action != 'delete' && !validateForm()){
        return false;
    }
    var data = {};
    data.action = action;
    $contentForm.find("[pid]").each(function () {
        data[$(this).attr("pid")] = $(this).val();
    });
    console.log(data);
    var array = [];
    $contentForm.find(".packetProductDiv input").each(function(){
        var $this = $(this);
        var detailListObj = {};
        detailListObj.packetId = detailId;
        detailListObj.productId = $this.attr("productId");
        detailListObj.quantity = $this.val();
        if(abcType(detailListObj.quantity) > 0){
            array.push(detailListObj);
        }
    })
    data.packetDetailDTOList = array;
    var ajaxUrl = 'saveDetail';
    $.ajax({
        type: "POST",
        url: ajaxCtx + ajaxUrl,
        data: JSON.stringify(data),
        dataType: "json",
        contentType: "application/json",
        success: function (rs) {
            console.log("saveDetail.success");
            console.log(rs);
            toDetail(rs.data.id);
            $.showToastr();
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.log("saveDetail.error");
            console.log(XMLHttpRequest);
            $.showErrorModal(XMLHttpRequest.responseText);
        },
        complete: function () {
            console.log("saveDetail.complete");
        }
    });
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

$.refreshCacheEnablePacketProducts = function () {
    var data = {};
    $.ajax({
        type: "POST",
        url: "product/refreshCacheEnablePacketProducts",
        data: data,
        dataType: "json",
        success: function (rs) {
            $.cacheEnablePacketProducts = rs;
            console.log("$.cacheEnablePacketProducts");
            console.log($.cacheEnablePacketProducts);
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            $.showErrorModal(XMLHttpRequest.responseText);
            console.log("error");
        },
        complete: function () {
        }
    });
}

function abcType(abc) {
    if(abc === 0 || abc === "0"){
        console.log("abcType: " + abc + ": 0");
        return 0;
    }
    if(abc === null || abc == undefined || $.trim(abc) === ""){
        console.log("abcType: " + abc + ": -1");
        return -1;
    }
    console.log("abcType: " + abc + ": 1");
    return 1;
}