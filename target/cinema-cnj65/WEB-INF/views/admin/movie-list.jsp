<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  AdminMovieServlet set: movies (List<Movie>), keyword, selectedStatus
--%>
<c:set var="pageTitle" value="Quản lý phim — CINEMA CNJ65" scope="request" />
<c:set var="pageHeading" value="Quản lý phim" scope="request" />
<c:set var="activeAdminNav" value="movies" scope="request" />
<%@ include file="/WEB-INF/views/common/control-header.jsp" %>

<div class="cnj-toolbar">
  <form method="get" action="${pageContext.request.contextPath}/admin/movies" class="cnj-toolbar__filters">
    <input class="cnj-input" type="text" name="keyword" placeholder="Tìm tên phim..." value="<c:out value="${keyword}" />" style="width:220px;">
    <select class="cnj-select" name="status" data-autosubmit>
      <option value="">Tất cả trạng thái</option>
      <option value="NOW_SHOWING" ${selectedStatus=='NOW_SHOWING'?'selected':''}>Đang chiếu</option>
      <option value="COMING_SOON" ${selectedStatus=='COMING_SOON'?'selected':''}>Sắp chiếu</option>
      <option value="ENDED" ${selectedStatus=='ENDED'?'selected':''}>Ngừng chiếu</option>
    </select>
    <button type="submit" class="cnj-btn cnj-btn--outline cnj-btn--sm">Lọc</button>
  </form>
  <a href="${pageContext.request.contextPath}/admin/movies?action=form" class="cnj-btn cnj-btn--primary">+ Thêm phim mới</a>
</div>

<div class="cnj-table-wrap">
  <table class="cnj-table">
    <thead><tr><th>Poster</th><th>Tên phim</th><th>Thể loại</th><th>Thời lượng</th><th>Trạng thái</th><th>Hành động</th></tr></thead>
    <tbody>
      <c:forEach var="m" items="${movies}">
        <tr>
          <td><img src="${pageContext.request.contextPath}${m.posterUrl}" alt="" style="width:40px;height:60px;object-fit:cover;border-radius:4px;"
                   onerror="this.src='${pageContext.request.contextPath}/assets/images/placeholder-poster.svg'"></td>
          <td><c:out value="${m.title}" /></td>
          <td><c:out value="${m.genreName}" /></td>
          <td>${m.duration} phút</td>
          <td>
            <c:choose>
              <c:when test="${m.status=='NOW_SHOWING'}"><span class="cnj-badge cnj-badge--success">Đang chiếu</span></c:when>
              <c:when test="${m.status=='COMING_SOON'}"><span class="cnj-badge cnj-badge--warning">Sắp chiếu</span></c:when>
              <c:otherwise><span class="cnj-badge cnj-badge--neutral">Ngừng chiếu</span></c:otherwise>
            </c:choose>
          </td>
          <td class="cnj-table__actions">
            <a href="${pageContext.request.contextPath}/admin/movies?action=form&id=${m.id}" class="cnj-btn cnj-btn--outline cnj-btn--sm">Sửa</a>
            <form method="post" action="${pageContext.request.contextPath}/admin/movies" style="display:inline;">
              <input type="hidden" name="action" value="delete">
              <input type="hidden" name="id" value="${m.id}">
              <button type="submit" class="cnj-btn cnj-btn--danger cnj-btn--sm"
                      data-confirm="Xóa phim '${m.title}'? Nếu phim đã có suất chiếu, hệ thống sẽ tự chuyển sang Ngừng chiếu thay vì xóa hẳn.">Xóa</button>
            </form>
          </td>
        </tr>
      </c:forEach>
      <c:if test="${empty movies}"><tr><td colspan="6" class="cnj-empty-state">Không tìm thấy phim nào.</td></tr></c:if>
    </tbody>
  </table>
</div>

<%@ include file="/WEB-INF/views/common/control-footer.jsp" %>
