<%@ page contentType="text/html;charset=UTF-8" language="java" %>

	<div class="d-flex justify-content-between align-items-center mb-4">
		<div>
			<h3 class="mb-1">Hotel Dashboard</h3>
			<p class="text-muted mb-0">Reservation, room booking, and billing summary</p>
		</div>
		<a href="${pageContext.request.contextPath}/reservations?action=new" class="btn btn-primary">
			+ New Reservation
		</a>
	</div>

	<div class="row g-3 mb-4">
		<div class="col-md-6 col-lg-3">
			<div class="card shadow-sm h-100">
				<div class="card-body">
					<h6 class="text-muted mb-1">Total Rooms</h6>
					<h3 class="mb-0">${stats.totalRooms}</h3>
				</div>
			</div>
		</div>
		<div class="col-md-6 col-lg-3">
			<div class="card shadow-sm h-100">
				<div class="card-body">
					<h6 class="text-muted mb-1">Available Rooms</h6>
					<h3 class="mb-0">${stats.availableRooms}</h3>
				</div>
			</div>
		</div>
		<div class="col-md-6 col-lg-3">
			<div class="card shadow-sm h-100">
				<div class="card-body">
					<h6 class="text-muted mb-1">Reservations</h6>
					<h3 class="mb-0">${stats.totalReservations}</h3>
				</div>
			</div>
		</div>
		<div class="col-md-6 col-lg-3">
			<div class="card shadow-sm h-100">
				<div class="card-body">
					<h6 class="text-muted mb-1">Revenue</h6>
					<h3 class="mb-0">LKR ${stats.totalRevenue}</h3>
				</div>
			</div>
		</div>
	</div>

	<div class="card shadow-sm">
		<div class="card-body">
			<h5 class="card-title">Quick Actions</h5>
			<div class="d-flex gap-2 flex-wrap">
				<a href="${pageContext.request.contextPath}/rooms" class="btn btn-outline-primary">View Rooms</a>
				<a href="${pageContext.request.contextPath}/reservations" class="btn btn-outline-primary">Manage
					Reservations</a>
				<a href="${pageContext.request.contextPath}/reservations?action=new"
					class="btn btn-outline-success">Book Room</a>
				<% com.example.hotelreservationsystem.model.User
					dashUser=(com.example.hotelreservationsystem.model.User) session.getAttribute("authUser"); if
					(dashUser !=null && "ADMIN" .equals(dashUser.getRole())) { %>
					<a href="${pageContext.request.contextPath}/staff" class="btn btn-outline-warning">Manage Staff</a>
					<% } %>
			</div>
		</div>
	</div>