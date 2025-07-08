$(function () {

   // overlay-script-jquery.js
$(function () {
  const $hamburger = $('.hamburger-overlay');
  const $nav = $('#overlay-menu');

  function isMobileView() {
    return window.matchMedia(commonElement.responsiveWidth).matches;
  }

  function openMenu() {
    $hamburger.addClass('active');
    $nav.addClass('active');
    $hamburger.attr('aria-expanded', true);
    $nav.attr('aria-hidden', false);
    $('body').css('overflow', 'hidden');
  }

  function closeMenu() {
    $hamburger.removeClass('active');
    $nav.removeClass('active');
    $hamburger.attr('aria-expanded', false);
    $nav.attr('aria-hidden', true);
    $('body').css('overflow', '');
  }

  $hamburger.on('click', function () {
    if (!isMobileView()) return; // モバイルサイズ以外では無視

    const isOpen = $hamburger.hasClass('active');
    if (isOpen) {
      closeMenu();
    } else {
      openMenu();
    }
  });

  $(document).on('keydown', function (e) {
    if (e.key === 'Escape' && isMobileView() && $nav.hasClass('active')) {
      closeMenu();
    }
  });

  // サイズ変更で強制リセット（PC↔モバイル）
  $(window).on('resize', function () {
    if (!isMobileView()) {
      closeMenu();
    }
  });
});


})