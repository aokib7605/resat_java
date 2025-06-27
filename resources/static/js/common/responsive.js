$(function () {

    $(function () {
        if (window.matchMedia(commonElement.responsiveWidth).matches) {
            $(commonElement.startDiv).addClass("mobile");
            $(commonElement.startDiv).removeClass("pc");
        } else {
            $(commonElement.startDiv).addClass("pc");
            $(commonElement.startDiv).removeClass("mobile");
        };
    });

    $(window).resize(function () {
        if (window.matchMedia(commonElement.responsiveWidth).matches) {
            $(commonElement.startDiv).addClass("mobile");
            $(commonElement.startDiv).removeClass("pc");
        } else {
            $(commonElement.startDiv).addClass("pc");
            $(commonElement.startDiv).removeClass("mobile");
        };
    });

    $(function () {
        if (window.matchMedia(commonElement.responsiveWidth).matches) {
            $(commonElement.custPage).addClass("mobile");
            $(commonElement.custPage).removeClass("pc");
        } else {
            $(commonElement.custPage).addClass("pc");
            $(commonElement.custPage).removeClass("mobile");
        };
    });

    $(window).resize(function () {
        if (window.matchMedia(commonElement.responsiveWidth).matches) {
            $(commonElement.custPage).addClass("mobile");
            $(commonElement.custPage).removeClass("pc");
        } else {
            $(commonElement.custPage).addClass("pc");
            $(commonElement.custPage).removeClass("mobile");
        };
    });
});