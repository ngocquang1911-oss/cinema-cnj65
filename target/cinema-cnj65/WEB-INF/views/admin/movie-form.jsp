<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  AdminMovieServlet set: movie (Movie, chi co khi Sua), genres (List<Genre>), error
--%>
<c:set var="pageTitle" value="${empty movie ? 'Thêm phim' : 'Sửa phim'} — CINEMA CNJ65" scope="request" />
<c:set var="pageHeading" value="${empty movie ? 'Thêm phim mới' : 'Sửa thông tin phim'}" scope="request" />
<c:set var="activeAdminNav" value="movies" scope="request" />
<%@ include file="/WEB-INF/views/common/control-header.jsp" %>

<c:if test="${not empty error}"><div class="cnj-alert cnj-alert--error"><c:out value="${error}" /></div></c:if>

<form method="post" action="${pageContext.request.contextPath}/admin/movies" class="cnj-card">
  <input type="hidden" name="action" value="save">
  <c:if test="${not empty movie}"><input type="hidden" name="id" value="${movie.id}"></c:if>

  <div class="cnj-card__body">
    <div class="cnj-grid cnj-grid--2col">
      <div class="cnj-form-group">
        <label class="cnj-label">Tên phim *</label>
        <input class="cnj-input" type="text" name="title" value="<c:out value="${movie.title}" />" required>
      </div>
      <div class="cnj-form-group">
        <label class="cnj-label">Thể loại *</label>
        <select class="cnj-select" name="genreId" required>
          <option value="">-- Chọn thể loại --</option>
          <c:forEach var="g" items="${genres}">
            <option value="${g.id}" ${movie.genreId == g.id ? 'selected' : ''}><c:out value="${g.name}" /></option>
          </c:forEach>
        </select>
      </div>
    </div>

    <div class="cnj-grid cnj-grid--2col">
      <div class="cnj-form-group">
        <label class="cnj-label">Đạo diễn</label>
        <input class="cnj-input" type="text" name="director" value="<c:out value="${movie.director}" />">
      </div>
      <div class="cnj-form-group">
        <label class="cnj-label">Diễn viên</label>
        <input class="cnj-input" type="text" name="actors" value="<c:out value="${movie.actors}" />">
      </div>
    </div>

    <div class="cnj-grid" style="grid-template-columns: 1fr 1fr 1fr;">
      <div class="cnj-form-group">
        <label class="cnj-label">Thời lượng (phút) *</label>
        <input class="cnj-input" type="number" name="duration" min="1" value="${movie.duration}" required>
      </div>
      <div class="cnj-form-group">
        <label class="cnj-label">Giới hạn tuổi</label>
        <select class="cnj-select" name="ageRating">
          <option value="P" ${movie.ageRating=='P'?'selected':''}>P</option>
          <option value="C13" ${movie.ageRating=='C13'?'selected':''}>C13</option>
          <option value="C16" ${movie.ageRating=='C16'?'selected':''}>C16</option>
          <option value="C18" ${movie.ageRating=='C18'?'selected':''}>C18</option>
        </select>
      </div>
      <div class="cnj-form-group">
        <label class="cnj-label">Ngày khởi chiếu</label>
        <input class="cnj-input" type="date" name="releaseDate" value="${movie.releaseDate}">
      </div>
    </div>

    <div class="cnj-form-group">
      <label class="cnj-label">Trạng thái</label>
      <select class="cnj-select" name="status">
        <option value="COMING_SOON" ${movie.status=='COMING_SOON'?'selected':''}>Sắp chiếu</option>
        <option value="NOW_SHOWING" ${movie.status=='NOW_SHOWING'?'selected':''}>Đang chiếu</option>
        <option value="ENDED" ${movie.status=='ENDED'?'selected':''}>Ngừng chiếu</option>
      </select>
    </div>

    <div class="cnj-grid cnj-grid--2col">
      <div class="cnj-form-group">
        <label class="cnj-label">URL Poster</label>
        <input class="cnj-input" type="text" name="posterUrl" value="<c:out value="${movie.posterUrl}" />" placeholder="/assets/images/... hoặc để trống dùng ảnh mặc định">
      </div>
      <div class="cnj-form-group">
        <label class="cnj-label">URL Trailer</label>
        <input class="cnj-input" type="text" name="trailerUrl" value="<c:out value="${movie.trailerUrl}" />" placeholder="https://youtube.com/...">
      </div>
    </div>

    <div class="cnj-form-group">
      <label class="cnj-label">Mô tả</label>
      <textarea class="cnj-textarea" name="description" rows="4"><c:out value="${movie.description}" /></textarea>
    </div>
  </div>

  <div class="cnj-card__body" style="border-top:1px solid var(--cnj-border); display:flex; gap:12px;">
    <button type="submit" class="cnj-btn cnj-btn--primary">Lưu phim</button>
    <a href="${pageContext.request.contextPath}/admin/movies" class="cnj-btn cnj-btn--ghost">Hủy</a>
  </div>
</form>

<%@ include file="/WEB-INF/views/common/control-footer.jsp" %>
