<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  StaffSaleServlet (action=seats) set: showtime (Showtime), seatsByRow (Map),
  foundCustomer (User, chi co khi da tim theo SDT va tim thay)
--%>
<c:set var="pageTitle" value="Chọn ghế — Bán vé quầy" scope="request" />
<c:set var="pageHeading" value="Bán vé: ${showtime.movieTitle}" scope="request" />
<c:set var="activeAdminNav" value="sale" scope="request" />
<%@ include file="/WEB-INF/views/common/control-header.jsp" %>

<p class="cnj-text-mist cnj-mb-4">
  <c:out value="${showtime.roomName}" /> · <c:out value="${showtime.showDateFormatted}" /> · <c:out value="${showtime.startTimeFormatted}" />
</p>

<c:if test="${param.error == 'no_seat_selected'}">
  <div class="cnj-alert cnj-alert--error">Vui lòng chọn ít nhất 1 ghế.</div>
</c:if>
<c:if test="${param.error == 'seats_taken'}">
  <div class="cnj-alert cnj-alert--error">Một số ghế vừa được đặt mất, vui lòng chọn lại.</div>
</c:if>

<%-- Buoc 1 (tuy chon): tim khach hang da co tai khoan theo SDT --%>
<form method="get" action="${pageContext.request.contextPath}/staff/sale" class="cnj-card cnj-mb-4">
  <input type="hidden" name="action" value="seats">
  <input type="hidden" name="showtimeId" value="${showtime.id}">
  <div class="cnj-card__body cnj-flex">
    <input class="cnj-input" type="tel" name="customerPhone" placeholder="Nhập SĐT để tìm khách hàng đã có tài khoản (bỏ trống nếu bán khách lẻ)"
           value="<c:out value="${param.customerPhone}" />" style="max-width:400px;">
    <button type="submit" class="cnj-btn cnj-btn--outline cnj-btn--sm">Tìm</button>
  </div>
</form>

<c:if test="${not empty param.customerPhone}">
  <c:choose>
    <c:when test="${not empty foundCustomer}">
      <div class="cnj-alert cnj-alert--success">Tìm thấy khách hàng: <strong><c:out value="${foundCustomer.fullName}" /></strong> (<c:out value="${foundCustomer.phone}" />) — vé sẽ được gắn vào lịch sử tài khoản này.</div>
    </c:when>
    <c:otherwise>
      <div class="cnj-alert cnj-alert--info">Không tìm thấy tài khoản với SĐT này — vé sẽ được bán dưới dạng khách lẻ.</div>
    </c:otherwise>
  </c:choose>
</c:if>

<form method="post" action="${pageContext.request.contextPath}/staff/sale">
  <input type="hidden" name="showtimeId" value="${showtime.id}">

  <c:choose>
    <c:when test="${not empty foundCustomer}">
      <input type="hidden" name="linkedCustomerId" value="${foundCustomer.id}">
    </c:when>
    <c:otherwise>
      <div class="cnj-card cnj-mb-4">
        <div class="cnj-card__body cnj-grid cnj-grid--2col">
          <div class="cnj-form-group" style="margin-bottom:0;">
            <label class="cnj-label">Tên khách (khách lẻ)</label>
            <input class="cnj-input" type="text" name="guestName" placeholder="Không bắt buộc">
          </div>
          <div class="cnj-form-group" style="margin-bottom:0;">
            <label class="cnj-label">SĐT khách (khách lẻ)</label>
            <input class="cnj-input" type="tel" name="guestPhone" value="<c:out value="${param.customerPhone}" />" placeholder="Không bắt buộc">
          </div>
        </div>
      </div>
    </c:otherwise>
  </c:choose>

  <div class="cnj-card cnj-mb-4">
    <div class="cnj-card__body">
      <div class="cnj-screen"><div class="cnj-screen__bar"></div><div class="cnj-screen__label">Màn hình</div></div>

      <div class="cnj-seat-legend">
        <div class="cnj-seat-legend__item"><span class="cnj-seat-legend__swatch" style="background:var(--cnj-surface);"></span> Trống (<c:out value="${showtime.priceNormalFormatted}" />)</div>
        <div class="cnj-seat-legend__item"><span class="cnj-seat-legend__swatch" style="background:var(--cnj-gold);"></span> Đang chọn</div>
        <div class="cnj-seat-legend__item"><span class="cnj-seat-legend__swatch" style="background:var(--cnj-slate);"></span> Đã bán</div>
      </div>

      <div class="cnj-seat-map">
        <c:forEach var="rowEntry" items="${seatsByRow}">
          <div class="cnj-seat-row">
            <span class="cnj-seat-row__label"><c:out value="${rowEntry.key}" /></span>
            <c:forEach var="seat" items="${rowEntry.value}">
              <c:choose>
                <c:when test="${seat.status == 'BOOKED' || seat.status == 'LOCKED'}">
                  <span class="cnj-seat cnj-seat--booked
                              ${seat.seatType=='VIP' ? 'cnj-seat--vip' : ''}
                              ${seat.seatType=='COUPLE' ? 'cnj-seat--couple' : ''}"><c:out value="${seat.seatCode}" /></span>
                </c:when>
                <c:otherwise>
                  <label class="cnj-seat-toggle">
                    <input type="checkbox" class="cnj-seat-checkbox" name="seatId" value="${seat.seatId}">
                    <span class="cnj-seat cnj-seat--available
                                ${seat.seatType=='VIP' ? 'cnj-seat--vip' : ''}
                                ${seat.seatType=='COUPLE' ? 'cnj-seat--couple' : ''}"><c:out value="${seat.seatCode}" /></span>
                  </label>
                </c:otherwise>
              </c:choose>
            </c:forEach>
          </div>
        </c:forEach>
      </div>
    </div>
  </div>

  <button type="submit" class="cnj-btn cnj-btn--primary">Xác nhận bán vé (Thanh toán tiền mặt)</button>
</form>

<%@ include file="/WEB-INF/views/common/control-footer.jsp" %>
