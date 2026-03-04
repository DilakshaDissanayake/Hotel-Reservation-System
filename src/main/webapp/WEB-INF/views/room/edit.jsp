
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% String message=(String) request.getAttribute("message"); String error=(String) request.getAttribute("error");
    com.example.hotelreservationsystem.model.Rooms room=(com.example.hotelreservationsystem.model.Rooms) request.getAttribute("room"); %>

<div class="d-flex justify-content-between align-items-center mb-3">
    <h3 class="mb-0">Edit Room</h3>
    <a href="${pageContext.request.contextPath}/rooms" class="btn btn-outline-secondary">Back</a>
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

<div class="card shadow-sm">
    <div class="card-body">
        <form method="post" action="${pageContext.request.contextPath}/rooms/edit" class="row g-3">
            <input type="hidden" name="id" value="${room.id}">
            <div class="col-md-3">
                <label class="form-label">Room #</label>
                <input type="text" class="form-control" name="roomNumber" value="${room.roomNumber}" required>
            </div>
            <div class="col-md-3">
                <label class="form-label">Type</label>
                <select class="form-select" name="roomType" required>
                    <option value="">Select Type</option>
                    <option value="SINGLE" ${room.roomType == 'SINGLE' ? 'selected' : ''}>Single</option>
                    <option value="DOUBLE" ${room.roomType == 'DOUBLE' ? 'selected' : ''}>Double</option>
                    <option value="DELUXE" ${room.roomType == 'DELUXE' ? 'selected' : ''}>Deluxe</option>
                    <option value="SUITE" ${room.roomType == 'SUITE' ? 'selected' : ''}>Suite</option>
                </select>
            </div>
            <div class="col-md-3">
                <label class="form-label">Rate / Night</label>
                <input type="number" step="0.01" min="0" class="form-control" name="ratePerNight" value="${room.ratePerNight}" required>
            </div>
            <div class="col-md-3">
                <label class="form-label">Status</label>
                <select class="form-select" name="status" required>
                    <option value="AVAILABLE" ${room.status == 'AVAILABLE' ? 'selected' : ''}>Available</option>
                    <option value="MAINTENANCE" ${room.status == 'MAINTENANCE' ? 'selected' : ''}>Maintenance</option>
                </select>
            </div>
            <div class="col-12">
                <label class="form-label">Description</label>
                <textarea class="form-control" name="description" rows="2">${room.description}</textarea>
            </div>
            <div class="col-12 d-flex gap-2">
                <button type="submit" class="btn btn-primary">Save Changes</button>
                <a href="${pageContext.request.contextPath}/rooms" class="btn btn-outline-secondary">Cancel</a>
            </div>
        </form>
    </div>
</div>
