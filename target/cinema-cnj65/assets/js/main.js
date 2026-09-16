/* =====================================================================
   CINEMA CNJ65 — MAIN.JS (HANH VI DUNG CHUNG TOAN HE THONG)
   ---------------------------------------------------------------------
   QUY UOC: moi hanh vi JS dieu khien qua thuoc tinh data-* (data-action=...),
   KHONG gan onclick="..." truc tiep trong JSP/HTML va KHONG dung id de
   chon phan tu hang loat (id chi danh cho 1 phan tu duy nhat tren trang,
   vi du #seatMap, #countdownTimer).
   ===================================================================== */

document.addEventListener('DOMContentLoaded', function () {

  // ---- 1) Toggle menu mobile ----
  var navToggle = document.querySelector('.cnj-navbar__toggle');
  var navLinks = document.querySelector('.cnj-navbar__links');
  if (navToggle && navLinks) {
    navToggle.addEventListener('click', function () {
      navLinks.classList.toggle('cnj-navbar__links--open');
    });
  }

  // ---- 2) Tu dong an alert thanh cong sau 4 giay ----
  document.querySelectorAll('.cnj-alert--success[data-autohide]').forEach(function (el) {
    setTimeout(function () {
      el.style.transition = 'opacity 400ms ease';
      el.style.opacity = '0';
      setTimeout(function () { el.remove(); }, 400);
    }, 4000);
  });

  // ---- 3) Xac nhan truoc khi thuc hien hanh dong nguy hiem (xoa, huy) ----
  // Vi du: <button data-confirm="Ban chac chan muon xoa phim nay?">Xoa</button>
  document.querySelectorAll('[data-confirm]').forEach(function (el) {
    el.addEventListener('click', function (e) {
      var message = el.getAttribute('data-confirm');
      if (!window.confirm(message)) {
        e.preventDefault();
        e.stopPropagation();
      }
    });
  });

  // ---- 4) Submit form ngay khi doi gia tri select (vi du bo loc) ----
  document.querySelectorAll('[data-autosubmit]').forEach(function (el) {
    el.addEventListener('change', function () {
      var form = el.closest('form');
      if (form) form.submit();
    });
  });
});

/* Ham dung chung: dinh dang so tien theo kieu Viet Nam (vi du 80000 -> "80.000 đ") */
function cnjFormatCurrency(amount) {
  return new Intl.NumberFormat('vi-VN').format(amount) + ' đ';
}
