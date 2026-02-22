<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>

    <title><%= request.getAttribute("title") %></title>

    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display&family=Jost&display=swap" rel="stylesheet">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">

    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/app.css">
</head>

<body class="app-body">

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
        <jsp:include page="${contentPage}" />
    </div>

    <div class="app-copy">
        © <%= java.time.Year.now() %> Ocean View Resort
    </div>


</div>


<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= request.getContextPath() %>/assets/js/app.js"></script>

</body>
</html>
