<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ page import="com.example.hotelreservationsystem.model.User" %>
        <%@ page import="java.util.List" %>

            <% String message=(String) request.getAttribute("message"); String error=(String)
                request.getAttribute("error"); List<User> users = (List<User>) request.getAttribute("users");
                    %>

                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <div>
                            <h3 class="mb-1">Staff Management</h3>
                            <p class="text-muted mb-0">Manage hotel administrators and receptionists</p>
                        </div>
                    </div>

                    <% if (message !=null) { %>
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <%= message %>
                                <button type="button" class="btn-close" data-bs-dismiss="alert"
                                    aria-label="Close"></button>
                        </div>
                        <% } %>

                            <% if (error !=null) { %>
                                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                    <%= error %>
                                        <button type="button" class="btn-close" data-bs-dismiss="alert"
                                            aria-label="Close"></button>
                                </div>
                                <% } %>

                                    <div class="card shadow-sm mb-4">
                                        <div class="card-body">
                                            <h5 class="card-title mb-3">Add New Staff Member</h5>
                                            <form action="${pageContext.request.contextPath}/staff" method="post"
                                                class="row g-3">
                                                <div class="col-md-6">
                                                    <label class="form-label">First Name</label>
                                                    <input type="text" name="firstName" class="form-control"
                                                        value="${firstNameAttr}" required>
                                                </div>
                                                <div class="col-md-6">
                                                    <label class="form-label">Last Name</label>
                                                    <input type="text" name="lastName" class="form-control"
                                                        value="${lastNameAttr}" required>
                                                </div>
                                                <div class="col-md-6">
                                                    <label class="form-label">Username</label>
                                                    <input type="text" name="username" class="form-control"
                                                        value="${usernameAttr}" required>
                                                </div>
                                                <div class="col-md-6">
                                                    <label class="form-label">Email</label>
                                                    <input type="email" name="email" class="form-control"
                                                        value="${emailAttr}" required>
                                                </div>
                                                <div class="col-md-6">
                                                    <label class="form-label">Password</label>
                                                    <input type="password" name="password" class="form-control" required
                                                        minlength="8">
                                                    <div class="form-text">At least 8 characters long.</div>
                                                </div>
                                                <div class="col-md-6">
                                                    <label class="form-label">Role</label>
                                                    <select name="role" class="form-select" required>
                                                        <option value="RECEPTIONIST" ${roleAttr=='RECEPTIONIST'
                                                            ? 'selected' : '' }>Receptionist</option>
                                                        <option value="ADMIN" ${roleAttr=='ADMIN' ? 'selected' : '' }>
                                                            Admin</option>
                                                    </select>
                                                </div>
                                                <div class="col-12">
                                                    <button type="submit" class="btn btn-primary">Add Staff
                                                        Member</button>
                                                </div>
                                            </form>
                                        </div>
                                    </div>

                                    <div class="card shadow-sm">
                                        <div class="card-header bg-white">
                                            <h5 class="mb-0">Staff List</h5>
                                        </div>
                                        <div class="table-responsive">
                                            <table class="table table-hover align-middle mb-0">
                                                <thead class="table-light">
                                                    <tr>
                                                        <th>Name</th>
                                                        <th>Username</th>
                                                        <th>Email</th>
                                                        <th>Role</th>
                                                        <th>Joined</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <% if (users !=null && !users.isEmpty()) { %>
                                                        <% for (User u : users) { %>
                                                            <tr>
                                                                <td>
                                                                    <%= u.getFirstName() + " " + u.getLastName() %>
                                                                </td>
                                                                <td>
                                                                    <%= u.getUsername() %>
                                                                </td>
                                                                <td>
                                                                    <%= u.getEmail() %>
                                                                </td>
                                                                <td>
                                                                    <span class="badge <%= " ADMIN".equals(u.getRole())
                                                                        ? "bg-danger" : "bg-primary" %>">
                                                                        <%= u.getRole() %>
                                                                    </span>
                                                                </td>
                                                                <td>
                                                                    <%= u.getCreatedAt() %>
                                                                </td>
                                                            </tr>
                                                            <% } %>
                                                                <% } else { %>
                                                                    <tr>
                                                                        <td colspan="5"
                                                                            class="text-center py-4 text-muted">No staff
                                                                            members found.</td>
                                                                    </tr>
                                                                    <% } %>
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>