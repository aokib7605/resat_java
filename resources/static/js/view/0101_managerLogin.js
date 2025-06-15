$(function () {
    $(function () {
        if (window.matchMedia(commonElement.responsiveWidth).matches) {
            $(managerLoginElement.centerContainer).append("\
                <h1 th:text='${title}'></h1>\
                ")
        } else {
            // PC時の処理
        };
    });

    $(window).resize(function () {
        if (window.matchMedia(commonElement.responsiveWidth).matches) {
            $(managerLoginElement.centerContainer).append("\
                <h1 th:text='${title}'></h1>\
                ")
        } else {
            // PC時の処理
        };
    });

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
                <input type="text" name="userId">
            `);
            } else if (method === 'mail') {
                $inputVal.html(`
                メールアドレス<br>
                <input type="text" name="userMail">
            `);
            } else {
                $inputVal.empty(); // どちらでもない場合は空に
            }
        }
    });

});