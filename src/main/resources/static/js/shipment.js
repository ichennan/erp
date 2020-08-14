;
var detailId = 0;
var detailListDetailId = 0;
var $contentForm = $("#contentForm");
var $detailListContentForm = $("#detailListContentForm");
var originalBoxDetailListString;
var ajaxCtx = 'shipment/';
var autoSaveAlertTimer;
$(document).ready(function(){
    $(window).on("hashchange",function () {
        if(autoSaveAlertTimer){
            clearInterval(autoSaveAlertTimer);
        }
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

    parent.$.refreshProductsSelect($detailListContentForm.find("[pid=productId]"));
});

function autoSaveAlert() {
    console.log("autoSaveAlert()");
    if(originalBoxDetailListString == JSON.stringify(getBoxDetailList())){
    }else{
        $.confirm({
            title: '确认框',
            content: '每5分钟提醒: 装箱详情或FBA详情有更改尚未保存，系统将自动保存',
            autoClose: 'cancel|61000',
            buttons: {
                ok: {
                    text: "确定",
                    btnClass: 'btn-primary',
                    keys: ['enter'],
                    action: function(){
                        saveDetail("update");
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
}

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
    var theadNames = ['发货日期','FBA No.','货代','线路','箱数','称重','单价','收费重量','金额','付款日期','签收日期','店铺','采购产品'];
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
                var storeName = parent.$.retrieveStoreName(obj.storeId);
                var tds = [obj.deliveryDate, obj.fbaNo, obj.carrier, obj.route, obj.boxCount, obj.weight, obj.unitPrice,  obj.chargeWeight, obj.amount, obj.paymentDate, obj.signedDate, storeName, parent.$.showProductNameGroupByProductIdGroup(obj.productIdGroup)];
                $.each(tds, function (index_2, obj_2) {
                    obj_2 = obj_2 ? obj_2 : "";
                    tr.append("<td>" + obj_2 + "</td>");
                })
                tr.click(function () {
                    toDetail(obj.id);
                });
                if(obj.statusDelivery){
                    tr.addClass("statusDelivery");
                }else{
                    tr.addClass("statusDeliveryNo");
                }
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
    if(autoSaveAlertTimer){
        clearInterval(autoSaveAlertTimer);
    }
    autoSaveAlertTimer = setInterval(autoSaveAlert, 300000);
    console.log("showDetail: " + detailId);
    $("#tableBox").hide();
    $("#contentBox").show();
    $("#detailListTable").remove();
    //
    $contentForm.find("[pid]").each(function () {
        $(this).val("").trigger("change");
    });
    if(detailId - 0 == 0){
        $contentForm.find("button.update").hide();
        $contentForm.find("button.create").show();
        $('.createHidden').hide();
        return;
    }
    $('.createHidden').show();
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
            $contentForm.find("[pid=storeName]").val(parent.$.retrieveStoreName(rs.data["storeId"]));
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

function saveDetail(action){
    console.log("saveDetail: " + action);
    if(action != 'delete' && !validateForm()){
        return false;
    }
    calculateSum();
    var data = {};
    // data.action = action;
    $contentForm.find("[pid]").each(function () {
        data[$(this).attr("pid")] = $(this).val();
    });
    var shipmentDetailList = getBoxDetailList();
    if(!validateShipmentDetailList(shipmentDetailList)){
        return false;
    }
    data.shipmentDetailList = shipmentDetailList;
    console.log(data);
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

function validateShipmentDetailList(shipmentDetailList){
    var validateFailed = false;
    $.each(shipmentDetailList, function(index, obj){
        console.log("obj");
        console.log(obj);
        if(abcType(obj.box) < 0){
            alert("箱号不能为空");
            validateFailed = true;
            return false;
        }
        if(obj.box.indexOf("Plan") > -1){
            alert("箱号不能包含Plan字段");
            validateFailed = true;
            return false;
        }
        if(abcType(obj.weight) < 0){
            alert("重量不能为空");
            validateFailed = true;
            return false;
        }
        if(abcType(obj.productId) > 0 && abcType(obj.quantity) < 0){
            console.log("有产品时，数量不能为空，可以为0");
            alert("有产品时，数量不能为空，可以为0");
            validateFailed = true;
            return false;
        }
        if(abcType(obj.quantity) > 0 && abcType(obj.productId) < 1){
            console.log("填了数量，产品不能为空");
            alert("填了数量，产品不能为空");
            validateFailed = true;
            return false;
        }
    })
    return !validateFailed;
}

// detailList

function showDetailList(){
    console.log("showDetailList()");
    $("#detailListTableDiv").empty();
    $("#planDiv").empty();
    createDetailListTableHead();
    //
    var data = {};
    data.shipmentId = detailId;
    var ajaxUrl = 'findAllDetail';
    var preTr = undefined;
    $.ajax({
        type: "POST",
        url: ajaxCtx + ajaxUrl,
        data: data,
        dataType: "json",
        success: function (rs) {
            console.log(rs);
            $.each(rs.array, function (index, obj) {
                if("Plan" == obj.box){
                    createPlanDiv(obj);
                }else{
                    preTr = createDetailListTableBody(preTr, obj);
                }
            });
            calculateSum();
            originalBoxDetailListString = JSON.stringify(getBoxDetailList());
            nextBox();
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            $.showErrorModal(XMLHttpRequest.responseText);
            console.log("error");
        },
        complete: function () {
        }
    });
}

function createDetailListTableHead(){
    var detailListTable = $("<table class='table table-bordered data-table' id='detailListTable'></table>");
    var thead = $("<thead><tr></tr></thead>");
    var theadNames = ['箱子', '重量', '产品', '数量', ''];
    $.each(theadNames, function (index, obj) {
        thead.find("tr").append("<th>" + obj + "</th>");
    })
    var tbody = $("<tbody></tbody>");
    detailListTable.append(thead).append(tbody);
    $("#detailListTableDiv").append(detailListTable);
}

function createPlanDiv(obj){
    var productId = obj.productId;
    var productName = parent.$.cacheProducts["id" + obj.productId].snname;
    var quantity = obj.quantity;
    var colDiv = $('<div class="col-sm-4 text-center">');
    var thumbnailDiv = $('<div class="thumbnail">');
    thumbnailDiv.attr("productId", "id" + productId);
    thumbnailDiv.attr("quantity", quantity);
    var img = $('<img />');
    var imgName = "id" + productId +".jpg";
    img.attr("src", "product/getProductImage/" + imgName + "");
    img.attr("productId", productId);
    img.attr("productName", productName);
    img.draggable({
        revert: true,
        opacity: 0.20
    });
    var captionDiv = $('<div class="caption">');
    var captionH = $('<h5></h5>');
    captionH.text(productName);
    var captionP = $('<p></p>');
    captionP.text(obj.quantity);
    var captionSpan = $('<span></span>');
    captionSpan.text(obj.remark);
    captionDiv.append(captionH, captionP, captionSpan);
    thumbnailDiv.append(img, captionDiv);
    colDiv.append(thumbnailDiv);
    colDiv.click(function () {
        showDetailListContentModalPre(obj.id);
    });
    $("#planDiv").append(colDiv);
}

function createNewBox(){
    var preTr = undefined;
    var obj = undefined;
    createDetailListTableBody(preTr, obj);
}

function createNewProductInBox(preTr){
    var obj = undefined;
    createDetailListTableBody(preTr, obj);
}

function createDetailListTableBody(preTr, obj){
    var originalObj = obj;
    if(obj === undefined){
        obj = {};
        obj.box = nextBox();
        obj.weight = "";
        obj.quantity = "";
    }
    var tr = $("<tr></tr>");
    //
    var boxTd = $("<td></td>");
    boxTd.attr("tidKey", "box");
    boxTd.attr("tidValue", obj.box);
    var boxInput = $("<input />");
    boxInput.val(obj.box);
    var addProductIcon = $('<i class="fa fa-fw fa-calendar-plus-o"></i>');
    addProductIcon.click(function(){
        createNewProductInBox(tr);
    });
    var deleteBoxIcon = $('<i class="fa fa-fw fa-calendar-minus-o"></i>');
    deleteBoxIcon.confirm({
        title: '确认框',
        content: '删除整个箱子?',
        buttons: {
            ok: {
                text: "确定",
                btnClass: 'btn-primary',
                keys: ['enter'],
                action: function(){
                    var currentTr = this.$target.parents("tr");
                    var boxRandom = currentTr.attr("boxRandom");
                    currentTr.siblings("[boxRandom=" + boxRandom +"]").remove();
                    currentTr.remove();
                }
            },
            cancel: {
                text: "取消",
                btnClass: 'btn',
                keys: ['esc'],
                action:function () {
                }
            }
        }
    });
    boxTd.append(addProductIcon, boxInput, deleteBoxIcon);
    //
    var weightTd = $("<td></td>");
    weightTd.attr("tidKey", "weight");
    weightTd.attr("tidValue", obj.weight);
    var weightInput = $("<input />");
    weightInput.val(obj.weight);
    weightInput.blur(function () {
        calculateSum();
    })
    weightTd.append(weightInput);
    var productTd = $("<td></td>");
    productTd.attr("tidKey", "productId");
    productTd.attr("tidValue", obj.productId);
    productTd.text(obj.productId ? parent.$.cacheProducts["id" + obj.productId].snname : "");
    //
    var quantityTd = $("<td></td>");
    quantityTd.attr("tidKey", "quantity");
    var quantityInput = $("<input />");
    quantityInput.val(obj.quantity);
    quantityInput.blur(function () {
        calculateSum();
    })
    quantityTd.append(quantityInput);
    //
    var actionTd = $("<td></td>");
    var deleteProductIcon = $('<i class="fa fa-fw fa-calendar-times-o"></i>');
    deleteProductIcon.click(function(){
        var $thisTr = $(this).parents("tr");
        $thisTr.find("td[tidKey=productId]").empty().attr("tidValue", "");
        $thisTr.find("td[tidKey=quantity]").find("input").val('');
    });
    actionTd.append(deleteProductIcon);
    //
    tr.append(boxTd, weightTd, productTd, quantityTd, actionTd);
    tr.droppable({
        activeClass: "droppable-active",
        hoverClass: "droppable-hover",
        drop: function( event, ui ) {
            var $productTd = $(this).find("[tidKey=productId]");
            if($productTd.text()){
                alert("无法替换，请删除后添加");
            }else{
                $productTd.text(ui.draggable.attr("productName"));
                $productTd.attr("tidValue", ui.draggable.attr("productId"));
                $productTd.addClass("droppable-done");
                calculateSum();
            }
        }
    });
    if(preTr){
        if(originalObj === undefined){
            var boxRandom = preTr.attr("boxRandom");
            if(preTr.siblings("[boxRandom=" + boxRandom +"]").length > 0){
                preTr.siblings("[boxRandom=" + boxRandom +"]:last").after(tr);
            }else{
                preTr.after(tr);
            }
        }else{
            preTr.after(tr);
        }
    }else{
        $("#detailListTableDiv").find("table").find("tbody").append(tr);
    }

    if(preTr === undefined){
        console.log("boxFather: " + "preTr === undefined");
        tr.addClass("boxFather");
        tr.attr("boxRandom", parseInt(Math.random() * (999999) + 1));
        return tr;
    }

    if(originalObj === undefined){
        console.log("boxChild: " + "originalObj === undefined");
        tr.addClass("boxChild");
        tr.attr("boxRandom", preTr.attr("boxRandom"));
        return tr;
    }

    var preBox = preTr.find("td[tidKey=box]").attr("tidValue");
    console.log("preBox");
    console.log(preBox);
    console.log("preBox");
    if(preBox === undefined){
        console.log("boxChild: " + "preBox === undefined");
        tr.addClass("boxChild");
        tr.attr("boxRandom", preTr.attr("boxRandom"));
        return tr;
    }

    if(obj.box == preBox){
        console.log("boxChild: " + "obj.box == preBox");
        tr.addClass("boxChild");
        tr.attr("boxRandom", preTr.attr("boxRandom"));
        return tr;
    }

    console.log("boxFather: " + "others");
    tr.addClass("boxFather");
    tr.attr("boxRandom", parseInt(Math.random() * (999999) + 1));
    return tr;
}

function showDetailListContentModalPre(id){
    if(originalBoxDetailListString == JSON.stringify(getBoxDetailList())){
        showDetailListContentModal(id);
        return;
    }
    $.confirm({
        title: '确认框',
        content: '装箱详情或FBA详情有更改尚未保存，系统将自动保存',
        buttons: {
            ok: {
                text: "确定",
                btnClass: 'btn-primary',
                keys: ['enter'],
                action: function(){
                    saveDetail("update");
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

function showDetailListContentModal(id){
    $('#detailListContentModal').modal('show');
    detailListDetailId = id;
    console.log("detailListContent: " + detailListDetailId);
    //
    $detailListContentForm.find("[pid]").each(function () {
        $(this).val("").trigger("change");
    });
    $detailListContentForm.find("[pid=quantity]").val(0).trigger("change");
    if(detailListDetailId - 0 == 0){
        $detailListContentForm.find("button.update").hide();
        $detailListContentForm.find("button.create").show();
        $detailListContentForm.find("[pid=box]").val("Plan");
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
    data.weight = 0;
    data.shipmentId = detailId;
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

function getBoxDetailList(){
    console.log("getBoxDetailList()");
    var $tbody = $("#detailListTableDiv table tbody");
    var boxDetailList = [];
    var box = 0;
    $tbody.find("tr").each(function () {
        var $this = $(this);
        var boxFather = $this.hasClass("boxFather");
        var boxDetail = {};
        boxDetail.shipmentId = detailId;
        var $boxInput = $this.find('td[tidKey=box]').find("input");
        if(boxFather){
            box = $boxInput.val();
        }
        boxDetail.box = box;
        var $weightInput = $this.find('td[tidKey=weight]').find("input");
        boxDetail.weight = boxFather ? $weightInput.val() : 0;
        boxDetail.productId = $this.find('td[tidKey=productId]').attr("tidValue");
        boxDetail.quantity = $this.find('td[tidKey=quantity]').find("input").val();
        if(
            (abcType(boxDetail.productId) > 0)
            ||
            (abcType(boxDetail.quantity) > 0)
        ){
            boxDetailList.push(boxDetail);
        }
    });
    console.log("boxDetailList");
    console.log(boxDetailList);
    return boxDetailList;
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

function calculateSum(){
    console.log("calculateSum()");
    var $tbody = $("#detailListTableDiv table tbody");
    var totalWeight = 0;
    var boxCount = 0;
    var productQuantity = {};
    $tbody.find('tr').each(function () {
        var $this = $(this);
        var productId = "id" + $this.find("td[tidKey=productId]").attr("tidValue");
        var quantity = $this.find("td[tidKey=quantity]").find("input").val();
        productQuantity[productId] = null2zero(productQuantity[productId]) + null2zero(quantity);

        if($this.hasClass("boxFather")){
            totalWeight = totalWeight + null2zero($this.find("td[tidKey=weight]").find("input").val());
            boxCount = boxCount + 1;
        }
    })
    console.log("totalWeight: " + totalWeight);
    console.log("productQuantity: " + productQuantity);
    console.log("boxCount: " + boxCount);
    $contentForm.find("[pid=weight]").val(totalWeight.toFixed(2));
    $contentForm.find("[pid=boxCount]").val(boxCount);

    $("#planDiv .thumbnail").each(function(){
        var $this = $(this);
        var $p = $this.find("p");
        var planQuantity = $this.attr("quantity");
        var productId = $this.attr("productId");
        var currentQuantity = null2zero(productQuantity[productId]);
        if(planQuantity - currentQuantity > 0){
            $p.removeClass().addClass("planLess");
        }
        if(planQuantity - currentQuantity == 0){
            $p.removeClass().addClass("planEqual");
        }
        if(planQuantity - currentQuantity < 0){
            $p.removeClass().addClass("planMore");
        }
        $this.find("p").text(planQuantity + " / " + currentQuantity);
    })
}

function null2zero(abc){
    if(abc){
        return abc * 1;
    }
    return 0;
}

function nextBox(){
    console.log("nextBox()");
    var $tbody = $("#detailListTableDiv table tbody");
    var maxValue = undefined;
    $tbody.find("tr.boxFather td[tidKey=box] input").each(function(){
        var currentValue = $(this).val();
        if(maxValue === undefined){
            maxValue = currentValue;
        }else{
            maxValue = currentValue > maxValue ? currentValue : maxValue;
        }
    })
    if(!maxValue){
        maxValue = "0";
    }
    var nextValue = String.fromCharCode(maxValue.charCodeAt() + 1);
    console.log("maxValue: " + maxValue);
    console.log("nextValue: " + nextValue);
    return nextValue;
}