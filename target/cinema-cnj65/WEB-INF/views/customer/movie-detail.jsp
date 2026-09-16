<%@ page pageEncoding="UTF-8" %>

  <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
      <%-- MovieDetailServlet (@WebServlet "/movie-detail" ) set: movie (Movie), showtimes (List<Showtime>),
        selectedDate (String yyyy-MM-dd),
        dateOptions (Map), todayStr (String yyyy-MM-dd)
        --%>
        <c:set var="pageTitle" value="${movie.title} — CINEMA CNJ65" scope="request" />
        <c:set var="activeNav" value="movies" scope="request" />
        <%@ include file="/WEB-INF/views/common/header.jsp" %>

          <section class="cnj-section">
            <div class="cnj-container">
              <div class="cnj-grid" style="grid-template-columns: 280px 1fr; gap: var(--cnj-sp-6);">

                <div>
                  <div class="cnj-poster-card__image-wrap" style="border-radius: var(--cnj-radius-md);">
                    <img class="cnj-poster-card__image" src="${pageContext.request.contextPath}${movie.posterUrl}"
                      alt="${movie.title}"
                      onerror="this.src='${pageContext.request.contextPath}/assets/images/placeholder-poster.svg'">
                  </div>
                  <c:if test="${not empty movie.trailerUrl}">
                    <a href="${movie.trailerUrl}" target="_blank" rel="noopener"
                      class="cnj-btn cnj-btn--outline cnj-btn--block cnj-mt-4">▶ Xem trailer</a>
                  </c:if>
                </div>

                <div>
                  <span class="cnj-badge cnj-badge--neutral cnj-mb-2">
                    <c:out value="${movie.ageRating}" />
                  </span>
                  <h1 class="cnj-fs-hero" style="font-size: clamp(2rem,4vw,3rem);">
                    <c:out value="${movie.title}" />
                  </h1>

                  <div class="cnj-flex cnj-mb-4" style="flex-wrap:wrap;">
                    <span class="cnj-badge cnj-badge--warning">
                      <c:out value="${movie.genreName}" />
                    </span>
                    <span class="cnj-text-mist">${movie.duration} phút</span>
                    <span class="cnj-text-mist">·</span>
                    <span class="cnj-text-mist">Khởi chiếu
                      <c:out value="${movie.releaseDateFormatted}" />
                    </span>
                  </div>

                  <p class="cnj-text-mist cnj-mb-4">
                    <c:out value="${movie.description}" />
                  </p>

                  <div class="cnj-mb-2"><strong>Đạo diễn:</strong> <span class="cnj-text-mist">
                      <c:out value="${movie.director}" />
                    </span></div>
                  <div class="cnj-mb-6"><strong>Diễn viên:</strong> <span class="cnj-text-mist">
                      <c:out value="${movie.actors}" />
                    </span></div>

                  <c:choose>
                    <c:when test="${movie.status == 'COMING_SOON'}">
                      <div class="cnj-alert cnj-alert--info">Phim này chưa mở bán vé — vui lòng quay lại sau ngày khởi
                        chiếu.</div>
                    </c:when>
                    <c:otherwise>
                      <h2 class="cnj-fs-h3 cnj-mb-4">Chọn suất chiếu</h2>

                      <%-- Tab chon nhanh: 7 ngay ke tu hom nay --%>
                        <div class="cnj-flex cnj-mb-4" style="flex-wrap: wrap;">
                          <c:forEach items="${dateOptions}" var="d">
                            <a href="${pageContext.request.contextPath}/movie-detail?id=${movie.id}&date=${d.value}"
                              class="cnj-btn ${d.value == selectedDate ? 'cnj-btn--primary' : 'cnj-btn--outline'} cnj-btn--sm">
                              <c:out value="${d.key}" />
                            </a>
                          </c:forEach>
                        </div>

                        <%-- Chon ngay bat ky (khong gioi han 7 ngay) — vi Admin co the tao suat chieu lap lai xa hon 7
                          ngay (toi da 62 ngay), khach hang can co cach xem duoc nhung ngay do ma khong chi phu thuoc
                          vao 7 nut tab phia tren. --%>
                          <form method="get" action="${pageContext.request.contextPath}/movie-detail"
                            class="cnj-flex cnj-mb-6">
                            <input type="hidden" name="id" value="${movie.id}">
                            <label class="cnj-label" for="jumpDate" style="margin-bottom:0;">Hoặc chọn ngày
                              khác:</label>
                            <input class="cnj-input" type="date" id="jumpDate" name="date" value="${selectedDate}"
                              min="${todayStr}" style="max-width:180px;" data-autosubmit>
                          </form>

                          <c:choose>
                            <c:when test="${empty showtimes}">
                              <p class="cnj-empty-state">Không có suất chiếu nào trong ngày này.</p>
                            </c:when>
                            <c:otherwise>
                              <div class="cnj-flex" style="flex-wrap: wrap;">
                                <c:forEach var="st" items="${showtimes}">
                                  <a href="${pageContext.request.contextPath}/booking/seats?showtimeId=${st.id}"
                                    class="cnj-card cnj-card--hover"
                                    style="padding: var(--cnj-sp-3) var(--cnj-sp-4); text-align:center;">
                                    <div class="cnj-text-gold cnj-text-mono" style="font-size:1.1rem; font-weight:700;">
                                      <c:out value="${st.startTimeFormatted}" />
                                    </div>
                                    <div class="cnj-text-mist" style="font-size: var(--cnj-fs-tiny);">
                                      <c:out value="${st.roomName}" />
                                    </div>
                                  </a>
                                </c:forEach>
                              </div>
                            </c:otherwise>
                          </c:choose>
                    </c:otherwise>
                  </c:choose>
                </div>
              </div>
            </div>
          </section>

          <%@ include file="/WEB-INF/views/common/footer.jsp" %>