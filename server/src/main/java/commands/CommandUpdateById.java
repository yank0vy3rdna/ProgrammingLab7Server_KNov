package commands;

import communication.Response;
import elements.Ticket;

public class CommandUpdateById extends Command {

    @Override
    public String getName() {
        return "update";
    }

    @Override
    public String getManual() {
        return "Обновить значение элемента коллекции по полю \"id\". Параметры: id {element}.";
}

    @Override
    public Response execute() {
        try {
            return new Response(getName(), context.ticketList.updateById((Long) arguments[0].getValue(), (Ticket) arguments[1].getValue(), context.handlerDatabase.isExistingUser(login, password)));
        }
        catch (Exception e) {
            return new Response(getName(), "Вы не прошли авторизацию.");
        }
    }
}
