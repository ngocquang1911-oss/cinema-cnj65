<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%--
  ==========================================================================
  COMMON HEADER — duoc include boi MOI trang qua the:
    <%@ include file="/WEB-INF/views/common/header.jsp" %>
  Servlet co the set request.setAttribute("pageTitle", "...") va
  request.setAttribute("activeNav", "home|movies|tickets|...") TRUOC KHI
  forward toi JSP de header hien thi dung tieu de / highlight dung menu.
  Neu khong set, gia tri mac dinh se duoc dung.
  ==========================================================================
--%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title><c:out value="${not empty pageTitle ? pageTitle : 'CINEMA CNJ65'}" /></title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/variables.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/ticket-stub.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/seat-map.css">
  <c:if test="${not empty extraCss}"><link rel="stylesheet" href="${pageContext.request.contextPath}${extraCss}"></c:if>
</head>
<body>

<nav class="cnj-navbar">
  <div class="cnj-navbar__inner">
    <a href="${pageContext.request.contextPath}/home" class="cnj-navbar__brand">
      <span class="cnj-navbar__brand-mark"></span> CINEMA CNJ65
    </a>

    <button type="button" class="cnj-btn cnj-btn--ghost cnj-navbar__toggle" aria-label="Mo menu">☰</button>

    <ul class="cnj-navbar__links">
      <li><a class="cnj-navbar__link ${activeNav == 'home' ? 'cnj-navbar__link--active' : ''}"
             href="${pageContext.request.contextPath}/home">Trang chủ</a></li>
      <li><a class="cnj-navbar__link ${activeNav == 'movies' ? 'cnj-navbar__link--active' : ''}"
             href="${pageContext.request.contextPath}/movies">Phim</a></li>
      <c:if test="${not empty sessionScope.user}">
        <li><a class="cnj-navbar__link ${activeNav == 'my-tickets' ? 'cnj-navbar__link--active' : ''}"
               href="${pageContext.request.contextPath}/booking/my-tickets">Vé của tôi</a></li>
      </c:if>
      <c:if test="${sessionScope.user.role == 'ADMIN'}">
        <li><a class="cnj-navbar__link ${activeNav == 'admin' ? 'cnj-navbar__link--active' : ''}"
               href="${pageContext.request.contextPath}/admin/dashboard">Quản trị</a></li>
      </c:if>
      <c:if test="${sessionScope.user.role == 'STAFF'}">
        <li><a class="cnj-navbar__link ${activeNav == 'staff' ? 'cnj-navbar__link--active' : ''}"
               href="${pageContext.request.contextPath}/staff/sale">Quầy vé</a></li>
      </c:if>
    </ul>

    <div class="cnj-navbar__actions">
      <c:choose>
        <c:when test="${not empty sessionScope.user}">
          <div class="cnj-navbar__user">
            <span class="cnj-navbar__avatar">
              <c:out value="${fn:substring(sessionScope.user.fullName, 0, 1)}" />
            </span>
            <a href="${pageContext.request.contextPath}/account/profile" class="cnj-navbar__link">
              <c:out value="${sessionScope.user.fullName}" />
            </a>
          </div>
          <a href="${pageContext.request.contextPath}/logout" class="cnj-btn cnj-btn--outline cnj-btn--sm">Đăng xuất</a>
        </c:when>
        <c:otherwise>
          <a href="${pageContext.request.contextPath}/login" class="cnj-btn cnj-btn--outline cnj-btn--sm">Đăng nhập</a>
          <a href="${pageContext.request.contextPath}/register" class="cnj-btn cnj-btn--primary cnj-btn--sm">Đăng ký</a>
        </c:otherwise>
      </c:choose>
    </div>
  </div>
</nav>

<main class="cnj-main">
