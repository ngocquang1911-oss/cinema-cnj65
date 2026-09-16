<%@ page pageEncoding="UTF-8" %>
  <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%-- MovieListServlet (@WebServlet "/movies" ) set: movies (List<Movie>), genres (List<Genre>), keyword,
        selectedGenreId, selectedStatus
        --%>
        <c:set var="pageTitle" value="Danh sách phim — CINEMA CNJ65" scope="request" />
        <c:set var="activeNav" value="movies" scope="request" />
        <%@ include file="/WEB-INF/views/common/header.jsp" %>

          <section class="cnj-section">
            <div class="cnj-container">
              <h1 class="cnj-fs-h2 cnj-mb-4">Danh sách phim</h1>

              <c:if test="${not empty error}">
                <div class="cnj-alert cnj-alert--error">
                  <c:out value="${error}" />
                </div>
              </c:if>

              <form method="get" action="${pageContext.request.contextPath}/movies" class="cnj-card cnj-mb-6">
                <div class="cnj-card__body cnj-grid" style="grid-template-columns: 2fr 1fr 1fr auto; align-items: end;">
                  <div class="cnj-form-group" style="margin-bottom:0;">
                    <label class="cnj-label" for="filterKeyword">Tìm phim</label>
                    <input class="cnj-input" type="text" id="filterKeyword" name="keyword"
                      placeholder="Nhập tên phim..." value="<c:out value=" ${keyword}" />">
                  </div>
                  <div class="cnj-form-group" style="margin-bottom:0;">
                    <label class="cnj-label" for="filterGenre">Thể loại</label>
                    <select class="cnj-select" id="filterGenre" name="genreId" data-autosubmit>
                      <option value="">Tất cả</option>
                      <c:forEach var="g" items="${genres}">
                        <option value="${g.id}" ${selectedGenreId==g.id ? 'selected' : '' }>
                          <c:out value="${g.name}" />
                        </option>
                      </c:forEach>
                    </select>
                  </div>
                  <div class="cnj-form-group" style="margin-bottom:0;">
                    <label class="cnj-label" for="filterStatus">Trạng thái</label>
                    <select class="cnj-select" id="filterStatus" name="status" data-autosubmit>
                      <option value="">Tất cả</option>
                      <option value="NOW_SHOWING" ${selectedStatus=='NOW_SHOWING' ? 'selected' : '' }>Đang chiếu
                      </option>
                      <option value="COMING_SOON" ${selectedStatus=='COMING_SOON' ? 'selected' : '' }>Sắp chiếu</option>
                    </select>
                  </div>
                  <button type="submit" class="cnj-btn cnj-btn--primary">Lọc</button>
                </div>
              </form>

              <c:choose>
                <c:when test="${empty movies}">
                  <p class="cnj-empty-state">Không tìm thấy phim phù hợp.</p>
                </c:when>
                <c:otherwise>
                  <div class="cnj-grid cnj-grid--movies">
                    <c:forEach var="movie" items="${movies}">
                      <a href="${pageContext.request.contextPath}/movie-detail?id=${movie.id}"
                        class="cnj-card cnj-card--hover cnj-poster-card">
                        <div class="cnj-poster-card__image-wrap">
                          <img class="cnj-poster-card__image" src="${pageContext.request.contextPath}${movie.posterUrl}"
                            alt="${movie.title}"
                            onerror="this.src='${pageContext.request.contextPath}/assets/images/placeholder-poster.svg'">
                          <c:if test="${movie.status == 'COMING_SOON'}">
                            <span class="cnj-poster-card__badge cnj-poster-card__badge--gold">Sắp chiếu</span>
                          </c:if>
                          <c:if test="${movie.status == 'NOW_SHOWING'}">
                            <span class="cnj-poster-card__badge">${movie.ageRating}</span>
                          </c:if>
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

          <%@ include file="/WEB-INF/views/common/footer.jsp" %>