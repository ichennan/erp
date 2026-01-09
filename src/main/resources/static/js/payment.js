var itemId = 0;
var $itemForm = $("#itemForm");
var ajaxCtx = 'payment/';
var theadNames = ['id', '日期', '收支', '一级分类', '二级分类', '三级分类', '店铺', '金额', '待付金额', '平台', '账户', '科目'];
$(document).ready(function(){
    createItemForm();
    $(window).on("hashchange",function () {
        var hash = location.hash ? location.hash : '';
        var hash4 = hash.substring(0, 5).toString();
        console.log('hashChange to ' + hash);
        if (hash4 == "#item") {
            itemId = hash.substring(8);
            showItem();
        } else {
            itemId = 0;
            showList();
        }
    }).trigger("hashchange");
});

function backToList(){
    location.hash = "#";
    $(window).trigger('hashchange');
}

function toItem(id){
    console.log("toItem: " + id);
    location.hash == "#itemId=" + id ? $(window).trigger('hashchange') : location.hash = "#itemId=" + id;
}

function showList(){
    console.log("showList()");
    $("#searchBox").show();
    $("#tableBox").show();
    $("#itemBox").hide();

    var table = createListTableHead();
    var data = {};
    var ajaxUrl = 'findList'
    $.ajax({
        type: "POST",
        url: ajaxCtx + ajaxUrl,
        data: data,
        dataType: "json",
        success: function (rs) {
            console.log(rs);
            createListTableBody(table, rs);
            createDataTable(table, {"order": 1});
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            $.showErrorModal(XMLHttpRequest.responseText);
            console.log("error");
        },
        complete: function () {
        }
    });
}

function showItem(){
    console.log("showItem: " + itemId);
    $("#searchBox").hide();
    $("#tableBox").hide();
    $("#itemBox").show();

    $itemForm.find("[pid]").each(function () {
        $(this).val("").trigger("change");
    });
    if(itemId - 0 == 0){
        $itemForm.find("button.update").hide();
        $itemForm.find("button.create").show();
        $('.createHidden').hide();
        return;
    }
    $('.createHidden').show();
    $itemForm.find("button.update").show();
    $itemForm.find("button.create").hide();

    var data = {};
    data.id = itemId;
    var ajaxUrl = 'getItem';
    $.ajax({
        type: "POST",
        url: ajaxCtx + ajaxUrl,
        data: data,
        dataType: "json",
        success: function (rs) {
            console.log("getItem.success");
            console.log(rs);
            $itemForm.find("[pid]").each(function () {
                var jpa = rs.data[$(this).attr("pid")];
                $(this).val(jpa).trigger("change");
            });
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.log("getItem.error");
            console.log(XMLHttpRequest);
            $.showErrorModal(XMLHttpRequest.responseText);
        },
        complete: function () {
            console.log("getItem.complete");
        }
    });
}

function saveItem(action){
    console.log("saveItem: " + action);
    if(action != 'delete' && !validateForm()){
        return false;
    }
    var data = {};
    data.action = action;
    $itemForm.find("[pid]").each(function () {
        data[$(this).attr("pid")] = $(this).val();
    });
    console.log(data);
    var ajaxUrl = 'saveItem';
    $.ajax({
        type: "POST",
        url: ajaxCtx + ajaxUrl,
        data: JSON.stringify(data),
        dataType: "json",
        contentType: "application/json",
        success: function (rs) {
            console.log("saveItem.success");
            console.log(rs);
            backToList();
            $.showToastr();
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.log("saveItem.error");
            console.log(XMLHttpRequest);
            $.showErrorModal(XMLHttpRequest.responseText);
        },
        complete: function () {
            console.log("saveItem.complete");
        }
    });
}

function validateForm(form) {
    var form = form ? form : $("#itemForm");
    if (form.get(0).checkValidity()){
        return true;
    }else{
        form.find("[type='submit']").trigger("click");
        return false;
    }
}

