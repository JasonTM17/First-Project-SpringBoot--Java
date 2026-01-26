<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

            <!DOCTYPE html>
            <html lang="en">

            <head>
                <meta charset="utf-8" />
                <meta http-equiv="X-UA-Compatible" content="IE=edge" />
                <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
                <meta name="description" content="Admin - Laptop Shop" />
                <title>Manage Orders | Admin</title>

                <link href="/css/styles.css" rel="stylesheet" />
                <script src="https://use.fontawesome.com/releases/v6.3.0/js/all.js" crossorigin="anonymous"></script>
            </head>

            <body class="sb-nav-fixed">

                <jsp:include page="../layout/header.jsp" />

                <div id="layoutSidenav">
                    <jsp:include page="../layout/sidebar.jsp" />

                    <div id="layoutSidenav_content">
                        <main>
                            <div class="container-fluid px-4">

                                <h1 class="mt-4">Manage Orders</h1>

                                <ol class="breadcrumb mb-4">
                                    <li class="breadcrumb-item">
                                        <a href="/admin">Dashboard</a>
                                    </li>
                                    <li class="breadcrumb-item active">Order</li>
                                </ol>

                                <div class="mt-5">
                                    <div class="row">
                                        <div class="col-12 mx-auto">

                                            <h3>Table Orders</h3>
                                            <hr />

                                            <table class="table table-bordered table-hover align-middle">
                                                <thead class="table-light">
                                                    <tr>
                                                        <th>ID</th>
                                                        <th>Total Price</th>
                                                        <th>User</th>
                                                        <th>Status</th>
                                                        <th style="width: 220px;">Action</th>
                                                    </tr>
                                                </thead>

                                                <tbody>
                                                    <c:forEach var="order" items="${orders}">
                                                        <tr>
                                                            <td>${order.id}</td>

                                                            <td>
                                                                <fmt:formatNumber value="${order.totalPrice}"
                                                                    type="number" groupingUsed="true" /> đ
                                                            </td>

                                                            <td>${order.user.fullName}</td>

                                                            <td>
                                                                <span class="badge 
                                                        ${order.status == 'COMPLETE' ? 'bg-success' :
                                                          order.status == 'PENDING' ? 'bg-warning text-dark' :
                                                          'bg-secondary'}">
                                                                    ${order.status}
                                                                </span>
                                                            </td>

                                                            <td>
                                                                <a href="/admin/order/${order.id}"
                                                                    class="btn btn-success btn-sm">
                                                                    View
                                                                </a>

                                                                <a href="/admin/order/update/${order.id}"
                                                                    class="btn btn-warning btn-sm mx-1">
                                                                    Update
                                                                </a>

                                                                <a href="/admin/order/delete/${order.id}"
                                                                    class="btn btn-danger btn-sm">
                                                                    Delete
                                                                </a>
                                                            </td>
                                                        </tr>
                                                    </c:forEach>
                                                    <c:if test="${empty orders}">
                                                        <tr>
                                                            <td colspan="6" style="text-align: center;">
                                                                Không có order
                                                            </td>
                                                        </tr>
                                                    </c:if>


                                                </tbody>
                                            </table>
                                            <c:if test="${not empty orders}">
                                                <nav aria-label="Page navigation example">
                                                    <ul class="pagination justify-content-center">

                                                        <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                                            <a class="page-link"
                                                                href="/admin/order?page=${currentPage - 1}"
                                                                aria-label="Previous">
                                                                <span aria-hidden="true">&laquo;</span>
                                                            </a>
                                                        </li>

                                                        <c:forEach begin="0" end="${totalPages - 1}" varStatus="loop">
                                                            <li
                                                                class="page-item ${loop.index + 1 == currentPage ? 'active' : ''}">
                                                                <a class="page-link"
                                                                    href="/admin/order?page=${loop.index + 1}">
                                                                    ${loop.index + 1}
                                                                </a>
                                                            </li>
                                                        </c:forEach>

                                                        <li
                                                            class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                                            <a class="page-link"
                                                                href="/admin/order?page=${currentPage + 1}"
                                                                aria-label="Next">
                                                                <span aria-hidden="true">&raquo;</span>
                                                            </a>
                                                        </li>
                                                    </ul>
                                                </nav>
                                            </c:if>

                                        </div>
                                    </div>
                                </div>

                            </div>
                        </main>

                        <jsp:include page="../layout/footer.jsp" />
                    </div>
                </div>

                <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"
                    crossorigin="anonymous"></script>
                <script src="/js/scripts.js"></script>

            </body>

            </html>