<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <% java.util.List reservationsList=(java.util.List) request.getAttribute("reservations"); %>

        <div class="d-flex justify-content-between align-items-center mb-3">
            <h3 class="mb-0">${title}</h3>
            <div>
                <a href="${pageContext.request.contextPath}/reservations"
                    class="btn btn-outline-primary <%= request.getAttribute("statusFilter")==null ? "active" : ""
                    %>">Active</a>
                <a href="${pageContext.request.contextPath}/reservations?status=history"
                    class="btn btn-outline-secondary <%= "history".equals(request.getAttribute("statusFilter"))
                    ? "active" : "" %>">History</a>
                <a href="${pageContext.request.contextPath}/reservations?action=new" class="btn btn-primary ms-3">+ New
                    Reservation</a>
            </div>
        </div>

        <div class="input-group mb-3">
            <span class="input-group-text bg-white"><i class="bi bi-search"></i></span>
            <input type="text" id="reservationSearch" class="form-control" placeholder="Search by ID, guest, email, room, status...">
        </div>

        <div class="card shadow-sm">
            <div class="table-responsive">
                <table id="reservationTable" class="table table-striped table-hover mb-0">
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
                                        class="btn btn-sm <%= "COMPLETED".equals(status) ? "btn-outline-primary"
                                        : "btn-outline-success" %>">
                                        <%= "COMPLETED" .equals(status) ? "View Bill" : "Payment & Bill" %>
                                    </a>
                                </td>
                            </tr>
                            <% } } else { %>
                                <tr>
                                    <td colspan="8" class="text-center py-4">No reservations found.</td>
                                </tr>
                                <% } %>
                    </tbody>
                </table>
            </div>
        </div>

        <script>
            (function () {
                function normalize(text) {
                    return String(text || '').toLowerCase().replace(/\s+/g, ' ').trim();
                }

                function initReservationSearch() {
                    var input = document.getElementById('reservationSearch');
                    var table = document.getElementById('reservationTable');
                    if (!input || !table) return;

                    var tbody = table.tBodies && table.tBodies.length ? table.tBodies[0] : null;
                    if (!tbody) return;

                    var noResultsRow = tbody.querySelector('tr.search-no-results');
                    if (!noResultsRow) {
                        noResultsRow = document.createElement('tr');
                        noResultsRow.className = 'search-no-results';
                        noResultsRow.style.display = 'none';
                        noResultsRow.innerHTML = '<td colspan="8" class="text-center py-4 text-muted">No matching results found.</td>';
                        tbody.appendChild(noResultsRow);
                    }

                    var runSearch = function () {
                        var term = normalize(input.value);
                        var visible = 0;

                        for (var i = 0; i < tbody.rows.length; i++) {
                            var row = tbody.rows[i];
                            if (row.classList.contains('search-no-results')) continue;

                            var match = term === '' || normalize(row.textContent).indexOf(term) !== -1;
                            row.style.display = match ? '' : 'none';
                            if (match) visible++;
                        }

                        noResultsRow.style.display = visible === 0 ? '' : 'none';
                    };

                    input.addEventListener('input', runSearch);
                    input.addEventListener('keyup', runSearch);
                    runSearch();
                }

                if (document.readyState === 'loading') {
                    document.addEventListener('DOMContentLoaded', initReservationSearch);
                } else {
                    initReservationSearch();
                }
            })();
        </script>