function createListTableHead(){
    console.log("createListTableHead");
    $("#listTable").remove();
    $("#listTable_wrapper").remove();
    var listTable = $("<table class='table table-bordered data-table' id='listTable'></table>");
    var thead = $("<thead><tr></tr></thead>");
    var theadSearch = $("<thead class='theadSearch'><tr></tr></thead>");
    $.each(theadNames, function (index, obj) {
        thead.find("tr").append("<th>" + obj + "</th>");
        theadSearch.find("tr").append("<th><input style='width:1px'></th>");
    })
    var tbody = $("<tbody></tbody>");
    listTable.append(thead).append(theadSearch).append(tbody);
    $("#tableDiv").append(listTable);
    return listTable;
}

function createListTableBody(table, rs){
    console.log("createListTableBody");
    $.each(rs.array, function (index, obj) {
        var tr = $("<tr></tr>");
        var tds = [
            obj.id,
            obj.transactionDate,
            obj.inex,
            obj.category1st,
            obj.category2nd,
            obj.category3rd,
            obj.transactionStore,
            obj.transactionAmount,
            obj.pendingAmount,
            obj.transactionPlatform,
            obj.transactionAccount,
            obj.transactionSubject
        ];
        $.each(tds, function (index_2, obj_2) {
            obj_2 = obj_2 ? obj_2 : "";
            var td = $("<td>" + obj_2 + "</td>");
            td.attr("columnName", theadNames[index_2]);
            if(obj.inex == '收入'){
                tr.addClass("inex-income");
            } else if(obj.inex == '支出'){
                tr.addClass("inex-expense");
            }
            tr.append(td);
        })
        tr.click(function () {
            toItem(obj.id);
        });
        table.find("tbody").append(tr);
    });
}

function createDataTable(table, opt){
    console.log("createDataTable");
    var order = (opt && opt.order) ? opt.order : 0;
    var tableId = table.attr("id");
    console.log("tableId: " + tableId);
    var datatable = table.DataTable({
        "bJQueryUI": true,
        "sPaginationType": "full_numbers",
        "ordering": true,
        "bSort": true,
        "language": $.dataTablesLanguage,
        "pageLength": 1000000,
        "order": [[1, 'desc']]
    });
    if(table.find('.theadSearch')){
        table.find('.theadSearch').find('input').css("width", "100%");
        $("#" + tableId + "_filter").css("display", "none");
        $("#" + tableId + "_length").css("display", "none");

        datatable.columns().eq( 0 ).each( function ( colIdx ) {
            $( 'input', datatable.column( colIdx ).header2() ).on( 'keyup change', function () {
                datatable
                    .column( colIdx )
                    .search( this.value )
                    .draw();
            } );
        } );
    }
}

function createItemForm() {
    console.log("createItemForm()");
    var itemArray = [];
    var i = -1;
    itemArray[++i] = {"label": "状态", "pid": "status", "inputType": "text"};
    itemArray[++i] = {"label": "收支", "pid": "inex", "required": true, "inputType": "select", "options": ["收入", "支出"]};
    itemArray[++i] = {"label": "一级分类", "pid": "category1st", "required": true, "inputType": "text"};
    itemArray[++i] = {"label": "二级分类", "pid": "category2nd", "inputType": "text"};
    itemArray[++i] = {"label": "三级分类", "pid": "category3rd", "inputType": "text"};
    itemArray[++i] = {"label": "日期", "pid": "transactionDate", "required": true, "inputType": "text", "placeholder": "YYYY-MM-DD"};
    itemArray[++i] = {"label": "店铺", "pid": "transactionStore", "inputType": "text"};
    itemArray[++i] = {"label": "金额", "pid": "transactionAmount", "required": true, "inputType": "number", "step": "0.01"};
    itemArray[++i] = {"label": "待付金额", "pid": "pendingAmount", "inputType": "number", "step": "0.01"};
    itemArray[++i] = {"label": "平台", "pid": "transactionPlatform", "inputType": "text"};
    itemArray[++i] = {"label": "账户", "pid": "transactionAccount", "inputType": "text"};
    itemArray[++i] = {"label": "科目", "pid": "transactionSubject", "inputType": "text"};
    itemArray[++i] = {"label": "备注", "pid": "remark", "inputType": "text"};
    $.drawContentForm($itemForm, itemArray);
}
