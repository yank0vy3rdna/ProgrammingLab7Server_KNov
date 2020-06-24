/**
 * Класс Ticket
 */

package elements;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Date;

public class Ticket implements Serializable {
    private Integer idUser;
    private long id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private Date creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Integer price; //Поле не может быть null, Значение поля должно быть больше 0
    private TicketType type; //Поле может быть null

        public Ticket(int idUser, long id, String name, Coordinates coordinates, Date creationDate, int price, TicketType ticketType){
           this.idUser = idUser;
           this.id = id;
           this.name = name;
           this.coordinates = coordinates;
           this.creationDate = creationDate;
           this.price = price;
           this.type = ticketType;
        }

        public Ticket(String name, Coordinates coordinates, int price, TicketType ticketType){
            this.name = name;
            this.coordinates = coordinates;
            this.price = price;
            this.type = ticketType;
        }

        public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

        public long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public void setName(String ticName) {
            name = ticName;
        }

        public void setCoordinates(Coordinates x) {
                coordinates = x;
        }

    public void setCreationDate() {
        if(this.creationDate != null) {
            return;
        }
        this.creationDate = new Date();
    }

    public void setCreationDate(Date date) {
        if(this.creationDate != null) {
            return;
        }
        this.creationDate = date;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void updateTicket(Ticket ticket){
        name = ticket.name;
        coordinates = ticket.coordinates;
        price = ticket.price;
        type = ticket.type;
        }

    public Integer getIdUser() {
        return idUser;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public TicketType getType() {
        return type;
    }

    public void setPrice (int price) {
            this.price = price;
        }

        public void setTicketType(TicketType type) {
            this.type = type;
        }

        public Integer getPrice() {
          return price;
        }


    @Override
    public String toString() {
        return  " id = " + id + "\n" +
                "name = " + name + "\n" +
                "coordinates: " + coordinates + "\n" +
                "creationDate = " + creationDate + "\n" +
                "price = " + price + "\n" +
                "type = " + type + "\n" + "\n";
        }

}
