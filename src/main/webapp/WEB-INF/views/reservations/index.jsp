<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <% java.util.List reservationsList=(java.util.List) request.getAttribute("reservations"); %>

        <div class="d-flex justify-content-between align-items-center mb-3">
            <h3 class="mb-0">${title}</h3>
            <div>
                <a href="${pageContext.request.contextPath}/reservations"
                    class="btn btn-outline-primary <%= request.getAttribute(" statusFilter")==null ? "active" : ""
                    %>">Active</a>
                <a href="${pageContext.request.contextPath}/reservations?status=history"
                    class="btn btn-outline-secondary <%= " history".equals(request.getAttribute("statusFilter"))
                    ? "active" : "" %>">History</a>
                <a href="${pageContext.request.contextPath}/reservations?action=new" class="btn btn-primary ms-3">+ New
                    Reservation</a>
            </div>
        </div>

        <div class="card shadow-sm">
            <div class="table-responsive">
                <table class="table table-striped table-hover mb-0">
                    <thead class="table-light">
                        <tr>
                            <th>Reservation ID</th>
                            <th>Guest</th>
                            <th>Email</th>
                            <th>Room</th>
                            <th>Stay</th>
                            <th>Status</th>
                            <th>Guests</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (reservationsList !=null && !reservationsList.isEmpty()) { for (Object res :
                            reservationsList) { pageContext.setAttribute("reservation", res); %>
                            <tr>
                                <td>${reservation.reservationId}</td>
                                <td>${reservation.guestName}</td>
                                <td>${reservation.guestEmail}</td>
                                <td>${reservation.roomNumber} (${reservation.roomType})</td>
                                <td>${reservation.checkInDate} - ${reservation.checkOutDate}</td>
                                <td>
                                    <% com.example.hotelreservationsystem.dto.ReservationSummaryDTO
                                        rs=(com.example.hotelreservationsystem.dto.ReservationSummaryDTO)
                                        pageContext.getAttribute("reservation"); String status=rs.getStatus(); if
                                        ("COMPLETED".equals(status)) { %>
                                        <span class="badge bg-success">Completed</span>
                                        <% } else if ("CANCELLED".equals(status)) { %>
                                            <span class="badge bg-danger">Cancelled</span>
                                            <% } else { %>
                                                <span class="badge bg-info text-dark">Confirmed</span>
                                                <% } %>
                                </td>
                                <td>${reservation.guestCount}</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/biling?reservationId=${reservation.reservationId}"
                                        class="btn btn-sm <%= " COMPLETED".equals(status) ? "btn-outline-primary"
                                        : "btn-outline-success" %>">
                                        <%= "COMPLETED" .equals(status) ? "View Bill" : "Payment & Bill" %>
                                    </a>
                                </td>
                            </tr>
                            <% } } else { %>
                                <tr>
                                    <td colspan="7" class="text-center py-4">No reservations found.</td>
                                </tr>
                                <% } %>
                    </tbody>
                </table>
            </div>
        </div>