$(function () {
    // 「確定」ボタン押下時の処理
    $(commonElement.submitBtn).on("click", function () {

        const form = document.getElementById("causeLoadingForm");
        if (!form.checkValidity()) {
            // 無効な場合 → ブラウザ標準の警告を表示
            form.reportValidity(); // ポップアップで警告を出す
            return;
        }

        // ボタンの多重クリックを防止
        $(this).prop("disabled", true);

        // フォーム送信
        $(commonElement.messageText).removeClass("hidden");
        $(commonElement.causeLoadingForm).submit();
    });
});