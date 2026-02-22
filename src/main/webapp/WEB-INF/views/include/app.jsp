<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title><%= request.getAttribute("title") != null ? request.getAttribute("title") : "Hotel Reservation System" %></title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/app.css">
</head>
<body class="bg-light">

<%  request.getRequestDispatcher("/WEB-INF/views/include/header.jsp").include(request, response); %>

<main class="container py-4">
    <%
        String contentPage = (String) request.getAttribute("contentPage");
        if (contentPage != null) {
            request.getRequestDispatcher(contentPage).include(request, response);
        }
    %>
</main>

<% request.getRequestDispatcher("/WEB-INF/views/include/footer.jsp").include(request, response); %>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= request.getContextPath() %>/assets/js/app.js"></script>
</body>
</html>