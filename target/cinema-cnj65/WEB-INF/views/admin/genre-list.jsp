<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  AdminGenreServlet set: genres (List<Genre>)
--%>
<c:set var="pageTitle" value="Thể loại phim — CINEMA CNJ65" scope="request" />
<c:set var="pageHeading" value="Quản lý thể loại phim" scope="request" />
<c:set var="activeAdminNav" value="genres" scope="request" />
<%@ include file="/WEB-INF/views/common/control-header.jsp" %>

<c:if test="${param.error == 'in_use'}">
  <div class="cnj-alert cnj-alert--error">Không thể xóa: thể loại này đang được gán cho ít nhất 1 phim.</div>
</c:if>

<div class="cnj-grid" style="grid-template-columns: 1fr 2fr; align-items: start;">

  <div class="cnj-card">
    <div class="cnj-card__body">
      <div class="cnj-card__title">Thêm thể loại mới</div>
      <form method="post" action="${pageContext.request.contextPath}/admin/genres">
        <input type="hidden" name="action" value="add">
        <div class="cnj-form-group">
          <input class="cnj-input" type="text" name="name" placeholder="Tên thể loại..." required>
        </div>
        <button type="submit" class="cnj-btn cnj-btn--primary cnj-btn--block">Thêm</button>
      </form>
    </div>
  </div>

  <div class="cnj-table-wrap">
    <table class="cnj-table">
      <thead><tr><th>ID</th><th>Tên thể loại</th><th>Hành động</th></tr></thead>
      <tbody>
        <c:forEach var="g" items="${genres}">
          <tr>
            <td class="cnj-text-mono">#${g.id}</td>
            <td>
              <form method="post" action="${pageContext.request.contextPath}/admin/genres" style="display:flex; gap:8px;">
                <input type="hidden" name="action" value="edit">
                <input type="hidden" name="id" value="${g.id}">
                <input class="cnj-input" type="text" name="name" value="<c:out value="${g.name}" />" style="max-width:220px;">
                <button type="submit" class="cnj-btn cnj-btn--outline cnj-btn--sm">Lưu</button>
              </form>
            </td>
            <td>
              <form method="post" action="${pageContext.request.contextPath}/admin/genres">
                <input type="hidden" name="action" value="delete">
                <input type="hidden" name="id" value="${g.id}">
                <button type="submit" class="cnj-btn cnj-btn--danger cnj-btn--sm"
                        data-confirm="Xóa thể loại '${g.name}'?">Xóa</button>
              </form>
            </td>
          </tr>
        </c:forEach>
        <c:if test="${empty genres}">
          <tr><td colspan="3" class="cnj-empty-state">Chưa có thể loại nào.</td></tr>
        </c:if>
      </tbody>
    </table>
  </div>
</div>

<%@ include file="/WEB-INF/views/common/control-footer.jsp" %>
