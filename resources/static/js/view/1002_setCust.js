$(function () {
    // アカウント削除ボタン押下時
    $(setCustElement.deleteUserBtn).on("click", function () {
        $(setCustElement.deleteUserRow).addClass('hidden');
        $(setCustElement.confideleteUserRow).removeClass('hidden');
    });

    // アカウント削除確認で「いいえ」ボタン押下時
    $(setCustElement.notDeleteUserBtn).on("click", function () {
        $(setCustElement.deleteUserRow).removeClass('hidden');
        $(setCustElement.confideleteUserRow).addClass('hidden');
    });
});