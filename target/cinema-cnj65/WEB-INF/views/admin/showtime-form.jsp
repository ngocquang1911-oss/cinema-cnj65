<%@ page contentType="text/html;charset=UTF-8" language="java" %>
  <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%-- AdminShowtimeServlet set: showtime (Showtime, chi co khi Sua), movies, rooms, error --%>
      <c:set var="pageTitle" value="${empty showtime ? 'Thêm suất chiếu' : 'Sửa suất chiếu'} — CINEMA CNJ65"
        scope="request" />
      <c:set var="pageHeading" value="${empty showtime ? 'Thêm suất chiếu mới' : 'Sửa suất chiếu'}" scope="request" />
      <c:set var="activeAdminNav" value="showtimes" scope="request" />
      <%@ include file="/WEB-INF/views/common/control-header.jsp" %>

        <c:if test="${not empty error}">
          <div class="cnj-alert cnj-alert--error">
            <c:out value="${error}" />
          </div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/admin/showtimes" class="cnj-card"
          style="max-width:640px;">
          <input type="hidden" name="action" value="save">
          <c:if test="${not empty showtime}"><input type="hidden" name="id" value="${showtime.id}"></c:if>

          <div class="cnj-card__body">
            <div class="cnj-grid cnj-grid--2col">
              <div class="cnj-form-group">
                <label class="cnj-label">Phim *</label>
                <select class="cnj-select" name="movieId" required>
                  <option value="">-- Chọn phim --</option>
                  <c:forEach var="m" items="${movies}">
                    <option value="${m.id}" ${showtime.movieId==m.id ? 'selected' : '' }>
                      <c:out value="${m.title}" />
                    </option>
                  </c:forEach>
                </select>
              </div>
              <div class="cnj-form-group">
                <label class="cnj-label">Phòng chiếu *</label>
                <select class="cnj-select" name="roomId" required>
                  <option value="">-- Chọn phòng --</option>
                  <c:forEach var="r" items="${rooms}">
                    <option value="${r.id}" ${showtime.roomId==r.id ? 'selected' : '' }>
                      <c:out value="${r.name}" /> (
                      <c:out value="${r.roomType}" />)
                    </option>
                  </c:forEach>
                </select>
              </div>
            </div>

            <c:choose>
              <%-- SUA suat chieu da co: chi 1 ngay duy nhat, khong hien tuy chon lap lai --%>
                <c:when test="${not empty showtime}">
                  <div class="cnj-form-group">
                    <label class="cnj-label">Ngày chiếu *</label>
                    <input class="cnj-input" type="date" name="showDate" value="${showtime.showDate}" required>
                  </div>
                </c:when>

                <%-- THEM MOI: cho chon 1 ngay hoac lap lai theo khoang ngay --%>
                  <c:otherwise>
                    <input type="hidden" name="mode" id="showtimeMode" value="single">

                    <div class="cnj-form-group">
                      <label class="cnj-label">Áp dụng cho</label>
                      <div class="cnj-flex">
                        <label class="cnj-card" style="padding:10px 16px; cursor:pointer;">
                          <input type="radio" name="modeRadio" value="single" checked
                            onchange="cnjToggleShowtimeMode('single')"> 1 ngày cụ thể
                        </label>
                        <label class="cnj-card" style="padding:10px 16px; cursor:pointer;">
                          <input type="radio" name="modeRadio" value="range" onchange="cnjToggleShowtimeMode('range')">
                          Nhiều ngày lặp lại (cùng 1 khung giờ)
                        </label>
                      </div>
                    </div>

                    <div id="showtimeSingleFields" class="cnj-form-group">
                      <label class="cnj-label">Ngày chiếu *</label>
                      <input class="cnj-input" type="date" name="showDate" required>
                    </div>

                    <div id="showtimeRangeFields" style="display:none;">
                      <div class="cnj-grid cnj-grid--2col">
                        <div class="cnj-form-group">
                          <label class="cnj-label">Từ ngày *</label>
                          <input class="cnj-input" type="date" name="rangeStart">
                        </div>
                        <div class="cnj-form-group">
                          <label class="cnj-label">Đến ngày *</label>
                          <input class="cnj-input" type="date" name="rangeEnd">
                        </div>
                      </div>
                      <div class="cnj-form-group">
                        <label class="cnj-label">Chỉ áp dụng vào các thứ (bỏ trống = áp dụng tất cả các ngày)</label>
                        <div class="cnj-flex" style="flex-wrap:wrap;">
                          <label class="cnj-badge cnj-badge--neutral" style="cursor:pointer;"><input type="checkbox"
                              name="weekday" value="2" style="margin-right:4px;">Thứ 2</label>
                          <label class="cnj-badge cnj-badge--neutral" style="cursor:pointer;"><input type="checkbox"
                              name="weekday" value="3" style="margin-right:4px;">Thứ 3</label>
                          <label class="cnj-badge cnj-badge--neutral" style="cursor:pointer;"><input type="checkbox"
                              name="weekday" value="4" style="margin-right:4px;">Thứ 4</label>
                          <label class="cnj-badge cnj-badge--neutral" style="cursor:pointer;"><input type="checkbox"
                              name="weekday" value="5" style="margin-right:4px;">Thứ 5</label>
                          <label class="cnj-badge cnj-badge--neutral" style="cursor:pointer;"><input type="checkbox"
                              name="weekday" value="6" style="margin-right:4px;">Thứ 6</label>
                          <label class="cnj-badge cnj-badge--neutral" style="cursor:pointer;"><input type="checkbox"
                              name="weekday" value="7" style="margin-right:4px;">Thứ 7</label>
                          <label class="cnj-badge cnj-badge--neutral" style="cursor:pointer;"><input type="checkbox"
                              name="weekday" value="1" style="margin-right:4px;">Chủ nhật</label>
                        </div>
                      </div>
                      <p class="cnj-form-hint cnj-mb-4">Tối đa 62 ngày mỗi lần. Ngày nào bị trùng lịch phòng sẽ tự động
                        bị bỏ qua (không chặn toàn bộ), hệ thống báo lại số suất đã tạo/bỏ qua sau khi lưu.</p>
                    </div>

                    <script>
                      function cnjToggleShowtimeMode(mode) {
                        document.getElementById('showtimeMode').value = mode;
                        document.getElementById('showtimeSingleFields').style.display = (mode === 'single') ? 'block' : 'none';
                        document.getElementById('showtimeRangeFields').style.display = (mode === 'range') ? 'block' : 'none';
                        document.querySelector('[name="showDate"]').required = (mode === 'single');
                        document.querySelector('[name="rangeStart"]').required = (mode === 'range');
                        document.querySelector('[name="rangeEnd"]').required = (mode === 'range');
                      }
                    </script>
                  </c:otherwise>
            </c:choose>

            <div class="cnj-grid" style="grid-template-columns: 1fr 1fr;">
              <div class="cnj-form-group">
                <label class="cnj-label">Giờ bắt đầu *</label>
                <input class="cnj-input" type="time" name="startTime" value="${showtime.startTime}" required>
              </div>
              <div class="cnj-form-group">
                <label class="cnj-label">Giờ kết thúc *</label>
                <input class="cnj-input" type="time" name="endTime" value="${showtime.endTime}" required>
              </div>
            </div>

            <p class="cnj-form-hint cnj-mb-4">⚠️ Hệ thống sẽ tự động kiểm tra và từ chối nếu phòng đã có suất chiếu khác
              trùng khung giờ.</p>

            <div class="cnj-grid" style="grid-template-columns: 1fr 1fr 1fr;">
              <div class="cnj-form-group">
                <label class="cnj-label">Giá vé Thường (đ) *</label>
                <input class="cnj-input" type="number" name="priceNormal" min="0" step="1000"
                  value="${showtime.priceNormal}" required>
              </div>
              <div class="cnj-form-group">
                <label class="cnj-label">Giá vé VIP (đ) *</label>
                <input class="cnj-input" type="number" name="priceVip" min="0" step="1000" value="${showtime.priceVip}"
                  required>
              </div>
              <div class="cnj-form-group">
                <label class="cnj-label">Giá vé Đôi (đ) *</label>
                <input class="cnj-input" type="number" name="priceCouple" min="0" step="1000"
                  value="${showtime.priceCouple}" required>
              </div>
            </div>
          </div>

          <div class="cnj-card__body" style="border-top:1px solid var(--cnj-border); display:flex; gap:12px;">
            <button type="submit" class="cnj-btn cnj-btn--primary">Lưu suất chiếu</button>
            <a href="${pageContext.request.contextPath}/admin/showtimes" class="cnj-btn cnj-btn--ghost">Hủy</a>
          </div>
        </form>

        <%@ include file="/WEB-INF/views/common/control-footer.jsp" %>