<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  RegisterServlet (@WebServlet "/register") set khi co loi:
    error, fullName, username, email, phone (giu lai gia tri da nhap)
--%>
<c:set var="pageTitle" value="Đăng ký tài khoản — CINEMA CNJ65" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<section class="cnj-section">
  <div class="cnj-container">
    <div class="cnj-form-card">
      <h1 class="cnj-fs-h2 cnj-text-center cnj-mb-4">Tạo tài khoản</h1>

      <c:if test="${not empty error}">
        <div class="cnj-alert cnj-alert--error"><c:out value="${error}" /></div>
      </c:if>

      <form method="post" action="${pageContext.request.contextPath}/register">
        <div class="cnj-form-group">
          <label class="cnj-label" for="regFullName">Họ và tên</label>
          <input class="cnj-input" type="text" id="regFullName" name="fullName"
                 value="<c:out value="${fullName}" />" required autofocus>
        </div>
        <div class="cnj-form-group">
          <label class="cnj-label" for="regUsername">Tên đăng nhập</label>
          <input class="cnj-input" type="text" id="regUsername" name="username"
                 value="<c:out value="${username}" />" required>
          <p class="cnj-form-hint">4-30 ký tự, chỉ gồm chữ, số, gạch dưới.</p>
        </div>
        <div class="cnj-grid cnj-grid--2col">
          <div class="cnj-form-group">
            <label class="cnj-label" for="regPassword">Mật khẩu</label>
            <input class="cnj-input" type="password" id="regPassword" name="password" required minlength="6">
          </div>
          <div class="cnj-form-group">
            <label class="cnj-label" for="regConfirmPassword">Xác nhận mật khẩu</label>
            <input class="cnj-input" type="password" id="regConfirmPassword" name="confirmPassword" required minlength="6">
          </div>
        </div>
        <div class="cnj-form-group">
          <label class="cnj-label" for="regEmail">Email</label>
          <input class="cnj-input" type="email" id="regEmail" name="email"
                 value="<c:out value="${email}" />" required>
        </div>
        <div class="cnj-form-group">
          <label class="cnj-label" for="regPhone">Số điện thoại</label>
          <input class="cnj-input" type="tel" id="regPhone" name="phone"
                 value="<c:out value="${phone}" />" placeholder="09xxxxxxxx" required>
        </div>
        <button type="submit" class="cnj-btn cnj-btn--primary cnj-btn--block">Đăng ký</button>
      </form>

      <p class="cnj-text-center cnj-text-mist cnj-mt-4" style="font-size: var(--cnj-fs-small);">
        Đã có tài khoản?
        <a href="${pageContext.request.contextPath}/login" class="cnj-text-gold">Đăng nhập</a>
      </p>
    </div>
  </div>
</section>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
