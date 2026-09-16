<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%--
  HEADER DUNG CHUNG CHO KHU VUC QUAN TRI (Admin) VA QUAY VE (Staff).
  Menu sidebar TU DONG doi theo sessionScope.user.role — dung 1 fragment
  duy nhat thay vi tach rieng admin-header/staff-header de tranh trung lap
  va lech giao dien giua 2 khu vuc.

  Servlet set request.setAttribute("activeAdminNav", "dashboard|movies|...")
  de highlight dung muc dang chon.
--%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title><c:out value="${not empty pageTitle ? pageTitle : 'Quản trị — CINEMA CNJ65'}" /></title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/variables.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/ticket-stub.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/seat-map.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
</head>
<body>
<div class="cnj-admin-layout">

  <aside class="cnj-admin-sidebar">
    <div class="cnj-admin-sidebar__brand">
      <span class="cnj-navbar__brand-mark"></span> CNJ65
    </div>

    <c:if test="${sessionScope.user.role == 'ADMIN'}">
      <div class="cnj-admin-sidebar__group-label">Tổng quan</div>
      <a class="cnj-admin-sidebar__link ${activeAdminNav == 'dashboard' ? 'cnj-admin-sidebar__link--active' : ''}"
         href="${pageContext.request.contextPath}/admin/dashboard">📊 Dashboard</a>

      <div class="cnj-admin-sidebar__group-label">Nội dung</div>
      <a class="cnj-admin-sidebar__link ${activeAdminNav == 'movies' ? 'cnj-admin-sidebar__link--active' : ''}"
         href="${pageContext.request.contextPath}/admin/movies">🎬 Phim</a>
      <a class="cnj-admin-sidebar__link ${activeAdminNav == 'genres' ? 'cnj-admin-sidebar__link--active' : ''}"
         href="${pageContext.request.contextPath}/admin/genres">🏷️ Thể loại</a>

      <div class="cnj-admin-sidebar__group-label">Vận hành rạp</div>
      <a class="cnj-admin-sidebar__link ${activeAdminNav == 'rooms' ? 'cnj-admin-sidebar__link--active' : ''}"
         href="${pageContext.request.contextPath}/admin/rooms">🚪 Phòng chiếu</a>
      <a class="cnj-admin-sidebar__link ${activeAdminNav == 'showtimes' ? 'cnj-admin-sidebar__link--active' : ''}"
         href="${pageContext.request.contextPath}/admin/showtimes">🕒 Suất chiếu</a>
      <a class="cnj-admin-sidebar__link ${activeAdminNav == 'tickets' ? 'cnj-admin-sidebar__link--active' : ''}"
         href="${pageContext.request.contextPath}/admin/tickets">🎟️ Vé đã bán</a>

      <div class="cnj-admin-sidebar__group-label">Con người</div>
      <a class="cnj-admin-sidebar__link ${activeAdminNav == 'customers' ? 'cnj-admin-sidebar__link--active' : ''}"
         href="${pageContext.request.contextPath}/admin/customers">👤 Khách hàng</a>
      <a class="cnj-admin-sidebar__link ${activeAdminNav == 'staff' ? 'cnj-admin-sidebar__link--active' : ''}"
         href="${pageContext.request.contextPath}/admin/staff">🧑‍💼 Nhân viên</a>
    </c:if>

    <c:if test="${sessionScope.user.role == 'STAFF'}">
      <div class="cnj-admin-sidebar__group-label">Quầy vé</div>
      <a class="cnj-admin-sidebar__link ${activeAdminNav == 'sale' ? 'cnj-admin-sidebar__link--active' : ''}"
         href="${pageContext.request.contextPath}/staff/sale">🎟️ Bán vé</a>
      <a class="cnj-admin-sidebar__link ${activeAdminNav == 'checkin' ? 'cnj-admin-sidebar__link--active' : ''}"
         href="${pageContext.request.contextPath}/staff/checkin">✅ Check-in vé</a>
    </c:if>

    <div class="cnj-admin-sidebar__group-label">Tài khoản</div>
    <a class="cnj-admin-sidebar__link" href="${pageContext.request.contextPath}/home">🏠 Về trang chủ</a>
    <a class="cnj-admin-sidebar__link" href="${pageContext.request.contextPath}/logout">🚪 Đăng xuất</a>
  </aside>

  <main class="cnj-admin-content">
    <div class="cnj-admin-topbar">
      <div>
        <h1 class="cnj-fs-h2"><c:out value="${not empty pageHeading ? pageHeading : pageTitle}" /></h1>
      </div>
      <div class="cnj-navbar__user">
        <span class="cnj-navbar__avatar"><c:out value="${fn:substring(sessionScope.user.fullName,0,1)}" /></span>
        <span><c:out value="${sessionScope.user.fullName}" /> · <c:out value="${sessionScope.user.role}" /></span>
      </div>
    </div>
