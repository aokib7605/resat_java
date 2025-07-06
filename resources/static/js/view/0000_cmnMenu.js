$(function () {
    handleResponsiveView();
    $(window).on('load resize', handleResponsiveView);

    function handleResponsiveView() {
        if (window.matchMedia(commonElement.responsiveWidth).matches) {
            // モバイル時の処理
            $(managerMenuElement.managerMenu).addClass('mobile');
            $(managerMenuElement.managerMenu).removeClass('pc');
            $(commonElement.hamburger_overlay).removeClass('hidden');
            $(commonElement.navContent).removeClass('hidden');
            $(commonElement.menuContent).addClass('hidden');
        } else {
            // PC時の処理
            $(managerMenuElement.managerMenu).addClass('pc');
            $(managerMenuElement.managerMenu).removeClass('mobile');
            $(commonElement.hamburger_overlay).addClass("hidden");
            $(commonElement.navContent).addClass('hidden');
            $(commonElement.menuContent).removeClass('hidden');
        }
    }

    // 公演メニューのトグル機能
    $(managerMenuElement.stage).on("click", function () {
        if ($(managerMenuElement.stageMenu).hasClass('hidden') === true) {
            $(managerMenuElement.stageMenu).removeClass('hidden');
        } else {
            $(managerMenuElement.stageMenu).addClass('hidden');
        }
    });

    // 票券管理メニューのトグル機能
    $(managerMenuElement.manage).on("click", function () {
        if ($(managerMenuElement.manageMenu).hasClass('hidden') === true) {
            $(managerMenuElement.manageMenu).removeClass('hidden');
        } else {
            $(managerMenuElement.manageMenu).addClass('hidden');
        }
    });

    // 便利機能メニューのトグル機能
    $(managerMenuElement.tool).on("click", function () {
        if ($(managerMenuElement.toolMenu).hasClass('hidden') === true) {
            $(managerMenuElement.toolMenu).removeClass('hidden');
        } else {
            $(managerMenuElement.toolMenu).addClass('hidden');
        }
    });

    // 団体メニューのトグル機能
    $(managerMenuElement.group).on("click", function () {
        if ($(managerMenuElement.groupMenu).hasClass('hidden') === true) {
            $(managerMenuElement.groupMenu).removeClass('hidden');
        } else {
            $(managerMenuElement.groupMenu).addClass('hidden');
        }
    });

    // ユーザーメニューのトグル機能
    $(managerMenuElement.user).on("click", function () {
        if ($(managerMenuElement.userMenu).hasClass('hidden') === true) {
            $(managerMenuElement.userMenu).removeClass('hidden');
        } else {
            $(managerMenuElement.userMenu).addClass('hidden');
        }
    });
});