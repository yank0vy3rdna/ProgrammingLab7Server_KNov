package commands;

import communication.Response;
import elements.Ticket;

public class CommandRemoveLower extends Command {

    @Override
    public String getName() {
        return "remove_lower";
    }

    @Override
    public String getManual() {
        return "Удалить все элементы меньшие, чем заданный \"price\". Параметры: price.";
    }

    @Override
    public Response execute() {
        try {
            return new Response(getName(), context.ticketList.removeLower((Ticket) arguments[0].getValue(), context.handlerDatabase.isExistingUser(login, password)));
        } catch (Exception e) {
            return new Response(getName(), "Вы не прошли авторизацию.");
        }
    }
}
