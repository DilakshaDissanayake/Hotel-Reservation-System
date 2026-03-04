package com.example.hotelreservationsystem.dao.impl;

import com.example.hotelreservationsystem.dao.ReservationDAO;
import com.example.hotelreservationsystem.dto.BillDetailsDTO;
import com.example.hotelreservationsystem.dto.DashboardStatsDTO;
import com.example.hotelreservationsystem.dto.ReservationSummaryDTO;
import com.example.hotelreservationsystem.exception.DatabaseException;
import com.example.hotelreservationsystem.model.Facilities;
import com.example.hotelreservationsystem.model.RoomFacilities;
import com.example.hotelreservationsystem.model.Rooms;
import com.example.hotelreservationsystem.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReservationDAOImpl implements ReservationDAO {

    private final boolean guestEmailColumnAvailable;
    private final boolean guestPhoneColumnAvailable;
    private final boolean resAddressColumnAvailable;
    private final boolean resContactColumnAvailable;
    private final boolean resRoomTypeColumnAvailable;

    private static final String SELECT_DASHBOARD_STATS =
            "SELECT " +
                    "(SELECT COUNT(*) FROM rooms) AS total_rooms, " +
                    "(SELECT COUNT(*) FROM rooms WHERE status = 'AVAILABLE') AS available_rooms, " +
                    "(SELECT COUNT(*) FROM reservations) AS total_reservations, " +
                    "(SELECT COUNT(*) FROM bills) AS total_bills, " +
                    "(SELECT COALESCE(SUM(total), 0) FROM bills) AS total_revenue";

    private static final String SELECT_ALL_ROOMS =
            "SELECT id, room_number, room_type, rate_per_night, status, description, created_at, updated_at " +
                    "FROM rooms ORDER BY room_number";

        private static final String INSERT_ROOM =
            "INSERT INTO rooms (room_number, room_type, rate_per_night, status, description) VALUES (?, ?, ?, ?, ?)";

    private static final String SELECT_ROOM_BY_ID =
            "SELECT id, room_number, room_type, rate_per_night, status, description, created_at, updated_at " +
                    "FROM rooms WHERE id = ?";

    private static final String UPDATE_ROOM =
            "UPDATE rooms SET room_number = ?, room_type = ?, rate_per_night = ?, status = ?, description = ? WHERE id = ?";

    private static final String SELECT_ALL_FACILITIES =
            "SELECT id, name, category, description, created_at FROM facilities ORDER BY name";

    private static final String SELECT_ROOM_FACILITIES =
            "SELECT room_id, facility_id, extra_price_per_night FROM room_facilities WHERE room_id = ?";

    private static final String INSERT_ROOM_FACILITY =
            "INSERT INTO room_facilities (room_id, facility_id, extra_price_per_night) VALUES (?, ?, ?)";

    private static final String DELETE_ROOM_FACILITIES =
            "DELETE FROM room_facilities WHERE room_id = ?";

    private static final String SELECT_AVAILABLE_ROOMS =
            "SELECT r.id, r.room_number, r.room_type, r.rate_per_night, r.status, r.description, r.created_at, r.updated_at " +
                    "FROM rooms r " +
                    "WHERE r.status = 'AVAILABLE' " +
                    "AND (? IS NULL OR r.room_type = ?) " +
                    "AND NOT EXISTS (" +
                    "   SELECT 1 FROM reservations res " +
                    "   WHERE res.room_id = r.id " +
                    "   AND NOT (res.check_out_date <= ? OR res.check_in_date >= ?)" +
                    ") " +
                    "ORDER BY r.room_number";

    private static final String CHECK_ROOM_AVAILABILITY =
            "SELECT COUNT(*) AS booking_count " +
                    "FROM reservations " +
                    "WHERE room_id = ? " +
                    "AND NOT (check_out_date <= ? OR check_in_date >= ?)";

    private static final String SELECT_NEXT_RESERVATION_ID =
            "SELECT COALESCE(MAX(reservation_id), 1000) + 1 AS next_id FROM reservations FOR UPDATE";

    private static final String INSERT_RESERVATION =
            "INSERT INTO reservations " +
                    "(reservation_id, guest_count, address, contact_number, room_type, check_in_date, check_out_date, room_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        private String insertReservationSql() {
            StringBuilder sql = new StringBuilder("INSERT INTO reservations (reservation_id, guest_count, check_in_date, check_out_date, room_id");
            if (resAddressColumnAvailable) sql.append(", address");
            if (resContactColumnAvailable) sql.append(", contact_number");
            if (resRoomTypeColumnAvailable) sql.append(", room_type");
            sql.append(") VALUES (?, ?, ?, ?, ?");
            if (resAddressColumnAvailable) sql.append(", ?");
            if (resContactColumnAvailable) sql.append(", ?");
            if (resRoomTypeColumnAvailable) sql.append(", ?");
            sql.append(")");
            return sql.toString();
        }

        private String insertPrimaryGuestSql() {
            StringBuilder sql = new StringBuilder("INSERT INTO reservation_guests (reservation_id, full_name, age, nic, passport_no");
            if (guestEmailColumnAvailable) sql.append(", email");
            if (guestPhoneColumnAvailable) sql.append(", phone_number");
            sql.append(", is_primary) VALUES (?, ?, ?, ?, ?");
            if (guestEmailColumnAvailable) sql.append(", ?");
            if (guestPhoneColumnAvailable) sql.append(", ?");
            sql.append(", TRUE)");
            return sql.toString();
        }

        private String getSummaryColumnsSql() {
            StringBuilder cols = new StringBuilder("r.reservation_id, r.guest_count, r.check_in_date, r.check_out_date");
            cols.append(resContactColumnAvailable ? ", r.contact_number" : ", NULL AS contact_number");
            return cols.toString();
        }

        private static final String SELECT_RESERVATION_SUMMARIES_TEMPLATE =
            "SELECT %s, " +
                "room.id AS room_id, room.room_number, room.room_type, room.rate_per_night, " +
                "guest.full_name AS guest_name, %s AS guest_email " +
                "FROM reservations r " +
                "JOIN rooms room ON room.id = r.room_id " +
                "LEFT JOIN reservation_guests guest " +
                "ON guest.reservation_id = r.reservation_id AND guest.is_primary = TRUE " +
                "ORDER BY r.created_at DESC";

        private static final String SELECT_RESERVATION_SUMMARY_BY_ID_TEMPLATE =
            "SELECT %s, " +
                "room.id AS room_id, room.room_number, room.room_type, room.rate_per_night, " +
                "guest.full_name AS guest_name, %s AS guest_email " +
                "FROM reservations r " +
                "JOIN rooms room ON room.id = r.room_id " +
                "LEFT JOIN reservation_guests guest " +
                "ON guest.reservation_id = r.reservation_id AND guest.is_primary = TRUE " +
                "WHERE r.reservation_id = ?";

    private static final String UPSERT_BILL =
            "INSERT INTO bills (reservation_id, nights, rate_per_night, extras_total, discount_amount, sub_total, total) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "nights = VALUES(nights), " +
                    "rate_per_night = VALUES(rate_per_night), " +
                    "extras_total = VALUES(extras_total), " +
                    "discount_amount = VALUES(discount_amount), " +
                    "sub_total = VALUES(sub_total), " +
                    "total = VALUES(total), " +
                    "generated_at = CURRENT_TIMESTAMP";

    private static final String SELECT_BILL_ID_BY_RESERVATION =
            "SELECT bill_id FROM bills WHERE reservation_id = ?";

    private static final String SELECT_BILL_DETAILS_BY_RESERVATION_TEMPLATE =
            "SELECT b.bill_id, b.reservation_id, b.nights, b.rate_per_night, b.extras_total, b.discount_amount, " +
                    "b.sub_total, b.total, b.generated_at, r.check_in_date, r.check_out_date, " +
                    "room.room_number, room.room_type, guest.full_name AS guest_name, %s AS guest_email " +
                    "FROM bills b " +
                    "JOIN reservations r ON r.reservation_id = b.reservation_id " +
                    "JOIN rooms room ON room.id = r.room_id " +
                    "LEFT JOIN reservation_guests guest " +
                    "ON guest.reservation_id = r.reservation_id AND guest.is_primary = TRUE " +
                    "WHERE b.reservation_id = ?";

    public ReservationDAOImpl() {
        this.guestEmailColumnAvailable = hasColumn("reservation_guests", "email");
        this.guestPhoneColumnAvailable = hasColumn("reservation_guests", "phone_number");
        this.resAddressColumnAvailable = hasColumn("reservations", "address");
        this.resContactColumnAvailable = hasColumn("reservations", "contact_number");
        this.resRoomTypeColumnAvailable = hasColumn("reservations", "room_type");
    }

    private String guestEmailColumnExpression() {
        return guestEmailColumnAvailable ? "guest.email" : "NULL";
    }

    private String selectReservationSummariesSql() {
        return String.format(SELECT_RESERVATION_SUMMARIES_TEMPLATE, getSummaryColumnsSql(), guestEmailColumnExpression());
    }

    private String selectReservationSummaryByIdSql() {
        return String.format(SELECT_RESERVATION_SUMMARY_BY_ID_TEMPLATE, getSummaryColumnsSql(), guestEmailColumnExpression());
    }

    private String selectBillDetailsByReservationSql() {
        return String.format(SELECT_BILL_DETAILS_BY_RESERVATION_TEMPLATE, guestEmailColumnExpression());
    }


    @Override
    public DashboardStatsDTO getDashboardStats() {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_DASHBOARD_STATS);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return new DashboardStatsDTO(
                        resultSet.getInt("total_rooms"),
                        resultSet.getInt("available_rooms"),
                        resultSet.getInt("total_reservations"),
                        resultSet.getInt("total_bills"),
                        resultSet.getDouble("total_revenue")
                );
            }
            return new DashboardStatsDTO();

        } catch (SQLException e) {
            throw new DatabaseException("Failed to load dashboard statistics", e);
        }
    }

    @Override
    public List<Rooms> findAllRooms() {
        List<Rooms> rooms = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_ROOMS);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                rooms.add(mapRoom(resultSet));
            }

            return rooms;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to load rooms", e);
        }
    }

    @Override
    public int createRoom(String roomNumber,
                          String roomType,
                          double ratePerNight,
                          String status,
                          String description) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_ROOM)) {

            statement.setString(1, roomNumber);
            statement.setString(2, roomType);
            statement.setDouble(3, ratePerNight);
            statement.setString(4, status);
            statement.setString(5, emptyToNull(description));

            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create room", e);
        }
    }

    @Override
    public List<Rooms> findAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, String roomType) {
        List<Rooms> rooms = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_AVAILABLE_ROOMS)) {

            String type = isBlank(roomType) ? null : roomType.trim().toUpperCase();

            statement.setString(1, type);
            statement.setString(2, type);
            statement.setDate(3, Date.valueOf(checkInDate));
            statement.setDate(4, Date.valueOf(checkOutDate));

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    rooms.add(mapRoom(resultSet));
                }
            }

            return rooms;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to load available rooms", e);
        }
    }

    @Override
    public int createRoomWithFacilities(String roomNumber,
                                        String roomType,
                                        double ratePerNight,
                                        String status,
                                        String description,
                                        List<RoomFacilities> facilities) {
        try (Connection connection = DBConnection.getConnection()) {
            boolean oldAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(INSERT_ROOM, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, roomNumber);
                statement.setString(2, roomType);
                statement.setDouble(3, ratePerNight);
                statement.setString(4, status);
                statement.setString(5, emptyToNull(description));
                int affectedRows = statement.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Creating room failed, no rows affected.");
                }

                int roomId;
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        roomId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Creating room failed, no ID obtained.");
                    }
                }

                if (facilities != null && !facilities.isEmpty()) {
                    try (PreparedStatement facilityStmt = connection.prepareStatement(INSERT_ROOM_FACILITY)) {
                        for (RoomFacilities rf : facilities) {
                            facilityStmt.setInt(1, roomId);
                            facilityStmt.setInt(2, rf.getFacilityId());
                            facilityStmt.setDouble(3, rf.getExtraPricePerNight());
                            facilityStmt.addBatch();
                        }
                        facilityStmt.executeBatch();
                    }
                }

                connection.commit();
                return roomId;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(oldAutoCommit);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create room with facilities", e);
        }
    }

    @Override
    public boolean updateRoomWithFacilities(int roomId,
                                            String roomNumber,
                                            String roomType,
                                            double ratePerNight,
                                            String status,
                                            String description,
                                            List<RoomFacilities> facilities) {
        try (Connection connection = DBConnection.getConnection()) {
            boolean oldAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(UPDATE_ROOM)) {
                statement.setString(1, roomNumber);
                statement.setString(2, roomType);
                statement.setDouble(3, ratePerNight);
                statement.setString(4, status);
                statement.setString(5, emptyToNull(description));
                statement.setInt(6, roomId);
                int affectedRows = statement.executeUpdate();

                if (affectedRows > 0) {
                    if (facilities != null) {
                        try (PreparedStatement deleteStmt = connection.prepareStatement(DELETE_ROOM_FACILITIES)) {
                            deleteStmt.setInt(1, roomId);
                            deleteStmt.executeUpdate();
                        }

                        if (!facilities.isEmpty()) {
                            try (PreparedStatement facilityStmt = connection.prepareStatement(INSERT_ROOM_FACILITY)) {
                                for (RoomFacilities rf : facilities) {
                                    facilityStmt.setInt(1, roomId);
                                    facilityStmt.setInt(2, rf.getFacilityId());
                                    facilityStmt.setDouble(3, rf.getExtraPricePerNight());
                                    facilityStmt.addBatch();
                                }
                                facilityStmt.executeBatch();
                            }
                        }
                    }
                    connection.commit();
                    return true;
                }
                connection.rollback();
                return false;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(oldAutoCommit);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update room and facilities", e);
        }
    }

    @Override
    public Optional<Rooms> getRoomById(int roomId) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ROOM_BY_ID)) {
            statement.setInt(1, roomId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRoom(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to load room details", e);
        }
    }

    @Override
    public List<Facilities> getAllFacilities() {
        List<Facilities> facilities = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_FACILITIES);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                facilities.add(new Facilities(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("category"),
                        resultSet.getString("description"),
                        resultSet.getString("created_at")
                ));
            }
            return facilities;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to load facilities", e);
        }
    }

    @Override
    public List<RoomFacilities> getRoomFacilities(int roomId) {
        List<RoomFacilities> roomFacilities = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ROOM_FACILITIES)) {
            statement.setInt(1, roomId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    roomFacilities.add(new RoomFacilities(
                            resultSet.getInt("room_id"),
                            resultSet.getInt("facility_id"),
                            resultSet.getDouble("extra_price_per_night")
                    ));
                }
            }
            return roomFacilities;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to load room facilities", e);
        }
    }

    @Override
    public boolean isRoomAvailable(int roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(CHECK_ROOM_AVAILABILITY)) {

            statement.setInt(1, roomId);
            statement.setDate(2, Date.valueOf(checkInDate));
            statement.setDate(3, Date.valueOf(checkOutDate));

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("booking_count") == 0;
                }
                return false;
            }

        } catch (SQLException e) {
            throw new DatabaseException("Failed to check room availability", e);
        }
    }

    @Override
    public long createReservationWithPrimaryGuest(
            int guestCount,
          String address,
          String contactNumber,
          String roomType,
          LocalDate checkInDate,
          LocalDate checkOutDate,
          int roomId,
          String guestFullName,
          Integer guestAge,
          String nic,
          String passportNo,
          String email,
          String phoneNumber) {
        try (Connection connection = DBConnection.getConnection()) {
            boolean oldAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try (PreparedStatement nextIdStatement = connection.prepareStatement(SELECT_NEXT_RESERVATION_ID)) {

                long reservationId;
                try (ResultSet resultSet = nextIdStatement.executeQuery()) {
                    if (!resultSet.next()) {
                        throw new SQLException("Failed to generate reservation id");
                    }
                    reservationId = resultSet.getLong("next_id");
                }

                try (PreparedStatement reservationStatement = connection.prepareStatement(insertReservationSql())) {
                    reservationStatement.setLong(1, reservationId);
                    reservationStatement.setInt(2, guestCount);
                    reservationStatement.setDate(3, Date.valueOf(checkInDate));
                    reservationStatement.setDate(4, Date.valueOf(checkOutDate));
                    reservationStatement.setInt(5, roomId);
                    
                    int resIndex = 6;
                    if (resAddressColumnAvailable) reservationStatement.setString(resIndex++, emptyToNull(address));
                    if (resContactColumnAvailable) reservationStatement.setString(resIndex++, contactNumber);
                    if (resRoomTypeColumnAvailable) reservationStatement.setString(resIndex++, roomType);
                    
                    reservationStatement.executeUpdate();
                }

                String guestSql = insertPrimaryGuestSql();
                try (PreparedStatement guestStatement = connection.prepareStatement(guestSql)) {
                    guestStatement.setLong(1, reservationId);
                    guestStatement.setString(2, guestFullName);
                    if (guestAge == null) {
                        guestStatement.setNull(3, java.sql.Types.INTEGER);
                    } else {
                        guestStatement.setInt(3, guestAge);
                    }
                    guestStatement.setString(4, emptyToNull(nic));
                    guestStatement.setString(5, emptyToNull(passportNo));

                    int paramIndex = 6;
                    if (guestEmailColumnAvailable) {
                        guestStatement.setString(paramIndex++, emptyToNull(email));
                    }
                    if (guestPhoneColumnAvailable) {
                        guestStatement.setString(paramIndex++, emptyToNull(phoneNumber));
                    }
                    guestStatement.executeUpdate();
                }

                connection.commit();
                return reservationId;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(oldAutoCommit);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create reservation", e);
        }
    }

    @Override
    public List<ReservationSummaryDTO> findAllReservationSummaries() {
        List<ReservationSummaryDTO> reservations = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
               PreparedStatement statement = connection.prepareStatement(selectReservationSummariesSql());
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                reservations.add(mapReservationSummary(resultSet));
            }
            return reservations;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to load reservations", e);
        }
    }

    @Override
    public Optional<ReservationSummaryDTO> findReservationSummaryById(long reservationId) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(selectReservationSummaryByIdSql())) {

            statement.setLong(1, reservationId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapReservationSummary(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to load reservation details", e);
        }
    }

    @Override
    public long createOrUpdateBill(long reservationId,
                                   int nights,
                                   double ratePerNight,
                                   double extrasTotal,
                                   double discountAmount,
                                   double subTotal,
                                   double total) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement upsertStatement = connection.prepareStatement(UPSERT_BILL);
             PreparedStatement idStatement = connection.prepareStatement(SELECT_BILL_ID_BY_RESERVATION)) {

            upsertStatement.setLong(1, reservationId);
            upsertStatement.setInt(2, nights);
            upsertStatement.setDouble(3, ratePerNight);
            upsertStatement.setDouble(4, extrasTotal);
            upsertStatement.setDouble(5, discountAmount);
            upsertStatement.setDouble(6, subTotal);
            upsertStatement.setDouble(7, total);
            upsertStatement.executeUpdate();

            idStatement.setLong(1, reservationId);
            try (ResultSet resultSet = idStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong("bill_id");
                }
            }

            throw new DatabaseException("Failed to resolve bill id after save", null);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create bill", e);
        }
    }

    @Override
    public Optional<BillDetailsDTO> findBillDetailsByReservationId(long reservationId) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(selectBillDetailsByReservationSql())) {

            statement.setLong(1, reservationId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new BillDetailsDTO(
                            resultSet.getLong("bill_id"),
                            resultSet.getLong("reservation_id"),
                            resultSet.getString("guest_name"),
                            resultSet.getString("guest_email"),
                            resultSet.getString("room_number"),
                            resultSet.getString("room_type"),
                            resultSet.getString("check_in_date"),
                            resultSet.getString("check_out_date"),
                            resultSet.getInt("nights"),
                            resultSet.getDouble("rate_per_night"),
                            resultSet.getDouble("extras_total"),
                            resultSet.getDouble("discount_amount"),
                            resultSet.getDouble("sub_total"),
                            resultSet.getDouble("total"),
                            resultSet.getString("generated_at")
                    ));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to load bill details", e);
        }
    }

    private Rooms mapRoom(ResultSet resultSet) throws SQLException {
        return new Rooms(
                resultSet.getInt("id"),
                resultSet.getString("room_number"),
                resultSet.getString("room_type"),
                resultSet.getDouble("rate_per_night"),
                resultSet.getString("status"),
                resultSet.getString("description"),
                resultSet.getString("created_at"),
                resultSet.getString("updated_at")
        );
    }

    private ReservationSummaryDTO mapReservationSummary(ResultSet resultSet) throws SQLException {
        return new ReservationSummaryDTO(
                resultSet.getLong("reservation_id"),
                resultSet.getInt("guest_count"),
                resultSet.getString("contact_number"),
                resultSet.getString("check_in_date"),
                resultSet.getString("check_out_date"),
                resultSet.getInt("room_id"),
                resultSet.getString("room_number"),
                resultSet.getString("room_type"),
                resultSet.getDouble("rate_per_night"),
                resultSet.getString("guest_name"),
                resultSet.getString("guest_email")
        );
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String emptyToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }

    private boolean hasColumn(String tableName, String columnName) {
        try (Connection connection = DBConnection.getConnection();
             ResultSet columns = connection.getMetaData()
                     .getColumns(connection.getCatalog(), null, tableName, columnName)) {
            return columns.next();
        } catch (SQLException e) {
            return false;
        }
    }
}

