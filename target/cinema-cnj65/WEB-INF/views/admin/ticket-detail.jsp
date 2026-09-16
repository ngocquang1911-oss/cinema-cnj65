<%@ page contentType="text/html;charset=UTF-8" language="java" %>
  <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

    <%-- AdminTicketServlet (action=detail) set: ticket (Ticket, kem details) Trang nay la trang chi tiet ve cua Admin.
      Khong hien nut "Xem chi tiet" trong _ticket-stub.jspf vi dang o san trang chi tiet. --%>

      <c:set var="pageTitle" value="Chi tiết vé — CINEMA CNJ65" scope="request" />
      <c:set var="pageHeading" value="Chi tiết vé ${ticket.ticketCode}" scope="request" />
      <c:set var="activeAdminNav" value="tickets" scope="request" />

      <%@ include file="/WEB-INF/views/common/control-header.jsp" %>

        <a href="${pageContext.request.contextPath}/admin/tickets" class="cnj-btn cnj-btn--ghost cnj-btn--sm cnj-mb-4">
          ← Quay lại danh sách
        </a>

        <c:if test="${param.success == 'cancelled'}">
          <div class="cnj-alert cnj-alert--success" data-autohide>
            Đã hủy vé thành công.
          </div>
        </c:if>

        <div style="max-width: 640px;">

          <%-- Đang ở trang Admin ticket-detail nên không cần nút "Xem chi tiết" của fragment. --%>
            <c:set var="showTicketDetailButton" value="false" />

            <%@ include file="/WEB-INF/views/common/_ticket-stub.jspf" %>

              <c:if test="${not empty ticket.staffId}">
                <div class="cnj-alert cnj-alert--info">
                  Vé này được bán tại quầy bởi nhân viên
                  (staff_id = ${ticket.staffId}).
                </div>
              </c:if>

              <c:if test="${ticket.status == 'PAID'}">
                <form method="post" action="${pageContext.request.contextPath}/admin/tickets">

                  <input type="hidden" name="id" value="${ticket.id}">

                  <button type="submit" class="cnj-btn cnj-btn--danger"
                    data-confirm="Admin hủy vé này? Ghế sẽ được trả lại trạng thái trống.">
                    Hủy vé (quyền Admin)
                  </button>

                </form>
              </c:if>

        </div>

        <%@ include file="/WEB-INF/views/common/control-footer.jsp" %>