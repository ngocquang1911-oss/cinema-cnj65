<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  AdminTicketServlet set: tickets (List<Ticket>), selectedStatus
--%>
<c:set var="pageTitle" value="Vé đã bán — CINEMA CNJ65" scope="request" />
<c:set var="pageHeading" value="Danh sách vé đã bán" scope="request" />
<c:set var="activeAdminNav" value="tickets" scope="request" />
<%@ include file="/WEB-INF/views/common/control-header.jsp" %>

<div class="cnj-toolbar">
  <form method="get" action="${pageContext.request.contextPath}/admin/tickets" class="cnj-toolbar__filters">
    <select class="cnj-select" name="status" data-autosubmit>
      <option value="">Tất cả trạng thái</option>
      <option value="PAID" ${selectedStatus=='PAID'?'selected':''}>Đã thanh toán</option>
      <option value="CHECKED_IN" ${selectedStatus=='CHECKED_IN'?'selected':''}>Đã check-in</option>
      <option value="CANCELLED" ${selectedStatus=='CANCELLED'?'selected':''}>Đã hủy</option>
    </select>
  </form>
</div>

<div class="cnj-table-wrap">
  <table class="cnj-table">
    <thead><tr><th>Mã vé</th><th>Phim</th><th>Suất chiếu</th><th>Khách hàng</th><th>Tổng tiền</th><th>Trạng thái</th><th></th></tr></thead>
    <tbody>
      <c:forEach var="t" items="${tickets}">
        <tr>
          <td class="cnj-text-mono cnj-text-gold"><c:out value="${t.ticketCode}" /></td>
          <td><c:out value="${t.movieTitle}" /></td>
          <td><c:out value="${t.showDateFormatted}" /> · <c:out value="${t.startTimeFormatted}" /></td>
          <td><c:out value="${not empty t.customerName ? t.customerName : t.guestName}" /></td>
          <td class="cnj-text-mono"><c:out value="${t.totalAmountFormatted}" /></td>
          <td>
            <c:choose>
              <c:when test="${t.status=='PAID'}"><span class="cnj-badge cnj-badge--success">Đã thanh toán</span></c:when>
              <c:when test="${t.status=='CHECKED_IN'}"><span class="cnj-badge cnj-badge--neutral">Đã check-in</span></c:when>
              <c:when test="${t.status=='CANCELLED'}"><span class="cnj-badge cnj-badge--danger">Đã hủy</span></c:when>
              <c:otherwise><span class="cnj-badge cnj-badge--warning">Chờ thanh toán</span></c:otherwise>
            </c:choose>
          </td>
          <td><a href="${pageContext.request.contextPath}/admin/tickets?action=detail&id=${t.id}" class="cnj-btn cnj-btn--outline cnj-btn--sm">Xem</a></td>
        </tr>
      </c:forEach>
      <c:if test="${empty tickets}"><tr><td colspan="7" class="cnj-empty-state">Không có vé nào.</td></tr></c:if>
    </tbody>
  </table>
</div>

<%@ include file="/WEB-INF/views/common/control-footer.jsp" %>
