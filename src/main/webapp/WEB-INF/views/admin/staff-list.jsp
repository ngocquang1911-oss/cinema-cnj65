<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  AdminStaffServlet set: staffList (List<User>), error
--%>
<c:set var="pageTitle" value="Nhân viên — CINEMA CNJ65" scope="request" />
<c:set var="pageHeading" value="Quản lý nhân viên" scope="request" />
<c:set var="activeAdminNav" value="staff" scope="request" />
<%@ include file="/WEB-INF/views/common/control-header.jsp" %>

<c:if test="${param.success == 'created'}">
  <div class="cnj-alert cnj-alert--success" data-autohide>Đã tạo tài khoản nhân viên thành công.</div>
</c:if>
<c:if test="${not empty error}"><div class="cnj-alert cnj-alert--error"><c:out value="${error}" /></div></c:if>

<div class="cnj-grid" style="grid-template-columns: 1fr 1.6fr; align-items: start;">

  <div class="cnj-card">
    <div class="cnj-card__body">
      <div class="cnj-card__title">Tạo tài khoản nhân viên</div>
      <p class="cnj-form-hint cnj-mb-4">Chỉ Admin mới có quyền tạo tài khoản Nhân viên — nhân viên không tự đăng ký được.</p>
      <form method="post" action="${pageContext.request.contextPath}/admin/staff">
        <div class="cnj-form-group">
          <label class="cnj-label">Họ tên *</label>
          <input class="cnj-input" type="text" name="fullName" required>
        </div>
        <div class="cnj-form-group">
          <label class="cnj-label">Tên đăng nhập *</label>
          <input class="cnj-input" type="text" name="username" required>
        </div>
        <div class="cnj-form-group">
          <label class="cnj-label">Mật khẩu *</label>
          <input class="cnj-input" type="password" name="password" minlength="6" required>
        </div>
        <div class="cnj-form-group">
          <label class="cnj-label">Email *</label>
          <input class="cnj-input" type="email" name="email" required>
        </div>
        <div class="cnj-form-group">
          <label class="cnj-label">Số điện thoại *</label>
          <input class="cnj-input" type="tel" name="phone" required>
        </div>
        <button type="submit" class="cnj-btn cnj-btn--primary cnj-btn--block">Tạo tài khoản</button>
      </form>
    </div>
  </div>

  <div class="cnj-table-wrap">
    <table class="cnj-table">
      <thead><tr><th>Họ tên</th><th>Tên đăng nhập</th><th>Email</th><th>SĐT</th><th>Trạng thái</th><th>Hành động</th></tr></thead>
      <tbody>
        <c:forEach var="s" items="${staffList}">
          <tr>
            <td><c:out value="${s.fullName}" /></td>
            <td class="cnj-text-mono"><c:out value="${s.username}" /></td>
            <td><c:out value="${s.email}" /></td>
            <td><c:out value="${s.phone}" /></td>
            <td>
              <c:choose>
                <c:when test="${s.status=='ACTIVE'}"><span class="cnj-badge cnj-badge--success">Hoạt động</span></c:when>
                <c:otherwise><span class="cnj-badge cnj-badge--danger">Đã khóa</span></c:otherwise>
              </c:choose>
            </td>
            <td>
              <form method="post" action="${pageContext.request.contextPath}/admin/staff">
                <input type="hidden" name="id" value="${s.id}">
                <c:choose>
                  <c:when test="${s.status=='ACTIVE'}">
                    <input type="hidden" name="action" value="lock">
                    <button type="submit" class="cnj-btn cnj-btn--danger cnj-btn--sm" data-confirm="Khóa tài khoản nhân viên '${s.fullName}'?">Khóa</button>
                  </c:when>
                  <c:otherwise>
                    <input type="hidden" name="action" value="unlock">
                    <button type="submit" class="cnj-btn cnj-btn--outline cnj-btn--sm">Mở khóa</button>
                  </c:otherwise>
                </c:choose>
              </form>
            </td>
          </tr>
        </c:forEach>
        <c:if test="${empty staffList}"><tr><td colspan="6" class="cnj-empty-state">Chưa có nhân viên nào.</td></tr></c:if>
      </tbody>
    </table>
  </div>
</div>

<%@ include file="/WEB-INF/views/common/control-footer.jsp" %>
