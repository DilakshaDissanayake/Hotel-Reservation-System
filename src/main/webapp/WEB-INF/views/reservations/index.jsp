<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <% java.util.List reservationsList=(java.util.List) request.getAttribute("reservations"); %>

        <div class="d-flex justify-content-between align-items-center mb-3">
            <h3 class="mb-0">Reservations</h3>
            <a href="${pageContext.request.contextPath}/reservations?action=new" class="btn btn-primary">+ New
                Reservation</a>
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
                                <td>${reservation.guestCount}</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/biling?reservationId=${reservation.reservationId}"
                                        class="btn btn-sm btn-outline-success">
                                        Payment & Bill
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