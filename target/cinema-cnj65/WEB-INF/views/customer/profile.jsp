<%@ page pageEncoding="UTF-8" %>
  <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%-- ProfileServlet (@WebServlet "/account/profile" ) set: profileUser (User), error, success --%>
      <c:set var="pageTitle" value="Hồ sơ cá nhân — CINEMA CNJ65" scope="request" />
      <%@ include file="/WEB-INF/views/common/header.jsp" %>

        <section class="cnj-section">
          <div class="cnj-container">
            <div class="cnj-form-card">
              <h1 class="cnj-fs-h2 cnj-text-center cnj-mb-4">Hồ sơ cá nhân</h1>

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

              <form method="post" action="${pageContext.request.contextPath}/account/profile">
                <div class="cnj-form-group">
                  <label class="cnj-label" for="pfUsername">Tên đăng nhập</label>
                  <input class="cnj-input" type="text" id="pfUsername" value="<c:out value="
                    ${profileUser.username}" />" disabled>
                  <p class="cnj-form-hint">Không thể thay đổi tên đăng nhập.</p>
                </div>
                <div class="cnj-form-group">
                  <label class="cnj-label" for="pfFullName">Họ và tên</label>
                  <input class="cnj-input" type="text" id="pfFullName" name="fullName" value="<c:out value="
                    ${profileUser.fullName}" />" required>
                </div>
                <div class="cnj-form-group">
                  <label class="cnj-label" for="pfEmail">Email</label>
                  <input class="cnj-input" type="email" id="pfEmail" name="email" value="<c:out value="
                    ${profileUser.email}" />" required>
                </div>
                <div class="cnj-form-group">
                  <label class="cnj-label" for="pfPhone">Số điện thoại</label>
                  <input class="cnj-input" type="tel" id="pfPhone" name="phone" value="<c:out value="
                    ${profileUser.phone}" />" required>
                </div>
                <button type="submit" class="cnj-btn cnj-btn--primary cnj-btn--block">Lưu thay đổi</button>
              </form>

              <a href="${pageContext.request.contextPath}/account/change-password"
                class="cnj-btn cnj-btn--outline cnj-btn--block cnj-mt-4">Đổi mật khẩu</a>
            </div>
          </div>
        </section>

        <%@ include file="/WEB-INF/views/common/footer.jsp" %>