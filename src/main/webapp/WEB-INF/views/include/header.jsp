<%@ page contentType="text/html;charset=UTF-8" language="java" %>

    <nav class="navbar navbar-expand-lg app-navbar shadow-sm">
        <div class="container">
            <a class="navbar-brand app-brand" href="<%= request.getContextPath() %>/">
                <img src="<%= request.getContextPath() %>/assets/img/logo.png" alt="Hotel Logo"
                    class="d-inline-block align-text-top" style="height: 40px; width:auto; margin-right:8px;">
            </a>

            <button class="navbar-toggler app-toggler" type="button" data-bs-toggle="collapse"
                data-bs-target="#mainNavbar" aria-controls="mainNavbar" aria-expanded="false"
                aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="mainNavbar">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                    <li class="nav-item">
                        <a class="nav-link app-link" href="<%= request.getContextPath() %>/dashboard">Dashboard</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link app-link" href="<%= request.getContextPath() %>/rooms">Rooms</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link app-link"
                            href="<%= request.getContextPath() %>/reservations">Reservations</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link app-link" href="#" data-bs-toggle="modal" data-bs-target="#helpModal">
                            <i class="bi bi-question-circle"></i> Help
                        </a>
                    </li>
                </ul>

                <ul class="navbar-nav ms-auto">
                    <% Object authUser=session.getAttribute("authUser"); if (authUser !=null) { %>
                        <li class="nav-item">
                            <a class="nav-link app-link" href="<%= request.getContextPath() %>/profile">
                                <i class="bi bi-person-circle"></i> Profile
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="btn app-btn-logout btn-sm ms-lg-2" href="<%= request.getContextPath() %>/logout">
                                Logout
                            </a>
                        </li>
                        <% } else { %>
                            <li class="nav-item">
                                <a class="nav-link app-link" href="<%= request.getContextPath() %>/login">Login</a>
                            </li>
                            <li class="nav-item">
                                <a class="btn app-btn-register btn-sm ms-lg-2"
                                    href="<%= request.getContextPath() %>/register">
                                    Register
                                </a>
                            </li>
                            <% } %>
                </ul>
            </div>
        </div>
    </nav>