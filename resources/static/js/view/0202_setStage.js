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

    $(document).ready(function () {
        // セレクトボックス変更時のイベント
        $('form').on('submit', function (e) {

            // 会場選択プルダウンで選択中の値を取得
            const selected = $(setStageElement.stagePlaceName).val();

            // 上記以外を選択している場合
            if (selected === "otherEX") {
                // フォーム送信を無効化
                e.preventDefault();

                // 選択プルダウンを非表示
                $(this).addClass("hidden")

                // 会場新規入力フォームを表示
                $(setStageElement.newStagePlaceName).removeClass("hidden");

                // 選択中の値をクリア（フォーム送信有効化）
                $(setStageElement.stagePlaceName).val("");
            }

        });
    })
});