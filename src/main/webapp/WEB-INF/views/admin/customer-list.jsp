<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  AdminCustomerServlet set: customers (List<User>), keyword
--%>
<c:set var="pageTitle" value="Khách hàng — CINEMA CNJ65" scope="request" />
<c:set var="pageHeading" value="Quản lý khách hàng" scope="request" />
<c:set var="activeAdminNav" value="customers" scope="request" />
<%@ include file="/WEB-INF/views/common/control-header.jsp" %>

<div class="cnj-toolbar">
  <form method="get" action="${pageContext.request.contextPath}/admin/customers" class="cnj-toolbar__filters">
    <input class="cnj-input" type="text" name="keyword" placeholder="Tìm tên, email, SĐT..." value="<c:out value="${keyword}" />" style="width:280px;">
    <button type="submit" class="cnj-btn cnj-btn--outline cnj-btn--sm">Tìm</button>
  </form>
</div>

<div class="cnj-table-wrap">
  <table class="cnj-table">
    <thead><tr><th>Họ tên</th><th>Tên đăng nhập</th><th>Email</th><th>SĐT</th><th>Trạng thái</th><th>Hành động</th></tr></thead>
    <tbody>
      <c:forEach var="c" items="${customers}">
        <tr>
          <td><c:out value="${c.fullName}" /></td>
          <td class="cnj-text-mono"><c:out value="${c.username}" /></td>
          <td><c:out value="${c.email}" /></td>
          <td><c:out value="${c.phone}" /></td>
          <td>
            <c:choose>
              <c:when test="${c.status=='ACTIVE'}"><span class="cnj-badge cnj-badge--success">Hoạt động</span></c:when>
              <c:otherwise><span class="cnj-badge cnj-badge--danger">Đã khóa</span></c:otherwise>
            </c:choose>
          </td>
          <td>
            <form method="post" action="${pageContext.request.contextPath}/admin/customers">
              <input type="hidden" name="id" value="${c.id}">
              <c:choose>
                <c:when test="${c.status=='ACTIVE'}">
                  <input type="hidden" name="action" value="lock">
                  <button type="submit" class="cnj-btn cnj-btn--danger cnj-btn--sm" data-confirm="Khóa tài khoản '${c.fullName}'?">Khóa</button>
                </c:when>
                <c:otherwise>
                  <input type="hidden" name="action" value="unlock">
                  <button type="submit" class="cnj-btn cnj-btn--outline cnj-btn--sm">Mở khóa</button>
                </c:otherwise>
              </c:choose>
            </form>
          </td>
        </tr>
      </c:forEach>
      <c:if test="${empty customers}"><tr><td colspan="6" class="cnj-empty-state">Không tìm thấy khách hàng nào.</td></tr></c:if>
    </tbody>
  </table>
</div>

<%@ include file="/WEB-INF/views/common/control-footer.jsp" %>
