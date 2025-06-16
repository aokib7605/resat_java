$(function () {
    handleResponsiveView();
    $(window).on('load resize', handleResponsiveView);

    function handleResponsiveView() {
        if (window.matchMedia(commonElement.responsiveWidth).matches) {
            // モバイル時の処理
            // PC時の処理（h1, h2削除）
            // $(managerLoginElement.h1).removeClass('hidden');
            // $(managerLoginElement.h2).removeClass('hidden');
        } else {
            // PC時の処理（h1, h2削除）
            // $(managerLoginElement.h1).addClass("hidden");
            // $(managerLoginElement.h2).addClass("hidden");
        }
    }
});