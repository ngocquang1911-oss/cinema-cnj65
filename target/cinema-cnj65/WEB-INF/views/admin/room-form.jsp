<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  AdminRoomServlet set: room (Room, chi co khi Sua), error
--%>
<c:set var="pageTitle" value="${empty room ? 'Thêm phòng' : 'Sửa phòng'} — CINEMA CNJ65" scope="request" />
<c:set var="pageHeading" value="${empty room ? 'Thêm phòng chiếu mới' : 'Sửa phòng chiếu'}" scope="request" />
<c:set var="activeAdminNav" value="rooms" scope="request" />
<%@ include file="/WEB-INF/views/common/control-header.jsp" %>

<c:if test="${not empty error}"><div class="cnj-alert cnj-alert--error"><c:out value="${error}" /></div></c:if>

<form method="post" action="${pageContext.request.contextPath}/admin/rooms" class="cnj-card" style="max-width:480px;">
  <input type="hidden" name="action" value="save">
  <c:if test="${not empty room}"><input type="hidden" name="id" value="${room.id}"></c:if>

  <div class="cnj-card__body">
    <div class="cnj-form-group">
      <label class="cnj-label">Tên phòng *</label>
      <input class="cnj-input" type="text" name="name" value="<c:out value="${room.name}" />" required placeholder="Ví dụ: Phòng 01">
    </div>
    <div class="cnj-form-group">
      <label class="cnj-label">Loại phòng</label>
      <select class="cnj-select" name="roomType">
        <option value="2D" ${room.roomType=='2D'?'selected':''}>2D</option>
        <option value="3D" ${room.roomType=='3D'?'selected':''}>3D</option>
        <option value="IMAX" ${room.roomType=='IMAX'?'selected':''}>IMAX</option>
      </select>
    </div>
    <c:if test="${not empty room}">
      <div class="cnj-form-group">
        <label class="cnj-label">Trạng thái</label>
        <select class="cnj-select" name="status">
          <option value="ACTIVE" ${room.status=='ACTIVE'?'selected':''}>Hoạt động</option>
          <option value="MAINTENANCE" ${room.status=='MAINTENANCE'?'selected':''}>Bảo trì</option>
        </select>
      </div>
    </c:if>
    <c:if test="${empty room}">
      <div class="cnj-alert cnj-alert--info">Sau khi tạo phòng, bạn sẽ được chuyển đến bước sinh sơ đồ ghế.</div>
    </c:if>
  </div>

  <div class="cnj-card__body" style="border-top:1px solid var(--cnj-border); display:flex; gap:12px;">
    <button type="submit" class="cnj-btn cnj-btn--primary">Lưu</button>
    <a href="${pageContext.request.contextPath}/admin/rooms" class="cnj-btn cnj-btn--ghost">Hủy</a>
  </div>
</form>

<%@ include file="/WEB-INF/views/common/control-footer.jsp" %>
