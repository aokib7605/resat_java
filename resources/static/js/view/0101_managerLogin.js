$(function () {
    $(window).on('load resize', handleResponsiveView);

    function handleResponsiveView() {
        if (window.matchMedia(commonElement.responsiveWidth).matches) {
            // モバイル時の処理
            // PC時の処理（h1, h2削除）
            $(managerLoginElement.h1).removeClass('hidden');
            $(managerLoginElement.h2).removeClass('hidden');
        } else {
            // PC時の処理（h1, h2削除）
            $(managerLoginElement.h1).addClass("hidden");
            $(managerLoginElement.h2).addClass("hidden");
        }
    }

    $(document).ready(function () {
        // 初期表示時に切り替え（必要に応じて）
        toggleInputBox($(managerLoginElement.loginMethod).val());

        // セレクトボックス変更時のイベント
        $(managerLoginElement.loginMethod).on('change', function () {
            const selected = $(this).val();
            toggleInputBox(selected);
        });

        function toggleInputBox(method) {
            const $inputVal = $(managerLoginElement.inputVal);
            if (method === 'id') {
                $inputVal.html(`
                ユーザーID<br>
                <input type="text" name="userId" required>
            `);
            } else if (method === 'mail') {
                $inputVal.html(`
                メールアドレス<br>
                <input type="email" name="userMail" required>
            `);
            } else {
                $inputVal.empty(); // どちらでもない場合は空に
            }
        }
    });

});