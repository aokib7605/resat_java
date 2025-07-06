$(function () {

    $(function () {
        if (window.matchMedia(commonElement.responsiveWidth).matches) {
            $(commonElement.startDiv).addClass("mobile");
            $(commonElement.startDiv).removeClass("pc");
            $(commonElement.mobileMessage).removeClass("hidden");
        } else {
            $(commonElement.startDiv).addClass("pc");
            $(commonElement.startDiv).removeClass("mobile");
            $(commonElement.mobileMessage).addClass("hidden");
        };
    });

    $(window).resize(function () {
        if (window.matchMedia(commonElement.responsiveWidth).matches) {
            $(commonElement.startDiv).addClass("mobile");
            $(commonElement.startDiv).removeClass("pc");
            $(commonElement.mobileMessage).removeClass("hidden");
        } else {
            $(commonElement.startDiv).addClass("pc");
            $(commonElement.startDiv).removeClass("mobile");
            $(commonElement.mobileMessage).addClass("hidden");
        };
    });

    $(function () {
        if (window.matchMedia(commonElement.responsiveWidth).matches) {
            $(commonElement.custPage).addClass("mobile");
            $(commonElement.custPage).removeClass("pc");
            $(commonElement.mobileMessage).removeClass("hidden");
        } else {
            $(commonElement.custPage).addClass("pc");
            $(commonElement.custPage).removeClass("mobile");
            $(commonElement.mobileMessage).addClass("hidden");
        };
    });

    $(window).resize(function () {
        if (window.matchMedia(commonElement.responsiveWidth).matches) {
            $(commonElement.custPage).addClass("mobile");
            $(commonElement.custPage).removeClass("pc");
            $(commonElement.mobileMessage).removeClass("hidden");
        } else {
            $(commonElement.custPage).addClass("pc");
            $(commonElement.custPage).removeClass("mobile");
            $(commonElement.mobileMessage).addClass("hidden");
        };
    });
});