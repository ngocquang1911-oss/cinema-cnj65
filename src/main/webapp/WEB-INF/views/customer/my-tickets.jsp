<%@ page pageEncoding="UTF-8" %>
  <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

    <%-- MyTicketsServlet (@WebServlet "/booking/my-tickets" ) set: tickets (List<Ticket>)
      --%>

      <c:set var="pageTitle" value="Vé của tôi — CINEMA CNJ65" scope="request" />
      <c:set var="activeNav" value="my-tickets" scope="request" />

      <%@ include file="/WEB-INF/views/common/header.jsp" %>

        <section class="cnj-section">
          <div class="cnj-container" style="max-width: 780px;">

            <h1 class="cnj-fs-h2 cnj-mb-6">Vé của tôi</h1>

            <c:choose>

              <c:when test="${empty tickets}">
                <div class="cnj-empty-state">
                  <p>Bạn chưa đặt vé nào.</p>

                  <a href="${pageContext.request.contextPath}/movies" class="cnj-btn cnj-btn--primary cnj-mt-4">
                    Đặt vé ngay
                  </a>
                </div>
              </c:when>

              <c:otherwise>
                <c:set var="showTicketDetailButton" value="true" />
                <c:set var="ticketDetailUrl" value="/booking/ticket-detail?id=" />
                <c:forEach var="ticket" items="${tickets}">
                  <%@ include file="/WEB-INF/views/common/_ticket-stub.jspf" %>
                </c:forEach>
              </c:otherwise>

            </c:choose>

          </div>
        </section>

        <%@ include file="/WEB-INF/views/common/footer.jsp" %>