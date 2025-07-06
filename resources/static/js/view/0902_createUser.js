// お客様ユーザー作成画面のjsです

$(function () {

    // メール「確認」ボタン押下時の処理
    $(createUserElement.confiMail).on("click", function () {

        const form = document.getElementById("confiMailSubmit");
        if (!form.checkValidity()) {
            // 無効な場合 → ブラウザ標準の警告を表示
            form.reportValidity(); // ポップアップで警告を出す
            return;
        }

        // フォーム送信
        $(createUserElement.mailBox).append("<p class='alertText'>少々お待ちください</p>");
        $(createUserElement.mailBox).submit();
    })

    // 「ユーザーID入力欄」変化時
    $(createUserElement.inputUserId).on("input", function () {
        const userId = $(this).val();

        // ✅ 条件: 半角英字 + 半角数字 含む & ASCII記号含めて8文字以上
        const isValid = /^[\x21-\x7E]{8,}$/.test(userId) &&
            /[A-Za-z]/.test(userId) &&
            /[0-9]/.test(userId);

        const $submitBox = $(createUserElement.userIdBox);
        const $button = $(createUserElement.confiUserId);
        const $btnText = $(createUserElement.btnText);

        // 既存のメッセージ削除
        $submitBox.find(".alertText").remove();

        if (!isValid) {
            // エラー文言追加（まだ存在していなければ）
            $submitBox.append("<p class='alertText'>半角英字と半角数字を含めた8文字以上で入力してください</p>");
            $button.addClass("disabled").attr("disabled", true);
            $btnText.addClass("disableBtnText").attr("disableBtnText", true);
        } else {
            // 条件クリア → ボタン活性化
            $button.removeClass("disabled").removeAttr("disabled");
            $btnText.removeClass("disableBtnText").removeAttr("disableBtnText");
        }
    });

    // 「ユーザーPASS入力欄」変化時
    $(createUserElement.inputUserPass).on("input", function () {
        const userId = $(this).val();

        // ✅ 条件: 半角英字 + 半角数字 含む & ASCII記号含めて8文字以上
        const isValid = /^[\x21-\x7E]{8,}$/.test(userId) &&
            /[A-Za-z]/.test(userId) &&
            /[0-9]/.test(userId);

        const $submitBox = $(createUserElement.userPassBox);
        const $button = $(createUserElement.confiUserPass);
        const $btnText = $(createUserElement.btnText);

        // 既存のメッセージ削除
        $submitBox.find(".alertText").remove();

        if (!isValid) {
            // エラー文言追加（まだ存在していなければ）
            $submitBox.append("<p class='alertText'>半角英字と半角数字を含めた8文字以上で入力してください</p>");
            $button.addClass("disabled").attr("disabled", true);
            $btnText.addClass("disableBtnText").attr("disableBtnText", true);
        } else {
            // 条件クリア → ボタン活性化
            $button.removeClass("disabled").removeAttr("disabled");
            $btnText.removeClass("disableBtnText").removeAttr("disableBtnText");
        }
    });
})