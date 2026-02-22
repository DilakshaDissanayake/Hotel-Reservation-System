<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<nav class="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm">
    <div class="container">
        <a class="navbar-brand fw-semibold" href="<%= request.getContextPath() %>/">
            <i class="bi bi-building"></i> Hotel Reservation
        </a>

        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNavbar"
                aria-controls="mainNavbar" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="mainNavbar">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <a class="nav-link" href="<%= request.getContextPath() %>/">Home</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= request.getContextPath() %>/rooms">Rooms</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= request.getContextPath() %>/reservations">My Reservations</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= request.getContextPath() %>/contact">Contact</a>
                </li>
            </ul>

            <ul class="navbar-nav ms-auto">
                <%
                    Object authUser = session.getAttribute("authUser");
                    if (authUser != null) {
                %>
                <li class="nav-item">
                    <a class="nav-link" href="<%= request.getContextPath() %>/profile">
                        <i class="bi bi-person-circle"></i> Profile
                    </a>
                </li>
                <li class="nav-item">
                    <a class="btn btn-outline-light btn-sm ms-lg-2" href="<%= request.getContextPath() %>/logout">
                        Logout
                    </a>
                </li>
                <%
                } else {
                %>
                <li class="nav-item">
                    <a class="nav-link" href="<%= request.getContextPath() %>/login">Login</a>
                </li>
                <li class="nav-item">
                    <a class="btn btn-light btn-sm ms-lg-2" href="<%= request.getContextPath() %>/register">
                        Register
                    </a>
                </li>
                <%
                    }
                %>
            </ul>
        </div>
    </div>
</nav>