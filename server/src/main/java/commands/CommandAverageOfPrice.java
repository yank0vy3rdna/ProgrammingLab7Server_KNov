package commands;

import communication.Response;

public class CommandAverageOfPrice extends Command {

    @Override
    public String getName() {
        return "average_of_price";
    }

    @Override
    public String getManual() {
        return "Считает среднее цен.";
    }

    @Override
    public Response execute() {
        try {
            if(context.handlerDatabase.isExistingUser(login, password) == -1) {
                throw new Exception();
            }
            else {
                return new Response(getName(), context.ticketList.averageOfPrice());
            }
        } catch (Exception e) {
            return new Response(getName(), "Вы не прошли авторизацию.");
        }
    }
}
