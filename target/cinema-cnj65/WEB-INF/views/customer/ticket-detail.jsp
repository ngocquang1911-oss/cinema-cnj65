<%@ page pageEncoding="UTF-8" %>
  <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%-- TicketDetailServlet (@WebServlet "/booking/ticket-detail" ) set: ticket (Ticket, kem details)
      CancelTicketServlet (@WebServlet "/booking/cancel-ticket" ) xu ly form huy ve ben duoi. --%>
      <c:set var="pageTitle" value="Chi tiết vé — CINEMA CNJ65" scope="request" />
      <c:set var="activeNav" value="my-tickets" scope="request" />
      <%@ include file="/WEB-INF/views/common/header.jsp" %>

        <section class="cnj-section">
          <div class="cnj-container" style="max-width: 680px;">
            <a href="${pageContext.request.contextPath}/booking/my-tickets"
              class="cnj-btn cnj-btn--ghost cnj-btn--sm cnj-mb-4">← Quay lại Vé của tôi</a>

            <c:if test="${param.success == 'cancelled'}">
              <div class="cnj-alert cnj-alert--success" data-autohide>Đã hủy vé thành công.</div>
            </c:if>
            <c:if test="${param.error == 'too_late_to_cancel'}">
              <div class="cnj-alert cnj-alert--error">Không thể hủy vé vì suất chiếu sắp diễn ra (dưới 2 giờ).</div>
            </c:if>
            <c:if test="${param.error == 'cancel_failed'}">
              <div class="cnj-alert cnj-alert--error">Hủy vé không thành công. Vui lòng thử lại.</div>
            </c:if>

            <%@ include file="/WEB-INF/views/common/_ticket-stub.jspf" %>

              <c:if test="${ticket.status == 'PAID'}">
                <form method="post" action="${pageContext.request.contextPath}/booking/cancel-ticket" class="cnj-mt-4">
                  <input type="hidden" name="id" value="${ticket.id}">
                  <button type="submit" class="cnj-btn cnj-btn--danger"
                    data-confirm="Bạn chắc chắn muốn hủy vé này? Hành động không thể hoàn tác.">
                    Hủy vé
                  </button>
                </form>
                <p class="cnj-form-hint cnj-mt-2">* Chỉ có thể hủy vé trước giờ chiếu ít nhất 2 tiếng.</p>
              </c:if>
          </div>
        </section>

        <%@ include file="/WEB-INF/views/common/footer.jsp" %>