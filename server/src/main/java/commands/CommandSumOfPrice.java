package commands;

import communication.Response;

public class CommandSumOfPrice extends Command {

    @Override
    public String getName() {
        return "sum_of_price";
    }

    @Override
    public String getManual() {
        return "Сумма цен.";
    }

    @Override
    public Response execute() {
        try {
            if(context.handlerDatabase.isExistingUser(login, password) == -1) {
                throw new Exception();
            }
            else {
                return new Response(getName(), context.ticketList.sunOfPrice());
            }
        } catch (Exception e) {
            return new Response(getName(), "Вы не прошли авторизацию.");
        }
    }
}
