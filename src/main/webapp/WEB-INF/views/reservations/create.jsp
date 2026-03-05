<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <% String error=(String) request.getAttribute("error"); java.util.List rooms=(java.util.List)
        request.getAttribute("rooms"); %>

        <div class="d-flex justify-content-between align-items-center mb-3">
            <h3 class="mb-0">Create Reservation</h3>
            <a href="${pageContext.request.contextPath}/reservations" class="btn btn-outline-secondary">Back</a>
        </div>

        <% if (error !=null && !error.isEmpty()) { %>
            <div class="alert alert-danger">
                <%= error %>
            </div>
            <% } %>

                <div class="card shadow-sm">
                    <div class="card-body">
                        <form method="post" action="${pageContext.request.contextPath}/reservations" class="row g-3">
                            <div class="col-md-4">
                                <label class="form-label">Guest Count</label>
                                <input type="number" class="form-control" name="guestCount" min="1" value="1" required>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label">Contact Number</label>
                                <input type="text" class="form-control" name="contactNumber" required>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label">Room Type</label>
                                <select class="form-select" name="roomType" required>
                                    <option value="">Select Type</option>
                                    <option value="SINGLE">Single</option>
                                    <option value="DOUBLE">Double</option>
                                    <option value="DELUXE">Deluxe</option>
                                    <option value="SUITE">Suite</option>
                                </select>
                            </div>

                            <div class="col-md-4">
                                <label class="form-label">Check In Date</label>
                                <input type="date" class="form-control" name="checkInDate" required>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label">Check Out Date</label>
                                <input type="date" class="form-control" name="checkOutDate" required>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label">Room</label>
                                <select class="form-select" name="roomId" required>
                                    <option value="">Select Room</option>
                                    <% if (rooms !=null) { for (Object roomObj : rooms) {
                                        pageContext.setAttribute("room", roomObj); %>
                                        <option value="${room.id}">${room.roomNumber} (${room.roomType}) - LKR
                                            ${room.ratePerNight}</option>
                                        <% } } %>
                                </select>
                            </div>

                            <div class="col-12">
                                <label class="form-label">Address</label>
                                <input type="text" class="form-control" name="address">
                            </div>

                            <div class="col-md-6">
                                <label class="form-label">Primary Guest Full Name</label>
                                <input type="text" class="form-control" name="guestFullName" required>
                            </div>
                            <div class="col-md-3">
                                <label class="form-label">Age</label>
                                <input type="number" class="form-control" name="guestAge" min="0">
                            </div>
                            <div class="col-md-3">
                                <label class="form-label">NIC</label>
                                <input type="text" class="form-control" name="nic">
                            </div>

                            <div class="col-md-4">
                                <label class="form-label">Passport No</label>
                                <input type="text" class="form-control" name="passportNo">
                            </div>
                            <div class="col-md-4">
                                <label class="form-label">Guest Email</label>
                                <input type="email" class="form-control" name="email" required>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label">Guest Phone</label>
                                <input type="text" class="form-control" name="phoneNumber">
                            </div>

                            <div class="col-12">
                                <button type="submit" class="btn btn-success">Save Reservation</button>
                            </div>
                        </form>
                    </div>
                </div>