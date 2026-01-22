<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
            <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

                <!DOCTYPE html>
                <html lang="en">

                <head>
                    <meta charset="utf-8">
                    <title>Checkout - Laptopshop</title>
                    <meta content="width=device-width, initial-scale=1.0" name="viewport">

                    <!-- Fonts & CSS -->
                    <link rel="preconnect" href="https://fonts.googleapis.com">
                    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
                    <link
                        href="https://fonts.googleapis.com/css2?family=Open+Sans:wght@400;600&family=Raleway:wght@600;800&display=swap"
                        rel="stylesheet">

                    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.15.4/css/all.css" />
                    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.4.1/font/bootstrap-icons.css"
                        rel="stylesheet">
                    <link href="/client/css/bootstrap.min.css" rel="stylesheet">
                    <link href="/client/css/style.css" rel="stylesheet">
                </head>

                <body>

                    <jsp:include page="../layout/header.jsp" />

                    <div class="container-fluid py-5">
                        <div class="container py-5">

                            <!-- Breadcrumb -->
                            <div class="mb-3">
                                <nav aria-label="breadcrumb">
                                    <ol class="breadcrumb">
                                        <li class="breadcrumb-item">
                                            <a href="/">Home</a>
                                        </li>
                                        <li class="breadcrumb-item active" aria-current="page">
                                            Thông tin thanh toán
                                        </li>
                                    </ol>
                                </nav>
                            </div>

                            <!-- TABLE CART -->
                            <div class="table-responsive mb-5">
                                <table class="table">
                                    <thead>
                                        <tr>
                                            <th>Sản phẩm</th>
                                            <th>Tên</th>
                                            <th>Giá cả</th>
                                            <th>Số lượng</th>
                                            <th>Thành tiền</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="cd" items="${cartDetails}">
                                            <tr>
                                                <td>
                                                    <img src="/images/product/${cd.product.image}"
                                                        class="rounded-circle" style="width:80px;height:80px">
                                                </td>
                                                <td class="align-middle">
                                                    <a href="/product/${cd.product.id}" class="text-success">
                                                        ${cd.product.name}
                                                    </a>
                                                </td>
                                                <td class="align-middle">
                                                    <fmt:formatNumber value="${cd.product.price}" type="number" /> đ
                                                </td>
                                                <td class="align-middle">
                                                    ${cd.quantity}
                                                </td>
                                                <td class="align-middle">
                                                    <fmt:formatNumber value="${cd.product.price * cd.quantity}"
                                                        type="number" /> đ
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>


                            <c:if test="${not empty cartDetails}">

                                <form:form action="/place-order" method="post" modelAttribute="cart">
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

                                    <div class="row g-5">


                                        <div class="col-md-7">
                                            <h4 class="mb-4">Thông Tin Người Nhận</h4>

                                            <div class="mb-3">
                                                <label class="form-label">Tên người nhận</label>
                                                <input class="form-control" name="receiverName" required />
                                            </div>

                                            <div class="mb-3">
                                                <label class="form-label">Địa chỉ người nhận</label>
                                                <input class="form-control" name="receiverAddress" required />
                                            </div>

                                            <div class="mb-3">
                                                <label class="form-label">Số điện thoại</label>
                                                <input class="form-control" name="receiverPhone" required />
                                            </div>

                                            <!-- hidden cartDetails -->
                                            <c:forEach var="cd" items="${cart.cartDetails}" varStatus="st">
                                                <form:input type="hidden" path="cartDetails[${st.index}].id"
                                                    value="${cd.id}" />

                                                <form:input type="hidden" path="cartDetails[${st.index}].quantity"
                                                    value="${cd.quantity}" />
                                            </c:forEach>

                                            <div class="mt-3">
                                                <i class="fas fa-arrow-left"></i>
                                                <a href="/cart">Quay lại giỏ hàng</a>
                                            </div>
                                        </div>

                                        <!-- RIGHT: Thông tin thanh toán -->
                                        <div class="col-md-5">
                                            <div class="bg-light rounded p-4">
                                                <h4 class="mb-4">Thông Tin Thanh Toán</h4>

                                                <div class="d-flex justify-content-between mb-3">
                                                    <span>Phí vận chuyển</span>
                                                    <span>0 đ</span>
                                                </div>

                                                <div class="d-flex justify-content-between mb-3">
                                                    <span>Hình thức</span>
                                                    <span>Thanh toán khi nhận hàng (COD)</span>
                                                </div>

                                                <hr>

                                                <div class="d-flex justify-content-between fw-bold fs-5 mb-4">
                                                    <span>Tổng số tiền</span>
                                                    <span>
                                                        <fmt:formatNumber value="${totalPrice}" type="number" /> đ
                                                    </span>
                                                </div>


                                                <button type="submit"
                                                    class="btn btn-success rounded-pill px-5 py-2 w-100">
                                                    XÁC NHẬN THANH TOÁN
                                                </button>
                                            </div>
                                        </div>

                                    </div>
                                </form:form>

                            </c:if>

                        </div>
                    </div>


                    <jsp:include page="../layout/footer.jsp" />

                    <!-- Back to Top -->
                    <a href="#" class="btn btn-primary border-3 border-primary rounded-circle back-to-top"><i
                            class="fa fa-arrow-up"></i></a>


                    <!-- JavaScript Libraries -->
                    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.4/jquery.min.js"></script>
                    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0/dist/js/bootstrap.bundle.min.js"></script>
                    <script src="/client/lib/easing/easing.min.js"></script>
                    <script src="/client/lib/waypoints/waypoints.min.js"></script>
                    <script src="/client/lib/lightbox/js/lightbox.min.js"></script>
                    <script src="/client/lib/owlcarousel/owl.carousel.min.js"></script>

                    <!-- Template Javascript -->
                    <script src="/client/js/main.js"></script>
                </body>

                </html>