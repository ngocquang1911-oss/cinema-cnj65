<%@ page pageEncoding="UTF-8" %>
  <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

    <%-- TicketSuccessServlet (@WebServlet "/ticket-success" ) set: ticket (Ticket, kem details) --%>

      <c:set var="pageTitle" value="Đặt vé thành công — CINEMA CNJ65" scope="request" />

      <%@ include file="/WEB-INF/views/common/header.jsp" %>

        <section class="cnj-section">
          <div class="cnj-container" style="max-width: 680px;">

            <div class="cnj-text-center cnj-mb-6">
              <div style="font-size: 3rem;">🎬</div>

              <h1 class="cnj-fs-h2 cnj-text-gold">
                Đặt vé thành công!
              </h1>

              <p class="cnj-text-mist">
                Vé điện tử của bạn đã sẵn sàng — vui lòng đưa mã vé cho nhân viên khi vào rạp.
              </p>
            </div>

            <%@ include file="/WEB-INF/views/common/_ticket-stub.jspf" %>

              <div class="cnj-flex cnj-mt-6" style="justify-content:center;">
                <a href="${pageContext.request.contextPath}/booking/my-tickets" class="cnj-btn cnj-btn--outline">
                  Xem vé của tôi
                </a>

                <a href="${pageContext.request.contextPath}/home" class="cnj-btn cnj-btn--primary">
                  Về trang chủ
                </a>
              </div>

          </div>
        </section>

        <%@ include file="/WEB-INF/views/common/footer.jsp" %>