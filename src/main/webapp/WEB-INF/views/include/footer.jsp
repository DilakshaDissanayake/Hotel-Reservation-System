<%@ page contentType="text/html;charset=UTF-8" language="java" %>

    <div class="app-copy">
        © <%= java.time.Year.now() %> Ocean View Resort
    </div>

    <div class="modal fade" id="helpModal" tabIndex="-1" aria-labelledby="helpModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="helpModalLabel"><i class="bi bi-info-circle"></i> How to Use the System</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body" style="font-size: 14px; color: #333;">
                    <h6><strong>1. Managing Rooms</strong></h6>
                    <p>Go to the <strong>Rooms</strong> tab to view the list of available rooms in the hotel.
                        Administrators can add new rooms by filling out the 'Add Room' form, specifying the room number,
                        type, rate, and status.</p>

                    <h6><strong>2. Creating a Reservation (FR2)</strong></h6>
                    <p>Click on the <strong>Reservations</strong> tab and select <strong>+ New Reservation</strong>.
                        Fill out the guest details (including NIC/Passport for security), select the dates, and pick an
                        available room. The system will prevent double-booking for overlapping dates.</p>

                    <h6><strong>3. Viewing Reservations (FR3)</strong></h6>
                    <p>On the <strong>Reservations</strong> tab, you can see all active and past bookings. Use the
                        'Payment & Bill' button to proceed to checkout for a specific reservation.</p>

                    <h6><strong>4. Billing & Checkout (FR4 & FR5)</strong></h6>
                    <p>When generating a bill, the system automatically calculates the
                        <code>Room Rate × Nights Stayed</code>. You can add extra charges (like meals or facilities) or
                        apply a discount. Once confirmed, you can <strong>Print the Bill</strong> or save it as a PDF.
                    </p>

                    <h6><strong>5. Safe Exit / Logout (FR7)</strong></h6>
                    <p>When you are finished using the system, click the <strong>Logout</strong> button in the top right
                        corner to safely end your session and prevent unauthorized access.</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close Help</button>
                </div>
            </div>
        </div>
    </div>