<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  AdminRoomServlet set: rooms (List<Room>)
--%>
<c:set var="pageTitle" value="Phòng chiếu — CINEMA CNJ65" scope="request" />
<c:set var="pageHeading" value="Quản lý phòng chiếu" scope="request" />
<c:set var="activeAdminNav" value="rooms" scope="request" />
<%@ include file="/WEB-INF/views/common/control-header.jsp" %>

<c:if test="${param.error == 'in_use'}">
  <div class="cnj-alert cnj-alert--error">Không thể xóa: phòng này đang có suất chiếu.</div>
</c:if>

<div class="cnj-toolbar">
  <div></div>
  <a href="${pageContext.request.contextPath}/admin/rooms?action=form" class="cnj-btn cnj-btn--primary">+ Thêm phòng mới</a>
</div>

<div class="cnj-table-wrap">
  <table class="cnj-table">
    <thead><tr><th>Tên phòng</th><th>Loại</th><th>Số ghế</th><th>Trạng thái</th><th>Hành động</th></tr></thead>
    <tbody>
      <c:forEach var="r" items="${rooms}">
        <tr>
          <td><c:out value="${r.name}" /></td>
          <td><span class="cnj-badge cnj-badge--neutral"><c:out value="${r.roomType}" /></span></td>
          <td>${r.totalSeats}</td>
          <td>
            <c:choose>
              <c:when test="${r.status=='ACTIVE'}"><span class="cnj-badge cnj-badge--success">Hoạt động</span></c:when>
              <c:otherwise><span class="cnj-badge cnj-badge--danger">Bảo trì</span></c:otherwise>
            </c:choose>
          </td>
          <td class="cnj-table__actions">
            <a href="${pageContext.request.contextPath}/admin/rooms?action=seats&id=${r.id}" class="cnj-btn cnj-btn--outline cnj-btn--sm">Sơ đồ ghế</a>
            <a href="${pageContext.request.contextPath}/admin/rooms?action=form&id=${r.id}" class="cnj-btn cnj-btn--outline cnj-btn--sm">Sửa</a>
            <form method="post" action="${pageContext.request.contextPath}/admin/rooms" style="display:inline;">
              <input type="hidden" name="action" value="delete">
              <input type="hidden" name="id" value="${r.id}">
              <button type="submit" class="cnj-btn cnj-btn--danger cnj-btn--sm" data-confirm="Xóa phòng '${r.name}'?">Xóa</button>
            </form>
          </td>
        </tr>
      </c:forEach>
      <c:if test="${empty rooms}"><tr><td colspan="5" class="cnj-empty-state">Chưa có phòng chiếu nào.</td></tr></c:if>
    </tbody>
  </table>
</div>

<%@ include file="/WEB-INF/views/common/control-footer.jsp" %>
