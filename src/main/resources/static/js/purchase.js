jQuery.datetimepicker.setLocale('ch');
;
var detailId = 0;
var detailListDetailId = 0;
var $contentForm = $("#contentForm");
var $detailListContentForm = $("#detailListContentForm");
var ajaxCtx = 'purchase/';
var ajaxCtx_product = 'product/';
var tableType = "";
$(document).ready(function(){
    parent.$.refreshProductsSelect($detailListContentForm.find("[pid=productId]"));

    console.log("parent.$.cacheProducts");
    var $searchProductIdSelect = $("[sid=productId]");
    var searchProductIdSelectData = [];
    $.each(parent.$.cacheProducts, function(productId, productObj){
        var optionObj = {};
        optionObj.id = productObj.id;
        optionObj.text = productObj.snname;
        searchProductIdSelectData.push(optionObj);
    });
    $searchProductIdSelect.select2({
        data: searchProductIdSelectData,
        placeholder:'请选择',
        allowClear:true,
        width: "100%"
    })

    $(".datetimepicker").datetimepicker({
        todayButton: true,
        timepicker:false,
        format:'Y-m-d'
    });

    $("span.spanOption.tableType").click(function () {
        var $this = $(this);
        $("span.tableType").removeClass("selected");
        $this.addClass("selected");
        tableType = $this.attr("tableType");
        $("[tableType]").hide();
        $("[tableType=" + tableType + "]").show();
        $("[tableType].spanOption").show();
    })

    $("span.spanOption.searchDateType").click(function () {
        var $this = $(this);
        $("span.searchDateType").removeClass("selected");
        $this.addClass("selected");
    })

    $("span.spanOption.searchDateRange").click(function () {
        var $this = $(this);
        $("span.searchDateRange").removeClass("selected");
        $this.addClass("selected");
        var searchDateRange = $this.attr("searchDateRange");
        if(!searchDateRange){
            $("[sid=dateFrom]").val("").trigger("change");
            return;
        }
        var dateToMoment = moment($("[sid=dateTo]").val(), "YYYY-MM-DD");
        var dateFromMoment = moment(dateToMoment).add(-1 * searchDateRange, 'months').add(1, 'days');
        $("[sid=dateFrom]").val(moment(dateFromMoment).format("YYYY-MM-DD")).trigger("change");
    })

    setAutoCompleteSuppliers();
    resetSearch();

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
            if(tableType == "purchaseProductList"){
                showListProducts();
            }else{
                showList();
            }
        }
    }).trigger("hashchange");
});

function setAutoCompleteSuppliers(){
    var data = {};
    var ajaxUrl = 'findAutoCompleteSupplier';
    $.ajax({
        type: "POST",
        url: ajaxCtx + ajaxUrl,
        data: data,
        dataType: "json",
        success: function (rs) {
            console.log("findAutoCompleteSupplier.success");
            console.log(rs);
            $(".autoCompleteSuppliers").autocomplete({
                minLength: 0,
                source: rs.array
            });
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.log("findAutoCompleteSupplier.error");
            console.log(XMLHttpRequest);
            $.showErrorModal(XMLHttpRequest.responseText);
        },
        complete: function () {
            console.log("findAutoCompleteSupplier.complete");
        }
    });
}

function resetSearch(){
    $("[sid=dateTo]").val(moment().format("YYYY-MM-DD")).trigger("change");
    $("span.defaultSelected").trigger("click");
    $("[sid=productId]").val("").trigger("change");
}

function searchFormCollect(){
    console.log("searchFormCollect()");
    var queryData = {};
    queryData.dateFrom = $("[sid=dateFrom]").val();
    queryData.dateTo = $("[sid=dateTo]").val();
    queryData.supplier = $("[sid=supplier]").val();
    queryData.dateType = $(".searchDateType.spanOption.selected").attr("searchDateType");
    console.log(queryData);
    return queryData;
}

function downloadList(){
    console.log("downloadList()");
    showList();
    var queryData = searchFormCollect();
    var dataStr = $.jsonToString(queryData);
    $("input#formData").attr("value", dataStr);
    $("form#downloadForm").attr("action", "purchase/downloadList");
    $("form#downloadForm").submit();
}

