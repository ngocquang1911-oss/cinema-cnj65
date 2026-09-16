/* =====================================================================
   CINEMA CNJ65 — SEAT-SELECT.JS
   ---------------------------------------------------------------------
   Dieu khien man hinh chon ghe (seat-select.jsp). Goi AJAX toi
   SeatLockServlet (/booking/seat-lock) o backend — xem file do de doi
   chieu dinh dang JSON tra ve neu can sua doi ca 2 phia cho khop nhau.

   CAU TRUC HTML MA FILE NAY YEU CAU (dat dung trong seat-select.jsp):
     <div id="seatMap" data-showtime-id="5" data-context-path="/cinema-cnj65">
       <button class="cnj-seat cnj-seat--available" data-seat-id="12"
               data-seat-code="A01" data-price="80000">A01</button>
       ...
     </div>
     <div id="bookingBar"> ... </div>
     <span id="countdownTimer"></span>
     <span id="selectedCount"></span>
     <span id="selectedTotal"></span>
     <button id="btnContinue" disabled>Tiep tuc</button>
   ===================================================================== */

(function () {
  var seatMap = document.getElementById('seatMap');
  if (!seatMap) return; // Trang khac khong co so do ghe thi bo qua toan bo script nay

  var showtimeId = seatMap.dataset.showtimeId;
  var contextPath = seatMap.dataset.contextPath;
  var lockTimeoutMinutes = parseInt(seatMap.dataset.lockTimeout || '5', 10);

  var selected = {};      // { seatId: price }
  var countdownSeconds = null;
  var countdownInterval = null;
  var pollInterval = null;

  var elCount = document.getElementById('selectedCount');
  var elTotal = document.getElementById('selectedTotal');
  var elTimer = document.getElementById('countdownTimer');
  var btnContinue = document.getElementById('btnContinue');

  // ---- Khoi tao: doc cac ghe da duoc JSP render san la "dang giu boi minh" ----
  seatMap.querySelectorAll('.cnj-seat--selected').forEach(function (btn) {
    selected[btn.dataset.seatId] = parseInt(btn.dataset.price, 10);
  });
  if (Object.keys(selected).length > 0) startCountdown();
  updateSummary();

  // ---- Gan su kien click cho tung ghe ----
  seatMap.addEventListener('click', function (e) {
    var btn = e.target.closest('.cnj-seat');
    if (!btn) return;
    if (btn.classList.contains('cnj-seat--booked') || btn.classList.contains('cnj-seat--locked')) return;

    var seatId = btn.dataset.seatId;
    var price = parseInt(btn.dataset.price, 10);

    if (btn.classList.contains('cnj-seat--selected')) {
      unlockSeat(seatId, btn);
    } else {
      lockSeat(seatId, price, btn);
    }
  });

  function lockSeat(seatId, price, btn) {
    btn.classList.add('cnj-seat--pending');
    postAction('lock', seatId).then(function (res) {
      btn.classList.remove('cnj-seat--pending');
      if (res.success) {
        btn.classList.remove('cnj-seat--available');
        btn.classList.add('cnj-seat--selected');
        selected[seatId] = price;
        updateSummary();
        if (!countdownInterval) startCountdown();
      } else {
        // Ghe vua bi nguoi khac giu mat - cap nhat lai trang thai that tu server
        showToast(res.message);
        refreshSeatStatuses();
      }
    });
  }

  function unlockSeat(seatId, btn) {
    btn.classList.add('cnj-seat--pending');
    postAction('unlock', seatId).then(function (res) {
      btn.classList.remove('cnj-seat--pending');
      if (res.success) {
        btn.classList.remove('cnj-seat--selected');
        btn.classList.add('cnj-seat--available');
        delete selected[seatId];
        updateSummary();
        if (Object.keys(selected).length === 0) stopCountdown();
      }
    });
  }

  function postAction(action, seatId) {
    var body = new URLSearchParams();
    body.set('action', action);
    body.set('showtimeId', showtimeId);
    body.set('seatId', seatId);

    return fetch(contextPath + '/booking/seat-lock', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: body.toString()
    }).then(function (r) { return r.json(); })
      .catch(function () { return { success: false, message: 'Loi ket noi may chu.' }; });
  }

  function updateSummary() {
    var ids = Object.keys(selected);
    var total = ids.reduce(function (sum, id) { return sum + selected[id]; }, 0);

    if (elCount) elCount.textContent = ids.length;
    if (elTotal) elTotal.textContent = cnjFormatCurrency(total);
    if (btnContinue) btnContinue.disabled = ids.length === 0;
  }

  // ---- Dem nguoc thoi gian giu ghe (UX phia client, server la nguon su that) ----
  function startCountdown() {
    countdownSeconds = lockTimeoutMinutes * 60;
    if (countdownInterval) clearInterval(countdownInterval);
    countdownInterval = setInterval(function () {
      countdownSeconds--;
      renderCountdown();
      if (countdownSeconds <= 0) {
        stopCountdown();
        showToast('Da het thoi gian giu ghe. Vui long chon lai.');
        window.location.reload();
      }
    }, 1000);
    renderCountdown();
    if (!pollInterval) pollInterval = setInterval(refreshSeatStatuses, 5000);
  }

  function stopCountdown() {
    if (countdownInterval) clearInterval(countdownInterval);
    countdownInterval = null;
    if (elTimer) elTimer.textContent = '';
  }

  function renderCountdown() {
    if (!elTimer) return;
    var m = Math.floor(countdownSeconds / 60);
    var s = countdownSeconds % 60;
    elTimer.textContent = m + ':' + (s < 10 ? '0' + s : s);
    elTimer.classList.toggle('cnj-booking-bar__timer--danger', countdownSeconds <= 30);
  }

  // ---- Poll trang thai ghe moi 5s de cap nhat real-time khi nguoi khac dat/nha ghe ----
  function refreshSeatStatuses() {
    fetch(contextPath + '/booking/seat-lock?showtimeId=' + showtimeId)
      .then(function (r) { return r.json(); })
      .then(function (list) {
        list.forEach(function (s) {
          if (selected[s.seatId]) return; // ghe minh dang giu - khong dong bo de tranh giat lai UI
          var btn = seatMap.querySelector('.cnj-seat[data-seat-id="' + s.seatId + '"]');
          if (!btn) return;
          btn.classList.remove('cnj-seat--available', 'cnj-seat--locked', 'cnj-seat--booked', 'cnj-seat--selected');
          btn.classList.add('cnj-seat--' + s.status.toLowerCase());
        });
      });
  }

  // ---- Toast bao loi don gian (khong phu thuoc thu vien ngoai) ----
  function showToast(message) {
    var toast = document.createElement('div');
    toast.className = 'cnj-alert cnj-alert--error';
    toast.style.position = 'fixed';
    toast.style.bottom = '90px';
    toast.style.right = '24px';
    toast.style.zIndex = '999';
    toast.style.maxWidth = '320px';
    toast.textContent = message;
    document.body.appendChild(toast);
    setTimeout(function () { toast.remove(); }, 3500);
  }

  // ---- Nut Tiep tuc: dieu huong sang trang xac nhan voi danh sach ghe da chon ----
  if (btnContinue) {
    btnContinue.addEventListener('click', function () {
      var ids = Object.keys(selected);
      if (ids.length === 0) return;
      var params = new URLSearchParams();
      params.set('showtimeId', showtimeId);
      ids.forEach(function (id) { params.append('seatId', id); });
      window.location.href = contextPath + '/booking/confirm?' + params.toString();
    });
  }
})();
