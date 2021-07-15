
$(function () {
    $.cacheProducts = {};
    $.cacheStores = {};
    App.setbasePath("../");
    addTabs({
        id: '10008',
        title: '欢迎页',
        close: true,
        url: 'welcome',
        urlType: "relative"
    });

    App.fixIframeCotent();
    var menus = [
        {
            id: "9010",
            text: "管理页面",
            // roles: "ROLE_Mgt_UserManagement_Read, ROLE_Mgt_UserManagement_Write, ROLE_Mgt_GroupManagement_Read, ROLE_Mgt_GroupManagement_Write, ROLE_Mgt_KeyConfig_Read, ROLE_Mgt_KeyConfig_Write",
            icon: "fa fa-th-list",
            children: [
                {
                    id: "901010",
                    text: "用户配置",
                    // roles: "ROLE_Mgt_UserManagement_Read, ROLE_Mgt_UserManagement_Write",
                    icon: "fa fa-user",
                    url: "userManagement",
                    targetType: "iframe-tab"
                },
                {
                    id: "901020",
                    text: "用户组配置",
                    // roles: "ROLE_Mgt_GroupManagement_Read, ROLE_Mgt_GroupManagement_Write",
                    icon: "fa fa-umbrella",
                    url: "groupManagement",
                    targetType: "iframe-tab"
                },
                {
                    id: "901030",
                    text: "操作日志",
                    // roles: "ROLE_Mgt_KeyConfig_Read, ROLE_Mgt_KeyConfig_Write",
                    icon: "fa fa-cloud",
                    url: "accesslog",
                    targetType: "iframe-tab"
                },
            ]
        },
        {
            id: "9020",
            text: "产品管理",
            roles: "ROLE_Product",
            icon: "fa fa-product-hunt",
            url: "product",
            targetType: "iframe-tab"
        },
        {
            id: "9030",
            text: "SKU",
            roles: "ROLE_Product",
            icon: "fa fa-star-half-o",
            url: "sku",
            targetType: "iframe-tab"
        },
        {
            id: "9040",
            text: "FBA管理",
            roles: "ROLE_Shipment",
            icon: "fa fa-amazon",
            url: "shipment",
            targetType: "iframe-tab"
        },
        {
            id: "9045",
            text: "海外仓管理",
            roles: "ROLE_Shipment",
            icon: "fa fa-anchor",
            url: "oversea",
            targetType: "iframe-tab"
        },
        {
            id: "9050",
            text: "采购管理",
            roles: "ROLE_Shipment",
            icon: "fa fa-shopping-cart",
            url: "purchase",
            targetType: "iframe-tab"
        },
        {
            id: "9060",
            text: "小包管理",
            roles: "ROLE_Shipment",
            icon: "fa fa-truck",
            url: "packet",
            targetType: "iframe-tab"
        },
        // {
        //     id: "9070",
        //     text: "计划管理",
        //     roles: "ROLE_Shipment",
        //     icon: "fa fa-hand-paper-o",
        //     url: "plan",
        //     targetType: "iframe-tab"
        // },
        {
            id: "9080",
            text: "数据上传",
            roles: "ROLE_Excel",
            icon: "fa fa-cloud-upload",
            url: "excel",
            targetType: "iframe-tab"
        // },
        // {
        //     id: "9100",
        //     text: "库存查看",
        //     roles: "",
        //     icon: "fa fa-balance-scale",
        //     url: "inventory",
        //     targetType: "iframe-tab"
        }
    ];
    $('.sidebar-menu').sidebarMenu({data: menus});

    $.refreshCacheProducts();
    $.refreshCacheStores();
});

$.refreshCacheProducts = function () {
    console.log("home.js.refreshCacheProducts()");
    var data = {};
    $.ajax({
        type: "POST",
        url: "product/refreshCacheProducts",
        data: data,
        dataType: "json",
        success: function (rs) {
            $.cacheProducts = rs;
            console.log("$.cacheProducts");
            console.log($.cacheProducts);
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            $.showErrorModal(XMLHttpRequest.responseText);
            console.log("error");
        },
        complete: function () {
        }
    });
}

$.refreshCacheStores = function () {
    console.log("home.js.refreshCacheStores()");
    var data = {};
    $.ajax({
        type: "POST",
        url: "product/refreshCacheStores",
        data: data,
        dataType: "json",
        success: function (rs) {
            $.cacheStores = rs;
            console.log("$.cacheStores");
            console.log($.cacheStores);
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            $.showErrorModal(XMLHttpRequest.responseText);
            console.log("error");
        },
        complete: function () {
        }
    });
}

$.refreshProductsSelect = function($select){
    console.log("home.js.refreshProductsSelect()");
    var data = [];
    for(key in $.cacheProducts){
        var optionObj = {};
        optionObj.id = key.substr(2);
        optionObj.text = $.cacheProducts[key].snname;
        data.push(optionObj);
    }

    $select.select2({
        data: data,
        placeholder:'请选择',
        allowClear:true,
        width: "100%"
    })
}

$.showProductNameGroupByProductIdGroup = function(productIdGroupString){
    console.log("home.js.showProductNameGroupByProductIdGroup(): " + productIdGroupString);
    var productNameGroup = [];
    if(!productIdGroupString){
        return productNameGroup;
    }
    var productIdGroup = productIdGroupString.split(",");
    $.each(productIdGroup, function (index, obj) {
        var productName = $.cacheProducts["id" + obj].snname;
        if($.inArray(productName,productNameGroup) < 0){
            productNameGroup.push(productName);
        }
    })
    return productNameGroup;
}

$.retrieveStoreName = function(storeId){
    console.log("retrieveStoreName(): " + storeId);
    var storeName = $.cacheStores["id" + storeId] ? $.cacheStores["id" + storeId].name + " " : "";
    return storeName;
}