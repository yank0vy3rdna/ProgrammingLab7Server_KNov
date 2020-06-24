package collection;

import application.Context;
import elements.Ticket;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class TicketList {

    private Context context;

    private HashMap<Long, Ticket> tickets;
    private ReadWriteLock locker;

    public TicketList(Context context) {
        this.context = context;
        tickets = new HashMap<>();
        locker = new ReentrantReadWriteLock();
    }

    public HashMap<Long, Ticket> getTickets() {
        locker.readLock().lock();
        HashMap<Long, Ticket> result = tickets;
        locker.readLock().unlock();
        return result;
    }

    public String printInfo() {
        locker.readLock().lock();
        String result = "Тип коллекции: " + tickets.getClass() + ", Размер: " + tickets.size();
        locker.readLock().unlock();
        return result;
    }

    public void addFromDatabase(Ticket item) {
        locker.writeLock().lock();
        tickets.put(item.getId(), item);
        locker.writeLock().unlock();
    }

    public String add(Ticket item, int idUser) {
        locker.writeLock().lock();
        item.setCreationDate();
        item.setIdUser(idUser);
        try {
            item = context.handlerDatabase.addTicket(item);
            tickets.put(item.getId(), item);
            locker.writeLock().unlock();
            return "Элемент добавлен.";
        } catch (SQLException e) {
            locker.writeLock().unlock();
            return "Элемент не удалось добавить в базу данных.";
        }
    }

    public String clear(int idUser) { //todo может не работать
        locker.writeLock().lock();
        Iterator<Map.Entry<Long, Ticket>> iterator = tickets.entrySet().iterator();
        while (iterator.hasNext()) {
            Ticket ticket = iterator.next().getValue();
            if(ticket.getIdUser() == idUser) {
                remove(ticket);
            }
        }
        locker.writeLock().unlock();
        return "Ваши элементы удалены.";
    }

    public String removeKey(long id, int idUser) {
        locker.writeLock().lock();
        if (tickets.containsKey(id)) {
            if (tickets.get(id).getIdUser() == idUser) {
                remove(tickets.get(id));
                locker.writeLock().unlock();
                return "Элемент Ticket с таким id удален.";
            }
            else {
                locker.writeLock().unlock();
                return "Вы не можете удалить этот элемент Ticket, так как он вам не принадлежит.";
            }
        }
        locker.writeLock().unlock();
        return "Элемент Ticket с таким id не найден.";
    }

    public void remove(Ticket ticket) {
        locker.writeLock().lock();
        try {
            context.handlerDatabase.removeTicket(ticket);
        } catch (SQLException e) {
            locker.writeLock().unlock();
            return;
        }
        tickets.remove(ticket);
        locker.writeLock().unlock();
    }

    public String updateById(long id, Ticket item, int idUser) {
        locker.writeLock().lock();
        if (tickets.containsKey(id)) {
            if (tickets.get(id).getIdUser() == idUser) {
                tickets.get(id).updateTicket(item);
                try {
                    context.handlerDatabase.updateTicket(tickets.get(id));
                    locker.writeLock().unlock();
                    return "Элемент Ticket обновлен.";
                } catch (SQLException e) {
                    locker.writeLock().unlock();
                    return "Обновить элемент в базе данных не удалось.";
                }
            }
            else {
                locker.writeLock().unlock();
                return "Ticket нельзя обновить, так как он вам не принадлежит.";
            }
        }
        locker.writeLock().unlock();
        return "Элемент Ticket с таким id не найден.";
    }

    public String show() {
        locker.readLock().lock();
        String result = "";
        for (Map.Entry<Long, Ticket> entry : tickets.entrySet()) {
            result = result + entry.getValue().toString() + "\n";
        }
        locker.readLock().unlock();
        return result;
    }

    public String sunOfPrice() {
        locker.readLock().lock();
        Integer result = 0;
        for (Map.Entry<Long, Ticket> entry : tickets.entrySet()) {
            result += entry.getValue().getPrice();
        }
        locker.readLock().unlock();
        return result.toString();
    }

    public String averageOfPrice() {
        locker.readLock().lock();
        Double result = 0.0;
        for (Map.Entry<Long, Ticket> entry : tickets.entrySet()) {
            result += entry.getValue().getPrice();
        }
        result = result / tickets.size();
        locker.readLock().unlock();
        return result.toString();
    }

    public String countGreaterThanPrice(Integer price) {
        locker.readLock().lock();
        Integer result = 0;
        for (Map.Entry<Long, Ticket> entry : tickets.entrySet()) {
            if(entry.getValue().getPrice() > price) {
                result++;
            }
        }
        locker.readLock().unlock();
        return result.toString();
    }

    public String removeLower(Ticket item, int idUser) { //todo это тоже
        locker.writeLock().lock();
        Iterator<Map.Entry<Long, Ticket>> iterator = tickets.entrySet().iterator();
        while (iterator.hasNext()) {
            Ticket ticket = iterator.next().getValue();
            if(ticket.getIdUser() == idUser && item.getPrice() > ticket.getPrice()) {
                remove(ticket);
            }
        }
        locker.writeLock().unlock();
        return "Ваши элементы меньшие заданного удалены.";
    }

    public String removeLowerKey(long id, int idUser) { //todo это тоже
        locker.writeLock().lock();
        Iterator<Map.Entry<Long, Ticket>> iterator = tickets.entrySet().iterator();
        while (iterator.hasNext()) {
            Ticket ticket = iterator.next().getValue();
            if(ticket.getIdUser() == idUser && id > ticket.getId()) {
                remove(ticket);
            }
        }
        locker.writeLock().unlock();
        return "Ваши элементы меньшие заданного удалены.";
    }
}
