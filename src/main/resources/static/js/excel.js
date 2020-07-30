;
var detailId = 0;
var $contentForm = $("#contentForm");
var ajaxCtx = 'excel/';
var excelId = 0;
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
        url: ajaxCtx + "uploadSupplierDelivery",
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
    $("#listTable").remove();
    $("#listTable_wrapper").remove();
    var listTable = $("<table class='table table-bordered data-table' id='listTable'></table>");
    var thead = $("<thead><tr></tr></thead>");
    // var theadSearch = $("<thead class='theadSearch'><tr></tr></thead>");
    var theadNames = ['订单号', '序号', '产品', '编号', '品名','规格','数量','单价','金额','备注'];
    $.each(theadNames, function (index, obj) {
        thead.find("tr").append("<th>" + obj + "</th>");
        // theadSearch.find("tr").append("<th><input style='width:1px'></th>");
    })
    var tbody = $("<tbody></tbody>");
    // listTable.append(thead).append(theadSearch).append(tbody);
    listTable.append(thead).append(tbody);
    $("#tableDiv").append(listTable);
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
            $.each(rs.orderDetailArray, function (index, obj) {
                var tr = $("<tr></tr>");
                var tds = [obj.dingdanhao, obj.xu, '', obj.bianhao, obj.pinming, obj.guige, obj.shuliang, obj.danjia, obj.jine, obj.beizhu];
                $.each(tds, function (index_2, obj_2) {
                    obj_2 = obj_2 ? obj_2 : "";
                    var tdContent = $("<span></span>");
                    tdContent.text(obj_2);
                    if(index_2 == 2){
                        tdContent = $("<select iid='productId'></select>");
                    }
                    var td = $("<td></td>");
                    td.attr("column", theadNames[index_2]);
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

            var datatable = $('#listTable').DataTable({
                "bJQueryUI": true,
                "sPaginationType": "full_numbers",
                "ordering": false,
                "bSort": false,
                "language": $.dataTablesLanguage,
                "pageLength": 100
            });

            // theadSearch.find('input').css("width", "100%");
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

function showDetail(){
    console.log("showDetail: " + detailId);
}

function saveDetail(action){
    console.log("saveDetail: " + action);
}