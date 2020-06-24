package commands;

import communication.Response;
import elements.Ticket;

public class CommandRemoveLowerKey extends Command {

    @Override
    public String getName() {
        return "remove_lower_kek";
    }

    @Override
    public String getManual() {
        return "Удалить все элементы меньшие, чем заданный \"id\". Параметры: id.";
    }

    @Override
    public Response execute() {
        try {
            return new Response(getName(), context.ticketList.removeLowerKey((Long) arguments[0].getValue(), context.handlerDatabase.isExistingUser(login, password)));
        } catch (Exception e) {
            return new Response(getName(), "Вы не прошли авторизацию.");
        }
    }
}
