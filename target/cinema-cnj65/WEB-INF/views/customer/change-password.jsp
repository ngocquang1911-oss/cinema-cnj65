<%@ page pageEncoding="UTF-8" %>
  <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%-- ChangePasswordServlet (@WebServlet "/account/change-password" ) set: error, success --%>
      <c:set var="pageTitle" value="Đổi mật khẩu — CINEMA CNJ65" scope="request" />
      <%@ include file="/WEB-INF/views/common/header.jsp" %>

        <section class="cnj-section">
          <div class="cnj-container">
            <div class="cnj-form-card">
              <h1 class="cnj-fs-h2 cnj-text-center cnj-mb-4">Đổi mật khẩu</h1>

              <c:if test="${not empty error}">
                <div class="cnj-alert cnj-alert--error">
                  <c:out value="${error}" />
                </div>
              </c:if>
              <c:if test="${not empty success}">
                <div class="cnj-alert cnj-alert--success" data-autohide>
                  <c:out value="${success}" />
                </div>
              </c:if>

              <form method="post" action="${pageContext.request.contextPath}/account/change-password">
                <div class="cnj-form-group">
                  <label class="cnj-label" for="cpOld">Mật khẩu hiện tại</label>
                  <input class="cnj-input" type="password" id="cpOld" name="oldPassword" required>
                </div>
                <div class="cnj-form-group">
                  <label class="cnj-label" for="cpNew">Mật khẩu mới</label>
                  <input class="cnj-input" type="password" id="cpNew" name="newPassword" required minlength="6">
                </div>
                <div class="cnj-form-group">
                  <label class="cnj-label" for="cpConfirm">Xác nhận mật khẩu mới</label>
                  <input class="cnj-input" type="password" id="cpConfirm" name="confirmPassword" required minlength="6">
                </div>
                <button type="submit" class="cnj-btn cnj-btn--primary cnj-btn--block">Đổi mật khẩu</button>
              </form>

              <a href="${pageContext.request.contextPath}/account/profile"
                class="cnj-btn cnj-btn--ghost cnj-btn--block cnj-mt-4">← Quay lại hồ sơ</a>
            </div>
          </div>
        </section>

        <%@ include file="/WEB-INF/views/common/footer.jsp" %>