function showList(){
    console.log("showList()");
    $("#tableBox").show();
    $("#searchBox").show();
    $("#contentBox").hide();
    $("#detailListBox").hide();
    //
    $("#listTable").remove();
    $("#listTable_wrapper").remove();
    var listTable = $("<table class='table table-bordered data-table' id='listTable'></table>");
    var thead = $("<thead><tr></tr></thead>");
    var theadSearch = $("<thead class='theadSearch'><tr></tr></thead>");
    var theadNames = ['id', '上传日期', '发货日期', '收货日期', '费用','供应商','采购产品'];
    $.each(theadNames, function (index, obj) {
        thead.find("tr").append("<th>" + obj + "</th>");
        theadSearch.find("tr").append("<th><input style='width:1px'></th>");
    })
    var tbody = $("<tbody></tbody>");
    listTable.append(thead).append(theadSearch).append(tbody);
    $("#tableDiv").append(listTable);
    var queryData = searchFormCollect();
    var ajaxUrl = 'findAll'
    $.ajax({
        type: "POST",
        url: ajaxCtx + ajaxUrl,
        data: queryData,
        dataType: "json",
        success: function (rs) {
            console.log(rs);
            $.each(rs.array, function (index, obj) {
                var tr = $("<tr></tr>");
                var tds = [obj.id, obj.excelDate, obj.deliveryDate, obj.receivedDate, obj.amount, obj.supplier, parent.$.showProductNameGroupByProductIdGroupWithQuantity(obj.productIdGroup)];
                $.each(tds, function (index_2, obj_2) {
                    obj_2 = obj_2 ? obj_2 : "";
                    var td = $("<td>" + obj_2 + "</td>");
                    td.attr("columnName", theadNames[index_2]);
                    tr.append(td);
                })
                // tr.attr("title", parent.$.showProductNameGroupByProductIdGroup(obj.productIdGroup));
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
                "pageLength": 100000,
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
                    calculatePurchaseQuanity();
                } );
            } );
            calculatePurchaseQuanity();
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
    console.log("toDetail: " + id);
    location.hash == "#detailId=" + id ? $(window).trigger('hashchange') : location.hash = "#detailId=" + id;
}

function showDetail(){
    console.log("showDetail: " + detailId);
    $("#tableBox").hide();
    $("#searchBox").hide();
    $("#contentBox").show();
    $("#detailListTable").remove();
    //
    $contentForm.find("[pid]").each(function () {
        $(this).val("").trigger("change");
    });
    $contentForm.find("[pid=freight]").val("0").trigger("change");
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

    showDetailList();
}

function deleteDetail(){
    deleteConfirm(saveDetail);
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
    var ajaxUrl = 'saveDetail';
    $.ajax({
        type: "POST",
        url: ajaxCtx + ajaxUrl,
        data: data,
        dataType: "json",
        success: function (rs) {
            console.log("saveDetail.success");
            console.log(rs);
            if("create" == action){
                toDetail(rs.data.id);
            }else{
                showList();
            }
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

// detailList

function showDetailList(){
    console.log("showDetailList()");
    //
    var detailListTable = $("<table class='table table-bordered data-table' id='detailListTable'></table>");
    var thead = $("<thead><tr></tr></thead>");
    var theadNames = ['产品', '采购数量', '收货数量', '采购单价'];
    $.each(theadNames, function (index, obj) {
        thead.find("tr").append("<th>" + obj + "</th>");
    })
    var tbody = $("<tbody></tbody>");
    detailListTable.append(thead).append(tbody);
    $("#detailListTableDiv").append(detailListTable);
    var data = {};
    data.purchaseId = detailId;
    var ajaxUrl = 'findAllDetail'
    $.ajax({
        type: "POST",
        url: ajaxCtx + ajaxUrl,
        data: data,
        dataType: "json",
        success: function (rs) {
            console.log(rs);
            $.each(rs.array, function (index, obj) {
                var tr = $("<tr></tr>");
                var tds = [parent.$.cacheProducts["id" + obj.productId].snname, obj.bookQuantity, obj.receivedQuantity, obj.unitPrice];
                $.each(tds, function (index_2, obj_2) {
                    obj_2 = obj_2 ? obj_2 : "";
                    tr.append("<td>" + obj_2 + "</td>");
                })
                tr.click(function () {
                    showDetailListContentModal(obj.id);
                });
                tbody.append(tr);
            });
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            $.showErrorModal(XMLHttpRequest.responseText);
            console.log("error");
        },
        complete: function () {
        }
    });
}

function showDetailListContentModal(id){
    $('#detailListContentModal').modal('show');
    detailListDetailId = id;
    console.log("detailListContent: " + detailListDetailId);
    //
    $detailListContentForm.find("[pid]").each(function () {
        $(this).val("").trigger("change");
    });
    $detailListContentForm.find("[pid=bookQuantity]").val("0").trigger("change");
    $detailListContentForm.find("[pid=receivedQuantity]").val("0").trigger("change");
    $detailListContentForm.find("[pid=unitPrice]").val("0").trigger("change");
    if(detailListDetailId - 0 == 0){
        $detailListContentForm.find("button.update").hide();
        $detailListContentForm.find("button.create").show();
        return;
    }
    $detailListContentForm.find("button.update").show();
    $detailListContentForm.find("button.create").hide();

    var data = {};
    data.id = detailListDetailId;
    var ajaxUrl = 'getDetailListDetail';
    $.ajax({
        type: "POST",
        url: ajaxCtx + ajaxUrl,
        data: data,
        dataType: "json",
        success: function (rs) {
            console.log("getDetailListDetail.success");
            console.log(rs);
            $detailListContentForm.find("[pid]").each(function () {
                var jpa = rs.data[$(this).attr("pid")];
                $(this).val(jpa).trigger("change");
            });
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.log("getDetailListDetail.error");
            console.log(XMLHttpRequest);
            $.showErrorModal(XMLHttpRequest.responseText);
        },
        complete: function () {
            console.log("getDetailListDetail.complete");
        }
    });

}

function deleteDetailListDetail(){
    deleteConfirm(saveDetailListDetail);
}


function saveDetailListDetail(action){
    console.log("saveDetailListDetail: " + action);
    if(action != 'delete' && !validateForm()){
        return false;
    }
    var data = {};
    data.action = action;
    $detailListContentForm.find("[pid]").each(function () {
        data[$(this).attr("pid")] = $(this).val();
    });
    data.purchaseId = detailId;
    console.log(data);
    var ajaxUrl = 'saveDetailListDetail';
    $.ajax({
        type: "POST",
        url: ajaxCtx + ajaxUrl,
        data: data,
        dataType: "json",
        success: function (rs) {
            console.log("saveDetailListDetail.success");
            console.log(rs);
            $('#detailListContentModal').modal('hide');
            toDetail(detailId);
            $.showToastr();
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.log("saveDetailListDetail.error");
            console.log(XMLHttpRequest);
            $.showErrorModal(XMLHttpRequest.responseText);
        },
        complete: function () {
            console.log("saveDetailListDetail.complete");
        }
    });
}

function deleteConfirm(fc) {
    var randomNumber = parseInt(10*Math.random());
    $.confirm({
        title: '删除确认框',
        content: '' +
            '<div class="form-group">' +
            '<label>即将删除，确认请输入数字: ' + randomNumber + '</label>' +
            '<input type="text" placeholder="请输入数字以确认删除" class="randomNumber form-control" required />' +
            '</div>',
        buttons: {
            ok: {
                text: '确认',
                btnClass: 'btn-blue',
                action: function () {
                    var randomNumberInput = this.$content.find('.randomNumber').val();
                    if(!randomNumberInput || (randomNumber - randomNumberInput != 0)){
                        $.alert('请输入正确数字以确认删除');
                        return false;
                    }
                    fc("delete");
                }
            },
            cancel: {
                text: "取消",
                btnClass: 'btn',
                keys: ['esc'],
                action:function () {
                    return;
                }
            }
        }
    });
}

function getPurchasePrice() {
    console.log("getPurchasePrice()");
    var productId = $detailListContentForm.find("[pid=productId]").val();
    if(productId){
        $detailListContentForm.find("[pid=unitPrice]").val(null2zero(parent.$.cacheProducts["id" + productId].purchasePrice));
    }
}

function updatePurchasePrice(){
    console.log("updatePurchasePrice");
    var data = {};
    data.purchasePrice = $detailListContentForm.find("[pid=unitPrice]").val();
    data.id = $detailListContentForm.find("[pid=productId]").val();
    if(!data.id){
        $.showErrorModal("请先选择产品");
        return;
    }
    console.log(data);
    var ajaxUrl = 'updatePurchasePrice';
    $.ajax({
        type: "POST",
        url: ajaxCtx_product + ajaxUrl,
        data: data,
        dataType: "json",
        success: function (rs) {
            console.log("updatePurchasePrice.success");
            console.log(rs);
            $.showToastr();
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.log("updatePurchasePrice.error");
            console.log(XMLHttpRequest);
            $.showErrorModal(XMLHttpRequest.responseText);
        },
        complete: function () {
            console.log("updatePurchasePrice.complete");
        }
    });
}

function null2zero(abc){
    if(abc){
        return abc * 1;
    }
    return 0;
}

//

function showListProducts(){
    console.log("showListProducts()");
    $("#tableBox").show();
    $("#searchBox").show();
    $("#contentBox").hide();
    $("#detailListProductsBox").hide();
    //
    $("#listTableProducts").remove();
    $("#listTableProducts_wrapper").remove();
    var listTable = $("<table class='table table-bordered data-table' id='listTableProducts'></table>");
    var thead = $("<thead><tr></tr></thead>");
    var theadSearch = $("<thead class='theadSearch'><tr></tr></thead>");
    var theadNames = ['上传日期', '发货日期', '收货日期', '供应商','采购产品','产品数量','采购价格'];
    $.each(theadNames, function (index, obj) {
        thead.find("tr").append("<th>" + obj + "</th>");
        theadSearch.find("tr").append("<th><input style='width:1px'></th>");
    })
    var tbody = $("<tbody></tbody>");
    listTable.append(thead).append(theadSearch).append(tbody);
    $("#tableDivProducts").append(listTable);
    var data = {};
    data.dateFrom = $("[sid=dateFrom]").val();
    data.dateTo = $("[sid=dateTo]").val();
    data.productId = $("[sid=productId]").val();
    data.dateType = $(".searchDateType.spanOption.selected").attr("searchDateType");
    console.log(data);
    var ajaxUrl = 'findAllProducts'
    $.ajax({
        type: "POST",
        url: ajaxCtx + ajaxUrl,
        data: data,
        dataType: "json",
        success: function (rs) {
            console.log(rs);
            $.each(rs.array, function (index, obj) {
                var tr = $("<tr></tr>");
                var tds = [obj.excelDate, obj.deliveryDate, obj.receivedDate, obj.supplier, parent.$.cacheProducts["id" + obj.productId].snname, obj.receivedQuantity, obj.unitPrice];
                $.each(tds, function (index_2, obj_2) {
                    obj_2 = obj_2 ? obj_2 : "";
                    var td = $("<td>" + obj_2 + "</td>");
                    td.attr("columnName", theadNames[index_2]);
                    tr.append(td);
                })
                tr.click(function () {
                    toDetail(obj.purchaseId);
                });
                tbody.append(tr);
            });

            var datatable = $('#listTableProducts').DataTable({
                "bJQueryUI": true,
                "sPaginationType": "full_numbers",
                "ordering": true,
                "bSort": true,
                "language": $.dataTablesLanguage,
                "pageLength": 10000000,
                "order": [[ 1, "desc" ]],
            });

            theadSearch.find('input').css("width", "100%");
            $("#listTableProducts_filter, #listTableProducts_length").css("display", "none");

            datatable.columns().eq( 0 ).each( function ( colIdx ) {
                $( 'input', datatable.column( colIdx ).header2() ).on( 'keyup change', function () {
                    datatable
                        .column( colIdx )
                        .search( this.value )
                        .draw();
                    calculatePurchaseProductsQuanity();
                });
            });

            calculatePurchaseProductsQuanity();
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            $.showErrorModal(XMLHttpRequest.responseText);
            console.log("error");
        },
        complete: function () {
        }
    });
}

function calculatePurchaseProductsQuanity(){
    var sumQuantity = 0;
    var sumPrice = 0;
    $("#listTableProducts").find("tbody tr:visible").each(function () {
        var $this = $(this);
        var quantity = $this.find("td[columnName = 产品数量]").text() * 1;
        var price = $this.find("td[columnName = 采购价格]").text() * 1;
        sumQuantity = sumQuantity + quantity;
        sumPrice = sumPrice + quantity * price;
    })
    $("#sumQuantity").text(sumQuantity);
    $("#sumPrice").text(sumPrice);
}

function calculatePurchaseQuanity(){
    console.log("calculatePurchaseQuanity()");
    var sumPurchaseCount = 0;
    var sumPurchasePrice = 0;
    $("#listTable").find("tbody tr:visible").each(function () {
        var $this = $(this);
        var price = $this.find("td[columnName = 费用]").text() * 1;
        sumPurchaseCount = sumPurchaseCount + 1;
        sumPurchasePrice = sumPurchasePrice + 1 * price;
    })
    $("#sumPurchaseCount").text(sumPurchaseCount);
    $("#sumPurchasePrice").text(sumPurchasePrice);
}