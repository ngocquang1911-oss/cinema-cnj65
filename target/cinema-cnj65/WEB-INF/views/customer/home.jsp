<%@ page pageEncoding="UTF-8" %>
  <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
      <%-- HomeServlet (@WebServlet {"/","/home"}) phai set 2 attribute: request.setAttribute("nowShowing", List<Movie>)
        request.setAttribute("comingSoon", List<Movie>)
          truoc khi forward toi day.
          --%>
          <c:set var="pageTitle" value="CINEMA CNJ65 — Đặt vé xem phim online" scope="request" />
          <c:set var="activeNav" value="home" scope="request" />
          <%@ include file="/WEB-INF/views/common/header.jsp" %>

            <section class="cnj-hero">
              <div class="cnj-container cnj-hero__inner">
                <p class="cnj-text-gold cnj-text-mono" style="letter-spacing:0.25em; font-size: var(--cnj-fs-tiny);">
                  SUẤT CHIẾU MỖI NGÀY</p>
                <h1 style="font-size: var(--cnj-fs-hero); margin: var(--cnj-sp-2) 0;">
                  Đắm mình vào<br><span class="cnj-text-gold">màn ảnh rộng</span>
                </h1>
                <p class="cnj-text-mist" style="max-width: 480px;">
                  Chọn phim, chọn ghế, thanh toán — chỉ trong vài bước. Vé điện tử gửi ngay sau khi đặt thành công.
                </p>
                <a href="${pageContext.request.contextPath}/movies" class="cnj-btn cnj-btn--primary cnj-mt-4">Đặt vé
                  ngay</a>
              </div>
            </section>

            <div class="cnj-filmstrip-divider"></div>

            <section class="cnj-section">
              <div class="cnj-container">
                <div class="cnj-flex--between cnj-mb-4">
                  <h2 class="cnj-fs-h2">Đang chiếu</h2>
                  <a href="${pageContext.request.contextPath}/movies?status=NOW_SHOWING"
                    class="cnj-btn cnj-btn--ghost cnj-btn--sm">Xem tất cả →</a>
                </div>

                <c:choose>
                  <c:when test="${empty nowShowing}">
                    <p class="cnj-empty-state">Hiện chưa có phim nào đang chiếu.</p>
                  </c:when>
                  <c:otherwise>
                    <div class="cnj-grid cnj-grid--movies">
                      <c:forEach var="movie" items="${nowShowing}">
                        <a href="${pageContext.request.contextPath}/movie-detail?id=${movie.id}"
                          class="cnj-card cnj-card--hover cnj-poster-card">
                          <div class="cnj-poster-card__image-wrap">
                            <img class="cnj-poster-card__image"
                              src="${pageContext.request.contextPath}${movie.posterUrl}" alt="${movie.title}"
                              onerror="this.src='${pageContext.request.contextPath}/assets/images/placeholder-poster.svg'">
                            <span class="cnj-poster-card__badge">${movie.ageRating}</span>
                          </div>
                          <div class="cnj-card__body">
                            <div class="cnj-card__title">
                              <c:out value="${movie.title}" />
                            </div>
                            <div class="cnj-card__meta">
                              <c:out value="${movie.genreName}" /> · ${movie.duration} phút
                            </div>
                          </div>
                        </a>
                      </c:forEach>
                    </div>
                  </c:otherwise>
                </c:choose>
              </div>
            </section>

            <div class="cnj-filmstrip-divider"></div>

            <section class="cnj-section cnj-section--alt">
              <div class="cnj-container">
                <div class="cnj-flex--between cnj-mb-4">
                  <h2 class="cnj-fs-h2">Sắp chiếu</h2>
                  <a href="${pageContext.request.contextPath}/movies?status=COMING_SOON"
                    class="cnj-btn cnj-btn--ghost cnj-btn--sm">Xem tất cả →</a>
                </div>

                <c:choose>
                  <c:when test="${empty comingSoon}">
                    <p class="cnj-empty-state">Chưa có lịch phim sắp chiếu.</p>
                  </c:when>
                  <c:otherwise>
                    <div class="cnj-grid cnj-grid--movies">
                      <c:forEach var="movie" items="${comingSoon}">
                        <a href="${pageContext.request.contextPath}/movie-detail?id=${movie.id}"
                          class="cnj-card cnj-card--hover cnj-poster-card">
                          <div class="cnj-poster-card__image-wrap">
                            <img class="cnj-poster-card__image"
                              src="${pageContext.request.contextPath}${movie.posterUrl}" alt="${movie.title}"
                              onerror="this.src='${pageContext.request.contextPath}/assets/images/placeholder-poster.svg'">
                            <span class="cnj-poster-card__badge cnj-poster-card__badge--gold">Sắp chiếu</span>
                          </div>
                          <div class="cnj-card__body">
                            <div class="cnj-card__title">
                              <c:out value="${movie.title}" />
                            </div>
                            <div class="cnj-card__meta">
                              <c:out value="${movie.releaseDateFormatted}" /> ·
                              <c:out value="${movie.genreName}" />
                            </div>
                          </div>
                        </a>
                      </c:forEach>
                    </div>
                  </c:otherwise>
                </c:choose>
              </div>
            </section>

            <%@ include file="/WEB-INF/views/common/footer.jsp" %>