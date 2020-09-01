;
$(document).ready(function(){
    var fnsku1 = "111222";
    var fnsku2 = "bbbccc";
    var fnsku3 = "111333";
    var fnsku4 = "bbbccc";
    var fnsku5 = "111222";
    //
    var iframe1 = $("<iframe width='100%' height='100%'></iframe>");
    iframe1.addClass("pdfIframe")
    iframe1.attr("src", "http://localhost:10086/erp/fnsku/" + fnsku1 + ".pdf");
    $("#testDiv").append(iframe1);
    //
    var iframe2 = $("<iframe width='100%' height='100%'></iframe>");
    iframe2.addClass("pdfIframe")
    iframe2.attr("src", "http://localhost:10086/erp/fnsku/" + fnsku2 + ".pdf");
    $("#testDiv").append(iframe2);
    //
    var iframe3 = $("<iframe width='100%' height='100%'></iframe>");
    iframe3.addClass("pdfIframe")
    iframe3.attr("src", "http://localhost:10086/erp/fnsku/" + fnsku3 + ".pdf");
    $("#testDiv").append(iframe3);
});

function test(){
    console.log("Test");
}
