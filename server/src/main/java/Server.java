import application.Context;

class Server {
    public static void main(String[] args) {
        Context context = new Context();
        context.initialization(args);
        context.run();
    }
}