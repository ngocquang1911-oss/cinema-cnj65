<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  StaffSaleServlet set: nowShowingMovies (List<Movie>), selectedMovieId (optional),
  showtimesToday (List<Showtime>, chi co khi da chon phim)
--%>
<c:set var="pageTitle" value="Bán vé tại quầy — CINEMA CNJ65" scope="request" />
<c:set var="pageHeading" value="Bán vé tại quầy" scope="request" />
<c:set var="activeAdminNav" value="sale" scope="request" />
<%@ include file="/WEB-INF/views/common/control-header.jsp" %>

<p class="cnj-text-mist cnj-mb-4">Bước 1: Chọn phim đang chiếu hôm nay để xem các suất còn lại.</p>

<div class="cnj-grid cnj-grid--movies cnj-mb-6">
  <c:forEach var="m" items="${nowShowingMovies}">
    <a href="${pageContext.request.contextPath}/staff/sale?movieId=${m.id}"
       class="cnj-card cnj-card--hover ${selectedMovieId == m.id ? 'cnj-card--hover' : ''}"
       style="${selectedMovieId == m.id ? 'border-color:var(--cnj-gold);' : ''} padding: var(--cnj-sp-3); text-align:center;">
      <div class="cnj-card__title" style="font-size:var(--cnj-fs-small);"><c:out value="${m.title}" /></div>
      <div class="cnj-card__meta">${m.duration} phút</div>
    </a>
  </c:forEach>
  <c:if test="${empty nowShowingMovies}"><p class="cnj-empty-state">Không có phim nào đang chiếu.</p></c:if>
</div>

<c:if test="${not empty selectedMovieId}">
  <p class="cnj-text-mist cnj-mb-4">Bước 2: Chọn suất chiếu hôm nay.</p>
  <div class="cnj-flex" style="flex-wrap: wrap;">
    <c:forEach var="st" items="${showtimesToday}">
      <a href="${pageContext.request.contextPath}/staff/sale?action=seats&showtimeId=${st.id}"
         class="cnj-card cnj-card--hover" style="padding: var(--cnj-sp-3) var(--cnj-sp-4); text-align:center;">
        <div class="cnj-text-gold cnj-text-mono" style="font-size:1.1rem; font-weight:700;"><c:out value="${st.startTimeFormatted}" /></div>
        <div class="cnj-text-mist" style="font-size: var(--cnj-fs-tiny);"><c:out value="${st.roomName}" /></div>
      </a>
    </c:forEach>
    <c:if test="${empty showtimesToday}"><p class="cnj-empty-state">Phim này không có suất chiếu hôm nay.</p></c:if>
  </div>
</c:if>

<%@ include file="/WEB-INF/views/common/control-footer.jsp" %>
