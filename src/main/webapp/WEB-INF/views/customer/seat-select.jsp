<%@ page pageEncoding="UTF-8" %>
  <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
      <%-- SeatSelectServlet (@WebServlet "/booking/seats" ) set: request.setAttribute("showtime", Showtime)
        request.setAttribute("seatsByRow", LinkedHashMap<String, List<ShowtimeSeat>>)

        CAU TRUC HTML #seatMap PHAI khop chinh xac voi assets/js/seat-select.js
        (xem chu thich dau file do). Neu sua id/class o day, PHAI sua ca ben JS.
        --%>
        <c:set var="pageTitle" value="Chọn ghế — ${showtime.movieTitle}" scope="request" />
        <c:set var="extraJs" value="/assets/js/seat-select.js" scope="request" />
        <%@ include file="/WEB-INF/views/common/header.jsp" %>

          <section class="cnj-section" style="padding-bottom: 120px;">
            <div class="cnj-container">

              <div class="cnj-mb-6">
                <h1 class="cnj-fs-h2">
                  <c:out value="${showtime.movieTitle}" />
                </h1>
                <p class="cnj-text-mist">
                  <c:out value="${showtime.roomName}" /> ·
                  <c:out value="${showtime.showDateFormatted}" /> ·
                  <c:out value="${showtime.startTimeFormatted}" /> -
                  <c:out value="${showtime.endTimeFormatted}" />
                </p>
              </div>

              <c:if test="${param.error == 'no_seat_selected'}">
                <div class="cnj-alert cnj-alert--error">Vui lòng chọn ít nhất 1 ghế trước khi tiếp tục.</div>
              </c:if>
              <c:if test="${param.error == 'seats_expired'}">
                <div class="cnj-alert cnj-alert--error">Ghế bạn chọn đã hết thời gian giữ. Vui lòng chọn lại.</div>
              </c:if>
              <c:if test="${param.error == 'seats_taken'}">
                <div class="cnj-alert cnj-alert--error">Rất tiếc, một số ghế vừa được người khác đặt mất. Vui lòng chọn
                  ghế khác.</div>
              </c:if>

              <div class="cnj-screen">
                <div class="cnj-screen__bar"></div>
                <div class="cnj-screen__label">Màn hình</div>
              </div>

              <div class="cnj-seat-legend">
                <div class="cnj-seat-legend__item"><span class="cnj-seat-legend__swatch"
                    style="background:var(--cnj-surface);"></span> Ghế thường (
                  <c:out value="${showtime.priceNormalFormatted}" />)
                </div>
                <div class="cnj-seat-legend__item"><span class="cnj-seat-legend__swatch"
                    style="background:var(--cnj-surface); border-color:#6E5A1F;"></span> Ghế VIP (
                  <c:out value="${showtime.priceVipFormatted}" />)
                </div>
                <div class="cnj-seat-legend__item"><span class="cnj-seat-legend__swatch"
                    style="background:var(--cnj-surface); width:28px;"></span> Ghế đôi (
                  <c:out value="${showtime.priceCoupleFormatted}" />)
                </div>
                <div class="cnj-seat-legend__item"><span class="cnj-seat-legend__swatch"
                    style="background:var(--cnj-gold);"></span> Đang chọn</div>
                <div class="cnj-seat-legend__item"><span class="cnj-seat-legend__swatch"
                    style="background:var(--cnj-slate);"></span> Đã bán</div>
              </div>

              <div id="seatMap" data-showtime-id="${showtime.id}" data-context-path="${pageContext.request.contextPath}"
                data-lock-timeout="5" class="cnj-seat-map">

                <c:forEach var="rowEntry" items="${seatsByRow}">
                  <div class="cnj-seat-row">
                    <span class="cnj-seat-row__label">
                      <c:out value="${rowEntry.key}" />
                    </span>

                    <c:forEach var="seat" items="${rowEntry.value}">
                      <c:choose>
                        <c:when test="${seat.seatType == 'VIP'}">
                          <c:set var="seatPrice" value="${showtime.priceVip}" />
                        </c:when>
                        <c:when test="${seat.seatType == 'COUPLE'}">
                          <c:set var="seatPrice" value="${showtime.priceCouple}" />
                        </c:when>
                        <c:otherwise>
                          <c:set var="seatPrice" value="${showtime.priceNormal}" />
                        </c:otherwise>
                      </c:choose>

                      <button type="button" class="cnj-seat
                           cnj-seat--${fn:toLowerCase(seat.status)}
                           ${seat.seatType == 'VIP' ? 'cnj-seat--vip' : ''}
                           ${seat.seatType == 'COUPLE' ? 'cnj-seat--couple' : ''}" data-seat-id="${seat.seatId}"
                        data-seat-code="${seat.seatCode}" data-price="${seatPrice}"
                        title="${seat.seatCode} — ${seat.seatType}">
                        <c:out value="${seat.seatCode}" />
                      </button>
                    </c:forEach>
                  </div>
                </c:forEach>

              </div>
            </div>
          </section>

          <div id="bookingBar" class="cnj-booking-bar">
            <div class="cnj-flex">
              <span class="cnj-text-mist">Đã chọn <strong id="selectedCount">0</strong> ghế</span>
              <span class="cnj-text-mist">·</span>
              <span class="cnj-text-gold cnj-text-mono" id="selectedTotal">0 đ</span>
              <span id="countdownTimer" class="cnj-booking-bar__timer"></span>
            </div>
            <button id="btnContinue" class="cnj-btn cnj-btn--primary" disabled>Tiếp tục →</button>
          </div>

          <%@ include file="/WEB-INF/views/common/footer.jsp" %>