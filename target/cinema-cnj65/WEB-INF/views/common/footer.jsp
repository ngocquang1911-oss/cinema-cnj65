<%@ page pageEncoding="UTF-8" %>
  </main>

  <footer class="cnj-footer">
    <div class="cnj-container">
      <div class="cnj-footer__grid">
        <div>
          <div class="cnj-navbar__brand cnj-mb-2">
            <span class="cnj-navbar__brand-mark"></span> CINEMA CNJ65
          </div>
          <p>Hệ thống quản lý vận hành rạp chiếu phim — đặt vé nhanh, chọn ghế trực quan, nhận vé điện tử tức thì.</p>
        </div>
        <div>
          <div class="cnj-footer__heading">Khám phá</div>
          <ul>
            <li class="cnj-mb-2"><a href="${pageContext.request.contextPath}/movies?status=NOW_SHOWING">Phim đang
                chiếu</a></li>
            <li class="cnj-mb-2"><a href="${pageContext.request.contextPath}/movies?status=COMING_SOON">Phim sắp
                chiếu</a></li>
          </ul>
        </div>
        <div>
          <div class="cnj-footer__heading">Hỗ trợ</div>
          <ul>
            <li class="cnj-mb-2">Hotline: 1900 1234</li>
            <li class="cnj-mb-2">Email: hotro@cinemacnj65.vn</li>
          </ul>
        </div>
      </div>
      <div class="cnj-footer__bottom">
        &copy; 2026 CINEMA CNJ65 — Đồ án môn học, không dùng cho mục đích thương mại.
      </div>
    </div>
  </footer>

  <script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
  <c:if test="${not empty extraJs}">
    <script src="${pageContext.request.contextPath}${extraJs}"></script>
  </c:if>
  </body>

  </html>