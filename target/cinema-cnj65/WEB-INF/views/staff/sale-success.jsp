<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  StaffSaleSuccessServlet set: ticket (Ticket, kem details)
--%>
<c:set var="pageTitle" value="Bán vé thành công — CINEMA CNJ65" scope="request" />
<c:set var="pageHeading" value="Bán vé thành công" scope="request" />
<c:set var="activeAdminNav" value="sale" scope="request" />
<%@ include file="/WEB-INF/views/common/control-header.jsp" %>

<div style="max-width: 640px;">
  <div class="cnj-alert cnj-alert--success cnj-mb-4">✅ Đã bán vé thành công, vui lòng in/đưa mã vé cho khách.</div>

  <%@ include file="/WEB-INF/views/common/_ticket-stub.jspf" %>

  <a href="${pageContext.request.contextPath}/staff/sale" class="cnj-btn cnj-btn--primary cnj-mt-4">Bán vé tiếp theo</a>
</div>

<%@ include file="/WEB-INF/views/common/control-footer.jsp" %>
