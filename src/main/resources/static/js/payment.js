var itemId = 0;
var $itemForm = $("#itemForm");
var $searchForm = $("#searchForm");
var ajaxCtx = 'payment/';
var parentJs = parent;
var theadNames = ['id', '日期', '收支', '科目', '分类', '店铺', '金额', '备注'];
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

    $("#search").click(function () {
        showList();
    });

    $("#search_reset").click(function () {
        $searchForm.find("[sid]").each(function () {
            $(this).val("");
        });
        showList();
    });

    $(".datetimepicker").datetimepicker({
        todayButton: true,
        timepicker:false,
        format:'Y-m-d'
    });

    $(".dtPicker.dtPickerFrom").datetimepicker({
        format: 'Y-m-d H:i:s',
        defaultTime: '00:00:00'
    });

    $(".dtPicker.dtPickerTo").datetimepicker({
        format: 'Y-m-d H:i:s',
        defaultTime: '23:59:59'
    });
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
    var data = searchFormCollect();
    var ajaxUrl = 'search'
    $.ajax({
        type: "POST",
        url: ajaxCtx + ajaxUrl,
        data: JSON.stringify(data),
        dataType: "json",
        contentType: "application/json",
        success: function (rs) {
            console.log(rs);
            createListTableBody(table, rs);
            createDataTable(table, {"order": 1});
            draw(rs);
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
    var listData = rs.rows || rs.array || [];
    $.each(listData, function (index, obj) {
        var tr = $("<tr></tr>");
        var tds = [
            obj.id,
            obj.transactionDate,
            obj.inex,
            obj.transactionSubject,
            obj.category1st,
            obj.transactionStore,
            obj.transactionAmount,
            obj.remark
        ];
        if(obj.imageCount > 0){
            tr.addClass("hasImage");
        }
        $.each(tds, function (index_2, obj_2) {
            obj_2 = obj_2 ? obj_2 : "";
            var td = $("<td>" + obj_2 + "</td>");
            td.attr("columnName", theadNames[index_2]);
            if(obj.inex == '收'){
                tr.addClass("inex-income");
            } else if(obj.inex == '支'){
                if(obj.transactionSubject == '转英'){
                    tr.addClass("inex-expense-transfer");
                } else {
                    tr.addClass("inex-expense");
                }
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
    itemArray[++i] = {"label": "图片数", "pid": "imageCount", "required": true, "inputType": "text"};
    itemArray[++i] = {"label": "备注", "pid": "remark", "inputType": "text"};
    $.drawContentForm($itemForm, itemArray);
}

function draw(rs) {
    console.log("draw charts");
    $("#chartBox").empty();
    var listData = rs.rows || rs.array || [];
    console.log("listData:", listData);

    if (!listData || listData.length === 0) {
        console.log("No data available");
        $("#chartBox").html('<div class="col-md-12"><div class="alert alert-info">暂无数据</div></div>');
        return;
    }

    var incomeData = {};  // 收入 - 按 transactionStore 分组
    var expenseData = {};  // 支出 - 按 transactionSubject 分组
    var expenseExcludeTransfer = {};  // 支出 - 除转英外
    var monthlySummary = {};  // 月度汇总 - 总收入、总支出、总结余

    $.each(listData, function (index, item) {
        var inex = item.inex || '';
        if (inex !== '收' && inex !== '支') {
            return;
        }

        var month = item.transactionDate ? item.transactionDate.substring(0, 7) : '';
        if (!month) {
            return;
        }

        var amount = parseFloat(item.transactionAmount) || 0;

        if (inex === '收') {
            // 收入：按 transactionStore 分组
            var store = item.transactionStore || '其他';
            if (!incomeData[store]) {
                incomeData[store] = {};
            }
            if (!incomeData[store][month]) {
                incomeData[store][month] = 0;
            }
            incomeData[store][month] += amount;

            // 月度汇总 - 收入
            if (!monthlySummary[month]) {
                monthlySummary[month] = { income: 0, expense: 0, expenseExcludeTransfer: 0, expenseTransfer: 0, balance: 0 };
            }
            monthlySummary[month].income += amount;

        } else {
            // 支出：按 transactionSubject 分组，转为正数
            var subject = item.transactionSubject || '其他';
            var absAmount = Math.abs(amount);

            if (!expenseData[subject]) {
                expenseData[subject] = {};
            }
            if (!expenseData[subject][month]) {
                expenseData[subject][month] = 0;
            }
            expenseData[subject][month] += absAmount;

            // 月度汇总 - 支出
            if (!monthlySummary[month]) {
                monthlySummary[month] = { income: 0, expense: 0, expenseExcludeTransfer: 0, expenseTransfer: 0, balance: 0 };
            }
            monthlySummary[month].expense += absAmount;

            // 分类：转英支出 vs 其他支出
            if (subject === '转英') {
                monthlySummary[month].expenseTransfer += absAmount;
            } else {
                if (!expenseExcludeTransfer[subject]) {
                    expenseExcludeTransfer[subject] = {};
                }
                if (!expenseExcludeTransfer[subject][month]) {
                    expenseExcludeTransfer[subject][month] = 0;
                }
                expenseExcludeTransfer[subject][month] += absAmount;
                monthlySummary[month].expenseExcludeTransfer += absAmount;
            }
        }
    });

    // 计算每月结余
    var sortedMonths = Object.keys(monthlySummary).sort();
    var runningBalance = 0;
    $.each(sortedMonths, function (index, month) {
        var summary = monthlySummary[month];
        summary.balance = summary.income - summary.expense;
        runningBalance += summary.balance;
    });

    console.log("incomeData:", incomeData);
    console.log("expenseData:", expenseData);
    console.log("expenseExcludeTransfer:", expenseExcludeTransfer);
    console.log("monthlySummary:", monthlySummary);

    // 1. 总收入图表 - 独占一行
    var incomeStores = Object.keys(incomeData);
    if (incomeStores.length > 0) {
        var summaryIncomeChartId = 'chart_total_income';
        var seriesData = [];
        var totalIncomeCount = 0;
        var totalIncomeAmount = 0;
        $.each(sortedMonths, function (i, month) {
            var summary = monthlySummary[month];
            seriesData.push(summary.income);
            totalIncomeAmount += summary.income;
        });

        // 计算收入笔数
        $.each(listData, function (index, item) {
            if (item.inex === '收') {
                totalIncomeCount++;
            }
        });

        var incomeChartHtml = '<div class="col-md-12">' +
            '<div class="box box-success">' +
            '<div class="box-header with-border">' +
            '<h3 class="box-title">总收入</h3>' +
            '</div>' +
            '<div class="box-body">' +
            '<div id="' + summaryIncomeChartId + '"></div>' +
            '</div>' +
            '</div>' +
            '</div>';

        $("#chartBox").append(incomeChartHtml);

        Highcharts.chart(summaryIncomeChartId, {
            chart: {
                type: 'line'
            },
            title: {
                text: '月度总收入 （共' + totalIncomeCount + '笔，总计' + totalIncomeAmount.toFixed(2) + '）'
            },
            xAxis: {
                categories: sortedMonths,
                title: {
                    text: '月份'
                }
            },
            yAxis: {
                title: {
                    text: '金额'
                }
            },
            tooltip: {
                valuePrefix: ''
            },
            plotOptions: {
                line: {
                    dataLabels: {
                        enabled: true,
                        format: '{point.y:.2f}',
                        style: {
                            color: '#00a65a',
                            fontWeight: 'bold'
                        }
                    }
                }
            },
            series: [{
                name: '总收入',
                data: seriesData,
                color: '#00a65a'
            }]
        });
    }

    // 2. 总支出图表（总支出、转英支出、除转英外的支出）- 独占一行
    var summaryExpenseChartId = 'chart_total_expense';
    var totalExpenseCount = 0;
    var totalExpenseAmount = 0;

    // 计算支出笔数和总金额
    $.each(listData, function (index, item) {
        if (item.inex === '支') {
            totalExpenseCount++;
            totalExpenseAmount += Math.abs(parseFloat(item.transactionAmount) || 0);
        }
    });

    var expenseChartHtml = '<div class="col-md-12">' +
        '<div class="box box-danger">' +
        '<div class="box-header with-border">' +
        '<h3 class="box-title">总支出</h3>' +
        '</div>' +
        '<div class="box-body">' +
        '<div id="' + summaryExpenseChartId + '"></div>' +
        '</div>' +
        '</div>' +
        '</div>';

    $("#chartBox").append(expenseChartHtml);

    Highcharts.chart(summaryExpenseChartId, {
        chart: {
            type: 'line'
        },
        title: {
            text: '月度总支出 （共' + totalExpenseCount + '笔，总计' + totalExpenseAmount.toFixed(2) + '）'
        },
        xAxis: {
            categories: sortedMonths,
            title: {
                text: '月份'
            }
        },
        yAxis: {
            title: {
                text: '金额'
            }
        },
        tooltip: {
            valuePrefix: ''
        },
        plotOptions: {
            line: {
                dataLabels: {
                    enabled: true,
                    format: '{point.y:.2f}'
                }
            }
        },
        series: [{
            name: '总支出',
            data: sortedMonths.map(function(month) {
                return monthlySummary[month].expense;
            }),
            color: '#dd4b39'
        }, {
            name: '转英支出',
            data: sortedMonths.map(function(month) {
                return monthlySummary[month].expenseTransfer;
            }),
            color: '#f39c12'
        }, {
            name: '除转英外支出',
            data: sortedMonths.map(function(month) {
                return monthlySummary[month].expenseExcludeTransfer;
            }),
            color: '#9c27b0'
        }]
    });

    // 3. 总结余图表 - 独占一行
    var summaryBalanceChartId = 'chart_monthly_balance';
    var balanceData = [];
    var cumulativeBalance = 0;
    $.each(sortedMonths, function (i, month) {
        var summary = monthlySummary[month];
        balanceData.push(summary.balance);
        cumulativeBalance += summary.balance;
    });

    var totalCount = totalIncomeCount + totalExpenseCount;

    var balanceChartHtml = '<div class="col-md-12">' +
        '<div class="box box-primary">' +
        '<div class="box-header with-border">' +
        '<h3 class="box-title">月度结余</h3>' +
        '</div>' +
        '<div class="box-body">' +
        '<div id="' + summaryBalanceChartId + '"></div>' +
        '</div>' +
        '</div>' +
        '</div>';

    $("#chartBox").append(balanceChartHtml);

    Highcharts.chart(summaryBalanceChartId, {
        chart: {
            type: 'line'
        },
        title: {
            text: '月度结余 （共' + totalCount + '笔，累计结余' + cumulativeBalance.toFixed(2) + '）'
        },
        xAxis: {
            categories: sortedMonths,
            title: {
                text: '月份'
            }
        },
        yAxis: {
            title: {
                text: '金额'
            }
        },
        tooltip: {
            valuePrefix: ''
        },
        plotOptions: {
            line: {
                dataLabels: {
                    enabled: true,
                    format: '{point.y:.2f}',
                    style: {
                        color: '#3c8dbc',
                        fontWeight: 'bold'
                    }
                }
            }
        },
        series: [{
            name: '结余',
            data: balanceData,
            color: '#3c8dbc'
        }]
    });

    // 4. 收入分店铺图表 - 每个店铺一个图表
    if (incomeStores.length > 0) {
        $.each(incomeStores, function (index, store) {
            var chartId = 'chart_income_store_' + store.replace(/\s+/g, '_').replace(/[^a-zA-Z0-9\u4e00-\u9fa5_-]/g, '') + '_' + index;
            var seriesData = [];
            var storeTotalAmount = 0;
            var storeCount = 0;
            $.each(sortedMonths, function (i, month) {
                var amount = incomeData[store][month] || 0;
                seriesData.push(amount);
                storeTotalAmount += amount;
            });

            // 计算该店铺的收入笔数
            $.each(listData, function (idx, item) {
                if (item.inex === '收' && (item.transactionStore || '其他') === store) {
                    storeCount++;
                }
            });

            var chartHtml = '<div class="col-md-12">' +
                '<div class="box box-success">' +
                '<div class="box-header with-border">' +
                '<h3 class="box-title">' + store + ' - 收入</h3>' +
                '</div>' +
                '<div class="box-body">' +
                '<div id="' + chartId + '"></div>' +
                '</div>' +
                '</div>' +
                '</div>';

            $("#chartBox").append(chartHtml);

            Highcharts.chart(chartId, {
                chart: {
                    type: 'line'
                },
                title: {
                    text: store + ' - 月度收入 （共' + storeCount + '笔，总计' + storeTotalAmount.toFixed(2) + '）'
                },
                xAxis: {
                    categories: sortedMonths,
                    title: {
                        text: '月份'
                    }
                },
                yAxis: {
                    title: {
                        text: '金额'
                    }
                },
                tooltip: {
                    valuePrefix: ''
                },
                plotOptions: {
                    line: {
                        dataLabels: {
                            enabled: true,
                            format: '{point.y:.2f}'
                        }
                    }
                },
                series: [{
                    name: '收入',
                    data: seriesData,
                    color: '#00a65a'
                }]
            });
        });
    }

    // 5. 支出分科目图表 - 每个科目一个图表
    var expenseSubjects = Object.keys(expenseData);
    if (expenseSubjects.length > 0) {
        $.each(expenseSubjects, function (index, subject) {
            var chartId = 'chart_expense_subject_' + subject.replace(/\s+/g, '_').replace(/[^a-zA-Z0-9\u4e00-\u9fa5_-]/g, '') + '_' + index;
            var seriesData = [];
            var subjectTotalAmount = 0;
            var subjectCount = 0;
            $.each(sortedMonths, function (i, month) {
                var amount = expenseData[subject][month] || 0;
                seriesData.push(amount);
                subjectTotalAmount += amount;
            });

            // 计算该科目的支出笔数
            $.each(listData, function (idx, item) {
                if (item.inex === '支' && (item.transactionSubject || '其他') === subject) {
                    subjectCount++;
                }
            });

            var chartHtml = '<div class="col-md-12">' +
                '<div class="box box-danger">' +
                '<div class="box-header with-border">' +
                '<h3 class="box-title">' + subject + ' - 支出</h3>' +
                '</div>' +
                '<div class="box-body">' +
                '<div id="' + chartId + '"></div>' +
                '</div>' +
                '</div>' +
                '</div>';

            $("#chartBox").append(chartHtml);

            Highcharts.chart(chartId, {
                chart: {
                    type: 'line'
                },
                title: {
                    text: subject + ' - 月度支出 （共' + subjectCount + '笔，总计' + subjectTotalAmount.toFixed(2) + '）'
                },
                xAxis: {
                    categories: sortedMonths,
                    title: {
                        text: '月份'
                    }
                },
                yAxis: {
                    title: {
                        text: '金额'
                    }
                },
                tooltip: {
                    valuePrefix: ''
                },
                plotOptions: {
                    line: {
                        dataLabels: {
                            enabled: true,
                            format: '{point.y:.2f}'
                        }
                    }
                },
                series: [{
                    name: '支出',
                    data: seriesData,
                    color: '#dd4b39'
                }]
            });
        });
    }
}

function searchFormCollect(){
    console.log("searchFormCollect()");
    var data = {};
    $searchForm.find("[sid]").each(function () {
        data[$(this).attr("sid")] = $(this).val();
    });
    console.log(data);
    return data;
}
