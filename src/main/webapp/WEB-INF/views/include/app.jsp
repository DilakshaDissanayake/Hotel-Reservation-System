<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="description" content="Ocean View Resort - Hotel Reservation System"/>
    <meta name="author" content="Ocean View Resort"/>

    <title>
        ${title != null ? title : 'Ocean View Resort'}
    </title>

    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;700&family=Jost:wght@400;500;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css">
</head>

<body class="app-body-main">

<div class="app-scene">
    <div class="app-bg"></div>
    <div class="app-sun"></div>
    <div class="app-rays"></div>
    <div class="app-waves">
        <div class="app-wave"></div>
        <div class="app-wave"></div>
        <div class="app-wave"></div>
    </div>

    <div class="app-card-wrap">
        <jsp:include page="/WEB-INF/views/include/header.jsp" />

        <main class="container py-4 flex-grow-1" role="main">
            <jsp:include page="${contentPage}" />
        </main>

        <jsp:include page="/WEB-INF/views/include/footer.jsp" />
    </div>


</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<script src="${pageContext.request.contextPath}/assets/js/app.js"></script>

<script defer src="https://cdn.jsdelivr.net/npm/lazyload@2.0.0-rc.2/lazyload.min.js"></script>

</body>
</html>