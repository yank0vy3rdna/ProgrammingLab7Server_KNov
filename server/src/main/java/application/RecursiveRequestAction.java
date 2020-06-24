package application;
import communication.Request;
import communication.Response;
import network.AddressedRequest;
import network.AddressedResponse;

import java.util.ArrayList;
import java.util.concurrent.RecursiveAction;

public class RecursiveRequestAction extends RecursiveAction {
    private final Context context;
    RecursiveRequestAction(Context ctx){
        context = ctx;
    }
    @Override
    protected void compute() {
        try {
            if (!context.queueAddressedRequests.isEmpty()) {
                AddressedRequest addressedRequest = context.queueAddressedRequests.poll();

                ArrayList<Response> responses = new ArrayList<>(addressedRequest.getRequests().size());
                for (Request request : addressedRequest.getRequests()) {
                    responses.add(context.handlerCommands.executeCommand(request));
                }
                context.queueAddressedResponse.add(new AddressedResponse(addressedRequest.getSocketAddress(), responses));
            }
        } catch (Exception ignored) {

        }
    }
}
