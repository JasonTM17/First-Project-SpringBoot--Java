<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="utf-8">
            <title>Đặt hàng thành công - Laptopshop</title>
            <meta content="width=device-width, initial-scale=1.0" name="viewport">

            <!-- Google Web Fonts -->
            <link rel="preconnect" href="https://fonts.googleapis.com">
            <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
            <link
                href="https://fonts.googleapis.com/css2?family=Open+Sans:wght@400;600&family=Raleway:wght@600;800&display=swap"
                rel="stylesheet">

            <!-- Icon Font -->
            <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.15.4/css/all.css" />
            <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.4.1/font/bootstrap-icons.css" rel="stylesheet">

            <!-- Bootstrap & Style -->
            <link href="/client/css/bootstrap.min.css" rel="stylesheet">
            <link href="/client/css/style.css" rel="stylesheet">
        </head>

        <body>

            <!-- Header -->
            <jsp:include page="../layout/header.jsp" />

            <!-- Thanks Page Start -->
            <div class="container-fluid py-5">
                <div class="container py-5">
                    <div class="row justify-content-center">
                        <div class="col-md-8">
                            <div class="bg-light rounded p-5 text-center">

                                <div class="mb-4">
                                    <i class="fas fa-check-circle text-success" style="font-size: 80px;"></i>
                                </div>

                                <h1 class="mb-3 text-success">Đặt hàng thành công!</h1>
                                <p class="mb-4 fs-5">
                                    Cảm ơn bạn đã mua hàng tại <strong>Laptopshop</strong>.<br>
                                    Đơn hàng của bạn đã được ghi nhận và sẽ sớm được xử lý.
                                </p>

                                <a href="/" class="btn btn-success rounded-pill px-5 py-3">
                                    <i class="fas fa-home me-2 text-white"></i>
                                    Quay về trang chủ
                                </a>

                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <!-- Thanks Page End -->

            <!-- Footer -->
            <jsp:include page="../layout/footer.jsp" />

            <!-- Back to Top -->
            <a href="#" class="btn btn-primary border-3 border-primary rounded-circle back-to-top">
                <i class="fa fa-arrow-up"></i>
            </a>

            <!-- JS -->
            <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.4/jquery.min.js"></script>
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0/dist/js/bootstrap.bundle.min.js"></script>
            <script src="/client/js/main.js"></script>

        </body>

        </html>