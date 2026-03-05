<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ page import="com.example.hotelreservationsystem.model.Rooms" %>
        <% String message=(String) request.getAttribute("message"); String error=(String) request.getAttribute("error");
            java.util.List roomsList=(java.util.List) request.getAttribute("rooms"); java.util.Map
            roomBookings=(java.util.Map) request.getAttribute("roomBookings");
            com.example.hotelreservationsystem.model.User authUser=(com.example.hotelreservationsystem.model.User)
            session.getAttribute("authUser"); String userRole=(authUser !=null) ? authUser.getRole() : "" ; %>

            <div class="d-flex justify-content-between align-items-center mb-3">
                <h3 class="mb-0">Rooms</h3>
                <a href="${pageContext.request.contextPath}/reservations?action=new" class="btn btn-primary">Book a
                    Room</a>
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

                            <% if ("ADMIN".equals(userRole)) { %>
                                <div class="card shadow-sm mb-3">
                                    <div class="card-body">
                                        <h5 class="card-title mb-3">Add Room</h5>
                                        <form method="post" action="${pageContext.request.contextPath}/rooms"
                                            class="row g-3">
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
                                <% } %>

                                    <div class="input-group mb-3">
                                        <span class="input-group-text bg-white"><i class="bi bi-search"></i></span>
                                        <input type="text" id="roomSearch" class="form-control" placeholder="Search by room #, type, status, description...">
                                    </div>

                                    <div class="card shadow-sm">
                                        <div class="table-responsive">
                                            <table id="roomTable" class="table table-striped table-hover mb-0">
                                                <thead class="table-light">
                                                    <tr>
                                                        <th>Room #</th>
                                                        <th>Type</th>
                                                        <th>Rate / Night</th>
                                                        <th>Status</th>
                                                        <th>Booked Dates</th>
                                                        <th>Description</th>
                                                        <th>Action</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <% if (roomsList !=null && !roomsList.isEmpty()) { for (Object r :
                                                        roomsList) { pageContext.setAttribute("room", r); %>
                                                        <tr>
                                                            <td>${room.roomNumber}</td>
                                                            <td>${room.roomType}</td>
                                                            <td>LKR ${room.ratePerNight}</td>
                                                            <td>
                                                                <% Rooms roomModel=(Rooms)
                                                                    pageContext.getAttribute("room"); String
                                                                    roomStatus=(roomModel !=null) ?
                                                                    roomModel.getStatus() : "" ; if
                                                                    ("AVAILABLE".equals(roomStatus)) { %>
                                                                    <span class="badge bg-success">Available</span>
                                                                    <% } else if ("OCCUPIED".equals(roomStatus)) { %>
                                                                        <span
                                                                            class="badge bg-warning text-dark">Occupied</span>
                                                                        <% } else if ("MAINTENANCE".equals(roomStatus))
                                                                            { %>
                                                                            <span
                                                                                class="badge bg-danger">Maintenance</span>
                                                                            <% } else { %>
                                                                                <span class="badge bg-secondary">
                                                                                    <%= roomStatus %>
                                                                                </span>
                                                                                <% } %>
                                                            </td>
                                                            <td>
                                                                <% if (roomBookings !=null &&
                                                                    roomBookings.containsKey(roomModel.getId())) {
                                                                    java.util.List bookings=(java.util.List)
                                                                    roomBookings.get(roomModel.getId()); if (bookings
                                                                    !=null && !bookings.isEmpty()) { for (Object b :
                                                                    bookings) {
                                                                    com.example.hotelreservationsystem.dto.ReservationSummaryDTO
                                                                    booking=(com.example.hotelreservationsystem.dto.ReservationSummaryDTO)
                                                                    b; %>
                                                                    <div class="small text-muted mb-1">
                                                                        <span class="badge bg-light text-dark border">
                                                                            <%= booking.getCheckInDate() %> to <%=
                                                                                    booking.getCheckOutDate() %>
                                                                        </span>
                                                                    </div>
                                                                    <% } } else { %>
                                                                        <span class="text-muted small">No upcoming
                                                                            bookings</span>
                                                                        <% } } %>
                                                            </td>
                                                            <td>${room.description}</td>
                                                            <td>
                                                                <% if ("ADMIN".equals(userRole)) { %>
                                                                    <a class="btn btn-primary md"
                                                                        href="${pageContext.request.contextPath}/rooms/edit?id=${room.id}">
                                                                        Edit
                                                                    </a>
                                                                    <% } else { %>
                                                                        <span class="text-muted">-</span>
                                                                        <% } %>
                                                            </td>
                                                        </tr>
                                                        <% } } else { %>
                                                            <tr>
                                                                <td colspan="7" class="text-center py-4">No rooms
                                                                    available.
                                                                </td>
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

                                            function initRoomSearch() {
                                                var input = document.getElementById('roomSearch');
                                                var table = document.getElementById('roomTable');
                                                if (!input || !table) return;

                                                var tbody = table.tBodies && table.tBodies.length ? table.tBodies[0] : null;
                                                if (!tbody) return;

                                                var noResultsRow = tbody.querySelector('tr.search-no-results');
                                                if (!noResultsRow) {
                                                    noResultsRow = document.createElement('tr');
                                                    noResultsRow.className = 'search-no-results';
                                                    noResultsRow.style.display = 'none';
                                                    noResultsRow.innerHTML = '<td colspan="7" class="text-center py-4 text-muted">No matching results found.</td>';
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
                                                document.addEventListener('DOMContentLoaded', initRoomSearch);
                                            } else {
                                                initRoomSearch();
                                            }
                                        })();
                                    </script>