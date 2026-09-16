<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Trang loi dung chung cho 404 / 403 / 500 (khai bao trong WEB-INF/web.xml).
  KHONG hien thi stack trace/thong tin ky thuat ra nguoi dung cuoi (bao mat)
  — chi log ra console server cho lap trinh vien xem.
--%>
<%
  Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
  String message = "Đã có lỗi xảy ra.";
  if (statusCode != null) {
    if (statusCode == 404) message = "Không tìm thấy trang bạn yêu cầu.";
    else if (statusCode == 403) message = "Bạn không có quyền truy cập trang này.";
    else if (statusCode == 500) message = "Hệ thống đang gặp sự cố, vui lòng thử lại sau.";
  }
  if (exception != null) {
    System.err.println("[CINEMA CNJ65 ERROR] " + exception.getMessage());
    exception.printStackTrace();
  }
%>
<c:set var="pageTitle" value="Đã có lỗi xảy ra — CINEMA CNJ65" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<section class="cnj-section cnj-text-center">
  <div class="cnj-container">
    <div style="font-size: 4rem;">🎞️</div>
    <h1 class="cnj-fs-h2 cnj-mb-2"><%= statusCode != null ? statusCode : "" %></h1>
    <p class="cnj-text-mist cnj-mb-6"><%= message %></p>
    <a href="${pageContext.request.contextPath}/home" class="cnj-btn cnj-btn--primary">Về trang chủ</a>
  </div>
</section>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
