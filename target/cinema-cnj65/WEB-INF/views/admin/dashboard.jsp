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

<div class="cnj-grid" style="grid-template-columns: repeat(3, 1fr); margin-bottom: var(--cnj-sp-5);">
  <div class="cnj-stat-card">
    <div class="cnj-stat-card__label">Tổng doanh thu</div>
    <div class="cnj-stat-card__value"><c:out value="${totalRevenueFormatted}" /></div>
  </div>
  <div class="cnj-stat-card">
    <div class="cnj-stat-card__label">Vé đã bán</div>
    <div class="cnj-stat-card__value">${totalTickets}</div>
  </div>
  <div class="cnj-stat-card">
    <div class="cnj-stat-card__label">Phim đang chiếu</div>
    <div class="cnj-stat-card__value" style="color: var(--cnj-paper);">
      <a href="${pageContext.request.contextPath}/admin/movies" class="cnj-text-gold">Quản lý →</a>
    </div>
  </div>
</div>

<div class="cnj-grid" style="grid-template-columns: 2fr 1fr;">
  <div class="cnj-card">
    <div class="cnj-card__body">
      <div class="cnj-card__title">Doanh thu 7 ngày gần nhất</div>
      <canvas id="revenueChart" height="90"></canvas>
    </div>
  </div>

  <div class="cnj-card">
    <div class="cnj-card__body">
      <div class="cnj-card__title">Top phim bán chạy</div>
      <c:choose>
        <c:when test="${empty topMovies}">
          <p class="cnj-text-mist" style="font-size: var(--cnj-fs-small);">Chưa có dữ liệu bán vé.</p>
        </c:when>
        <c:otherwise>
          <c:forEach var="row" items="${topMovies}" varStatus="st">
            <div class="cnj-flex--between cnj-mb-2">
              <span class="cnj-text-mist" style="font-size: var(--cnj-fs-small);">${st.index + 1}. <c:out value="${row[0]}" /></span>
              <span class="cnj-badge cnj-badge--warning"><c:out value="${row[1]}" /> vé</span>
            </div>
          </c:forEach>
        </c:otherwise>
      </c:choose>
    </div>
  </div>
</div>

<%-- Ve bieu do bang canvas thuan (khong phu thuoc Chart.js ngoai de giam rui ro loi tai thu vien) --%>
<script>
(function () {
  var labels = ${chartLabelsJson};
  var values = ${chartValuesJson};
  var canvas = document.getElementById('revenueChart');
  if (!canvas || labels.length === 0) return;
  var ctx = canvas.getContext('2d');
  var w = canvas.width = canvas.offsetWidth;
  var h = canvas.height;
  var max = Math.max.apply(null, values) || 1;
  var barW = w / labels.length * 0.6;
  var gap = w / labels.length;

  ctx.clearRect(0, 0, w, h);
  labels.forEach(function (label, i) {
    var barH = (values[i] / max) * (h - 30);
    var x = i * gap + (gap - barW) / 2;
    var y = h - barH - 20;
    ctx.fillStyle = '#F4B400';
    ctx.fillRect(x, y, barW, barH);
    ctx.fillStyle = '#B8C0D0';
    ctx.font = '10px monospace';
    ctx.textAlign = 'center';
    ctx.fillText(label.slice(5), x + barW / 2, h - 6);
  });
})();
</script>

<%@ include file="/WEB-INF/views/common/control-footer.jsp" %>
