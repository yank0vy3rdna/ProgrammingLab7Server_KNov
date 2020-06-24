package commands;

import communication.Response;

public class CommandRemoveKey extends Command {

    @Override
    public String getName() {
        return "remove_key";
    }

    @Override
    public String getManual() {
        return "Удалить элемент из коллекции по его \"id\". Параметры: id.";
    }

    @Override
    public Response execute() {
        try {
            return new Response(getName(), context.ticketList.removeKey((Integer) arguments[0].getValue(), context.handlerDatabase.isExistingUser(login, password)));
        } catch (Exception e) {
            return new Response(getName(), "Вы не прошли авторизацию.");
        }
    }
}
