package commands;

import communication.Response;
import elements.Ticket;

public class CommandInsert extends Command {

    @Override
    public String getName() {
        return "insert";
    }

    @Override
    public String getManual() {
        return "Добавить новый элемент в коллекцию. Параметры: {element}.";
    }

    @Override
    public Response execute() {
        try {
            return new Response(getName(), context.ticketList.add((Ticket) arguments[0].getValue(), context.handlerDatabase.isExistingUser(login, password)));
        }
        catch (Exception e) {
            return new Response(getName(), "Вы не прошли авторизацию.");
        }
    }
}
