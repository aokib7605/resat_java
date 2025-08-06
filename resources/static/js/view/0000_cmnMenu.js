$(function () {
    handleResponsiveView();
    $(window).on('load resize', handleResponsiveView);

    function handleResponsiveView() {
        if (window.matchMedia(commonElement.responsiveWidth).matches) {
            // モバイル時の処理
            $(managerMenuElement.managerMenu).addClass('mobile');
            $(managerMenuElement.managerMenu).removeClass('pc');
        } else {
            // PC時の処理
            $(managerMenuElement.managerMenu).addClass('pc');
            $(managerMenuElement.managerMenu).removeClass('mobile');
        }
    }

    // 公演メニューのトグル機能
    $(managerMenuElement.stage).on("click", function () {
        if ($(managerMenuElement.stageMenu).hasClass('hidden') === true) {
            initMenuVisibleForMobile()
            $(managerMenuElement.stageMenu).removeClass('hidden');
            // オーバーレイのフェードイン処理
            overLayFadeIn();
        } else {
            $(managerMenuElement.stageMenu).addClass('hidden');
            // オーバーレイのフェードアウト処理
            overLayFadeOut();
        }
    });

    // 票券管理メニューのトグル機能
    $(managerMenuElement.manage).on("click", function () {
        if ($(managerMenuElement.manageMenu).hasClass('hidden') === true) {
            initMenuVisibleForMobile()
            $(managerMenuElement.manageMenu).removeClass('hidden');
            // オーバーレイのフェードイン処理
            overLayFadeIn();
        } else {
            $(managerMenuElement.manageMenu).addClass('hidden');
            // オーバーレイのフェードアウト処理
            overLayFadeOut();
        }
    });

    // 便利機能メニューのトグル機能
    $(managerMenuElement.tool).on("click", function () {
        if ($(managerMenuElement.toolMenu).hasClass('hidden') === true) {
            initMenuVisibleForMobile()
            $(managerMenuElement.toolMenu).removeClass('hidden');
            // オーバーレイのフェードイン処理
            overLayFadeIn();
        } else {
            $(managerMenuElement.toolMenu).addClass('hidden');
            // オーバーレイのフェードアウト処理
            overLayFadeOut();
        }
    });

    // 団体メニューのトグル機能
    $(managerMenuElement.group).on("click", function () {
        if ($(managerMenuElement.groupMenu).hasClass('hidden') === true) {
            initMenuVisibleForMobile()
            $(managerMenuElement.groupMenu).removeClass('hidden');
            // オーバーレイのフェードイン処理
            overLayFadeIn();
        } else {
            $(managerMenuElement.groupMenu).addClass('hidden');
            // オーバーレイのフェードアウト処理
            overLayFadeOut();
        }
    });

    // ユーザーメニューのトグル機能
    $(managerMenuElement.user).on("click", function () {
        if ($(managerMenuElement.userMenu).hasClass('hidden') === true) {
            initMenuVisibleForMobile()
            $(managerMenuElement.userMenu).removeClass('hidden');
            // オーバーレイのフェードイン処理
            overLayFadeIn();
        } else {
            $(managerMenuElement.userMenu).addClass('hidden');
            // オーバーレイのフェードアウト処理
            overLayFadeOut();
        }
    });

    function initMenuVisibleForMobile() {
        if ($(managerMenuElement.managerMenu).hasClass('mobile') == true) {
            $(managerMenuElement.stageMenu).addClass('hidden');
            $(managerMenuElement.manageMenu).addClass('hidden');
            $(managerMenuElement.toolMenu).addClass('hidden');
            $(managerMenuElement.groupMenu).addClass('hidden');
            $(managerMenuElement.userMenu).addClass('hidden');
        }

    }

    function overLayFadeIn() {
        if ($(managerMenuElement.managerMenu).hasClass('mobile') == true) {
            // $(commonElement.overLayMenu).removeClass("hidden");
            $("#overLayBackground").fadeIn(200);
            $("#overLayMenu").fadeIn(200);
        }
    }

    function overLayFadeOut() {
        if ($(managerMenuElement.managerMenu).hasClass('mobile') == true) {
            // $(commonElement.overLayMenu).addClass("hidden");
            $("#overLayBackground").fadeOut(200);
            $("#overLayMenu").fadeOut(200);
        }
    }

    // モバイル側の処理
    // homeボタン押下
    $(commonElement.headerHome).on("click", function () {
        clickHeaderBtn("home");
    });

    // 公演ボタン押下
    $(commonElement.headerStage).on("click", function () {
        clickHeaderBtn("stage");
    });

    // groupボタン押下
    $(commonElement.headerGroup).on("click", function () {
        clickHeaderBtn("group");
    });

    // accountボタン押下
    $(commonElement.headerAccount).on("click", function () {
        clickHeaderBtn("account");
    });

    function clickHeaderBtn(viewMode) {
        // フォームを作成
        const form = document.createElement("form");
        form.method = "POST";
        form.action = "/" + viewMode;

        // パラメータをhiddenフィールドとして追加
        const input = document.createElement("input");
        input.type = "hidden";
        input.name = "viewMode";
        input.value = viewMode;
        form.appendChild(input);

        // フォームをDOMに追加して送信
        document.body.appendChild(form);
        form.submit();
    }
});