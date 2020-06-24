package application;

import collection.TicketList;
import elements.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;

public class HandlerDatabase {
    private Connection connection;
    private Context context;

    public HandlerDatabase(Context context) {
        this.context = context;
    }

    public void initialization(String url) throws SQLException, ClassNotFoundException {
        initialization(url, null, null);
    }

    public void initialization(String url, String user, String password) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");

        if(user == null || password == null) {
            connection = DriverManager.getConnection(url);
        }
        else {
            connection = DriverManager.getConnection(url, user, password);
        }
        if (connection == null) {
            throw new SQLException();
        }
    }

    public TicketList getTicketList() throws SQLException {
        TicketList ticketList = new TicketList(context);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM ticket");
        while (resultSet.next()) {
            int idUser = resultSet.getInt(1);
            long id = resultSet.getLong(2);
            String name = resultSet.getString(3);
            double coordinateX = resultSet.getDouble(4);
            Long coordinateY = resultSet.getLong(5);
            int price = resultSet.getInt(6);
            String ticketType = resultSet.getString(7);
            String creationDate = resultSet.getString(8);
            try {
                ticketList.addFromDatabase(new Ticket(idUser, id, name, new Coordinates(coordinateX, coordinateY),
                        new SimpleDateFormat("dd/MM/yyyy").parse(creationDate), price, TicketType.valueOf(ticketType)));
            } catch (Exception e) {
                throw new SQLException();
            }
        }
        return ticketList;
    }

    public Ticket addTicket(Ticket ticket) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO ticket VALUES (?, DEFAULT, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setInt(1, ticket.getIdUser());
        preparedStatement.setString(2, ticket.getName());
        preparedStatement.setDouble(3, ticket.getCoordinates().getX());
        preparedStatement.setLong(4, ticket.getCoordinates().getY());
        preparedStatement.setDouble(5, ticket.getPrice());
        preparedStatement.setString(6, ticket.getType().toString());
        preparedStatement.setString(7, new SimpleDateFormat("dd/MM/yyyy").format(ticket.getCreationDate()));
        preparedStatement.executeUpdate();
        ResultSet set = preparedStatement.getGeneratedKeys();
        if (set.next()) {
            ticket.setId(set.getLong(set.findColumn("id")));
        }
        else {
            throw new SQLException();
        }
        preparedStatement.close();
        return ticket;
    }

    public void updateTicket(Ticket ticket) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE ticket SET id_user = ?, id = ?, name = ?, coordinate_x = ?, coordinate_y = ?, price = ?, ticket_type = ?, creationdate = ? WHERE id = ?");
        preparedStatement.setInt(1, ticket.getIdUser());
        preparedStatement.setLong(2, ticket.getId());
        preparedStatement.setString(3, ticket.getName());
        preparedStatement.setDouble(4, ticket.getCoordinates().getX());
        preparedStatement.setLong(5, ticket.getCoordinates().getY());
        preparedStatement.setInt(6, ticket.getPrice());
        preparedStatement.setString(7, ticket.getType().toString());
        preparedStatement.setString(8, new SimpleDateFormat("dd/MM/yyyy").format(ticket.getCreationDate()));
        preparedStatement.setLong(9, ticket.getId());
        preparedStatement.execute();
        preparedStatement.close();
    }

    public void removeTicket(Ticket ticket) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM ticket WHERE id = ?");
        preparedStatement.setLong(1, ticket.getId());
        preparedStatement.execute();
        preparedStatement.close();
    }

    public void registrationUser(String login, String password) throws NoSuchAlgorithmException, SQLException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-384");
        byte[] bytes = messageDigest.digest(password.getBytes(StandardCharsets.UTF_8));
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO users VALUES (DEFAULT , ?, ?)");
        preparedStatement.setString(1, login);
        String hash = new String(bytes, StandardCharsets.UTF_8);
        preparedStatement.setString(2, hash);
        preparedStatement.execute();
        preparedStatement.close();
    }

    public int isExistingUser(String login, String password) throws SQLException, NoSuchAlgorithmException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE login = ?");
        preparedStatement.setString(1, login);
        ResultSet resultSet = preparedStatement.executeQuery();

        if(resultSet.next()) {
            int idUser = resultSet.getInt(1);
            String databasePass = resultSet.getString(3);
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-384");
            byte[] passwordBytes = messageDigest.digest(password.getBytes(StandardCharsets.UTF_8));
            String inputPass = new String(passwordBytes, StandardCharsets.UTF_8);

            if(inputPass.equals(databasePass)) {
                return idUser;
            }
            else {
                throw new SQLException();
            }
        }
        else {
            throw new SQLException();
        }
    }
}
