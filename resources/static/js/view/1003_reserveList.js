$(function () {
    handleResponsiveView();
    $(window).on('load resize', handleResponsiveView);

    function handleResponsiveView() {
        if (window.matchMedia(commonElement.responsiveWidth).matches) {
            // モバイル時の処理
            $(reserveListElement.openStageCol).addClass('hidden');

        } else {
            // PC時の処理
            $(reserveListElement.openStageCol).removeClass('hidden');
        }
    }

    // 公演一覧から公演テーブル押下時、jsを経由してPOST送信を実行（モバイル）
    $(document).ready(function () {
        if ($('div[name="custPage"]').hasClass('mobile')) {
            $('.mobile .reserveList table').on('click', function () {
                const sysTraId = $(this).data('systraid'); // table要素にdata-systraid属性を持たせておく

                // フォームを動的に作成してPOST送信
                const form = $('<form>', {
                    action: '/myPage/reserveList',
                    method: 'post'
                }).append(
                    $('<input>', {
                        type: 'hidden',
                        name: 'sysTraId',
                        value: sysTraId
                    }),
                    $('<input>', {
                        type: 'hidden',
                        name: 'mode',
                        value: 'openStage'
                    })
                );

                $('body').append(form);
                form.submit();
            });
        }
    });

    // 予約キャンセルボタン押下時
    $(reserveListElement.traCancelBtn).on("click", function () {
        $(reserveListElement.traCancelDiv).addClass('hidden');
        $(reserveListElement.confiCancelDiv).removeClass('hidden');
        $(reserveListElement.backBtnDiv).addClass('hidden');
    });

    // 予約キャンセル確認で「いいえ」ボタン押下時
    $(reserveListElement.notCancelBtn).on("click", function () {
        $(reserveListElement.traCancelDiv).removeClass('hidden');
        $(reserveListElement.confiCancelDiv).addClass('hidden');
        $(reserveListElement.backBtnDiv).removeClass('hidden');
    });

})