<%@ page contentType="text/html;charset=UTF-8" language="java" %>
  <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%-- LoginServlet (@WebServlet "/login" ) set (khi co loi): request.setAttribute("error", "..." ) va
      request.setAttribute("username", "..." ) de giu lai gia tri da nhap. --%>
      <c:set var="pageTitle" value="Đăng nhập — CINEMA CNJ65" scope="request" />
      <%@ include file="/WEB-INF/views/common/header.jsp" %>

        <section class="cnj-section">
          <div class="cnj-container">
            <div class="cnj-form-card">
              <h1 class="cnj-fs-h2 cnj-text-center cnj-mb-4">Đăng nhập</h1>

              <c:if test="${param.notice == 'login_required'}">
                <div class="cnj-alert cnj-alert--info">Bạn cần đăng nhập để tiếp tục đặt vé.</div>
              </c:if>
              <c:if test="${param.notice == 'register_success'}">
                <div class="cnj-alert cnj-alert--success" data-autohide>Đăng ký thành công! Hãy đăng nhập để tiếp tục.
                </div>
              </c:if>
              <c:if test="${not empty error}">
                <div class="cnj-alert cnj-alert--error">
                  <c:out value="${error}" />
                </div>
              </c:if>

              <form method="post" action="${pageContext.request.contextPath}/login">
                <div class="cnj-form-group">
                  <label class="cnj-label" for="loginUsername">Tên đăng nhập</label>
                  <input class="cnj-input" type="text" id="loginUsername" name="username" value="<c:out value="
                    ${username}" />" required autofocus>
                </div>
                <div class="cnj-form-group">
                  <label class="cnj-label" for="loginPassword">Mật khẩu</label>
                  <input class="cnj-input" type="password" id="loginPassword" name="password" required>
                </div>
                <button type="submit" class="cnj-btn cnj-btn--primary cnj-btn--block">Đăng nhập</button>
              </form>

              <p class="cnj-text-center cnj-text-mist cnj-mt-4" style="font-size: var(--cnj-fs-small);">
                Chưa có tài khoản?
                <a href="${pageContext.request.contextPath}/register" class="cnj-text-gold">Đăng ký ngay</a>
              </p>

              <!-- <div class="cnj-alert cnj-alert--info cnj-mt-4" style="font-size: var(--cnj-fs-tiny);">
        Tài khoản demo: <strong>khang</strong> / <strong>123456</strong> (khách hàng) ·
        <strong>admin</strong> / <strong>123456</strong> (quản trị) ·
        <strong>staff01</strong> / <strong>123456</strong> (nhân viên)
      </div> -->
            </div>
          </div>
        </section>

        <%@ include file="/WEB-INF/views/common/footer.jsp" %>