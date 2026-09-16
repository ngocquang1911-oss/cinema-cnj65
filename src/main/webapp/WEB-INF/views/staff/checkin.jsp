<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  StaffCheckinServlet set (sau POST): error hoac success, ticket (co the co ca 2 truong hop)
--%>
<c:set var="pageTitle" value="Check-in vé — CINEMA CNJ65" scope="request" />
<c:set var="pageHeading" value="Check-in vé tại rạp" scope="request" />
<c:set var="activeAdminNav" value="checkin" scope="request" />
<%@ include file="/WEB-INF/views/common/control-header.jsp" %>

<div style="max-width: 560px;">
  <form method="post" action="${pageContext.request.contextPath}/staff/checkin" class="cnj-card cnj-mb-6">
    <div class="cnj-card__body">
      <label class="cnj-label" for="ticketCode">Nhập mã vé (hoặc quét QR vào ô này)</label>
      <div class="cnj-flex">
        <input class="cnj-input cnj-text-mono" type="text" id="ticketCode" name="ticketCode"
               placeholder="CNJ-XXXXXXX" autofocus required style="text-transform:uppercase;">
        <button type="submit" class="cnj-btn cnj-btn--primary">Check-in</button>
      </div>
    </div>
  </form>

  <c:if test="${not empty error}"><div class="cnj-alert cnj-alert--error"><c:out value="${error}" /></div></c:if>
  <c:if test="${not empty success}"><div class="cnj-alert cnj-alert--success"><c:out value="${success}" /></div></c:if>

  <c:if test="${not empty ticket}">
    <%@ include file="/WEB-INF/views/common/_ticket-stub.jspf" %>
  </c:if>
</div>

<script>
  // Tu dong viet hoa ma ve khi go, giup nhan viên khong can bam Shift
  document.getElementById('ticketCode').addEventListener('input', function (e) {
    e.target.value = e.target.value.toUpperCase();
  });
</script>

<%@ include file="/WEB-INF/views/common/control-footer.jsp" %>
