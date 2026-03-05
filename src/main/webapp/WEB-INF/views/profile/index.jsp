<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ page import="com.example.hotelreservationsystem.model.User" %>
        <% User authUser=(User) request.getAttribute("authUser"); %>

            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h3 class="mb-1">My Profile</h3>
                    <p class="text-muted mb-0">Your account information</p>
                </div>
            </div>

            <div class="row">
                <div class="col-md-6">
                    <div class="card shadow-sm">
                        <div class="card-body">

                            <div class="d-flex align-items-center mb-4">
                                <div style="width: 64px; height: 64px; border-radius: 50%;
                                    background: linear-gradient(135deg, #0A9DC0, #005F82);
                                    display: flex; align-items: center; justify-content: center;
                                    font-size: 28px; color: #fff; font-weight: 700; flex-shrink: 0;">
                                    <%= authUser !=null && authUser.getFirstName() !=null ?
                                        authUser.getFirstName().charAt(0) : "?" %>
                                </div>
                                <div class="ms-3">
                                    <h5 class="mb-0">
                                        <%= authUser !=null ? authUser.getFirstName() + " " + authUser.getLastName()
                                            : "Unknown" %>
                                    </h5>
                                    <span class="badge bg-primary" style="font-size: 12px;">
                                        <%= authUser !=null ? authUser.getRole() : "—" %>
                                    </span>
                                </div>
                            </div>

                            <table class="table table-borderless mb-0">
                                <tbody>
                                    <tr>
                                        <th style="width: 140px; color: #004E6E;">Username</th>
                                        <td>
                                            <%= authUser !=null ? authUser.getUsername() : "—" %>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th style="color: #004E6E;">Email</th>
                                        <td>
                                            <%= authUser !=null ? authUser.getEmail() : "—" %>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th style="color: #004E6E;">First Name</th>
                                        <td>
                                            <%= authUser !=null ? authUser.getFirstName() : "—" %>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th style="color: #004E6E;">Last Name</th>
                                        <td>
                                            <%= authUser !=null ? authUser.getLastName() : "—" %>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th style="color: #004E6E;">Role</th>
                                        <td>
                                            <%= authUser !=null ? authUser.getRole() : "—" %>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th style="color: #004E6E;">Member Since</th>
                                        <td>
                                            <%= authUser !=null ? authUser.getCreatedAt() : "—" %>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>

                            <div class="mt-4 d-flex gap-2">
                                <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-outline-primary">
                                    <i class="bi bi-house"></i> Dashboard
                                </a>
                                <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-danger">
                                    <i class="bi bi-box-arrow-right"></i> Logout
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>