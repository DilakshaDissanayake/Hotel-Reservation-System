<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <% String message=(String) request.getAttribute("message"); String error=(String) request.getAttribute("error");
        Object reservation=request.getAttribute("reservation"); Object bill=request.getAttribute("bill"); %>

        <div class="d-flex justify-content-between align-items-center mb-3">
            <h3 class="mb-0">Billing & Invoice</h3>
            <a href="${pageContext.request.contextPath}/reservations" class="btn btn-outline-secondary">Back to
                Reservations</a>
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

                        <% if (reservation !=null) { %>
                            <div class="card shadow-sm mb-3">
                                <div class="card-body">
                                    <h5 class="mb-3">Reservation Summary</h5>
                                    <div class="row g-2">
                                        <div class="col-md-4"><strong>Reservation ID:</strong>
                                            ${reservation.reservationId}</div>
                                        <div class="col-md-4"><strong>Guest:</strong> ${reservation.guestName}</div>
                                        <div class="col-md-4"><strong>Email:</strong> ${reservation.guestEmail}</div>
                                        <div class="col-md-4"><strong>Room:</strong> ${reservation.roomNumber}
                                            (${reservation.roomType})</div>
                                        <div class="col-md-4"><strong>Rate / Night:</strong> LKR
                                            ${reservation.ratePerNight}</div>
                                        <div class="col-md-4"><strong>Stay:</strong> ${reservation.checkInDate} to
                                            ${reservation.checkOutDate}</div>
                                    </div>
                                </div>
                            </div>

                            <% if (bill==null) { %>
                                <div class="card shadow-sm mb-3">
                                    <div class="card-body">
                                        <h5 class="mb-3">Process Payment</h5>
                                        <form method="post" action="${pageContext.request.contextPath}/biling"
                                            class="row g-3">
                                            <input type="hidden" name="reservationId"
                                                value="${reservation.reservationId}">
                                            <div class="col-md-6">
                                                <label class="form-label">Extras Total (LKR)</label>
                                                <input type="number" name="extrasTotal" class="form-control" value="0"
                                                    min="0" step="0.01">
                                            </div>
                                            <div class="col-md-6">
                                                <label class="form-label">Discount Amount (LKR)</label>
                                                <input type="number" name="discountAmount" class="form-control"
                                                    value="0" min="0" step="0.01">
                                            </div>
                                            <div class="col-12">
                                                <button type="submit" class="btn btn-success">Confirm Payment & Generate
                                                    Bill</button>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                                <% } else { %>
                                    <div class="card shadow-sm" id="billPrintArea">
                                        <div class="card-body">
                                            <div class="d-flex justify-content-between align-items-start mb-3">
                                                <div>
                                                    <h4 class="mb-1">Ocean View Resort</h4>
                                                    <div class="text-muted">Professional Invoice</div>
                                                </div>
                                                <div class="text-end">
                                                    <div><strong>Bill ID:</strong> ${bill.billId}</div>
                                                    <div><strong>Generated:</strong> ${bill.generatedAt}</div>
                                                </div>
                                            </div>

                                            <hr>

                                            <div class="row mb-3">
                                                <div class="col-md-6">
                                                    <h6>Guest Information</h6>
                                                    <div>${bill.guestName}</div>
                                                    <div>${bill.guestEmail}</div>
                                                </div>
                                                <div class="col-md-6 text-md-end">
                                                    <h6>Reservation</h6>
                                                    <div>ID: ${bill.reservationId}</div>
                                                    <div>Room: ${bill.roomNumber} (${bill.roomType})</div>
                                                    <div>Stay: ${bill.checkInDate} - ${bill.checkOutDate}</div>
                                                </div>
                                            </div>

                                            <div class="table-responsive">
                                                <table class="table table-bordered">
                                                    <thead class="table-light">
                                                        <tr>
                                                            <th>Description</th>
                                                            <th class="text-end">Amount (LKR)</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        <tr>
                                                            <td>Room Charges (${bill.nights} nights x
                                                                ${bill.ratePerNight})</td>
                                                            <td class="text-end">${bill.roomCharges}</td>
                                                        </tr>
                                                        <tr>
                                                            <td>Extras</td>
                                                            <td class="text-end">${bill.extrasTotal}</td>
                                                        </tr>
                                                        <tr>
                                                            <td>Discount</td>
                                                            <td class="text-end">-${bill.discountAmount}</td>
                                                        </tr>
                                                        <tr>
                                                            <th>Grand Total</th>
                                                            <th class="text-end">${bill.total}</th>
                                                        </tr>
                                                    </tbody>
                                                </table>
                                            </div>

                                            <div class="d-flex gap-2">
                                                <button class="btn btn-primary" onclick="window.print()">Print
                                                    Bill</button>
                                                <a href="${pageContext.request.contextPath}/reservations"
                                                    class="btn btn-outline-secondary">Done</a>
                                            </div>
                                            <p class="text-muted mt-3 mb-0">Invoice email is automatically sent to guest
                                                address: ${bill.guestEmail}</p>
                                        </div>
                                    </div>
                                    <% } %>
                                        <% } %>