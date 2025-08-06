$(function () {
    // 画面サイズに応じて画面要素に適切なクラスを付与する
    function applyResponsiveClass(target) {
        if (window.matchMedia(commonElement.responsiveWidth).matches) {
            // モバイルの場合
            $(target).addClass("mobile").removeClass("pc");
            $(commonElement.mobileMessage).removeClass("hidden");
            $(commonElement.leftMenu).addClass("hidden");
            $(commonElement.logoutBtn).removeClass("hidden");
            $(commonElement.managerMenu).removeClass("managerMenu");
            $(commonElement.header).removeClass("hidden");
            $(commonElement.footer).removeClass("hidden");
            $(commonElement.overLayMenu).removeClass("hidden");
            $(commonElement.hamburger_overlay).removeClass('hidden');
            $(commonElement.navContent).removeClass('hidden');
            $(commonElement.menuContent).addClass('hidden');
            $(commonElement.logoutBtn).addClass("hidden");
        } else {
            // PCの場合
            $(target).addClass("pc").removeClass("mobile");
            $(commonElement.mobileMessage).addClass("hidden");
            $(commonElement.leftMenu).removeClass("hidden");
            $(commonElement.logoutBtn).addClass("hidden");
            $(commonElement.managerMenu).addClass("managerMenu");
            $(commonElement.header).addClass("hidden");
            $(commonElement.footer).addClass("hidden");
            $(commonElement.overLayMenu).addClass("hidden");
            $(commonElement.hamburger_overlay).addClass("hidden");
            $(commonElement.navContent).addClass('hidden');
            $(commonElement.menuContent).removeClass('hidden');
            $(commonElement.logoutBtn).removeClass("hidden");
        }
    }

    // 画面初期表示時に実行
    applyResponsiveClass(commonElement.startDiv);
    applyResponsiveClass(commonElement.custPage);

    // 画面リサイズ時にも実行
    $(window).resize(function () {
        applyResponsiveClass(commonElement.startDiv);
        applyResponsiveClass(commonElement.custPage);
    });
});
