<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <% String message=(String) request.getAttribute("message"); String error=(String) request.getAttribute("error");
        java.util.List roomsList=(java.util.List) request.getAttribute("rooms"); %>

        <div class="d-flex justify-content-between align-items-center mb-3">
            <h3 class="mb-0">Rooms</h3>
            <a href="${pageContext.request.contextPath}/reservations?action=new" class="btn btn-primary">Book a Room</a>
        </div>

        <% if (message !=null && !message.isEmpty()) { %>
            <div class="alert alert-success">
                <%= message %>
            </div>
            <% } %>

                <% if (error !=null && !error.isEmpty()) { %>
                    <div class="alert alert-danger">
                        <%= error %>
                    </div>
                    <% } %>

                        <div class="card shadow-sm mb-3">
                            <div class="card-body">
                                <h5 class="card-title mb-3">Add Room</h5>
                                <form method="post" action="${pageContext.request.contextPath}/rooms" class="row g-3">
                                    <div class="col-md-3">
                                        <label class="form-label">Room #</label>
                                        <input type="text" class="form-control" name="roomNumber" required>
                                    </div>
                                    <div class="col-md-3">
                                        <label class="form-label">Type</label>
                                        <select class="form-select" name="roomType" required>
                                            <option value="">Select Type</option>
                                            <option value="SINGLE">Single</option>
                                            <option value="DOUBLE">Double</option>
                                            <option value="DELUXE">Deluxe</option>
                                            <option value="SUITE">Suite</option>
                                        </select>
                                    </div>
                                    <div class="col-md-3">
                                        <label class="form-label">Rate / Night</label>
                                        <input type="number" step="0.01" min="0" class="form-control"
                                            name="ratePerNight" required>
                                    </div>
                                    <div class="col-md-3">
                                        <label class="form-label">Status</label>
                                        <select class="form-select" name="status" required>
                                            <option value="AVAILABLE">Available</option>
                                            <option value="MAINTENANCE">Maintenance</option>
                                        </select>
                                    </div>
                                    <div class="col-12">
                                        <label class="form-label">Description</label>
                                        <textarea class="form-control" name="description" rows="2"></textarea>
                                    </div>
                                    <div class="col-12">
                                        <button type="submit" class="btn btn-success">Add Room</button>
                                    </div>
                                </form>
                            </div>
                        </div>

                        <div class="card shadow-sm">
                            <div class="table-responsive">
                                <table class="table table-striped table-hover mb-0">
                                    <thead class="table-light">
                                        <tr>
                                            <th>Room #</th>
                                            <th>Type</th>
                                            <th>Rate / Night</th>
                                            <th>Status</th>
                                            <th>Description</th>
                                            <th>Action</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <% if (roomsList !=null && !roomsList.isEmpty()) { for (Object r : roomsList) {
                                            pageContext.setAttribute("room", r); %>
                                            <tr>
                                                <td>${room.roomNumber}</td>
                                                <td>${room.roomType}</td>
                                                <td>LKR ${room.ratePerNight}</td>
                                                <td>${room.status}</td>
                                                <td>${room.description}</td>
                                                <td>
                                                    <a class="btn btn-primary md" href="${pageContext.request.contextPath}/rooms/edit?id=${room.id}">
                                                        Edit
                                                    </a>
                                                </td>
                                            </tr>
                                            <% } } else { %>
                                                <tr>
                                                    <td colspan="6" class="text-center py-4">No rooms available.</td>
                                                </tr>
                                                <% } %>
                                    </tbody>
                                </table>
                            </div>
                        </div>