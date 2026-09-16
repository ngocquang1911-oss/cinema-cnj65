<%@ page pageEncoding="UTF-8" %>
  <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%-- BookingConfirmServlet (@WebServlet "/booking/confirm" ) set: showtime (Showtime), selectedSeats
      (List<ShowtimeSeat>), total (BigDecimal)
      Form o day POST thang toi PaymentServlet (/booking/payment).
      --%>
      <c:set var="pageTitle" value="Xác nhận đặt vé — CINEMA CNJ65" scope="request" />
      <%@ include file="/WEB-INF/views/common/header.jsp" %>

        <section class="cnj-section">
          <div class="cnj-container" style="max-width: 640px;">
            <h1 class="cnj-fs-h2 cnj-mb-6 cnj-text-center">Xác nhận đơn hàng</h1>

            <div class="cnj-card cnj-mb-4">
              <div class="cnj-card__body">
                <div class="cnj-card__title">
                  <c:out value="${showtime.movieTitle}" />
                </div>
                <p class="cnj-text-mist cnj-mb-4">
                  <c:out value="${showtime.roomName}" /> ·
                  <c:out value="${showtime.showDateFormatted}" /> ·
                  <c:out value="${showtime.startTimeFormatted}" />
                </p>

                <div class="cnj-flex--between cnj-mb-2">
                  <span class="cnj-text-mist">Ghế đã chọn</span>
                </div>
                <div class="cnj-ticket__seats cnj-mb-4">
                  <c:forEach var="seat" items="${selectedSeats}">
                    <span class="cnj-ticket__seat-chip">
                      <c:out value="${seat.seatCode}" /> (
                      <c:out value="${seat.seatType}" />)
                    </span>
                  </c:forEach>
                </div>

                <div class="cnj-flex--between"
                  style="border-top: 1px solid var(--cnj-border); padding-top: var(--cnj-sp-3);">
                  <span class="cnj-fs-h3">Tổng cộng</span>
                  <span class="cnj-fs-h3 cnj-text-gold cnj-text-mono">
                    <c:out value="${totalFormatted}" />
                  </span>
                </div>
              </div>
            </div>

            <form method="post" action="${pageContext.request.contextPath}/booking/payment">
              <input type="hidden" name="showtimeId" value="${showtime.id}">
              <c:forEach var="seat" items="${selectedSeats}">
                <input type="hidden" name="seatId" value="${seat.seatId}">
              </c:forEach>

              <div class="cnj-card cnj-mb-6">
                <div class="cnj-card__body">
                  <div class="cnj-label cnj-mb-2">Phương thức thanh toán</div>

                  <div class="cnj-grid cnj-grid--2col">
                    <label class="cnj-card" style="padding: var(--cnj-sp-3); cursor:pointer;">
                      <input type="radio" name="paymentMethod" value="CASH" checked> Tiền mặt tại quầy
                    </label>
                    <label class="cnj-card" style="padding: var(--cnj-sp-3); cursor:pointer;">
                      <input type="radio" name="paymentMethod" value="CARD"> Thẻ ngân hàng
                    </label>
                    <label class="cnj-card" style="padding: var(--cnj-sp-3); cursor:pointer;">
                      <input type="radio" name="paymentMethod" value="MOMO"> Ví MoMo
                    </label>
                    <label class="cnj-card" style="padding: var(--cnj-sp-3); cursor:pointer;">
                      <input type="radio" name="paymentMethod" value="VNPAY"> VNPay
                    </label>
                  </div>
                  <p class="cnj-form-hint cnj-mt-2">* Đồ án minh họa: hệ thống ghi nhận thanh toán ngay, chưa kết nối
                    cổng thanh toán thật.</p>
                </div>
              </div>

              <button type="submit" class="cnj-btn cnj-btn--primary cnj-btn--block">Xác nhận thanh toán</button>
            </form>
          </div>
        </section>

        <%@ include file="/WEB-INF/views/common/footer.jsp" %>