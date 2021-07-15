;
$(document).ready(function(){
    $(".datetimepicker").datetimepicker({
        todayButton: true,
        timepicker:false,
        format:'Y-m-d'
    });
});


$.drawContentForm = function(contentForm, itemArray){
    console.log("drawContentForm(): " + contentForm.attr("id"));
    console.log(itemArray);
    for(var index0 in itemArray){
        var item = itemArray[index0];
        var formGroupDiv = $('<div class="form-group col-xs-12">');
        var label = $('<label class="col-xs-4 control-label text-right"></label>');
        label.text(item.label);
        if(item.required){
            var requiredSpan = $('<span class="required">*</span>');
            label.append(requiredSpan);
        }
        var inputDiv = $('<div class="col-xs-8">');
        var inputItem;
        switch(item.inputType){
            case "text":
                inputItem = $('<input class="form-control"/>');
                break;
            case "select":
                inputItem = $('<select class="form-control" ></select>');
                var itemOptionArray = item.array;
                console.log(item.label);
                console.log(item.array);
                for(var index1 in itemOptionArray){
                    var itemOption = itemOptionArray[index1];
                    var itemOptionOption = $('<option></option>');
                    itemOptionOption.val(itemOption.value);
                    itemOptionOption.text(itemOption.label);
                    inputItem.append(itemOptionOption);
                }
                break;
            default:
                inputItem = $('<input class="form-control" />');
                break;
        }
        inputItem.attr("pid", item.pid);
        if(item.required){
            inputItem.attr("required", "required");
        }
        if(item.readonly){
            inputItem.attr("readonly", "readonly");
        }
        if(item.maxlength){
            inputItem.attr("maxlength", item.maxlength);
        }
        if(item.minlength){
            inputItem.attr("minlength", item.minlength);
        }
        if(item.updateForbidden){
            inputItem.addClass("updateForbidden");
        }
        inputDiv.append(inputItem);
        formGroupDiv.append(label).append(inputDiv);
        contentForm.find('.box-body').append(formGroupDiv);
    }
}