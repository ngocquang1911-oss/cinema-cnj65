<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  AdminShowtimeServlet set: showtimes (List<Showtime>), selectedDate
--%>
<c:set var="pageTitle" value="Suất chiếu — CINEMA CNJ65" scope="request" />
<c:set var="pageHeading" value="Quản lý suất chiếu" scope="request" />
<c:set var="activeAdminNav" value="showtimes" scope="request" />
<%@ include file="/WEB-INF/views/common/control-header.jsp" %>

<c:if test="${param.error == 'has_sold_tickets'}">
  <div class="cnj-alert cnj-alert--error">Không thể hủy: suất chiếu này đã có vé được bán.</div>
</c:if>

<div class="cnj-toolbar">
  <form method="get" action="${pageContext.request.contextPath}/admin/showtimes" class="cnj-toolbar__filters">
    <input class="cnj-input" type="date" name="date" value="<c:out value="${selectedDate}" />" data-autosubmit>
    <a href="${pageContext.request.contextPath}/admin/showtimes" class="cnj-btn cnj-btn--ghost cnj-btn--sm">Xóa lọc</a>
  </form>
  <a href="${pageContext.request.contextPath}/admin/showtimes?action=form" class="cnj-btn cnj-btn--primary">+ Thêm suất chiếu</a>
</div>

<div class="cnj-table-wrap">
  <table class="cnj-table">
    <thead><tr><th>Phim</th><th>Phòng</th><th>Ngày</th><th>Giờ chiếu</th><th>Giá (Thường/VIP/Đôi)</th><th>Trạng thái</th><th>Hành động</th></tr></thead>
    <tbody>
      <c:forEach var="st" items="${showtimes}">
        <tr>
          <td><c:out value="${st.movieTitle}" /></td>
          <td><c:out value="${st.roomName}" /></td>
          <td><c:out value="${st.showDateFormatted}" /></td>
          <td class="cnj-text-mono"><c:out value="${st.startTimeFormatted}" /> - <c:out value="${st.endTimeFormatted}" /></td>
          <td class="cnj-text-mono" style="font-size:var(--cnj-fs-tiny);">
            <c:out value="${st.priceNormalFormatted}" /> / <c:out value="${st.priceVipFormatted}" /> / <c:out value="${st.priceCoupleFormatted}" />
          </td>
          <td>
            <c:choose>
              <c:when test="${st.status=='ACTIVE'}"><span class="cnj-badge cnj-badge--success">Hoạt động</span></c:when>
              <c:otherwise><span class="cnj-badge cnj-badge--danger">Đã hủy</span></c:otherwise>
            </c:choose>
          </td>
          <td class="cnj-table__actions">
            <c:if test="${st.status=='ACTIVE'}">
              <a href="${pageContext.request.contextPath}/admin/showtimes?action=form&id=${st.id}" class="cnj-btn cnj-btn--outline cnj-btn--sm">Sửa</a>
              <form method="post" action="${pageContext.request.contextPath}/admin/showtimes" style="display:inline;">
                <input type="hidden" name="action" value="cancel">
                <input type="hidden" name="id" value="${st.id}">
                <button type="submit" class="cnj-btn cnj-btn--danger cnj-btn--sm" data-confirm="Hủy suất chiếu này?">Hủy</button>
              </form>
            </c:if>
          </td>
        </tr>
      </c:forEach>
      <c:if test="${empty showtimes}"><tr><td colspan="7" class="cnj-empty-state">Không có suất chiếu nào.</td></tr></c:if>
    </tbody>
  </table>
</div>

<%@ include file="/WEB-INF/views/common/control-footer.jsp" %>
