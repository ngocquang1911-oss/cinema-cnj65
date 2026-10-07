<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  AdminDashboardServlet set: totalRevenueFormatted, totalTickets, topMovies
  (List<Object[]>{title, soldCount}), chartLabelsJson, chartValuesJson
--%>
<c:set var="pageTitle" value="Dashboard — CINEMA CNJ65" scope="request" />
<c:set var="pageHeading" value="Tổng quan vận hành" scope="request" />
<c:set var="activeAdminNav" value="dashboard" scope="request" />
<%@ include file="/WEB-INF/views/common/control-header.jsp" %>

<div class="cnj-stat-grid">
  <div class="cnj-stat-card">
    <span class="cnj-stat-card__icon">💰</span>
    <div class="cnj-stat-card__body">
      <div class="cnj-stat-card__label">Tổng doanh thu</div>
      <div class="cnj-stat-card__value"><c:out value="${totalRevenueFormatted}" /></div>
      <div class="cnj-stat-card__sub">Tính trên vé đã thanh toán</div>
    </div>
  </div>

  <div class="cnj-stat-card cnj-stat-card--green">
    <span class="cnj-stat-card__icon">🎟️</span>
    <div class="cnj-stat-card__body">
      <div class="cnj-stat-card__label">Vé đã bán</div>
      <div class="cnj-stat-card__value">${totalTickets}</div>
      <div class="cnj-stat-card__sub">Toàn hệ thống</div>
    </div>
  </div>

  <div class="cnj-stat-card cnj-stat-card--red">
    <span class="cnj-stat-card__icon">🎬</span>
    <div class="cnj-stat-card__body">
      <div class="cnj-stat-card__label">Quản lý phim</div>
      <div class="cnj-stat-card__value" style="font-size:1.1rem;">
        <a href="${pageContext.request.contextPath}/admin/movies" class="cnj-text-gold">Xem danh sách →</a>
      </div>
    </div>
  </div>

  <div class="cnj-stat-card">
    <span class="cnj-stat-card__icon">🕒</span>
    <div class="cnj-stat-card__body">
      <div class="cnj-stat-card__label">Suất chiếu</div>
      <div class="cnj-stat-card__value" style="font-size:1.1rem;">
        <a href="${pageContext.request.contextPath}/admin/showtimes" class="cnj-text-gold">Quản lý lịch →</a>
      </div>
    </div>
  </div>
</div>

<div class="cnj-dashboard-row">
  <div class="cnj-card">
    <div class="cnj-card__body">
      <div class="cnj-flex--between cnj-mb-4">
        <div class="cnj-card__title" style="margin-bottom:0;">Doanh thu 7 ngày gần nhất</div>
        <span class="cnj-badge cnj-badge--warning">7 ngày</span>
      </div>
      <div style="position:relative; width:100%; height:220px;">
        <canvas id="revenueChart"></canvas>
      </div>
    </div>
  </div>

  <div class="cnj-card">
    <div class="cnj-card__body">
      <div class="cnj-card__title">🏆 Top phim bán chạy</div>
      <c:choose>
        <c:when test="${empty topMovies}">
          <p class="cnj-text-mist" style="font-size: var(--cnj-fs-small);">Chưa có dữ liệu bán vé.</p>
        </c:when>
        <c:otherwise>
          <c:forEach var="row" items="${topMovies}" varStatus="st">
            <div class="cnj-flex--between cnj-mb-2" style="padding:8px 0; ${st.index > 0 ? 'border-top:1px solid var(--cnj-border);' : ''}">
              <span class="cnj-text-mist" style="font-size: var(--cnj-fs-small); overflow:hidden; text-overflow:ellipsis; white-space:nowrap; max-width:70%;">
                <span class="cnj-text-gold cnj-text-mono">#${st.index + 1}</span> <c:out value="${row[0]}" />
              </span>
              <span class="cnj-badge cnj-badge--success"><c:out value="${row[1]}" /> vé</span>
            </div>
          </c:forEach>
        </c:otherwise>
      </c:choose>
    </div>
  </div>
</div>

<%-- Ve bieu do bang canvas thuan (khong phu thuoc Chart.js ngoai de giam rui ro loi tai thu vien).
     Co gridline ngang + nhan gia tri + tu dong co gian theo kich thuoc khung cha (responsive). --%>
<script>
(function () {
  var labels = ${chartLabelsJson};
  var values = ${chartValuesJson};
  var canvas = document.getElementById('revenueChart');
  if (!canvas) return;

  function draw() {
    var wrap = canvas.parentElement;
    var w = wrap.clientWidth;
    var h = wrap.clientHeight;
    var dpr = window.devicePixelRatio || 1;
    canvas.width = w * dpr;
    canvas.height = h * dpr;
    canvas.style.width = w + 'px';
    canvas.style.height = h + 'px';
    var ctx = canvas.getContext('2d');
    ctx.scale(dpr, dpr);
    ctx.clearRect(0, 0, w, h);

    if (!labels.length) {
      ctx.fillStyle = '#7C8494';
      ctx.font = '13px sans-serif';
      ctx.textAlign = 'center';
      ctx.fillText('Chưa có dữ liệu doanh thu', w / 2, h / 2);
      return;
    }

    var paddingBottom = 24, paddingTop = 10, paddingLeft = 8, paddingRight = 8;
    var chartH = h - paddingTop - paddingBottom;
    var max = Math.max.apply(null, values) || 1;
    var gap = (w - paddingLeft - paddingRight) / labels.length;
    var barW = gap * 0.55;

    ctx.strokeStyle = 'rgba(255,255,255,0.06)';
    ctx.lineWidth = 1;
    [0.25, 0.5, 0.75, 1].forEach(function (p) {
      var y = paddingTop + chartH * (1 - p);
      ctx.beginPath();
      ctx.moveTo(paddingLeft, y);
      ctx.lineTo(w - paddingRight, y);
      ctx.stroke();
    });

    labels.forEach(function (label, i) {
      var barH = (values[i] / max) * chartH;
      var x = paddingLeft + i * gap + (gap - barW) / 2;
      var y = paddingTop + chartH - barH;

      var grad = ctx.createLinearGradient(0, y, 0, paddingTop + chartH);
      grad.addColorStop(0, '#F4B400');
      grad.addColorStop(1, 'rgba(244,180,0,0.35)');
      ctx.fillStyle = grad;
      ctx.beginPath();
      ctx.roundRect ? ctx.roundRect(x, y, barW, barH, 4) : ctx.rect(x, y, barW, barH);
      ctx.fill();

      ctx.fillStyle = '#B8C0D0';
      ctx.font = '10px monospace';
      ctx.textAlign = 'center';
      ctx.fillText(label.slice(5), x + barW / 2, h - 6);
    });
  }

  draw();
  window.addEventListener('resize', draw);
})();
</script>

<%@ include file="/WEB-INF/views/common/control-footer.jsp" %>