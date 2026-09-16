<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  AdminRoomServlet (action=seats) set: room (Room), seatsByRow (Map<String,List<Seat>>)
--%>
<c:set var="pageTitle" value="Sơ đồ ghế — ${room.name}" scope="request" />
<c:set var="pageHeading" value="Sơ đồ ghế: ${room.name}" scope="request" />
<c:set var="activeAdminNav" value="rooms" scope="request" />
<%@ include file="/WEB-INF/views/common/control-header.jsp" %>

<c:if test="${param.success == 'generated'}">
  <div class="cnj-alert cnj-alert--success" data-autohide>Đã sinh sơ đồ ghế thành công.</div>
</c:if>

<div class="cnj-grid" style="grid-template-columns: 1fr 340px;">

  <div class="cnj-card">
    <div class="cnj-card__body">
      <c:choose>
        <c:when test="${empty seatsByRow}">
          <p class="cnj-empty-state">Phòng này chưa có sơ đồ ghế. Hãy sinh ghế ở bên phải.</p>
        </c:when>
        <c:otherwise>
          <div class="cnj-screen"><div class="cnj-screen__bar"></div><div class="cnj-screen__label">Màn hình</div></div>
          <div class="cnj-seat-map">
            <c:forEach var="rowEntry" items="${seatsByRow}">
              <div class="cnj-seat-row">
                <span class="cnj-seat-row__label"><c:out value="${rowEntry.key}" /></span>
                <c:forEach var="seat" items="${rowEntry.value}">
                  <span class="cnj-seat cnj-seat--available
                              ${seat.seatType=='VIP' ? 'cnj-seat--vip' : ''}
                              ${seat.seatType=='COUPLE' ? 'cnj-seat--couple' : ''}"
                        style="cursor:default;" title="${seat.seatCode} — ${seat.seatType}">
                    <c:out value="${seat.seatCode}" />
                  </span>
                </c:forEach>
              </div>
            </c:forEach>
          </div>
        </c:otherwise>
      </c:choose>
    </div>
  </div>

  <div class="cnj-card">
    <div class="cnj-card__body">
      <div class="cnj-card__title">Sinh sơ đồ ghế tự động</div>
      <p class="cnj-form-hint cnj-mb-4">⚠️ Sinh lại sẽ XÓA toàn bộ ghế cũ của phòng này và tạo mới.</p>

      <form method="post" action="${pageContext.request.contextPath}/admin/rooms"
            data-confirm="Sinh lại sơ đồ ghế sẽ xóa toàn bộ ghế hiện tại. Tiếp tục?">
        <input type="hidden" name="action" value="generate-seats">
        <input type="hidden" name="roomId" value="${room.id}">

        <div class="cnj-form-group">
          <label class="cnj-label">Số hàng ghế (A, B, C...)</label>
          <input class="cnj-input" type="number" name="rows" min="1" max="15" value="5" required>
        </div>
        <div class="cnj-form-group">
          <label class="cnj-label">Số ghế mỗi hàng</label>
          <input class="cnj-input" type="number" name="cols" min="1" max="20" value="8" required>
        </div>
        <div class="cnj-form-group">
          <label class="cnj-label">Hàng ghế đôi (nhập số thứ tự, để trống nếu không có)</label>
          <input class="cnj-input" type="number" name="coupleRow" min="0" placeholder="Ví dụ: 4 (tức hàng E)">
          <p class="cnj-form-hint">Đánh số từ 0 (hàng A = 0, hàng B = 1...). 2 hàng cuối cùng tự động là ghế VIP.</p>
        </div>

        <button type="submit" class="cnj-btn cnj-btn--primary cnj-btn--block">Sinh sơ đồ ghế</button>
      </form>
    </div>
  </div>
</div>

<%@ include file="/WEB-INF/views/common/control-footer.jsp" %>
