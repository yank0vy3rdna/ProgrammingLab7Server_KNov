package commands;

import communication.Response;

public class CommandCountGreaterThanPrice extends Command {

    @Override
    public String getName() {
        return "count_greater_than_price";
    }

    @Override
    public String getManual() {
        return "Вывести элементы коллекции, значение поля \"price\" которого больше старого.";
    }

    @Override
    public Response execute() {
        try {
            if(context.handlerDatabase.isExistingUser(login, password) == -1) {
                throw new Exception();
            }
            else {
                return new Response(getName(), context.ticketList.countGreaterThanPrice((Integer) arguments[0].getValue()));
            }
        } catch (Exception e) {
            return new Response(getName(), "Вы не прошли авторизацию.");
        }
    }
}
